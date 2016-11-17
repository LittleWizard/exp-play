package utils

import play.twirl.api.Html
import ui.HtmlStream


object TestUtils {

  def render(layout: String => Html): Html = {
    layout("Hello World")
  }


  def renderStream(layout: String => HtmlStream): HtmlStream = {
    layout("Hello World")
  }


}
