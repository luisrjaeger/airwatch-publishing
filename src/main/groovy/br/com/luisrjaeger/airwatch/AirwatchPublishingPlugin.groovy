package br.com.luisrjaeger.airwatch

import br.com.luisrjaeger.airwatch.model.Airwatch
import br.com.luisrjaeger.airwatch.task.PublishTask
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
                    task.filePath = outputFile.absolutePath.replace(outputFile.name, "")
                    task.bundleId = variant.applicationId
                    task.version = android.defaultConfig.versionName
                    task.airwatch = extension

                    group 'publishing airwatch'
                    description "Publish ${variant.name} apk to airwatch"
                }
            }
        }
    }

}