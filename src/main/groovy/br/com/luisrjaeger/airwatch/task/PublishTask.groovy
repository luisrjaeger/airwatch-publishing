package br.com.luisrjaeger.airwatch.task

import br.com.luisrjaeger.airwatch.model.Airwatch
import br.com.luisrjaeger.airwatch.model.BeginInstall
import com.google.gson.Gson
import com.google.gson.JsonParser
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

    //'Authorization: Basic YXBpYWRtaW46cmVubmVyQDEyMw==' 'aw-tenant-code: O2UkzdtEXxC+dIcDce6UjUHQeI++IFxLjcxSj0hKpa4=' 'Accept: application/json' 'Content-Type: application/octet-stream' --data-binary @remarcacao-dsv-2.6.2.3.apk http://awds.lojasrenner.com.br/api/mam/blobs/uploadblob?filename=remarcacao-dsv-2.6.2.3.apk"
    //'Authorization: Basic YXBpYWRtaW46cmVubmVyQDEyMw==' 'aw-tenant-code: O2UkzdtEXxC+dIcDce6UjUHQeI++IFxLjcxSj0hKpa4=' 'Accept: application/json' 'Content-Type: application/json' -d '{\"FileName\": \"remarcacao-dsv-2.6.2.3.apk\", \"BlobId\": 81509, \"LocationGroupId\": 2103, \"ApplicationId\": \"br.com.lojasrenner.remarcacao.test\", \"DeviceType\": \"Android\", \"PushMode\": \"Auto\", \"AutoUpdateVersion\": false, \"ApplicationName\": \"RemarcacaoTeste\", \"SupportedModels\" : { \"Model\" : [{ \"ModelName\" : \"Android\" }] } }' http://awds.lojasrenner.com.br/api/mam/apps/internal/begininstall"
    PublishTask() { }

    @TaskAction
    def postToAirwatch() {

        if (!airwatch.applicationName) throw new Exception("airwatch.applicationName not defined and it's mandatory")
        if (!airwatch.serverUrl) throw new Exception("airwatch.serverUrl not defined and it's mandatory")
        if (!airwatch.apiKey) throw new Exception("airwatch.apiKey not defined and it's mandatory")
        if (!airwatch.userName) throw new Exception("airwatch.userName not defined and it's mandatory")
        if (!airwatch.password) throw new Exception("airwatch.password not defined and it's mandatory")

        println "Sending APK - ${airwatch.applicationName}"

        loadApkFile()

        def blobId = postApk()

        if (!blobId) throw new Exception("Unable to get blobId")


    }

    private Integer postApk() {
        def responseApk = okHttpClient.newCall(buildApkRequest()).execute()
        def apkId

        if (!responseApk.successful) throw new Exception(responseApk.message())

        def json = new JsonParser().parse(responseApk.body().string().toString()).getAsJsonObject()
        apkId = json.get('Value').getAsInt()

        println "**** RESPONSE APK ****"
        println json
        println apkId
        println "******************"

        responseApk.close()

        return apkId
    }

    private Integer postSave(Integer blobId) {
        beginInstall = loadBeginInstall()
        beginInstall.BlobId = blobId
        def id

        def responseSave = okHttpClient.newCall(buildSaveRequest()).execute()
        if (!responseSave.successful) throw new Exception(responseSave.message())

        def json = new JsonParser().parse(responseSave.body().string().toString()).getAsJsonObject()
        id = json.get('Id').getAsInt()

        println "**** RESPONSE SAVE ****"
        println json
        println id
        println "******************"

        responseSave.close()

        return id
    }

    private BeginInstall loadBeginInstall() {
        def begin = new BeginInstall()
        begin.ApplicationId = bundleId
        begin.ApplicationName = airwatch.applicationName
        begin.FileName = file.name
        begin.PushMode = airwatch.pushMode

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
            .url("${airwatch.serverUrl}api/mam/blobs/uploadblob?filename=${file.name}")
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/octet-stream")
            .addHeader("Authorization", "Basic ${buildBasicAuth()}")
            .addHeader("aw-tenant-code", "${airwatch.apiKey}")
            .post(new MultipartBody.Builder()
                .addFormDataPart("apk", file.name,
                    RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build()
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
                println "HTTP LOG - $message"
            }
        })
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        return interceptor
    }

}
