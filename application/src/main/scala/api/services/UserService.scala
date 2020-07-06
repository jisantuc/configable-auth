package com.jisantuc.configableauth.api.services

import com.jisantuc.configableauth.api.Auth

import cats.effect._
import cats.implicits._
import com.jisantuc.configableauth.api.endpoints.UserEndpoints
import com.jisantuc.configableauth.database.UserDao
import com.jisantuc.configableauth.datamodel.{Domain, InvalidToken, User, ValidToken}
import doobie.util.transactor.Transactor
import doobie._
import doobie.implicits._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import sttp.tapir.server.http4s._
import eu.timepit.refined.auto._

import java.util.UUID

class UsersService[F[_]: Sync](auth: Auth[F], xa: Transactor[F])(
    implicit contextShift: ContextShift[F]
) extends Http4sDsl[F] {

  def listUsers: F[Either[String, List[User]]] =
    UserDao.query.list.transact(xa).map(Either.right)

  def getUser(id: UUID): F[Either[String, User]] =
    UserDao.query.filter(id).selectOption.transact(xa) map {
      case Some(user) => Either.right(user)
      case _          => Either.left("not found")
    }

  def createUser(user: User.Create): F[Either[String, User]] =
    UserDao.create(user).transact(xa) map {
      case user: User => Either.right(user)
      case _          => Either.left("could not create user")
    }

  def deleteUser(id: UUID): F[Either[String, Unit]] =
    UserDao.query.filter(id).delete.transact(xa) map {
      case 1 => Either.right(())
      case _ => Either.left("could not delete user or deleted several :thinking:")
    }

  def updateUser(user: User, id: UUID): F[Either[String, Unit]] =
    UserDao.update(id, user).transact(xa) map {
      case 1 => Either.right(())
      case _ => Either.left("could not update user or updated several :thinking:")
    }

  val listRoute = UserEndpoints.listUsers
    .serverLogicPart(auth.authenticateRoute(Domain.Read))
    .andThen({
      case (ValidToken(_, _, _), _) => listUsers
      case (InvalidToken(_, _), _)  => Sync[F].pure(Either.left("no good"))
    })
    .toRoutes

  val detailRoute = UserEndpoints.getUser
    .serverLogicPart(auth.authenticateRoute(Domain.Read))
    .andThen({
      case (ValidToken(_, _, _), userId) => getUser(userId)
      case (InvalidToken(_, _), _)       => Sync[F].pure(Either.left("no good"))
    })
    .toRoutes

  val createRoute = UserEndpoints.createUser
    .serverLogicPart(auth.authenticateRoute(Domain.Creative))
    .andThen({
      case (ValidToken(_, _, _), userCreate) => createUser(userCreate)
      case (InvalidToken(_, _), _)           => Sync[F].pure(Either.left("no good"))
    })
    .toRoutes

  val deleteRoute = UserEndpoints.deleteUser
    .serverLogicPart(auth.authenticateRoute(Domain.Destructive))
    .andThen({
      case (ValidToken(_, _, _), userId) => deleteUser(userId)
      case (InvalidToken(_, _), _)       => Sync[F].pure(Either.left("no good"))
    })
    .toRoutes

  val updateRoute = UserEndpoints.updateUser
    .serverLogicPart(auth.authenticateRoute(Domain.Destructive))
    .andThen({
      case (ValidToken(_, _, _), (update, userId)) => updateUser(update, userId)
      case (InvalidToken(_, _), _)                 => Sync[F].pure(Either.left("no good"))
    })
    .toRoutes

  val routes: HttpRoutes[F] = listRoute <+> detailRoute <+> createRoute <+> deleteRoute <+> updateRoute
}
