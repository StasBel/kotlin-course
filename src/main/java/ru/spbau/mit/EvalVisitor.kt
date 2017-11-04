package ru.spbau.mit

import ru.spbau.mit.parser.ExprBaseVisitor
import ru.spbau.mit.parser.ExprParser.*

class EvalVisitor : ExprBaseVisitor<Int>() {
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
}