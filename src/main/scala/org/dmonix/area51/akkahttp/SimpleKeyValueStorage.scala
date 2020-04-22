package org.dmonix.area51.akkahttp

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{as, complete, entity, get, pathPrefix, put, _}
import akka.http.scaladsl.server.Route
import spray.json._

import scala.collection._
  
object SimpleKeyValueStorage {
  case class KeyValue(name:String, value:String, lastUpdatedTime:Long = System.currentTimeMillis())
  def apply() = new SimpleKeyValueStorage()
}

import SimpleKeyValueStorage._
trait SimpleKeyProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val keyValueFormat = jsonFormat3(KeyValue)
}
  
/**
  * Simple example of creating a web service for storing/deleting/updating key/values.
  * Supported operations and paths
  * PUT - v1/key/[keyname]
  * POST - v1/key/[keyname]
  * DELETE- v1/keys/[keyname]
  * GET- v1/keys/[keyname]
  * GET- v1/keys
  * @author Peter Nerg
  */
class SimpleKeyValueStorage extends SimpleKeyProtocol {

  private val keyValues = mutable.Map[String, KeyValue]()
  
  private[akkahttp] def getKey(name:String):Option[KeyValue] = keyValues.get(name)
  private[akkahttp] def storeKey(kv:KeyValue):Unit =  keyValues.put(kv.name, kv)
  
  //The '/v1/key' path
  private val keyPath: Route =
    pathPrefix("v1" / "key"/ Remaining ) { keyName =>
      get {
        getKey(keyName) match {
          case Some(value) =>
            complete(value)
          case None =>
            complete(StatusCodes.NotFound, s"Key '$keyName' not found")
        }
      } ~
        delete {
          val existed = keyValues.remove(keyName).isDefined
          complete(existed.toString)
        } ~
        put {
          entity(as[String]) { keyValue =>
            getKey(keyName) match {
              case Some(_) =>
                complete(StatusCodes.Forbidden, s"The key '$keyName' already exists")
              case None =>
                storeKey(KeyValue(keyName, keyValue))
                complete(StatusCodes.Created, s"Key '$keyName' was created")
            }
          }
        } ~
        post {
          entity(as[String]) { keyValue =>
            getKey(keyName) match {
              case Some(kv) =>
                storeKey(kv.copy(value = keyValue, lastUpdatedTime = System.currentTimeMillis()))
                complete(s"Key '$keyName' was updated")
              case None =>
                complete(StatusCodes.NotFound, s"Key '$keyName' not found")
            }
          }
        }
    }
  
  //The '/v1/keys' path
  private val keysPath: Route =
      pathPrefix("v1" / "keys" ) {
        get {
          complete(keyValues.keys)
        }
      }
  
  def route:Route = keyPath ~ keysPath
}
