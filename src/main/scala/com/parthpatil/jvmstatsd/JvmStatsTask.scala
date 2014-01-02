package com.parthpatil.jvmstatsd

import java.lang.management.ManagementFactory
import scala.collection.mutable
import scala.collection.JavaConverters._
import com.twitter.conversions.string._
import java.util.Date

/**
 * Created by parth on 1/1/14.
 */
class JvmStatsTask extends StatsTask {
  override def run() {
    getJvmGauges() foreach { case (k, v)  => statsd.gauge(k, formatter.format(v)) }
    println((new Date) + " Sent jvm guages")

    getJvmCounters() foreach { case (k,v) => statsd.increment(k, v) }
    println((new Date) + " Sent jvm counters")
  }

  def getJvmGauges(): Map[String, Long] = {
    val out = mutable.Map[String, Long]()

    val mem = ManagementFactory.getMemoryMXBean()

    val heap = mem.getHeapMemoryUsage()
    out += ("jvm.heap.committed" -> heap.getCommitted())
    out += ("jvm.heap.max" -> heap.getMax())
    out += ("jvm.heap.used" -> heap.getUsed())

    val nonheap = mem.getNonHeapMemoryUsage()
    out += ("jvm.nonheap.committed" -> nonheap.getCommitted())
    out += ("jvm.nonheap.max" -> nonheap.getMax())
    out += ("jvm.nonheap.used" -> nonheap.getUsed())

    val threads = ManagementFactory.getThreadMXBean()
    out += ("jvm.thread.daemon_count" -> threads.getDaemonThreadCount().toLong)
    out += ("jvm.thread.count" -> threads.getThreadCount().toLong)
    out += ("jvm.thread.peak_count" -> threads.getPeakThreadCount().toLong)

    val runtime = ManagementFactory.getRuntimeMXBean()
    out += ("jvm.start_time" -> runtime.getStartTime())
    out += ("jvm.uptime" -> runtime.getUptime())

    val os = ManagementFactory.getOperatingSystemMXBean()
    out += ("jvm.num_cpus" -> os.getAvailableProcessors().toLong)
    os match {
      case unix: com.sun.management.UnixOperatingSystemMXBean =>
        out += ("jvm.fd.count" -> unix.getOpenFileDescriptorCount)
        out += ("jvm.fd.limit" -> unix.getMaxFileDescriptorCount)
      case _ =>   // ew, Windows... or something
    }

    var postGCTotalUsage = 0L
    var currentTotalUsage = 0L
    ManagementFactory.getMemoryPoolMXBeans().asScala.foreach { pool =>
      val name = pool.getName.regexSub("""[^\w]""".r) { m => "." }
      Option(pool.getCollectionUsage).foreach { usage =>
        out += ("jvm.post_gc." + name + ".used" -> usage.getUsed)
        postGCTotalUsage += usage.getUsed
        out += ("jvm.post_gc." + name + ".max" -> usage.getMax)
      }
      Option(pool.getUsage) foreach { usage =>
        out += ("jvm.current_mem." + name + ".used" -> usage.getUsed)
        currentTotalUsage += usage.getUsed
        out += ("jvm.current_mem." + name + ".max" -> usage.getMax)
      }
    }
    out += ("jvm.post_gc.used" -> postGCTotalUsage)
    out += ("jvm.current_mem.used" -> currentTotalUsage)

    out.toMap
  }

  def getJvmCounters(): Map[String, Long] = {
    val out = mutable.Map[String, Long]()

    var totalCycles = 0L
    var totalTime = 0L

    ManagementFactory.getGarbageCollectorMXBeans().asScala.foreach { gc =>
      val name = gc.getName.regexSub("""[^\w]""".r) { m => "." }
      val collectionCount = gc.getCollectionCount
      out += ("jvm.gc." + name + ".cycles" -> collectionCount)
      val collectionTime = gc.getCollectionTime
      out += ("jvm.gc." + name + ".msec" -> collectionTime)
      // note, these could be -1 if the collector doesn't have support for it.
      if (collectionCount > 0)
        totalCycles += collectionCount
      if (collectionTime > 0)
        totalTime += gc.getCollectionTime
    }
    out += ("jvm.gc.cycles" -> totalCycles)
    out += ("jvm.gc.msec" -> totalTime)

    out.toMap
  }
}
