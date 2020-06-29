package com.jisantuc.configableauth.api.commands

import com.jisantuc.configableauth.datamodel.Domain

final case class AuthConfig(
    authedDomains: Set[Domain]
)
