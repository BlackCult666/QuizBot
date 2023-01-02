import config.PropertiesReader
import database.DatabaseWrapper
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.LongPollingBot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.callbacks.CallbackQueryHandler
import io.github.ageofwar.telejam.chats.PrivateChat
import io.github.ageofwar.telejam.commands.Command
import io.github.ageofwar.telejam.commands.CommandHandler
import io.github.ageofwar.telejam.messages.TextMessage
import io.github.ageofwar.telejam.methods.AnswerCallbackQuery
import io.github.ageofwar.telejam.methods.EditMessageText
import io.github.ageofwar.telejam.methods.SendMessage
import io.github.ageofwar.telejam.text.Text
import utils.QuestionManager
import utils.getButtons
import utils.mentionPlayer
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

fun main() {
    val token = PropertiesReader.getProperty("bot-token")
    KotlinBot(Bot.fromToken(token)).run()
}

class KotlinBot(
    bot: Bot
) : LongPollingBot(bot) {
    init {
        val databaseWrapper = DatabaseWrapper()

        events.apply {
            registerCommand(StartCommand(bot), "start")
            registerCommand(StatsCommand(bot, databaseWrapper), "stats")
            registerUpdateHandler(QuizHandler(bot, databaseWrapper))
        }

        QuestionManager()

        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                QuizTimer(bot).startQuiz()
            }
        }, 0, PropertiesReader.getProperty("delay").toLong() * 1000)
    }
}

class StartCommand(
    private val bot: Bot
) : CommandHandler {

    override fun onCommand(command: Command, message: TextMessage) {
        val firstName: String = message.sender.firstName
        val id: Long = message.sender.id
        if (message.chat is PrivateChat) {
            val sendMessage = SendMessage()
                .replyToMessage(message)
                .text(Text.parseHtml(PropertiesReader.getProperty("start-message").format(id, firstName)))
            bot.execute(sendMessage)
        }
    }
}

class QuizTimer(
    private val bot: Bot
) {

    fun startQuiz() {
        QuestionManager.question = QuestionManager.getRandomQuestion()
        QuestionManager.question.setUUID(UUID.randomUUID().toString())
        val sendMessage = SendMessage()
            .chat(PropertiesReader.getProperty("chat-id"))
            .text(Text.parseHtml(QuestionManager.question.toString()))
            .replyMarkup(getButtons())

        bot.execute(sendMessage)
    }
}

class QuizHandler(
    private val bot: Bot,
    private val databaseWrapper: DatabaseWrapper
) : CallbackQueryHandler {

    private val cooldowns = mutableSetOf<Long>()

    override fun onCallbackQuery(callbackQuery: CallbackQuery) {
        val callback: String = callbackQuery.data.get()
        if (callback.startsWith("answer")) {
            val uuid = callback.split("@")[1]
            val answer = callback.split("@")[2]
            handleGame(uuid, answer, callbackQuery)
        }
    }

    fun handleGame(uuid: String, answer: String, callbackQuery: CallbackQuery) {
        val answerCallbackQuery = AnswerCallbackQuery().callbackQuery(callbackQuery)
        if (uuid != QuestionManager.question.uuid) {
            answerCallbackQuery.text(PropertiesReader.getProperty("expired-question-query"))
        } else {
            if(!cooldowns.contains(callbackQuery.sender.id)) {
                addPlayerCooldown(callbackQuery.sender.id)
                checkAnswer(answer, callbackQuery)
            } else {
                answerCallbackQuery.text(PropertiesReader.getProperty("cooldown-query"))
            }
        }
        bot.execute(answerCallbackQuery)
    }

    fun addPlayerCooldown(id: Long) {
        cooldowns.add(id)
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                cooldowns.remove(id)
            }
        }, PropertiesReader.getProperty("cooldown-delay").toLong() * 1000)
    }

    fun checkAnswer(answer: String, callbackQuery: CallbackQuery) {
        val id = callbackQuery.sender.id
        val correctAnswer = QuestionManager.question.answer

        if (answer == correctAnswer) {
            databaseWrapper.incrementCorrectNumber(id)
            databaseWrapper.incrementStreak(id)
            databaseWrapper.checkBestStreak(id)
            cooldowns.clear()
            matchEnded(callbackQuery)
        } else {
            databaseWrapper.incrementErrorsNumber(id)
            databaseWrapper.resetActualStreak(id)
            bot.execute(AnswerCallbackQuery().callbackQuery(callbackQuery).text(PropertiesReader.getProperty("wrong-answer-query")))
        }
    }
    fun matchEnded(callbackQuery: CallbackQuery) {
        val question = QuestionManager.question
        val correctAnswer = QuestionManager.question.answer

        val time: String = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val newQuestion: String = question.toString()
            .replace("\uD83D\uDD39 ${question.answer}", "\uD83D\uDD38 <b>${question.answer}</b>")

        val answerQuery = AnswerCallbackQuery().callbackQuery(callbackQuery).text(PropertiesReader.getProperty("correct-answer-query"))
        val editMessage = EditMessageText().callbackQuery(callbackQuery).text(Text.parseHtml("${newQuestion}\n" + PropertiesReader.getProperty("game-ended").format(question.description, mentionPlayer(callbackQuery.sender.firstName, callbackQuery.sender.id), correctAnswer, time, databaseWrapper.getActualStreak(callbackQuery.sender.id)))).replyMarkup(null)
        bot.execute(editMessage)
        bot.execute(answerQuery)
    }
}

class StatsCommand(
    private val bot: Bot,
    private val databaseWrapper: DatabaseWrapper
) : CommandHandler {

    override fun onCommand(command: Command, message: TextMessage) {
        val firstName: String = message.sender.firstName
        val id: Long = message.sender.id
        val sendMessage = SendMessage().replyToMessage(message)
        /*if(message.chat is PrivateChat) {
            TODO() It's not a priority atm
        }*/
        sendMessage.text(Text.parseHtml(PropertiesReader.getProperty("stats-message")
                    .format(mentionPlayer(firstName, id), databaseWrapper.getCorrectAnswers(id), databaseWrapper.getWrongAnswers(id), databaseWrapper.getActualStreak(id), databaseWrapper.getBestStreak(id))))
        bot.execute(sendMessage)
    }

}
