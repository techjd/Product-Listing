package com.techjd.productlisting.data.repository

import com.techjd.productlisting.api.ProductsApi
import java.io.File
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class ProductsRepository @Inject constructor(
  private val productsApi: ProductsApi,
) : BaseRepository() {

  suspend fun getProducts() = safeApiCall {
    productsApi.getProducts()
  }

  suspend fun addProduct(
    imageFile: File?,
    productName: String,
    productCategory: String,
    productPrice: String,
    productTax: String
  ) = safeApiCall {

    val parts: ArrayList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()

    imageFile?.let { file ->
        val body = MultipartBody.Part.createFormData(
          "files",
          imageFile.name,
          file.asRequestBody()
        )
        parts.add(body)
    }

    val name = createPartFromString(productName)
    val category = createPartFromString(productCategory)
    val price = createPartFromString(productPrice)
    val tax = createPartFromString(productTax)

    val map = HashMap<String, RequestBody>()
    map["product_name"] = name
    map["product_type"] = category
    map["price"] = price
    map["tax"] = tax

    productsApi.addProduct(
      images = parts,
      partMap = map
    )
  }


  private fun createPartFromString(descriptionString: String): RequestBody {
    return descriptionString
      .toRequestBody(MultipartBody.FORM)
  }
}