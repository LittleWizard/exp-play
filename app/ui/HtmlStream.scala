package ui

import play.api.libs.iteratee.{Enumeratee, Enumerator}
import play.twirl.api.{HtmlFormat, Format, Appendable, Html}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.immutable.Seq
import scala.concurrent.{ExecutionContext, Future}


case class HtmlStream(enumerator: Enumerator[Html]) extends Appendable[HtmlStream] {

  def +=(other: HtmlStream): HtmlStream = andThen(other)

  def andThen(other: HtmlStream): HtmlStream = HtmlStream(enumerator.andThen(other.enumerator))

}


/***
  * Companion object for HtmlStream that contains convenient factory and composition methods
*/

object HtmlStream {

  /**
   * Create an HtmlStream from a String
   * @param text
   * @return
   */

  /*def apply(text: String) = {
    apply(Html(text))
  }*/

  /**
   * Create a HtmlStream from Html
   * @param html
   * @return
   */

  def apply(html: Html): HtmlStream = {
    HtmlStream(Enumerator(html))
  }

  def apply(html: Future[Html]): HtmlStream = {
    fromHtmlFuture(html)
  }


  def empty: HtmlStream = {
    fromString("")
  }

  def fromString(text: String): HtmlStream = {
    fromHtml(Html(text))
  }

  def fromHtml(html: Html): HtmlStream = {
    apply(html)
  }

  def fromStringFuture(eventualString: Future[String])(implicit ec: ExecutionContext): HtmlStream = {
    fromHtmlFuture(eventualString.map(Html.apply))
  }

  def fromHtmlFuture(eventuallyHtml: Future[Html])(implicit executionContext: ExecutionContext): HtmlStream = {
    flattern(eventuallyHtml.map(fromHtml))
  }

  def flattern(eventuallyStream: Future[HtmlStream])(implicit ec: ExecutionContext): HtmlStream = {
    fromHtmlEnumerator(Enumerator.flatten(eventuallyStream.map(_.enumerator)))
  }

  def fromHtmlEnumerator(enumerator: Enumerator[Html]): HtmlStream = {
    new HtmlStream(enumerator)
  }

  def interleave(streams: HtmlStream*): HtmlStream = {
    fromHtmlEnumerator(Enumerator.interleave(streams.map(_.enumerator)))
  }

}

object HtmlStreamFormat extends Format[HtmlStream] {
  override def raw(text: String): HtmlStream = {
    HtmlStream.fromString(text)
  }

  override def escape(text: String): HtmlStream = {
    raw(HtmlFormat.escape(text).body)
  }

  override def empty: HtmlStream = {
    raw("")
  }

  override def fill(elements: Seq[HtmlStream]): HtmlStream = {
    elements.reduce((agg, curr) => agg.andThen(curr))
  }
}

object HtmlStreamImplicits {
  implicit def toEnumerator(stream: HtmlStream)(implicit ec: ExecutionContext): Enumerator[Html] = {
    stream.enumerator.through(Enumeratee.filter(!_.body.isEmpty))
  }
}