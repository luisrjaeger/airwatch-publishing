package br.com.luisrjaeger.airwatch.model

class Airwatch {

    public String serverUrl

    public String applicationName

    public String filePath

    public String fileName

    public String apiKey

    public String userName

    public String password

    public String pushMode = "Auto"

    public Integer organizationGroupId

    public Boolean validateVersionOnPublishing = true

    void validateOptions() {
        if (!applicationName) throw new Exception("airwatch.applicationName not defined and it's mandatory")
        if (!serverUrl) throw new Exception("airwatch.serverUrl not defined and it's mandatory")
        if (!apiKey) throw new Exception("airwatch.apiKey not defined and it's mandatory")
        if (!userName) throw new Exception("airwatch.userName not defined and it's mandatory")
        if (!password) throw new Exception("airwatch.password not defined and it's mandatory")
        if (!organizationGroupId) throw new Exception("airwatch.organizationGroupId not defined and it's mandatory")
    }

}