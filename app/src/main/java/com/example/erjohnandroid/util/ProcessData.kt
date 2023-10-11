package com.example.erjohnandroid.util

object ProcessData {
    private fun <T> processData(
        state: ResultState<T>?,
        onLoading: () -> Unit = {},
        onSuccess: (data: T) -> Unit = {},
        onError: (exception: Throwable) -> Unit = {}
    ) {
        when (state) {
            is ResultState.Loading -> {
                // showCustomProgressDialog()
//                _binding!!.txtGetsynchingtext.append("\nGetting data")
                onLoading()
            }
            is ResultState.Success -> {
                state.data?.let {
                    onSuccess(it)
                }
            }
            is ResultState.Error -> {
               // Toast.makeText(this, "Error!! ${state.exception}", Toast.LENGTH_LONG).show()
                onError(state.exception)
            }
            else -> {}
        }
    }


}