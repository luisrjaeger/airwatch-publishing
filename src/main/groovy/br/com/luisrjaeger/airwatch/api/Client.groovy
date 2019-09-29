package br.com.luisrjaeger.airwatch.api


import com.google.gson.Gson
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor

import java.util.concurrent.TimeUnit

class Client {

    private String serverUrl

    private String apiKey

    private String userName

    private String password

    private okHttpClient = new OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        //.addInterceptor(buildInterceptor())
        .build()

    Client(String serverUrl, String apiKey, String userName, String password) {
        this.serverUrl = serverUrl
        this.apiKey = apiKey
        this.userName = userName
        this.password = password
    }

    Request buildGet(String endpoint, Object... args) {
        return buildUrl(endpoint, args).get().build()
    }

    Request buildDelete(String endpoint, Object... args) {
        return buildUrl(endpoint, args).delete().build()
    }

    public <T> Request buildPut(T body, String endpoint, Object... args) {
        return buildUrl(endpoint, args).put(buildBody(body)).build()
    }

    public <T> Request buildPost(T body, String endpoint, Object... args) {
        return buildUrl(endpoint, args).post(buildBody(body)).build()
    }

    Request buildStreamPost(File file, String endpoint, Object... args) {
        return buildUrl(endpoint, args).post(buildStreamBody(file)).build()
    }

    Response runRequest(Request request) {
        return okHttpClient.newCall(request).execute()
    }

    public <T> T runRequest(Request request, Class<T> clazz) {
        def response = runRequest(request)
        if (!response.successful) throw new Exception("${response.message()} - ${response.body().string()}")
        return getResponse(response, clazz)
    }

    private static <T> RequestBody buildBody(T body) {
        return RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(body))
    }

    private static RequestBody buildStreamBody(File file) {
        return RequestBody.create(MediaType.parse("application/octet-stream"), file)
    }

    private Request.Builder buildUrl(String endpoint, Object... args) {
        return buildHeader().url("${serverUrl}${String.format(endpoint, args)}")
    }

    private Request.Builder buildHeader() {
        return new Request.Builder()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/octet-stream")
            .addHeader("Authorization", "Basic ${buildBasicAuth()}")
            .addHeader("aw-tenant-code", "${apiKey}")
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

    private static <T> T getResponse(Response response, Class<T> clazz) {
        return (T) new Gson().fromJson(response.body().string().toString(), clazz)
    }

    private String buildBasicAuth() {
        return "${userName}:${password}".bytes.encodeBase64()
    }

}
