package org.syzygist.split

import org.scalatest.Assertions._
import org.scalatest.FunSuite

class SplitWhenBenchmarkSuite extends FunSuite {
  val correct = 2102695662045401264L
  val benchmark = new SplitWhenBenchmark

  import benchmark._

  test("Our implementation for the standard library's streams should produce the right result") {
    assert(splitWhen_stdlib() === correct)
  }

  test("Our splitWhen should produce the right result") {
    assert(splitWhen_syzygist() === correct)
  }

  test("scalaz-stream's split should produce the right result") {
    assert(splitWhen_scalaz() === correct)
  }
}
