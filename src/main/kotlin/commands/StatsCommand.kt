package eu.blackcult.commands

import eu.blackcult.database.MongoWrapper
import eu.blackcult.json.ResourceLoader.messages
import eu.blackcult.utils.mentionPlayer
import eu.blackcult.utils.sendMessage
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.commands.Command
import io.github.ageofwar.telejam.commands.CommandHandler
import io.github.ageofwar.telejam.messages.TextMessage
import io.github.ageofwar.telejam.text.Text
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class StatsCommand(
    private val bot: Bot,
    private val mongoWrapper: MongoWrapper
) : CommandHandler {

    override fun onCommand(command: Command, message: TextMessage) {
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

    private fun percentage(part: Int, whole: Int): Double {
        if (whole == 0) {
            return 0.0
        }

        val percentage = 100.0 * part / whole

        val symbols = DecimalFormatSymbols(Locale.ITALY)
        val df = DecimalFormat("#.##", symbols)

        return df.format(percentage).toDouble()

    }
}