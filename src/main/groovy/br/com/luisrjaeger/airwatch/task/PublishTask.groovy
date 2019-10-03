package br.com.luisrjaeger.airwatch.task

import br.com.luisrjaeger.airwatch.api.AirwatchAPI
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

    String fileName

    BeginInstall beginInstall

    AirwatchAPI api

    PublishTask() { }

    @TaskAction
    def postToAirwatch() {
        airwatch.validateOptions()

        api = new AirwatchAPI(airwatch.serverUrl, airwatch.apiKey, airwatch.userName, airwatch.password)

        //if (getExistingApplication()) throw new Exception("Bundle $bundleId version $version already present on AirWatch")
        if (getExistingApplication()) {
            println "Bundle $bundleId version $version already present on AirWatch"
            return
        }

        println "Sending APK - ${airwatch.applicationName}"
        separator()

        def blobId = postApk()

        if (!blobId) throw new Exception("Unable to get blobId")

        println "APK file $blobId sent!"
        separator()

        println "Saving application..."

        def id = postSave(blobId)

        if (!id) throw new Exception("Unable to save application on Airwatch")

        println "Application $id saved!"
        separator()
    }

    private boolean getExistingApplication() {
        println "Validate version? ${airwatch.validateVersionOnPublishing}"
        if (!airwatch.validateVersionOnPublishing) return false

        println "Searching Bundle - $bundleId - Version $version"
        separator()

        return AppFilterHelper.existVersion(
            api.searchApplication(bundleId)?.Application,
            version,
            airwatch.organizationGroupId
        )
    }

    private Integer postApk() {
        loadApkFile()
        UploadBlob uploadBlob = api.sendApk(file, airwatch.organizationGroupId)

        return uploadBlob.Value
    }

    private Integer postSave(Integer blobId) {
        beginInstall = loadBeginInstall()
        beginInstall.BlobId = blobId

        RespBeginInstall rbi = api.saveApplication(beginInstall)

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
        def files = directory.listFiles()

        separator()
        println fileName
        println files
        separator()

        file = files.find {
            /*it.name.contains(fileName)*/it.name.contains(".apk")
        }

        if (file == null) throw new Exception("APK not Found")
        println "File name - ${file.name}"
    }

    private static void separator() {
        println '**********************'
    }

}
