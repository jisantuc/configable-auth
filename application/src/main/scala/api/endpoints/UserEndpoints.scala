package com.jisantuc.configableauth.api.endpoints

import java.util.UUID

import com.jisantuc.configableauth.datamodel.User
import sttp.tapir._
import sttp.tapir.json.circe._

object UserEndpoints {

  val base = endpoint.in("users").in(header[Option[TokenHeader]]("Authorization"))

  val listUsers: Endpoint[Option[TokenHeader], String, List[User], Nothing] =
    base.get
      .out(jsonBody[List[User]])
      .errorOut(stringBody)
      .description("List Users")
      .name("User List View")

  val getUser: Endpoint[(Option[TokenHeader], UUID), String, User, Nothing] =
    base.get
      .in(path[UUID])
      .out(jsonBody[User])
      .errorOut(stringBody)
      .description("Retrieve a single user")
      .name("search-get")

  val createUser: Endpoint[(Option[TokenHeader], User.Create), String, User, Nothing] =
    base.post
      .in(jsonBody[User.Create])
      .out(jsonBody[User])
      .errorOut(stringBody)
      .description("Create a User")
      .name("create-user")

  val deleteUser: Endpoint[(Option[TokenHeader], UUID), String, Unit, Nothing] =
    base.delete
      .in(path[UUID])
      .errorOut(stringBody)
      .description("Delete a user")

  val updateUser: Endpoint[(Option[TokenHeader], User, UUID), String, Unit, Nothing] =
    base.put
      .in(jsonBody[User])
      .in(path[UUID])
      .errorOut(stringBody)
      .description("Update a user")
      .name("update-user")

  val endpoints = List(listUsers, getUser, createUser, deleteUser, updateUser)

}
