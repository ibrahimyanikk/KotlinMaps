package com.ibrahim.kotlinmaps.RoomDb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ibrahim.kotlinmaps.model.Place
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface PlaceDao
{
    @Query("SELECT * FROM Place")
    fun getall():Flowable<List<Place>>
    //bu getall bir liste döndürcek ve liste nin içinde de Place olacak

    @Insert
    fun insert(place:Place):Completable

    @Delete
    fun delete(place:Place):Completable
}