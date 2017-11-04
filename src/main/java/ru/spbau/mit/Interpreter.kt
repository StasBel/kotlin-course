package ru.spbau.mit

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import ru.spbau.mit.parser.ExprLexer
import ru.spbau.mit.parser.ExprParser

interface Interpreter {
    fun interpret(code: String): Int
}

object SimpleInterpreter : Interpreter {
    override fun interpret(code: String): Int {
        val lexer = ExprLexer(CharStreams.fromString(code))
        val parser = ExprParser(BufferedTokenStream(lexer))
        return EvalVisitor().visit(parser.prog())
    }
}