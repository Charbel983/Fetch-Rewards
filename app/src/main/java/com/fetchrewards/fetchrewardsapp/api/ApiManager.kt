package com.fetchrewards.fetchrewardsapp.api

import com.fetchrewards.fetchrewardsapp.models.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApiManager {

    private val apiService = RetrofitManager.apiService

    suspend fun fetchItems() : Map<Int, List<Item>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getItems()
                if (response.isSuccessful) {
                    response.body()
                        ?.filter { !it.name.isNullOrBlank() }
                        ?.groupBy { it.listId }
                        ?.toSortedMap()
                        ?: emptyMap()
                } else {
                    emptyMap()
                }
            } catch (e : Exception) {
                emptyMap()
            }
        }
    }

}