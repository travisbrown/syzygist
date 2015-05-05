package org.syzygist.parse

import org.parboiled2._
import org.scalacheck.{ Gen, Properties }
import org.scalacheck.Prop._
import scalaz._, Scalaz._

object DisjunctionDeliverySpec extends Properties("DisjunctionDelivery") {
  implicit val ParseErrorEqual: Equal[ParseError] = Equal.equalA

  class SimpleParser(val input: ParserInput) extends Parser {
    def Word: Rule1[String] = rule {
      capture(oneOrMore(CharPredicate.Alpha)) ~ zeroOrMore(' ')
    }

    def BadWord: Rule1[String] = rule {
      Word ~> ((s: String) => test(s.size == 3) ~ push(s.substring(4)))
    }

    def Words: Rule1[List[String]] = rule {
      oneOrMore(BadWord | Word) ~> ((_: Seq[String]).toList)
    }
  }

  property(" (matches results of DeliveryScheme.Either)") =
    forAll((input: String) => compareResults(input))

  property(" (matches results of DeliveryScheme.Either on canned inputs)") = forAll(
    Gen.oneOf("aaa bbbb ccccc", "a bb cccc", "1a 2b")
  )(input => compareResults(input))

  private def compareResults(input: String): Boolean = {
    val parser = new SimpleParser(input)

    val eitherResult = Validation.fromTryCatchNonFatal(
      parser.Words.run()(Parser.DeliveryScheme.Either)
    ).toValidationNel

    val disjunctionResult = Validation.fromTryCatchNonFatal(
      parser.Words.run()(DisjunctionDeliveryScheme)
    ).toValidationNel

    (eitherResult |@| disjunctionResult).tupled.fold(
      {
        case NonEmptyList(eitherError, disjunctionError) =>
          eitherError.getMessage === disjunctionError.getMessage
        case _ => false
      },
      Function.tupled(_ === _.toEither)
    )
  }
}
