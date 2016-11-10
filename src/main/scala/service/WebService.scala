import akka.actor.ActorSystem
import akka.actor.{ Actor, Props }
import akka.http.scaladsl.Http
import akka.event.{ LoggingAdapter, Logging }
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.pattern.ask
import akka.stream.{ ActorMaterializer, Materializer }
import akka.stream.scaladsl.{ Flow, Sink, Source }
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import scala.concurrent.{ ExecutionContextExecutor, Future }
import scala.io._
import scala.io.StdIn
import spray.json.DefaultJsonProtocol

case class Invitation(invitee: String, email: String)

/** Mocks DB objects */
object InvitationDb {
  case class CreateInvitation(invitation: Invitation)
  case object FindAllInvitations
}

/** [Temporary] orchestrates actor behavior in relation to objects */
class InvitationDb extends Actor {
  import InvitationDb._
  var invitations: Map[String, Invitation] = Map.empty

  def receive = {
    case FindAllInvitations           => println("ALL OK") /*inviter ! invitations.values.toList*/
    case CreateInvitation(invitation) => println("ALL OK") /*invitations = invitations ++ Map(invitation.invitee -> invitation.email); inviter ! invitation*/
  }
}

/** Pulls in all implicit conversions to build JSON format instances, both RootJsonReader and RootJsonWriter. */
trait InviterJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val invitationFormat = jsonFormat2(Invitation)
}

/** Utilizes Akka HTTP high-level API to define DSL routes and orchestrate flow */
object WebService extends App with InviterJsonProtocol with SprayJsonSupport {

  implicit val system = ActorSystem("inviter")
  implicit val materializer = ActorMaterializer()

  import system.dispatcher // execution context
  implicit val executor = system.dispatcher

  val inviter = system.actorOf(Props[InvitationDb], name = "inviter")
  
  //  implicit val timeout = Timeout(5 seconds)

  /**
   *  TODO: Set DSL routes
   *  Inbound: HttpRequest outbound: Future[HttpResponse]
   */

}  
