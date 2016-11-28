package inviter

import akka.actor._
import akka.actor.{ ActorSystem, Actor, Props }
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.{ HttpMethods, StatusCodes }
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.language.postfixOps
import scala.io.StdIn
import spray.json._
import spray.json.DefaultJsonProtocol

object Service extends Protocol with DAO with SprayJsonSupport {
  import akka.pattern.ask
  import Server._

  implicit val timeout = Timeout(5 seconds)
  val master = system.actorOf(Props[DbActor])

  def routes: Route = {
    pathPrefix("invitation") {
      get {
        complete(showLastInvitation.toJson)
      }
    } ~
      post {
        entity(as[Invitation]) { invitation =>
          (master ? createInvitation(invitation.name, invitation.email))
          complete(StatusCodes.Created)
        }
      }
  }
}