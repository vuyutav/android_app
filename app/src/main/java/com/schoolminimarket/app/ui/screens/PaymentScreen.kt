package com.schoolminimarket.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.schoolminimarket.app.ui.viewmodel.PaymentUiState
import com.schoolminimarket.app.ui.viewmodel.PaymentViewModel

@Composable
fun PaymentScreen(navController: NavHostController, orderId: String) {
    val viewModel: PaymentViewModel = hiltViewModel()
    // Trigger loading when composable first composed
    viewModel.loadPaymentInfo(orderId)
    val uiState = viewModel.uiState.collectAsState().value

    Column(modifier = Modifier.padding(16.dp)) {
        when (uiState) {
            is PaymentUiState.Loading -> {
                CircularProgressIndicator()
            }
            is PaymentUiState.Success -> {
                Text(text = "Pembayaran untuk Order ID: ${uiState.paymentInfo.orderId}")
                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = uiState.paymentInfo.qrUrl,
                    contentDescription = "QR Code",
                    modifier = Modifier.height(200.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { navController.navigate("home") }) {
                    Text(text = "Selesai")
                }
            }
            is PaymentUiState.Paid -> {
                Text(text = "Pembayaran berhasil! Order ID: ${uiState.orderId}")
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { navController.navigate("home") }) {
                    Text(text = "Kembali ke Beranda")
                }
            }
            is PaymentUiState.Error -> {
                Text(text = "Error: ${uiState.message}")
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text(text = "Kembali")
                }
            }
        }
    }
}
