package com.schoolminimarket.app.repository

import com.schoolminimarket.app.model.ProductDto
import com.schoolminimarket.app.model.PagedResult
import com.schoolminimarket.app.network.ApiService
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ProductRepository {
    override suspend fun getProducts(
        search: String?,
        categoryId: String?,
        page: Int,
        size: Int,
        token: String
    ): PagedResult<ProductDto> {
        // The backend expects Bearer token in Authorization header.
        val authHeader = "Bearer $token"
        return apiService.getProducts(
            search = search,
            categoryId = categoryId,
            page = page,
            size = size,
            token = authHeader
        )
    }
}
