//package inviter
//
//import akka.actor.{ Actor, Props }
//
//object DbActor {
//  case class CreateInvitation(invitation: Invitation)
//  case object ShowLastInvitation
//}
//
//class DbActor extends Actor {
//  import DbActor._
//
//  def receive = {
//    case CreateInvitation(invitation) => DAO.saveInvitation(invitation)
//    case ShowLastInvitation           => DAO.invitations.last
//  }
//}