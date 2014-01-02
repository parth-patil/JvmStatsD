package com.parthpatil.jvmstatsd

import scala.collection.mutable
import java.util.concurrent.{TimeUnit, Executors}

class StatsSender(statsd: StatsD, numSchedulerThreads: Int = 2) {
  // Tasks that need to be scheduled to send stats to statsd
  private val tasks = mutable.ArrayBuffer[(StatsTask, Int)]()
  val fScheduler    = Executors.newScheduledThreadPool(numSchedulerThreads)

  def addTask(task: StatsTask, secs: Int) {
    task.setStatsD(statsd)
    tasks += ((task, secs))
    fScheduler.scheduleWithFixedDelay(task, 0, secs, TimeUnit.SECONDS)
  }

  def getTasks(): Seq[(StatsTask, Int)] = {
    tasks.toSeq
  }
}
