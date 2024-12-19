package com.ibrahim.kotlinmaps.view

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.ibrahim.kotlinmaps.R
import com.ibrahim.kotlinmaps.RoomDb.PlaceDao
import com.ibrahim.kotlinmaps.RoomDb.PlaceDatabase
import com.ibrahim.kotlinmaps.databinding.ActivityMapsBinding
import com.ibrahim.kotlinmaps.model.Place
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher:ActivityResultLauncher<String>
    private lateinit var sharedPreferences:SharedPreferences
    private var bool:Boolean?=null
    private var selectedLatitude:Double?=null
    private var selectedLongitude:Double?=null
    private lateinit var db:PlaceDatabase
    private lateinit var placeDao: PlaceDao
    private var compositeDisposable=CompositeDisposable()
    var placeformain:Place?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        registerLauncher()

        sharedPreferences=this.getSharedPreferences("com.ibrahim.kotlinmaps", MODE_PRIVATE)
        bool=false
        bool=sharedPreferences.getBoolean("key",false)

        selectedLatitude=0.0
        selectedLongitude=0.0

        db=Room.databaseBuilder(applicationContext,PlaceDatabase::class.java,"Places").build()
        placeDao=db.placeDao()

        binding.button.isEnabled=false
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)

        /////////////////////////////////////////////////////////////////////////////////////////

        //ilk önce intenti çekecegiz ondan sonra new mi old mu ona göre yolumuza bakacagız
        val intent=intent
        val info=intent.getStringExtra("info")



        if(info=="new"){
            println("if e girdi")



            binding.button.visibility=View.VISIBLE
            binding.button2.visibility=View.INVISIBLE

            locationManager=this.getSystemService(LOCATION_SERVICE) as LocationManager

            locationListener=object :LocationListener{
                override fun onLocationChanged(location: Location) {
                    //konum değiştiğinde ne olacak
                    if(bool==false){
                        var userLocation= LatLng(location.latitude,location.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,12f))
                        mMap.addMarker(MarkerOptions().position(userLocation).title("UserLocation"))
                        println("location: ${userLocation}")
                        println("konumunu aldim")
                        sharedPreferences.edit().putBoolean("key",true).apply()
                    }

                }

            }
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                    Snackbar.make(binding.root,"Give Permiison for Location",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                        View.OnClickListener {
                            //izin al
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }).show()
                }else{
                    //izin al
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }else{
                //zaten izin vardır direk yaz
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
                var lastLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if(lastLocation!=null){
                    val lastuserlocation=LatLng(lastLocation.latitude,lastLocation.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastuserlocation,12f))
                }
            }
        }
        else
        {
            println("else girdi")
            mMap.clear()

            val placeformain = intent.getSerializableExtra("key") as? Place

            if(placeformain!=null){
                placeformain?.let {
                    binding.button.visibility=View.INVISIBLE
                    binding.button2.visibility=View.VISIBLE

                    binding.editTextText.setText(it.name)

                    val konum=LatLng(it.latitude,it.longitude)
                    println("konum: ${it.latitude}")
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(konum,12f))
                    mMap.addMarker(MarkerOptions().position(konum).title(it.name))
                }
            }


        }









    }
    private fun registerLauncher(){
        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){result->
            if(result){
                //izini yazdım
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
                    //kesin eminiz
                    var lastLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if(lastLocation!=null){
                        val lastuserlocation=LatLng(lastLocation.latitude,lastLocation.longitude)
                        mMap.addMarker(MarkerOptions().position(lastuserlocation).title("lastlocation"))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastuserlocation,12f))
                    }
                }else{
                    println("girmememsi lazım!!!!!!!")
                }

            }else{
                Toast.makeText(this,"Give Permission",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMapLongClick(p0: LatLng) {
        selectedLatitude=p0.latitude
        selectedLongitude=p0.longitude
        mMap.clear()
        if((selectedLatitude!=null)&&(selectedLongitude!=null)){
            var konum=LatLng(selectedLatitude!!,selectedLongitude!!)
            mMap.addMarker(MarkerOptions().position(konum))
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(konum,12f))
        }
        binding.button.isEnabled=true

    }
    fun save(view:View){
        val place=Place(binding.editTextText.text.toString(),selectedLatitude!!,selectedLongitude!!)
        //placeDao.insert(place)//çok kısa yöntem hemen ekledik ****************
        //bikeren yukarıdaki gibi eklemeyecegiz rxjava yı kullanarak ekleyecegiz
        //onun içinde composite disposible kullanacagız
        compositeDisposable.add(
            placeDao.insert(place)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )
        //yeni bir fonksiyon kullanmamaız gerekiyor çünkü subscribe den sonra ne olacağını yazmamaız gerekiyor
        //bu yüzden handle responsw adında yeni bir fonksiyon tanımlarız

    }


    private fun handleResponse(){
        val intent= Intent(this@MapsActivity,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }


    fun delete(view:View){
        println("delete girdi")
        placeformain?.let {
            compositeDisposable.add(
                placeDao.delete(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse)
            )
            println("silindi")
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}