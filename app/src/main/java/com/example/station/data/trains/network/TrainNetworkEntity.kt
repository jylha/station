package com.example.station.data.trains.network

import com.google.gson.annotations.SerializedName

data class TrainNetworkEntity(

    @SerializedName("trainNumber")
    val number: Int,

    @SerializedName("trainType")
    val type: String,

    @SerializedName("trainCategory")
    val category: String,

    @SerializedName("commuterLineID")
    val commuterLineId: String? = null,

    @SerializedName("runningCurrently")
    val runningCurrently: Boolean,

    @SerializedName("cancelled")
    val cancelled: Boolean,

    @SerializedName("version")
    val version: Long,

    @SerializedName("timeTableRows")
    val timetable: List<TimetableRowNetworkEntity>
)
