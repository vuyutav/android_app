package com.schoolminimarket.app.repository

import com.schoolminimarket.app.model.ProductDto
import com.schoolminimarket.app.model.PagedResult
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun getProducts(
        search: String? = null,
        categoryId: String? = null,
        page: Int = 1,
        size: Int = 20,
        token: String
    ): PagedResult<ProductDto>
}
