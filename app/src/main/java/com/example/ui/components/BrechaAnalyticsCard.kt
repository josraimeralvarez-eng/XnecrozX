package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalculationResult
import com.example.model.ConversionDirection
import com.example.ui.theme.BcvBlue
import com.example.ui.theme.BinanceGold
import com.example.ui.theme.SpreadRed
import com.example.ui.theme.SpreadRedContainer
import com.example.viewmodel.CalculatorViewModel

@Composable
fun BrechaAnalyticsCard(
  result: CalculationResult,
  onInfoClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val brechaPct = result.brechaPercent
  val brechaAbs = result.brechaAbsolutaPorDolar
  val diffTotalBs = result.diferenciaTotalBs

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("brecha_analytics_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ),
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
            color = SpreadRed.copy(alpha = 0.15f),
            modifier = Modifier.size(28.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = null,
                tint = SpreadRed,
                modifier = Modifier.size(16.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Brecha Cambiaria (Binance vs BCV)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        IconButton(
          onClick = onInfoClick,
          modifier = Modifier.size(28.dp).testTag("btn_brecha_info")
        ) {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Información sobre la brecha",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Porcentaje de Brecha",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          AnimatedContent(
            targetState = brechaPct,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "brecha_pct_anim"
          ) { pct ->
            Text(
              text = CalculatorViewModel.formatPercent(pct),
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.Black,
              color = if (pct > 0) SpreadRed else MaterialTheme.colorScheme.primary,
              fontSize = 24.sp
            )
          }
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "Diferencia por Dólar",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          AnimatedContent(
            targetState = brechaAbs,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "brecha_abs_anim"
          ) { absVal ->
            Text(
              text = "+${CalculatorViewModel.formatMoney(absVal, 2)} Bs/$",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Visual progress / spread indicator
      val progressRatio = (brechaPct / 50.0).coerceIn(0.0, 1.0).toFloat()
      LinearProgressIndicator(
        progress = { progressRatio },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = SpreadRed,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
      )

      Spacer(modifier = Modifier.height(10.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
      Spacer(modifier = Modifier.height(8.dp))

      // Impact on current amount
      if (result.amountInput > 0) {
        val message = if (result.direction == ConversionDirection.USD_TO_VES) {
          "Por $${CalculatorViewModel.formatMoney(result.amountInput, 2)} USD pagas Bs. ${CalculatorViewModel.formatMoney(diffTotalBs, 2)} más a tasa Binance vs BCV"
        } else {
          "Por Bs. ${CalculatorViewModel.formatMoney(result.amountInput, 2)} obtienes $${CalculatorViewModel.formatMoney(diffTotalBs, 2)} menos en Binance que a tasa BCV"
        }
        Text(
          text = message,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 12.sp,
          lineHeight = 16.sp
        )
      }
    }
  }
}
