package com.example.calculator.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculator.engine.CalculatorEngine
import com.example.calculator.model.ConversionUnit
import com.example.calculator.model.HistoryItem
import com.example.calculator.model.UnitCategory
import com.example.calculator.model.UnitConverterData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class AppScreenTab {
  CALCULATOR,
  CONVERTER,
  TIP
}

data class CalculatorUiState(
  val expression: String = "",
  val livePreview: String = "",
  val resultText: String = "0",
  val isResultDisplayed: Boolean = false,
  val angleMode: CalculatorEngine.AngleMode = CalculatorEngine.AngleMode.DEGREE,
  val isScientificExpanded: Boolean = false,
  val memoryValue: Double = 0.0,
  val hasMemory: Boolean = false,
  val history: List<HistoryItem> = emptyList(),
  val activeTab: AppScreenTab = AppScreenTab.CALCULATOR,
  val showHistorySheet: Boolean = false,

  // Unit Converter State
  val converterCategory: UnitCategory = UnitCategory.LENGTH,
  val converterFromUnit: ConversionUnit = UnitConverterData.categories[UnitCategory.LENGTH]!![0],
  val converterToUnit: ConversionUnit = UnitConverterData.categories[UnitCategory.LENGTH]!![1],
  val converterInput: String = "1",
  val converterOutput: String = "0.001",

  // Tip Calculator State
  val tipBillAmount: String = "50.00",
  val tipPercent: Float = 15f,
  val tipSplitPeople: Int = 2,
  val tipTotalAmount: Double = 57.50,
  val tipAmountOnly: Double = 7.50,
  val tipPerPersonTotal: Double = 28.75,
  val tipPerPersonTip: Double = 3.75
)

class CalculatorViewModel : ViewModel() {

  private val engine = CalculatorEngine()
  private val _uiState = MutableStateFlow(CalculatorUiState())
  val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

  init {
    updateTipCalculations()
  }

  fun setTab(tab: AppScreenTab) {
    _uiState.update { it.copy(activeTab = tab) }
  }

  fun toggleHistorySheet(show: Boolean) {
    _uiState.update { it.copy(showHistorySheet = show) }
  }

  fun toggleScientific() {
    _uiState.update { it.copy(isScientificExpanded = !it.isScientificExpanded) }
  }

  fun toggleAngleMode() {
    val newMode = if (_uiState.value.angleMode == CalculatorEngine.AngleMode.DEGREE) {
      CalculatorEngine.AngleMode.RADIAN
    } else {
      CalculatorEngine.AngleMode.DEGREE
    }
    engine.angleMode = newMode
    _uiState.update { it.copy(angleMode = newMode) }
    updateLivePreview()
  }

  fun onDigit(digit: String) {
    val current = _uiState.value
    val newExpr = if (current.isResultDisplayed) {
      digit
    } else {
      if (current.expression == "0") digit else current.expression + digit
    }
    _uiState.update {
      it.copy(
        expression = newExpr,
        isResultDisplayed = false
      )
    }
    updateLivePreview()
  }

  fun onDecimal() {
    val current = _uiState.value
    val expr = current.expression
    if (current.isResultDisplayed) {
      _uiState.update { it.copy(expression = "0.", isResultDisplayed = false) }
      updateLivePreview()
      return
    }

    // Check if the current number already has a decimal
    val lastTokens = expr.split(Regex("[+\\-×÷%^()]"))
    val currentNum = lastTokens.lastOrNull() ?: ""
    if (!currentNum.contains('.')) {
      val newExpr = if (expr.isEmpty() || isOperator(expr.last())) "${expr}0." else "$expr."
      _uiState.update { it.copy(expression = newExpr, isResultDisplayed = false) }
      updateLivePreview()
    }
  }

  fun onOperator(op: String) {
    val current = _uiState.value
    var expr = current.expression

    if (current.isResultDisplayed) {
      // Continue chaining calculation with previous result
      expr = current.resultText.replace(",", "")
    }

    if (expr.isEmpty()) {
      if (op == "−") {
        _uiState.update { it.copy(expression = "−", isResultDisplayed = false) }
      }
      return
    }

    val lastChar = expr.last()
    val newExpr = if (isOperator(lastChar)) {
      // Replace last operator
      expr.dropLast(1) + op
    } else {
      expr + op
    }

    _uiState.update { it.copy(expression = newExpr, isResultDisplayed = false) }
    updateLivePreview()
  }

