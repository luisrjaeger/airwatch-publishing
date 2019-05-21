package br.com.luisrjaeger.airwatch.model

class Attachment {

    String fallback

    String pretext

    String color

    def fields = []

    def field(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        Field field = new Field()
        closure.delegate = field
        fields << field
        closure()
    }

}