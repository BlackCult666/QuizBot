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
            throw IllegalArgumentException("File messages.json non trovato nella directory del progetto.")
        }
        BufferedReader(FileReader(messagesFilePath)).use {
            return gson.fromJson(it, MutableMap::class.java) as MutableMap<String, String>
        }
    }

    private fun loadQuestions(): MutableList<Question> {
        if (!questionsFilePath.exists()) {
            throw IllegalArgumentException("File questions.json non trovato nella directory del progetto.")
        }
        BufferedReader(FileReader(questionsFilePath)).use {
            return gson.fromJson(it, Array<Question>::class.java).toMutableList()
        }
    }

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
}