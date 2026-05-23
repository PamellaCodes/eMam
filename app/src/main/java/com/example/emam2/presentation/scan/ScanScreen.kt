package com.example.emam2.presentation.scan

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.*
import com.example.emam2.ui.theme.PrimaryColor
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(viewModel: ScanViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val cameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) cameraPermission.launchPermissionRequest()
    }

    when {
        uiState.isAnalyzing -> LoadingView()
        uiState.result != null -> ResultView(
            result = uiState.result!!,
            bitmap = uiState.capturedBitmap,
            isSaved = uiState.isSaved,
            onSave = { viewModel.saveResult() },
            onReset = { viewModel.reset() }
        )
        uiState.error.isNotEmpty() -> ErrorView(
            message = uiState.error,
            onRetry = { viewModel.reset() }
        )
        !cameraPermission.status.isGranted -> PermissionView(
            onRequest = { cameraPermission.launchPermissionRequest() }
        )
        else -> CameraView(onImageCaptured = { viewModel.analyzeFood(it) })
    }
}

@Composable
fun CameraView(onImageCaptured: (Bitmap) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider. getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().apply {
                        setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val capture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()
                    imageCapture = capture
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, capture)
                    } catch (e: Exception) { e.printStackTrace() }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Capture button
        Button(
            onClick = {
                val capture = imageCapture ?: return@Button
                capture.takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            val buffer = image.planes[0].buffer
                            val bytes = ByteArray(buffer.remaining())
                            buffer.get(bytes)
                            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            val rotated = rotateBitmap(bitmap, image.imageInfo.rotationDegrees)
                            image.close()
                            onImageCaptured(rotated)
                        }
                        override fun onError(exception: ImageCaptureException) {
                            exception.printStackTrace()
                        }
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .size(72.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
        ) {
            Icon(Icons.Filled.Camera, contentDescription = "Ambil foto", tint = Color.White, modifier = Modifier.size(32.dp))
        }

        // Label
        Text(
            "Arahkan kamera ke makanan",
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 48.dp),
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

fun rotateBitmap(bitmap: Bitmap, rotation: Int): Bitmap {
    if (rotation == 0) return bitmap
    val matrix = android.graphics.Matrix().apply { postRotate(rotation.toFloat()) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

@Composable
fun ResultView(
    result: NutritionResult,
    bitmap: Bitmap?,
    isSaved: Boolean,
    onSave: () -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(result.foodName, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)

        if (result.aiNote.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(result.aiNote, fontSize = 13.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Kandungan Nutrisi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Per ${result.servingGrams.toInt()} gram", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                NutrientRow("🔥 Kalori", "${result.calories.toInt()} kkal")
                NutrientRow("🩸 Zat Besi", "${"%.1f".format(result.ironMg)} mg")
                NutrientRow("🟢 Folat", "${"%.0f".format(result.folateMcg)} mcg")
                NutrientRow("🦴 Kalsium", "${"%.0f".format(result.calciumMg)} mg")
                NutrientRow("💊 Zinc", "${"%.1f".format(result.zincMg)} mg")
                NutrientRow("🌊 Yodium", "${"%.0f".format(result.iodineMcg)} mcg")
                NutrientRow("☀️ Vitamin D", "${"%.1f".format(result.vitaminDMcg)} mcg")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (isSaved) {
            Text("✅ Tersimpan!", color = Color.Green, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Simpan ke Riwayat") }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onReset,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryColor)
        ) { Text("Scan Ulang") }
    }
}

@Composable
fun NutrientRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
    HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
}

@Composable
fun LoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = PrimaryColor, modifier = Modifier.size(56.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Menganalisis makanan...", color = PrimaryColor, fontWeight = FontWeight.Bold)
            Text("Gemini sedang memproses", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = Color.Red, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Analisis Gagal", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(message, fontSize = 13.sp, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)) {
                Text("Coba Lagi")
            }
        }
    }
}

@Composable
fun PermissionView(onRequest: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Icon(Icons.Filled.CameraAlt, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(80.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Izin Kamera Diperlukan", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Aplikasi memerlukan izin kamera untuk scan makanan.", fontSize = 13.sp, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRequest, colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)) {
                Text("Izinkan Kamera")
            }
        }
    }
}