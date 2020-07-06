package com.jisantuc.configableauth.datamodel

import cats.syntax.functor._
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder, Json}

sealed abstract class TokenResponse

object TokenResponse {

  implicit val encTokenResponse: Encoder[TokenResponse] = new Encoder[TokenResponse] {

    def apply(value: TokenResponse): Json = value match {
      case valid @ ValidToken(_, _, _)  => Encoder[ValidToken].apply(valid)
      case invalid @ InvalidToken(_, _) => Encoder[InvalidToken].apply(invalid)
    }
  }
  implicit val decTokenResponse: Decoder[TokenResponse] = Decoder[InvalidToken].widen or Decoder[ValidToken].widen
}

case class ValidToken(
    scopes: List[String],
    ttl: Long,
    userId: String
) extends TokenResponse

object ValidToken {
  implicit val decValidToken: Decoder[ValidToken] = deriveDecoder
  implicit val encValidToken: Encoder[ValidToken] = deriveEncoder
}

case class InvalidToken(
    badToken: String,
    realm: String
) extends TokenResponse

object InvalidToken {
  implicit val decInvalidToken: Decoder[InvalidToken] = deriveDecoder

  implicit val encInvalidToken: Encoder[InvalidToken] = deriveEncoder

}
