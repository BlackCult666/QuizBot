package utils

import com.google.gson.Gson
import objects.Question
import java.io.FileReader
import java.util.*

class QuestionManager {

    companion object {
        val questions = mutableListOf<Question>()
        lateinit var question : Question

        fun getRandomQuestion(): Question {
            val random = Random()
            question = questions[random.nextInt(questions.size)]
            question.possibleAnswers.shuffle()
            return question
        }
    }
    init {
        loadQuestions()
    }

    fun loadQuestions() {
        val gson = Gson()
        val questionsFromJson = gson.fromJson(FileReader("src/main/questions.json"), Array<Question>::class.java)
        questions.addAll(questionsFromJson)
    }

}