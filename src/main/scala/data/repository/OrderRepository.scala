package data.repository

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import data.entity.Entities.{Cart, Order}
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor.Aux

class OrderRepository private(xa: Aux[IO, Unit]) {
  implicit val cartRead: Read[Order] =
    Read[(Long, String, Int)]
      .map {
        case (
          chatId,
          userOrder,
          price
          ) =>
          Order(
            chatId,
            userOrder,
            price
          )
      }

  implicit val userWrite: Write[Order] =
    Write[(Long, String, Int)]
      .contramap(o =>
        (
          o.chatId,
          o.userOrder,
          o.price,
        )
      )

  def save(order: Order): Unit = {
    Fragment
      .const(
        s"INSERT INTO orders (chat_id, user_order, price) VALUES (${order.chatId}, '${order.userOrder}', '${order.price}')"
      )
      .update
      .run
      .transact(xa)
      .unsafeRunSync()
  }

  def findAllUserOrders(id: Long): List[Order] = {
    sql"SELECT * FROM orders WHERE chat_id = $id"
      .query[Order]
      .to[List]
      .transact(xa)
      .unsafeRunSync()
  }
}

object OrderRepository {
  def apply(xa: Aux[IO, Unit]): OrderRepository = {
    new OrderRepository(xa)
  }
}
