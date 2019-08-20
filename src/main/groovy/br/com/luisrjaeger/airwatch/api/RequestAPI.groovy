package br.com.luisrjaeger.airwatch.api

import br.com.luisrjaeger.airwatch.model.BeginInstall
import br.com.luisrjaeger.airwatch.model.DeviceStatus
import br.com.luisrjaeger.airwatch.model.InstallApplication
import br.com.luisrjaeger.airwatch.model.response.BeginInstall as RespBeginInstall
import br.com.luisrjaeger.airwatch.model.response.Search
import br.com.luisrjaeger.airwatch.model.response.SearchDevice
import br.com.luisrjaeger.airwatch.model.response.UploadBlob
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
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
        //.addInterceptor(buildInterceptor())
        .build()

    RequestAPI(String serverUrl, String apiKey, String userName, String password) {
        this.serverUrl = serverUrl
        this.apiKey = apiKey
        this.userName = userName
        this.password = password
    }

    Search searchApplication(String bundleId) {
        return runRequest(buildSearchRequest(bundleId), Search.class)
    }

    UploadBlob sendApk(File file) {
        return runRequest(buildApkRequest(file), UploadBlob.class)
    }

    RespBeginInstall saveApplication(BeginInstall beginInstall) {
        return runRequest(buildSaveRequest(beginInstall), RespBeginInstall.class)
    }

    SearchDevice searchDevice(Integer applicationId, DeviceStatus status) {
        return runRequest(buildSearchDevices(applicationId, status), SearchDevice.class)
    }

    boolean installAppOnDevice(InstallApplication install) {
        return runRequest(buildInstallUninstallOnDevice(install)).successful
    }

    boolean uninstallAppFromDevice(InstallApplication install) {
        return runRequest(buildInstallUninstallOnDevice(install, false)).successful
    }

    private Request buildSearchRequest(String bundleId) {
        return buildHeader()
            .url("${serverUrl}api/mam/apps/search?bundleid=$bundleId&pagesize=10000")
            .get().build()
    }

    private Request buildSearchDevices(Integer applicationId, DeviceStatus status) {
        return buildHeader()
            .url("${serverUrl}api/mam/apps/internal/$applicationId/devices?status=$status&pagesize=10000")
            .get().build()
    }

    private Request buildApkRequest(File file) {
        return buildHeader()
            .url("${serverUrl}api/mam/blobs/uploadblob?filename=${file.name}") //could send organizationGroupId??
            .post(RequestBody.create(MediaType.parse("application/octet-stream"), file)).build()
    }

    private Request buildSaveRequest(BeginInstall beginInstall) {
        return buildHeader()
            .url("${serverUrl}api/mam/apps/internal/begininstall")
            .post(
                RequestBody.create(
                    MediaType.parse("application/json"),
                    new Gson().toJson(beginInstall)
                )
            ).build()
    }

    private Request buildInstallUninstallOnDevice(InstallApplication install, boolean cmdInstall = true) {
        return buildHeader()
            .url("${serverUrl}api/mam/apps/internal/${install.applicationId}/${ cmdInstall ? 'install' : 'uninstall' }")
            .post(
                RequestBody.create(
                    MediaType.parse("application/json"),
                    new Gson().toJson(install)
                )
            ).build()
    }

    private Response runRequest(Request request) {
        return okHttpClient.newCall(request).execute()
    }

    private <T> T runRequest(Request request, Class<T> clazz) {
        def response = runRequest(request)
        if (!response.successful) throw new Exception("${response.message()} - ${response.body().string()}")
        return getResponse(response, clazz)
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
