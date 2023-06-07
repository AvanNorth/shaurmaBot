package data.entity

object Entities {
  case class Cart(
    chatId: Long,
    userOrder: String,
    price: Int
   ) {

  }

  object Cart {
    def getClearCart(id: Long): Cart = {
      Cart(
        chatId = id,
        userOrder = "",
        price = -1
      )
    }
  }

  case class Order(
   chatId: Long,
   userOrder: String,
   price: Int
   )

  object Order {
    def getOrderFromCart(cart: Cart) = Order(
      chatId = cart.chatId,
      userOrder = cart.userOrder,
      price = cart.price
    )
  }
}
