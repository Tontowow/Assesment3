package com.tontowi0086.assesment3.ui.screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.tontowi0086.assesment3.model.Barang
import com.tontowi0086.assesment3.network.BarangApi
import java.io.File
import java.io.FileOutputStream

@Composable
fun BarangDialog(
    barang: Barang?,
    onDismissRequest: () -> Unit,
    onConfirmation: (String, String, File?) -> Unit
) {
    var nama by remember { mutableStateOf(barang?.nama ?: "") }
    var deskripsi by remember { mutableStateOf(barang?.deskripsi ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageFile by remember { mutableStateOf<File?>(null) }
    val context = LocalContext.current

    val cropActivityLauncher = rememberLauncherForActivityResult(
        contract = CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            val uri = result.uriContent
            imageUri = uri
            uri?.let {
                imageFile = it.toTempFile(context)
            }
        } else {
            val exception = result.error
            Toast.makeText(context, "Gagal memotong gambar: ${exception?.message}", Toast.LENGTH_SHORT).show()
        }
    }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val cropOptions = CropImageContractOptions(it, CropImageOptions())
            cropActivityLauncher.launch(cropOptions)
        }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (barang == null) "Tambah Barang" else "Ubah Barang",
                    style = MaterialTheme.typography.titleLarge
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    val imageUrl = if (imageUri != null) {
                        imageUri
                    } else if (barang?.gambar != null) {
                        BarangApi.getBaseUrl().dropLast(1) + barang.gambar
                    } else {
                        null
                    }

                    if (imageUrl == null) {
                        Text("Pilih Gambar")
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text("Nama Barang") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi") },
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = onDismissRequest, modifier = Modifier.weight(1f)) {
                        Text("Batal")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (nama.isBlank() || deskripsi.isBlank()) {
                                Toast.makeText(context, "Nama dan deskripsi tidak boleh kosong.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (barang == null && imageFile == null) {
                                Toast.makeText(context, "Gambar wajib diisi untuk barang baru.", Toast.LENGTH_SHORT).show()
                            } else {
                                onConfirmation(nama, deskripsi, imageFile)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Simpan")
                    }
                }
            }
        }
    }
}

fun Uri.toTempFile(context: Context): File {
    val inputStream = context.contentResolver.openInputStream(this)
        ?: throw IllegalStateException("InputStream is null")
    val file = File.createTempFile("image_crop_", ".jpg", context.cacheDir)
    val outputStream = FileOutputStream(file)
    inputStream.copyTo(outputStream)
    inputStream.close()
    outputStream.close()
    return file
}