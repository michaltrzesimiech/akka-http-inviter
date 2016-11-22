package inviter

import akka.actor.ActorSystem
import akka.Done
import akka.event.{ LoggingAdapter, Logging }
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
import scala.concurrent.Future
import scala.io.StdIn
import spray.json._
import spray.json.DefaultJsonProtocol

object InviterRoutes extends InviterJsonProtocol with SprayJsonSupport {

  def routes: Route = {
    pathPrefix("invitation") {
      get {
        complete(DAO.invitations.toJson)
      }
    } ~
      post {
        entity(as[Invitation]) { invitation =>
          completeDAO..saveInvitation(invitation))
        }
      }
  }

  /** Invokes ActorSystem, materializes Actor, binds routes to server, gracefully shuts down on user action. */
  def run: Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val config = ConfigFactory.load()
    val log = Logging(system, getClass)

    val binding = Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
    println(s"Server running. Press RETURN to stop."); StdIn.readLine()
    binding
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}

object InviterServer extends App {
  InviterRoutes.run
}