package eu.blackcult

import eu.blackcult.commands.StartCommand
import eu.blackcult.commands.StatsCommand
import eu.blackcult.database.MongoWrapper
import eu.blackcult.updates.StoringHandler
import eu.blackcult.utils.sendMessage
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.LongPollingBot
import io.github.ageofwar.telejam.text.Text
import java.util.*


class QuizBot(
    bot: Bot
) : LongPollingBot(bot) {

    init {
        val mongoWrapper = MongoWrapper()

        events.apply {
            registerCommand(StartCommand(bot), "start")
            registerCommand(StatsCommand(bot, mongoWrapper), "stats")

            registerUpdateHandler(StoringHandler(mongoWrapper))
        }

        startJob()
    }

    private fun startJob() {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                QuizJob(bot).run()
            }
        }, 0, 5 * 1000)
    }
}

class QuizJob(
    private val bot: Bot
) {

    fun run() {
        bot.sendMessage(-1002225719460, Text.parseHtml("Test"))
    }
}

fun main() {
    val token = "5487274600:AAGMjOQoU4hVO_88UBUjjKfOADVJ00GrXgQ"
    val bot = Bot.fromToken(token)

    QuizBot(bot).use {
        it.run()
    }
}

