package utils

import play.api.libs.json.{Json, JsValue}
import play.twirl.api.Html

object Embed {


  /**
   * Convert the given json into an HTML that can be safely embedded into a website in a way that the browser completely ignores it.
   * @param json
   * @return
   */

  def escapeForEmbedding(json: JsValue): Html = {
    escapeForEmbedding(Json.stringify(json));
  }


  /**
   * Convert the given HTML into a HTML string that can be safely embedded into a web page in a way that the browser completely ignores it
   * @param html
   * @return
   */

  def escapeForEmbedding(html: Html): Html = {
    escapeForEmbedding(html.body)
  }

  /**
   * Convert the given String into a HTML that can be safely embedded into a web page in a way that the browser
   * completely ignores it.
   * @param str
   * @return
   */

  def escapeForEmbedding(str: String) = {
    Html(escapeDashes(str));
  }

  /** *
    * To hide content from the browse, we wrap it in an HTML comment. To make sure no content can escape from that
    * comment, the only thing we have to do is escape double dashes
    * @param str
    * @return
    */

  private def escapeDashes(str: String): String = {
    str.replaceAll("--", "\\\\\\u002d\\\\\\u002d")
  }

}
