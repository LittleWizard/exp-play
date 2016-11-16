package controllers

import javax.inject.{Inject, Singleton}

import akka.stream.scaladsl.Source
import play.api.libs.iteratee.Enumerator
import play.api.libs.streams.Streams
import play.api.mvc.{Action, Controller}
import ui.HtmlStream
import utils.{Compose, ServiceClient}
import views.stream.stream.wvypStreaming
import scala.concurrent.ExecutionContext.Implicits.global
import ui.HtmlStreamImplicits._

@Singleton
class Wvyp2Controller @Inject() (serviceClient: ServiceClient) extends Controller {

  def index(embed: Boolean = false) = Action { request =>
    val wvyp = serviceClient.makeServiceCall("wvyp")
    val search = serviceClient.makeServiceCall("search")
    val wvypStream = Enumerator.flatten(wvyp.map(str => Enumerator(views.html.wvyp.wvypCount(str.toInt))))
    val searchStream = Enumerator.flatten(search.map(str => Enumerator(views.html.wvyp.searchCount(str.toInt))))
    val body = wvypStream.andThen(searchStream)
    val result = Source.fromPublisher(Streams.enumeratorToPublisher(body))
    Ok.chunked(result)
  }


  def index2(embed: Boolean = false) = Action { request =>
    val wvyp = serviceClient.makeServiceCall("wvyp")
    val search = serviceClient.makeServiceCall("search")
    val wvypStream = Compose.renderStream(wvyp.map(str => views.html.wvyp.wvypCount(str.toInt)), "wvypCount")
    val searchStream = Compose.renderStream(search.map(str => views.html.wvyp.searchCount(str.toInt)), "searchCount")
    //val body = wvypStream.andThen(searchStream)

    val body = HtmlStream.interleave(wvypStream, searchStream)

    val res = wvypStreaming(body)

    val result = Source.fromPublisher(Streams.enumeratorToPublisher(res))
    Ok.chunked(result)
  }



  //def index3



}
