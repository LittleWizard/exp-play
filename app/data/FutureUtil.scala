package data

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import akka.actor.ActorSystem
import scala.concurrent.Future
import akka.pattern.after
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class FutureUtil @Inject() (actorSystem: ActorSystem) {

  /**
   * Return a Scala Future that will be redeemed with the given message after the specified delay
   *
   * @param message
   * @param delay
   * @param unit
   * @tparam A
   * @return
   */

  def timeout[A](message: => A, delay: Long, unit: TimeUnit = TimeUnit.MILLISECONDS): Future[A] = {
    after(FiniteDuration(delay, TimeUnit.MILLISECONDS), actorSystem.scheduler)(Future(message))
  }


}
