package br.com.luisrjaeger.airwatch.api

import br.com.luisrjaeger.airwatch.model.BeginInstall
import br.com.luisrjaeger.airwatch.model.response.BeginInstall as RespBeginInstall
import br.com.luisrjaeger.airwatch.model.DeviceStatus
import br.com.luisrjaeger.airwatch.model.InstallApplication
import br.com.luisrjaeger.airwatch.model.response.Search
import br.com.luisrjaeger.airwatch.model.response.SearchDevice
import br.com.luisrjaeger.airwatch.model.response.UploadBlob
import okhttp3.Request

class AirwatchAPI {

    Client client

    AirwatchAPI(String serverUrl, String apiKey, String userName, String password) {
        client = new Client(serverUrl, apiKey, userName, password)
    }

    Search searchApplication(String bundleId) {
        return client.runRequest(buildSearchRequest(bundleId), Search.class)
    }

    UploadBlob sendApk(File file, Integer organizationGroupId) {
        return client.runRequest(buildApkRequest(file, organizationGroupId), UploadBlob.class)
    }

    RespBeginInstall saveApplication(BeginInstall beginInstall) {
        return client.runRequest(buildSaveRequest(beginInstall), RespBeginInstall.class)
    }

    SearchDevice searchDevice(Integer applicationId, DeviceStatus status) {
        return client.runRequest(buildSearchDevices(applicationId, status), SearchDevice.class)
    }

    boolean installAppOnDevice(InstallApplication install) {
        return client.runRequest(buildInstallUninstallOnDevice(install)).successful
    }

    boolean uninstallAppFromDevice(InstallApplication install) {
        return client.runRequest(buildInstallUninstallOnDevice(install, false)).successful
    }

    private Request buildSearchRequest(String bundleId) {
        return client.buildGet(Endpoint.APPS_SEARCH, bundleId)
    }

    private Request buildSearchDevices(Integer applicationId, DeviceStatus status) {
        return client.buildGet(Endpoint.DEVICES_ID, applicationId, status)
    }

    private Request buildApkRequest(File file, Integer organizationGroupId) {
        return client.buildStreamPost(file, Endpoint.UPLOAD_BLOB, file.name, organizationGroupId)
    }

    private Request buildSaveRequest(BeginInstall beginInstall) {
        return client.buildPost(beginInstall, Endpoint.BEGIN_INSTALL)
    }

    private Request buildInstallUninstallOnDevice(InstallApplication install, boolean cmdInstall = true) {
        return client.buildPost(install, Endpoint.INSTALL_UNINSTALL, install.applicationId, cmdInstall ? 'install' : 'uninstall')
    }

}