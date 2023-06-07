package bot

import cats.Parallel
import cats.effect.{Async, IO}
import cats.syntax.all._
import data.entity.Entities.Cart
import data.repository.{CartRepository, OrderRepository}
import data.usecase.{CartUseCase, OrderUseCase}
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux
import telegramium.bots.high.implicits._
import telegramium.bots.high.keyboards.InlineKeyboardMarkups
import telegramium.bots.high.{Api, LongPollBot}
import telegramium.bots.{CallbackQuery, ChatIntId, InlineKeyboardMarkup, Message}

class ShaurmaBot[F[_]]()(implicit bot: Api[F], asyncF: Async[F], parallel: Parallel[F])
  extends LongPollBot[F](bot) {

  private val xa: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", // driver classname
    "jdbc:postgresql:shaurmaBot", // connect URL (driver-specific)
    "postgres", // user
    "131jvjy131", // password
  )

  private val cartRep = CartRepository(xa)
  private val cartUseCase = CartUseCase(cartRep)

  private val orderRep = OrderRepository(xa)
  private val orderUseCase = OrderUseCase(orderRep)

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

        case "menu_shaurma_mini" => query.message.fold(asyncF.unit)(m => addToCart(m, "Шаурма мини", 99))
        case "menu_shaurma_classic" => query.message.fold(asyncF.unit)(m => addToCart(m, "Шаурма классическая", 199))
        case "menu_shaurma_cheese" => query.message.fold(asyncF.unit)(m => addToCart(m, "Шаурма с сыром", 219))

        case "menu_pizza_mini" => query.message.fold(asyncF.unit)(m => addToCart(m, "Пицца мини", 119))
        case "menu_pizza_classic" => query.message.fold(asyncF.unit)(m => addToCart(m, "Пицца классическая", 159))
        case "menu_pizza_cheese" => query.message.fold(asyncF.unit)(m => addToCart(m, "Пицца сборная с сыром", 199))

        case "menu_sandwich_chicken" => query.message.fold(asyncF.unit)(m => addToCart(m, "Сэндвич с курицей", 99))
        case "menu_sandwich_grand" => query.message.fold(asyncF.unit)(m => addToCart(m, "Сэндвич Гранд", 119))

        case "orders" => query.message.fold(asyncF.unit)(m => sendOrders(m))
        case "cart" => query.message.fold(asyncF.unit)(m => sendCart(m))
        case "order_make" => query.message.fold(asyncF.unit)(m => makeOrder(m))

        case "hello_msg" => query.message.fold(asyncF.unit)(m => editHelloMsg(m))
      }
      .getOrElse(asyncF.unit)

  /*
  Бот полностью готов и работает, если вы видите этот коммент и какие либо закоменченные функции, значит я не смог разобраться с БД :(
   */

  private def sendOrders(msg: Message): F[Unit] = {
    val orders = orderUseCase.getOrderList(msg.chat.id)
    var orderListString = ""

    if (orders.isEmpty)
      orderListString = "У вас пока нет заказов! Закажите что нибудь через меню"
    else
      orders.foreach(order =>
        orderListString += s"${order.userOrder}\n-----\n${order.price} руб\n\n"
      )

    editText(msg, orderListString, backToStartMarkup())
  }

  private def sendCart(msg: Message): F[Unit] = {
    val cart = cartUseCase.getCartInfo(msg.chat.id)
    var cartOrderString = ""
    var markup = backToStartMarkup()

    if (cart.get.equals(Cart.getClearCart(msg.chat.id)))
      cartOrderString = "Корзина пуста!"
    else {
      markup = preOrderMarkup()
      cartOrderString = s"${cart.get.userOrder} \n\nЦена: ${cart.get.price.toString}"
    }

    editText(msg, cartOrderString, markup)
  }

  private def editText(msg: Message, text: String, markups: Some[InlineKeyboardMarkup]) =
    editMessageText(
      chatId = Some(ChatIntId(msg.chat.id)),
      messageId = Some(msg.messageId),
      text = text,
      replyMarkup = markups
    ).exec.void

  private def makeOrder(msg: Message): F[Unit] = {
    orderUseCase.createOrder(cartUseCase.getCartInfo(msg.chat.id).get)
    cartUseCase.cleanCart(msg.chat.id)

    editMessageText(
      chatId = Some(ChatIntId(msg.chat.id)),
      messageId = Some(msg.messageId),
      text = "Приятного аппетита!",
      replyMarkup = backToStartMarkup()
    ).exec.void
  }

  private def addToCart(msg: Message, orderItem: String, price: Int) = {
    val oldCart = cartUseCase.getCartInfo(msg.chat.id).get
    cartUseCase.updateCartInfo(oldCart, orderItem, price)

    editText(msg, orderItem + " добавлено в корзину", backToMenuMarkup())
  }

  private def sendHelloMsg(msg: Message) = {
    initUser(msg.chat.id)
    sendMessage(
      chatId = ChatIntId(msg.chat.id),
      text = helloString,
      replyMarkup = helloMarkup()
    ).exec.void
  }

  private def initUser(id: Long): Unit = {
    if (cartUseCase.getCartInfo(id).isEmpty)
      cartUseCase.createClearCart(id)
  }

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
