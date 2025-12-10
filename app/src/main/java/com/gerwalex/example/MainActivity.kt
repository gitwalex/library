package com.gerwalex.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gerwalex.example.ui.theme.LibraryTheme
import com.gerwalex.library.composables.ads.manager.BannerAdsConsentManager
import com.google.android.gms.ads.AdView
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi


class MainActivity : ComponentActivity() {
    private val ADUNITID = "ca-app-pub-3940256099942544/9214589741"

    @OptIn(ExperimentalAtomicApi::class)
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
    private var adView: AdView? = null
    private lateinit var googleMobileAdsConsentManager: BannerAdsConsentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Content()
        }
    }
}

@Composable
fun Content() {
    LibraryTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                OutlinedTextField(value = "DUmmy", onValueChange = {})
//                AutoCompleteTextViewExample()
                HorizontalDivider()
                DragDropLazyColumnExample()
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Content()
}