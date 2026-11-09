package com.example.calculator.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonOrangeDark

enum class CalcButtonType {
  NUMBER,
  OPERATOR,
  FUNCTION,
  SCIENTIFIC,
  EQUALS
}

@Composable
fun CalcButton(
  text: String,
  onClick: () -> Unit,
  type: CalcButtonType,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  fontSize: TextUnit = 24.sp,
  height: Dp = 64.dp,
  testTag: String = "calc_btn_$text"
) {
  val haptic = LocalHapticFeedback.current
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val (containerColor, contentColor) = when (type) {
    CalcButtonType.NUMBER -> {
      MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurface
    }
    CalcButtonType.FUNCTION -> {
      MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    }
    CalcButtonType.OPERATOR -> {
      NeonOrange to Color.White
    }
    CalcButtonType.EQUALS -> {
      NeonOrangeDark to Color.White
    }
    CalcButtonType.SCIENTIFIC -> {
      MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f) to MaterialTheme.colorScheme.tertiary
    }
  }

  Surface(
    onClick = {
      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
      onClick()
    },
    modifier = modifier
      .height(height)
      .testTag(testTag),
    shape = RoundedCornerShape(20.dp),
    color = if (isPressed) containerColor.copy(alpha = 0.8f) else containerColor,
    contentColor = contentColor,
    tonalElevation = if (isPressed) 1.dp else 4.dp,
    shadowElevation = if (type == CalcButtonType.OPERATOR || type == CalcButtonType.EQUALS) 4.dp else 1.dp,
    border = if (type == CalcButtonType.NUMBER) {
      BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
    } else null,
    interactionSource = interactionSource
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 4.dp),
      contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = text,
          modifier = Modifier.size(24.dp)
        )
      } else {
        Text(
          text = text,
          fontSize = fontSize,
          fontWeight = if (type == CalcButtonType.NUMBER) FontWeight.Normal else FontWeight.SemiBold
        )
      }
    }
  }
}
