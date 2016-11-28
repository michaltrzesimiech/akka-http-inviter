package inviter

import akka.actor.{ Actor }

object DbActor {
  case class CreateInvitation(email: String, invitee: String)
}

class DbActor extends Actor with DAO {
  import DbActor._

  def receive = {
    case CreateInvitation(email, invitee) => createInvitation(email, invitee)
  }
}