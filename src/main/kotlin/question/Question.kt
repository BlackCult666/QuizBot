package eu.blackcult.question

/**
 * Simple data class representing a generic question.
 */
data class Question(
    val question: String,
    val description: String,
    val possibleAnswers: List<String>,
    val correctAnswer: Int,
)