package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExchangeRates
import com.example.ui.theme.BcvBlue
import com.example.ui.theme.BinanceGold
import com.example.ui.theme.IntervencionPurple

@Composable
fun EditRatesDialog(
  currentRates: ExchangeRates,
  onDismiss: () -> Unit,
  onSave: (Double, Double, Double, Double) -> Unit,
  onResetDefaults: () -> Unit
) {
  var bcvText by remember { mutableStateOf(currentRates.tasaBcv.toString()) }
  var binanceVentaText by remember { mutableStateOf(currentRates.tasaBinanceVenta.toString()) }
  var binanceCompraText by remember { mutableStateOf(currentRates.tasaBinanceCompra.toString()) }
  var intervencionMarginText by remember { mutableStateOf(currentRates.intervencionMarginPercent.toString()) }

  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("dialog_edit_rates"),
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Ajustar Tasas",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )
        TextButton(
          onClick = {
            onResetDefaults()
            onDismiss()
          },
          modifier = Modifier.testTag("btn_reset_rates")
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "Defecto", fontSize = 12.sp)
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Text(
          text = "Modifica los valores de las tasas para reflejar los precios actuales del mercado:",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // 1. Tasa BCV
        RateEditItem(
          label = "Tasa BCV Oficial (Bs/USD)",
          valueText = bcvText,
          onValueChange = { bcvText = it },
          accentColor = BcvBlue,
          onStep = { delta ->
            val cur = bcvText.toDoubleOrNull() ?: 0.0
            val next = (cur + delta).coerceAtLeast(1.0)
            bcvText = String.format(java.util.Locale.US, "%.3f", next)
          },
          testTag = "input_edit_bcv"
        )

        // 2. Tasa Binance Venta
        RateEditItem(
          label = "Tasa Binance Venta (Bs/USD)",
          valueText = binanceVentaText,
          onValueChange = { binanceVentaText = it },
          accentColor = BinanceGold,
          onStep = { delta ->
            val cur = binanceVentaText.toDoubleOrNull() ?: 0.0
            val next = (cur + delta).coerceAtLeast(1.0)
            binanceVentaText = String.format(java.util.Locale.US, "%.2f", next)
          },
          testTag = "input_edit_binance_venta"
        )

        // 3. Tasa Binance Compra
        RateEditItem(
          label = "Tasa Binance Compra (Bs/USD)",
          valueText = binanceCompraText,
          onValueChange = { binanceCompraText = it },
          accentColor = BinanceGold,
          onStep = { delta ->
            val cur = binanceCompraText.toDoubleOrNull() ?: 0.0
            val next = (cur + delta).coerceAtLeast(1.0)
            binanceCompraText = String.format(java.util.Locale.US, "%.2f", next)
          },
          testTag = "input_edit_binance_compra"
        )

        // 4. Margen Intervención (+0.5%)
        RateEditItem(
          label = "Margen Intervención (%)",
          valueText = intervencionMarginText,
          onValueChange = { intervencionMarginText = it },
          accentColor = IntervencionPurple,
          stepAmount = 0.1,
          onStep = { delta ->
            val cur = intervencionMarginText.toDoubleOrNull() ?: 0.0
            val next = (cur + delta).coerceAtLeast(0.0)
            intervencionMarginText = String.format(java.util.Locale.US, "%.2f", next)
          },
          testTag = "input_edit_intervencion_margin"
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val bcv = bcvText.replace(',', '.').toDoubleOrNull() ?: currentRates.tasaBcv
          val bVenta = binanceVentaText.replace(',', '.').toDoubleOrNull() ?: currentRates.tasaBinanceVenta
          val bCompra = binanceCompraText.replace(',', '.').toDoubleOrNull() ?: currentRates.tasaBinanceCompra
          val margin = intervencionMarginText.replace(',', '.').toDoubleOrNull() ?: currentRates.intervencionMarginPercent
          onSave(bcv, bVenta, bCompra, margin)
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.testTag("btn_save_rates")
      ) {
        Text("Guardar Cambios", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Cancelar")
      }
    }
  )
}

@Composable
private fun RateEditItem(
  label: String,
  valueText: String,
  onValueChange: (String) -> Unit,
  accentColor: Color,
  stepAmount: Double = 1.0,
  onStep: (Double) -> Unit,
  testTag: String
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
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = accentColor
      )
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        FilledTonalIconButton(
          onClick = { onStep(-stepAmount) },
          modifier = Modifier.size(38.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Remove,
            contentDescription = "Disminuir",
            modifier = Modifier.size(18.dp)
          )
        }

        OutlinedTextField(
          value = valueText,
          onValueChange = onValueChange,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          modifier = Modifier
            .weight(1f)
            .testTag(testTag),
          shape = RoundedCornerShape(8.dp),
          textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )

        FilledTonalIconButton(
          onClick = { onStep(stepAmount) },
          modifier = Modifier.size(38.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Aumentar",
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}
