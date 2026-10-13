package com.example.engine

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.*

class ExpressionEvaluator {

    sealed class Result {
        data class Success(val value: Double, val formatted: String) : Result()
        data class Error(val message: String) : Result()
    }

    fun evaluate(rawExpr: String, isRadMode: Boolean): Result {
        if (rawExpr.isBlank()) return Result.Error("Empty expression")

        try {
            val normalized = prepareExpression(rawExpr)
            val parser = Parser(normalized, isRadMode)
            val value = parser.parse()

            if (value.isNaN()) {
                return Result.Error("Undefined result")
            }
            if (value.isInfinite()) {
                return Result.Error("Cannot divide by zero")
            }

            // Clean up near-zero float inaccuracies (e.g., sin(180 deg))
            val cleanValue = if (abs(value) < 1e-13) 0.0 else value
            val formatted = formatResult(cleanValue)

            return Result.Success(cleanValue, formatted)
        } catch (e: ArithmeticException) {
            return Result.Error(e.message ?: "Math error")
        } catch (e: IllegalArgumentException) {
            return Result.Error(e.message ?: "Invalid syntax")
        } catch (e: Exception) {
            return Result.Error("Invalid expression")
        }
    }

    private fun prepareExpression(raw: String): String {
        var expr = raw.replace(" ", "")
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", "PI")
            .replace("φ", "PHI")
            .replace("√", "sqrt")

        // Auto-insert multiplication: e.g. 2(3) -> 2*(3), 3PI -> 3*PI, )2 -> )*2
        val regexImplicit = Regex("(\\d|PI|PHI|e|\\))(?=[(a-zA-Z])")
        expr = regexImplicit.replace(expr) { match ->
            val char = match.value
            // If next token is part of function or paren, insert '*'
            "$char*"
        }

        // Close unbalanced parentheses
        val openCount = expr.count { it == '(' }
        val closeCount = expr.count { it == ')' }
        if (openCount > closeCount) {
            expr += ")".repeat(openCount - closeCount)
        }

        return expr
    }

    fun formatResult(value: Double): String {
        if (value.isNaN()) return "NaN"
        if (value == Double.POSITIVE_INFINITY) return "∞"
        if (value == Double.NEGATIVE_INFINITY) return "-∞"

        val absVal = abs(value)
        if (absVal != 0.0 && (absVal >= 1e12 || absVal < 1e-7)) {
            val symbols = DecimalFormatSymbols(Locale.US)
            val df = DecimalFormat("0.######E0", symbols)
            return df.format(value).replace("E", "e")
        }

        // Standard decimal formatting
        val bd = try {
            BigDecimal.valueOf(value).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros()
        } catch (e: Exception) {
            BigDecimal.valueOf(value)
        }
        val plain = bd.toPlainString()
        return if (plain == "-0") "0" else plain
    }

    private class Parser(private val input: String, private val isRadMode: Boolean) {
        private var pos = 0
        private val len = input.length

        fun parse(): Double {
            val result = parseExpression()
            if (pos < len) {
                throw IllegalArgumentException("Unexpected character at end: ${input[pos]}")
            }
            return result
        }

        // Expression: Addition & Subtraction
        private fun parseExpression(): Double {
            var value = parseTerm()
            while (pos < len) {
                val c = input[pos]
                if (c == '+') {
                    pos++
                    value += parseTerm()
                } else if (c == '-') {
                    pos++
                    value -= parseTerm()
                } else {
                    break
                }
            }
            return value
        }

        // Term: Multiplication, Division, Modulo
        private fun parseTerm(): Double {
            var value = parsePower()
            while (pos < len) {
                val c = input[pos]
                if (c == '*') {
                    pos++
                    value *= parsePower()
                } else if (c == '/') {
                    pos++
                    val divisor = parsePower()
                    if (divisor == 0.0) throw ArithmeticException("Cannot divide by zero")
                    value /= divisor
                } else if (c == '%') {
                    pos++
                    // Percentage behavior: if followed by another term, it could be modulus or percentage
                    value = value / 100.0
                } else {
                    break
                }
            }
            return value
        }

        // Power: Exponentiation (right-associative)
        private fun parsePower(): Double {
            var base = parsePostfix()
            if (pos < len && input[pos] == '^') {
                pos++
                val exponent = parsePower() // right-associative recursion
                base = base.pow(exponent)
            }
            return base
        }

