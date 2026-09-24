package com.schoolminimarket.app.repository

import com.schoolminimarket.app.model.ProductDto
import com.schoolminimarket.app.model.ProductDetailDto
import com.schoolminimarket.app.model.PagedResult

interface ProductRepository {
    suspend fun getProducts(
        search: String? = null,
        categoryId: String? = null,
        page: Int = 1,
        size: Int = 20,
        token: String
    ): PagedResult<ProductDto>

    suspend fun getProductDetail(productId: String, token: String): ProductDetailDto
}

