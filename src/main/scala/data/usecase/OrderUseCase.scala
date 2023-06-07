package data.usecase

import data.entity.Entities.{Cart, Order}
import data.repository.OrderRepository

class OrderUseCase(orderRepository: OrderRepository) {
  def createOrder(cart: Cart): Unit = {
    orderRepository.save(Order.getOrderFromCart(cart))
  }

  def getOrderList(id: Long): List[Order] =
    orderRepository.findAllUserOrders(id)
}

object OrderUseCase {
  def apply(orderRepository: OrderRepository): OrderUseCase =
    new OrderUseCase(orderRepository)
}
