package com.jisantuc.configableauth.api

import com.jisantuc.configableauth.api.commands.AuthConfig
import com.jisantuc.configableauth.api.endpoints.TokenHeader
import com.jisantuc.configableauth.datamodel.Domain

import cats.implicits._
import cats.effect.Sync
import eu.timepit.refined.auto._

class Auth[F[_]: Sync](authConfig: AuthConfig) {

  def authenticateRoute(domain: Domain)(tokenHeaderO: Option[TokenHeader]): F[Either[String, Unit]] =
    (authConfig.authedDomains.contains(domain), tokenHeaderO) match {
      case (true, Some(_)) => Sync[F].pure(Either.right(()))
      case (false, _)      => Sync[F].pure(Either.right(()))
      case _               => Sync[F].pure(Either.left("need a token for this route"))
    }
}
