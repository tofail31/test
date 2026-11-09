package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calculator.ui.AppScreenTab
import com.example.calculator.ui.CalculatorViewModel
import com.example.calculator.ui.screens.CalculatorScreen
import com.example.calculator.ui.screens.TipCalculatorScreen
import com.example.calculator.ui.screens.UnitConverterScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonOrange

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainAppContent()
      }
    }
  }
}

@Composable
fun MainAppContent(viewModel: CalculatorViewModel = viewModel()) {
  val uiState by viewModel.uiState.collectAsState()

  // Handle back button when not on main calculator tab
  if (uiState.activeTab != AppScreenTab.CALCULATOR) {
    BackHandler {
      viewModel.setTab(AppScreenTab.CALCULATOR)
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    contentWindowInsets = WindowInsets.safeDrawing,
    bottomBar = {
      NavigationBar(
        tonalElevation = 6.dp,
        windowInsets = WindowInsets.navigationBars,
        modifier = Modifier.testTag("main_navigation_bar")
      ) {
        NavigationBarItem(
          selected = uiState.activeTab == AppScreenTab.CALCULATOR,
          onClick = { viewModel.setTab(AppScreenTab.CALCULATOR) },
          icon = { Icon(Icons.Default.Calculate, contentDescription = "Calculator") },
          label = { Text("Calculator") },
          colors = NavigationBarItemDefaults.colors(
            indicatorColor = NeonOrange.copy(alpha = 0.2f),
            selectedIconColor = NeonOrange,
            selectedTextColor = NeonOrange
          ),
          modifier = Modifier.testTag("nav_item_calculator")
        )

        NavigationBarItem(
          selected = uiState.activeTab == AppScreenTab.CONVERTER,
          onClick = { viewModel.setTab(AppScreenTab.CONVERTER) },
          icon = { Icon(Icons.Default.SwapHoriz, contentDescription = "Unit Converter") },
          label = { Text("Converter") },
          colors = NavigationBarItemDefaults.colors(
            indicatorColor = NeonOrange.copy(alpha = 0.2f),
            selectedIconColor = NeonOrange,
            selectedTextColor = NeonOrange
          ),
          modifier = Modifier.testTag("nav_item_converter")
        )

        NavigationBarItem(
          selected = uiState.activeTab == AppScreenTab.TIP,
          onClick = { viewModel.setTab(AppScreenTab.TIP) },
          icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Tip & Split") },
          label = { Text("Tip & Split") },
          colors = NavigationBarItemDefaults.colors(
            indicatorColor = NeonOrange.copy(alpha = 0.2f),
            selectedIconColor = NeonOrange,
            selectedTextColor = NeonOrange
          ),
          modifier = Modifier.testTag("nav_item_tip")
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (uiState.activeTab) {
        AppScreenTab.CALCULATOR -> CalculatorScreen(viewModel, uiState)
        AppScreenTab.CONVERTER -> UnitConverterScreen(viewModel, uiState)
        AppScreenTab.TIP -> TipCalculatorScreen(viewModel, uiState)
      }
    }
  }
}
