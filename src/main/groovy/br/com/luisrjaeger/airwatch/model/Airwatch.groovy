package br.com.luisrjaeger.airwatch.model

class Airwatch {

    String serverUrl

    String applicationName

    String apiKey

    String userName

    String password

    String pushMode = "Auto"

    OrganizationGroup organizationGroup = new OrganizationGroup()

    def organizationGroup(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = organizationGroup
        closure()
    }

}