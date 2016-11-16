package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{Cookie, Action, Controller}
import utils.{StaticContent, ServiceClient}
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class WvypController @Inject() (serviceClient: ServiceClient) extends Controller {

  def index(embed: Boolean = false) = Action.async { request =>
    val wvyp = serviceClient.makeServiceCall("wvyp")
    val search = serviceClient.makeServiceCall("search")

    val css = Vector("/assets/stylesheets/wvyp.css")
    val js = Vector("/assets/javascripts/wvyp.js")

    //what is the best way of handling static content in controller, instead of a view, feels clunky

    for {
      wvypCount <- wvyp
      searchCount <- search
    } yield {
      if(embed) Ok(views.html.wvyp.wvypBody(wvypCount.toInt, searchCount.toInt))
        .withCookies(Cookie("foo", "bar"))
        .withHeaders(StaticContent.asHeaders(css, js):_*)
      else Ok(views.html.wvyp.wvyp(wvypCount.toInt, searchCount.toInt)).withCookies(Cookie("foo", "bar"))
    }
  }
}
