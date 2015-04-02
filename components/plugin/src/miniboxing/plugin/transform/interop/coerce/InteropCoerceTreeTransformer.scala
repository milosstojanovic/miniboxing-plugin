//
//     _____   .__         .__ ___.                    .__ scala-miniboxing.org
//    /     \  |__|  ____  |__|\_ |__    ____  ___  ___|__|  ____     ____
//   /  \ /  \ |  | /    \ |  | | __ \  /  _ \ \  \/  /|  | /    \   / ___\
//  /    Y    \|  ||   |  \|  | | \_\ \(  <_> ) >    < |  ||   |  \ / /_/  >
//  \____|__  /|__||___|  /|__| |___  / \____/ /__/\_ \|__||___|  / \___  /
//          \/          \/          \/               \/         \/ /_____/
// Copyright (c) 2011-2015 Scala Team, École polytechnique fédérale de Lausanne
//
// Authors:
//    * Vlad Ureche
//
package miniboxing
package plugin
package transform
package interop
package coerce

import scala.tools.nsc.transform.InfoTransform
import scala.tools.nsc.transform.TypingTransformers
import scala.tools.nsc.transform.InfoTransform
import scala.tools.nsc.typechecker.Analyzer

trait InteropCoerceTreeTransformer extends InfoTransform with TypingTransformers with ScalacCrossCompilingLayer {
  self: InteropCoerceComponent =>

  import global._
  import definitions._
  import interop._

  override def transformInfo(sym: Symbol, tpe: Type): Type =
    tpe

  override def newTransformer(unit: CompilationUnit): Transformer = new Transformer {
    override def transform(tree: Tree) = tree
  }

  class CoercePhase(prev: scala.tools.nsc.Phase) extends StdPhase(prev) {
    override def name = InteropCoerceTreeTransformer.this.phaseName
    override def checkable = false
    def apply(unit: CompilationUnit): Unit = {
      val tree = afterInteropCoerce(new TreeAdapters().adapt(unit))
      tree.foreach(node => assert(node.isInstanceOf[Import] || node.tpe != null, node))
    }
  }

  class TreeAdapters extends TweakedAnalyzer {
    var indent = 0
    override lazy val global: self.global.type = self.global

    def adapt(unit: CompilationUnit): Tree = {
      val context = rootContext(unit)
      // turnOffErrorReporting(this)(context)
      val checker = new TreeAdapter(context)
      unit.body = checker.typed(unit.body)
      unit.body
    }

    override def newTyper(context: Context): Typer =
      new TreeAdapter(context)

    def adaptdbg(ind: Int, msg: => String): Unit = {
//       println("  " * ind + msg)
    }

    class TreeAdapter(context0: Context) extends TweakedTyper(context0) {
      override protected def finishMethodSynthesis(templ: Template, clazz: Symbol, context: Context): Template =
        templ

      def supertyped(tree: Tree, mode: Mode, pt: Type): Tree =
        super.typed(tree, mode, pt)

      override protected def adapt(tree: Tree, mode: Mode, pt: Type, original: Tree = EmptyTree): Tree = {
        val oldTpe = tree.tpe
        val newTpe = pt

        def superAdapt =
          if (oldTpe <:< newTpe)
            tree
          else
            if (flag_strict_typechecking)
              super.adapt(tree, mode, pt, original)
            else
              tree.setType(newTpe)

        if (tree.isTerm) {
          if ((oldTpe.isMbFunction ^ newTpe.isMbFunction) && (!newTpe.isWildcard)) {
            val conversion = if (oldTpe.isMbFunction) marker_mbfun2fun else marker_fun2mbfun
            val tpe =
              if (oldTpe.isMbFunction)
                oldTpe.dealiasWiden.withoutMbFunction
              else
                newTpe.dealiasWiden.withoutMbFunction
            val tree1 = gen.mkMethodCall(conversion, List(tpe), List(tree))
            val tree2 = super.typed(tree1, mode, pt)
            assert(tree2.tpe != ErrorType, tree2)
            // super.adapt is automatically executed when calling super.typed
            tree2
          } else if (oldTpe.isMbFunction && (oldTpe.isMbFunction == newTpe.isMbFunction) && !(oldTpe <:< newTpe)) {
            // workaround the isSubType issue with singleton types
            // and annotated types (see mb_erasure_torture10.scala)
            tree.setType(newTpe)
            tree
          } else
            superAdapt
        } else
          superAdapt
      }

      case object AlreadyTyped
      implicit class WithAlreadyTyped(val tree: Tree) {
        def withTypedAnnot: Tree = tree.updateAttachment[AlreadyTyped.type](AlreadyTyped)
      }

      override def typed(tree: Tree, mode: Mode, pt: Type): Tree = {
        val ind = indent
        indent += 1
        adaptdbg(ind, " <== " + tree + ": " + showRaw(pt, true, true, false, false) + "  now: " + tree.tpe + "   " + tree.pos)

        if (tree.hasAttachment[AlreadyTyped.type] && (pt == WildcardType) && tree.tpe != null)
          return tree

        val res = tree match {
          case EmptyTree | TypeTree() =>
            super.typed(tree, mode, pt)

          case Apply(outer, List()) if (outer.hasSymbolField && outer.symbol.name.decoded == "<outer>") =>
            super.typed(outer, mode, pt)

          case Select(qual, meth) if qual.isTerm && tree.symbol.isMethod =>
            val qual2 = super.typedQualifier(qual.setType(null), mode, WildcardType).withTypedAnnot

            if (qual2.isMbFunction) {
              val tpe2 = if (qual2.tpe.hasAnnotation(mbFunctionClass)) qual2.tpe else qual2.tpe.widen
              val tpe3 = tpe2.removeAnnotation(mbFunctionClass)
              val qual3 =  gen.mkMethodCall(gen.mkAttributedRef(marker_mbfun2fun), List(tpe3), List(qual2))
              super.typed(Select(qual3, meth) setSymbol tree.symbol, mode, pt)
            } else {
              tree.setType(null)
              super.typed(tree, mode, pt)
            }

          case _ =>
            tree.setType(null)
            super.typed(tree, mode, pt)
        }

        adaptdbg(ind, " ==> " + res + ": " + res.tpe)
//        if (res.tpe == ErrorType)
//          adaptdbg(ind, "ERRORS: " + context.errBuffer)
        indent -= 1
        res
      }
    }
  }
}
