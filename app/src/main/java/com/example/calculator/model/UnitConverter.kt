package com.example.calculator.model

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

enum class UnitCategory(val displayName: String) {
  LENGTH("Length"),
  WEIGHT("Weight & Mass"),
  TEMPERATURE("Temperature"),
  AREA("Area"),
  VOLUME("Volume"),
  SPEED("Speed"),
  DATA("Digital Storage")
}

data class ConversionUnit(
  val id: String,
  val name: String,
  val symbol: String,
  val toBase: (Double) -> Double,
  val fromBase: (Double) -> Double
)

object UnitConverterData {

  private val df = DecimalFormat("#,##0.######", DecimalFormatSymbols(Locale.US)).apply {
    maximumFractionDigits = 6
  }

  val categories: Map<UnitCategory, List<ConversionUnit>> = mapOf(
    UnitCategory.LENGTH to listOf(
      ConversionUnit("m", "Meter", "m", { it }, { it }),
      ConversionUnit("km", "Kilometer", "km", { it * 1000.0 }, { it / 1000.0 }),
      ConversionUnit("cm", "Centimeter", "cm", { it / 100.0 }, { it * 100.0 }),
      ConversionUnit("mm", "Millimeter", "mm", { it / 1000.0 }, { it * 1000.0 }),
      ConversionUnit("mi", "Mile", "mi", { it * 1609.344 }, { it / 1609.344 }),
      ConversionUnit("yd", "Yard", "yd", { it * 0.9144 }, { it / 0.9144 }),
      ConversionUnit("ft", "Foot", "ft", { it * 0.3048 }, { it / 0.3048 }),
      ConversionUnit("in", "Inch", "in", { it * 0.0254 }, { it / 0.0254 })
    ),
    UnitCategory.WEIGHT to listOf(
      ConversionUnit("kg", "Kilogram", "kg", { it }, { it }),
      ConversionUnit("g", "Gram", "g", { it / 1000.0 }, { it * 1000.0 }),
      ConversionUnit("mg", "Milligram", "mg", { it / 1_000_000.0 }, { it * 1_000_000.0 }),
      ConversionUnit("lb", "Pound", "lb", { it * 0.45359237 }, { it / 0.45359237 }),
      ConversionUnit("oz", "Ounce", "oz", { it * 0.028349523125 }, { it / 0.028349523125 }),
      ConversionUnit("t", "Metric Ton", "t", { it * 1000.0 }, { it / 1000.0 })
    ),
    UnitCategory.TEMPERATURE to listOf(
      ConversionUnit("c", "Celsius", "°C", { it }, { it }),
      ConversionUnit("f", "Fahrenheit", "°F", { (it - 32.0) * 5.0 / 9.0 }, { it * 9.0 / 5.0 + 32.0 }),
      ConversionUnit("k", "Kelvin", "K", { it - 273.15 }, { it + 273.15 })
    ),
    UnitCategory.AREA to listOf(
      ConversionUnit("sq_m", "Square Meter", "m²", { it }, { it }),
      ConversionUnit("sq_km", "Square Kilometer", "km²", { it * 1_000_000.0 }, { it / 1_000_000.0 }),
      ConversionUnit("sq_ft", "Square Foot", "ft²", { it * 0.092903 }, { it / 0.092903 }),
      ConversionUnit("sq_yd", "Square Yard", "yd²", { it * 0.836127 }, { it / 0.836127 }),
      ConversionUnit("acre", "Acre", "ac", { it * 4046.85642 }, { it / 4046.85642 }),
      ConversionUnit("ha", "Hectare", "ha", { it * 10000.0 }, { it / 10000.0 })
    ),
    UnitCategory.VOLUME to listOf(
      ConversionUnit("l", "Liter", "L", { it }, { it }),
      ConversionUnit("ml", "Milliliter", "mL", { it / 1000.0 }, { it * 1000.0 }),
      ConversionUnit("gal", "Gallon (US)", "gal", { it * 3.78541 }, { it / 3.78541 }),
      ConversionUnit("qt", "Quart (US)", "qt", { it * 0.946353 }, { it / 0.946353 }),
      ConversionUnit("pt", "Pint (US)", "pt", { it * 0.473176 }, { it / 0.473176 }),
      ConversionUnit("cup", "Cup (US)", "cup", { it * 0.236588 }, { it / 0.236588 }),
      ConversionUnit("floz", "Fluid Ounce", "fl oz", { it * 0.0295735 }, { it / 0.0295735 }),
      ConversionUnit("cum", "Cubic Meter", "m³", { it * 1000.0 }, { it / 1000.0 })
    ),
    UnitCategory.SPEED to listOf(
      ConversionUnit("kmh", "Kilometers/hour", "km/h", { it }, { it }),
      ConversionUnit("mph", "Miles/hour", "mph", { it * 1.609344 }, { it / 1.609344 }),
      ConversionUnit("ms", "Meters/second", "m/s", { it * 3.6 }, { it / 3.6 }),
      ConversionUnit("knot", "Knot", "kn", { it * 1.852 }, { it / 1.852 })
    ),
    UnitCategory.DATA to listOf(
      ConversionUnit("b", "Byte", "B", { it }, { it }),
      ConversionUnit("kb", "Kilobyte", "KB", { it * 1024.0 }, { it / 1024.0 }),
      ConversionUnit("mb", "Megabyte", "MB", { it * 1024.0 * 1024.0 }, { it / (1024.0 * 1024.0) }),
      ConversionUnit("gb", "Gigabyte", "GB", { it * 1024.0 * 1024.0 * 1024.0 }, { it / (1024.0 * 1024.0 * 1024.0) }),
      ConversionUnit("tb", "Terabyte", "TB", { it * 1024.0 * 1024.0 * 1024.0 * 1024.0 }, { it / (1024.0 * 1024.0 * 1024.0 * 1024.0) })
    )
  )

  fun convert(value: Double, fromUnit: ConversionUnit, toUnit: ConversionUnit): Double {
    val baseValue = fromUnit.toBase(value)
    return toUnit.fromBase(baseValue)
  }

  fun formatValue(value: Double): String {
    return df.format(value)
  }
}
