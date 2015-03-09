package org.syzygist.split

import org.scalatest.Assertions._
import org.scalatest.FunSuite

class SplitOnBenchmarkSuite extends FunSuite {
  val correct = 4353823357344L
  val benchmark = new SplitOnBenchmark

  import benchmark._

  test("Our implementation for the standard library's streams should produce the right result") {
    assert(splitOn_stdlib() === correct)
  }

  test("Our splitOn should produce the right result") {
    assert(splitOn_syzygist() === correct)
  }

  test("scalaz-stream's splitOn should produce the right result") {
    assert(splitOn_scalaz() === correct)
  }
}
