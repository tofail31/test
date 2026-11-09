package com.example.calculator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.CalculatorUiState
import com.example.calculator.ui.CalculatorViewModel
import com.example.calculator.ui.components.CalcButton
import com.example.calculator.ui.components.CalcButtonType
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonOrange
import java.util.Locale

@Composable
fun TipCalculatorScreen(
  viewModel: CalculatorViewModel,
  uiState: CalculatorUiState,
  modifier: Modifier = Modifier
) {
  val tipPresets = listOf(10f, 15f, 18f, 20f, 25f)

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Big Result Summary Card
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "PER PERSON",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = String.format(Locale.US, "$%.2f", uiState.tipPerPersonTotal),
                style = MaterialTheme.typography.displaySmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = NeonOrange
                )
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "TIP / PERSON",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = String.format(Locale.US, "$%.2f", uiState.tipPerPersonTip),
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.SemiBold,
                  color = NeonCyan
                )
              )
            }
          }

          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Total Bill: " + String.format(Locale.US, "$%.2f", uiState.tipTotalAmount),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Total Tip: " + String.format(Locale.US, "$%.2f", uiState.tipAmountOnly),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }

      // Bill Amount Input Row
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Bill Amount", style = MaterialTheme.typography.titleMedium)
          Text(
            text = "$${uiState.tipBillAmount.ifEmpty { "0" }}",
            style = MaterialTheme.typography.headlineSmall.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          )
        }
      }

      // Tip Percentage Selector
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Tip Percentage", style = MaterialTheme.typography.titleSmall)
          Text(
            "${uiState.tipPercent.toInt()}%",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = NeonOrange)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          tipPresets.forEach { preset ->
            FilterChip(
              selected = uiState.tipPercent == preset,
              onClick = { viewModel.setTipPercent(preset) },
              label = { Text("${preset.toInt()}%") },
              modifier = Modifier.weight(1f),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = NeonOrange.copy(alpha = 0.2f),
                selectedLabelColor = NeonOrange
              )
            )
          }
        }

        Slider(
          value = uiState.tipPercent,
          onValueChange = { viewModel.setTipPercent(it) },
          valueRange = 0f..40f,
          steps = 39,
          colors = SliderDefaults.colors(
            thumbColor = NeonOrange,
            activeTrackColor = NeonOrange
          ),
          modifier = Modifier.testTag("tip_slider")
        )
      }

      // Split Among People Row
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Split Between", style = MaterialTheme.typography.titleMedium)
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            FilledTonalIconButton(
              onClick = { viewModel.setTipSplitPeople(uiState.tipSplitPeople - 1) },
              enabled = uiState.tipSplitPeople > 1,
              modifier = Modifier.testTag("tip_split_minus_button")
            ) {
              Icon(Icons.Default.Remove, contentDescription = "Decrease people")
            }

            Text(
              text = "${uiState.tipSplitPeople} ${if (uiState.tipSplitPeople == 1) "person" else "people"}",
              style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )

            FilledTonalIconButton(
              onClick = { viewModel.setTipSplitPeople(uiState.tipSplitPeople + 1) },
              modifier = Modifier.testTag("tip_split_plus_button")
            ) {
              Icon(Icons.Default.Add, contentDescription = "Increase people")
            }
          }
        }
      }
    }

    // Quick bill input keypad
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        CalcButton("7", { handleTipDigit(viewModel, uiState, "7") }, CalcButtonType.NUMBER, Modifier.weight(1f), height = 48.dp)
        CalcButton("8", { handleTipDigit(viewModel, uiState, "8") }, CalcButtonType.NUMBER, Modifier.weight(1f), height = 48.dp)
        CalcButton("9", { handleTipDigit(viewModel, uiState, "9") }, CalcButtonType.NUMBER, Modifier.weight(1f), height = 48.dp)
        CalcButton("C", { viewModel.setTipBillAmount("0") }, CalcButtonType.FUNCTION, Modifier.weight(1f), height = 48.dp)
      }
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        CalcButton("4", { handleTipDigit(viewModel, uiState, "4") }, CalcButtonType.NUMBER, Modifier.weight(1f), height = 48.dp)
        CalcButton("5", { handleTipDigit(viewModel, uiState, "5") }, CalcButtonType.NUMBER, Modifier.weight(1f), height = 48.dp)
        CalcButton("6", { handleTipDigit(viewModel, uiState, "6") }, CalcButtonType.NUMBER, Modifier.weight(1f), height = 48.dp)
        CalcButton("⌫", {
          val cur = uiState.tipBillAmount
          viewModel.setTipBillAmount(if (cur.length > 1) cur.dropLast(1) else "0")
        }, CalcButtonType.FUNCTION, Modifier.weight(1f), height = 48.dp)
      }
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        CalcButton("1", { handleTipDigit(viewModel, uiState, "1") }, CalcButtonType.NUMBER, Modifier.weight(1f), height = 48.dp)
        CalcButton("2", { handleTipDigit(viewModel, uiState, "2") }, CalcButtonType.NUMBER, Modifier.weight(1f), height = 48.dp)
        CalcButton("3", { handleTipDigit(viewModel, uiState, "3") }, CalcButtonType.NUMBER, Modifier.weight(1f), height = 48.dp)
        CalcButton(".", {
          if (!uiState.tipBillAmount.contains('.')) {
            viewModel.setTipBillAmount("${uiState.tipBillAmount}.")
          }
        }, CalcButtonType.NUMBER, Modifier.weight(1f), height = 48.dp)
      }
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        CalcButton("0", { handleTipDigit(viewModel, uiState, "0") }, CalcButtonType.NUMBER, Modifier.weight(3f), height = 48.dp)
        CalcButton("00", { handleTipDigit(viewModel, uiState, "00") }, CalcButtonType.NUMBER, Modifier.weight(1f), height = 48.dp)
      }
    }
  }
}

private fun handleTipDigit(viewModel: CalculatorViewModel, state: CalculatorUiState, digit: String) {
  val cur = state.tipBillAmount
  val next = if (cur == "0" && digit != ".") digit else cur + digit
  if (next.length <= 10) {
    viewModel.setTipBillAmount(next)
  }
}
