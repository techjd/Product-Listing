package com.techjd.productlisting.presentation.fragments.listproducts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide.init
import com.techjd.productlisting.data.model.response.products.Products
import com.techjd.productlisting.data.model.response.products.ProductsItem
import com.techjd.productlisting.data.repository.ProductsRepository
import com.techjd.productlisting.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.ArrayList
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ProductsViewModel @Inject constructor(
  private val productsRepository: ProductsRepository
) : ViewModel() {

  private val _products = MutableStateFlow<NetworkResult<Products>>(NetworkResult.Loading())
  val products: StateFlow<NetworkResult<Products>> = _products.asStateFlow()

  private val _searchedProducts = MutableStateFlow<NetworkResult<Products>>(_products.value)
  val searchedProducts: StateFlow<NetworkResult<Products>> = _searchedProducts.asStateFlow()

  init {
    getProducts()
  }

  internal fun getProducts() {
    viewModelScope.launch {
      _products.emit(productsRepository.getProducts())
    }
  }

  fun searchProducts(query: String) {
    viewModelScope.launch {
      val filteredItems = Products()
      products.value.data?.forEach { item ->
        if (item.productName.lowercase().contains(query) ||
          item.productType.lowercase().contains(query)) {
          filteredItems.add(item)
        }
      }
      _searchedProducts.value = NetworkResult.Success(filteredItems)
    }
  }
}