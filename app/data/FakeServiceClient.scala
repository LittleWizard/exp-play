package data

import javax.inject.{Inject, Singleton}

import play.api.libs.json.{Json, JsValue}

import scala.concurrent.Future
import scala.util.Random
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class FakeServiceClient @Inject() (futureUtil: FutureUtil) {
  import data.FakeServiceClient._


  def fakeRemoteCall(id: String, delayInMillis: Long): Future[Response] = {
    val randomJitter = new Random().nextInt(delayInMillis.toInt).toLong
    val delay = delayInMillis + randomJitter

    val fakeJsonResponse = Response(id, delay)
    futureUtil.timeout(fakeJsonResponse, delay)
  }

  def fakeRemoteCallJson(id: String, delayInMillis: Long): Future[JsValue] = fakeRemoteCall(id, delayInMillis).map(Json.toJson(_))

  def fakeRemoteCallFast(id: String): Future[Response] = fakeRemoteCall(id, FAST_RESPONSE_TIME_IN_MILLIS)
  def fakeRemoteCallMedium(id: String): Future[Response] = fakeRemoteCall(id, MEDIUM_RESPONSE_TIME_IN_MILLIS)
  def fakeRemoteCallSlow(id: String): Future[Response] = fakeRemoteCall(id, SLOW_RESPONSE_TIME_IN_MILLIS)

  def fakeRemoteCallJsonFast(id: String): Future[JsValue] = fakeRemoteCallJson(id, FAST_RESPONSE_TIME_IN_MILLIS)
  def fakeRemoteCallJsonMedium(id: String): Future[JsValue] = fakeRemoteCallJson(id, MEDIUM_RESPONSE_TIME_IN_MILLIS)
  def fakeRemoteCallJsonSlow(id: String): Future[JsValue] = fakeRemoteCallJson(id, SLOW_RESPONSE_TIME_IN_MILLIS)

  def fakeRemoteCallError(id: String, delayInMillis: Long): Future[Response] = {
    fakeRemoteCall(id, delayInMillis).map(response => throw FakeRemoteCallException(response))
  }

}

object FakeServiceClient {
  val FAST_RESPONSE_TIME_IN_MILLIS = 5
  val MEDIUM_RESPONSE_TIME_IN_MILLIS = 500
  val SLOW_RESPONSE_TIME_IN_MILLIS = 3000

  val RESPONSE_TO_TEST_ESCAPING = "Escaping test <!-- This comment should be escaped in the HTML -->"
}

case class FakeRemoteCallException(response: Response) extends RuntimeException(s"""Error in "${response.id}" after ${response.delay} ms""")