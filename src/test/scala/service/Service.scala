import org.scalatest.{ Matchers, WordSpec }
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.{ ToResponseMarshallable, ToResponseMarshaller }
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.{ HttpResponse, HttpRequest, HttpMethods, StatusCodes }
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.{ Unmarshal, FromRequestUnmarshaller }
import spray.json._
import spray.json.DefaultJsonProtocol
import akka.http.scaladsl.model.ContentTypes._
import InviterRoutes._

class InviterRoutesTest extends WordSpec with Matchers with ScalatestRouteTest {

  "GET requests to the path /invitation" in {
    Get("/invitation") ~> InviterRoutes.routes ~> check {
      status shouldEqual StatusCodes.OK
    }
  }

  "POST requests to the path /invitation" in {
    Post("/invitation", HttpEntity(`application/json`, """{ "invitee": "Babukar", "email": "babukar@mail.com" }""")) ~> InviterRoutes.routes ~> check {
      status.isSuccess() shouldEqual true
    }
  }

}