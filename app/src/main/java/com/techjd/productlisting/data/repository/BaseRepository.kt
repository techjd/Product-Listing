package com.techjd.productlisting.data.repository

import android.util.Log
import com.google.gson.Gson
import com.techjd.productlisting.utils.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response

private const val ERROR_MESSAGE = "Please Try Again Later"

abstract class BaseRepository {

  private val TAG = "ERROR"

  suspend fun <T> safeApiCall(
    apiToBeCalled: suspend () -> Response<T>,
  ): NetworkResult<T> {
    return withContext(Dispatchers.IO) {
      try {
        val data: Response<T> = apiToBeCalled()
        if (data.isSuccessful) {
          data.body()?.let { response ->
            NetworkResult.Success(response)
          } ?: NetworkResult.Error(ERROR_MESSAGE)
        } else {
          val errorObject = data.errorBody()?.charStream()?.readText()?.let { JSONObject(it) }
          Log.d(TAG, "safeApiCall: $errorObject")
          NetworkResult.Error(ERROR_MESSAGE)
        }
      } catch (t: Throwable) {
        Log.d(TAG, "safeApiCall: throwable ${t.message}")
        NetworkResult.Error(ERROR_MESSAGE)
      }
    }
  }
}