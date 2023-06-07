package data.usecase

import data.entity.Entities.Cart
import data.repository.CartRepository

class CartUseCase(cartRepository: CartRepository) {
  def createCart(cart: Cart): Unit = {
    cartRepository.save(cart)
  }

  def createClearCart(id: Long): Unit = {
    cartRepository.save(Cart.getClearCart(id))
  }

  def getCartInfo(id: Long): Option[Cart] =
    cartRepository.findCartById(id)

  def cleanCart(id: Long) = {
    cartRepository.updateCart(
      Cart.getClearCart(id)
    )
  }

  def updateCartInfo(oldCart: Cart, orderItem: String, price: Int): Unit = {
    cartRepository.updateCart(Cart(
      chatId = oldCart.chatId,
      userOrder = s"${oldCart.userOrder}\n${orderItem}",
      price = oldCart.price + price
    ))
  }
}

object CartUseCase {
  def apply(cartRepository: CartRepository): CartUseCase =
    new CartUseCase(cartRepository)
}
