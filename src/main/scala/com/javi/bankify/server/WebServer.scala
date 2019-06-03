package com.javi.bankify.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import cats.effect.IO

object WebServer {

  def start: IO[Http.ServerBinding] = {

    val interface = "0.0.0.0"
    val port      = 8080
    val server    = new WebServer() with HttpRouter

    implicit val actorSystem: ActorSystem = server.actorSystem
    import server.httpMateriaizer

    val bindingFuture = Http().bindAndHandle(server.main, interface, port)

    IO.fromFuture(IO(bindingFuture))
  }
}

class WebServer extends BankifyTestAPI {
  implicit val actorSystem = ActorSystem("bankifytest-system")
}