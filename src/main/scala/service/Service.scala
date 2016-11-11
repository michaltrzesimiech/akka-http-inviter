/** Building version (with dummy route covering 2 endpoints, temporarily without DB Actor). */

import akka.actor.ActorSystem
import akka.actor.{ Actor, Props }
import akka.event.{ LoggingAdapter, Logging }
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.{ ToResponseMarshallable, ToResponseMarshaller }
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.{ HttpMethods, StatusCodes }
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.{ Unmarshal, FromRequestUnmarshaller }
import akka.stream.{ ActorMaterializer, Materializer }
import akka.stream.scaladsl.{ Flow, Sink, Source }
import com.typesafe.config.{ Config, ConfigFactory }
import scala.concurrent.{ ExecutionContextExecutor, Future }
import scala.io._
import scala.io.StdIn
import spray.json._

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
  import akka.pattern.ask

  implicit val system = ActorSystem("inviter-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  /**
   * TODO:
   * 1. Provide a RootJsonFormat[T] for your type and bring it into scope,
   * 2. Learn from trait PredefinedToResponseMarshallers
   * 3. Possibly productive:
   * implicit val invitationUM: FromRequestUnmarshaller[Invitation] = ???
   * implicit val invitationM: ToResponseMarshaller[Invitation] = ???
   * implicit val invitationSeqM: ToResponseMarshaller[List[Invitation]] = ???
   */

  val route: Route =
    path("order" / IntNumber) { invitation =>
      get /*& entity(as[Invitation])*/ {
        complete {
          "Received GET request for order " + invitation
        }
      } ~
        put {
          complete /*& entity(as[Invitation])*/ {
            "Received PUT request for order " + invitation
          }
        }
    }

  val binding = Http().bindAndHandle(route, "localhost", 8080)

  println(s"Server running. Press any key to stop."); StdIn.readLine()
  binding
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done

}
