package com.schoolminimarket.app.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderCreateItemDto(
    val productId: String,
    val quantity: Int,
    val price: Double
)

@Serializable
data class OrderCreateRequest(
    val items: List<OrderCreateItemDto>,
    val total: Double,
    val paymentMethod: String? = null // e.g., "CASH" or "MIDTRANS"
)

@Serializable
data class OrderCreateResponse(
    val orderId: String,
    val status: String,
    val message: String? = null
)
