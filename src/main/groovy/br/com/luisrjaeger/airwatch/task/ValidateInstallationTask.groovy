package br.com.luisrjaeger.airwatch.task

import br.com.luisrjaeger.airwatch.api.RequestAPI
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
            println "Searching Devices with applicationId ${app.id} assigned"

            def devices = searchDevicesWith(app.getId(), DeviceStatus.Assigned)

            println "Searching Devices with applicationId ${app.id} installed"

            def devicesInstalled = searchDevicesWith(app.getId(), DeviceStatus.Installed)

            devices.removeAll(devicesInstalled)

            println "Were found ${devices.size()} devices without application installed."

            if (devices.isEmpty()) {
                println "No pending installation found for ${app.getId()}!"
                println "**********************"
                continue
            }

            println ""
            println "Stating installation..."
            println ""

            for (def deviceId : devices) {
                sendInstalltion(app.id, deviceId)
            }

            println "**********************"
        }

        println "Validate installation succeeded!"
        println "**********************"
    }

    private List<Search.Application> getExistingApplications() {
        List<Search.Application> apps = requestAPI.searchApplication(bundleId)?.Application
        return apps?.findAll { it.AppVersion == version } ?: [ ]
    }

    private List<Integer> searchDevicesWith(Integer applicationId, DeviceStatus status) {
        SearchDevice search = requestAPI.searchDevice(applicationId, status)

        println ""
        println "${search.Total} devices found!"
        println ""

        return search?.DevicesId ?: [ ]
    }

    private sendInstalltion(Integer appId, Integer deviceId) {
        println "Sending installation to device $deviceId"
        if (requestAPI.installAppOnDevice(new InstallApplication(applicationId: appId, DeviceId: deviceId))) {
            println "DONE!"
        } else {
            println "FAILURE!"
        }
    }

}
