package eu.blackcult.question

data class Question(
    val question: String,
    val description: String,
    val possibleAnswers: List<String>,
    val correctAnswer: Int,
)