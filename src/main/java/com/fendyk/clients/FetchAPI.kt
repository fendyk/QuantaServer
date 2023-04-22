package com.fendyk.clients

import com.fendyk.Main
import com.fendyk.utilities.Log
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

abstract class FetchAPI<K, DTO, UpdateDTO>(private val dtoType: Class<DTO>) {
    enum class RequestMethod(val method: String) {
        GET("GET"),
        POST("POST"),
        PATCH("PATCH"),
        DELETE("DELETE")
    }

    fun fetch(url: String, requestMethod: RequestMethod, data: Any?): CompletableFuture<DTO> {
        val body: RequestBody? = if (data != null) Main.gson.toJson(data).toRequestBody(JSON) else null
        val request: Request = requestBuilder
                .url(Companion.url + "/minecraftusers/" + url)
                .method(requestMethod.toString(), body)
                .build()
        return CompletableFuture.supplyAsync {
            try {
                client.newCall(request).execute().use { response ->
                    if (main.serverConfig.isInDebugMode) {
                        Log.info("")
                        Log.info("Request URL: " + request.url)
                        Log.info("Response Code: " + response.code)
                        Log.info("Response Message: " + response.message)
                        Log.info("")
                    }
                    if (!response.isSuccessful) {
                        throw IOException("Unexpected response code: " + response.code)
                    }
                    response.body.use { responseBody ->
                        if (responseBody == null) return@supplyAsync null
                        val json = responseBody.string()
                        if (main.serverConfig.isInDebugMode) {
                            Log.info("Response Body: $json")
                        }
                        return@supplyAsync Main.gson.fromJson<DTO>(
                                json,
                                dtoType
                        )
                    }
                }
            } catch (e: IOException) {
                Log.error("Error in fetchFromApi: " + e.message)
                Log.error("Stacktrace: " + Arrays.toString(e.stackTrace))
                return@supplyAsync null
            }
        }
    }

    open fun get(key: K): CompletableFuture<DTO> {
        val future = CompletableFuture<DTO>()
        future.completeExceptionally(Exception("GET is not implemented yet."))
        return future;
    }
    open fun create(dto: DTO): CompletableFuture<DTO> {
        val future = CompletableFuture<DTO>()
        future.completeExceptionally(Exception("CREATE is not implemented yet."))
        return future;
    }
    open fun update(key: K, dto: UpdateDTO): CompletableFuture<DTO> {
        val future = CompletableFuture<DTO>()
        future.completeExceptionally(Exception("UPDATE is not implemented yet."))
        return future;
    }
    open fun delete(key: K): CompletableFuture<DTO> {
        val future = CompletableFuture<DTO>()
        future.completeExceptionally(Exception("DELETE is not implemented yet."))
        return future;
    }


    companion object {
        var main: Main = Main.getInstance()
        protected var client: OkHttpClient = OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build()
        @JvmField
        protected val JSON: MediaType? = ("application/json; charset=utf-8").toMediaTypeOrNull()
        protected lateinit var url: String;
        protected const val authHeader: String = "Authorization"
        @JvmField
        protected var requestBuilder: Request.Builder = Request.Builder()
        @JvmStatic
        fun connect(url: String, jwtKey: String) {
            FetchAPI.url = url;
            requestBuilder.addHeader(authHeader, "Bearer $jwtKey")
        }
    }
}
