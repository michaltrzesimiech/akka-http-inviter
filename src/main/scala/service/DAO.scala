package inviter

import scala.collection.mutable.Seq

trait DAO {
  val invitation0 = Invitation("John Smith", "john@smith.mx")
  var invitations: collection.mutable.Seq[Invitation] = Seq(invitation0)

  def createInvitation(name: String, email: String) = {
    invitations = invitations :+ Invitation(name, email)
  }

  def showLastInvitation = {
    invitations.last
  }
}