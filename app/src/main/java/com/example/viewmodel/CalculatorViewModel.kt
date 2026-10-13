package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.engine.ExpressionEvaluator
import com.example.model.CalculatorState
import com.example.model.ConversionUnit
import com.example.model.HistoryItem
import com.example.model.UnitCategory
import com.example.model.UnitDefinitions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.roundToInt

data class UnitConverterUiState(
    val category: UnitCategory = UnitCategory.LENGTH,
    val fromUnit: ConversionUnit = UnitDefinitions.unitsByCategory[UnitCategory.LENGTH]!![0], // Meter
    val toUnit: ConversionUnit = UnitDefinitions.unitsByCategory[UnitCategory.LENGTH]!![4], // Mile
    val inputAmount: String = "1",
    val convertedAmount: String = "0.000621371"
)

data class FinanceToolsUiState(
    // Tip Calculator
    val billAmount: String = "50.00",
    val tipPercent: Double = 15.0,
    val customTipPercent: String = "",
    val isCustomTip: Boolean = false,
    val splitCount: Int = 2,
    val roundUpTotal: Boolean = false,
    val tipAmount: Double = 7.50,
    val totalWithTip: Double = 57.50,
    val tipPerPerson: Double = 3.75,
    val totalPerPerson: Double = 28.75,

    // Discount Calculator
    val originalPrice: String = "100.00",
    val discountPercent: String = "20",
    val taxPercent: String = "8",
    val savingsAmount: Double = 20.00,
    val taxAmount: Double = 6.40,
    val finalPrice: Double = 86.40
)

class CalculatorViewModel : ViewModel() {

    private val evaluator = ExpressionEvaluator()

    private val _calculatorState = MutableStateFlow(CalculatorState())
    val calculatorState: StateFlow<CalculatorState> = _calculatorState.asStateFlow()

    private val _unitConverterState = MutableStateFlow(UnitConverterUiState())
    val unitConverterState: StateFlow<UnitConverterUiState> = _unitConverterState.asStateFlow()

    private val _financeState = MutableStateFlow(FinanceToolsUiState())
    val financeState: StateFlow<FinanceToolsUiState> = _financeState.asStateFlow()

    init {
        updateUnitConversion()
        recalculateFinance()
    }

    // ==========================================
    // CALCULATOR ACTIONS
    // ==========================================

    fun onDigit(digit: String) {
        _calculatorState.update { current ->
            val newExpr = if (current.isEvaluated) {
                digit
            } else {
                current.expression + digit
            }
            current.copy(
                expression = newExpr,
                isEvaluated = false,
                errorMessage = null
            )
        }
        updatePreview()
    }

    fun onDecimal() {
        _calculatorState.update { current ->
            val expr = if (current.isEvaluated) "" else current.expression
            // Check if current number already has a decimal
            val lastNumber = expr.takeLastWhile { it.isDigit() || it == '.' }
            val newExpr = if (lastNumber.contains('.')) {
                expr
            } else if (expr.isEmpty() || !expr.last().isDigit()) {
                expr + "0."
            } else {
                expr + "."
            }
            current.copy(
                expression = newExpr,
                isEvaluated = false,
                errorMessage = null
            )
        }
        updatePreview()
    }

    fun onOperator(op: String) {
        _calculatorState.update { current ->
            var expr = current.expression
            if (expr.isEmpty()) {
                if (op == "−" || op == "-") {
                    expr = "−"
                } else if (current.lastResult != null) {
                    expr = current.lastResult + op
                }
            } else {
                val lastChar = expr.last()
                if (isOperatorChar(lastChar)) {
                    // Replace operator with new one
                    expr = expr.dropLast(1) + op
                } else {
                    expr += op
                }
            }
            current.copy(
                expression = expr,
                isEvaluated = false,
                errorMessage = null
            )
        }
        updatePreview()
    }

    private fun isOperatorChar(c: Char): Boolean {
        return c == '+' || c == '−' || c == '-' || c == '×' || c == '*' || c == '÷' || c == '/' || c == '^'
    }