        // Postfix: Factorials and percent signs
        private fun parsePostfix(): Double {
            var value = parseUnary()
            while (pos < len) {
                if (input[pos] == '!') {
                    pos++
                    value = factorial(value)
                } else {
                    break
                }
            }
            return value
        }

        // Unary: + or - prefix
        private fun parseUnary(): Double {
            if (pos < len) {
                if (input[pos] == '+') {
                    pos++
                    return parseUnary()
                }
                if (input[pos] == '-') {
                    pos++
                    return -parseUnary()
                }
            }
            return parsePrimary()
        }

        // Primary: numbers, parentheses, functions, constants
        private fun parsePrimary(): Double {
            if (pos >= len) throw IllegalArgumentException("Unexpected end of expression")

            val c = input[pos]

            // Parentheses
            if (c == '(') {
                pos++
                val result = parseExpression()
                if (pos < len && input[pos] == ')') {
                    pos++
                }
                return result
            }

            // Numbers
            if (c.isDigit() || c == '.') {
                val start = pos
                while (pos < len && (input[pos].isDigit() || input[pos] == '.')) {
                    pos++
                }
                val numStr = input.substring(start, pos)
                return numStr.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number: $numStr")
            }

            // Identifiers: functions & constants
            if (c.isLetter()) {
                val start = pos
                while (pos < len && input[pos].isLetter()) {
                    pos++
                }
                val name = input.substring(start, pos)

                // Constants
                when (name) {
                    "PI" -> return Math.PI
                    "PHI" -> return 1.618033988749895
                    "e" -> Math.E
                }
                if (name == "e") return Math.E

                // Functions require '('
                if (pos < len && input[pos] == '(') {
                    pos++
                    val arg = parseExpression()
                    if (pos < len && input[pos] == ')') {
                        pos++
                    }
                    return evaluateFunction(name, arg)
                } else {
                    throw IllegalArgumentException("Expected '(' after function $name")
                }
            }

            throw IllegalArgumentException("Unexpected character: $c")
        }

        private fun evaluateFunction(func: String, arg: Double): Double {
            return when (func.lowercase(Locale.US)) {
                "sin" -> {
                    val rad = if (isRadMode) arg else Math.toRadians(arg)
                    sin(rad)
                }
                "cos" -> {
                    val rad = if (isRadMode) arg else Math.toRadians(arg)
                    cos(rad)
                }
                "tan" -> {
                    val rad = if (isRadMode) arg else Math.toRadians(arg)
                    val cosVal = cos(rad)
                    if (abs(cosVal) < 1e-15) throw ArithmeticException("Tangent undefined")
                    tan(rad)
                }
                "asin" -> {
                    if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Domain error")
                    val rad = asin(arg)
                    if (isRadMode) rad else Math.toDegrees(rad)
                }
                "acos" -> {
                    if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Domain error")
                    val rad = acos(arg)
                    if (isRadMode) rad else Math.toDegrees(rad)
                }
                "atan" -> {
                    val rad = atan(arg)
                    if (isRadMode) rad else Math.toDegrees(rad)
                }
                "ln" -> {
                    if (arg <= 0.0) throw ArithmeticException("Natural log non-positive")
                    ln(arg)
                }
                "log" -> {
                    if (arg <= 0.0) throw ArithmeticException("Log non-positive")
                    log10(arg)
                }
                "sqrt" -> {
                    if (arg < 0.0) throw ArithmeticException("Negative square root")
                    sqrt(arg)
                }
                "cbrt" -> cbrt(arg)
                "abs" -> abs(arg)
                "inv" -> {
                    if (arg == 0.0) throw ArithmeticException("Cannot divide by zero")
                    1.0 / arg
                }
                else -> throw IllegalArgumentException("Unknown function: $func")
            }
        }

        private fun factorial(n: Double): Double {
            if (n < 0 || n != floor(n)) throw ArithmeticException("Factorial requires non-negative integer")
            if (n > 170) throw ArithmeticException("Factorial overflow")
            var res = 1.0
            val intN = n.toInt()
            for (i in 2..intN) {
                res *= i
            }
            return res
        }
    }
}
