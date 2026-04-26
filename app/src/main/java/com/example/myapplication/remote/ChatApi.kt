package com.example.myapplication.remote

import com.example.myapplication.model.Message
import com.example.myapplication.model.SimpleResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ChatApi {
    @GET("vku_app/get_messages.php")
    suspend fun getMessages(
        @Query("sender_id") senderId: Int,
        @Query("receiver_id") receiverId: Int
    ): List<Message>

    @FormUrlEncoded
    @POST("vku_app/send_message.php")
    suspend fun sendMessages(
        @Field("sender_id") senderId: Int,
        @Field("receiver_id") receiverId: Int,
        @Field("content") content: String
    ): Message

    @FormUrlEncoded
    @POST("vku_app/delete_message.php")
    suspend fun deleteMessages(
        @Field("message_id") messageId: Int
    ): SimpleResponse

    companion object {
        private const val BASE_URL = "http://10.0.2.2:8081/"

        fun create(): ChatApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ChatApi::class.java)
        }
    }
}