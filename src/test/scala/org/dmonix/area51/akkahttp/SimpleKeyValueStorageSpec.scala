package org.dmonix.area51.akkahttp

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.Specs2RouteTest
import org.dmonix.area51.akkahttp.SimpleKeyValueStorage._
import org.specs2.mutable.Specification

/**
  * @author Peter Nerg
  */
class SimpleKeyValueStorageSpec extends Specification with Specs2RouteTest with SimpleKeyProtocol {

  
  "Invoking /kv/key path" >> {
   "with a GET" >> {
     val storage = SimpleKeyValueStorage()
     val existingKey = createRandomKey()
     val nonExistingKey = createRandomKey()
     storage.storeKey(existingKey)
     "for a non-existing key shall yield 404 NOT FOUND" >> {
       Get("/v1/key/"+nonExistingKey.name) ~> storage.route ~> check {
         status shouldEqual StatusCodes.NotFound
       }
     }
     "for an existing key shall yield 200 OK" >> {
       Get("/v1/key/"+existingKey.name) ~> storage.route ~> check {
         status shouldEqual StatusCodes.OK
         responseAs[KeyValue] shouldEqual existingKey
       }
     }
   }    

   "with a Delete" >> {
     val storage = SimpleKeyValueStorage()
     val existingKey = createRandomKey()
     val nonExistingKey = createRandomKey()
     storage.storeKey(existingKey)
     "for a non-existing key shall yield 200 OK" >> {
       Delete("/v1/key/"+nonExistingKey.name) ~> storage.route ~> check {
         status shouldEqual StatusCodes.OK
         responseAs[String] === "false"
       }
     }
     "for an existing key shall yield 200 OK" >> {
       Delete("/v1/key/"+existingKey.name) ~> storage.route ~> check {
         status shouldEqual StatusCodes.OK
         storage.getKey(existingKey.name) must beNone
         responseAs[String] === "true"
       }
     }
   }

  "with a PUT" >> {
      val storage = SimpleKeyValueStorage()
      val existingKey = createRandomKey()
      val nonExistingKey = createRandomKey()
      storage.storeKey(existingKey)
      "for a non-existing key shall yield 201 CREATED" >> {
        Put("/v1/key/"+nonExistingKey.name, nonExistingKey.value) ~> storage.route ~> check {
          status shouldEqual StatusCodes.Created
          storage.getKey(nonExistingKey.name) must beSome().like(areMatch(nonExistingKey))
        }
      }
      "for an existing key shall yield 403 FORBIDDEN" >> {
        Put("/v1/key/"+existingKey.name) ~> storage.route ~> check {
          status shouldEqual StatusCodes.Forbidden
        }
      }
    }

    "with a POST" >> {
      val storage = SimpleKeyValueStorage()
      val existingKey = createRandomKey()
      val nonExistingKey = createRandomKey()
      storage.storeKey(existingKey)
      "for a non-existing key shall yield 404 NOT FOUND" >> {
        Post("/v1/key/"+nonExistingKey.name) ~> storage.route ~> check {
          status shouldEqual StatusCodes.NotFound
        }
      }
      "for an existing key shall yield 200 OK" >> {
        Post("/v1/key/"+existingKey.name, "new-value") ~> storage.route ~> check {
          status shouldEqual StatusCodes.OK
          storage.getKey(existingKey.name) must beSome().like(areMatch(existingKey.name, "new-value"))
        }
      }
    }
  }

  "Invoking /kv/key path" >> {
    "with a GET and no data shall yield an empty list" >> {
      val storage = SimpleKeyValueStorage()
      Get("/v1/keys") ~> storage.route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Seq[String]] must beEmpty

      }
    }
    "with a GET and data shall yield a list of the keys" >> {
      val storage = SimpleKeyValueStorage()
      val kv1 = createRandomKey()
      val kv2 = createRandomKey()
      storage.storeKey(kv1)
      storage.storeKey(kv2)
      Get("/v1/keys") ~> storage.route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Seq[String]] must contain(allOf(kv1.name, kv2.name))
      }
    }
  }

  private def areMatch(expected:KeyValue):PartialFunction[KeyValue, Boolean] = areMatch(expected.name, expected.value)
  private def areMatch(name:String, value:String):PartialFunction[KeyValue, Boolean] = {case KeyValue(`name`, `value` , _) => true}

  private def createRandomKey() = KeyValue(UUID.randomUUID().toString, "my-value-"+System.currentTimeMillis())
  
}
