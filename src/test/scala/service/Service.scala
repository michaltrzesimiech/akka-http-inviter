import org.scalatest.{ Matchers, WordSpec }
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import Directives._

class InviterRoutesTest extends WordSpec with Matchers with ScalatestRouteTest {

  "GET requests to the path /invitation" in {
    Get("/invitation") ~> InviterRoutes.routes ~> check {
      status shouldEqual StatusCodes.OK
    }
  }

  "POST requests to the path /invitation" in {
    Post("/invitation") ~> InviterRoutes.routes ~> check {
      status shouldEqual StatusCodes.OK
    }
  }

}