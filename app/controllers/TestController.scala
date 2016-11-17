package controllers

import javax.inject.Singleton

import akka.stream.scaladsl.Source
import play.api.libs.streams.Streams
import play.api.mvc.{Action, Controller}
import views.stream.stream.test2
import ui.HtmlStreamImplicits._

@Singleton
class TestController  extends Controller {


  def test = Action { request =>
   Ok(views.html.test())
  }

  /*def testTwo = Action { request =>
    val res = test2()
    val res2 = Streams.enumeratorToPublisher(res)
    val result = Source.fromPublisher(res2)
    Ok(result)
  }*/


}
