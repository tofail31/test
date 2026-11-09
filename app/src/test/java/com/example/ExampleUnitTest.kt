package com.example

import com.example.calculator.engine.CalculatorEngine
import com.example.calculator.model.UnitCategory
import com.example.calculator.model.UnitConverterData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun calculator_basicArithmetic() {
    val engine = CalculatorEngine()
    val res1 = engine.evaluate("15 + 27 × 2")
    assertTrue(res1.isSuccess)
    assertEquals(69.0, res1.getOrThrow(), 0.0001)

    val res2 = engine.evaluate("(100 - 20) ÷ 4")
    assertTrue(res2.isSuccess)
    assertEquals(20.0, res2.getOrThrow(), 0.0001)
  }

  @Test
  fun calculator_scientificFunctions() {
    val engine = CalculatorEngine()
    engine.angleMode = CalculatorEngine.AngleMode.DEGREE

    val sin90 = engine.evaluate("sin(90)")
    assertTrue(sin90.isSuccess)
    assertEquals(1.0, sin90.getOrThrow(), 0.0001)

    val sqrt16 = engine.evaluate("√(16)")
    assertTrue(sqrt16.isSuccess)
    assertEquals(4.0, sqrt16.getOrThrow(), 0.0001)
  }

  @Test
  fun unitConverter_lengthTest() {
    val meters = UnitConverterData.categories[UnitCategory.LENGTH]!!.first { it.id == "m" }
    val kilometers = UnitConverterData.categories[UnitCategory.LENGTH]!!.first { it.id == "km" }

    val converted = UnitConverterData.convert(2500.0, meters, kilometers)
    assertEquals(2.5, converted, 0.0001)
  }
}
