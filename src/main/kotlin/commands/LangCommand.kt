package eu.blackcult.commands

import com.google.gson.Gson
import eu.blackcult.GROUP_ID
import eu.blackcult.json.ResourceLoader.messages
import eu.blackcult.callbacks.TempStorage
import eu.blackcult.json.ResourceLoader
import eu.blackcult.utils.isAdmin
import eu.blackcult.utils.sendMessage
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.commands.Command
import io.github.ageofwar.telejam.commands.CommandHandler
import io.github.ageofwar.telejam.inline.CallbackDataInlineKeyboardButton
import io.github.ageofwar.telejam.messages.DocumentMessage
import io.github.ageofwar.telejam.messages.Message
import io.github.ageofwar.telejam.messages.TextMessage
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup
import io.github.ageofwar.telejam.text.Text
import java.io.BufferedReader
import java.io.InputStreamReader

class LangCommand(
    private val bot: Bot
) : CommandHandler {

    override fun onCommand(command: Command, message: TextMessage) {
        if (message.chat.id != GROUP_ID) return
        if (!bot.isAdmin(message.chat, message.sender.id)) {
            bot.sendMessage(message, Text.parseHtml(messages["noPermission"]))
            return
        }

        if (message.replyToMessage.isEmpty) {
            bot.sendMessage(message, Text.parseHtml(messages["replyToFile"]))
            return
        }

        val repliedMessage = message.replyToMessage.get()
        if (repliedMessage !is DocumentMessage) {
            return
        }

        val documentName = repliedMessage.document.name.get()
        if (!documentName.endsWith(".json")) {
            bot.sendMessage(message, Text.parseHtml(messages["wrongExtension"]))
            return
        }

        handleJsonUpdate(message, repliedMessage)
    }

    private fun handleJsonUpdate(message: Message, repliedMessage: DocumentMessage) {
        val inputStream = bot.downloadFile(repliedMessage.document.id)

        val newMessages: Map<String, String> = inputStream.use { input ->
            BufferedReader(InputStreamReader(input)).use { reader ->
                Gson().fromJson(reader, MutableMap::class.java) as Map<String, String>
            }
        }

        val missingKeys = ResourceLoader.validateMessages(newMessages)
        if (missingKeys.isNotEmpty()) {
            bot.sendMessage(message, Text.parseHtml("<b>Campi mancanti:</b> ${missingKeys.joinToString(", ")}"))
            return
        }

        TempStorage.store(message.chat.id, newMessages)

        val buttons = arrayOf(
            CallbackDataInlineKeyboardButton(messages["fileBackButton"], "lang_back"),
            CallbackDataInlineKeyboardButton(messages["fileAcceptButton"], "lang_accept")
        )

        bot.sendMessage(message, Text.parseHtml(messages["fileConfirm"]), InlineKeyboardMarkup(buttons))
    }
}