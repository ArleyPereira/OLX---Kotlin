package com.example.olx.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.olx.api.Repository
import com.example.olx.api.Resource
import kotlinx.coroutines.Dispatchers

class AddressViewModel : ViewModel() {
    fun getAddress(zipCode: String) = liveData(Dispatchers.IO) {
        try {
            // Armazena o resultado do retorno da API
            val address = Repository().getAddress(zipCode)

            // Retorna o resultado em caso de sucesso
            emit(Resource.onSuccess(address))
        } catch (exception: Exception) {
            emit(Resource.onFailure())
        }
    }
}