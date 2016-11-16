package controllers

import javax.inject.Inject
import akka.actor.ActorSystem
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class MockController @Inject() (actorSystem: ActorSystem) extends Controller {

  def mock(serviceName: String) = Action.async {

    serviceName match {
      case "wvyp" => respond(data = "56", delay = 3000)
      case "search" => respond(data = "10", delay = 10)
      case "likes" => respond(data = "150", delay = 1)
      case "comments" => respond(data = "14", delay = 1)
    }

  }

  private def respond(data: String, delay: Long): Future[Result] = {
    akka.pattern.after(delay millis, actorSystem.scheduler)(Future(Ok(data)))
  }

}
