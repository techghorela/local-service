package com.example.data.repository

import com.example.data.local.InitialData
import com.example.data.local.ServiceDao
import com.example.data.local.ServiceEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ServiceRepository(
    private val serviceDao: ServiceDao
) {
    val allServices: Flow<List<ServiceEntity>> = serviceDao.getAllServices().map { list ->
        if (list.isEmpty()) {
            // Fallback to initial services if database hasn't finished seeding
            InitialData.defaultServices
        } else {
            list
        }
    }

    val popularServices: Flow<List<ServiceEntity>> = serviceDao.getPopularServices().map { list ->
        if (list.isEmpty()) {
            InitialData.defaultServices.filter { it.isPopular }
        } else {
            list
        }
    }

    fun getServicesByCategory(category: String): Flow<List<ServiceEntity>> {
        return serviceDao.getServicesByCategory(category).map { list ->
            if (list.isEmpty()) {
                InitialData.defaultServices.filter { it.category.equals(category, ignoreCase = true) }
            } else {
                list
            }
        }
    }

    fun searchServices(query: String): Flow<List<ServiceEntity>> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            return allServices
        }
        return serviceDao.searchServices(trimmed).map { list ->
            if (list.isEmpty()) {
                InitialData.defaultServices.filter {
                    it.name.contains(trimmed, ignoreCase = true) ||
                    it.category.contains(trimmed, ignoreCase = true) ||
                    it.description.contains(trimmed, ignoreCase = true)
                }
            } else {
                list
            }
        }
    }

    suspend fun getServiceById(id: String): ServiceEntity? {
        return serviceDao.getServiceById(id) ?: InitialData.defaultServices.find { it.id == id }
    }

    suspend fun seedServicesIfNeeded() {
        if (serviceDao.getServicesCount() == 0) {
            serviceDao.insertServices(InitialData.defaultServices)
        }
    }
}
