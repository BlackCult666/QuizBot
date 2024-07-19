package eu.blackcult.callbacks

import com.google.gson.Gson
import eu.blackcult.json.ResourceLoader.messages
import eu.blackcult.json.ResourceLoader
import eu.blackcult.utils.answerQuery
import eu.blackcult.utils.editText
import eu.blackcult.utils.isAdmin
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.callbacks.CallbackQueryHandler
import io.github.ageofwar.telejam.text.Text
import java.io.File

class LangCallback(
    private val bot: Bot
) : CallbackQueryHandler {

    override fun onCallbackQuery(callback: CallbackQuery) {
        val data = callback.data.get()
        if (!data.startsWith("lang")) return

        val chat = callback.message.get().chat
        if (!bot.isAdmin(chat, callback.sender.id)) {
            bot.answerQuery(callback, messages["noPermission"].toString())
            return
        }

        val choice = data.split("_")[1]
        when (choice) {
            "back" -> {
                bot.editText(callback, Text.parseHtml(messages["fileCancel"]))
                bot.answerQuery(callback, messages["fileCancelQuery"].toString())
            }
            "accept" -> {
                val chatId = chat.id
                val newMessages = TempStorage.retrieve(chatId)

                handleDownload(callback, newMessages)
            }
        }
    }

    private fun handleDownload(callback: CallbackQuery, newMessages: Map<String, String>?) {
        val newFile = File("messages.json")
        newFile.writeText(Gson().toJson(newMessages))

        ResourceLoader.reloadMessages()

        bot.editText(callback, Text.parseHtml(messages["fileUpdated"]))
        bot.answerQuery(callback, messages["fileUpdatedQuery"].toString())
    }
}

object TempStorage {
    private val storage = mutableMapOf<Long, Map<String, String>>()

    fun store(chatId: Long, newMessages: Map<String, String>) {
        storage[chatId] = newMessages
    }

    fun retrieve(chatId: Long): Map<String, String>? {
        return storage.remove(chatId)
    }
}