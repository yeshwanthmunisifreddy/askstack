package com.thesubgraph.askstack.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.thesubgraph.askstack.application.navigation.Destination
import com.thesubgraph.askstack.application.navigation.Router
import com.thesubgraph.askstack.base.components.NetworkStatusBanner
import com.thesubgraph.askstack.base.theme.AskStackTheme
import com.thesubgraph.askstack.features.shared.viewmodel.NetworkStatusViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AskStackTheme {
                val networkStatusViewModel: NetworkStatusViewModel = hiltViewModel()
                val isConnected = networkStatusViewModel.isConnected.collectAsState()
                val showConnectedMessage =
                    networkStatusViewModel.showConnectedMessage.collectAsState()

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                        .navigationBarsPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        NetworkStatusBanner(
                            isConnected = isConnected,
                            showConnectedMessage = showConnectedMessage
                        )
                        AskStackApp()
                    }

                }
            }
        }
    }
}


@Composable
fun AskStackApp() {
    val navController = rememberNavController()
    val router = Router(navController = navController)
    router.ComposeRouter(Destination.Home)
}