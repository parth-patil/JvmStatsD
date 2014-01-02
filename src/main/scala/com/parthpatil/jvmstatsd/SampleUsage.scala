package com.parthpatil.jvmstatsd

import akka.actor.ActorSystem
import scala.concurrent.ExecutionContext.Implicits.global

object SampleUsage {
  def main(args: Array[String]) {
    val actorSystem = ActorSystem("statsd-actor-system")
    val statsD = new StatsD(
      context = actorSystem,
      host = "localhost",
      port = 18125)

    val statsSender = new StatsSender(statsD)

    // Create the JvmStatsTask that publishes stats every 10 secs
    statsSender.addTask(new JvmStatsTask, 1)
  }
}
