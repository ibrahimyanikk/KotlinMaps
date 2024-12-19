package com.ibrahim.kotlinmaps.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

//Room Database******************************************

@Entity
class Place(
    @ColumnInfo(name="name")
    var name:String,

    @ColumnInfo(name="latitude")
    var latitude:Double,

    @ColumnInfo(name="longitude")
    var longitude:Double):Serializable
    //id yi burda belirtmeyiz çünkü construtor oluştururken id yi biz yazmayız
    //bu yüzden onu aşağıda primary key olarak id yi yazacagız
{
    @PrimaryKey(autoGenerate = true)
    var id=0
}