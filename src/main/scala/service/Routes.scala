package inviter

import akka.actor._
import akka.actor.{ ActorSystem, Actor, Props }
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
import akka.util.Timeout
import akka.stream.{ ActorMaterializer, Materializer }
import akka.stream.scaladsl.{ Flow, Sink, Source }
import com.typesafe.config.{ Config, ConfigFactory }
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.language.postfixOps
import scala.io.StdIn
import spray.json._
import spray.json.DefaultJsonProtocol

object InviterRoutes extends InviterJsonProtocol with SprayJsonSupport {
  import akka.pattern.ask

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  //  implicit val timeout = Timeout(5 seconds)
  //  val master = system.actorOf(Props[DbActor])

  def routes: Route = {
    pathPrefix("invitation") {
      get {
        val futGetAll = DAO.showLastInvitation.toJson
        complete(futGetAll)
      }
    } ~
      post {
        entity(as[Invitation]) { invitation =>
          val futSave = (DAO.saveInvitation(invitation).toJson)
          complete(futSave)
        }
      }
  }

  def run: Unit = {
    val config = ConfigFactory.load()
    val log = Logging(system, getClass)

    val binding = Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
    println(s"Server running. Press RETURN to stop."); StdIn.readLine()
    binding
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}