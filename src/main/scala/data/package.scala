import doobie.Read
import io.estatico.newtype.macros.newtype

package object data {

  @newtype
  case class ChatId(value: Long)
  object ChatId {
    implicit val doobieRead: Read[ChatId] = Read[Long].map(ChatId(_))
  }
}
