package com.tontowi0086.assesment3.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tontowi0086.assesment3.R
import com.tontowi0086.assesment3.model.Barang
import com.tontowi0086.assesment3.ui.screen.BarangDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val data by viewModel.data.collectAsState()
    val status by viewModel.status.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedBarang: Barang? by remember { mutableStateOf(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                selectedBarang = null
                showDialog = true
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Barang")
            }
        }
    ) { padding ->
        when (status) {
            ApiStatus.LOADING -> {
                Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            ApiStatus.SUCCESS -> {
                LazyColumn(
                    modifier = Modifier.padding(padding).fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(data) { barang ->
                        BarangCard(
                            barang = barang,
                            onEdit = {
                                selectedBarang = barang
                                showDialog = true
                            },
                            onDelete = {
                                selectedBarang = barang
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
            ApiStatus.FAILED -> {
                Column(
                    modifier = Modifier.padding(padding).fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Gagal memuat data.")
                    Button(onClick = { viewModel.retrieveData() }) {
                        Text("Coba Lagi")
                    }
                }
            }
            ApiStatus.IDLE -> {
                // State awal, bisa kosong atau menampilkan pesan
            }
        }

        if (showDialog) {
            BarangDialog(
                barang = selectedBarang,
                onDismissRequest = { showDialog = false },
                onConfirmation = { nama, deskripsi, file ->
                    if (selectedBarang == null) {
                        // Untuk tambah, file wajib
                        file?.let {
                            viewModel.saveData(nama, deskripsi, it) { showDialog = false }
                        }
                    } else {
                        // Untuk update, file opsional
                        viewModel.updateData(selectedBarang!!.id, nama, deskripsi, file) { showDialog = false }
                    }
                }
            )
        }

        if (showDeleteDialog) {
            selectedBarang?.let { barang ->
                HapusDialog(
                    barang = barang,
                    onDismissRequest = { showDeleteDialog = false },
                    onConfirmation = {
                        viewModel.deleteData(barang.id)
                        showDeleteDialog = false
                    }
                )
            }
        }
    }
}