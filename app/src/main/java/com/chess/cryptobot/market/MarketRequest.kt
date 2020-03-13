package com.chess.cryptobot.market

import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.model.response.ErrorResponse
import com.chess.cryptobot.model.response.MarketResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private const val DEFAULT_ENCODING = "UTF-8"

abstract class MarketRequest(val url: String, apiKey: String?, secretKey: String?) : Market {
    var path: String? = null
    var algorithm: String? = null
    val apiKey: String = apiKey ?: ""
    private val secretKey: String = secretKey ?: ""

    abstract fun initGson(): Gson

    open fun initHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(45, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build()
    }

    fun initRetrofit(gson: Gson): Retrofit {
        return Retrofit.Builder()
                .baseUrl(url)
                .client(initHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
    }



    abstract fun initService(retrofit: Retrofit): Any
    override fun keysIsEmpty(): Boolean {
        return apiKey.isEmpty() || secretKey.isEmpty()
    }

    private fun encode(value: String): String {
        return asString(encodeToBytes(value))
    }

    private fun encodeToBytes(value: String): ByteArray {
        return try {
            val keySpec = SecretKeySpec(
                    secretKey.toByteArray(charset(DEFAULT_ENCODING)), algorithm)
            val mac = Mac.getInstance(algorithm)
            mac.init(keySpec)
            mac.doFinal(value.toByteArray(charset(DEFAULT_ENCODING)))
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException(e)
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException(e)
        }
    }

    private fun asString(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (b in bytes) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString().toUpperCase(Locale.getDefault())
    }

    private fun buildQueryString(args: Map<String, String>): String {
        val result = StringBuilder()
        for (param in args.keys) {
            if (result.isNotEmpty()) result.append('&')
            try {
                result.append(URLEncoder.encode(param, DEFAULT_ENCODING))
                        .append("=").append(URLEncoder.encode(args[param], DEFAULT_ENCODING))
            } catch (ex: Exception) {
                ex.printStackTrace()
                result.append(param).append("=").append(param)
            }
        }
        return result.toString()
    }

    fun makeHash(queryParams: Map<String, String>): String {
        return encode(String.format(Locale.US, "%s%s", path, buildQueryString(queryParams)))
    }

    @Throws(MarketException::class)
    fun execute(call: Call<out MarketResponse?>): MarketResponse {
        val response: MarketResponse?
        try {
            val result: Response<*> = call.execute()
            response = result.body() as MarketResponse?
            if (response == null) {
                val errorBody = result.errorBody()
                if (errorBody == null) {
                    throw MarketException("No response")
                } else {
                    val gson = GsonBuilder().create()
                    val errorResponse = gson.fromJson(errorBody.string(), ErrorResponse::class.java)
                    throw MarketException(errorResponse.errorMessage)
                }
            } else if (!response.success()) {
                throw MarketException(response.message())
            }
        } catch (e: Exception) {
            throw MarketException(e.message)
        }
        return response
    }

}