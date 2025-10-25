package com.example.lab_week_07

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.lab_week_07.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    // Deklarasikan ActivityResultLauncher di sini
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi requestPermissionLauncher di dalam onCreate
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                // Izin diberikan, panggil fungsi untuk mendapatkan lokasi
                getLastLocation()
            } else {
                // Izin ditolak, beri tahu pengguna atau tampilkan dialog
                // Di sini kita bisa menampilkan kembali dialog penjelasan (rationale)
                // sebagai contoh jika pengguna menolak.
                showPermissionRationale {
                    requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
                }
            }
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Pindahkan logika pengecekan izin ke sini
        checkLocationPermission()

        // Contoh menambahkan marker di Sydney
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private fun checkLocationPermission() {
        when {
            hasLocationPermission() -> {
                // Izin sudah ada, langsung dapatkan lokasi
                getLastLocation()
            }
            shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) -> {
                // Tampilkan dialog penjelasan mengapa izin ini dibutuhkan
                showPermissionRationale {
                    requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
                }
            }
            else -> {
                // Langsung minta izin jika belum pernah ditolak/diterima
                requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
            }
        }
    }

    // Fungsi ini harus berada di dalam kelas
    private fun getLastLocation() {
        // Peringatan keamanan akan muncul di sini karena kita belum secara eksplisit
        // memeriksa izin di dalam fungsi ini. Namun, kita sudah melakukannya
        // di checkLocationPermission sebelum memanggil fungsi ini.
        // Anda dapat menambahkan anotasi untuk menekan peringatan ini jika perlu.
        Log.d("MapsActivity", "getLastLocation() dipanggil. Siap untuk mengambil lokasi.")
        // TODO: Tambahkan logika untuk mengambil lokasi terakhir pengguna di sini
        // Contoh: fusedLocationProviderClient.lastLocation.addOnSuccessListener { ... }
    }

    // Fungsi ini harus berada di dalam kelas
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Contoh fungsi untuk menampilkan dialog penjelasan (rationale)
    private fun showPermissionRationale(onPositiveClick: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Izin Lokasi Dibutuhkan")
            .setMessage("Aplikasi ini memerlukan izin lokasi untuk menampilkan lokasi Anda di peta.")
            .setPositiveButton("OK") { _, _ ->
                onPositiveClick()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
