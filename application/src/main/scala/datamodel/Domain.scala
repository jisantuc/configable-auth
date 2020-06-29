package com.jisantuc.configableauth.datamodel

import cats.data.{NonEmptyList, ValidatedNel}
import cats.syntax.validated._
import com.monovore.decline.Argument

sealed abstract class Domain

object Domain {

  case object Read        extends Domain
  case object Creative    extends Domain
  case object Destructive extends Domain

  def fromStringValidated(s: String): ValidatedNel[String, Domain] = s.toLowerCase match {
    case "read"        => Read.valid
    case "creative"    => Creative.valid
    case "destructive" => Destructive.valid
    case _             => NonEmptyList.of(s"Could not parse domain from: $s").invalid
  }

  implicit val argumentDomain: Argument[Domain] = new Argument[Domain] {
    def read(string: String) = fromStringValidated(string)

    def defaultMetavar: String = "authed-domain"
  }
}
