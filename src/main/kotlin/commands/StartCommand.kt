package eu.blackcult.commands

import eu.blackcult.json.ResourceLoader.messages
import eu.blackcult.utils.mentionPlayer
import eu.blackcult.utils.sendMessage
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.commands.Command
import io.github.ageofwar.telejam.commands.CommandHandler
import io.github.ageofwar.telejam.inline.UrlInlineKeyboardButton
import io.github.ageofwar.telejam.messages.TextMessage
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup
import io.github.ageofwar.telejam.text.Text
import java.time.LocalTime

@Suppress("UNUSED_EXPRESSION")
class StartCommand(
    private val bot: Bot
) : CommandHandler {

    override fun onCommand(command: Command, message: TextMessage) {
        val sender = message.sender

        val actualHour = LocalTime.now().hour

        val greeting = when (actualHour) {
            in 6..11 -> "\uD83C\uDF1E Buongiorno"
            in 12..17 -> "\uD83C\uDF1E Buon pomeriggio"
            in 18..23 -> "\uD83C\uDF12 Buonasera"
            else -> "\uD83C\uDF12 Buonasera"
        }
        val startMessage = messages["start"]
            ?.replace("{user}", mentionPlayer(sender.id, sender.firstName))
            ?.replace("{greet}", greeting)

        bot.sendMessage(message, Text.parseHtml(startMessage), InlineKeyboardMarkup(UrlInlineKeyboardButton("Vuoi saperne di più?", "https://t.me/steinsnetwork")))
    }


}