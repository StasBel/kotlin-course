package ru.spbau.mit

import org.antlr.v4.runtime.ParserRuleContext
import ru.spbau.mit.parser.FunBaseVisitor
import ru.spbau.mit.parser.FunParser
import java.io.OutputStream

class EvalVisitor(private val outputStream: OutputStream) : FunBaseVisitor<Int>() {
    private val context: Context = Context()

    override fun visitFile(ctx: FunParser.FileContext): Int = with(ctx) {
        block().eval(GlobalScope(outputStream))
    }

    override fun visitBlock(ctx: FunParser.BlockContext): Int {

    }

    private fun ParserRuleContext.eval(newScope: Scope? = null): Int {
        newScope?.let { context.addScope() }
        val returnValue = accept(this@EvalVisitor)
        newScope?.let { context.removeScope() }
        return returnValue
    }

    /*
    override fun visitProg(ctx: ProgContext): Int = this.visit(ctx.expr())

    override fun visitParentsExpr(ctx: ParentsExprContext): Int = this.visit(ctx.expr())

    override fun visitBinopExpr(ctx: BinopExprContext): Int {
        val left = this.visit(ctx.left)
        val op = ctx.op.text
        val right = this.visit(ctx.right)
        fun Boolean.asInt(): Int = if (this) 1 else 0
        return when (op) {
            "*" -> left * right
            "/" -> left / right
            "%" -> left % right
            "+" -> left + right
            "-" -> left - right
            "<" -> (left < right).asInt()
            "<=" -> (left <= right).asInt()
            ">" -> (left > right).asInt()
            ">=" -> (left >= right).asInt()
            "==" -> (left == right).asInt()
            "!=" -> (left != right).asInt()
            "&&" -> ((left != 0) && (right != 0)).asInt()
            "||" -> ((left != 0) || (right != 0)).asInt()
            else -> throw NotImplementedError("Forgot to implement $op operation.")
        }
    }

    override fun visitLiteralExpr(ctx: LiteralExprContext): Int = ctx.value.text.toInt()
    */
}