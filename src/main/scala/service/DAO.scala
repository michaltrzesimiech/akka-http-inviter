package inviter

import scala.collection.mutable.Seq

object DAO {
  val invitation0 = Invitation("John Smith", "john@smith.mx")
  var invitations: collection.mutable.Seq[Invitation] = Seq(invitation0)

  def saveInvitation(invitation: Invitation) = {
    invitations = invitations :+ invitation
    invitations.last
  }

  def showLastInvitation = {
    invitations.last
  }
}