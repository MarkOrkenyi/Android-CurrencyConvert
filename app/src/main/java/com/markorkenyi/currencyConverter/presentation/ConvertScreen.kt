package com.markorkenyi.currencyConverter.presentation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.markorkenyi.currencyConverter.presentation.theme.WearAppTheme
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


class StartupViewModel : ViewModel() {

    var exchangeRate = mutableStateOf(0.0f)
        private set
    var baseCurrency by mutableStateOf("NOK")
    var destCurrency by mutableStateOf("HUF")
    init {
        fetchRates()
    }

    private fun fetchRates() {
        viewModelScope.launch {
            try {
                // Convert baseCurrency to lowercase because the API returns e.g. "eur"
                val response = RetrofitInstance.api.getRates(baseCurrency.lowercase())
                // Lookup the destination rate using lowercase key, if applicable
                exchangeRate.value = (response.rates[destCurrency.lowercase()] ?: 0.0f).toFloat()
                Log.d("url:", exchangeRate.toString())
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching rates", e)
            }
        }
    }
}

object RetrofitInstance {
    private val gson = GsonBuilder()
        .registerTypeAdapter(CurrencyResponse::class.java, CurrencyResponseDeserializer())
        .create()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val api: CurrencyApi by lazy {
        retrofit.create(CurrencyApi::class.java)
    }
}

interface CurrencyApi {
    @GET("{baseCurrency}.json")
    suspend fun getRates(
        @Path("baseCurrency") baseCurrency: String
    ): CurrencyResponse
}

data class CurrencyResponse(
    val date: String,
    val rates: Map<String, Double>
)


class CurrencyResponseDeserializer : JsonDeserializer<CurrencyResponse> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): CurrencyResponse {
        val jsonObject = json.asJsonObject
        val date = jsonObject["date"].asString

        // Assume the only extra property is the base currency rates object.
        val ratesMap = mutableMapOf<String, Double>()
        jsonObject.entrySet().forEach { (key, value) ->
            if (key != "date" && value.isJsonObject) {
                val ratesObj = value.asJsonObject
                ratesObj.entrySet().forEach { (innerKey, innerValue) ->
                    ratesMap[innerKey] = innerValue.asDouble
                }
            }
        }
        return CurrencyResponse(date, ratesMap)
    }
}

@Composable
fun ConvertScreen(baseCurrency: String, destCurrency: String) {
    WearAppTheme {
        var exchangeRate by remember { mutableStateOf(0.0) }
        var inputValue by remember { mutableStateOf(1.0f) }
        val computedValue = inputValue * exchangeRate

        LaunchedEffect(baseCurrency, destCurrency) {
            try {
                val response = RetrofitInstance.api.getRates(baseCurrency.lowercase())
                exchangeRate = response.rates[destCurrency.lowercase()] ?: 0.0
            } catch (e: Exception) {
                Log.e("ConvertScreen", "Error fetching rates", e)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),

            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            // Top row for Input currency
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = baseCurrency,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    fontSize = 24.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            // Row for Input Value
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "%.2f".format(inputValue),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 22.sp
                    )
                }
            }
            // Middle row with buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Slider(
                    modifier = Modifier
                        .height(30.dp)
                        .scale(0.7f)
                        .fillMaxWidth(),
                    value = inputValue,
                    onValueChange = { inputValue = it },
                    valueRange = 0f..50f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                ))
            }
            // Row for Output value
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "%.2f".format(computedValue),
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.primaryContainer,
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            // Row for Output currency
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = destCurrency,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    fontSize = 24.sp
                )
            }
        }
    }
}

@WearPreviewDevices
@Composable
fun ConvertScreenPreview() {
    WearAppTheme {
        ConvertScreen(
            baseCurrency = "usd",
            destCurrency = "eur"
        )
    }
}
