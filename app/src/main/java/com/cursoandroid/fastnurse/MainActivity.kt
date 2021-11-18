package com.cursoandroid.fastnurse

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.shape.MarkerEdgeTreatment
import java.util.jar.Manifest

// añadimos la interfaz OnMapReadyCallback.
class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var mark: MarkerOptions

    //Método de la interfaz onMapReadyCallback
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableMyLocation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createMapFragment() // LLama a la función que inicializa el fragmento de layout
    }

    // función encargada de inicializar el fragment que hemos creado en el layout
    private fun createMapFragment() {
        // Variable que decimos a supportFragmentManager que busque un fragment que tenga una id llamada fragmentMap, que será la id del fragment que añadimos en activity_main.xml
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this) //la inicializamos con la función getMapAsync(this).
    }

    // Función para comprobar permiso de localización
    private fun isPermissionsGranted() = ContextCompat.checkSelfPermission(
        this,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED // Si permisos están activados o no.

    // Función para comprobar si se ha iniciado mapa
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        if (isPermissionsGranted()) {
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermision()
        }
    }

    // Función para solicitar los permisos
    companion object {
        const val REQUEST_CODE_LOCATION = 0 // Código para saber si es nuestro permiso el aceptado
    }

    private fun requestLocationPermision() {
        // Si entra en if ya se habrían rechazado permisos
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(
                this,
                " Please go to settings and accept the permissions",
                Toast.LENGTH_SHORT
            ).show()
        } else { // Si entra en else nunca se han pedido permisos, se hace a traves de ActivityCompact.requestPermisions pasando activity (this), permiso que queremos que acepte, y código creado.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(
                    this,
                    "To activate the location go to settings and accept the permissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {}
        }
    }
// Función para que no rompa app en caso anulación de perms en settings
    @SuppressLint("MissingPermission")
    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return // Si el mapa ha sido cargado
        if (!isPermissionsGranted()) { // Si los permisos están activos
            map.isMyLocationEnabled = false // Si no, desactivamos localización en tiempo real
            Toast.makeText(
                this,
                "To activate the location go to settings and accept the permissions",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}