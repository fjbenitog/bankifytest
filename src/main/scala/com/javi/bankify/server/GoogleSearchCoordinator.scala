package com.javi.bankify.server

import akka.actor.{Actor, ActorRef, Props, ReceiveTimeout}
import com.javi.bankify.server.GoogleSearch._
import com.javi.bankify.server.GoogleSearchReceptionist._

import scala.concurrent.duration._

object GoogleSearchCoordinator {

  def props(reply: ActorRef, googleSearch: ActorRef): Props =
    Props(new GoogleSearchCoordinator(reply, googleSearch))
}

class GoogleSearchCoordinator(reply: ActorRef, googleSearch: ActorRef) extends Actor {

  override def preStart(): Unit =
    context.setReceiveTimeout(3 seconds)

  override def receive: Receive = {
    case Query(query) =>
      context.system.log.debug(s"Created Coordinator for GoogleSearch with query:$query")
      googleSearch ! Search(query)

    case f @ Found(title, url, text) =>
      context.system.log.debug(s"A result was found:$f")
      reply ! Result(title, url, text)
      context.stop(self)

    case ReceiveTimeout =>
      context.system.log.debug(s"Timeout was reached for searching query.")
      reply ! SearchFailure("Timeout Searching in Google")
      context.stop(self)

    case Failure(message) =>
      context.system.log.debug(s"Failure Searching in Google - $message.")
      reply ! SearchFailure(message)
      context.stop(self)
  }
}