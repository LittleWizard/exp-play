package controllers

import javax.inject.Inject

import akka.stream.scaladsl.Source
import data.FakeServiceClient
import play.api.libs.streams.Streams
import play.api.mvc.{Action, Controller}
import utils.{PageLetRenderOptions, BigPipe, HtmlPageLet}
import views.stream.stream.withBigPipe
import scala.concurrent.ExecutionContext.Implicits.global
import ui.HtmlStreamImplicits._

class WithBigPipe @Inject() (fakeServiceClient: FakeServiceClient) extends Controller {


  def index = Action {

    //Make several fake service calls in parallel to represent fetching data from remote backends. Some of the calls will be fast, some
    //medium and some slow

    val profileFuture = fakeServiceClient.fakeRemoteCallMedium("profile")
    val graphFuture = fakeServiceClient.fakeRemoteCallMedium("graph")
    val feedFuture = fakeServiceClient.fakeRemoteCallSlow("feed")
    val inboxFuture = fakeServiceClient.fakeRemoteCallSlow("inbox")
    val adsFuture = fakeServiceClient.fakeRemoteCallFast("ads")
    val searchFuture = fakeServiceClient.fakeRemoteCallFast("search")

    //Convert each Future into PageLet which will be rendered as HTML as soon as the data is available

    val profile = HtmlPageLet("profile", profileFuture.map(views.html.ui.module.apply(_)))
    val graph = HtmlPageLet("graph", graphFuture.map(views.html.ui.module.apply(_)))
    val feed = HtmlPageLet("feed", feedFuture.map(views.html.ui.module.apply((_))))
    val inbox = HtmlPageLet("inbox", inboxFuture.map(views.html.ui.module.apply((_))))
    val ads = HtmlPageLet("ads", adsFuture.map(views.html.ui.module.apply((_))))
    val search = HtmlPageLet("search", searchFuture.map(views.html.ui.module.apply((_))))


    //Using BigPipe to compose the PageLets and render them immediately using a streaming template

    val bigPipe = new BigPipe(PageLetRenderOptions.ClientSide, profile, graph, feed, inbox, ads, search)

    val res = withBigPipe(bigPipe, profile, graph, feed, inbox, ads, search)
    val result = Source.fromPublisher(Streams.enumeratorToPublisher(res))
    Ok.chunked(result)

  }

}
