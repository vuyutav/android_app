package com.schoolminimarket.app.network

import com.schoolminimarket.app.model.AuthRequest
import com.schoolminimarket.app.model.AuthResponse
import com.schoolminimarket.app.model.OrderCreateRequest
import com.schoolminimarket.app.model.OrderCreateResponse
import com.schoolminimarket.app.model.ProductDto
import com.schoolminimarket.app.model.ProductDetailDto
import com.schoolminimarket.app.model.PagedResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Authentication
    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse

    // Products
    @GET("products")
    suspend fun getProducts(
        @Query("search") search: String? = null,
        @Query("categoryId") categoryId: String? = null,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20,
        @Header("Authorization") token: String
    ): PagedResult<ProductDto>

    @GET("products/{id}")
    suspend fun getProductDetail(
        @Path("id") productId: String,
        @Header("Authorization") token: String
    ): ProductDetailDto

    // Cart (simplified – token passed via header)
    @GET("cart")
    suspend fun getCart(@Header("Authorization") token: String): com.schoolminimarket.app.model.CartDto

    @POST("cart")
    suspend fun addOrUpdateCartItem(
        @Body request: com.schoolminimarket.app.model.AddCartItemRequest,
        @Header("Authorization") token: String
    )

    // Payment info (QR code)
    @GET("payments/{orderId}")
    suspend fun getPaymentInfo(
        @Path("orderId") orderId: String,
        @Header("Authorization") token: String
    ): com.schoolminimarket.app.model.PaymentInfoDto
}
