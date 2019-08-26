package br.com.luisrjaeger.airwatch.model

class Airwatch {

    String serverUrl

    String applicationName

    String filePath

    String apiKey

    String userName

    String password

    String pushMode = "Auto"

    Integer organizationGroupId

    Boolean validateVersionOnPublishing = true

    validateOptions() {
        if (!applicationName) throw new Exception("airwatch.applicationName not defined and it's mandatory")
        if (!serverUrl) throw new Exception("airwatch.serverUrl not defined and it's mandatory")
        if (!apiKey) throw new Exception("airwatch.apiKey not defined and it's mandatory")
        if (!userName) throw new Exception("airwatch.userName not defined and it's mandatory")
        if (!password) throw new Exception("airwatch.password not defined and it's mandatory")
        if (!organizationGroupId) throw new Exception("airwatch.organizationGroupId not defined and it's mandatory")
    }

}