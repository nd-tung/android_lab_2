package com.example.networkinformation.httpRequest

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenCellIDApi {
    @GET("cell/get")
    fun getCellLocation(
        @Query("key") apiKey: String,
        @Query("mcc") mcc: Int,
        @Query("mnc") mnc: Int,
        @Query("lac") lac: Int,
        @Query("cellid") cellId: Int,
        @Query("format") format: String = "json"
    ): Call<CellLocationResponse>
}