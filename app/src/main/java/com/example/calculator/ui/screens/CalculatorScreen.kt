package com.example.calculator.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.CalculatorUiState
import com.example.calculator.ui.CalculatorViewModel
import com.example.calculator.ui.components.*

@Composable
fun CalculatorScreen(
  viewModel: CalculatorViewModel,
  uiState: CalculatorUiState,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(bottom = 12.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    // Top Display area
    CalculatorDisplay(
      expression = uiState.expression,
      livePreview = uiState.livePreview,
      resultText = uiState.resultText,
      isResultDisplayed = uiState.isResultDisplayed,
      angleMode = uiState.angleMode,
      hasMemory = uiState.hasMemory,
      isScientific = uiState.isScientificExpanded,
      onToggleAngleMode = { viewModel.toggleAngleMode() },
      onToggleScientific = { viewModel.toggleScientific() },
      onOpenHistory = { viewModel.toggleHistorySheet(true) },
      modifier = Modifier.weight(1f, fill = false)
    )

    // Scientific keypad (expandable)
    AnimatedVisibility(
      visible = uiState.isScientificExpanded,
      enter = expandVertically() + fadeIn(),
      exit = shrinkVertically() + fadeOut()
    ) {
      ScientificKeypad(
        onFunction = { viewModel.onScientificFunction(it) },
        onMemoryAdd = { viewModel.onMemoryAdd() },
        onMemorySub = { viewModel.onMemorySubtract() },
        onMemoryRecall = { viewModel.onMemoryRecall() },
        onMemoryClear = { viewModel.onMemoryClear() }
      )
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Main 5-row Keypad
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Row 1: AC, (), %, ÷
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        CalcButton(
          text = if (uiState.expression.isNotEmpty()) "C" else "AC",
          onClick = { viewModel.onClear() },
          type = CalcButtonType.FUNCTION,
          modifier = Modifier.weight(1f),
          fontSize = 22.sp,
          testTag = "btn_clear"
        )
        CalcButton(
          text = "( )",
          onClick = { viewModel.onParenthesis() },
          type = CalcButtonType.FUNCTION,
          modifier = Modifier.weight(1f),
          fontSize = 22.sp,
          testTag = "btn_parenthesis"
        )
        CalcButton(
          text = "%",
          onClick = { viewModel.onPercent() },
          type = CalcButtonType.FUNCTION,
          modifier = Modifier.weight(1f),
          fontSize = 22.sp,
          testTag = "btn_percent"
        )
        CalcButton(
          text = "÷",
          onClick = { viewModel.onOperator("÷") },
          type = CalcButtonType.OPERATOR,
          modifier = Modifier.weight(1f),
          fontSize = 28.sp,
          testTag = "btn_divide"
        )
      }

      // Row 2: 7, 8, 9, ×
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        CalcButton("7", { viewModel.onDigit("7") }, CalcButtonType.NUMBER, Modifier.weight(1f), testTag = "btn_7")
        CalcButton("8", { viewModel.onDigit("8") }, CalcButtonType.NUMBER, Modifier.weight(1f), testTag = "btn_8")
        CalcButton("9", { viewModel.onDigit("9") }, CalcButtonType.NUMBER, Modifier.weight(1f), testTag = "btn_9")
        CalcButton("×", { viewModel.onOperator("×") }, CalcButtonType.OPERATOR, Modifier.weight(1f), fontSize = 28.sp, testTag = "btn_multiply")
      }

      // Row 3: 4, 5, 6, −
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        CalcButton("4", { viewModel.onDigit("4") }, CalcButtonType.NUMBER, Modifier.weight(1f), testTag = "btn_4")
        CalcButton("5", { viewModel.onDigit("5") }, CalcButtonType.NUMBER, Modifier.weight(1f), testTag = "btn_5")
        CalcButton("6", { viewModel.onDigit("6") }, CalcButtonType.NUMBER, Modifier.weight(1f), testTag = "btn_6")
        CalcButton("−", { viewModel.onOperator("−") }, CalcButtonType.OPERATOR, Modifier.weight(1f), fontSize = 28.sp, testTag = "btn_subtract")
      }

      // Row 4: 1, 2, 3, +
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        CalcButton("1", { viewModel.onDigit("1") }, CalcButtonType.NUMBER, Modifier.weight(1f), testTag = "btn_1")
        CalcButton("2", { viewModel.onDigit("2") }, CalcButtonType.NUMBER, Modifier.weight(1f), testTag = "btn_2")
        CalcButton("3", { viewModel.onDigit("3") }, CalcButtonType.NUMBER, Modifier.weight(1f), testTag = "btn_3")
        CalcButton("+", { viewModel.onOperator("+") }, CalcButtonType.OPERATOR, Modifier.weight(1f), fontSize = 28.sp, testTag = "btn_add")
      }

      // Row 5: 0, ., ⌫, =
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        CalcButton("0", { viewModel.onDigit("0") }, CalcButtonType.NUMBER, Modifier.weight(1f), testTag = "btn_0")
        CalcButton(".", { viewModel.onDecimal() }, CalcButtonType.NUMBER, Modifier.weight(1f), fontSize = 26.sp, testTag = "btn_dot")
        CalcButton(
          text = "Back",
          onClick = { viewModel.onBackspace() },
          type = CalcButtonType.FUNCTION,
          modifier = Modifier.weight(1f),
          icon = Icons.AutoMirrored.Filled.Backspace,
          testTag = "btn_backspace"
        )
        CalcButton(
          text = "=",
          onClick = { viewModel.onEqual() },
          type = CalcButtonType.EQUALS,
          modifier = Modifier.weight(1f),
          fontSize = 30.sp,
          testTag = "btn_equal"
        )
      }
    }
  }

  // History Bottom Sheet
  if (uiState.showHistorySheet) {
    HistoryBottomSheet(
      history = uiState.history,
      onSelectItem = { viewModel.onSelectHistoryItem(it) },
      onClearHistory = { viewModel.onClearHistory() },
      onDismiss = { viewModel.toggleHistorySheet(false) }
    )
  }
}
