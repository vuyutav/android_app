package com.schoolminimarket.app.repository

import com.schoolminimarket.app.model.CartDto
import com.schoolminimarket.app.model.AddCartItemRequest
import com.schoolminimarket.app.network.ApiService
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : CartRepository {
    override suspend fun getCart(token: String): CartDto {
        val authHeader = "Bearer $token"
        return apiService.getCart(authHeader)
    }

    override suspend fun addOrUpdateCartItem(request: AddCartItemRequest, token: String) {
        val authHeader = "Bearer $token"
        apiService.addOrUpdateCartItem(request, authHeader)
    }
}
