package ru.spbau.mit

sealed class LangError(message: String) : Error(message)

class NotImplementedError(message: String) : LangError(message)