package ru.spbau.mit

interface Inputer {
    fun input(): String
}

class StringInputer(private val code: String) : Inputer {
    override fun input(): String = code
}

interface Outputer {
    fun output(result: Int)
}

object ConsoleOutputer : Outputer {
    override fun output(result: Int) = println(result)
}

interface Runner {
    fun run(interpreter: Interpreter)
}

class SimpleRunner(private val inputer: Inputer, private val outputer: Outputer) : Runner {
    override fun run(interpreter: Interpreter) = outputer.output(interpreter.interpret(inputer.input()))
}