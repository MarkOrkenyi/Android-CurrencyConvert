/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.markorkenyi.currencyConverter.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.markorkenyi.currencyConverter.presentation.theme.WearAppTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.wear.compose.material3.MaterialTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(onConvertClick = { base, dest ->
                // Navigate with arguments; note that for simplicity we assume currency codes have no special characters
                navController.navigate("convert/$base/$dest")
            })
        }
        composable(
            route = "convert/{baseCurrency}/{destCurrency}",
            arguments = listOf(
                navArgument("baseCurrency") { type = NavType.StringType },
                navArgument("destCurrency") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val base = backStackEntry.arguments?.getString("baseCurrency") ?: "USD"
            val dest = backStackEntry.arguments?.getString("destCurrency") ?: "EUR"
            ConvertScreen(base, dest)
        }
    }
}
@WearPreviewDevices
@Composable
fun WearApp() {
    WearAppTheme {
        ConvertScreen("usd", "eur")
    }
}
