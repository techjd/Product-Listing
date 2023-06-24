package com.techjd.productlisting.utils

import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance

object NetworkObserver{
  private val _events = MutableStateFlow<Boolean>(false)
  val events = _events.asSharedFlow()

  suspend fun publish(event: Boolean) {
    _events.emit(event)
  }

  suspend inline fun <reified T> subscribe(crossinline onEvent: (T) -> Unit) {
    events.filterIsInstance<T>()
      .collectLatest { event ->
        coroutineContext.ensureActive()
        onEvent(event)
      }
  }
}