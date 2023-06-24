package com.techjd.productlisting.presentation.fragments.listproducts

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.techjd.productlisting.R
import com.techjd.productlisting.api.ProductsApi
import com.techjd.productlisting.data.model.response.products.Products
import com.techjd.productlisting.databinding.FragmentProductsListBinding
import com.techjd.productlisting.utils.NetworkResult
import com.techjd.productlisting.utils.NetworkResult.Error
import com.techjd.productlisting.utils.NetworkResult.Idle
import com.techjd.productlisting.utils.NetworkResult.Loading
import com.techjd.productlisting.utils.NetworkResult.Success
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@AndroidEntryPoint
class ProductsListFragment : Fragment() {

  private var _binding: FragmentProductsListBinding? = null
  private val binding get() = _binding!!
  private val TAG = this::class.java.simpleName

  private val productsAdapter = ProductsAdapter()

  private val productsViewModel: ProductsViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentProductsListBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    binding.addNewProduct.setOnClickListener {
      findNavController().navigate(R.id.action_productsListFragment_to_addProductsFragment)
    }

    binding.productsRv.apply {
      adapter = productsAdapter
      layoutManager = LinearLayoutManager(requireContext())
    }

    binding.swipeLayout.setOnRefreshListener {
      productsViewModel.getProducts()
      binding.searchBar.setText("")
    }

    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        launch {
          productsViewModel.products.collect { products ->
            renderUI(products)
          }
        }
        launch {
          productsViewModel.searchedProducts.collect { products ->
            products.data?.let {
              if (it.isNotEmpty()) {
                productsAdapter.submitList(products.data)
              }
            }
          }
        }
      }
    }

    binding.searchBar.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(
        s: CharSequence?,
        start: Int,
        count: Int,
        after: Int
      ) {

      }

      override fun onTextChanged(
        s: CharSequence?,
        start: Int,
        before: Int,
        count: Int
      ) {
        s?.let { chars ->
          if (chars.isEmpty()) {
            productsAdapter.submitList(productsViewModel.products.value.data)
          } else {
            productsViewModel.searchProducts(s.toString().lowercase())
          }
        }
      }

      override fun afterTextChanged(s: Editable?) {

      }
    })
  }

  private fun renderUI(products: NetworkResult<Products>) {
      when(products) {
        is Error -> {
          with(binding) {
            searchBar.isEnabled = false
            swipeLayout.isRefreshing = false
            progressBar.visibility = View.GONE
            productsRv.visibility = View.GONE
            errorTxt.visibility = View.VISIBLE
            errorTxt.text = products.message
          }
        }
        is Loading -> {
          with(binding) {
            swipeLayout.isRefreshing = false
            searchBar.isEnabled = false
            productsRv.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            errorTxt.visibility = View.GONE
          }
        }
        is Success -> {
          with(binding) {
            swipeLayout.isRefreshing = false
            searchBar.isEnabled = true
            productsRv.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            errorTxt.visibility = View.GONE
            productsAdapter.submitList(products.data)
          }
        }
        is Idle -> {
          // no operation
        }
      }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}