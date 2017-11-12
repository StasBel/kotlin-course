package ru.spbau.mit

sealed class EvalException(
        message: String? = null,
        cause: Throwable? = null
) : RuntimeException(message, cause)

class ContextException(message: String) : EvalException(message)