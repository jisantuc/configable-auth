package com.jisantuc.configableauth.api.commands

import cats.implicits._
import com.monovore.decline.Opts
import com.monovore.decline.refined._
import com.jisantuc.configableauth.datamodel.Domain
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString

trait AuthOptions {

  private val authedDomains =
    (Opts
      .options[Domain](
        "authed-domain",
        help =
          "Which sorts of actions to require authentication for. Excludes --auth-all as a valid option if provided."
      )
      .orEmpty)
      .orElse(
        Opts
          .flag("auth-all", help = "Authenticate all routes. Excludes --authed-domain as valid options if provided.")
          .orFalse map {
          case true => List(Domain.Read, Domain.Destructive, Domain.Creative)
          case _    => Nil
        }
      )

  private val authHost =
    Opts
      .option[NonEmptyString]("authentication-host", help = "Host for authentication server, if one exists")

  private val authPort =
    Opts.option[PosInt]("authentication-port", help = "Which port to connect to the authentication host on")

  private val authRoute =
    Opts
      .option[String]("authentication-route", help = "Url route to authentication endpoint on authentication host")
      .orNone

  private val authServiceScheme =
    Opts
      .option[String]("authentication-scheme", "Scheme authentication service is exposed with")
      .withDefault("http")
      .validate("Scheme must be either 'http' or 'https'")(s => (s == "http" || s == "https"))

  private val authenticationServiceConfig =
    ((authHost, authPort, authRoute, authServiceScheme) mapN { AuthenticationServiceConfig.apply }).orNone

  val authConfig: Opts[AuthConfig] = (authedDomains, authenticationServiceConfig) mapN {
    case (domains, serviceConfig) =>
      AuthConfig(domains.toSet, serviceConfig)
  }
}
