package ru.spbau.mit

import org.antlr.v4.runtime.misc.ParseCancellationException
import org.junit.Test
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals

class TestParsing {
    private fun assertEqualsStr(
            expected: String,
            source: String,
            message: String? = null
    ) {
        val inputStream = source.byteInputStream()
        val outputStream = ByteArrayOutputStream()
        interpret(inputStream, outputStream)
        assertEquals(expected, outputStream.toString(), message)
    }

    @Test(expected = ParseCancellationException::class)
    fun testBracketMatching() {
        val source = """
            |var x = 10
            |var y = 100
            |if (x < y) {
            |   println(x)
            |else {
            |   println(y)
            |}
            """.trimMargin()
        assertEqualsStr("", source, "True block don't have closing bracket")
    }

    @Test(expected = ParseCancellationException::class)
    fun testPalenessMatching() {
        val source = """
            |var x = 100
            |var y = 100
            |while (x == y {
            |   println(x + y)
            |   x = x + y
            |}
            """.trimMargin()
        assertEqualsStr("", source, "While cond don't have closing bracket")
    }

    @Test
    fun testCorrectExample() {
        val source = """
            |fun foo(x) {
            |   println(x * 10)
            |   return x % 10
            |}
            |var y = 19
            |y = foo(y)
            |var i
            |if (y == 9) {
            |   i = 10
            |}
            |// cycle while i <= 20, each time multiply
            |// i by 2
            |// this is commentary btw
            |while (i <= 10) {
            |   i = i * 2
            |   println(i)
            |}
            """.trimMargin()
        assertEqualsStr("190\n20\n", source, "Example of correct control flow")
    }
}