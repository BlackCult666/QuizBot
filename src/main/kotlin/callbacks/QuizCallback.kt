package eu.blackcult.callbacks

import eu.blackcult.database.MongoWrapper
import eu.blackcult.json.ResourceLoader.messages
import eu.blackcult.question.Question
import eu.blackcult.utils.answerQuery
import eu.blackcult.utils.editText
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.callbacks.CallbackQueryHandler
import io.github.ageofwar.telejam.text.Text
import mentionPlayer
import java.util.*

var activeQuiz : Question? = null

class QuizCallback(
    private val bot: Bot,
    private val mongoWrapper: MongoWrapper
) : CallbackQueryHandler {

    private val cooldowns = mutableSetOf<Long>()

    override fun onCallbackQuery(callback: CallbackQuery) {
        val callbackData = callback.data.get()
        if (!callbackData.startsWith("answer")) return

        val uuid = callbackData.split("_")[1]
        val answer = callbackData.split("_")[2]

        val question = activeQuiz!!
        if (uuid != question.uuid) {
            bot.answerQuery(callback, messages["expiredQuiz"].toString())
            return
        }

        if (!mongoWrapper.playerExists(callback.sender.id)) {
            mongoWrapper.addPlayer(callback.sender.id, callback.sender.firstName)
        }

        handleGame(callback, answer, question)
    }

    private fun handleGame(callback: CallbackQuery, answer: String, question: Question) {
        if (!cooldowns.contains(callback.sender.id)) {
            mongoWrapper.recordAnswer(callback.sender.id, question.uuid, answer)

            if (answer == question.correctAnswer) {
                handleCorrectAnswer(callback, question)
            } else {
                handleWrongAnswer(callback)
            }

            addCooldown(callback.sender.id)
        } else {
            bot.answerQuery(callback, messages["quizDelay"].toString())
        }
    }

    private fun handleCorrectAnswer(callback: CallbackQuery, question: Question) {
        val emoji = messages["answerEmoji"]
        val correctEmoji = messages["correctEmoji"]

        val questionStats = mongoWrapper.getAnswerStats(question.uuid)
        val totalVotes = questionStats.values.sum()

        val formattedAnswers = question.possibleAnswers.joinToString("\n") { answer ->
            val voteCount = questionStats[answer] ?: 0
            val percentage = if (totalVotes > 0) (voteCount * 100 / totalVotes) else 0
            if (answer == question.correctAnswer) {
                "$correctEmoji <b>$answer</b> - $percentage% (voti: $voteCount)"
            } else {
                "$emoji $answer - $percentage% (voti: $voteCount)"
            }
        }

        mongoWrapper.incrementCorrectAnswers(callback.sender.id)
        mongoWrapper.incrementActualStreak(callback.sender.id)
        mongoWrapper.updateBestStreak(callback.sender.id)

        val stats = mongoWrapper.getStats(callback.sender.id)
        val correctText = messages["correct"]
            ?.replace("{question}", question.question)
            ?.replace("{possibleAnswers}", formattedAnswers)
            ?.replace("{description}", question.description)
            ?.replace("{user}", mentionPlayer(callback.sender.id, callback.sender.firstName))
            ?.replace("{actualStreak}", stats.actualStreak.toString())

        bot.editText(callback, Text.parseHtml(correctText))
        bot.answerQuery(callback, messages["correctQuery"].toString())
    }

    private fun handleWrongAnswer(callback: CallbackQuery) {
        bot.answerQuery(callback, messages["wrongQuery"].toString())

        mongoWrapper.incrementWrongAnswers(callback.sender.id)
        mongoWrapper.resetActualStreak(callback.sender.id)
        mongoWrapper.updateBestStreak(callback.sender.id)
    }

    private fun addCooldown(id: Long) {
        cooldowns.add(id)
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                cooldowns.remove(id)
            }
        }, 2 * 1000)
    }
}