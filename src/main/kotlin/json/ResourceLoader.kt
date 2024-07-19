package eu.blackcult.json

import com.google.gson.Gson
import eu.blackcult.question.Question
import java.io.*

object ResourceLoader {
    private val gson = Gson()

    private val messagesFilePath = File("messages.json")
    private val questionsFilePath = File("questions.json")

    private val requiredKeys = setOf(
        "start", "welcome", "stats", "noPermission", "wrongChat", "quiz",
        "answerEmoji", "correctEmoji", "correct", "correctQuery", "wrongQuery",
        "quizDelay", "expiredQuiz", "topMessage", "replyToFile", "wrongExtension",
        "fileCancel", "fileCancelQuery", "fileUpdated", "fileUpdatedQuery",
        "fileConfirm", "fileBackButton", "fileAcceptButton",
    )

    var messages: MutableMap<String, String> = loadMessages()
        private set
    val questions: MutableList<Question> = loadQuestions()


    private fun loadMessages(): MutableMap<String, String> {
        if (!messagesFilePath.exists()) {
            throw IllegalArgumentException("File messages.json not found.")
        }
        BufferedReader(FileReader(messagesFilePath)).use {
            return gson.fromJson(it, MutableMap::class.java) as MutableMap<String, String>
        }
    }

    private fun loadQuestions(): MutableList<Question> {
        if (!questionsFilePath.exists()) {
            throw IllegalArgumentException("File questions.json not found.")
        }
        BufferedReader(FileReader(questionsFilePath)).use {
            return gson.fromJson(it, Array<Question>::class.java).toMutableList()
        }
    }

    fun reloadMessages() {
        messages = loadMessages()
    }

    /* Work in progress.

     fun saveMessages() {
        BufferedWriter(FileWriter(messagesFilePath)).use {
            gson.toJson(messages, it)
        }
    }

    fun saveQuestions() {
        BufferedWriter(FileWriter(questionsFilePath)).use {
            gson.toJson(questions, it)
        }
    }*/

    fun validateMessages(newMessages: Map<String, String>): List<String> {
        return requiredKeys.filterNot { newMessages.containsKey(it) }
    }
}