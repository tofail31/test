package com.example.calculator.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.engine.CalculatorEngine
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonTeal

@Composable
fun CalculatorDisplay(
  expression: String,
  livePreview: String,
  resultText: String,
  isResultDisplayed: Boolean,
  angleMode: CalculatorEngine.AngleMode,
  hasMemory: Boolean,
  isScientific: Boolean,
  onToggleAngleMode: () -> Unit,
  onToggleScientific: () -> Unit,
  onOpenHistory: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val scrollState = rememberScrollState()

  LaunchedEffect(expression, resultText) {
    scrollState.animateScrollTo(scrollState.maxValue)
  }

  Surface(
    modifier = modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 2.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      // Top utility status row: Mode, Memory, Scientific toggle, History
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left side: DEG/RAD badge & Memory badge
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          FilterChip(
            selected = angleMode == CalculatorEngine.AngleMode.DEGREE,
            onClick = onToggleAngleMode,
            label = {
              Text(
                text = if (angleMode == CalculatorEngine.AngleMode.DEGREE) "DEG" else "RAD",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
              selectedLabelColor = NeonCyan,
              containerColor = MaterialTheme.colorScheme.surfaceVariant,
              labelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("angle_mode_chip")
          )

          if (hasMemory) {
            Badge(
              containerColor = NeonOrange.copy(alpha = 0.2f),
              contentColor = NeonOrange
            ) {
              Text("M", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
            }
          }
        }

        // Right side: Scientific Toggle, History, Copy
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onToggleScientific,
            modifier = Modifier.testTag("toggle_scientific_button")
          ) {
            Icon(
              imageVector = Icons.Default.Science,
              contentDescription = "Toggle Scientific Keypad",
              tint = if (isScientific) NeonTeal else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          IconButton(
            onClick = onOpenHistory,
            modifier = Modifier.testTag("open_history_button")
          ) {
            Icon(
              imageVector = Icons.Default.History,
              contentDescription = "Calculation History",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          IconButton(
            onClick = {
              val textToCopy = if (isResultDisplayed) resultText else expression.ifEmpty { resultText }
              if (textToCopy.isNotEmpty()) {
                clipboardManager.setText(AnnotatedString(textToCopy))
                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
              }
            },
            modifier = Modifier.testTag("copy_result_button")
          ) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Copy result",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // Main Calculation Area
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 10.dp, bottom = 4.dp),
        horizontalAlignment = Alignment.End
      ) {
        // Expression Line
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
          horizontalArrangement = Arrangement.End
        ) {
          Text(
            text = if (isResultDisplayed) expression else expression.ifEmpty { "0" },
            style = if (isResultDisplayed) {
              MaterialTheme.typography.titleLarge.copy(
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            } else {
              MaterialTheme.typography.displayMedium.copy(
                fontSize = if (expression.length > 12) 34.sp else 44.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurface
              )
            },
            maxLines = 1,
            textAlign = TextAlign.End,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.testTag("calculator_expression_text")
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Result / Live Preview Line
        AnimatedVisibility(
          visible = isResultDisplayed || livePreview.isNotEmpty(),
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          Text(
            text = if (isResultDisplayed) resultText else livePreview,
            style = if (isResultDisplayed) {
              MaterialTheme.typography.displayLarge.copy(
                fontSize = if (resultText.length > 10) 36.sp else 48.sp,
                fontWeight = FontWeight.Normal,
                color = NeonOrange
              )
            } else {
              MaterialTheme.typography.headlineMedium.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.secondary
              )
            },
            maxLines = 1,
            textAlign = TextAlign.End,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.testTag("calculator_result_text")
          )
        }
      }
    }
  }
}
