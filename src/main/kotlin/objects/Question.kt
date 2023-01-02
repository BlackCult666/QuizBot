package objects

import config.PropertiesReader

class Question(
    val question: String,
    val answers: Array<String>,
    val answer: String,
    val description: String,
    val number: Int,
    var uuid: String
) {
    fun setUUID(random : String) {
        this.uuid = random
    }
    override fun toString(): String {
        var result = ""
        for (i in answers) {
            result += PropertiesReader.getProperty("possible-answers-decoration") + " " + i + "\n"
        }
        return PropertiesReader.getProperty("question-format").format(number, question, result)
    }
}
