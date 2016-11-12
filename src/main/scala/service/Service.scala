/**
 * Building version;
 * with partially defined route covering 2 endpoints;
 * expedited configuration details to application.conf;
 * with DB Actor.
 */

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
import spray.json.DefaultJsonProtocol

/** Domain model */
case class Invitation(invitee: String, email: String)
object Invitation

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

///** Pulls in all implicit conversions to build JSON format instances, both RootJsonReader and RootJsonWriter. */
//trait InviterJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
//  implicit val invitationFormat = jsonFormat2(Invitation.apply)
//}

/** Pulls in all implicit conversions to build JSON format instances, both RootJsonReader and RootJsonWriter. */
trait InviterJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit object InvitationJsonFromat extends RootJsonFormat[Invitation] {
    implicit val invitationFormat = jsonFormat2(Invitation.apply)

    def write(c: Invitation) =
      JsArray(JsString(c.invitee), JsString(c.email))

    def read(value: JsValue) = value match {
      case JsArray(Vector(JsString(name), JsString(email))) =>
        new Invitation(name, email)
      case _ => deserializationError("Color expected")
    }
  }

  /**
   * TODO:
   * 1. Reduce unused boilerplate.
   * 2. Learn from trait PredefinedToResponseMarshallers
   * 3. Possibly productive:
   * implicit val invitationUM: FromRequestUnmarshaller[Invitation] = ???
   * implicit val invitationM: ToResponseMarshaller[Invitation] = ???
   * implicit val invitationSeqM: ToResponseMarshaller[List[Invitation]] = ???
   */

}

/** Core service. Invokes ActorSystem, materializes Actor, orchestrates DSL routes, binds to server, terminates server.  */
object Service extends App with InviterJsonProtocol with SprayJsonSupport {
  import akka.pattern.ask
  import akka.util.Timeout
  import akka.pattern.ask
  import scala.concurrent.duration._
  import scala.language.postfixOps

  implicit val system = ActorSystem("inviter-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val inviter = system.actorOf(Props[InvitationDb], "inviter")
  implicit val timeout = Timeout(5 seconds)

  val route: Route =
    path("invitation") {
      post {
        entity(as[Invitation]) { invitation =>
          complete {
//            invitation
             val futPost = (inviter ? InvitationDb.CreateSingleInvitation(invitation))
             complete(futPost)
          }
        }
      } ~ get {
        entity(as[List[Invitation]]) { invitation =>
          val futGet: Future[List[Invitation]] = (inviter ? InvitationDb.FindAllInvitations).mapTo[List[Invitation]]
          complete(futGet)
        }
      }
    }

  //  val route: Route =
  //    path("invitation") {
  //      get & entity(as[List[Invitation]]) { invitation =>
  //        val futGet: Future[List[Invitation]] = (inviter ? InvitationDb.FindAllInvitations).mapTo[List[Invitation]]
  //        complete(futGet)
  //      } ~
  //        path("invitation") {
  //          post & entity(as[Invitation]) { invitation =>
  //            val futPost = (inviter ? InvitationDb.CreateSingleInvitation(invitation))
  //            complete(futPost)
  //          }
  //        }
  //    }

  //  val route =
  //    path("invitation") {
  //        post {
  //          entity(as[Invitation]) { invitation =>
  //            respondWithStatus(StatusCodes.OK) {
  //              respondWithMediaType(`application/json`) {
  //                inviter ? InvitationDb.FindAllInvitations).map[ToResponseMarshallable]
  //              }
  //      }
  //    }

  //  val route: Route =
  //    path("invitation" / IntNumber) { invitation =>
  //      get /*& entity(as[Invitation])*/ {
  //        complete {
  //          "Received GET request for order " + invitation
  //        }
  //      } ~
  //        put {
  //          complete /*& entity(as[Invitation])*/ {
  //            "Received PUT request for order " + invitation
  //          }
  //        }
  //    }

  val config = ConfigFactory.load()
  val binding = Http().bindAndHandle(route, config.getString("http.interface"), config.getInt("http.port"))

  println(s"Server running. Press any key to stop."); StdIn.readLine()
  binding
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done

}
