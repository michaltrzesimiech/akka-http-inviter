package inviter

import akka.actor.ActorSystem
import akka.event.{ LoggingAdapter, Logging }
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.Http
import akka.stream.{ ActorMaterializer, Materializer }
import com.typesafe.config.{ Config, ConfigFactory }
import scala.concurrent.{ Await, ExecutionContext, ExecutionContextExecutor, Future }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io._
import scala.io.StdIn
import scala.language.{ implicitConversions }

object Server extends App with DAO {
  import Service._

  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val mater: ActorMaterializer = ActorMaterializer()

  val config = ConfigFactory.load()
  val log = Logging(system, getClass)

  val binding = Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
  println(s"Server running. Press RETURN to stop."); StdIn.readLine()
  binding
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}