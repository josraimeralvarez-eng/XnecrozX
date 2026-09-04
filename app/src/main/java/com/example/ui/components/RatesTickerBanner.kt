package com.example.ui.components

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
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BinanceMode
import com.example.model.ExchangeRates
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.BcvBlue
import com.example.ui.theme.BinanceGold
import com.example.ui.theme.IntervencionPurple
import com.example.viewmodel.CalculatorViewModel

@Composable
fun RatesTickerBanner(
  rates: ExchangeRates,
  binanceMode: BinanceMode,
  onEditClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("rates_ticker_banner"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    ),
    border = CardDefaults.outlinedCardBorder()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(Color(0xFF10B981))
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Tasas de Cambio Activas",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onEditClick)
            .testTag("btn_edit_rates_banner")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Editar tasas",
              modifier = Modifier.size(14.dp),
              tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Ajustar",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        MiniRatePill(
          title = "BCV",
          rate = CalculatorViewModel.formatRate(rates.tasaBcv),
          badge = "Oficial",
          accentColor = BcvBlue,
          modifier = Modifier.weight(1f)
        )
        MiniRatePill(
          title = "Intervención",
          rate = CalculatorViewModel.formatRate(rates.tasaIntervencion),
          badge = "+${rates.intervencionMarginPercent}%",
          accentColor = IntervencionPurple,
          modifier = Modifier.weight(1f)
        )
        MiniRatePill(
          title = "Binance",
          rate = CalculatorViewModel.formatRate(rates.getBinanceRate(binanceMode)),
          badge = binanceMode.shortLabel,
          accentColor = BinanceGold,
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
private fun MiniRatePill(
  title: String,
  rate: String,
  badge: String,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 2.dp
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 11.sp
        )
      }

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = rate,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        color = accentColor,
        fontSize = 13.sp
      )

      Spacer(modifier = Modifier.height(2.dp))

      Surface(
        shape = RoundedCornerShape(4.dp),
        color = accentColor.copy(alpha = 0.15f)
      ) {
        Text(
          text = badge,
          style = MaterialTheme.typography.labelSmall,
          color = accentColor,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
        )
      }
    }
  }
}
