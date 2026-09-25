package com.schoolminimarket.app.repository

import com.schoolminimarket.app.model.PaymentInfoDto

interface PaymentRepository {
    suspend fun getPaymentInfo(orderId: String, token: String): PaymentInfoDto
}
