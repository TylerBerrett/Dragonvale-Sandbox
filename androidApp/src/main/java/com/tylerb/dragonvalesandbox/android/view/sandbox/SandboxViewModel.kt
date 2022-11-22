package com.tylerb.dragonvalesandbox.android.view.sandbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tylerb.dragonvalesandbox.SharedRepository
import com.tylerb.dragonvalesandbox.model.DragonData
import com.tylerb.dragonvalesandbox.util.myResultRunCatching
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SandboxViewModel @Inject constructor(
    private val sharedRepository: SharedRepository
) : ViewModel() {

    enum class WhichDragon {
        One,
        Two,
        None;
    }

    enum class FilterName {
        Beb,
        Fast,
        Rift;
    }

    data class Filters(
        val beb: Boolean = false,
        val fast: Boolean = false,
        val rift: Boolean = false
    )

    data class SandboxUiState(
        val initLoading: Boolean = true,
        val initFailure: Boolean = false,
        val dragons: List<DragonData> = listOf(),
        val dragonOne: String = "Hail",
        val dragonTwo: String = "Hydra",
        val whichDragon: WhichDragon = WhichDragon.None,
        val searchQuery: String = "",
        val filters: Filters = Filters(),
        val spawn: List<DragonData> = listOf(),
    )

    private val _uiState = MutableStateFlow(SandboxUiState())

    val uiState: StateFlow<SandboxUiState>
        get() = _uiState

    private val allDragons = ArrayList<DragonData>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            myResultRunCatching { sharedRepository.getDragonList() }
                .onSuccess { dragons ->
                    allDragons.addAll(dragons)
                    _uiState.value = _uiState.value.copy(
                        initLoading = false,
                        dragons = dragons
                    )
                    getSpawn()
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        initLoading = false,
                        initFailure = true
                    )
                }
        }
    }

    fun onSearch(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query
        )
        if (query.isNotEmpty()) {
            val filteredDragons = allDragons.filter {
                it.name.lowercase().contains(query.lowercase())
            }
            _uiState.value = _uiState.value.copy(
                dragons = filteredDragons
            )
        } else {
            _uiState.value = _uiState.value.copy(
                dragons = allDragons
            )
        }
    }

    fun onFilterChecked(filter: FilterName) {

        val currentFilters = _uiState.value.filters
        val filters = when(filter) {
            FilterName.Beb -> currentFilters.copy(beb = !currentFilters.beb)
            FilterName.Fast -> currentFilters.copy(
                fast = !currentFilters.fast,
                rift = false
            )
            FilterName.Rift -> currentFilters.copy(
                rift = !currentFilters.rift,
                fast = false
            )
        }

        _uiState.value = _uiState.value.copy(
            filters = filters
        )

        getSpawn()
    }

    fun onWhichDragon(whichDragon: WhichDragon) {
        _uiState.value = _uiState.value.copy(
            whichDragon = whichDragon
        )
    }

    fun onDragonSelected(dragonData: DragonData) {
        _uiState.value = _uiState.value.copy(
            dragonOne = if (_uiState.value.whichDragon == WhichDragon.One){
                dragonData.name
            } else _uiState.value.dragonOne,
            dragonTwo = if (_uiState.value.whichDragon == WhichDragon.Two){
                dragonData.name
            } else _uiState.value.dragonTwo
        )
        getSpawn()
    }

    private fun getSpawn() {
        if (allDragons.isEmpty()) return
        val dragonOne = allDragons.find { it.name == _uiState.value.dragonOne }!!
        val dragonTwo = allDragons.find { it.name == _uiState.value.dragonTwo }!!

        val spawn = sharedRepository.breedCalc(
            allDragons,
            dragonOne,
            dragonTwo,
            _uiState.value.filters.beb,
            _uiState.value.filters.fast
        )

        _uiState.value = _uiState.value.copy(
            spawn = spawn ?: emptyList()
        )

    }







}