import akka.actor.ActorSystem
import akka.actor.{ Actor, Props }
import akka.http.scaladsl.Http
import akka.event.{ LoggingAdapter, Logging }
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.StatusCodes.{ Created, OK }
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ ActorMaterializer, Materializer }
import akka.stream.scaladsl.{ Flow, Sink, Source }
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import scala.concurrent.{ ExecutionContextExecutor, Future }
import scala.io._
import scala.io.StdIn
import spray.json.DefaultJsonProtocol

/** Domain model */
final case class Invitation(invitee: String, email: String)

object InvitationDb {
  case class CreateInvitation(invitation: Invitation)
  case object FindAllInvitations
}

class InvitationDb extends Actor {
  import scala.collection.mutable.Seq

  import InvitationDb._
  var invitations: Seq[Invitation] = Seq.empty

  def receive = {
    case CreateInvitation(invitation) => invitations = invitations :+ invitation
    case FindAllInvitations           => invitations.toList
  }
}

/** Utilizes Akka HTTP H-L S-S API to define DSL routes and orchestrate flow */
object WebService extends App with InviterJsonProtocol with SprayJsonSupport {
  import akka.util.Timeout
  import akka.pattern.ask
  import scala.concurrent.duration._
  import scala.language.postfixOps

  implicit val system = ActorSystem("inviter")
  implicit val materializer = ActorMaterializer()

  import system.dispatcher // execution context
  implicit val executor = system.dispatcher

  implicit val timeout = Timeout(5 seconds)

  val invitationDb = system.actorOf(Props[InvitationDb] /*, name = "inviter"*/ )

  val route = {
    pathPrefix("invitation") {
      get { complete(invitationDb ? InvitationDb.FindAllInvitations).mapTo[List[Invitation]] }
    } ~ (post & entity(as[Invitation])) { invitation =>
      /** TODO: scala.concurrent.Future[Any] =>  akka.http.scaladsl.marshalling.ToResponseMarshallable*/
      complete(invitationDb ? InvitationDb.CreateInvitation(invitation)).mapTo[Invitation]
    }
  }

  /*
  val route: Route = {
    pathPrefix("invitations") {
      post & entity(as[Invitation]) { invitation =>
        complete {
          Created -> "OK".toString Map(InvitationDb.CreateInvitation(invitee, email)).toJson
        }
      } ~
        (get) {
          complete {
            OK -> "All OK".toString Map(InvitationDb.FindAllInvitations.toJson)
          }
        }
    }
  }
	*/

  /**
   * Low level example
   * val requestHandler: HttpRequest => HttpResponse = {
   * case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
   * HttpResponse(entity = HttpEntity(
   * ContentTypes.`text/html(UTF-8)`,
   * "<html><body>Hello world!</body></html>"))
   *
   * case HttpRequest(GET, Uri.Path("/invitation"), _, _, _) =>
   * HttpResponse(200, entity = "")
   * }
   */

  /**
   * Bind to server, handle, undbind and terminate when done:
   *
   * A. val binding = Http(system).bind(interface = "localhost", port = 8080); binding.startHandlingWith(route)
   * B. below
   */

  val binding = Http().bindAndHandle(route, "localhost", 8080)

  /**
   * TODO: Unbind and terminate:
   * println(s"Server up and running. Press any key to stop..."); StdIn.readLine()
   *    binding
   *      .flatMap(_.unbind())
   *     .onComplete(_ => system.terminate()) // and shutdown when done
   */

}
