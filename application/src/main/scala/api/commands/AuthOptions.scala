package com.jisantuc.configableauth.api.commands

import com.monovore.decline.Opts
import com.jisantuc.configableauth.datamodel.Domain

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

  val authConfig: Opts[AuthConfig] = authedDomains map { domains =>
    AuthConfig(domains.toSet)
  }
}
