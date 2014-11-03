package org.syzygist.parse

import org.parboiled2.ParserInput
import scala.reflect.macros.Context
import scalaz.concurrent.{ Future, Task }

object ParserMacros {
  /**
   * A convenience macro that allows us to create a parsing function without
   * running into Parboiled2's constraints on how parsers can be run.
   */
  def parseWithImpl[P <: ValueParser](c: Context)(implicit
    P: c.WeakTypeTag[P]
  ): c.Expr[String => Task[P#Value]] = {
    import c.universe._

    c.Expr[String => Task[P#Value]](
      Function(
        ValDef(
          Modifiers(Flag.PARAM),
          newTermName("input"),
          TypeTree(typeOf[String]),
          EmptyTree
        ) :: Nil,
        Apply(
          Select(
            New(
              Select(
                Select(
                  Select(Ident(nme.ROOTPKG), newTermName("scalaz")),
                  newTermName("concurrent")
                ),
                newTypeName("Task")
              )
            ),
            nme.CONSTRUCTOR
          ),
          Apply(
            Select(reify(Future).tree, newTermName("delay")),
            Block(
              ValDef(
                Modifiers(),
                newTermName("parser"),
                TypeTree(),
                Apply(
                  Select(New(TypeTree(P.tpe)), nme.CONSTRUCTOR),
                  Ident(newTermName("input")) :: Nil
                )
              ) :: Nil,
              Apply(
                Select(
                  Select(Ident(newTermName("parser")), newTermName("value")),
                  newTermName("run")
                ),
                Nil
              )
            ) :: Nil
          ) :: Nil
        )
      )
    )
  }
}
