package com.schoolminimarket.app.model

import kotlinx.serialization.Serializable

@Serializable
data class PaymentInfoDto(
    val orderId: String,
    val qrUrl: String // URL to QR code image (could be data URI)
)
