package br.com.luisrjaeger.airwatch.task

import br.com.luisrjaeger.airwatch.model.Message
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class PublishTask extends DefaultTask {

    Message messageToSend

    PublishTask() { }

    @TaskAction
    def postMessage() {
        GsonBuilder builder = new GsonBuilder()
        Gson gson = builder.create()

        println "Sending Message - ${messageToSend.name}"
        def json = gson.toJson(messageToSend)
        println "JSON - $json"

        def post = new URL(messageToSend.webHook).openConnection()
        post.setRequestMethod("POST")
        post.setDoOutput(true)
        post.setRequestProperty("Content-Type", "application/json")
        post.getOutputStream().write(json.getBytes("UTF-8"))

        def statusCode = post.getResponseCode()
        println("STATUS CODE $statusCode")
        println(post.getInputStream().getText())

        if (statusCode != 200) throw Exception(post.getInputStream().getText())
    }

}
