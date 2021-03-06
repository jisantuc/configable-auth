package com.jisantuc.configableauth.api

import cats.effect._
import cats.implicits._
import com.jisantuc.configableauth.api.commands.{ApiConfig, AuthConfig, Commands, DatabaseConfig}
import com.jisantuc.configableauth.api.endpoints.UserEndpoints
import com.jisantuc.configableauth.api.services.UsersService
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import org.http4s.implicits._
import org.http4s.server.blaze._
import org.http4s.server.middleware._
import org.http4s.server.{Router, Server => HTTP4sServer}
import sttp.client.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.http4s.SwaggerHttp4s

object Server extends IOApp {

  private def createServer(
      apiConfig: ApiConfig,
      authConfig: AuthConfig,
      dbConfig: DatabaseConfig
  ): Resource[IO, HTTP4sServer[IO]] =
    AsyncHttpClientCatsBackend.resource[IO]() flatMap { implicit backend =>
      for {
        connectionEc       <- ExecutionContexts.fixedThreadPool[IO](2)
        transactionBlocker <- Blocker[IO]
        xa <- HikariTransactor.newHikariTransactor[IO](
          "org.postgresql.Driver",
          dbConfig.jdbcUrl,
          dbConfig.dbUser,
          dbConfig.dbPass,
          connectionEc,
          transactionBlocker
        )
        allEndpoints = UserEndpoints.endpoints
        docs         = allEndpoints.toOpenAPI("configableauth", "0.0.1")
        docRoutes = new SwaggerHttp4s(docs.toYaml, "open-api", "spec.yaml")
          .routes[IO]
        auth       = new Auth[IO](authConfig)
        userRoutes = new UsersService[IO](auth, xa).routes
        router = CORS(
          Router(
            "/api" -> ResponseLogger
              .httpRoutes(false, false)(userRoutes <+> docRoutes)
          )
        ).orNotFound
        serverEc <- ExecutionContexts.fixedThreadPool[IO](4)
        server <- {
          BlazeServerBuilder[IO](serverEc)
            .bindHttp(apiConfig.internalPort.value, "0.0.0.0")
            .withHttpApp(router)
            .resource
        }
      } yield {
        server
      }
    }

  override def run(args: List[String]): IO[ExitCode] = {
    import Commands._

    applicationCommand.parse(args) map {
      case RunServer(apiConfig, authConfig, dbConfig) =>
        createServer(apiConfig, authConfig, dbConfig)
          .use(_ => IO.never)
          .as(ExitCode.Success)
      case RunMigrations(config) => runMigrations(config)
    } match {
      case Left(e) =>
        IO {
          println(e.toString())
        } map { _ =>
          ExitCode.Error
        }
      case Right(s) => s
    }
  }
}
