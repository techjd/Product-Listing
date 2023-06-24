package com.techjd.productlisting.api

import com.techjd.productlisting.data.model.response.products.Products
import com.techjd.productlisting.data.model.response.upload.UploadImage
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap

interface ProductsApi {
  @GET("get")
  suspend fun getProducts(): Response<Products>

  @Multipart
  @POST("add")
  suspend fun addProduct(
    @PartMap partMap: HashMap<String, RequestBody>,
    @Part images: ArrayList<MultipartBody.Part>,
  ): Response<UploadImage>

}