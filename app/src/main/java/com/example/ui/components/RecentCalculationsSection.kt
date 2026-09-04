package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CalculationHistory
import com.example.model.ConversionDirection
import com.example.ui.theme.BcvBlue
import com.example.ui.theme.BinanceGold
import com.example.ui.theme.IntervencionPurple
import com.example.ui.theme.SpreadRed
import com.example.viewmodel.CalculatorViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RecentCalculationsSection(
  historyList: List<CalculationHistory>,
  onRestore: (CalculationHistory) -> Unit,
  onDelete: (Long) -> Unit,
  onClearAll: () -> Unit,
  onSaveCurrent: () -> Unit,
  lastSavedNotification: String?,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("section_recent_history"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = CardDefaults.outlinedCardBorder()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            modifier = Modifier.size(32.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Historial Reciente",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer
              ) {
                Text(
                  text = "${historyList.size}/5",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSecondaryContainer,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  fontSize = 11.sp
                )
              }
            }
            Text(
              text = "Últimos 5 cálculos almacenados en Room",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 11.sp
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (historyList.isNotEmpty()) {
            TextButton(
              onClick = onClearAll,
              modifier = Modifier.testTag("btn_clear_history")
            ) {
              Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.error
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Borrar",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
              )
            }
          }
        }
      }

      // Notification banner if saved
      AnimatedVisibility(
        visible = lastSavedNotification != null,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        if (lastSavedNotification != null) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.BookmarkBorder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = lastSavedNotification,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
              )
            }
          }
        }
      }

      if (historyList.isEmpty()) {
        // Empty State
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 20.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.History,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
              modifier = Modifier.size(32.dp)
            )
            Text(
              text = "Sin historial de cálculos",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Tus últimas 5 conversiones se guardarán automáticamente aquí.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 12.sp
            )
          }
        }
      } else {
        // History List (Up to 5)
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          historyList.forEachIndexed { index, item ->
            HistoryItemCard(
              item = item,
              itemNumber = index + 1,
              onRestore = { onRestore(item) },
              onDelete = { onDelete(item.id) }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun HistoryItemCard(
  item: CalculationHistory,
  itemNumber: Int,
  onRestore: () -> Unit,
  onDelete: () -> Unit
) {
  val isUsdToVes = item.parsedDirection == ConversionDirection.USD_TO_VES
  val formattedTime = formatTimestamp(item.timestamp)

  val inputCurrencySymbol = if (isUsdToVes) "USD" else "VES"
  val inputPrefix = if (isUsdToVes) "$" else "Bs."
  val outputPrefix = if (isUsdToVes) "Bs." else "$"

  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
    ),
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .clickable { onRestore() }
      .testTag("history_item_$itemNumber")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Top row: Index badge + Timestamp + Direction badge + Delete button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = "$itemNumber",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Text(
            text = formattedTime,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp
          )

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (isUsdToVes) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.tertiaryContainer
          ) {
            Text(
              text = if (isUsdToVes) "$ → Bs." else "Bs. → $",
              style = MaterialTheme.typography.labelSmall,
              color = if (isUsdToVes) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onTertiaryContainer,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "Tocar para cargar",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 11.sp
          )
          Spacer(modifier = Modifier.width(4.dp))
          IconButton(
            onClick = onDelete,
            modifier = Modifier
              .size(26.dp)
              .testTag("btn_delete_history_${item.id}")
          ) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = "Eliminar cálculo",
              modifier = Modifier.size(16.dp),
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
          }
        }
      }

      // Input amount callout
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Monto:",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = "$inputPrefix ${CalculatorViewModel.formatMoney(item.amount, 2)} $inputCurrencySymbol",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      HorizontalDivider(
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
        thickness = 1.dp
      )

      // 3 Results comparison grid
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // BCV
        HistoryRateColumn(
          title = "BCV Oficial",
          rate = "${CalculatorViewModel.formatRate(item.tasaBcv)} Bs",
          total = "$outputPrefix ${CalculatorViewModel.formatMoney(item.totalBcv, 2)}",
          accentColor = BcvBlue,
          modifier = Modifier.weight(1f)
        )

        // Intervención
        HistoryRateColumn(
          title = "Intervención (+0.5%)",
          rate = "${CalculatorViewModel.formatRate(item.tasaIntervencion)} Bs",
          total = "$outputPrefix ${CalculatorViewModel.formatMoney(item.totalIntervencion, 2)}",
          accentColor = IntervencionPurple,
          modifier = Modifier.weight(1f)
        )

        // Binance
        HistoryRateColumn(
          title = "Binance (${item.binanceMode.take(1).uppercase() + item.binanceMode.drop(1).lowercase()})",
          rate = "${CalculatorViewModel.formatRate(item.tasaBinance)} Bs",
          total = "$outputPrefix ${CalculatorViewModel.formatMoney(item.totalBinance, 2)}",
          accentColor = BinanceGold,
          badge = CalculatorViewModel.formatPercent(item.brechaPercent),
          badgeColor = SpreadRed,
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
private fun HistoryRateColumn(
  title: String,
  rate: String,
  total: String,
  accentColor: Color,
  badge: String? = null,
  badgeColor: Color = MaterialTheme.colorScheme.primary,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.padding(horizontal = 2.dp),
    verticalArrangement = Arrangement.spacedBy(2.dp)
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.labelSmall,
      color = accentColor,
      fontWeight = FontWeight.SemiBold,
      fontSize = 10.sp,
      maxLines = 1
    )
    Text(
      text = total,
      style = MaterialTheme.typography.labelMedium,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface,
      fontSize = 12.sp,
      maxLines = 1
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = rate,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 9.sp,
        maxLines = 1
      )
      if (badge != null) {
        Spacer(modifier = Modifier.width(3.dp))
        Surface(
          shape = RoundedCornerShape(3.dp),
          color = badgeColor.copy(alpha = 0.15f)
        ) {
          Text(
            text = badge,
            style = MaterialTheme.typography.labelSmall,
            color = badgeColor,
            fontWeight = FontWeight.Bold,
            fontSize = 8.sp,
            modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
          )
        }
      }
    }
  }
}

private fun formatTimestamp(timestamp: Long): String {
  val now = System.currentTimeMillis()
  val diffMillis = now - timestamp

  return when {
    diffMillis < 60_000 -> "Hace un momento"
    diffMillis < 3600_000 -> "Hace ${diffMillis / 60_000}m"
    diffMillis < 86400_000 -> {
      val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
      "Hoy ${timeFormat.format(Date(timestamp))}"
    }
    else -> {
      val dateFormat = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
      dateFormat.format(Date(timestamp))
    }
  }
}
