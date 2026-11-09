package com.example.calculator.engine

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.*

class CalculatorEngine {

  enum class AngleMode {
    DEGREE,
    RADIAN
  }

  var angleMode: AngleMode = AngleMode.DEGREE

  private val decimalFormat = DecimalFormat("#,##0.########", DecimalFormatSymbols(Locale.US)).apply {
    isGroupingUsed = true
    maximumFractionDigits = 8
  }

  /**
   * Evaluates a mathematical expression string.
   * Returns a Result containing the formatted result or an error message.
   */
  fun evaluate(rawExpr: String): Result<Double> {
    val clean = sanitize(rawExpr)
    if (clean.isBlank()) return Result.failure(IllegalArgumentException("Empty expression"))

    return try {
      val parser = Parser(clean, angleMode)
      val value = parser.parse()
      if (value.isNaN()) {
        Result.failure(ArithmeticException("Undefined"))
      } else if (value.isInfinite()) {
        Result.failure(ArithmeticException("Cannot divide by zero"))
      } else {
        Result.success(value)
      }
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  fun formatResult(value: Double): String {
    if (value.isNaN()) return "Undefined"
    if (value.isInfinite()) return if (value > 0) "Infinity" else "-Infinity"
    // Handle integer values cleanly
    return if (abs(value - round(value)) < 1e-10 && abs(value) < 1e14) {
      val df = DecimalFormat("#,##0", DecimalFormatSymbols(Locale.US))
      df.format(round(value).toLong())
    } else if (abs(value) >= 1e11 || (abs(value) > 0 && abs(value) < 1e-6)) {
      String.format(Locale.US, "%.6e", value)
    } else {
      decimalFormat.format(value)
    }
  }

  private fun sanitize(expr: String): String {
    return expr
      .replace("×", "*")
      .replace("÷", "/")
      .replace("−", "-")
      .replace("π", Math.PI.toString())
      .replace("e", Math.E.toString())
  }

  /**
   * Recursive descent parser for arithmetic & scientific expressions.
   */
  private class Parser(private val input: String, private val angleMode: AngleMode) {
    private var pos = -1
    private var ch = ' '

    init {
      nextChar()
    }

    private fun nextChar() {
      pos++
      ch = if (pos < input.length) input[pos] else '\u0000'
    }

    private fun eat(charToEat: Char): Boolean {
      while (ch == ' ') nextChar()
      if (ch == charToEat) {
        nextChar()
        return true
      }
      return false
    }

    fun parse(): Double {
      val x = parseExpression()
      while (ch == ' ') nextChar()
      if (ch == ')') nextChar()
      return x
    }

    private fun parseExpression(): Double {
      var x = parseTerm()
      while (true) {
        when {
          eat('+') -> x += parseTerm()
          eat('-') -> x -= parseTerm()
          else -> return x
        }
      }
    }

    private fun parseTerm(): Double {
      var x = parseFactor()
      while (true) {
        when {
          eat('*') -> x *= parseFactor()
          eat('/') -> {
            val divisor = parseFactor()
            if (divisor == 0.0) return Double.POSITIVE_INFINITY
            x /= divisor
          }
          eat('%') -> {
            x %= parseFactor()
          }
          else -> return x
        }
      }
    }

    private fun parseFactor(): Double {
      while (ch == ' ') nextChar()
      if (eat('+')) return +parseFactor()
      if (eat('-')) return -parseFactor()

      var x: Double
      val startPos = pos
      if (eat('(')) {
        x = parseExpression()
        eat(')')
      } else if ((ch in '0'..'9') || ch == '.') {
        while ((ch in '0'..'9') || ch == '.') nextChar()
        val numStr = input.substring(startPos, pos)
        x = numStr.toDoubleOrNull() ?: 0.0
      } else if (ch in 'a'..'z' || ch in 'A'..'Z' || ch == '√' || ch == '∛') {
        while (ch in 'a'..'z' || ch in 'A'..'Z' || ch == '√' || ch == '∛') nextChar()
        val func = input.substring(startPos, pos)
        val arg = parseFactor()
        x = applyFunction(func, arg)
      } else {
        x = 0.0
      }

      if (eat('^')) {
        x = x.pow(parseFactor())
      }
      if (eat('!')) {
        x = factorial(x)
      }

      return x
    }

    private fun applyFunction(func: String, arg: Double): Double {
      return when (func.lowercase(Locale.US)) {
        "sqrt", "√" -> if (arg < 0) Double.NaN else sqrt(arg)
        "cbrt", "∛" -> cbrt(arg)
        "sin" -> {
          val rad = if (angleMode == AngleMode.DEGREE) Math.toRadians(arg) else arg
          sin(rad)
        }
        "cos" -> {
          val rad = if (angleMode == AngleMode.DEGREE) Math.toRadians(arg) else arg
          cos(rad)
        }
        "tan" -> {
          val rad = if (angleMode == AngleMode.DEGREE) Math.toRadians(arg) else arg
          tan(rad)
        }
        "asin" -> {
          val rad = asin(arg)
          if (angleMode == AngleMode.DEGREE) Math.toDegrees(rad) else rad
        }
        "acos" -> {
          val rad = acos(arg)
          if (angleMode == AngleMode.DEGREE) Math.toDegrees(rad) else rad
        }
        "atan" -> {
          val rad = atan(arg)
          if (angleMode == AngleMode.DEGREE) Math.toDegrees(rad) else rad
        }
        "ln" -> if (arg <= 0) Double.NaN else ln(arg)
        "log", "log10" -> if (arg <= 0) Double.NaN else log10(arg)
        "abs" -> abs(arg)
        else -> arg
      }
    }

    private fun factorial(n: Double): Double {
      if (n < 0 || n != floor(n) || n > 170) return Double.NaN
      var result = 1.0
      val intN = n.toInt()
      for (i in 2..intN) {
        result *= i
      }
      return result
    }
  }
}
