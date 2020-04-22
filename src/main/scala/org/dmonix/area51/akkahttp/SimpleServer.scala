package org.dmonix.area51.akkahttp

import java.net.InetSocketAddress

import akka.actor.{ActorSystem, Terminated}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import org.slf4j.LoggerFactory

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}


object SimpleServer {
  def apply(route:Route) = new SimpleServer(route)
}

/**
  * @author Peter Nerg
  */
class SimpleServer(route:Route) {
  private val logger = LoggerFactory.getLogger(classOf[SimpleServer])

  private implicit val system = ActorSystem("consul-sim")
  private implicit val materializer = ActorMaterializer()
  private implicit val executionContext = system.dispatcher

  private var server: Option[ServerBinding] = None
  
  /**
    * Starts the simulator on the designated port
    * @param port The port to listen to, defaults to 0 i.e. chosen by the host
    * @return The host/port the simulator listens to
    */
  def start(port:Int = 0): InetSocketAddress = synchronized {
    val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", port)
    val binding =  Await.result(bindingFuture, 10.seconds)
    server = Some(binding)
    val address = binding.localAddress 
    logger.info(s"Started Consul Sim on port [${address.getPort}]")
    address
  }

  /**
    * Shutdown the simulator
    * @return
    */
  def shutdown(): Terminated = synchronized {
    val shutdownFuture = server
      .map(_.unbind()) //unbind the server if it is started
      .getOrElse(Future.successful(())) //server not started, shutdown is "success"
      .flatMap(_ => system.terminate()) //terminate the actor system

    Await.result(shutdownFuture, 30.seconds)
  }
}
