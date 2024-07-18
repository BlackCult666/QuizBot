package eu.blackcult.question

data class Question(
    var uuid: String,
    val question: String,
    val description: String,
    val correctAnswer: String,
    val possibleAnswers: List<String>,
)