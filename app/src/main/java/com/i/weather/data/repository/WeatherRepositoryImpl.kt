package com.i.weather.data.repository

import com.i.weather.data.mappers.toWeatherInfo
import com.i.weather.data.remote.ApiService
import com.i.weather.domain.repository.WeatherRepository
import com.i.weather.domain.util.Resource
import com.i.weather.domain.weather.WeatherInfo
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(private val apiService: ApiService) : WeatherRepository {

    override suspend fun getWeatherData(latitude: Double, longitude: Double): Resource<WeatherInfo> =
        try {
            Resource.Success(apiService.getWeatherData(latitude, longitude).toWeatherInfo())
        } catch (exception: Exception) {
            Resource.Error(message = exception.message ?: "An unknown error occurred.")
        }

}