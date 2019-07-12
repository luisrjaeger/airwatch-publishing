package br.com.luisrjaeger.airwatch.model

class BeginInstall {

    public String FileName

    public Integer BlobId

    public Integer LocationGroupId

    public String ApplicationId

    public String DeviceType = "Android"

    public String PushMode

    public boolean AutoUpdateVersion = false

    public String ApplicationName

    public SupportedModels SupportedModels = new SupportedModels()

}
