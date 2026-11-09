package com.example.calculator.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HistoryItem(
  val id: Long = System.currentTimeMillis() + (0..999).random(),
  val expression: String,
  val result: String,
  val timestamp: Long = System.currentTimeMillis()
) {
  val formattedTime: String
    get() {
      val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
      return sdf.format(Date(timestamp))
    }

  val formattedDate: String
    get() {
      val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
      return sdf.format(Date(timestamp))
    }
}
