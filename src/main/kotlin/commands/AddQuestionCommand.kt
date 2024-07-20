package eu.blackcult.commands

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import eu.blackcult.json.ResourceLoader
import eu.blackcult.json.ResourceLoader.messages
import eu.blackcult.question.Question
import eu.blackcult.utils.isAdmin
import eu.blackcult.utils.sendMessage
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.chats.PrivateChat
import io.github.ageofwar.telejam.commands.Command
import io.github.ageofwar.telejam.commands.CommandHandler
import io.github.ageofwar.telejam.messages.TextMessage
import io.github.ageofwar.telejam.text.Text

class AddQuestionCommand(
    private val bot: Bot,
) : CommandHandler {

    override fun onCommand(command: Command, message: TextMessage) {
        if (message.chat is PrivateChat) return
        if (!bot.isAdmin(message.chat, message.sender.id)) {
            bot.sendMessage(message, Text.parseHtml(messages["noPermission"]))
            return
        }

        val args = command.args.toString()

        if (args.isEmpty()) {
            bot.sendMessage(message, Text.parseHtml(messages["addQuestionUsage"]))
            return
        }

        val missingFields = ResourceLoader.validateQuestion(args)
        if (missingFields.isNotEmpty()) {
            bot.sendMessage(message, Text.parseHtml("<b>Campi mancanti o errori presenti:</b> ${missingFields.joinToString(", ")}"))
            return
        }

        try {
            val newQuestion = Gson().fromJson(args, Question::class.java)
            ResourceLoader.addQuestion(newQuestion)

            bot.sendMessage(message, Text.parseHtml(messages["questionAdded"]))
        } catch (e: JsonSyntaxException) {
            bot.sendMessage(message, Text.parseHtml(messages["jsonError"]))
        }

    }
}