package com.schoolminimarket.app.repository

import com.schoolminimarket.app.model.PaymentInfoDto
import com.schoolminimarket.app.network.ApiService
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : PaymentRepository {
    override suspend fun getPaymentInfo(orderId: String, token: String): PaymentInfoDto {
        val authHeader = "Bearer $token"
        return apiService.getPaymentInfo(orderId, authHeader)
    }
}
