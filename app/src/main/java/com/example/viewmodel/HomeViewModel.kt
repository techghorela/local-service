package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.ServiceEntity
import com.example.data.local.UserPreferences
import com.example.data.model.HisarBlock
import com.example.data.repository.ServiceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val selectedBlock: String = HisarBlock.HISAR_1.displayName,
    val selectedCategory: String = "",
    val allServices: List<ServiceEntity> = emptyList(),
    val popularServices: List<ServiceEntity> = emptyList(),
    val filteredServices: List<ServiceEntity> = emptyList(),
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val serviceRepository: ServiceRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _isLoading = MutableStateFlow(true)

    init {
        viewModelScope.launch {
            serviceRepository.seedServicesIfNeeded()
            delay(400) // Brief subtle loading transition for skeleton preview
            _isLoading.value = false
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        userPreferences.selectedBlockFlow,
        _selectedCategory,
        serviceRepository.allServices,
        serviceRepository.popularServices,
        _isLoading
    ) { block, category, allServices, popular, loading ->
        val filtered = if (category.isEmpty()) {
            allServices
        } else {
            allServices.filter { it.category.equals(category, ignoreCase = true) }
        }
        HomeUiState(
            selectedBlock = block,
            selectedCategory = category,
            allServices = allServices,
            popularServices = popular,
            filteredServices = filtered,
            isLoading = loading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun onBlockSelected(blockName: String) {
        viewModelScope.launch {
            userPreferences.setSelectedBlock(blockName)
        }
    }

    fun onCategorySelected(categoryName: String) {
        _selectedCategory.value = categoryName
    }
}

class HomeViewModelFactory(
    private val serviceRepository: ServiceRepository,
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(serviceRepository, userPreferences) as T
    }
}
