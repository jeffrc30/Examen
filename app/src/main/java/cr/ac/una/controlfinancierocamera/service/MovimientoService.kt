package cr.ac.una.controlfinancierocamera.service

import com.google.gson.GsonBuilder
import cr.ac.una.controlfinancierocamera.AuthInterceptor
import cr.ac.una.jsoncrud.dao.MovimientoDAO
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MovimientoService {
    val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor("9IPgkGudSJz8ykWaKPR2MUztUvzJ421gE2RDc1-L8VAwllNevg"))
        .build()

    val gson = GsonBuilder().setPrettyPrinting().create()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://crudapi.co.uk/api/v1/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val apiService = retrofit.create(MovimientoDAO::class.java)

}