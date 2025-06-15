package com.tontowi0086.assesment3.ui.screen

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tontowi0086.assesment3.model.Barang
import com.tontowi0086.assesment3.network.BarangApi
import com.tontowi0086.assesment3.network.UserDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class MainViewModel(application: Application) : ViewModel() {
    private val dataStore = UserDataStore(application)
    private val apiService = BarangApi.getInstance(application)

    val authToken = dataStore.authToken

    val data = MutableStateFlow<List<Barang>>(emptyList())
    val status = MutableStateFlow(ApiStatus.IDLE)
    val errorMessage = MutableStateFlow<String?>(null)

    fun login(idToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.login(mapOf("token" to idToken))
                dataStore.saveToken(response.token)
            } catch (e: Exception) {
                errorMessage.value = "Login gagal: ${e.message}"
                Log.e("MainViewModel", "Login failed", e)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStore.clearToken()
        }
    }

    fun retrieveData() {
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.LOADING
            try {
                data.value = apiService.getBarang()
                status.value = ApiStatus.SUCCESS
            } catch (e: Exception) {
                Log.e("MainViewModel", "RetrieveData Failure", e)
                errorMessage.value = "Gagal memuat data: ${e.message}"
                status.value = ApiStatus.FAILED
            }
        }
    }

    fun saveData(nama: String, deskripsi: String, file: File, onFinished: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val namaRb = nama.toRequestBody("text/plain".toMediaTypeOrNull())
                val deskripsiRb = deskripsi.toRequestBody("text/plain".toMediaTypeOrNull())
                val fileRb = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val gambar = MultipartBody.Part.createFormData("gambar", file.name, fileRb)
                apiService.addBarang(namaRb, deskripsiRb, gambar)
                retrieveData() // Refresh data
                launch(Dispatchers.Main) { onFinished() }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Save Failure", e)
                errorMessage.value = "Gagal menyimpan: ${e.message}"
            }
        }
    }

    fun updateData(id: Long, nama: String, deskripsi: String, file: File?, onFinished: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val namaRb = nama.toRequestBody("text/plain".toMediaTypeOrNull())
                val deskripsiRb = deskripsi.toRequestBody("text/plain".toMediaTypeOrNull())
                val gambar = file?.let {
                    val fileRb = it.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("gambar", it.name, fileRb)
                }
                apiService.updateBarang(id, namaRb, deskripsiRb, gambar)
                retrieveData() // Refresh data
                launch(Dispatchers.Main) { onFinished() }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Update Failure", e)
                errorMessage.value = "Gagal memperbarui: ${e.message}"
            }
        }
    }

    fun deleteData(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                apiService.deleteBarang(id)
                retrieveData() // Refresh data
            } catch (e: Exception) {
                Log.e("MainViewModel", "Delete Failure", e)
                errorMessage.value = "Gagal menghapus: ${e.message}"
            }
        }
    }

    fun clearMessage() {
        errorMessage.value = null
    }
}

enum class ApiStatus { IDLE, LOADING, SUCCESS, FAILED }

class MainViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}