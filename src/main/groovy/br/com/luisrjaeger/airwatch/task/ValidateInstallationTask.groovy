package br.com.luisrjaeger.airwatch.task

import br.com.luisrjaeger.airwatch.api.AirwatchAPI
import br.com.luisrjaeger.airwatch.helper.AppFilterHelper
import br.com.luisrjaeger.airwatch.model.Airwatch
import br.com.luisrjaeger.airwatch.model.DeviceStatus
import br.com.luisrjaeger.airwatch.model.InstallApplication
import br.com.luisrjaeger.airwatch.model.response.Search
import br.com.luisrjaeger.airwatch.model.response.SearchDevice
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class ValidateInstallationTask extends DefaultTask {

    Airwatch airwatch

    String bundleId

    String version

    AirwatchAPI api

    ValidateInstallationTask() { }

    @TaskAction
    def validateInstallation() {
        airwatch.validateOptions()

        api = new AirwatchAPI(airwatch.serverUrl, airwatch.apiKey, airwatch.userName, airwatch.password)

        println "Searching Bundle - $bundleId - Version $version"
        println "**********************"

        def apps = getExistingApplications()

        println "${apps.size()} applications found!"
        println "**********************"

        if (apps.isEmpty()) {
            println "No application to install!"
            println "**********************"
            return
        }

        for (def app : apps) {
            println "**********************"

            def devices = searchDevicesWith(app.id, DeviceStatus.Assigned)
            def devicesInstalled = searchDevicesWith(app.id, DeviceStatus.Installed)

            devices -= devicesInstalled

            println "Were found ${devices.size()} devices without application installed"
            println devices

            if (devices.isEmpty()) {
                println "No pending installation found for ${app.id}!"
                println "**********************"
                continue
            }

            println ""
            println "Starting installation..."
            println ""

            for (def deviceId : devices) {
                sendInstallation(app.id, deviceId)
            }

            println ""
            println "**********************"
        }

        println "Validate installation succeeded!"
        println "**********************"
    }

    private List<Search.Application> getExistingApplications() {
        return AppFilterHelper.filterVersion(
            api.searchApplication(bundleId)?.Application,
            version,
            airwatch.organizationGroupId
        )
    }

    private List<Integer> searchDevicesWith(Integer applicationId, DeviceStatus status) {
        println "Searching Devices with applicationId $applicationId $status"
        SearchDevice search = api.searchDevice(applicationId, status)
        def list = search?.DeviceId ?: [ ]

        println ""
        println "${search?.Total ?: 0} devices found!"
        println "$list"
        println ""

        return list
    }

    private sendInstallation(Integer appId, Integer deviceId) {
        if (api.installAppOnDevice(new InstallApplication(applicationId: appId, DeviceId: deviceId))) {
            print "$deviceId "
        } else {
            print "$deviceId(FAILURE) "
        }
    }

}
