package utils

import javax.inject.{Inject, Singleton}
import play.api.libs.ws.WSClient
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class ServiceClient @Inject() (wSClient: WSClient)  {

  def makeServiceCall(serviceName: String): Future[String] = {
    wSClient.url(s"http://localhost:9000/mock/$serviceName").get().map(_.body)
  }

}
