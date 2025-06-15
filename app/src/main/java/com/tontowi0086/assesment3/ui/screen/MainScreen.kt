package com.tontowi0086.assesment3.ui.screen

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tontowi0086.assesment3.ui.theme.Assesment3Theme

@Composable
fun MainScreen() {
    val context = LocalContext.current.applicationContext
    val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(context as Application))
    val authToken by viewModel.authToken.collectAsState(initial = null)
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Ambil data ketika authToken berubah dari null menjadi non-null (setelah login)
    LaunchedEffect(authToken) {
        if (authToken != null) {
            viewModel.retrieveData()
        }
    }

    // Tampilkan Toast untuk error
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }

    Assesment3Theme {
        Surface(modifier = Modifier.fillMaxSize()) {
            if (authToken == null) {
                // Tampilkan layar login jika tidak ada token
                LoginScreen(onLoginSuccess = { idToken ->
                    viewModel.login(idToken)
                })
            } else {
                // Tampilkan layar utama jika sudah login
                HomeScreen(viewModel = viewModel)
            }
        }
    }
}