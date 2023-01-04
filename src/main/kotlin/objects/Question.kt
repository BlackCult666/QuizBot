package objects

import config.PropertiesReader
import java.util.*

data class Question(
    val question: String,
    val possibleAnswers: Array<String>,
    val correctAnswer: String,
    val description: String,
    val number: Int,
    var uuid: String
) {
    override fun toString(): String {
        var result = ""
        for (i in possibleAnswers) {
            result += PropertiesReader.getProperty("possible-answers-decoration") + " " + i + "\n"
        }
        return PropertiesReader.getProperty("question-format").format(number, question, result)
    }
}
