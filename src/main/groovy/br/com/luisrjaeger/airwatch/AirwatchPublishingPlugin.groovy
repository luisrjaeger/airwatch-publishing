package br.com.luisrjaeger.airwatch

import br.com.luisrjaeger.airwatch.model.Message
import br.com.luisrjaeger.airwatch.task.PublishTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class AirwatchPublishingPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def messages = project.container(Message)
        project.extensions.add("airwatch", messages)

        messages.all { message ->
            project.tasks.create("post${message.name.capitalize()}ToSlack", PublishTask) { task ->
                messageToSend = message
                description "Publish ${message.name} message to airwatch"
            }
        }
    }

}