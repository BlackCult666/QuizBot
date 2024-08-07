package eu.blackcult.messages

import eu.blackcult.GROUP_ID
import eu.blackcult.database.MongoWrapper
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.chats.PrivateChat
import io.github.ageofwar.telejam.messages.Message
import io.github.ageofwar.telejam.messages.MessageHandler
import io.github.ageofwar.telejam.messages.NewChatMembersMessage

class StoringHandler(
    private val bot: Bot,
    private val mongoWrapper: MongoWrapper
) : MessageHandler {

    override fun onMessage(message: Message) {
        if (message.chat is PrivateChat || message.chat.id != GROUP_ID) return
        if (message.sender.id == bot.id) return

        if (message is NewChatMembersMessage) {
            for (user in message.newChatMembers) {
                if (!mongoWrapper.playerExists(user.id)) {
                    mongoWrapper.addPlayer(user.id, user.firstName)
                }
            }
        } else {
            val sender = message.sender
            if (mongoWrapper.playerExists(sender.id)) {
                val firstName = mongoWrapper.getStats(sender.id).firstName
                if (firstName != sender.firstName) mongoWrapper.updateFirstName(sender.id, sender.firstName)
            } else {
                mongoWrapper.addPlayer(sender.id, sender.firstName)
            }
        }
    }
}