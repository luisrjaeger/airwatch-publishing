package br.com.luisrjaeger.airwatch.model.response

class BeginInstall {

    String ApplicationName

    String BundleId

    String AppVersion

    Integer Platform

    Boolean IsReimbursable

    Integer ApplicationSource

    Integer LocationGroupId

    String OrganizationGroupUuid

    Integer PushMode

    Integer AppRank

    Integer AssignedDeviceCount

    Integer InstalledDeviceCount

    Integer NotInstalledDeviceCount

    Boolean AutoUpdateVersion

    Boolean EnableProvisioning

    Boolean IsDependencyFile

    Integer ContentGatewayId

    public Id Id

    String Uuid

    class Id {
        public Integer Value
    }

}