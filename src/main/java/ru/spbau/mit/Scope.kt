package ru.spbau.mit

import java.io.OutputStream


typealias DefinedVariable = Int
typealias Variable = DefinedVariable?
typealias Function = (List<DefinedVariable>) -> DefinedVariable

open class Scope(
        private val variables: MutableMap<String, Variable> = mutableMapOf(),
        private val functions: MutableMap<String, Function> = mutableMapOf()
) {
    fun hasVariable(name: String) = name in variables

    fun declareVariable(name: String) {
        if (hasVariable(name)) throw ContextException("Variable $name already in use.")
        variables[name] = null
    }

    fun setVariable(name: String, value: DefinedVariable) {
        variables[name] = value
    }

    fun getVariable(name: String): DefinedVariable {
        if (variables[name] == null) throw ContextException("Variable $name is't initialized.")
        return variables[name]!!
    }

    fun hasFunction(name: String) = name in functions

    fun defineFunction(name: String, body: Function) {
        if (hasFunction(name)) throw ContextException("Function $name already in use.")
        functions[name] = body
    }

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