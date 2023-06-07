package data.repository

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import data.entity.Entities.Cart
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor.Aux

class CartRepository private(xa: Aux[IO, Unit]) {
  implicit val cartRead: Read[Cart] =
    Read[(Long, String, Int)]
      .map {
        case (
          chatId,
          userOrder,
          price
          ) =>
          Cart(
            chatId,
            userOrder,
            price
          )
      }

  implicit val userWrite: Write[Cart] =
    Write[(Long, String, Int)]
      .contramap(c =>
        (
          c.chatId,
          c.userOrder,
          c.price,
        )
      )

  def save(cart: Cart): Unit = {
    Fragment
      .const(
        s"INSERT INTO cart (chat_id, user_order, price) VALUES (${cart.chatId}, '${cart.userOrder}', '${cart.price}')"
      )
      .update
      .run
      .transact(xa)
      .unsafeRunSync()
  }

  def findCartById(id: Long): Option[Cart] = {
    sql"SELECT * FROM cart WHERE chat_id = $id"
      .query[Cart]
      .option
      .transact(xa)
      .unsafeRunSync()
  }

  def updateCart(cart: Cart): Unit = {
    sql"UPDATE cart SET user_order = ${cart.userOrder}, price = ${cart.price} WHERE chat_id = ${cart.chatId};"
      .update.run
      .transact(xa)
      .unsafeRunSync()
  }
}

object CartRepository {
  def apply(xa: Aux[IO, Unit]): CartRepository = {
    new CartRepository(xa)
  }
}
