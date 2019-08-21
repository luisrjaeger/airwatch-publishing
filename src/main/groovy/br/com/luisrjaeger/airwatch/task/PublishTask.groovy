package br.com.luisrjaeger.airwatch.task

import br.com.luisrjaeger.airwatch.api.RequestAPI
import br.com.luisrjaeger.airwatch.helper.AppFilterHelper
import br.com.luisrjaeger.airwatch.model.Airwatch
import br.com.luisrjaeger.airwatch.model.BeginInstall
import br.com.luisrjaeger.airwatch.model.response.BeginInstall as RespBeginInstall
import br.com.luisrjaeger.airwatch.model.response.UploadBlob
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class PublishTask extends DefaultTask {

    Airwatch airwatch

    String bundleId

    String version

    File file

    String filePath

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

        if (getExistingApplication()) throw new Exception("Bundle $bundleId version $version already present on AirWatch")

        println "Sending APK - ${airwatch.applicationName}"
        println "**********************"

        def blobId = postApk()

        if (!blobId) throw new Exception("Unable to get blobId")

        println "APK file $blobId sent!"
        println "**********************"

        println "Saving application..."

        def id = postSave(blobId)

        if (!id) throw new Exception("Unable to save application on Airwatch")

        println "Application saved!"
        println "**********************"
    }

    private boolean getExistingApplication() {
        println "Validate version? ${airwatch.validateVersionOnPublishing}"
        if (!airwatch.validateVersionOnPublishing) return false

        println "Searching Bundle - $bundleId - Version $version"
        println "**********************"

        return AppFilterHelper.existVersion(
            requestAPI.searchApplication(bundleId)?.Application,
            version,
            airwatch.organizationGroupId
        )
    }

    private Integer postApk() {
        loadApkFile()
        UploadBlob uploadBlob = requestAPI.sendApk(file)

        return uploadBlob.Value
    }

    private Integer postSave(Integer blobId) {
        beginInstall = loadBeginInstall()
        beginInstall.BlobId = blobId

        RespBeginInstall rbi = requestAPI.saveApplication(beginInstall)

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

}
