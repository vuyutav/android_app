package com.schoolminimarket.app.repository

import com.schoolminimarket.app.model.OrderCreateRequest
import com.schoolminimarket.app.model.OrderCreateResponse

interface OrderRepository {
    suspend fun createOrder(request: OrderCreateRequest, token: String): OrderCreateResponse
    suspend fun getOrderStatus(orderId: String, token: String): com.schoolminimarket.app.model.OrderStatusDto
}
