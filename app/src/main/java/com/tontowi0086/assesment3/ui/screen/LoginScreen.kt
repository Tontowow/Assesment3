package com.tontowi0086.assesment3.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.tontowi0086.assesment3.BuildConfig
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = {
            scope.launch {
                try {
                    // 1. Buat opsi untuk Google ID (ini sudah benar)
                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(BuildConfig.API_KEY)
                        .build()

                    // 2. Buat request kredensial utama (ini sudah benar)
                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    // ================================================================
                    // PERBAIKAN DI SINI: Tambahkan 'context' sebagai parameter pertama
                    // ================================================================
                    val result = credentialManager.getCredential(context, request)

                    val credential = result.credential

                    // Pengecekan tipe yang lebih sederhana, sekarang seharusnya berfungsi
                    if (credential is GoogleIdTokenCredential) {
                        onLoginSuccess(credential.idToken)
                    } else {
                        Log.e("LoginScreen", "Unexpected credential type: ${credential.type}")
                        Toast.makeText(context, "Login gagal: Tipe kredensial tidak didukung.", Toast.LENGTH_LONG).show()
                    }
                } catch (e: GetCredentialException) {
                    Log.e("LoginScreen", "GetCredentialException", e)
                    // Contoh pesan error dari library: "The user must be signed in to Google to use this feature."
                    Toast.makeText(context, "Login gagal: ${e.message}", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Log.e("LoginScreen", "Exception", e)
                    Toast.makeText(context, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }) {
            Text("Sign in with Google")
        }
    }
}