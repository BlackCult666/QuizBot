package eu.blackcult.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import eu.blackcult.question.Question
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

/*object ResourceLoader {
    private val gson = Gson()

    val messages: Map<String, String>
    val questions: List<Question>

    init {
        messages = loadMessages()
        questions = loadQuestions()
    }

    private fun loadMessages(): Map<String, String> {
        val inputStream = javaClass.classLoader.getResourceAsStream("messages.json")
            ?: throw IllegalArgumentException("file not found! messages.json")
        InputStreamReader(inputStream).use {
            return gson.fromJson(it, Map::class.java) as Map<String, String>
        }
    }

    private fun loadQuestions(): List<Question> {
        val inputStream = javaClass.classLoader.getResourceAsStream("questions.json")
            ?: throw IllegalArgumentException("file not found! questions.json")
        InputStreamReader(inputStream).use {
            return gson.fromJson(it, Array<Question>::class.java).toList()
        }
    }
}*/

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