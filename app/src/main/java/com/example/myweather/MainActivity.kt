package com.example.myweather

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.myweather.databinding.ActivityMainBinding
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.text.DecimalFormat
import com.example.myweather.WeatherCollection

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var etCity: EditText
    private lateinit var etCountry: EditText
    private lateinit var tvResult: TextView

    private val url: String = "http://api.openweathermap.org/data/2.5/weather"

    private lateinit var appId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        etCity = binding.etCity
        etCountry = binding.etCountry
        tvResult = binding.tvResult
    }

    private fun convertToCelsius(fah: Double): Double {
        val df = DecimalFormat("#.##")
        return df.format(fah - 273.15).toDouble()
    }

    fun getWeatherDetails(view: android.view.View) {
        val tempUrl: String
        val city: String = etCity.text.toString().trim()
        val country: String = etCountry.text.toString().trim()

        if (city == "") {
            tvResult.text = "都市名が無記入です。"
        } else {
            appId = BuildConfig.API_KEY
            tempUrl = if (country != "") {
                "$url?q=$city,$country&appid=$appId"
            } else {
                "$url?q=$city&appid=$appId"
            }
            val stringRequest = StringRequest(Request.Method.POST, tempUrl,
                { response ->
                    val el = Json.decodeFromString<WeatherCollection>(response)
                    val output: String = "${el.name}(${el.sys.country})の現在の天気" +
                            "\n 気温: ${convertToCelsius(el.main.temp)}℃" +
                            "\n 体感温度: ${convertToCelsius(el.main.feelsLike)}℃" +
                            "\n 湿度: ${el.main.humidity}%" +
                            "\n 説明: ${el.weather[0].description}" +
                            "\n 風速: ${el.wind.speed}m/s (メートル秒)" +
                            "\n 雲量: ${el.clouds.all}%" +
                            "\n 気圧: ${el.main.pressure}hPa"
                    tvResult.text = output

                },
                { error ->
                    Toast.makeText(view.context, error.toString().trim(), Toast.LENGTH_SHORT).show()
                })
            val requestQueue: RequestQueue = Volley.newRequestQueue(this)
            requestQueue.add(stringRequest)
        }
    }
}