# airwatch-publishing
Airwatch plugin for Gradle

# Usage
Airwatch plugin for Gradle to upload and deploy Android apks on Airwatch Console API.
It creates `publishing airwatch` task group and a task `publishToAirwatch` for each build variant.

```
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "br.com.luisrjaeger:airwatch-publishing:0.0.+"
  }
}

apply plugin: "luisrjaeger.airwatch-publishing"

airwatch {
    applicationName = 'Application Name'
    serverUrl = 'http://airwatch-server/'
    apiKey = 'xxxxxxxxxxxxxxxx'
    userName = 'username'
    password = '********'
    organizationGroupId = 1234
}

```

