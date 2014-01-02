package com.parthpatil.jvmstatsd

import java.text.DecimalFormat

class StatsTask() extends Runnable {
  protected var statsd: StatsD = null

  // formatter to remove scientific notation from doubles
  val formatter = new DecimalFormat("#")

  override def run() { }
  def setStatsD(sd: StatsD) { statsd = sd }
}
