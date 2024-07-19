package eu.blackcult.commands

import eu.blackcult.GROUP_ID
import eu.blackcult.database.MongoWrapper
import eu.blackcult.database.PlayerStats
import eu.blackcult.json.ResourceLoader.messages
import eu.blackcult.utils.sendMessage
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.chats.PrivateChat
import io.github.ageofwar.telejam.commands.Command
import io.github.ageofwar.telejam.commands.CommandHandler
import io.github.ageofwar.telejam.messages.TextMessage
import io.github.ageofwar.telejam.text.Text
import mentionPlayer

class TopCommand(
    private val bot: Bot,
    private val mongoWrapper: MongoWrapper
) : CommandHandler {

    override fun onCommand(command: Command, message: TextMessage) {
        if (message.chat is PrivateChat) {
            bot.sendMessage(message, Text.parseHtml(messages["wrongChat"]))
            return
        }

        if (message.chat.id != GROUP_ID) return

        val correctTop = formatTop(mongoWrapper.getTopPlayers("correctAnswers"), "correctAnswers")
        val wrongTop = formatTop(mongoWrapper.getTopPlayers("wrongAnswers"), "wrongAnswers")
        val streakTop = formatTop(mongoWrapper.getTopPlayers("bestStreak"), "bestStreak")

        val streakMessage = messages["topMessage"]
            ?.replace("{correctTop}", correctTop)
            ?.replace("{wrongTop}", wrongTop)
            ?.replace("{streakTop}", streakTop)

        bot.sendMessage(message, Text.parseHtml(streakMessage))

    }


    private fun formatTop(players: List<PlayerStats>, type: String): String {
        return players.take(3).mapIndexed { index, player ->
            val mentionPlayer = mentionPlayer(player.id, player.firstName)
            val emoji = when (index) {
                0 -> "\uD83E\uDD47"
                1 -> "\uD83E\uDD48"
                2 -> "\uD83E\uDD49"
                else -> ""
            }
            val statValue = when (type) {
                "correctAnswers" -> player.correctAnswers
                "wrongAnswers" -> player.wrongAnswers
                "bestStreak" -> player.bestStreak
                else -> 0
            }

            "$emoji $mentionPlayer - $statValue"
        }.joinToString("\n")
    }
}