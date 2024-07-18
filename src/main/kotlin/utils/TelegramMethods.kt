package eu.blackcult.utils

import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.messages.Message
import io.github.ageofwar.telejam.messages.TextMessage
import io.github.ageofwar.telejam.methods.AnswerCallbackQuery
import io.github.ageofwar.telejam.methods.EditMessageText
import io.github.ageofwar.telejam.methods.SendMessage
import io.github.ageofwar.telejam.replymarkups.ReplyMarkup
import io.github.ageofwar.telejam.text.Text
import java.io.Serializable

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

fun Bot.editText(callbackQuery: CallbackQuery, text: Text): Serializable? {
    val editMessage = EditMessageText()
        .callbackQuery(callbackQuery)
        .text(text)

    return execute(editMessage)
}

fun Bot.answerQuery(callbackQuery: CallbackQuery, text: String): Boolean {
    val answerCallbackQuery = AnswerCallbackQuery()
        .callbackQuery(callbackQuery)
        .text(text)

    return execute(answerCallbackQuery)
}