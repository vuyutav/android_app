package com.schoolminimarket.app.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderStatusDto(
    val orderId: String,
    val status: String // e.g., "PENDING", "PAID", "FAILED"
)
