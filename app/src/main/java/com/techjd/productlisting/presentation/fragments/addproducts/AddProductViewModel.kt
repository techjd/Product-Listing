package com.techjd.productlisting.presentation.fragments.addproducts

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techjd.productlisting.data.model.response.upload.UploadImage
import com.techjd.productlisting.data.repository.ProductsRepository
import com.techjd.productlisting.utils.NetworkResult
import com.techjd.productlisting.utils.NetworkResult.Idle
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AddProductViewModel @Inject constructor(
  private val productsRepository: ProductsRepository
) : ViewModel() {

  private val _uploadProducts = MutableStateFlow<NetworkResult<UploadImage>>(NetworkResult.Idle())
  val uploadProducts: StateFlow<NetworkResult<UploadImage>> = _uploadProducts.asStateFlow()

  var uri: Uri? = null

  fun addProduct(
    file: File?,
    productName: String,
    productCategory: String,
    productPrice: String,
    productTax: String
  ) {
    viewModelScope.launch {
      _uploadProducts.emit(NetworkResult.Loading())
      _uploadProducts.emit(productsRepository.addProduct(file, productName, productCategory, productPrice, productTax))
    }
  }
}