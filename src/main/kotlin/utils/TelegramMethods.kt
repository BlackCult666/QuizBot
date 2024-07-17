package eu.blackcult.utils

import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.messages.Message
import io.github.ageofwar.telejam.messages.TextMessage
import io.github.ageofwar.telejam.methods.SendMessage
import io.github.ageofwar.telejam.replymarkups.ReplyMarkup
import io.github.ageofwar.telejam.text.Text

fun mentionPlayer(id: Long, name: String) = "<a href='tg://user?id=$id'>$name</a>"

fun Bot.sendMessage(replyToMessage: Message, text: Text, replyMarkup: ReplyMarkup? = null): TextMessage {
    val sendMessage = SendMessage()
        .replyToMessage(replyToMessage)
        .text(text)
        .replyMarkup(replyMarkup)
        .disableWebPagePreview()
    return execute(sendMessage)
}

fun Bot.sendMessage(chatId: Long, text: Text, replyMarkup: ReplyMarkup? = null): TextMessage {
    val sendMessage = SendMessage()
        .chat(chatId)
        .text(text)
        .replyMarkup(replyMarkup)
    return execute(sendMessage)
}