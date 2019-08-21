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

class ValidateInstallationTask extends DefaultTask {

    Airwatch airwatch

    String bundleId

    String version

    RequestAPI requestAPI

    ValidateInstallationTask() { }

    @TaskAction
    def validateInstallation() {
        if (!airwatch.serverUrl) throw new Exception("airwatch.serverUrl not defined and it's mandatory")
        if (!airwatch.apiKey) throw new Exception("airwatch.apiKey not defined and it's mandatory")
        if (!airwatch.userName) throw new Exception("airwatch.userName not defined and it's mandatory")
        if (!airwatch.password) throw new Exception("airwatch.password not defined and it's mandatory")

        requestAPI = new RequestAPI(airwatch.serverUrl, airwatch.apiKey, airwatch.userName, airwatch.password)

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

            println "**********************"
        }

        println "Validate installation succeeded!"
        println "**********************"
    }

    private List<Search.Application> getExistingApplications() {
        return AppFilterHelper.filterVersion(
            requestAPI.searchApplication(bundleId)?.Application,
            version,
            airwatch.organizationGroupId
        )
    }

    private List<Integer> searchDevicesWith(Integer applicationId, DeviceStatus status) {
        println "Searching Devices with applicationId $applicationId $status"
        SearchDevice search = requestAPI.searchDevice(applicationId, status)
        def list = search?.DeviceId ?: [ ]

        println ""
        println "${search?.Total ?: 0} devices found!"
        println "$list"
        println ""

        return list
    }

    private sendInstallation(Integer appId, Integer deviceId) {
        if (requestAPI.installAppOnDevice(new InstallApplication(applicationId: appId, DeviceId: deviceId))) {
            println "$deviceId - DONE!"
        } else {
            println "$deviceId - FAILURE!"
        }
        println ""
    }

}
