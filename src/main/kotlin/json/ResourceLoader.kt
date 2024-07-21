package eu.blackcult.json

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import eu.blackcult.question.Question
import java.io.*

object ResourceLoader {
    private val gson = Gson()

    private val messagesFilePath = File("messages.json")
    private val questionsFilePath = File("questions.json")

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

    fun addQuestion(question: Question) {
        questions.add(question)
        saveQuestions()
    }

    fun validateMessages(newMessages: Map<String, String>): List<String> {
        val messagesKey = setOf(
            "start", "welcome", "stats", "noPermission", "wrongChat", "quiz",
            "answerEmoji", "correctEmoji", "correct", "correctQuery", "wrongQuery",
            "quizDelay", "expiredQuiz", "topMessage", "replyToFile", "wrongExtension",
            "fileCancel", "fileCancelQuery", "fileUpdated", "fileUpdatedQuery",
            "fileConfirm", "fileBackButton", "fileAcceptButton", "addQuestionUsage",
            "questionAdded", "jsonError",
        )

        return messagesKey.filterNot { newMessages.containsKey(it) }
    }

    fun validateQuestion(jsonString: String): List<String> {
        val requiredKeys = listOf("question", "possibleAnswers", "correctAnswer", "description")
        val missingFields = mutableListOf<String>()

        try {
            val jsonObject = JsonParser.parseString(jsonString).asJsonObject

            for (key in requiredKeys) {
                if (!jsonObject.has(key)) {
                    missingFields.add(key)
                }
            }

            if (jsonObject.has("possibleAnswers")) {
                val possibleAnswers = jsonObject.getAsJsonArray("possibleAnswers")
                if (possibleAnswers.size() < 1) {
                    missingFields.add("possibleAnswers")
                }
            }

        } catch (e: JsonSyntaxException) {
            missingFields.add("Invalid JSON format")
        }

        return missingFields
    }

    private fun saveQuestions() {
        BufferedWriter(FileWriter(questionsFilePath)).use {
            gson.toJson(questions, it)
        }
    }
}