package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BcvBlue
import com.example.ui.theme.BinanceGold
import com.example.ui.theme.IntervencionPurple
import com.example.ui.theme.SpreadRed

@Composable
fun InfoExplainerDialog(onDismiss: () -> Unit) {
  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("dialog_info_explainer"),
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.HelpOutline,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Guía de Variables Cambiarias",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        ExplainerCard(
          icon = Icons.Default.AccountBalance,
          color = BcvBlue,
          title = "1. Tasa BCV Oficial",
          description = "Es la tasa de cambio de referencia publicada diariamente por el Banco Central de Venezuela, calculada mediante el promedio ponderado de las mesas de cambio de la banca nacional."
        )

        ExplainerCard(
          icon = Icons.Default.LocalAtm,
          color = IntervencionPurple,
          title = "2. Tasa de Intervención (+0.5%)",
          description = "Corresponde a la tasa aplicada en operaciones bancarias e intervención cambiaria cuando se aplica la comisión financiera bancaria del 0.5% (Tasa BCV × 1.005)."
        )

        ExplainerCard(
          icon = Icons.Default.CurrencyBitcoin,
          color = BinanceGold,
          title = "3. Tasa Binance (P2P)",
          description = "Es la cotización en el mercado libre P2P (USDT a Bolívares). Se divide en Venta (cuando vendes USDT para recibir Bs.) y Compra (cuando pagas Bs. para comprar USDT)."
        )

        ExplainerCard(
          icon = Icons.Default.TrendingUp,
          color = SpreadRed,
          title = "4. Brecha Cambiaria",
          description = "Indica el porcentaje de sobreprecio entre el mercado libre P2P de Binance y la tasa oficial del BCV: ((Tasa Binance - Tasa BCV) / Tasa BCV) × 100."
        )
      }
    },
    confirmButton = {
      Button(onClick = onDismiss) {
        Text("Entendido", fontWeight = FontWeight.Bold)
      }
    }
  )
}

@Composable
private fun ExplainerCard(
  icon: ImageVector,
  color: Color,
  title: String,
  description: String
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = CardDefaults.outlinedCardBorder()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = color.copy(alpha = 0.15f),
          modifier = Modifier.size(24.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
          }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = title,
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
          color = color
        )
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 12.sp,
        lineHeight = 16.sp
      )
    }
  }
}
