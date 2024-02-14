package com.i.weather.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.i.weather.domain.location.LocationTracker
import com.i.weather.domain.repository.WeatherRepository
import com.i.weather.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationTracker: LocationTracker
) : ViewModel() {

    var state by mutableStateOf(WeatherState())
        private set

    fun loadWeatherInfo() {
        viewModelScope.launch {

            state = state.copy(isLoading = true, error = null)
            locationTracker.getCurrentLocation()?.let {
                state = when (val result = weatherRepository.getWeatherData(it.latitude, it.longitude)) {
                    is Resource.Success -> state.copy(weatherInfo = result.data, isLoading = false, error = null)
                    is Resource.Error -> state.copy(weatherInfo = null, isLoading = false, error = result.message)
                }
            } ?: kotlin.run {
                state = state.copy(isLoading = false, error = "Couldn't retrieve location. Make sure grant permission and enable GPS.")
            }
        }
    }

}