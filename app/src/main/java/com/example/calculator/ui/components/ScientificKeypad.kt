package com.example.calculator.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScientificKeypad(
  onFunction: (String) -> Unit,
  onMemoryAdd: () -> Unit,
  onMemorySub: () -> Unit,
  onMemoryRecall: () -> Unit,
  onMemoryClear: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp),
    verticalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    // Row 1: Trigonometric & Logarithms
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      CalcButton("sin", { onFunction("sin") }, CalcButtonType.SCIENTIFIC, Modifier.weight(1f), height = 44.dp, fontSize = 15.sp)
      CalcButton("cos", { onFunction("cos") }, CalcButtonType.SCIENTIFIC, Modifier.weight(1f), height = 44.dp, fontSize = 15.sp)
      CalcButton("tan", { onFunction("tan") }, CalcButtonType.SCIENTIFIC, Modifier.weight(1f), height = 44.dp, fontSize = 15.sp)
      CalcButton("ln", { onFunction("ln") }, CalcButtonType.SCIENTIFIC, Modifier.weight(1f), height = 44.dp, fontSize = 15.sp)
      CalcButton("log", { onFunction("log") }, CalcButtonType.SCIENTIFIC, Modifier.weight(1f), height = 44.dp, fontSize = 15.sp)
    }

    // Row 2: Inverse Trig & Roots
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      CalcButton("asin", { onFunction("asin") }, CalcButtonType.SCIENTIFIC, Modifier.weight(1f), height = 44.dp, fontSize = 14.sp)
      CalcButton("acos", { onFunction("acos") }, CalcButtonType.SCIENTIFIC, Modifier.weight(1f), height = 44.dp, fontSize = 14.sp)
      CalcButton("atan", { onFunction("atan") }, CalcButtonType.SCIENTIFIC, Modifier.weight(1f), height = 44.dp, fontSize = 14.sp)
      CalcButton("√", { onFunction("√") }, CalcButtonType.SCIENTIFIC, Modifier.weight(1f), height = 44.dp, fontSize = 18.sp)
      CalcButton("∛", { onFunction("∛") }, CalcButtonType.SCIENTIFIC, Modifier.weight(1f), height = 44.dp, fontSize = 18.sp)
    }

    // Row 3: Powers, Factorial & Constants
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      CalcButton("x²", { onFunction("x²") }, CalcButtonType.SCIENTIFIC, Modifier.weight(1f), height = 44.dp, fontSize = 16.sp)
      CalcButton("xʸ", { onFunction("xʸ") }, CalcButtonType.SCIENTIFIC, Modifier.weight(1f), height = 44.dp, fontSize = 16.sp)
      CalcButton("1/x", { onFunction("1/x") }, CalcButtonType.SCIENTIFIC, Modifier.weight(1f), height = 44.dp, fontSize = 15.sp)
      CalcButton("x!", { onFunction("x!") }, CalcButtonType.SCIENTIFIC, Modifier.weight(1f), height = 44.dp, fontSize = 16.sp)
      CalcButton("π", { onFunction("π") }, CalcButtonType.SCIENTIFIC, Modifier.weight(1f), height = 44.dp, fontSize = 18.sp)
    }

    // Row 4: Euler constant & Memory keys
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      CalcButton("e", { onFunction("e") }, CalcButtonType.SCIENTIFIC, Modifier.weight(1f), height = 44.dp, fontSize = 17.sp)
      CalcButton("MC", onMemoryClear, CalcButtonType.SCIENTIFIC, Modifier.weight(1f), height = 44.dp, fontSize = 13.sp)
      CalcButton("MR", onMemoryRecall, CalcButtonType.SCIENTIFIC, Modifier.weight(1f), height = 44.dp, fontSize = 13.sp)
      CalcButton("M+", onMemoryAdd, CalcButtonType.SCIENTIFIC, Modifier.weight(1f), height = 44.dp, fontSize = 13.sp)
      CalcButton("M-", onMemorySub, CalcButtonType.SCIENTIFIC, Modifier.weight(1f), height = 44.dp, fontSize = 13.sp)
    }
  }
}
