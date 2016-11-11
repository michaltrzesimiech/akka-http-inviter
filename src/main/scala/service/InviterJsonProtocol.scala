import spray.json.DefaultJsonProtocol
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

/** Pulls in all implicit conversions to build JSON format instances, both RootJsonReader and RootJsonWriter. */
trait InviterJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val invitationFormat = jsonFormat2(Invitation)
  
}

