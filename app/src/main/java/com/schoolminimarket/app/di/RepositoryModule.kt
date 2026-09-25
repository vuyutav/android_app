// RepositoryModule.kt
package com.schoolminimarket.app.di

import com.schoolminimarket.app.repository.CartRepository
import com.schoolminimarket.app.repository.CartRepositoryImpl
import com.schoolminimarket.app.repository.OrderRepository
import com.schoolminimarket.app.repository.OrderRepositoryImpl
import com.schoolminimarket.app.repository.ProductRepository
import com.schoolminimarket.app.repository.ProductRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository

    @Binds
    abstract fun bindOrderRepository(impl: OrderRepositoryImpl): OrderRepository
}
