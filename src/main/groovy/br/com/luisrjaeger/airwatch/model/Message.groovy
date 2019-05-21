package br.com.luisrjaeger.airwatch.model

class Message {

    transient final String name

    transient String webHook

    String channel

    String iconUrl

    String iconEmoji

    String text

    String username

    def attachments = []

    Message(String name) {
        this.name = name
    }

    void attachment(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        Attachment attachment = new Attachment()
        closure.delegate = attachment
        attachments << attachment
        closure()
    }

}