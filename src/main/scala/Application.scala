import bot.ShaurmaBot
import cats.effect.{ExitCode, IO, IOApp}
import doobie.Transactor
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.client.Client
import org.http4s.client.middleware.Logger
import telegramium.bots.high.{Api, BotApi}

object Application extends IOApp {
  private val token = "5898516695:AAGw-tSwkGQxVgXC-aPPNe1kz0A2M42JnWY"

  override def run(args: List[String]): IO[ExitCode] = {
    val transactor: Transactor[IO] = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver", // driver classname
      "jdbc:postgresql:shaurmaBot", // connect URL (driver-specific)
      "postgres", // user
      "131jvjy131", // password
    )

    BlazeClientBuilder[IO].resource
      .use { httpClient =>
        val http = Logger(logBody = false, logHeaders = false)(httpClient)

        implicit val api: Api[IO] = createBotBack(http, token)
        val shaurmaBot = new ShaurmaBot()
        shaurmaBot.start().as(ExitCode.Success)
      }
  }

  private def createBotBack(http: Client[IO], token: String) =
    BotApi(http, baseUrl = s"https://api.telegram.org/bot$token")
}
