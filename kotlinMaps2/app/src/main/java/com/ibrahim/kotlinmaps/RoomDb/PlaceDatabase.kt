package com.ibrahim.kotlinmaps.RoomDb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ibrahim.kotlinmaps.model.Place

@Database(entities = [Place::class], version = 1)
abstract class PlaceDatabase : RoomDatabase() {
    abstract fun placeDao(): PlaceDao
}