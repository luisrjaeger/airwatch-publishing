# airwatch-publishing
Gradle plugin to publish Android apks to Airwatch Console API
https://plugins.gradle.org/plugin/luisrjaeger.airwatch-publishing

# Usage
Airwatch plugin for Gradle to upload and deploy Android apks on Airwatch Console API.
It creates `publishing airwatch` task group and tasks `publish...ToAirwatch` and `validate...Installation` for each build variant.

```
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "br.com.luisrjaeger:airwatch-publishing:+"
  }
}

apply plugin: "luisrjaeger.airwatch-publishing"

airwatch {
    validateVersionOnPublishing = false //Default true
    organizationGroupId = 1234
    applicationName = 'Application Name'
    serverUrl = 'http://airwatch-server/'
    apiKey = 'xxxxxxxxxxxxxxxx'
    userName = 'username'
    password = '********'
}
```

