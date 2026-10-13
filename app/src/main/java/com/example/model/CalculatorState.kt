package com.example.model

data class CalculatorState(
    val expression: String = "",
    val previewResult: String? = null,
    val lastResult: String? = null,
    val isEvaluated: Boolean = false,
    val isScientificExpanded: Boolean = false,
    val isRadMode: Boolean = false, // false = DEG, true = RAD
    val isInverseTrig: Boolean = false, // 2nd function toggle
    val memoryValue: Double = 0.0,
    val hasMemory: Boolean = false,
    val history: List<HistoryItem> = emptyList(),
    val errorMessage: String? = null
)
