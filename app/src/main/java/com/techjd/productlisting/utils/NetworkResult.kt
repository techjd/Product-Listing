package com.techjd.productlisting.utils

sealed class NetworkResult<T>(val data: T?, val message: String?) {
  class Success<T>(data: T) : NetworkResult<T>(data, null)
  class Error<T>(message: String?, data: T? = null) :
    NetworkResult<T>(null, message)
  class Loading<T> : NetworkResult<T>(null, null)
  class Idle<T>(): NetworkResult<T>(null, null)
}