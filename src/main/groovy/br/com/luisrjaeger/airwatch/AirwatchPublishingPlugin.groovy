package br.com.luisrjaeger.airwatch


import br.com.luisrjaeger.airwatch.model.Airwatch
import br.com.luisrjaeger.airwatch.task.PublishTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class AirwatchPublishingPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def extension = project.extensions.create("airwatch", Airwatch)

        project.tasks.create("publishToAirwatch", PublishTask) { task ->
            task.airwatch = extension
            description "Publish ${project.name} apk to airwatch"
        }
    }

}