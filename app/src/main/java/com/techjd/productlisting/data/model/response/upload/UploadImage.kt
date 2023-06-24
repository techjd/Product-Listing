package com.techjd.productlisting.data.model.response.upload


import com.google.gson.annotations.SerializedName

data class UploadImage(
    @SerializedName("message")
    val message: String,
    @SerializedName("product_details")
    val productDetails: ProductDetails,
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("success")
    val success: Boolean
)