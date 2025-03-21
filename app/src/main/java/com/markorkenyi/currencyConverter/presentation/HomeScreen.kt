package com.markorkenyi.currencyConverter.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.markorkenyi.currencyConverter.network.CurrencyRepository
import com.markorkenyi.currencyConverter.presentation.theme.WearAppTheme


@Composable
fun HomeScreenContent(
    currencies: Map<String, String>,
    baseCurrency: String,
    destCurrency: String,
    onBaseClick: (String) -> Unit,
    onDestClick: (String) -> Unit,
    onConvertClick: (String, String) -> Unit
) {
    // Define your allowed currencies list (in lowercase).
    val allowedCurrencies = listOf("usd", "eur", "nok", "huf", "sek")
    // Filter the keys from your currencies map.
    val filteredCurrencies = currencies.keys.filter { it in allowedCurrencies }.sorted()

    // Exclude the destination selection from the base list and vice versa.
    val baseCurrencyList = filteredCurrencies.filter { it != destCurrency }
    val destCurrencyList = filteredCurrencies.filter { it != baseCurrency }

    WearAppTheme {
        // Use the official Wear Scaffold.
        Scaffold(
            vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
            positionIndicator = { /* Optional scroll indicator */ }
        ) {
            // Wear Scaffold doesn't provide content padding so we simply fill the space.
            Box(modifier = Modifier.fillMaxSize()) {
                // Center the row of currency lists at the top.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.TopCenter)
                        .padding(top = 30.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Left: Base currency list.
                        LazyColumn(
                            modifier = Modifier
                                .widthIn(max = 80.dp)
                                .heightIn(max = 110.dp)
                                .padding(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(items = baseCurrencyList) { currencyCode ->
                                Text(
                                    text = currencyCode.uppercase(),
                                    modifier = Modifier
                                        .clickable {
                                            // Toggle de-selection: if clicked currency equals current selection, de-select it.
                                            if (currencyCode == baseCurrency) onBaseClick("")
                                            else onBaseClick(currencyCode)
                                        }
                                        .padding(4.dp),
                                    style = if (currencyCode == baseCurrency)
                                        MaterialTheme.typography.bodyLarge.copy(
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    else
                                        MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                        // Right: Destination currency list.
                        LazyColumn(
                            modifier = Modifier
                                .widthIn(max = 80.dp)
                                .heightIn(max = 110.dp)
                                .padding(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(items = destCurrencyList) { currencyCode ->
                                Text(
                                    text = currencyCode.uppercase(),
                                    modifier = Modifier
                                        .clickable {
                                            if (currencyCode == destCurrency) onDestClick("")
                                            else onDestClick(currencyCode)
                                        }
                                        .padding(4.dp),
                                    style = if (currencyCode == destCurrency)
                                        MaterialTheme.typography.bodyLarge.copy(
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    else
                                        MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
                // Convert button at the bottom center.
                Button(
                    onClick = { onConvertClick(baseCurrency, destCurrency) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                ) {
                    Text("Convert")
                }
            }
        }
    }
}


@Composable
fun HomeScreen(onConvertClick: (base: String, dest: String) -> Unit) {
    // Default selections
    var baseCurrency by remember { mutableStateOf("USD") }
    var destCurrency by remember { mutableStateOf("EUR") }
    // Hold the currencies map; start empty
    var currencies by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    // Fetch currencies from the API if not in preview
    LaunchedEffect(Unit) {
        try {
            // For production, use your repository:
            currencies = CurrencyRepository().getCurrencies()
        } catch (e: Exception) {
            // Handle the error appropriately
        }
    }

    HomeScreenContent(
        currencies = currencies,
        baseCurrency = baseCurrency,
        destCurrency = destCurrency,
        onBaseClick = { selected -> baseCurrency = selected },
        onDestClick = { selected -> destCurrency = selected },
        onConvertClick = onConvertClick
    )
}

@WearPreviewDevices
@Composable
fun PreviewHomeScreenContent() {
    // Dummy currencies map
    val dummyCurrencies = mapOf(
        "usd" to "United States Dollar",
        "eur" to "Euro",
        "nok" to "Norwegian Krone",
        "huf" to "Hungarian Forint",
        "jpy" to "Japanese Yen",
        "gbp" to "British Pound"
    )

    // For preview, we provide fixed values for base and destination currencies.
    HomeScreenContent(
        currencies = dummyCurrencies,
        baseCurrency = "USD",
        destCurrency = "EUR",
        onBaseClick = {},
        onDestClick = {},
        onConvertClick = { _, _ -> }
    )
}
