package eu.blackcult

import eu.blackcult.callbacks.LangCallback
import eu.blackcult.callbacks.QuizCallback
import eu.blackcult.callbacks.activeQuiz
import eu.blackcult.commands.*
import eu.blackcult.database.MongoWrapper
import eu.blackcult.json.ResourceLoader.messages
import eu.blackcult.json.ResourceLoader.questions
import eu.blackcult.messages.StoringHandler
import eu.blackcult.messages.WelcomeHandler
import eu.blackcult.question.Question
import eu.blackcult.utils.sendMessage
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.LongPollingBot
import io.github.ageofwar.telejam.json.Json
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup
import io.github.ageofwar.telejam.text.Text
import java.util.*

const val GROUP_ID = -1002225719460

class QuizBot(
    bot: Bot
) : LongPollingBot(bot) {

    init {
        val mongoWrapper = MongoWrapper()

        events.apply {
            registerCommand(StartCommand(bot), "start")
            registerCommand(LangCommand(bot), "setlang", "setmessages")
            registerCommand(AddQuestionCommand(bot), "addquiz", "addquestion")
            registerCommand(StatsCommand(bot, mongoWrapper), "stats", "statistiche")
            registerCommand(TopCommand(bot, mongoWrapper), "top", "classifica", "leaderboard")

            registerUpdateHandler(WelcomeHandler(bot))
            registerUpdateHandler(LangCallback(bot))
            registerUpdateHandler(StoringHandler(bot, mongoWrapper))
            registerUpdateHandler(QuizCallback(bot, mongoWrapper))
        }

        startJob()

    }

    private fun startJob() {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                QuizJob(bot).run()
            }
        }, 0, 30 * 1000)
    }
}

class QuizJob(
    private val bot: Bot
) {

    fun run() {
        val randomQuestion = questions.random()
        val possibleAnswers = randomQuestion.possibleAnswers.shuffled()

        val emoji = messages["answerEmoji"]

        val formattedAnswers = possibleAnswers.joinToString("\n") { answer ->
            "$emoji $answer"
        }

        val quizText = messages["quiz"]
            ?.replace("{question}", randomQuestion.question)
            ?.replace("{possibleAnswers}", formattedAnswers)

        val randomUUID = UUID.randomUUID().toString()

        activeQuiz = Question(randomUUID, randomQuestion.question, randomQuestion.description, randomQuestion.correctAnswer, possibleAnswers)

        bot.sendMessage(GROUP_ID, Text.parseHtml(quizText), getButtons(randomUUID, possibleAnswers))
    }

    private fun getButtons(uuid: String, answers: List<String>): InlineKeyboardMarkup {
        return Json.fromJson(
            "{\"inline_keyboard\": [[{\"text\":\"${answers[0]}\", \"callback_data\":\"answer_${uuid}_${answers[0]}\"}," +
                    "{\"text\":\"${answers[1]}\", \"callback_data\":\"answer_${uuid}_${answers[1]}\"}], " +
                    "[{\"text\":\"${answers[2]}\", \"callback_data\":\"answer_${uuid}_${answers[2]}\"}, " +
                    "{\"text\":\"${answers[3]}\", \"callback_data\":\"answer_${uuid}_${answers[3]}\"}]]}",
            InlineKeyboardMarkup::class.java
        )
    }
}

fun main() {
    val token = "token"
    val bot = Bot.fromToken(token)

    QuizBot(bot).use {
        it.run()
    }
}

