package ru.spbau.mit

import org.antlr.v4.runtime.ParserRuleContext
import ru.spbau.mit.parser.FunBaseVisitor
import ru.spbau.mit.parser.FunParser
import java.io.OutputStream

class EvalVisitor(private val outputStream: OutputStream) : FunBaseVisitor<Int?>() {
    private val context: Context = Context()

    override fun visitFile(ctx: FunParser.FileContext): Int? = with(ctx) {
        return block().eval(GlobalScope(outputStream))
    }

    override fun visitBlock(ctx: FunParser.BlockContext): Int? = with(ctx) {
        statement().forEach {
            with(it) {
                val result = eval()
                if (result != null
                        && (whileBlock() != null || ifBlock() != null || returnBlock() != null)) {
                    return result
                }
            }
        }
        return null
    }

    override fun visitFunction(ctx: FunParser.FunctionContext): Int? = with(ctx) {
        context.defineFunction(name.text) { args ->
            val names = argumentNames().ID().map { it.text }
            val variables = mutableMapOf(*names.zip(args as List<Int?>).toTypedArray())
            val result = block().eval(Scope(variables = variables))
            result ?: 0
        }
        return null
    }

    override fun visitVariable(ctx: FunParser.VariableContext): Int? = with(ctx) {
        context.declareVariable(name.text)
        expression()?.let { context.setVariable(name.text, it.eval()!!) }
        return null
    }

    override fun visitWhileBlock(ctx: FunParser.WhileBlockContext): Int? = with(ctx) {
        fun condition() = condition.eval()!!.asBool()
        while (condition()) {
            val result = block().eval(Scope())
            if (result != null) return result
        }
        return null
    }

    override fun visitIfBlock(ctx: FunParser.IfBlockContext): Int? = with(ctx) {
        fun condition() = condition.eval()!!.asBool()
        return when {
            condition() -> trueBlock.eval(Scope())
            falseBlock != null -> falseBlock.eval(Scope())
            else -> null
        }
    }

    override fun visitAssignment(ctx: FunParser.AssignmentContext): Int? = with(ctx) {
        context.setVariable(name.text, expression().eval()!!)
        return null
    }

    override fun visitReturnBlock(ctx: FunParser.ReturnBlockContext): Int? = with(ctx) {
        return expression().eval()
    }

    override fun visitFuncCallExpr(ctx: FunParser.FuncCallExprContext): Int? = with(ctx) {
        val function = context.getFunction(name.text)
        val arguments = arguments().expression().map { it.eval()!! }
        return function(arguments)
    }

    override fun visitBinopExpr(ctx: FunParser.BinopExprContext): Int? {
        val left = ctx.left.eval()!!
        val op = ctx.op.text
        val right = ctx.right.eval()!!
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

    override fun visitLiteralExpr(ctx: FunParser.LiteralExprContext): Int? {
        return ctx.value.text.toInt()
    }

    override fun visitIDExpr(ctx: FunParser.IDExprContext): Int? {
        return context.getVariable(ctx.name.text)
    }

    override fun visitParentsExpr(ctx: FunParser.ParentsExprContext): Int? {
        return ctx.expression().eval()
    }

    private fun Int.asBool(): Boolean = this@asBool != 0

    private fun Boolean.asInt(): Int = if (this@asInt) 1 else 0

    private fun ParserRuleContext.eval(newScope: Scope? = null): Int? {
        newScope?.let { context.addScope(newScope) }
        val result = accept(this@EvalVisitor)
        newScope?.let { context.removeScope() }
        return result
    }
}