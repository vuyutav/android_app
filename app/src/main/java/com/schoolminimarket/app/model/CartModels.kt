package com.schoolminimarket.app.model

import kotlinx.serialization.Serializable

@Serializable
data class CartItemDto(
    val id: String,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val price: Double,
    val total: Double = price * quantity
)

@Serializable
data class CartDto(
    val items: List<CartItemDto>,
    val totalCost: Double = items.sumOf { it.total }
)

@Serializable
data class AddCartItemRequest(
    val productId: String,
    val quantity: Int
)
