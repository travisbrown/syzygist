package org.syzygist.split

object StreamFunctions {
  def splitStream[A](xs: Stream[A])(p: A => Boolean): Stream[Vector[A]] =
    xs.span(x => !p(x)) match {
      case (current, _ #:: rest) => current.toVector #:: splitStream(rest)(p)
      case (current, _) => current.toVector #:: Stream.empty
    }
}
