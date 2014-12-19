# Syzygist [![Build status](https://img.shields.io/travis/travisbrown/syzygist/master.svg)](http://travis-ci.org/travisbrown/syzygist) [![Coverage status](https://img.shields.io/coveralls/travisbrown/syzygist/master.svg)](https://coveralls.io/r/travisbrown/syzygist?branch=master)

This is a small set of utilities for working with
[Scalaz streams](https://github.com/scalaz/scalaz-stream).
The `split` package is a kind of port of Haskell's
[`Data.List.Split`](http://hackage.haskell.org/package/split-0.1.1/docs/Data-List-Split.html),
and `parse` provides some tools for using
[`parboiled2`](http://hackage.haskell.org/package/split-0.1.1/docs/Data-List-Split.html)
in the context of Scalaz streams.

`split` is reasonably well documented and tested. `parse` isn't.

## Example usage

The [Penn Treebank](http://www.cis.upenn.edu/~treebank/) includes several
thousand files containing parsed sentences with the following format:

```
( (S 
    (NP-SBJ (DT The) 
      (ADJP (RBS most) (JJ troublesome) )
      (NN report) )
    (VP (MD may) 
      (VP (VB be) 
        (NP-PRD 
          (NP (DT the) (NNP August) (NN merchandise) (NN trade) (NN deficit) )
          (ADJP (JJ due) 
            (ADVP (IN out) )
            (NP-TMP (NN tomorrow) )))))
    (. .) ))
```

We can write a simple _s_-expression parser with `parboiled2`:

``` scala
import org.parboiled2._
import org.syzygist.parse._
import scalaz.Tree

class SentenceParser(input: ParserInput) extends ValueParser(input) {
  type Value = Tree[String]

  def value: Rule1[Tree[String]] = rule {
    Whitespace ~ OpenBracket ~ Node ~ CloseBracket
  }

  def Whitespace: Rule0 = rule { zeroOrMore(anyOf(" \t\n")) }
  def OpenBracket: Rule0 = rule { '(' ~ Whitespace }
  def CloseBracket: Rule0 = rule { ')' ~ Whitespace }

  def Terminal: Rule1[String] = rule {
    capture(oneOrMore(noneOf(" ()"))) ~ Whitespace
  }

  def Node: Rule1[Tree[String]] = rule {
    OpenBracket ~ (Branch | Leaf) ~ CloseBracket
  }

  def Branch: Rule1[Tree[String]] = rule {
    Terminal ~ oneOrMore(Node) ~> (
      (tag: String, nodes: Seq[Tree[String]]) => Tree.node(tag, nodes.toStream)
    )
  }

  def Leaf: Rule1[Tree[String]] = rule {
    Terminal ~ Terminal ~> (
      (tag: String, word: String) => Tree.node(tag, Stream(Tree.leaf(word)))
    )
  }
}
```

This parser accepts individual sentences, but we want to perform streaming
processing on thousands of files, each of which may contain many sentences. The
`split` package's `whenElt` and `parse`'s `parseWith` make this easy:

``` scala
import org.syzygist.split.Splitter.whenElt
import scalaz.concurrent.Task
import scalaz.stream._

val sentenceSplitter = whenElt[String](_.startsWith("(")).keepDelimsL.split

def parseFile(file: String): Process[Task, Tree[String]] =
  io.linesR(file)
    .pipe(sentenceSplitter)
    .map(_.mkString).filter(_.nonEmpty).evalMap(parseWith[SentenceParser])

val sentences = parseFile("penn-treebank-rel3/parsed/mrg/wsj/24/wsj_2400.mrg")
```
