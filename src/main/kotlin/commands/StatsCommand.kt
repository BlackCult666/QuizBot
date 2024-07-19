package eu.blackcult.commands

import eu.blackcult.GROUP_ID
import eu.blackcult.database.MongoWrapper
import eu.blackcult.json.ResourceLoader.messages
import eu.blackcult.utils.sendMessage
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.chats.PrivateChat
import io.github.ageofwar.telejam.commands.Command
import io.github.ageofwar.telejam.commands.CommandHandler
import io.github.ageofwar.telejam.messages.TextMessage
import io.github.ageofwar.telejam.text.Text
import mentionPlayer
import percentage

class StatsCommand(
    private val bot: Bot,
    private val mongoWrapper: MongoWrapper
) : CommandHandler {

    override fun onCommand(command: Command, message: TextMessage) {
        if (message.chat is PrivateChat) {
            bot.sendMessage(message, Text.parseHtml(messages["wrongChat"]))
            return
        }

        if (message.chat.id != GROUP_ID) return

        if (!mongoWrapper.playerExists(message.sender.id)) {
            mongoWrapper.addPlayer(message.sender.id, message.sender.firstName)
        }

        val playerStats = mongoWrapper.getStats(message.sender.id)
        val totalAnswers = playerStats.wrongAnswers + playerStats.correctAnswers
        val correctRatio = percentage(playerStats.correctAnswers, totalAnswers)
        val wrongRatio = percentage(playerStats.wrongAnswers, totalAnswers)

        val statsMessage = messages["stats"]
            ?.replace("{user}", mentionPlayer(playerStats.id, playerStats.firstName))
            ?.replace("{totalAnswers}", "$totalAnswers")
            ?.replace("{correctAnswers}", "${playerStats.correctAnswers}")
            ?.replace("{wrongAnswers}", "${playerStats.wrongAnswers}")
            ?.replace("{correctRatio}", "$correctRatio")
            ?.replace("{wrongRatio}", "$wrongRatio")
            ?.replace("{actualStreak}", "${playerStats.actualStreak}")
            ?.replace("{bestStreak}", "${playerStats.bestStreak}")

        bot.sendMessage(message, Text.parseHtml(statsMessage))
    }
}