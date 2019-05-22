package br.com.luisrjaeger.airwatch.task

import br.com.luisrjaeger.airwatch.model.Airwatch
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class PublishTask extends DefaultTask {

    Airwatch airwatch

    PublishTask() { }

    @TaskAction
    def postToAirwatch() {
        GsonBuilder builder = new GsonBuilder()
        Gson gson = builder.create()

        //"curl --request POST --header 'Authorization: Basic YXBpYWRtaW46cmVubmVyQDEyMw==' --header 'aw-tenant-code: O2UkzdtEXxC+dIcDce6UjUHQeI++IFxLjcxSj0hKpa4=' --header 'Accept: application/json' 'Content-Type: application/octet-stream' --data-binary @remarcacao-dsv-2.6.2.3.apk http://awds.lojasrenner.com.br/api/mam/blobs/uploadblob?filename=remarcacao-dsv-2.6.2.3.apk"
        //"curl --request POST --header 'Authorization: Basic YXBpYWRtaW46cmVubmVyQDEyMw==' --header 'aw-tenant-code: O2UkzdtEXxC+dIcDce6UjUHQeI++IFxLjcxSj0hKpa4=' --header 'Accept: application/json' --header 'Content-Type: application/json' -d '{\"FileName\": \"remarcacao-dsv-2.6.2.3.apk\", \"BlobId\": 81509, \"LocationGroupId\": 2103, \"ApplicationId\": \"br.com.lojasrenner.remarcacao.test\", \"DeviceType\": \"Android\", \"PushMode\": \"Auto\", \"AutoUpdateVersion\": false, \"ApplicationName\": \"RemarcacaoTeste\", \"SupportedModels\" : { \"Model\" : [{ \"ModelName\" : \"Android\" }] } }' http://awds.lojasrenner.com.br/api/mam/apps/internal/begininstall"
        println "Sending APK - ${airwatch.applicationName}"
//        def json = gson.toJson(airwatch)
//        println "JSON - $json"
//
//        def post = new URL(airwatch.webHook).openConnection()
//        post.setRequestMethod("POST")
//        post.setDoOutput(true)
//        post.setRequestProperty("Content-Type", "application/json")
//        post.getOutputStream().write(json.getBytes("UTF-8"))
//
//        def statusCode = post.getResponseCode()
//        println("STATUS CODE $statusCode")
//        println(post.getInputStream().getText())
//
//        if (statusCode != 200) throw Exception(post.getInputStream().getText())
    }

}
