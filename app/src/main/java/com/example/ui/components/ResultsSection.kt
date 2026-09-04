package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BinanceMode
import com.example.model.CalculationResult
import com.example.model.ConversionDirection
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.BcvBlue
import com.example.ui.theme.BinanceGold
import com.example.ui.theme.IntervencionPurple
import com.example.viewmodel.CalculatorViewModel

@Composable
fun ResultsSection(
  result: CalculationResult,
  selectedBinanceMode: BinanceMode,
  onBinanceModeChange: (BinanceMode) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Text(
      text = if (result.direction == ConversionDirection.USD_TO_VES) "Total a cobrar / pagar en Bolívares:" else "Total equivalente en Dólares:",
      style = MaterialTheme.typography.titleSmall,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onBackground
    )

    // 1. CARD BCV OFICIAL
    ResultCard(
      title = "Tasa BCV Oficial",
      badgeText = "Banco Central",
      icon = Icons.Default.AccountBalance,
      themeColor = BcvBlue,
      rateSubtitle = "1 USD = ${CalculatorViewModel.formatRate(result.rates.tasaBcv)} Bs.",
      symbol = result.direction.toSymbol,
      totalAmount = CalculatorViewModel.formatMoney(result.totalBcv, 2),
      testTag = "card_result_bcv",
      onCopy = {
        copyToClipboard(
          context,
          "Total BCV (${result.direction.toSymbol} ${CalculatorViewModel.formatMoney(result.totalBcv, 2)})",
          CalculatorViewModel.formatMoney(result.totalBcv, 2)
        )
      }
    )

    // 2. CARD INTERVENCIÓN BANCARIA (+0.5%)
    ResultCard(
      title = "Tasa Intervención (+${result.rates.intervencionMarginPercent}%)",
      badgeText = "Bancos Nacionales",
      icon = Icons.Default.LocalAtm,
      themeColor = IntervencionPurple,
      rateSubtitle = "1 USD = ${CalculatorViewModel.formatRate(result.rates.tasaIntervencion)} Bs. (BCV + ${result.rates.intervencionMarginPercent}%)",
      symbol = result.direction.toSymbol,
      totalAmount = CalculatorViewModel.formatMoney(result.totalIntervencion, 2),
      testTag = "card_result_intervencion",
      onCopy = {
        copyToClipboard(
          context,
          "Total Intervención (${result.direction.toSymbol} ${CalculatorViewModel.formatMoney(result.totalIntervencion, 2)})",
          CalculatorViewModel.formatMoney(result.totalIntervencion, 2)
        )
      }
    )

    // 3. CARD BINANCE P2P
    BinanceResultCard(
      result = result,
      selectedBinanceMode = selectedBinanceMode,
      onBinanceModeChange = onBinanceModeChange,
      testTag = "card_result_binance",
      onCopy = {
        copyToClipboard(
          context,
          "Total Binance ${selectedBinanceMode.label} (${result.direction.toSymbol} ${CalculatorViewModel.formatMoney(result.totalBinance, 2)})",
          CalculatorViewModel.formatMoney(result.totalBinance, 2)
        )
      }
    )
  }
}

@Composable
fun ResultCard(
  title: String,
  badgeText: String,
  icon: ImageVector,
  themeColor: Color,
  rateSubtitle: String,
  symbol: String,
  totalAmount: String,
  testTag: String,
  onCopy: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag(testTag),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = CardDefaults.outlinedCardBorder()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = CircleShape,
            color = themeColor.copy(alpha = 0.15f),
            modifier = Modifier.size(32.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = icon,
                contentDescription = null,
                tint = themeColor,
                modifier = Modifier.size(18.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = title,
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = rateSubtitle,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 11.sp
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = themeColor.copy(alpha = 0.12f)
        ) {
          Text(
            text = badgeText,
            style = MaterialTheme.typography.labelSmall,
            color = themeColor,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Text(
            text = "$symbol ",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = themeColor
          )
          Text(
            text = totalAmount,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 24.sp
          )
        }

        IconButton(
          onClick = onCopy,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "Copiar resultado",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

@Composable
fun BinanceResultCard(
  result: CalculationResult,
  selectedBinanceMode: BinanceMode,
  onBinanceModeChange: (BinanceMode) -> Unit,
  testTag: String,
  onCopy: () -> Unit,
  modifier: Modifier = Modifier
) {
  val themeColor = BinanceGold

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag(testTag),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = CardDefaults.outlinedCardBorder()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = CircleShape,
            color = themeColor.copy(alpha = 0.15f),
            modifier = Modifier.size(32.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.CurrencyBitcoin,
                contentDescription = null,
                tint = themeColor,
                modifier = Modifier.size(18.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "Tasa Binance (P2P)",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            val currentRate = result.rates.getBinanceRate(selectedBinanceMode)
            Text(
              text = "1 USD = ${CalculatorViewModel.formatRate(currentRate)} Bs. (${selectedBinanceMode.label})",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 11.sp
            )
          }
        }

        // Toggle Venta / Compra
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ) {
          Row(modifier = Modifier.padding(2.dp)) {
            BinanceMode.entries.forEach { mode ->
              val isSelected = mode == selectedBinanceMode
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) themeColor else Color.Transparent,
                modifier = Modifier
                  .clip(RoundedCornerShape(16.dp))
                  .clickable { onBinanceModeChange(mode) }
              ) {
                Text(
                  text = mode.label,
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 11.sp,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Text(
            text = "${result.direction.toSymbol} ",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = themeColor
          )
          AnimatedContent(
            targetState = result.totalBinance,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "binance_total_anim"
          ) { total ->
            Text(
              text = CalculatorViewModel.formatMoney(total, 2),
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.Black,
              color = MaterialTheme.colorScheme.onSurface,
              fontSize = 24.sp
            )
          }
        }

        IconButton(
          onClick = onCopy,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "Copiar resultado",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

private fun copyToClipboard(context: Context, label: String, text: String) {
  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
  val clip = ClipData.newPlainText(label, text)
  clipboard.setPrimaryClip(clip)
  Toast.makeText(context, "Copiado: $text", Toast.LENGTH_SHORT).show()
}
