package br.com.luisrjaeger.airwatch.task

import br.com.luisrjaeger.airwatch.api.RequestAPI
import br.com.luisrjaeger.airwatch.model.Airwatch
import br.com.luisrjaeger.airwatch.model.BeginInstall
import br.com.luisrjaeger.airwatch.model.response.BeginInstall as RespBeginInstall
import br.com.luisrjaeger.airwatch.model.response.Search
import br.com.luisrjaeger.airwatch.model.response.UploadBlob
import com.google.gson.Gson
import okhttp3.Response
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class PublishTask extends DefaultTask {

    Airwatch airwatch

    String filePath

    String bundleId

    String version

    File file

    BeginInstall beginInstall

    RequestAPI requestAPI

    PublishTask() { }

    @TaskAction
    def postToAirwatch() {
        if (!airwatch.applicationName) throw new Exception("airwatch.applicationName not defined and it's mandatory")
        if (!airwatch.serverUrl) throw new Exception("airwatch.serverUrl not defined and it's mandatory")
        if (!airwatch.apiKey) throw new Exception("airwatch.apiKey not defined and it's mandatory")
        if (!airwatch.userName) throw new Exception("airwatch.userName not defined and it's mandatory")
        if (!airwatch.password) throw new Exception("airwatch.password not defined and it's mandatory")

        requestAPI = new RequestAPI(airwatch.serverUrl, airwatch.apiKey, airwatch.userName, airwatch.password)

        println "Searching Bundle - $bundleId - Version $version"
        println "**********************"

        if (getExistingApplication()) throw new Exception("Bundle $bundleId version $version already present on AirWatch")

        println "Sending APK - ${airwatch.applicationName}"
        println "**********************"

        def blobId = postApk()

        if (!blobId) throw new Exception("Unable to get blobId")

        println "APK sent!"
        println "**********************"

        def id = postSave(blobId)

        if (!id) throw new Exception("Unable to save application on Airwatch")

        println "Application saved!"
        println "**********************"
    }

    private boolean getExistingApplication() {
        def responseSearch = requestAPI.searchApplication(bundleId)

        if (!responseSearch.successful) throw new Exception(responseSearch.message())

        List<Search.Application> apps = getResponse(responseSearch, Search.class).Application

        return apps.any { it.AppVersion == version }
    }

    private Integer postApk() {
        loadApkFile()

        def responseApk = requestAPI.sendApk(file)

        if (!responseApk.successful) throw new Exception(responseApk.message())

        UploadBlob uploadBlob = getResponse(responseApk, UploadBlob.class)
        responseApk.close()

        return uploadBlob.Value
    }

    private Integer postSave(Integer blobId) {
        beginInstall = loadBeginInstall()
        beginInstall.BlobId = blobId

        def responseSave = requestAPI.saveApplication(beginInstall)
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
        begin.LocationGroupId = airwatch.organizationGroupId

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

    private static <T> T getResponse(Response response, Class<T> clazz) {
        return (T) new Gson().fromJson(response.body().string().toString(), clazz)
    }

}
