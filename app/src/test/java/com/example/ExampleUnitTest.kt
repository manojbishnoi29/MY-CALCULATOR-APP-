package com.example

import com.example.engine.ExpressionEvaluator
import com.example.model.UnitCategory
import com.example.model.UnitDefinitions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  private val evaluator = ExpressionEvaluator()

  @Test
  fun testBasicArithmetic() {
    val res1 = evaluator.evaluate("2 + 2", isRadMode = false)
    assertTrue(res1 is ExpressionEvaluator.Result.Success)
    assertEquals("4", (res1 as ExpressionEvaluator.Result.Success).formatted)

    val res2 = evaluator.evaluate("10 - 3 * 2", isRadMode = false)
    assertTrue(res2 is ExpressionEvaluator.Result.Success)
    assertEquals("4", (res2 as ExpressionEvaluator.Result.Success).formatted)

    val res3 = evaluator.evaluate("(12 + 8) / 4", isRadMode = false)
    assertTrue(res3 is ExpressionEvaluator.Result.Success)
    assertEquals("5", (res3 as ExpressionEvaluator.Result.Success).formatted)
  }

  @Test
  fun testScientificEvaluation() {
    // Trigonometry in degree mode: sin(30) = 0.5
    val sin30 = evaluator.evaluate("sin(30)", isRadMode = false)
    assertTrue(sin30 is ExpressionEvaluator.Result.Success)
    assertEquals("0.5", (sin30 as ExpressionEvaluator.Result.Success).formatted)

    // Square root and power
    val sqrtRes = evaluator.evaluate("sqrt(144)", isRadMode = false)
    assertTrue(sqrtRes is ExpressionEvaluator.Result.Success)
    assertEquals("12", (sqrtRes as ExpressionEvaluator.Result.Success).formatted)

    val powRes = evaluator.evaluate("2^8", isRadMode = false)
    assertTrue(powRes is ExpressionEvaluator.Result.Success)
    assertEquals("256", (powRes as ExpressionEvaluator.Result.Success).formatted)
  }

  @Test
  fun testUnitConversions() {
    val lengthUnits = UnitDefinitions.unitsByCategory[UnitCategory.LENGTH]!!
    val meter = lengthUnits.first { it.symbol == "m" }
    val km = lengthUnits.first { it.symbol == "km" }

    val converted = UnitDefinitions.convert(1000.0, UnitCategory.LENGTH, meter, km)
    assertEquals(1.0, converted, 0.001)

    val tempUnits = UnitDefinitions.unitsByCategory[UnitCategory.TEMPERATURE]!!
    val celsius = tempUnits.first { it.symbol == "°C" }
    val fahrenheit = tempUnits.first { it.symbol == "°F" }

    val tempF = UnitDefinitions.convert(100.0, UnitCategory.TEMPERATURE, celsius, fahrenheit)
    assertEquals(212.0, tempF, 0.001)
  }
}
