package com.example.calculator.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.model.HistoryItem
import com.example.ui.theme.NeonOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryBottomSheet(
  history: List<HistoryItem>,
  onSelectItem: (HistoryItem) -> Unit,
  onClearHistory: () -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    modifier = Modifier.testTag("history_bottom_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp)
        .navigationBarsPadding()
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = NeonOrange
          )
          Text(
            text = "Calculation History",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )
        }

        if (history.isNotEmpty()) {
          TextButton(
            onClick = onClearHistory,
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.testTag("clear_history_button")
          ) {
            Icon(
              imageVector = Icons.Default.DeleteSweep,
              contentDescription = "Clear All",
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Clear")
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      if (history.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Default.History,
              contentDescription = null,
              modifier = Modifier.size(48.dp),
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "No history yet",
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "Calculations will appear here",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 420.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(history, key = { it.id }) { item ->
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
              ),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectItem(item) }
                .testTag("history_item_${item.id}")
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = item.expression,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Text(
                    text = item.formattedTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "= ${item.result}",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonOrange
                  )
                )
              }
            }
          }
        }
      }
    }
  }
}
