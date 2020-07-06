package com.jisantuc.configableauth.api

import com.jisantuc.configableauth.api.commands.{AuthConfig, AuthenticationServiceConfig}
import com.jisantuc.configableauth.api.endpoints.TokenHeader
import com.jisantuc.configableauth.datamodel.{Domain, TokenResponse, ValidToken}

import cats.implicits._
import cats.effect.Sync
import eu.timepit.refined.auto._
import io.circe.{CursorOp, DecodingFailure}
import io.circe.syntax._
import sttp.client.circe._
import sttp.client._
import sttp.model.Uri

class Auth[F[_]: Sync: SttpBackend[*[_], Nothing, NothingT]](authConfig: AuthConfig) {

  def validateToken(
      token: TokenHeader,
      authRequestConfig: AuthenticationServiceConfig
  ): F[Either[String, TokenResponse]] = {
    val requestUri: java.net.URI =
      java.net.URI.create(
        s"""${authRequestConfig.authenticationScheme}://${authRequestConfig.authenticationHost}:${authRequestConfig.authenticationPort}/${authRequestConfig.authenticationRoute getOrElse ""}"""
      )
    val uri = Uri(requestUri)
    basicRequest
      .post(uri)
      .body(Map("token" -> s"${token.headerValue.replace("Bearer ", "")}").asJson)
      .response(asJson[TokenResponse])
      .send() map { response =>
      println(s"Status is: ${response.statusText}")
      println(s"Body is: ${response.body}")
      response.body.leftMap {
        case DeserializationError(_, e: DecodingFailure) =>
          println(s"Err is: $e")
          s"The authentication server didn't respond as expected at ${CursorOp.opsToPath(e.history)}"
        case err =>
          s"Something went wrong reading JSON from the configured auth server: ${err.body}"
      }
    }
  }

  def authenticateRoute(domain: Domain)(tokenHeaderO: Option[TokenHeader]): F[Either[String, TokenResponse]] =
    (authConfig.authedDomains.contains(domain), tokenHeaderO, authConfig.authenticationServiceConfig) match {
      case (true, Some(token), Some(config)) => validateToken(token, config)
      case (true, None, Some(_))             => Sync[F].pure(Either.left("This route requires a token"))
      case (true, _, None) =>
        Sync[F].pure(Either.left("An authentication service was not configured for this server"))
      case (false, _, _) => Sync[F].pure(Either.right(ValidToken(Nil, 9000L, "anonymous")))
    }
}
