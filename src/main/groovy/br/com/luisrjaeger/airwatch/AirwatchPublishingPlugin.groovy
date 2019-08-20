package br.com.luisrjaeger.airwatch

import br.com.luisrjaeger.airwatch.model.Airwatch
import br.com.luisrjaeger.airwatch.task.PublishTask
import br.com.luisrjaeger.airwatch.task.UninstallOlderTask
import br.com.luisrjaeger.airwatch.task.ValidateInstallationTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class AirwatchPublishingPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def extension = project.extensions.create("airwatch", Airwatch)
        def android = project.extensions.findByName("android")

        if (!android) throw new Exception("This is not an Android project")

        android.applicationVariants.all { variant ->
            variant.outputs.all {
                project.tasks.create("publish${variant.name.capitalize()}ToAirwatch", PublishTask) { task ->
                    //Workaround until Google fix outputFile deprecated api call
                    //task.filePath = extension.filePath ?: outputFile.absolutePath.replace(outputFile.name, "")
                    task.filePath = extension.filePath ?:
                        variant.getPackageApplicationProvider().get().outputs.files[1]
                    task.bundleId = variant.applicationId
                    task.version = android.defaultConfig.versionName
                    task.airwatch = extension

                    group 'publishing airwatch'
                    description "Publish ${variant.name} apk to airwatch"
                }

                project.tasks.create("validate${variant.name.capitalize()}Installation", ValidateInstallationTask) { task ->
                    task.bundleId = variant.applicationId
                    task.version = android.defaultConfig.versionName
                    task.airwatch = extension

                    group 'publishing airwatch'
                    description "Validate ${variant.name} apk deployment on devices and force installation"
                }

                project.tasks.create("uninstallOlder${variant.name.capitalize()}", UninstallOlderTask) { task ->
                    task.bundleId = variant.applicationId
                    task.version = project.findProperty('airwatch.keepVersion') ?: android.defaultConfig.versionName
                    task.airwatch = extension

                    group 'publishing airwatch'
                    description "Uninstall ${variant.name} older versions from devices"
                }
            }
        }
    }

}