package com.jisantuc.configableauth.api.commands

import com.monovore.decline.Opts

object Options extends DatabaseOptions with ApiOptions with AuthOptions {

  val catalogRoot: Opts[String] = Opts
    .option[String]("catalog-root", "Root of STAC catalog to import")
}
