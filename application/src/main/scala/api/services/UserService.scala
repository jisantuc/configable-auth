package com.jisantuc.configableauth.api.services

import java.util.UUID

import cats.effect._
import cats.implicits._
import com.jisantuc.configableauth.api.endpoints.{TokenHeader, UserEndpoints}
import com.jisantuc.configableauth.database.UserDao
import com.jisantuc.configableauth.datamodel.User
import doobie.util.transactor.Transactor
import doobie._
import doobie.implicits._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import sttp.tapir.server.http4s._
import eu.timepit.refined.auto._

class UsersService[F[_]: Sync](xa: Transactor[F])(
    implicit contextShift: ContextShift[F]
) extends Http4sDsl[F] {

  def listUsers(tokenHeader: Option[TokenHeader]): F[Either[Unit, List[User]]] =
    UserDao.query.list.transact(xa).map(Either.right)

  def getUser(tokenHeader: Option[TokenHeader], id: UUID): F[Either[Unit, User]] =
    UserDao.query.filter(id).selectOption.transact(xa) map {
      case Some(user) => Either.right(user)
      case _          => Either.left(())
    }

  def createUser(tokenHeader: Option[TokenHeader], user: User.Create): F[Either[Unit, User]] =
    UserDao.create(user).transact(xa) map {
      case user: User => Either.right(user)
      case _          => Either.left(())
    }

  def deleteUser(tokenHeader: Option[TokenHeader], id: UUID): F[Either[Unit, Unit]] =
    UserDao.query.filter(id).delete.transact(xa) map {
      case 1 => Either.right(())
      case _ => Either.left(())
    }

  def updateUser(tokenHeader: Option[TokenHeader], user: User, id: UUID): F[Either[Unit, Unit]] =
    UserDao.update(id, user).transact(xa) map {
      case 1 => Either.right(())
      case _ => Either.left(())
    }

  val routes: HttpRoutes[F] = UserEndpoints.listUsers.toRoutes(u => listUsers(u)) <+> UserEndpoints.getUser
    .toRoutes(Function.tupled(getUser)) <+> UserEndpoints.createUser.toRoutes(
    Function.tupled(createUser)
  ) <+> UserEndpoints.deleteUser.toRoutes(Function.tupled(deleteUser)) <+> UserEndpoints.updateUser
    .toRoutes(Function.tupled(updateUser))
}
