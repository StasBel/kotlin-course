package ru.spbau.mit

import org.junit.Test
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals

class TestExpression {
    private fun assertEqualsExpr(
            expected: String,
            source: String,
            message: String? = null
    ) {
        val inputStream = "println($source)".byteInputStream()
        val outputStream = ByteArrayOutputStream()
        interpret(inputStream, outputStream)
        assertEquals(expected + "\n", outputStream.toString(), message)
    }

    @Test
    fun testPriorities() {
        assertEqualsExpr("0", "1 + (-1)", "Unary minus with sum")
        assertEqualsExpr("0", "-1 + 1", "Unary minus with sum")
        assertEqualsExpr("0", "2 - 2", "Simple subtracting")
        assertEqualsExpr("6", "2 * 2 + 2", "Sum and mul priorities")
        assertEqualsExpr("6", "2 + 2 + 2", "Sum chain")
        assertEqualsExpr("2", "2 / 2 * 2 / 2 * 2", "Divide and mul chain")
        assertEqualsExpr("0", "2 && 0", "And with true and false")
        assertEqualsExpr("0", "-2 && 0", "And with true and false")
        assertEqualsExpr("1", "-2 && 2", "And with true and false")
        assertEqualsExpr("0", "0 && (-0)", "And with true and false")
        assertEqualsExpr("0", "-1 - -1", "A tricky one with minus")
        assertEqualsExpr("1", "2 || 0", "Or with true and false")
        assertEqualsExpr("1", "-2 || 0", "Or with true and false")
        assertEqualsExpr("1", "-2 || 2", "Or with true and false")
        assertEqualsExpr("0", "(10 - 10) || 0", "Or with true and false")
        assertEqualsExpr("0", "2 * 9 && 10 - 10", "And/ops priorities")
        assertEqualsExpr("1", "2 * 9 || 10 - 10", "Or/ops priorities")
        assertEqualsExpr("0\n0", "println(0) && 1", "Builtin return 0")
        assertEqualsExpr("153", "(4 + 5) * (8 + 9)", "Parentheses")
        assertEqualsExpr("1", "-29 != 29", "Neq simple")
        assertEqualsExpr("0", "29 != 29", "Neq simple")
        assertEqualsExpr("1", "(10 <= 11) && (11 <= 12)", "Intervals")
        assertEqualsExpr("1", "(10 <= 11) || (12 <= 11)", "Intervals")
        assertEqualsExpr("1", "10 % 3 == 1", "Mod with eq")
        assertEqualsExpr("0", "10 % 3 < 1", "Mod with less")
        assertEqualsExpr("0", "10 % 3 > 1", "Mod with greater")
        assertEqualsExpr("1", "13 % 4 >= 1", "Mod with greater")
        assertEqualsExpr("1", "21 % 2 <= 1", "Mod with greater")
        assertEqualsExpr("1", "-10 < 10", "Negative less then positive")
    }
}
