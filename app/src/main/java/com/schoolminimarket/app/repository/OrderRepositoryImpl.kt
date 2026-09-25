package com.schoolminimarket.app.repository

import com.schoolminimarket.app.model.OrderCreateRequest
import com.schoolminimarket.app.model.OrderCreateResponse
import com.schoolminimarket.app.network.ApiService
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : OrderRepository {
    override suspend fun createOrder(request: OrderCreateRequest, token: String): OrderCreateResponse {
        val authHeader = "Bearer $token"
        // Idempotency key could be generated here; for simplicity using UUID placeholder
        val idempotencyKey = java.util.UUID.randomUUID().toString()
        return apiService.createOrder(idempotencyKey, request, authHeader)
    }
}
