package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConversionDirection
import com.example.ui.theme.AmberPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmountInputSection(
  amount: String,
  direction: ConversionDirection,
  onAmountChange: (String) -> Unit,
  onDirectionToggle: () -> Unit,
  onQuickAmountSelect: (Double) -> Unit,
  onClear: () -> Unit,
  onBackspace: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("amount_input_card"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Header with direction and toggle button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Convertir desde",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          AnimatedContent(
            targetState = direction,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "direction_text"
          ) { dir ->
            Text(
              text = dir.fromName,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }

        FilledTonalIconButton(
          onClick = onDirectionToggle,
          modifier = Modifier
            .size(44.dp)
            .testTag("btn_toggle_direction")
        ) {
          Icon(
            imageVector = Icons.Default.SwapVert,
            contentDescription = "Cambiar dirección de conversión",
            tint = MaterialTheme.colorScheme.primary
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Amount Input Field
      OutlinedTextField(
        value = amount,
        onValueChange = onAmountChange,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_amount"),
        textStyle = TextStyle(
          fontSize = 28.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        ),
        placeholder = {
          Text(
            text = "0.00",
            style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
          )
        },
        leadingIcon = {
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            modifier = Modifier.padding(start = 12.dp, end = 4.dp)
          ) {
            Text(
              text = direction.fromSymbol,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Black,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }
        },
        trailingIcon = {
          if (amount.isNotEmpty()) {
            Row(modifier = Modifier.padding(end = 4.dp)) {
              IconButton(
                onClick = onBackspace,
                modifier = Modifier.size(36.dp).testTag("btn_backspace")
              ) {
                Icon(
                  imageVector = Icons.Default.Backspace,
                  contentDescription = "Borrar último dígito",
                  modifier = Modifier.size(20.dp),
                  tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              IconButton(
                onClick = onClear,
                modifier = Modifier.size(36.dp).testTag("btn_clear")
              ) {
                Icon(
                  imageVector = Icons.Default.Clear,
                  contentDescription = "Limpiar monto",
                  modifier = Modifier.size(20.dp),
                  tint = MaterialTheme.colorScheme.error
                )
              }
            }
          }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = MaterialTheme.colorScheme.primary,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
          focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
          unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        )
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Quick amount chips
      Text(
        text = "Montos rápidos sugeridos:",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(6.dp))

      val quickAmounts = if (direction == ConversionDirection.USD_TO_VES) {
        listOf(10.0, 20.0, 50.0, 100.0, 200.0, 500.0, 1000.0)
      } else {
        listOf(100.0, 500.0, 1000.0, 5000.0, 10000.0, 50000.0)
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        quickAmounts.forEach { quickVal ->
          val label = if (direction == ConversionDirection.USD_TO_VES) {
            "$$${quickVal.toInt()}"
          } else {
            "Bs. ${quickVal.toInt()}"
          }
          FilterChip(
            selected = amount == quickVal.toInt().toString(),
            onClick = { onQuickAmountSelect(quickVal) },
            label = {
              Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
              )
            },
            shape = RoundedCornerShape(10.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primary,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
              containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              labelColor = MaterialTheme.colorScheme.onSurface
            ),
            border = null,
            modifier = Modifier.height(32.dp)
          )
        }
      }
    }
  }
}
