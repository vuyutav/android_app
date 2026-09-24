package com.schoolminimarket.app.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: String,
    val name: String,
    val description: String? = null,
    val price: Double,
    val isActive: Boolean,
    val stock: Int,
    val categoryId: String,
    val categoryName: String
)

@Serializable
data class ProductDetailDto(
    val id: String,
    val name: String,
    val description: String? = null,
    val price: Double,
    val isActive: Boolean,
    val stock: Int,
    val images: List<String> = emptyList(), // URLs
    val categoryId: String,
    val categoryName: String
)

@Serializable
data class PagedResult<T>(
    val page: Int,
    val size: Int,
    val total: Int,
    val items: List<T>
)
