package inviter

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.{ ToResponseMarshallable, ToResponseMarshaller }
import akka.http.scaladsl.unmarshalling.{ Unmarshal, FromRequestUnmarshaller }
import spray.json.DefaultJsonProtocol

/** Pulls in implicit conversions to build JSON instances */
trait InviterJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val invitationFormat = jsonFormat2(Invitation.apply)
}

