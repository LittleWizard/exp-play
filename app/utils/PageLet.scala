package utils

import akka.stream.Materializer
import play.api.http.HeaderNames
import play.api.libs.json.{Json, JsValue}
import play.api.mvc.{Cookies, Cookie, Codec, Result}
import play.twirl.api.Html
import ui.HtmlStream
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import PageLetConstants._

trait PageLet {

  /** *
    * A unique id for this PageLet. Usually corresponds to the id in the DOM where this PageLet should be rendered
    */

  val id: String


  /**
   * Render a HTML placeholder for this PageLet. This will be filled in later using JavaScript code when the PageLet data is available and shows up in the browser
   * @return
   */

  def renderPlaceholder: HtmlStream = {
    HtmlStream.fromHtml(views.html.ui.pageLetServerSide(id, EmptyContent))
  }


  /**
   * Render all the HTML for this PageLet server side. This is typically used when the PageLets are being streamed
   * in order, which is useful for clients that do not support JavaScript and search engine crawlers(i.e SEO)
   * @return
   */

  def renderServerSide: HtmlStream


  /**
   * Render the HTML for this PageLet so that it's initially invisible and can be inserted into the proper place in the DOM client side, using JavaScript.
   * This is typically used when the PageLets are being streamed out of order to minimized the load time for a page
   * @return
   */

  def renderClientSide: HtmlStream



}


/**
 * A PageLet that contains HTML. Both server side and client side rendering are supported
 * @param id
 * @param content
 */

case class HtmlPageLet(id: String, content: Future[Html]) extends  PageLet {
  /**
   * Render all the HTML for this PageLet server side. This is typically used when the PageLets are being streamed
   * in order, which is useful for clients that do not support JavaScript and search engine crawlers(i.e SEO)
   * @return
   */
  override def renderServerSide: HtmlStream = {
    HtmlStream.fromHtmlFuture(content.map(str => views.html.ui.pageLetServerSide(id, str.body)))
  }

  /**
   * Render the HTML for this PageLet so that it's initially invisible and can be inserted into the proper place in the DOM client side, using JavaScript.
   * This is typically used when the PageLets are being streamed out of order to minimized the load time for a page
   * @return
   */
  override def renderClientSide: HtmlStream = {
    HtmlStream.fromHtmlFuture(content.map(str => views.html.ui.pageLetClientSide(str.body, id, PageLetContentType.html)))
  }

}


/**
 * A PageLet that contains JSON. The general usage pattern is to send this JSON to the browser and render it using a
 * client side templating language, such as Mustache.js. Therefore, this PageLet only supports client side rendering and will
 * throw an exception if you try to render it server side
 * @param id
 * @param content
 */

case class JsonPageLet(id: String, content: Future[JsValue]) extends PageLet {
  /**
   * Render all the HTML for this PageLet server side. This is typically used when the PageLets are being streamed
   * in order, which is useful for clients that do not support JavaScript and search engine crawlers(i.e SEO)
   * @return
   */
  override def renderServerSide: HtmlStream = {
    throw new UnsupportedOperationException(s"Server side rendering is not supported for ${getClass.getName}")
  }

  /**
   * Render the HTML for this PageLet so that it's initially invisible and can be inserted into the proper place in the DOM client side, using JavaScript.
   * This is typically used when the PageLets are being streamed out of order to minimized the load time for a page
   * @return
   */
  override def renderClientSide: HtmlStream = {
    HtmlStream.fromHtmlFuture(content.map(json => views.html.ui.pageLetClientSide(Json.stringify(json), id, PageLetContentType.json)))
  }
}


/**
 * A PageLet that contains plain text. Both server side and client side rendering are supported
 * @param id
 * @param content
 */

case class TextPageLet(id: String, content: Future[String]) extends PageLet {
  /**
   * Render all the HTML for this PageLet server side. This is typically used when the PageLets are being streamed
   * in order, which is useful for clients that do not support JavaScript and search engine crawlers(i.e SEO)
   * @return
   */
  override def renderServerSide: HtmlStream = {
    HtmlStream.fromHtmlFuture(content.map(str => views.html.ui.pageLetServerSide(id, str)))
  }

  /**
   * Render the HTML for this PageLet so that it's initially invisible and can be inserted into the proper place in the DOM client side, using JavaScript.
   * This is typically used when the PageLets are being streamed out of order to minimized the load time for a page
   * @return
   */
  override def renderClientSide: HtmlStream = {
    HtmlStream.fromHtmlFuture(content.map(str => views.html.ui.pageLetClientSide(str, id, PageLetContentType.text)))
  }
}


object PageLetConstants {
  val EmptyContent = ""
}
