package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AmountInputSection
import com.example.ui.components.BrechaAnalyticsCard
import com.example.ui.components.EditRatesDialog
import com.example.ui.components.InfoExplainerDialog
import com.example.ui.components.RatesTickerBanner
import com.example.ui.components.ResultsSection
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import com.example.ui.components.CalculationHistoryDialog
import com.example.ui.components.RecentCalculationsSection
import com.example.ui.components.ShareQuoteDialog
import com.example.viewmodel.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
  viewModel: CalculatorViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val recentHistory by viewModel.recentHistory.collectAsStateWithLifecycle()

  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
              modifier = Modifier.size(36.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.CurrencyExchange,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Calculadora Cambiaria",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "BCV • Intervención • Binance",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
              )
            }
          }
        },
        actions = {
          IconButton(
            onClick = { viewModel.openHistoryDialog() },
            modifier = Modifier.testTag("btn_top_history")
          ) {
            BadgedBox(
              badge = {
                if (recentHistory.isNotEmpty()) {
                  Badge {
                    Text(text = recentHistory.size.toString())
                  }
                }
              }
            ) {
              Icon(
                imageVector = Icons.Default.History,
                contentDescription = "Historial de cálculos",
                tint = if (recentHistory.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
          IconButton(
            onClick = { viewModel.openInfoDialog() },
            modifier = Modifier.testTag("btn_top_info")
          ) {
            Icon(
              imageVector = Icons.Default.HelpOutline,
              contentDescription = "Guía explicativa",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          IconButton(
            onClick = { viewModel.openEditRatesDialog() },
            modifier = Modifier.testTag("btn_top_edit_rates")
          ) {
            Icon(
              imageVector = Icons.Default.Tune,
              contentDescription = "Ajustar tasas",
              tint = MaterialTheme.colorScheme.primary
            )
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.background
        )
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = { viewModel.openShareDialog() },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = CircleShape,
        modifier = Modifier
          .padding(8.dp)
          .shadow(6.dp, CircleShape)
          .testTag("fab_share_quote")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Compartir cotización"
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Compartir",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentAlignment = Alignment.TopCenter
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .widthIn(max = 600.dp)
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // 1. Rates Ticker Banner
        RatesTickerBanner(
          rates = uiState.rates,
          binanceMode = uiState.binanceMode,
          onEditClick = { viewModel.openEditRatesDialog() }
        )

        // 2. Amount Input Section
        AmountInputSection(
          amount = uiState.amountInput,
          direction = uiState.direction,
          onAmountChange = { viewModel.updateAmount(it) },
          onDirectionToggle = { viewModel.toggleDirection() },
          onQuickAmountSelect = { viewModel.setQuickAmount(it) },
          onClear = { viewModel.clearAmount() },
          onBackspace = { viewModel.backspace() }
        )

        // 3. Results Section (BCV, Intervención, Binance)
        ResultsSection(
          result = uiState.result,
          selectedBinanceMode = uiState.binanceMode,
          onBinanceModeChange = { viewModel.setBinanceMode(it) }
        )

        // 4. Brecha Analytics & Spread Card
        BrechaAnalyticsCard(
          result = uiState.result,
          onInfoClick = { viewModel.openInfoDialog() }
        )

        // 5. Recent Calculations (Last 5 stored in Room Database)
        RecentCalculationsSection(
          historyList = recentHistory,
          onRestore = { viewModel.restoreCalculation(it) },
          onDelete = { viewModel.deleteHistoryItem(it) },
          onClearAll = { viewModel.clearAllHistory() },
          onSaveCurrent = { viewModel.saveCurrentCalculationNow() },
          lastSavedNotification = uiState.lastSavedNotification
        )

        // Extra spacing at bottom to prevent FAB overlap
        Spacer(modifier = Modifier.height(72.dp))
      }
    }
  }

  // Dialogs
  if (uiState.showHistoryDialog) {
    CalculationHistoryDialog(
      historyList = recentHistory,
      onRestore = { viewModel.restoreCalculation(it) },
      onDelete = { viewModel.deleteHistoryItem(it) },
      onClearAll = { viewModel.clearAllHistory() },
      onDismiss = { viewModel.closeHistoryDialog() }
    )
  }

  if (uiState.showEditRatesDialog) {
    EditRatesDialog(
      currentRates = uiState.rates,
      onDismiss = { viewModel.closeEditRatesDialog() },
      onSave = { bcv, bVenta, bCompra, margin ->
        viewModel.updateRates(bcv, bVenta, bCompra, margin)
      },
      onResetDefaults = {
        viewModel.resetRatesToDefault()
      }
    )
  }

  if (uiState.showShareDialog) {
    ShareQuoteDialog(
      formattedSummary = viewModel.generateFormattedSummary(),
      onDismiss = { viewModel.closeShareDialog() }
    )
  }

  if (uiState.showInfoDialog) {
    InfoExplainerDialog(
      onDismiss = { viewModel.closeInfoDialog() }
    )
  }
}
