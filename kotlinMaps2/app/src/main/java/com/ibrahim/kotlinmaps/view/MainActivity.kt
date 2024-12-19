package com.ibrahim.kotlinmaps.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.ibrahim.kotlinmaps.R
import com.ibrahim.kotlinmaps.RoomDb.PlaceDao
import com.ibrahim.kotlinmaps.RoomDb.PlaceDatabase
import com.ibrahim.kotlinmaps.adapter.PlaceAdapter
import com.ibrahim.kotlinmaps.databinding.ActivityMainBinding
import com.ibrahim.kotlinmaps.model.Place
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var db:PlaceDatabase
    private lateinit var placeDao: PlaceDao
    var compositeDisposable= CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //ilk önce activitymapse te olduğu gibi database ve placedao yu tanımlayacagız
        db= Room.databaseBuilder(applicationContext,PlaceDatabase::class.java,"Places").build()//key e dikkat
        placeDao=db.placeDao()
        //tabikide buradada composite disposible kullanaacgız

        compositeDisposable.add(
            placeDao.getall()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse))


    }
    private fun handleResponse(placeList : List<Place>){  //burası önemli
        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        val adapter=PlaceAdapter(placeList)
        binding.recyclerView.adapter=adapter

        adapter.notifyDataSetChanged()//************
        println("kac tane: ${placeList.size}")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuinflater=MenuInflater(this)
        menuinflater.inflate(R.menu.menuu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId== R.id.addplace){
            val intent= Intent(this@MainActivity, MapsActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}