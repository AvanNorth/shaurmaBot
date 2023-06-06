import telegramium.bots.InlineKeyboardMarkup
import telegramium.bots.high.keyboards.{InlineKeyboardButtons, InlineKeyboardMarkups}

package object bot {
  val helloString = "Добро пожаловать в мою шаурмишку, что хотите заказать? \nСегодня у нас акция - шаурма мини за 100 рублей!"
  val shaurmaMenuString = "У нас самая вкусная шаурма в городе!"
  val pizzaMenuString = "Вот какие пиццы у нас есть:"
  val sandwichMenuString = "Сэндвич! Отлично подходит для перекуса!"

  def backToMenuBtn() =
    InlineKeyboardButtons.callbackData("<- В меню", callbackData = "menu")

  def backToStartBtn() =
    InlineKeyboardButtons.callbackData("<- На главную", callbackData = "hello_msg")

  def helloMarkup(): Some[InlineKeyboardMarkup] =
    Some(
      InlineKeyboardMarkups.singleRow(
        List(
          InlineKeyboardButtons.callbackData("Меню", callbackData = "menu"),
          InlineKeyboardButtons.callbackData("Мои заказы", callbackData = "orders"),
          InlineKeyboardButtons.callbackData("Корзина", callbackData = "cart")
        )
      )
    )

  def preOrderMarkup(): Some[InlineKeyboardMarkup] =
    Some(
      InlineKeyboardMarkups.singleRow(
        List(
          InlineKeyboardButtons.callbackData("Оформить заказ", callbackData = "order_make"),
          backToMenuBtn()
        )
      )
    )

  def menuMarkup(): Some[InlineKeyboardMarkup] =
    Some(
      InlineKeyboardMarkups.singleColumn(
        List(
          InlineKeyboardButtons.callbackData("Шаурма", callbackData = "menu_shaurma"),
          InlineKeyboardButtons.callbackData("Пицца", callbackData = "menu_pizza"),
          InlineKeyboardButtons.callbackData("Сендвичи", callbackData = "menu_sandwich"),
          backToStartBtn()
        )
      )
    )

  def menuShurmaMarkup(): Some[InlineKeyboardMarkup] =
    Some(
      InlineKeyboardMarkups.singleColumn(
        List(
          InlineKeyboardButtons.callbackData("Шаурма мини | 99 руб", callbackData = "menu_shaurma_mini"),
          InlineKeyboardButtons.callbackData("Шаурма классическая | 199 руб", callbackData = "menu_shaurma_classic"),
          InlineKeyboardButtons.callbackData("Шаурма с сыром | 219 руб", callbackData = "menu_shaurma_cheese"),
          backToMenuBtn()
        )
      )
    )

  def menuPizzaMarkup(): Some[InlineKeyboardMarkup] =
    Some(
      InlineKeyboardMarkups.singleColumn(
        List(
          InlineKeyboardButtons.callbackData("Пицца закрытая мини | 119 руб", callbackData = "menu_pizza_mini"),
          InlineKeyboardButtons.callbackData("Пицца классическая | 159 руб", callbackData = "menu_pizza_classic"),
          InlineKeyboardButtons.callbackData("Пицца сборная с сыром | 199 руб", callbackData = "menu_pizza_cheese"),
          backToMenuBtn()
        )
      )
    )

  def menuSandwichMarkup(): Some[InlineKeyboardMarkup] =
    Some(
      InlineKeyboardMarkups.singleColumn(
        List(
          InlineKeyboardButtons.callbackData("Сэндвич с курицей | 99 руб", callbackData = "menu_sandwich_chicken"),
          InlineKeyboardButtons.callbackData("Сэндвич \"Гранд\" | 119 руб", callbackData = "menu_sandwich_grand"),
          backToMenuBtn()
        )
      )
    )
}
