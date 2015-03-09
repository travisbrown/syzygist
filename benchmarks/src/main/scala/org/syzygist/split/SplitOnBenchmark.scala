package org.syzygist.split

import org.openjdk.jmh.annotations._
import scalaz.concurrent.Task
import scalaz.std.anyVal._
import scalaz.stream.{ Process, process1 }
import scalaz.syntax.equal._

@State(Scope.Thread)
class SplitOnBenchmark {
  import StreamFunctions._

  val size = 100000

  val streamS: Stream[Long] = Stream.continually(13).flatMap(1L to _).take(size)
  val streamZ: Process[Task, Long] =
    Process.constant(13).flatMap(i => Process.emitAll(1L to i)).take(size)

  @Benchmark
  def splitOn_stdlib(): Long = splitStream(streamS)(_ === 11L).map(_.product).sum

  @Benchmark
  def splitOn_syzygist(): Long = (streamZ |> splitOn(Vector(11L))).runFoldMap(_.product).run

  @Benchmark
  def splitOn_scalaz(): Long = (streamZ |> process1.splitOn(11L)).runFoldMap(_.product).run
}
