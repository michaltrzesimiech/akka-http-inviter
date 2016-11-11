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

/** Core service. Invokes ActorSystem, materializes Actor, orchestrates DSL routes, binds to server, terminates server.  */
object Service extends App with InviterJsonProtocol with SprayJsonSupport {

  implicit val system = ActorSystem("inviter-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  /** */
  val route =
    pathPrefix("test") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>ALL OK</h1>"))
      }
    }

  val binding = Http().bindAndHandle(route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  binding
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done

}
