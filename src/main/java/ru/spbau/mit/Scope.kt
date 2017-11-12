package ru.spbau.mit

import java.io.OutputStream


typealias DefinedVariable = Int
typealias Variable = DefinedVariable?
typealias Function = (List<Int>) -> Int

open class Scope(
        private val variables: MutableMap<String, Variable> = mutableMapOf(),
        private val functions: MutableMap<String, Function> = mutableMapOf()
) {
    fun declareVariable(name: String) {
        if (name in variables) throw ContextException("Variable $name already in use.")
        variables[name] = null
    }

    fun setVariable(name: String, value: DefinedVariable) {
        variables[name] = value
    }

    fun hasVariable(name: String) = name in variables

    fun getVariable(name: String): DefinedVariable {
        if (variables[name] == null) throw ContextException("Variable $name is't initialized.")
        return variables[name]!!
    }

    fun defineFunction(name: String, body: Function) {
        functions[name] = body
    }

    fun hasFunction(name: String) = name in functions

    fun getFunction(name: String): Function {
        return functions[name]!!
    }
}

private fun makePrintln(outputStream: OutputStream): Function {
    return object : Function {
        override fun invoke(args: List<Int>): Int {
            outputStream.write(args.joinToString(separator = " ", postfix = "\n").toByteArray())
            outputStream.flush()
            return 0
        }
    }
}

class GlobalScope(
        outputStream: OutputStream
) : Scope(functions = mutableMapOf("println" to makePrintln(outputStream)))