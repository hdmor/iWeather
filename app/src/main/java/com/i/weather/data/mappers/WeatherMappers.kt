package com.i.weather.data.mappers

import com.i.weather.data.remote.WeatherDataDto
import com.i.weather.data.remote.WeatherDto
import com.i.weather.domain.weather.WeatherData
import com.i.weather.domain.weather.WeatherInfo
import com.i.weather.domain.weather.WeatherType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private data class IndexedWeatherData(
    val index: Int,
    val data: WeatherData
)

fun WeatherDataDto.toWeatherDataMap(): Map<Int, List<WeatherData>> = time.mapIndexed { index, time ->

    IndexedWeatherData(
        index = index,
        data = WeatherData(
            time = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME),
            temperatureCelsius = temperatures[index],
            pressure = pressures[index],
            windSpeed = windSpeeds[index],
            humidity = humidities[index],
            weatherType = WeatherType.fromWMO(weatherCodes[index])
        )
    )
}.groupBy {
    it.index / 24
}.mapValues { entry ->
    entry.value.map { it.data }
}

fun WeatherDto.toWeatherInfo(): WeatherInfo {
    val weatherDataMap = weatherData.toWeatherDataMap()
    val now = LocalDateTime.now()
    val currentWeatherData = weatherDataMap[0]?.find {
        val hour = if (now.minute < 30) now.hour else now.hour + 1
        it.time.hour == hour
    }
    return WeatherInfo(weatherDataPerDay = weatherDataMap, currentWeatherData = currentWeatherData)
}

