package com.electricfeel.gsierra.mapbook.model.api

import com.electricfeel.gsierra.mapbook.model.data.foursquare.FoursquareJSON
import com.electricfeel.gsierra.mapbook.utils.Constants
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by gsierra on 11/03/2018.
 */

/**
 * Api Service for retrieving data from Foursquare
 */
interface FoursquareAPI {

    /**Search for food*/
    @GET("/v2/venues/search")
    fun getFoodNearby(@Query("client_id") clientID: String,
                      @Query("client_secret") clientSecret: String,
                      @Query("v") vDate: String,
                      @Query("categoryId") categoryId: String,
                      @Query("radius") radius: Int,
                      @Query("ll") ll: String): Observable<FoursquareJSON>

    companion object {
        fun create(): FoursquareAPI {
            /**For Logging details*/
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            return Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .baseUrl(Constants.FOURSQUARE_BASE_URL)
                    .build()
                    .create(FoursquareAPI::class.java)
        }
    }
}
