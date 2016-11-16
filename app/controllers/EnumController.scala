package controllers

import javax.inject.Inject
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Source}
import play.api.libs.iteratee.Enumerator
import play.api.libs.streams.Streams
import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class EnumController @Inject() (actorSystem: ActorSystem) extends Controller {

  def index = Action {
    val source = Source.single("Hello World\n")
    Ok.chunked(source)
  }


  def inf1 = Action {
    val source = Source.fromFuture(Future.successful("YES"))
    Ok.chunked(source)
  }

  def inf2 = Action {
    val source = Source.unfoldAsync()(size => {
      val data: Future[String] = akka.pattern.after(500 millis, actorSystem.scheduler)(Future("hello\n"))
      data.map(contents => Some(size, contents))
    })
    Ok.chunked(source)
  }

  def inf3 = Action {
    val source = Source.tick(0 millisecond, 500 millisecond, "hello\n")
    Ok.chunked(source)
  }

  def compose = Action {
    val hello = Enumerator("hello\n")
    val bye = Enumerator("good bye\n")
    val body = hello.andThen(bye)
    Ok.chunked(Source.fromPublisher(Streams.enumeratorToPublisher(body)))
  }


  def inf4 = Action {
    val hello = Enumerator.generateM({akka.pattern.after(500 millis, actorSystem.scheduler)(Future(Some("helllo\n")))})
    val bye = Enumerator.generateM(akka.pattern.after(2000 millis, actorSystem.scheduler)(Future(Some("bye\n"))))
    val body = Enumerator.interleave(hello, bye)
    val response = Source.fromPublisher(Streams.enumeratorToPublisher(body))
    Ok.chunked(response)
  }


}
