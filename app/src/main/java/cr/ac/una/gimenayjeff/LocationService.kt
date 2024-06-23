package cr.ac.una.gimenayjeff

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import cr.ac.una.gimenayjeff.clases.Movimiento
import cr.ac.una.gimenayjeff.clases.thumbnail
import cr.ac.una.gimenayjeff.controller.PageController
import cr.ac.una.gimenayjeff.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LocationService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private val pageController = PageController()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var notificationManager: NotificationManager
    private var contNotificacion = 2

    private var lastLatitude: Double? = null
    private var lastLongitude: Double? = null

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Places.initialize(applicationContext, "AIzaSyBLiFVeg7U_Ugu5bMf7EQ_TBEfPE3vOSF4")
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel()
        this.startForeground(1, createNotification("Service running"))

        requestLocationUpdates()
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            "locationServiceChannel",
            "Location Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(serviceChannel)
    }

    private fun createNotification(message: String): Notification {
        return NotificationCompat.Builder(this, "locationServiceChannel")
            .setContentTitle("Location Service")
            .setContentText(message)
            .setSmallIcon(R.drawable.appicon)
            .build()
    }

    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000
        ).apply {
            setMinUpdateIntervalMillis(5000)
        }.build()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.locations.forEach { location ->
                val latitude = location.latitude
                val longitude = location.longitude

                // Check if the location has changed significantly
                if (hasLocationChanged(latitude, longitude)) {
                    lastLatitude = latitude
                    lastLongitude = longitude
                    getPlaceName(latitude, longitude)
                }
            }
        }
    }

    private fun hasLocationChanged(newLatitude: Double, newLongitude: Double): Boolean {
        val threshold = 0.001 // Change this value to adjust sensitivity
        if (lastLatitude == null || lastLongitude == null) {
            return true
        }
        val latDiff = Math.abs(newLatitude - lastLatitude!!)
        val lonDiff = Math.abs(newLongitude - lastLongitude!!)
        return latDiff > threshold || lonDiff > threshold
    }

    @SuppressLint("MissingPermission")
    private fun getPlaceName(latitude: Double, longitude: Double) {
        val placeFields: List<Place.Field> = listOf(Place.Field.NAME)
        val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)
        val placesClient: PlacesClient = Places.createClient(this)

        val placeResponse = placesClient.findCurrentPlace(request)
        placeResponse.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val response = task.result
                val topPlace = response.placeLikelihoods
                    .maxByOrNull { it.likelihood }

                topPlace?.let { placeLikelihood ->
                    val placeName = placeLikelihood.place.name ?: "Unknown"
                    Log.d("LocationService", "Lugar: $placeName, Probabilidad: ${placeLikelihood.likelihood}")
                    searchWikipediaAndNotify(placeName, latitude, longitude)
                }
            } else {
                val exception = task.exception
                if (exception is ApiException) {
                    Log.e("LocationService", "Lugar no encontrado: ${exception.statusCode}")
                }
            }
        }
    }

    private fun sendNotification(placeName: String, articleTitle: String, latitude: Double, longitude: Double, wikipediaUrl: String, _descripcion: String, _img: thumbnail) {
        contNotificacion++

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("url", wikipediaUrl)
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            contNotificacion,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val formattedArticleTitle = articleTitle.replace("_", " ")
        val coordinates = "Latitud: $latitude, Longitud: $longitude"
        // Guardar datos de la notificación en la base de datos
        guardarDatosDeNotificacion(coordinates, placeName, formattedArticleTitle, wikipediaUrl, _descripcion, _img)


        val collapsedView = RemoteViews(packageName, R.layout.notificacion_colapsada).apply {
            setTextViewText(R.id.title, "Nuevo lugar: $placeName")
            setTextViewText(R.id.coordinates, coordinates)
            setOnClickPendingIntent(R.id.collapsed_notification, pendingIntent)
        }

        val expandedView = RemoteViews(packageName, R.layout.notificacion_expandida).apply {
            setTextViewText(R.id.title, placeName)
            setTextViewText(R.id.message, formattedArticleTitle)
            setTextViewText(R.id.coordinates, coordinates)
            setOnClickPendingIntent(R.id.boton_notificacion, pendingIntent)
        }

        val notification = NotificationCompat.Builder(this, "locationServiceChannel")
            .setSmallIcon(R.drawable.appicon)
            .setContentTitle("Nuevo lugar: $placeName")
            .setCustomContentView(collapsedView)
            .setCustomBigContentView(expandedView)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(contNotificacion, notification)
    }

    private fun searchWikipediaAndNotify(placeName: String, latitude: Double, longitude: Double) {
        val formattedQuery = placeName.replace(" ", "_")
        serviceScope.launch {
            try {
                val resultadoBusqueda = withContext(Dispatchers.IO) {
                    pageController.Buscar(formattedQuery)
                }
                if (resultadoBusqueda.isNotEmpty()) {
                    val firstResultTitle = resultadoBusqueda.first().title
                    val wikipediaUrl = "https://es.wikipedia.org/wiki/$firstResultTitle"
                    val _descripcion = resultadoBusqueda.first().extract
                    val _img = resultadoBusqueda.first().thumbnail
                    sendNotification(placeName, firstResultTitle, latitude, longitude, wikipediaUrl, _descripcion, _img)
                    Log.d("ResultadoBusqueda", "Se encontró información: $resultadoBusqueda")
                } else {
                    Log.d("ResultadoBusqueda", "No se encontró información")
                }
            } catch (e: HttpException) {
                Log.e("HTTP_ERROR", "No se encontró información. Error: ${e.message}")
            } catch (e: Exception) {
                Log.e("ERROR", "No se encontró información. Error: ${e.message}")
            }
        }
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    //nuevo
    private fun guardarDatosDeNotificacion(coordinates: String, name: String, message: String, _URL: String, _descripcion: String, _img: thumbnail) {
        val formattedMessage = message.replace(" ", "_")
        val currentDateAndTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
            Date()
        )

        val imageUrl = _img.source

        val movimiento = Movimiento(
            id = null,  // Se autogenerará
            Coordenadas = coordinates,
            FechayHora = currentDateAndTime,
            NombreWiki = formattedMessage,
            Nombre = name,
            Imagen = imageUrl,
            Descripcion = _descripcion,
            URL = _URL,
        )

        // Insertar el movimiento en la base de datos
        serviceScope.launch {
            val db = AppDatabase.getInstance(this@LocationService)
            withContext(Dispatchers.IO) {
                db.ubicacionDao().insert(movimiento)
            }
        }
    }
}