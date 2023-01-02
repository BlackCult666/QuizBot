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
        val title = "<b>DOMANDA</b> <code>#${number}</code>\n\n"
        val message = "<b>${question}</b>\n"
        var result = ""
        for (i in answers) {
            result += "\uD83D\uDD39 $i\n"
        }
        return title + message + result
    }
}
