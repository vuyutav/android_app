package com.schoolminimarket.app.repository

import com.schoolminimarket.app.model.CartDto
import com.schoolminimarket.app.model.AddCartItemRequest

interface CartRepository {
    suspend fun getCart(token: String): CartDto
    suspend fun addOrUpdateCartItem(request: AddCartItemRequest, token: String): Unit
}
