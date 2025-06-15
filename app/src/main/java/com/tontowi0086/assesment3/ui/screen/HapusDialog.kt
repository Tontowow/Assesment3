package com.tontowi0086.assesment3.ui.screen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.tontowi0086.assesment3.model.Barang

@Composable
fun HapusDialog(
    barang: Barang,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Hapus Barang") },
        text = { Text("Apakah Anda yakin ingin menghapus ${barang.nama}?") },
        confirmButton = {
            Button(onClick = onConfirmation) {
                Text("Hapus")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Batal")
            }
        }
    )
}