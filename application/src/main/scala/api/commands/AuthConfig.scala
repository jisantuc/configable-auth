package com.jisantuc.configableauth.api.commands

import com.jisantuc.configableauth.datamodel.Domain
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString

final case class AuthConfig(
    authedDomains: Set[Domain],
    authenticationServiceConfig: Option[AuthenticationServiceConfig]
)

final case class AuthenticationServiceConfig(
    authenticationHost: NonEmptyString,
    authenticationPort: PosInt,
    authenticationRoute: Option[String],
    authenticationScheme: String
) {

  val authServiceRoute = s"""$authenticationScheme://$authenticationHost${authenticationRoute map { s =>
    s"/$s"
  } getOrElse ""}"""
}
