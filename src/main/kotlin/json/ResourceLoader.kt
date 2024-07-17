package eu.blackcult.json

import com.google.gson.Gson
import eu.blackcult.question.Question
import java.io.*

object ResourceLoader {
    private val gson = Gson()

    private val messagesFilePath = File("messages.json")
    private val questionsFilePath = File("questions.json")

    val messages: MutableMap<String, String> = loadMessages()
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

    /* Was thinking about making the bot customizable on Telegram itself.

    fun saveMessages() {
        BufferedWriter(FileWriter(messagesFilePath)).use {
            gson.toJson(messages, it)
        }
    }

    fun saveQuestions() {
        BufferedWriter(FileWriter(questionsFilePath)).use {
            gson.toJson(questions, it)
        }
    }

     */
}