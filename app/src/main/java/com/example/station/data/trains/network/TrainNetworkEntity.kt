package com.example.station.data.trains.network

import com.google.gson.annotations.SerializedName

data class TrainNetworkEntity(

    @SerializedName("trainNumber")
    val number: Int,

    @SerializedName("trainType")
    val type: String,

    @SerializedName("trainCategory")
    val category: String,

    @SerializedName("runningCurrently")
    val runningCurrently: Boolean,

    @SerializedName("timeTableRows")
    val timetable: List<TimetableRowNetworkEntity>
)
