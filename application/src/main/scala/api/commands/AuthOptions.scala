package com.jisantuc.configableauth.api.commands

import com.monovore.decline.Opts
import com.jisantuc.configableauth.datamodel.Domain

trait AuthOptions {

  private val authedDomains =
    Opts
      .options[Domain]("authed-domain", help = "Which sorts of actions to require authentication for")
      .orEmpty

  val authConfig: Opts[AuthConfig] = authedDomains map { domains =>
    AuthConfig(domains.toSet)
  }
}
