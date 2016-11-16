package controllers

import javax.inject._
import akka.stream.Materializer
import play.api.mvc._
import utils.{StaticContent, Compose}
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class HomeController @Inject()(wvypController: WvypController, wvyuController: WvyuController)(implicit materializer: Materializer) extends Controller {

  def index = Action.async { request =>

    val wvyp = wvypController.index(embed = true)(request)
    val wvyu = wvyuController.index(embed = true)(request)

    for {

      //use play's internal router and call those controller methods like functions

      wvypResult <- wvyp
      wvyuResult <- wvyu

      wvypBody <- Compose.readBody(wvypResult)
      wvyuBody <- Compose.readBody(wvyuResult)

    } yield {
      if(wvypResult.header.status == 200 && wvyuResult.header.status == 200) {

        val css = StaticContent.mergeCssHeaders(wvypResult, wvyuResult)
        val js = StaticContent.mergeJsHeaders(wvypResult, wvyuResult)

        Ok(views.html.home(wvypBody, wvyuBody, css, js)).withCookies(Compose.mergeCookies(wvypResult, wvyuResult):_*)
      }

      else {
        NotFound  //or something else(handle errors)
      }

    }
  }

}
