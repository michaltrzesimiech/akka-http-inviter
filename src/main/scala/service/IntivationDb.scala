/** Mocks DB. Migrate from main object at a later stage */

/*import scala.collection.mutable.Map

case class Invitation(invitee: String, email: String)

object InvitationDb {
  case class CreateInvitation(invitation: Invitation)
  case object FindAllInvitations
}

class InvitationDb extends Actor {
  import InvitationDb._
  var invitations: Map[String, String] = Map.empty

  def receive = {
    case FindAllInvitations           => println("ALL OK") inviter ! invitations.values.toList
    case CreateInvitation(invitation) => println("ALL OK") invitations = invitations ++ Map(invitation.invitee -> invitation.email); inviter ! invitation
  }
}
*/