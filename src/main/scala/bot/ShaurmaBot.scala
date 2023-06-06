package bot

import cats.Parallel
import cats.effect.Async
import cats.syntax.all._
import telegramium.bots.high.implicits._
import telegramium.bots.high.keyboards.InlineKeyboardMarkups
import telegramium.bots.high.{Api, LongPollBot}
import telegramium.bots.{CallbackQuery, ChatIntId, Message}

class ShaurmaBot[F[_]]()(implicit bot: Api[F], asyncF: Async[F], parallel: Parallel[F])
  extends LongPollBot[F](bot) {

  override def onMessage(msg: Message): F[Unit] =
    msg.text.filter(_.toLowerCase.startsWith("/start")).fold(asyncF.unit) { _ =>
      sendHelloMsg(msg)
    }

  override def onCallbackQuery(query: CallbackQuery): F[Unit] =
    query.data
      .map {
        case "menu" => query.message.fold(asyncF.unit)(m => editMenuOptions(m))

        case "menu_shaurma" => query.message.fold(asyncF.unit)(m => editMenuShaurma(m))
        case "menu_pizza" => query.message.fold(asyncF.unit)(m => editMenuPizza(m))
        case "menu_sandwich" => query.message.fold(asyncF.unit)(m => editMenuSandwich(m))

        // case "orders" => query.message.fold(asuncF.unit)(m => sendOrders(m))
        // case "cart" => query.message.fold(asuncF.unit)(m => sendCart(m))
        // case "order_make" => query.message.fold(asuncF.unit)(m => makeOrder(m))

        case "hello_msg" => query.message.fold(asyncF.unit)(m => editHelloMsg(m))
      }
      .getOrElse(asyncF.unit)

  //private def sendOrders(msg: Message) {
  // orders.list.foreach( order =>
  // sendMessage(
  //   chatId = ChatIntId(msg.chat.id),
  //   text = orders.order
  // ).exec.void
  // )
  // sendHelloMsg(msg)
  //}

  //private def sendCart(msg: Message) {
  // sendMessage(
  //   chatId = ChatIntId(msg.chat.id),
  //   text = cart.order + "\n\nЦена: " + cart.price.toString()
  // ).exec.void
  // sendPreOrderMsg(msg)
  //}

  //private def makeOrder(msg: Message) {
  // orders.save(cart)
  // cart.clear()
  // editMessageText(
  //      chatId = Some(ChatIntId(msg.chat.id)),
  //      messageId = Some(msg.messageId),
  //      text = "Приятного аппетита!",
  //      replyMarkup = Some(InlineKeyboardMarkups.singleButton(backToStartBtn()))
  //    ).exec.void
  //}

  private def addToCart(msg: Message): Unit = {

  }

  private def sendPreOrderMsg(msg: Message) =
    sendMessage(
      chatId = ChatIntId(msg.chat.id),
      text = "Оформляем заказ?",
      replyMarkup = preOrderMarkup()
    ).exec.void

  private def sendHelloMsg(msg: Message) =
    sendMessage(
      chatId = ChatIntId(msg.chat.id),
      text = helloString,
      replyMarkup = helloMarkup()
    ).exec.void

  private def editHelloMsg(msg: Message) =
    editMessageText(
      chatId = Some(ChatIntId(msg.chat.id)),
      messageId = Some(msg.messageId),
      text = helloString,
      replyMarkup = helloMarkup()
    ).exec.void

  private def editMenuOptions(msg: Message) =
    editMessageText(
      chatId = Some(ChatIntId(msg.chat.id)),
      messageId = Some(msg.messageId),
      text = s"Наше меню:",
      replyMarkup = menuMarkup()
    ).exec.void

  private def editMenuShaurma(msg: Message) =
    editMessageText(
      chatId = Some(ChatIntId(msg.chat.id)),
      messageId = Some(msg.messageId),
      text = shaurmaMenuString,
      replyMarkup = menuShurmaMarkup()
    ).exec.void

  private def editMenuPizza(msg: Message) =
    editMessageText(
      chatId = Some(ChatIntId(msg.chat.id)),
      messageId = Some(msg.messageId),
      text = pizzaMenuString,
      replyMarkup = menuPizzaMarkup()
    ).exec.void

  private def editMenuSandwich(msg: Message) =
    editMessageText(
      chatId = Some(ChatIntId(msg.chat.id)),
      messageId = Some(msg.messageId),
      text = sandwichMenuString,
      replyMarkup = menuSandwichMarkup()
    ).exec.void
}