    fun onParenthesis() {
        _calculatorState.update { current ->
            val expr = if (current.isEvaluated) "" else current.expression
            val openCount = expr.count { it == '(' }
            val closeCount = expr.count { it == ')' }

            val newChar = if (expr.isEmpty() || isOperatorChar(expr.last()) || expr.last() == '(') {
                "("
            } else if (openCount > closeCount && (expr.last().isDigit() || expr.last() == ')' || expr.last() == 'π' || expr.last() == 'e')) {
                ")"
            } else {
                "×("
            }

            current.copy(
                expression = expr + newChar,
                isEvaluated = false,
                errorMessage = null
            )
        }
        updatePreview()
    }

    fun onPercentage() {
        _calculatorState.update { current ->
            val expr = current.expression
            if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')' || expr.last() == 'π' || expr.last() == 'e')) {
                current.copy(expression = expr + "%", isEvaluated = false)
            } else {
                current
            }
        }
        updatePreview()
    }

    fun onToggleSign() {
        _calculatorState.update { current ->
            val expr = current.expression
            if (expr.isEmpty()) return@update current

            // Find last number token and negate it
            var index = expr.length - 1
            while (index >= 0 && (expr[index].isDigit() || expr[index] == '.')) {
                index--
            }

            val prefix = expr.substring(0, index + 1)
            val number = expr.substring(index + 1)

            if (number.isNotEmpty()) {
                val newExpr = if (prefix.endsWith("(-")) {
                    prefix.dropLast(2) + number
                } else if (prefix.endsWith("−") || prefix.endsWith("-")) {
                    prefix.dropLast(1) + "+" + number
                } else if (prefix.endsWith("+")) {
                    prefix.dropLast(1) + "−" + number
                } else {
                    prefix + "(-" + number + ")"
                }
                current.copy(expression = newExpr, isEvaluated = false)
            } else {
                current
            }
        }
        updatePreview()
    }

    fun onScientificFunction(func: String) {
        _calculatorState.update { current ->
            val expr = if (current.isEvaluated) "" else current.expression
            val newExpr = when (func) {
                "x²" -> if (expr.isNotEmpty() && !isOperatorChar(expr.last())) "$expr^2" else "${expr}0^2"
                "xʸ" -> if (expr.isNotEmpty() && !isOperatorChar(expr.last())) "$expr^" else "${expr}0^"
                "√x", "sqrt" -> if (expr.isEmpty() || isOperatorChar(expr.last()) || expr.last() == '(') "${expr}√(" else "${expr}×√("
                "∛x", "cbrt" -> if (expr.isEmpty() || isOperatorChar(expr.last()) || expr.last() == '(') "${expr}cbrt(" else "${expr}×cbrt("
                "1/x" -> if (expr.isEmpty() || isOperatorChar(expr.last()) || expr.last() == '(') "${expr}inv(" else "${expr}×inv("
                "x!" -> if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) "$expr!" else expr
                "10ˣ" -> if (expr.isEmpty() || isOperatorChar(expr.last()) || expr.last() == '(') "${expr}10^(" else "${expr}×10^("
                "eˣ" -> if (expr.isEmpty() || isOperatorChar(expr.last()) || expr.last() == '(') "${expr}e^(" else "${expr}×e^("
                "|x|" -> if (expr.isEmpty() || isOperatorChar(expr.last()) || expr.last() == '(') "${expr}abs(" else "${expr}×abs("
                else -> { // sin, cos, tan, asin, acos, atan, ln, log
                    if (expr.isEmpty() || isOperatorChar(expr.last()) || expr.last() == '(') {
                        "$expr$func("
                    } else {
                        "$expr×$func("
                    }
                }
            }
            current.copy(expression = newExpr, isEvaluated = false, errorMessage = null)
        }
        updatePreview()
    }

    fun onConstant(constant: String) {
        _calculatorState.update { current ->
            val expr = if (current.isEvaluated) "" else current.expression
            val token = if (constant == "pi") "π" else if (constant == "phi") "φ" else "e"
            val newExpr = if (expr.isEmpty() || isOperatorChar(expr.last()) || expr.last() == '(') {
                expr + token
            } else {
                "$expr×$token"
            }
            current.copy(expression = newExpr, isEvaluated = false, errorMessage = null)
        }
        updatePreview()
    }

    fun onBackspace() {
        _calculatorState.update { current ->
            if (current.isEvaluated) {
                current.copy(expression = "", previewResult = null, isEvaluated = false)
            } else {
                val expr = current.expression
                if (expr.isEmpty()) return@update current

                // Check multi-character tokens like "sqrt(", "cbrt(", "asin(", "acos(", "atan(", "sin(", "cos(", "tan(", "log(", "inv("
                val patterns = listOf("sqrt(", "cbrt(", "asin(", "acos(", "atan(", "sin(", "cos(", "tan(", "log(", "inv(", "abs(", "ln(")
                var removed = false
                var newExpr = expr

                for (pat in patterns) {
                    if (expr.endsWith(pat)) {
                        newExpr = expr.dropLast(pat.length)
                        removed = true
                        break
                    }
                }

                if (!removed) {
                    newExpr = expr.dropLast(1)
                }

                current.copy(expression = newExpr, errorMessage = null)
            }
        }
        updatePreview()
    }

    fun onClear() {
        _calculatorState.update {
            it.copy(
                expression = "",
                previewResult = null,
                isEvaluated = false,
                errorMessage = null
            )
        }
    }

    fun onEquals() {
        val current = _calculatorState.value
        val expr = current.expression
        if (expr.isBlank()) return

        when (val res = evaluator.evaluate(expr, current.isRadMode)) {
            is ExpressionEvaluator.Result.Success -> {
                val historyItem = HistoryItem(
                    expression = expr,
                    result = res.formatted
                )
                _calculatorState.update {
                    it.copy(
                        expression = res.formatted,
                        previewResult = null,
                        lastResult = res.formatted,
                        isEvaluated = true,
                        errorMessage = null,
                        history = listOf(historyItem) + it.history
                    )
                }
            }
            is ExpressionEvaluator.Result.Error -> {
                _calculatorState.update {
                    it.copy(errorMessage = res.message)
                }
            }
        }
    }

    private fun updatePreview() {
        val state = _calculatorState.value
        if (state.expression.isBlank() || state.isEvaluated) {
            _calculatorState.update { it.copy(previewResult = null) }
            return
        }

        // Try evaluating expression for live preview
        when (val res = evaluator.evaluate(state.expression, state.isRadMode)) {
            is ExpressionEvaluator.Result.Success -> {
                _calculatorState.update { it.copy(previewResult = "= ${res.formatted}") }
            }
            is ExpressionEvaluator.Result.Error -> {
                _calculatorState.update { it.copy(previewResult = null) }
            }
        }
    }

    fun toggleRadMode() {
        _calculatorState.update { it.copy(isRadMode = !it.isRadMode) }
        updatePreview()
    }

    fun toggleInverseTrig() {
        _calculatorState.update { it.copy(isInverseTrig = !it.isInverseTrig) }
    }

    fun toggleScientificExpanded() {
        _calculatorState.update { it.copy(isScientificExpanded = !it.isScientificExpanded) }
    }

    fun onMemoryAdd() {
        val current = _calculatorState.value
        val valueToUse = current.expression.toDoubleOrNull()
            ?: current.previewResult?.removePrefix("= ")?.toDoubleOrNull()
            ?: return
        _calculatorState.update {
            val newMem = it.memoryValue + valueToUse
            it.copy(memoryValue = newMem, hasMemory = true)
        }
    }

    fun onMemorySubtract() {
        val current = _calculatorState.value
        val valueToUse = current.expression.toDoubleOrNull()
            ?: current.previewResult?.removePrefix("= ")?.toDoubleOrNull()
            ?: return
        _calculatorState.update {
            val newMem = it.memoryValue - valueToUse
            it.copy(memoryValue = newMem, hasMemory = true)
        }
    }

    fun onMemoryRecall() {
        val mem = _calculatorState.value.memoryValue
        val formatted = evaluator.formatResult(mem)
        _calculatorState.update {
            it.copy(expression = formatted, isEvaluated = true)
        }
        updatePreview()
    }

    fun onMemoryClear() {
        _calculatorState.update {
            it.copy(memoryValue = 0.0, hasMemory = false)
        }
    }

    fun onHistoryItemClick(item: HistoryItem, loadExpression: Boolean) {
        _calculatorState.update {
            val exprToSet = if (loadExpression) item.expression else item.result
            it.copy(
                expression = exprToSet,
                isEvaluated = !loadExpression,
                errorMessage = null
            )
        }
        updatePreview()
    }

    fun clearHistory() {
        _calculatorState.update { it.copy(history = emptyList()) }
    }

    // ==========================================
    // UNIT CONVERTER ACTIONS
    // ==========================================

    fun selectUnitCategory(category: UnitCategory) {
        val units = UnitDefinitions.unitsByCategory[category] ?: return
        _unitConverterState.update {
            it.copy(
                category = category,
                fromUnit = units[0],
                toUnit = if (units.size > 1) units[1] else units[0]
            )
        }
        updateUnitConversion()
    }

    fun setFromUnit(unit: ConversionUnit) {
        _unitConverterState.update { it.copy(fromUnit = unit) }
        updateUnitConversion()
    }

    fun setToUnit(unit: ConversionUnit) {
        _unitConverterState.update { it.copy(toUnit = unit) }
        updateUnitConversion()
    }

    fun setUnitInputAmount(amount: String) {
        _unitConverterState.update { it.copy(inputAmount = amount) }
        updateUnitConversion()
    }

    fun swapUnits() {
        _unitConverterState.update {
            it.copy(
                fromUnit = it.toUnit,
                toUnit = it.fromUnit
            )
        }
        updateUnitConversion()
    }

    private fun updateUnitConversion() {
        val state = _unitConverterState.value
        val num = state.inputAmount.toDoubleOrNull() ?: 0.0
        val converted = UnitDefinitions.convert(num, state.category, state.fromUnit, state.toUnit)

        val symbols = DecimalFormatSymbols(Locale.US)
        val df = if (converted != 0.0 && (Math.abs(converted) >= 1e9 || Math.abs(converted) < 1e-5)) {
            DecimalFormat("0.######E0", symbols)
        } else {
            DecimalFormat("#,##0.######", symbols)
        }
        val formatted = df.format(converted)

        _unitConverterState.update { it.copy(convertedAmount = formatted) }
    }

    // ==========================================
    // FINANCE TOOLS ACTIONS
    // ==========================================

    fun setBillAmount(amount: String) {
        _financeState.update { it.copy(billAmount = amount) }
        recalculateFinance()
    }

    fun setTipPercent(percent: Double) {
        _financeState.update { it.copy(tipPercent = percent, isCustomTip = false) }
        recalculateFinance()
    }

    fun setCustomTipPercent(customStr: String) {
        val percent = customStr.toDoubleOrNull() ?: 0.0
        _financeState.update {
            it.copy(
                customTipPercent = customStr,
                tipPercent = percent,
                isCustomTip = true
            )
        }
        recalculateFinance()
    }

    fun setSplitCount(count: Int) {
        if (count < 1) return
        _financeState.update { it.copy(splitCount = count) }
        recalculateFinance()
    }

    fun toggleRoundUpTotal() {
        _financeState.update { it.copy(roundUpTotal = !it.roundUpTotal) }
        recalculateFinance()
    }

    fun setOriginalPrice(price: String) {
        _financeState.update { it.copy(originalPrice = price) }
        recalculateFinance()
    }

    fun setDiscountPercent(discount: String) {
        _financeState.update { it.copy(discountPercent = discount) }
        recalculateFinance()
    }

    fun setTaxPercent(tax: String) {
        _financeState.update { it.copy(taxPercent = tax) }
        recalculateFinance()
    }

    private fun recalculateFinance() {
        _financeState.update { current ->
            // Tip calculation
            val bill = current.billAmount.toDoubleOrNull() ?: 0.0
            var rawTip = bill * (current.tipPercent / 100.0)
            var rawTotal = bill + rawTip

            if (current.roundUpTotal && rawTotal > 0.0) {
                val roundedTotal = kotlin.math.ceil(rawTotal)
                rawTip = roundedTotal - bill
                rawTotal = roundedTotal
            }

            val split = current.splitCount.coerceAtLeast(1)
            val tipPerPerson = rawTip / split
            val totalPerPerson = rawTotal / split

            // Discount calculation
            val orig = current.originalPrice.toDoubleOrNull() ?: 0.0
            val discPct = current.discountPercent.toDoubleOrNull() ?: 0.0
            val taxPct = current.taxPercent.toDoubleOrNull() ?: 0.0

            val savings = orig * (discPct / 100.0)
            val discountedPrice = orig - savings
            val tax = discountedPrice * (taxPct / 100.0)
            val finalAmt = discountedPrice + tax

            current.copy(
                tipAmount = rawTip,
                totalWithTip = rawTotal,
                tipPerPerson = tipPerPerson,
                totalPerPerson = totalPerPerson,
                savingsAmount = savings,
                taxAmount = tax,
                finalPrice = finalAmt
            )
        }
    }
}
