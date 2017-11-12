package ru.spbau.mit

import java.util.*


class Context {
    private val scopes: Deque<Scope> = ArrayDeque<Scope>()

    private val currentScope: Scope
        get() = scopes.peek()

    fun addScope(scope: Scope? = null) = scopes.add(scope?.let { Scope() })

    fun removeScope() = scopes.pop()

    fun declareVariable(name: String) = currentScope.declareVariable(name)

    fun setVariable(name: String, value: DefinedVariable) {
        scopes.firstOrNull { it.hasVariable(name) }
                ?.setVariable(name, value)
                ?: throw ContextException("Variable $name is't declared.")
    }

    fun getVariable(name: String): DefinedVariable {
        return scopes.firstOrNull { it.hasVariable(name) }
                ?.getVariable(name)
                ?: throw ContextException("Variable $name is't declared.")
    }

    fun defineFunction(name: String, body: Function) {
        scopes.firstOrNull { it.hasFunction(name) }
                ?.defineFunction(name, body)
                ?: throw ContextException("Function $name already in use.")
    }

    fun getFunction(name: String): Function {
        return scopes.firstOrNull { it.hasFunction(name) }
                ?.getFunction(name)
                ?: throw ContextException("Function $name is't defined.")
    }
}