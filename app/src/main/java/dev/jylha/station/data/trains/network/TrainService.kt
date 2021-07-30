package dev.jylha.station.data.trains.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TrainService {

    @GET("trains/{date}/{number}")
    suspend fun fetchTrain(
        @Path("date") date: String,
        @Path("number") number: Int,
        @Query("version") version: Long?,
    ): List<TrainNetworkEntity>

    @GET("trains/latest/{number}")
    suspend fun fetchLatestTrain(
        @Path("number") number: Int,
        @Query("version") version: Long?,
    ): List<TrainNetworkEntity>

    @GET("live-trains/station/{stationCode}")
    suspend fun fetchTrainsByTime(
        @Path("stationCode") stationCode: String,
        @Query("minutes_before_departure") minutesBeforeDeparture: Int = 120,
        @Query("minutes_after_departure") minutesAfterDeparture: Int = 30,
        @Query("minutes_before_arrival") minutesBeforeArrival: Int = 120,
        @Query("minutes_after_arrival") minutesAfterArrival: Int = 30,
        @Query("train_categories") trainCategories: String? = "Long-Distance,Commuter",
        @Query("version") version: Long? = null,
    ): List<TrainNetworkEntity>

    @GET("live-trains/station/{stationCode}")
    suspend fun fetchTrainsByCount(
        @Path("stationCode") stationCode: String,
        @Query("arrived_trains") arrived: Int = 5,
        @Query("arriving_trains") arriving: Int = 20,
        @Query("departed_trains") departed: Int = 5,
        @Query("departing_trains") departing: Int = 20,
        @Query("train_categories") trainCategories: String? = "Long-Distance,Commuter",
        @Query("version") version: Long? = null,
    ): List<TrainNetworkEntity>

    @GET("metadata/cause-category-codes")
    suspend fun fetchCauseCategoryCodes(): List<CauseCategoryNetworkEntity>

    @GET("metadata/detailed-cause-category-codes")
    suspend fun fetchDetailedCauseCategoryCodes(): List<DetailedCauseCategoryNetworkEntity>

    @GET("metadata/third-cause-category-codes")
    suspend fun fetchThirdLevelCauseCategoryCodes(): List<ThirdLevelCauseCategoryNetworkEntity>
}
