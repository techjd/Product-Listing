package com.techjd.productlisting.presentation.fragments.addproducts

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView.Guidelines.ON
import com.google.android.material.snackbar.Snackbar
import com.techjd.productlisting.R
import com.techjd.productlisting.databinding.FragmentAddProductsBinding
import com.techjd.productlisting.utils.NetworkObserver
import com.techjd.productlisting.utils.NetworkResult.Error
import com.techjd.productlisting.utils.NetworkResult.Idle
import com.techjd.productlisting.utils.NetworkResult.Loading
import com.techjd.productlisting.utils.NetworkResult.Success
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddProductsFragment : Fragment() {

  private var _binding: FragmentAddProductsBinding? = null
  private val binding get() = _binding!!
  private val TAG = this::class.java.simpleName

  private val categories = arrayOf(
    "Select Category",
    "Mobiles", "Electronics",
    "Health Appliances",
    "Food",
    "Sports",
    "Toys",
    "Books",
    "Movies",
    "Cars",
    "Others"
  )

  private val customCropImage = registerForActivityResult(CropImageContract()) {
    if (it !is CropImage.CancelledResult) {
      setImage(it.uriContent)
      Log.d(TAG, it.uriContent.toString())
    }
  }

  private val addProductViewModel: AddProductViewModel by viewModels()

  private var isNetworkAvailable = true

  private fun setImage(uriContent: Uri?) {
    uriContent?.let {
      addProductViewModel.uri = it
      binding.uploadImage.setImageURI(it)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentAddProductsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

    binding.spinner.adapter = arrayAdapter

    binding.addProduct.setOnClickListener {
      val productName = binding.productNameEdt.text.toString()
      val productPrice = binding.productPriceEdt.text.toString()
      val productTax = binding.productTaxEdt.text.toString()
      val productCategory = binding.spinner.selectedItem.toString()

      if (isNetworkAvailable) {
        if (validate(productName, productCategory, productTax, productPrice)) {
          addProductViewModel.addProduct(getFile(), productName, productCategory, productPrice, productTax)
        } else {
          showSnackBar("Please Fill All Fields")
        }
      } else {
        showSnackBar("No Internet Connection")
      }
    }

    binding.uploadImage.setOnClickListener {
      customCropImage.launch(
        CropImageContractOptions(
          uri = null,
          cropImageOptions = CropImageOptions(
            imageSourceIncludeCamera = false,
            imageSourceIncludeGallery = true,
            fixAspectRatio = true,
            guidelines = ON
          ),
        ),
      )
    }

    lifecycleScope.launch {
      NetworkObserver.subscribe<Boolean> {
        isNetworkAvailable = it
      }
    }

    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        addProductViewModel.uploadProducts.collect { uploadProductsResponse ->
          when(uploadProductsResponse) {
            is Error -> {
              with(binding) {
                addProduct.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                showSnackBar(uploadProductsResponse.message)
              }
            }
            is Loading -> {
              with(binding) {
                addProduct.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
              }
            }
            is Success -> {
              with(binding) {
                showSnackBar("Product Successfully Added")
                progressBar.visibility = View.GONE
                addProduct.visibility = View.VISIBLE
                productNameEdt.setText("")
                productPriceEdt.setText("")
                productTaxEdt.setText("")
                spinner.setSelection(0)
                uploadImage.setImageResource(R.drawable.upload_image)
              }
            }

            is Idle -> {
              // no operation
            }
          }
        }
      }
    }
  }

  private fun showSnackBar(description: String?) {
    Snackbar.make(binding.scrollView, "$description", 2000).show()
  }

  private fun validate(
    productName: String,
    productCategory: String,
    productTax: String,
    productPrice: String
  ): Boolean {
    return !(productCategory == categories[0] ||
      productTax.isEmpty() ||
      productPrice.isEmpty() ||
      productName.isEmpty())
  }

  private fun getFile(): File? {
    val file = addProductViewModel.uri?.let {  uri ->
      val fileDir = requireContext().applicationContext.filesDir
      val file = File(fileDir, "sample_image")
      val inputStream = requireContext().contentResolver.openInputStream(uri)
      val outputStream = FileOutputStream(file)
      inputStream?.copyTo(outputStream)
      file
    }
    return file
  }
}