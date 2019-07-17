package br.com.luisrjaeger.airwatch.api

import br.com.luisrjaeger.airwatch.model.BeginInstall
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor

import java.util.concurrent.TimeUnit

class RequestAPI {

    private String serverUrl

    private String apiKey

    private String userName

    private String password

    private okHttpClient = new OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(buildInterceptor())
        .build()

    RequestAPI(String serverUrl, String apiKey, String userName, String password) {
        this.serverUrl = serverUrl
        this.apiKey = apiKey
        this.userName = userName
        this.password = password
    }

    def searchApplication(String bundleId) {
        return okHttpClient.newCall(buildSearchRequest(bundleId)).execute()
    }

    private Request buildSearchRequest(String bundleId) {
        return new Request.Builder()
            .url("${serverUrl}api/mam/apps/search?bundleid=${bundleId}")
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Basic ${buildBasicAuth()}")
            .addHeader("aw-tenant-code", "${apiKey}")
            .get().build()
    }

    def sendApk(File file) {
        return okHttpClient.newCall(buildApkRequest(file)).execute()
    }

    private Request buildApkRequest(File file) {
        return new Request.Builder()
            .url("${serverUrl}api/mam/blobs/uploadblob?filename=${file.name}") //organizationGroupId
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/octet-stream")
            .addHeader("Authorization", "Basic ${buildBasicAuth()}")
            .addHeader("aw-tenant-code", "${apiKey}")
            .post(RequestBody.create(MediaType.parse("application/octet-stream"), file)
            ).build()
    }

    def saveApplication(BeginInstall beginInstall) {
        return okHttpClient.newCall(buildSaveRequest(beginInstall)).execute()
    }

    private Request buildSaveRequest(BeginInstall beginInstall) {
        return new Request.Builder()
            .url("${serverUrl}api/mam/apps/internal/begininstall")
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Basic ${buildBasicAuth()}")
            .addHeader("aw-tenant-code", "${apiKey}")
            .post(
                RequestBody.create(
                    MediaType.parse("application/json"),
                    new Gson().toJson(beginInstall)
                )
            ).build()
    }

    private static HttpLoggingInterceptor buildInterceptor() {
        def interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            void log(String message) {
                if (message.startsWith("{")) println "HTTP BODY LOG - $message"
            }
        })
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        return interceptor
    }

    private String buildBasicAuth() {
        return "${userName}:${password}".bytes.encodeBase64()
    }

}
