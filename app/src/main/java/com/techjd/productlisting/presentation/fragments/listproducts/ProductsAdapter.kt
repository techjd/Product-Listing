package com.techjd.productlisting.presentation.fragments.listproducts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.techjd.productlisting.R
import com.techjd.productlisting.data.model.response.products.ProductsItem
import com.techjd.productlisting.databinding.ItemProductBinding

class ProductsAdapter :
  ListAdapter<ProductsItem, ProductsAdapter.RestaurantViewHolder>(ProductsComparator()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
    val binding =
      ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return RestaurantViewHolder(binding)
  }

  override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
    val currentItem = getItem(position)
    if (currentItem != null) {
      holder.bind(currentItem)
    }
  }

  class RestaurantViewHolder(private val binding: ItemProductBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(product: ProductsItem) {
      binding.apply {

        with(product) {
            Glide.with(itemView)
              .load(image.ifEmpty { R.drawable.no_image_available })
              .into(productImage)

            itemName.text = productName
            itemCategory.text = productType
            itemPrice.text = String.format("%.2f",price)
            itemTax.text = String.format("%.2f", tax)
        }
      }
    }

    private fun getFormattedNumber(num: String): String {
      val lastValue = num.substringAfter(".")
      return if (lastValue == "00") {
        num.substringBefore(".")
      } else {
        num
      }
    }
  }

  class ProductsComparator : DiffUtil.ItemCallback<ProductsItem>() {
    override fun areItemsTheSame(oldItem: ProductsItem, newItem: ProductsItem) =
      oldItem.productName == newItem.productName

    override fun areContentsTheSame(oldItem: ProductsItem, newItem: ProductsItem) =
      oldItem == newItem
  }
}