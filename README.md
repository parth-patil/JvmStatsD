JvmStatsD
=========

Periodically send JVM stats to StatsD server

Following is sample usage of the library

```scala
import akka.actor.ActorSystem
import scala.concurrent.ExecutionContext.Implicits.global

class SampleUsage {
  def main(args: Array[String]) {
    val actorSystem = ActorSystem("statsd-actor-system")
    val statsD = new StatsD(
      context = actorSystem,
      host = "localhost",
      port = 8192)

    val statsSender = new StatsSender(statsD)

    // Create the JvmStatsTask that publishes stats every 10 secs
    statsSender.addTask(new JvmStatsTask, 10)
  }
}
```
