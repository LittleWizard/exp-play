package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{Cookie, Action, Controller}
import utils.{StaticContent, ServiceClient}
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class WvyuController @Inject() (serviceClient: ServiceClient) extends Controller {

  def index(embed: Boolean = false) = Action.async { request =>
    val likes = serviceClient.makeServiceCall("likes")
    val comments = serviceClient.makeServiceCall("comments")

    val css = Vector("/assets/stylesheets/wvyu.css")
    val js = Vector("/assets/javascripts/wvyu.js")

    for {
      likesCount <- likes
      commentsCount <- comments
    } yield {
      if(embed) Ok(views.html.wvyu.wvyuBody(likesCount.toInt, commentsCount.toInt))
        .withCookies(Cookie("oga", "boga"))
        .withHeaders(StaticContent.asHeaders(css, js):_*)
      else Ok(views.html.wvyu.wvyu(likesCount.toInt, commentsCount.toInt)).withCookies(Cookie("oga", "boga"))
    }
  }
}