  fun onParenthesis() {
    val current = _uiState.value
    val expr = current.expression
    val openCount = expr.count { it == '(' }
    val closeCount = expr.count { it == ')' }

    val newExpr = if (expr.isEmpty() || isOperator(expr.last()) || expr.last() == '(') {
      "$expr("
    } else if (openCount > closeCount && (expr.last().isDigit() || expr.last() == ')' || expr.last() == 'π' || expr.last() == 'e')) {
      "$expr)"
    } else {
      "$expr×("
    }

    _uiState.update { it.copy(expression = newExpr, isResultDisplayed = false) }
    updateLivePreview()
  }

  fun onScientificFunction(fn: String) {
    val current = _uiState.value
    val expr = if (current.isResultDisplayed) current.resultText.replace(",", "") else current.expression

    val newExpr = when (fn) {
      "x²" -> if (expr.isNotEmpty() && !isOperator(expr.last())) "$expr^2" else "${expr}0^2"
      "xʸ" -> if (expr.isNotEmpty() && !isOperator(expr.last())) "$expr^" else "${expr}0^"
      "√" -> if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) "$expr×√(" else "${expr}√("
      "∛" -> if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) "$expr×∛(" else "${expr}∛("
      "1/x" -> if (expr.isNotEmpty() && !isOperator(expr.last())) "1/($expr)" else "${expr}1/("
      "x!" -> if (expr.isNotEmpty() && !isOperator(expr.last())) "$expr!" else expr
      "π" -> if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) "${expr}×π" else "${expr}π"
      "e" -> if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) "${expr}×e" else "${expr}e"
      "sin" -> "${expr}sin("
      "cos" -> "${expr}cos("
      "tan" -> "${expr}tan("
      "ln" -> "${expr}ln("
      "log" -> "${expr}log("
      "asin" -> "${expr}asin("
      "acos" -> "${expr}acos("
      "atan" -> "${expr}atan("
      else -> expr
    }

    _uiState.update { it.copy(expression = newExpr, isResultDisplayed = false) }
    updateLivePreview()
  }

  fun onPercent() {
    val current = _uiState.value
    val expr = current.expression
    if (expr.isEmpty() || isOperator(expr.last())) return

    // Evaluate current number / 100
    val newExpr = "$expr% "
    _uiState.update { it.copy(expression = newExpr, isResultDisplayed = false) }
    updateLivePreview()
  }

  fun onBackspace() {
    val current = _uiState.value
    if (current.isResultDisplayed) {
      _uiState.update { it.copy(expression = "", resultText = "0", livePreview = "", isResultDisplayed = false) }
      return
    }

    val expr = current.expression
    if (expr.isNotEmpty()) {
      // Check for multi-char functions like "sin(", "cos(", "log("
      val suffixes = listOf("asin(", "acos(", "atan(", "sin(", "cos(", "tan(", "log(", "ln(", "√(", "∛(")
      var trimmed = expr
      var matched = false
      for (suffix in suffixes) {
        if (trimmed.endsWith(suffix)) {
          trimmed = trimmed.dropLast(suffix.length)
          matched = true
          break
        }
      }
      if (!matched) {
        trimmed = trimmed.dropLast(1)
      }

      _uiState.update { it.copy(expression = trimmed) }
      updateLivePreview()
    }
  }

  fun onClear() {
    _uiState.update {
      it.copy(
        expression = "",
        livePreview = "",
        resultText = "0",
        isResultDisplayed = false
      )
    }
  }

  fun onEqual() {
    val current = _uiState.value
    val expr = current.expression
    if (expr.isBlank()) return

    val eval = engine.evaluate(expr)
    eval.onSuccess { value ->
      val formatted = engine.formatResult(value)
      val newHistory = listOf(
        HistoryItem(expression = expr, result = formatted)
      ) + current.history

      _uiState.update {
        it.copy(
          resultText = formatted,
          expression = expr,
          livePreview = "",
          isResultDisplayed = true,
          history = newHistory
        )
      }
    }.onFailure { ex ->
      _uiState.update {
        it.copy(
          resultText = ex.message ?: "Error",
          livePreview = "",
          isResultDisplayed = true
        )
      }
    }
  }

  fun onSelectHistoryItem(item: HistoryItem) {
    _uiState.update {
      it.copy(
        expression = item.expression,
        resultText = item.result,
        livePreview = "",
        isResultDisplayed = true,
        showHistorySheet = false
      )
    }
  }

  fun onClearHistory() {
    _uiState.update { it.copy(history = emptyList(), showHistorySheet = false) }
  }

  // Memory functions
  fun onMemoryAdd() {
    val current = _uiState.value
    val value = current.resultText.replace(",", "").toDoubleOrNull() ?: 0.0
    val newMem = current.memoryValue + value
    _uiState.update { it.copy(memoryValue = newMem, hasMemory = newMem != 0.0) }
  }

  fun onMemorySubtract() {
    val current = _uiState.value
    val value = current.resultText.replace(",", "").toDoubleOrNull() ?: 0.0
    val newMem = current.memoryValue - value
    _uiState.update { it.copy(memoryValue = newMem, hasMemory = newMem != 0.0) }
  }

  fun onMemoryRecall() {
    val current = _uiState.value
    if (!current.hasMemory) return
    val memStr = engine.formatResult(current.memoryValue)
    onDigit(memStr)
  }

  fun onMemoryClear() {
    _uiState.update { it.copy(memoryValue = 0.0, hasMemory = false) }
  }

  // --- UNIT CONVERTER METHODS ---

  fun setConverterCategory(category: UnitCategory) {
    val units = UnitConverterData.categories[category] ?: return
    _uiState.update {
      it.copy(
        converterCategory = category,
        converterFromUnit = units[0],
        converterToUnit = if (units.size > 1) units[1] else units[0]
      )
    }
    updateConversion()
  }

  fun setConverterFromUnit(unit: ConversionUnit) {
    _uiState.update { it.copy(converterFromUnit = unit) }
    updateConversion()
  }

  fun setConverterToUnit(unit: ConversionUnit) {
    _uiState.update { it.copy(converterToUnit = unit) }
    updateConversion()
  }

  fun onConverterInputChanged(input: String) {
    _uiState.update { it.copy(converterInput = input) }
    updateConversion()
  }

  fun swapConverterUnits() {
    val current = _uiState.value
    _uiState.update {
      it.copy(
        converterFromUnit = current.converterToUnit,
        converterToUnit = current.converterFromUnit
      )
    }
    updateConversion()
  }

  private fun updateConversion() {
    val current = _uiState.value
    val num = current.converterInput.toDoubleOrNull() ?: 0.0
    val converted = UnitConverterData.convert(num, current.converterFromUnit, current.converterToUnit)
    val formatted = UnitConverterData.formatValue(converted)
    _uiState.update { it.copy(converterOutput = formatted) }
  }

  // --- TIP CALCULATOR METHODS ---

  fun setTipBillAmount(amount: String) {
    _uiState.update { it.copy(tipBillAmount = amount) }
    updateTipCalculations()
  }

  fun setTipPercent(percent: Float) {
    _uiState.update { it.copy(tipPercent = percent) }
    updateTipCalculations()
  }

  fun setTipSplitPeople(count: Int) {
    if (count in 1..50) {
      _uiState.update { it.copy(tipSplitPeople = count) }
      updateTipCalculations()
    }
  }

  private fun updateTipCalculations() {
    val current = _uiState.value
    val bill = current.tipBillAmount.toDoubleOrNull() ?: 0.0
    val tipPercent = current.tipPercent.toDouble()
    val people = current.tipSplitPeople.coerceAtLeast(1)

    val tip = bill * (tipPercent / 100.0)
    val total = bill + tip
    val perPersonTotal = total / people
    val perPersonTip = tip / people

    _uiState.update {
      it.copy(
        tipAmountOnly = tip,
        tipTotalAmount = total,
        tipPerPersonTotal = perPersonTotal,
        tipPerPersonTip = perPersonTip
      )
    }
  }

  private fun updateLivePreview() {
    val expr = _uiState.value.expression
    if (expr.length < 2 || _uiState.value.isResultDisplayed) {
      _uiState.update { it.copy(livePreview = "") }
      return
    }

    // Only attempt live preview if expression has an operator or function
    if (!expr.any { it in "+-×÷%^()√" }) {
      _uiState.update { it.copy(livePreview = "") }
      return
    }

    val res = engine.evaluate(expr)
    res.onSuccess { value ->
      val formatted = engine.formatResult(value)
      _uiState.update { it.copy(livePreview = "= $formatted") }
    }.onFailure {
      _uiState.update { it.copy(livePreview = "") }
    }
  }

  private fun isOperator(c: Char): Boolean = c in "+-×÷"
}
