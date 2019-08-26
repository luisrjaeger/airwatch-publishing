package br.com.luisrjaeger.airwatch.task

import br.com.luisrjaeger.airwatch.api.RequestAPI
import br.com.luisrjaeger.airwatch.helper.AppFilterHelper
import br.com.luisrjaeger.airwatch.model.Airwatch
import br.com.luisrjaeger.airwatch.model.DeviceStatus
import br.com.luisrjaeger.airwatch.model.InstallApplication
import br.com.luisrjaeger.airwatch.model.response.Search
import br.com.luisrjaeger.airwatch.model.response.SearchDevice
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class UninstallOlderTask extends DefaultTask {

    Airwatch airwatch

    String bundleId

    String version

    RequestAPI requestAPI

    UninstallOlderTask() { }

    @TaskAction
    def validateInstallation() {
        airwatch.validateOptions()

        requestAPI = new RequestAPI(airwatch.serverUrl, airwatch.apiKey, airwatch.userName, airwatch.password)

        println "Searching Bundle - $bundleId - Keeping Version $version"
        println "**********************"

        def apps = getExistingApplications()

        println "${apps.size()} applications found!"
        println "**********************"

        if (apps.isEmpty()) {
            println "No older applications found!"
            println "**********************"
            return
        }

        for (def app : apps) {
            println "**********************"

            println "Searching Devices with applicationId ${app.id} - ${app.AppVersion} ${DeviceStatus.Installed}"
            def devices = searchDevicesWith(app.id, DeviceStatus.Installed)

            if (devices.isEmpty()) {
                println "No remain installation found for ${app.id}!"
                println "**********************"
                continue
            }

            println ""
            println "Starting uninstall..."
            println ""

            for (def deviceId : devices) {
                sendUninstall(app.id, deviceId)
            }

            println ""
            println "**********************"
        }

        println "Uninstall command succeeded!"
        println "**********************"
    }

    private List<Search.Application> getExistingApplications() {
        return AppFilterHelper.filterBundle(
            requestAPI.searchApplication(bundleId)?.Application,
            airwatch.organizationGroupId
        )
    }

    private List<Integer> searchDevicesWith(Integer applicationId, DeviceStatus status) {
        SearchDevice search = requestAPI.searchDevice(applicationId, status)
        def list = search?.DeviceId ?: [ ]

        println ""
        println "${search?.Total ?: 0} devices found!"
        println "$list"
        println ""

        return list
    }

    private sendUninstall(Integer appId, Integer deviceId = null) {
        if (requestAPI.uninstallAppFromDevice(new InstallApplication(applicationId: appId, DeviceId: deviceId))) {
            print "$deviceId "
        } else {
            print "$deviceId(FAILURE) "
        }
    }

}
