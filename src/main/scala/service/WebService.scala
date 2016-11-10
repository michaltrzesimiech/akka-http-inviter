import akka.actor.ActorSystem
import akka.actor.{ Actor, Props }
import akka.http.scaladsl.Http
import akka.event.{ LoggingAdapter, Logging }
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model._
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

/** Mocks DB */
case class Invitation(invitee: String, email: String)

object InvitationDb {
  case class CreateInvitation(invitation: Invitation)
  case object FindAllInvitations
}

class InvitationDb extends Actor {
  import InvitationDb._
  import scala.collection.mutable.Map
  var invitations: Map[String, String] = Map.empty

  def receive = {
    case FindAllInvitations           => println("[List of all invitations]") /*inviter ! invitations.values.toList*/
    case CreateInvitation(invitation) => Invitation("Giordano Bruno", "giordano.bruno@solid.edu") /* invitations = invitations ++ Map(invitation.invitee -> invitation.email); inviter ! invitation */
  }
}

/** Utilizes Akka HTTP high-level API to define DSL routes and orchestrate flow */
object WebService extends App with InviterJsonProtocol with SprayJsonSupport {

  implicit val system = ActorSystem("inviter")
  implicit val materializer = ActorMaterializer()

  import system.dispatcher // execution context
  implicit val executor = system.dispatcher

  //  implicit val timeout = Timeout(5 seconds)

  val inviter = system.actorOf(Props[InvitationDb], name = "inviter")

  /** TODO: Set DSL routes, inbound: HttpRequest outbound: Future[HttpResponse]*/

  val route = {
    pathPrefix("invitations") {
      post & entity(as[Invitation]) { invitation =>
        complete {
          Created -> Map(InvitationDb.CreateInvitation(invitation)).toJson
        }
      } ~
        (get) {
          complete {
            OK -> Map(InvitationDb.FindAllInvitations.toJson)
          }
        }
    }
  }

  /*    import akka.pattern.ask
     val routes: Route = {
    path() {
      get {
        val invitationFut = (inviter ? InvitationDb.FindAllInvitations).mapTo[List[Invitation]]
        complete(invitationFut)
      } ~
        (post & entity(as[Invitation])) { invitation =>
          val fut = (inviter ? InvitationDb.CreateInvitation(invitation)).mapTo[Invitation]
          complete(fut)
        }
    }
  }*/

  /**
   * Bind to server, handle, undbind and terminate when done:
   *
   * val binding = Http().bindAndHandle(route, "localhost", 8080)  //
   * println(s"Server up and running. Press any key to stop...")
   * StdIn.readLine()
   * binding
   * .flatMap(_.unbind())
   * .onComplete(_ => system.terminate()) // and shutdown when done
   */

  println("GOOD WORK LAD")

}  
