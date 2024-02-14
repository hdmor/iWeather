package com.i.weather.domain.repository

import com.i.weather.domain.util.Resource
import com.i.weather.domain.weather.WeatherInfo

interface WeatherRepository {

    suspend fun getWeatherData(latitude: Double, longitude: Double): Resource<WeatherInfo>
}