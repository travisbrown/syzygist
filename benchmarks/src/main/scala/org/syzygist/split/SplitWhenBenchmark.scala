package org.syzygist.split

import org.openjdk.jmh.annotations._
import scalaz.concurrent.Task
import scalaz.std.anyVal._
import scalaz.stream.{ Process, process1 }

@State(Scope.Thread)
class SplitWhenBenchmark {
  import StreamFunctions._

  val size = 1000

  val streamS: Stream[Long] = Stream.iterate(1L)(_ + 1).take(size)
  val streamZ: Process[Task, Long] = Process.iterate(1L)(_ + 1).take(size)

  @Benchmark
  def splitWhen_stdlib(): Long = splitStream(streamS)(_ % 7 == 0).map(_.product).sum

  @Benchmark
  def splitWhen_syzygist(): Long = (
    streamZ |> splitWhen[Long](_ % 7 == 0)
  ).runFoldMap(_.product).run

  @Benchmark
  def splitWhen_scalaz(): Long = (
    streamZ |> process1.split[Long](_ % 7 == 0)
  ).runFoldMap(_.product).run
}
