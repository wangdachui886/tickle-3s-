package com.lightledger.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lightledger.app.ui.LightLedgerApp
import com.lightledger.app.ui.LightLedgerViewModel
import com.lightledger.app.ui.LightLedgerViewModelFactory
import com.lightledger.app.ui.theme.LightLedgerTheme

class MainActivity : ComponentActivity() {
    private val widgetAction = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureSystemBars()
        widgetAction.value = intent.getStringExtra(WidgetActions.ExtraAction)

        val repository = (application as LightLedgerApplication).repository

        setContent {
            LightLedgerTheme {
                val viewModel: LightLedgerViewModel = viewModel(
                    factory = LightLedgerViewModelFactory(repository),
                )
                LightLedgerApp(
                    viewModel = viewModel,
                    launchAction = widgetAction.value,
                    onLaunchActionConsumed = { widgetAction.value = null },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        widgetAction.value = intent.getStringExtra(WidgetActions.ExtraAction)
    }

    private fun configureSystemBars() {
        window.statusBarColor = Color.parseColor("#F7F7F5")
        window.navigationBarColor = Color.parseColor("#F7F7F5")
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
    }
}
