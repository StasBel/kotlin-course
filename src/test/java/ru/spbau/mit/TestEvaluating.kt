package ru.spbau.mit

import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import kotlin.test.assertEquals

class TestEvaluating {
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

    private fun assertEqualsFile(
            expected: String,
            sourcePath: String,
            message: String? = null
    ) {
        val inputStream = FileInputStream(sourcePath)
        val outputStream = ByteArrayOutputStream()
        interpret(inputStream, outputStream)
        assertEquals(expected, outputStream.toString(), message)
    }

    @Test
    fun testExamples() {
        assertEqualsFile("0\n", "src/test/resources/example1.fun")
        assertEqualsFile("1 1\n2 1\n3 2\n4 3\n5 5\n", "src/test/resources/example2.fun")
        assertEqualsFile("42\n", "src/test/resources/example3.fun")
    }

    @Test
    fun testClosure() {
        val source = """
            |var x = 17
            |fun foo() {
            |   x = 89
            |}
            |foo()
            |println(x)
            """.trimMargin()
        assertEqualsStr("89\n", source, "Global assignment from func")
    }

    @Test
    fun testShadowing() {
        val source = """
            |fun foo() {
            |   fun foo() {
            |       return 1
            |   }
            |   return foo()
            |}
            |println(foo())
            """.trimMargin()
        assertEqualsStr("1\n", source, "Local function renaming")
    }

    @Test(expected = ContextException::class)
    fun testRedefinition() {
        val source = """
            |fun foo(x) {
            |   return 10
            |}
            |fun foo(y) {
            |   return 11
            |}
            |println(foo())
            """.trimMargin()
        assertEqualsStr("", source)
    }

    @Test
    fun testMultipleArguments() {
        val source = """
            |fun approx_number_seconds(y, m, d, h, s) {
            |   var days = 30 * (12 * y + m) + d
            |   var hours = 24 * days + h
            |   return hours * 60 + s
            |}
            |println(approx_number_seconds(10, 16, 8, 100, 10000))
            """.trimMargin()
        assertEqualsStr("5902720\n", source, "Testing passing multiple args")
    }
}