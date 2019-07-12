package br.com.luisrjaeger.airwatch.task

import br.com.luisrjaeger.airwatch.model.Airwatch
import br.com.luisrjaeger.airwatch.model.BeginInstall
import br.com.luisrjaeger.airwatch.model.response.BeginInstall as RespBeginInstall
import br.com.luisrjaeger.airwatch.model.response.UploadBlob
import com.google.gson.Gson
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.util.concurrent.TimeUnit

class PublishTask extends DefaultTask {

    Airwatch airwatch

    String filePath

    String bundleId

    File file

    BeginInstall beginInstall

    private okHttpClient = new OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(buildInterceptor())
        .build()

    PublishTask() { }

    @TaskAction
    def postToAirwatch() {
        if (!airwatch.applicationName) throw new Exception("airwatch.applicationName not defined and it's mandatory")
        if (!airwatch.serverUrl) throw new Exception("airwatch.serverUrl not defined and it's mandatory")
        if (!airwatch.apiKey) throw new Exception("airwatch.apiKey not defined and it's mandatory")
        if (!airwatch.userName) throw new Exception("airwatch.userName not defined and it's mandatory")
        if (!airwatch.password) throw new Exception("airwatch.password not defined and it's mandatory")

        println "Sending APK - ${airwatch.applicationName}"
        println "**********************"

        loadApkFile()

        def blobId = postApk()

        if (!blobId) throw new Exception("Unable to get blobId")

        println "APK sent!"
        println "**********************"

        def id = postSave(blobId)

        if (!id) throw new Exception("Unable to save application on Airwatch")

        println "Application saved!"
        println "**********************"
    }

    private Integer postApk() {
        def responseApk = okHttpClient.newCall(buildApkRequest()).execute()

        if (!responseApk.successful) throw new Exception(responseApk.message())

        UploadBlob uploadBlob = getResponse(responseApk, UploadBlob.class)
        responseApk.close()

        return uploadBlob.Value
    }

    private Integer postSave(Integer blobId) {
        beginInstall = loadBeginInstall()
        beginInstall.BlobId = blobId

        def responseSave = okHttpClient.newCall(buildSaveRequest()).execute()
        if (!responseSave.successful) throw new Exception(responseSave.message())

        RespBeginInstall rbi = getResponse(responseSave, RespBeginInstall.class)
        responseSave.close()

        return rbi.Id.Value
    }

    private BeginInstall loadBeginInstall() {
        def begin = new BeginInstall()
        begin.ApplicationId = bundleId
        begin.ApplicationName = airwatch.applicationName
        begin.FileName = file.name
        begin.PushMode = airwatch.pushMode
        begin.LocationGroupId = airwatch.organizationGroup ? airwatch.organizationGroup.id : null

        return begin
    }

    private void loadApkFile() {
        println "File Path - ${filePath}"

        def directory = new File(filePath)
        file = directory.listFiles().find {
            it.name.contains(".apk")
        }

        if (file == null) throw new Exception("APK not Found")

        println "File name - ${file.name}"
    }

    private String buildBasicAuth() {
        return "${airwatch.userName}:${airwatch.password}".bytes.encodeBase64()
    }

    private Request buildApkRequest() {
        return new Request.Builder()
            .url("${airwatch.serverUrl}api/mam/blobs/uploadblob?filename=${file.name}") //organizationGroupId
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/octet-stream")
            .addHeader("Authorization", "Basic ${buildBasicAuth()}")
            .addHeader("aw-tenant-code", "${airwatch.apiKey}")
            .post(RequestBody.create(MediaType.parse("application/octet-stream"), file)
            ).build()
    }

    private Request buildSaveRequest() {
        return new Request.Builder()
            .url("${airwatch.serverUrl}api/mam/apps/internal/begininstall")
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Basic ${buildBasicAuth()}")
            .addHeader("aw-tenant-code", "${airwatch.apiKey}")
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

    private static <T> T getResponse(Response response, Class<T> clazz) {
        return (T) new Gson().fromJson(response.body().string().toString(), clazz)
    }

}
