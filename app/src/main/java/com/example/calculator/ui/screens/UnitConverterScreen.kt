package com.example.calculator.ui.screens

import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.model.ConversionUnit
import com.example.calculator.model.UnitCategory
import com.example.calculator.model.UnitConverterData
import com.example.calculator.ui.CalculatorUiState
import com.example.calculator.ui.CalculatorViewModel
import com.example.calculator.ui.components.CalcButton
import com.example.calculator.ui.components.CalcButtonType
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterScreen(
  viewModel: CalculatorViewModel,
  uiState: CalculatorUiState,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val availableUnits = UnitConverterData.categories[uiState.converterCategory] ?: emptyList()

  var showFromDropdown by remember { mutableStateOf(false) }
  var showToDropdown by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Horizontal category selector chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        UnitCategory.values().forEach { category ->
          FilterChip(
            selected = uiState.converterCategory == category,
            onClick = { viewModel.setConverterCategory(category) },
            label = { Text(category.displayName) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
              selectedLabelColor = NeonCyan
            ),
            modifier = Modifier.testTag("converter_chip_${category.name}")
          )
        }
      }

      // Conversion Cards
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // "FROM" Section
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("From", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Box {
              OutlinedButton(
                onClick = { showFromDropdown = true },
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
              ) {
                Text("${uiState.converterFromUnit.name} (${uiState.converterFromUnit.symbol})")
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
              }
              DropdownMenu(
                expanded = showFromDropdown,
                onDismissRequest = { showFromDropdown = false }
              ) {
                availableUnits.forEach { unit ->
                  DropdownMenuItem(
                    text = { Text("${unit.name} (${unit.symbol})") },
                    onClick = {
                      viewModel.setConverterFromUnit(unit)
                      showFromDropdown = false
                    }
                  )
                }
              }
            }
          }

          Text(
            text = uiState.converterInput.ifEmpty { "0" },
            style = MaterialTheme.typography.headlineMedium.copy(
              fontSize = 32.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
          )

          // Swap Units Button
          Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
          ) {
            FilledTonalIconButton(
              onClick = { viewModel.swapConverterUnits() },
              modifier = Modifier.testTag("swap_converter_units_button")
            ) {
              Icon(Icons.Default.SwapVert, contentDescription = "Swap Units", tint = NeonOrange)
            }
          }

          // "TO" Section
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("To", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Box {
              OutlinedButton(
                onClick = { showToDropdown = true },
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
              ) {
                Text("${uiState.converterToUnit.name} (${uiState.converterToUnit.symbol})")
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
              }
              DropdownMenu(
                expanded = showToDropdown,
                onDismissRequest = { showToDropdown = false }
              ) {
                availableUnits.forEach { unit ->
                  DropdownMenuItem(
                    text = { Text("${unit.name} (${unit.symbol})") },
                    onClick = {
                      viewModel.setConverterToUnit(unit)
                      showToDropdown = false
                    }
                  )
                }
              }
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = {
                clipboardManager.setText(AnnotatedString(uiState.converterOutput))
                Toast.makeText(context, "Copied ${uiState.converterOutput} ${uiState.converterToUnit.symbol}", Toast.LENGTH_SHORT).show()
              }
            ) {
              Icon(Icons.Default.ContentCopy, contentDescription = "Copy converted value", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Text(
              text = uiState.converterOutput,
              style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = NeonOrange
              ),
              textAlign = TextAlign.End
            )
          }
        }
      }
    }

    // Integrated Numeric Keypad for fast input
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        CalcButton("7", { handleConverterDigit(viewModel, uiState, "7") }, CalcButtonType.NUMBER, Modifier.weight(1f), height = 54.dp)
        CalcButton("8", { handleConverterDigit(viewModel, uiState, "8") }, CalcButtonType.NUMBER, Modifier.weight(1f), height = 54.dp)
        CalcButton("9", { handleConverterDigit(viewModel, uiState, "9") }, CalcButtonType.NUMBER, Modifier.weight(1f), height = 54.dp)
        CalcButton("C", { viewModel.onConverterInputChanged("0") }, CalcButtonType.FUNCTION, Modifier.weight(1f), height = 54.dp)
      }
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        CalcButton("4", { handleConverterDigit(viewModel, uiState, "4") }, CalcButtonType.NUMBER, Modifier.weight(1f), height = 54.dp)
        CalcButton("5", { handleConverterDigit(viewModel, uiState, "5") }, CalcButtonType.NUMBER, Modifier.weight(1f), height = 54.dp)
        CalcButton("6", { handleConverterDigit(viewModel, uiState, "6") }, CalcButtonType.NUMBER, Modifier.weight(1f), height = 54.dp)
        CalcButton(
          text = "⌫",
          onClick = {
            val input = uiState.converterInput
            viewModel.onConverterInputChanged(if (input.length > 1) input.dropLast(1) else "0")
          },
          type = CalcButtonType.FUNCTION,
          modifier = Modifier.weight(1f),
          height = 54.dp,
          icon = Icons.AutoMirrored.Filled.Backspace
        )
      }
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        CalcButton("1", { handleConverterDigit(viewModel, uiState, "1") }, CalcButtonType.NUMBER, Modifier.weight(1f), height = 54.dp)
        CalcButton("2", { handleConverterDigit(viewModel, uiState, "2") }, CalcButtonType.NUMBER, Modifier.weight(1f), height = 54.dp)
        CalcButton("3", { handleConverterDigit(viewModel, uiState, "3") }, CalcButtonType.NUMBER, Modifier.weight(1f), height = 54.dp)
        CalcButton(".", {
          if (!uiState.converterInput.contains('.')) {
            viewModel.onConverterInputChanged("${uiState.converterInput}.")
          }
        }, CalcButtonType.NUMBER, Modifier.weight(1f), height = 54.dp)
      }
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        CalcButton("0", { handleConverterDigit(viewModel, uiState, "0") }, CalcButtonType.NUMBER, Modifier.weight(2f), height = 54.dp)
        CalcButton("00", { handleConverterDigit(viewModel, uiState, "00") }, CalcButtonType.NUMBER, Modifier.weight(1f), height = 54.dp)
        CalcButton("±", {
          val num = uiState.converterInput.toDoubleOrNull() ?: 0.0
          viewModel.onConverterInputChanged((-num).toString())
        }, CalcButtonType.FUNCTION, Modifier.weight(1f), height = 54.dp)
      }
    }
  }
}

private fun handleConverterDigit(viewModel: CalculatorViewModel, state: CalculatorUiState, digit: String) {
  val cur = state.converterInput
  val next = if (cur == "0" && digit != ".") digit else cur + digit
  if (next.length <= 12) {
    viewModel.onConverterInputChanged(next)
  }
}
