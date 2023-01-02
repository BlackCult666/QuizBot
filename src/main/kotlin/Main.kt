import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.LongPollingBot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.callbacks.CallbackQueryHandler
import io.github.ageofwar.telejam.chats.PrivateChat
import io.github.ageofwar.telejam.commands.Command
import io.github.ageofwar.telejam.commands.CommandHandler
import io.github.ageofwar.telejam.messages.Message
import io.github.ageofwar.telejam.messages.TextMessage
import io.github.ageofwar.telejam.methods.AnswerCallbackQuery
import io.github.ageofwar.telejam.methods.EditMessageText
import io.github.ageofwar.telejam.methods.SendMessage
import io.github.ageofwar.telejam.text.Text
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Timer
import java.util.TimerTask
import java.util.UUID

fun main() {
    val token = "<token>"
    KotlinBot(Bot.fromToken(token)).run()
}

class KotlinBot(
    bot: Bot
) : LongPollingBot(bot) {
    init {
        QuestionManager()

        events.apply {
            registerCommand(StartCommand(bot), "start")
            registerUpdateHandler(QuizHandler(bot))
        }
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                QuizTimer(bot).startQuiz()
            }
        }, 0, 10*1000)
    }
}

class StartCommand(
    private val bot: Bot
) : CommandHandler {

    override fun onCommand(command: Command, message: TextMessage) {
        val firstName : String = message.sender.firstName
        val id : Long = message.sender.id
        if(message.chat is PrivateChat) {
            val sendMessage = SendMessage()
                .replyToMessage(message)
                .text(Text.parseHtml( mentionPlayer(firstName, id) + "<i>, non puoi usarmi in chat privata!</i>"))

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
            .chat(5562157760)
            .text(Text.parseHtml(QuestionManager.question.toString()))
            .replyMarkup(getButtons())

        bot.execute(sendMessage)
    }
}

class QuizHandler(
    private val bot: Bot
) : CallbackQueryHandler {


    override fun onCallbackQuery(callbackQuery: CallbackQuery) {
        val callback : String = callbackQuery.data.get()
        if(callback.startsWith("answer")) {
            val uuid = callback.split("@")[1]
            val answer = callback.split("@")[2]
            println(uuid)
            handleGame(uuid, answer, callbackQuery)
        }
    }
    fun handleGame(uuid: String, answer: String, callbackQuery: CallbackQuery) {
        val answerCallbackQuery = AnswerCallbackQuery().callbackQuery(callbackQuery)
        if(uuid != QuestionManager.question.uuid) {
            answerCallbackQuery.text("Domanda scaduta")
        } else {
            checkAnswer(answer, callbackQuery)
        }
        bot.execute(answerCallbackQuery)
    }
    fun checkAnswer(answer: String, callbackQuery: CallbackQuery) {
        val question = QuestionManager.question
        val correctAnswer = QuestionManager.question.answer
        if(answer.equals(correctAnswer, ignoreCase=true)) {
            val firstName = callbackQuery.sender.firstName
            val id = callbackQuery.sender.id
            val time : String = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))

            val editMessage = EditMessageText()
                .callbackQuery(callbackQuery)
                .text(Text.parseHtml("${question}\n${question.description}\n\nCongratulazioni ${mentionPlayer(firstName, id)}, la risposta era: <b>$correctAnswer</b>\n\uD83D\uDD62 $time"))
                .replyMarkup(null)
            bot.execute(editMessage)
        } else {
            TODO()
        }
    }
}
