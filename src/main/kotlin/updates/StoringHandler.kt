package eu.blackcult.updates

import eu.blackcult.database.MongoWrapper
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.messages.Message
import io.github.ageofwar.telejam.messages.MessageHandler
import io.github.ageofwar.telejam.messages.TextMessage

class StoringHandler(
    private val bot: Bot,
    private val mongoWrapper: MongoWrapper
) : MessageHandler {

    override fun onMessage(message: Message) {
        if (message is TextMessage) {
            println("Yes")
        } else {
            println("NOOOOOOOOOOOOoo!")
            println(message)
        }
    }
}