package com.example.model

enum class UnitCategory(val displayName: String, val iconName: String) {
    LENGTH("Length", "Straighten"),
    WEIGHT("Weight", "FitnessCenter"),
    TEMPERATURE("Temperature", "Thermostat"),
    AREA("Area", "CropFree"),
    VOLUME("Volume", "LocalDrink"),
    SPEED("Speed", "Speed"),
    STORAGE("Data", "Memory"),
    TIME("Time", "Schedule")
}

data class ConversionUnit(
    val name: String,
    val symbol: String,
    // Factor relative to standard base unit (or custom conversion for temperature)
    val toBaseFactor: Double = 1.0
)

object UnitDefinitions {
    val unitsByCategory = mapOf(
        UnitCategory.LENGTH to listOf(
            ConversionUnit("Meter", "m", 1.0),
            ConversionUnit("Kilometer", "km", 1000.0),
            ConversionUnit("Centimeter", "cm", 0.01),
            ConversionUnit("Millimeter", "mm", 0.001),
            ConversionUnit("Mile", "mi", 1609.344),
            ConversionUnit("Yard", "yd", 0.9144),
            ConversionUnit("Foot", "ft", 0.3048),
            ConversionUnit("Inch", "in", 0.0254)
        ),
        UnitCategory.WEIGHT to listOf(
            ConversionUnit("Kilogram", "kg", 1.0),
            ConversionUnit("Gram", "g", 0.001),
            ConversionUnit("Milligram", "mg", 0.000001),
            ConversionUnit("Pound", "lb", 0.45359237),
            ConversionUnit("Ounce", "oz", 0.028349523),
            ConversionUnit("Metric Ton", "t", 1000.0)
        ),
        UnitCategory.TEMPERATURE to listOf(
            ConversionUnit("Celsius", "°C", 1.0),
            ConversionUnit("Fahrenheit", "°F", 1.0),
            ConversionUnit("Kelvin", "K", 1.0)
        ),
        UnitCategory.AREA to listOf(
            ConversionUnit("Square Meter", "m²", 1.0),
            ConversionUnit("Square Kilometer", "km²", 1000000.0),
            ConversionUnit("Square Foot", "ft²", 0.09290304),
            ConversionUnit("Acre", "ac", 4046.8564),
            ConversionUnit("Hectare", "ha", 10000.0)
        ),
        UnitCategory.VOLUME to listOf(
            ConversionUnit("Liter", "L", 1.0),
            ConversionUnit("Milliliter", "mL", 0.001),
            ConversionUnit("US Gallon", "gal", 3.78541),
            ConversionUnit("US Quart", "qt", 0.946353),
            ConversionUnit("US Pint", "pt", 0.473176),
            ConversionUnit("US Cup", "cup", 0.236588),
            ConversionUnit("Fluid Ounce", "fl oz", 0.0295735),
            ConversionUnit("Cubic Meter", "m³", 1000.0)
        ),
        UnitCategory.SPEED to listOf(
            ConversionUnit("Meter / sec", "m/s", 1.0),
            ConversionUnit("Kilometer / hr", "km/h", 0.277778),
            ConversionUnit("Miles / hr", "mph", 0.44704),
            ConversionUnit("Knot", "kn", 0.514444)
        ),
        UnitCategory.STORAGE to listOf(
            ConversionUnit("Byte", "B", 1.0),
            ConversionUnit("Kilobyte", "KB", 1024.0),
            ConversionUnit("Megabyte", "MB", 1024.0 * 1024.0),
            ConversionUnit("Gigabyte", "GB", 1024.0 * 1024.0 * 1024.0),
            ConversionUnit("Terabyte", "TB", 1024.0 * 1024.0 * 1024.0 * 1024.0)
        ),
        UnitCategory.TIME to listOf(
            ConversionUnit("Second", "s", 1.0),
            ConversionUnit("Millisecond", "ms", 0.001),
            ConversionUnit("Minute", "min", 60.0),
            ConversionUnit("Hour", "h", 3600.0),
            ConversionUnit("Day", "d", 86400.0),
            ConversionUnit("Week", "wk", 604800.0),
            ConversionUnit("Year", "yr", 31536000.0)
        )
    )

    fun convert(
        value: Double,
        category: UnitCategory,
        fromUnit: ConversionUnit,
        toUnit: ConversionUnit
    ): Double {
        if (category == UnitCategory.TEMPERATURE) {
            // First convert fromUnit to Celsius
            val celsius = when (fromUnit.symbol) {
                "°C" -> value
                "°F" -> (value - 32.0) * 5.0 / 9.0
                "K" -> value - 273.15
                else -> value
            }
            // Then convert Celsius to toUnit
            return when (toUnit.symbol) {
                "°C" -> celsius
                "°F" -> (celsius * 9.0 / 5.0) + 32.0
                "K" -> celsius + 273.15
                else -> celsius
            }
        }

        // Standard linear conversions
        val inBase = value * fromUnit.toBaseFactor
        return inBase / toUnit.toBaseFactor
    }
}
