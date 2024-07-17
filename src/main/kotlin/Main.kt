package eu.blackcult

import eu.blackcult.commands.StartCommand
import eu.blackcult.commands.StatsCommand
import eu.blackcult.database.MongoWrapper
import eu.blackcult.updates.StoringHandler
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.LongPollingBot


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
    }
}

fun main() {
    val token = "5487274600:AAGMjOQoU4hVO_88UBUjjKfOADVJ00GrXgQ"
    val bot = Bot.fromToken(token)

    QuizBot(bot).use {
        it.run()
    }
}

