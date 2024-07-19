package eu.blackcult.utils

import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.chats.Chat
import io.github.ageofwar.telejam.messages.Message
import io.github.ageofwar.telejam.messages.TextMessage
import io.github.ageofwar.telejam.messages.VideoMessage
import io.github.ageofwar.telejam.methods.*
import io.github.ageofwar.telejam.replymarkups.ReplyMarkup
import io.github.ageofwar.telejam.text.Text
import io.github.ageofwar.telejam.users.ChatMember
import java.io.Serializable

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

fun Bot.sendVideo(replyToMessage: Message, text: Text, url: String): VideoMessage {
    val sendVideo = SendVideo()
        .replyToMessage(replyToMessage)
        .video(url)
        .caption(text)
        .duration(-1)

    return execute(sendVideo)
}

fun Bot.editText(callbackQuery: CallbackQuery, text: Text): Serializable {
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

fun Bot.getChatMember(chat: Chat, userId: Long): ChatMember {
    val getChatMember = GetChatMember()
        .chat(chat)
        .user(userId)

    return execute(getChatMember)
}

fun Bot.isAdmin(chat: Chat, id: Long) = getChatMember(chat, id).isAdmin