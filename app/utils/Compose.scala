package utils

import akka.stream.Materializer
import play.api.http.HeaderNames
import play.api.mvc.{Codec, Cookies, Cookie, Result}
import play.twirl.api.Html
import ui.HtmlStream

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Compose {
  def mergeCookies(results: Result*): Seq[Cookie] = {
    results.flatMap { result =>
      result.header.headers.get(HeaderNames.SET_COOKIE).map(Cookies.decodeCookieHeader).getOrElse(Seq.empty)
    }
  }

  def readBody(result: Result)(implicit codec: Codec, materializer: Materializer): Future[Html] = {
    result.body.consumeData.map(byteString => Html(new String(byteString.decodeString(codec.charset))))
  }

  def renderStream(html: Html, id: String): HtmlStream = {
    HtmlStream(render(html, id))
  }

  def render(html: Html, id: String): Html = {
    views.html.ui.pagelet(html, id)
  }

  def renderStream(htmlFuture: Future[Html], id: String): HtmlStream = {
    HtmlStream.flattern(htmlFuture.map(html => renderStream(html, id)))
  }
}
