package utils

import io.github.ageofwar.telejam.json.Json
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup

fun mentionPlayer(firstName: String, id: Long): String {
    return "<a href='tg://user?id=$id'>$firstName</a>"
}

fun getButtons(): InlineKeyboardMarkup {
    val question = QuestionManager.question
    val answers = question.possibleAnswers
    return Json.fromJson(
        "{\"inline_keyboard\": [[{\"text\":\"${answers[0]}\", \"callback_data\":\"answer@${question.uuid}@${answers[0]}\"}," +
                "{\"text\":\"${answers[1]}\", \"callback_data\":\"answer@${question.uuid}@${answers[1]}\"}], " +
                "[{\"text\":\"${answers[2]}\", \"callback_data\":\"answer@${question.uuid}@${answers[2]}\"}, " +
                "{\"text\":\"${answers[3]}\", \"callback_data\":\"answer@${question.uuid}@${answers[3]}\"}]]}",
        InlineKeyboardMarkup::class.java
    )
}