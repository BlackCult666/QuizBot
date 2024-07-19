package eu.blackcult.messages

import eu.blackcult.GROUP_ID
import eu.blackcult.json.ResourceLoader.messages
import eu.blackcult.utils.sendVideo
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.messages.Message
import io.github.ageofwar.telejam.messages.MessageHandler
import io.github.ageofwar.telejam.messages.NewChatMembersMessage
import io.github.ageofwar.telejam.text.Text
import mentionPlayer

class WelcomeHandler(
    private val bot: Bot
) : MessageHandler {

    override fun onMessage(message: Message) {
        if (message is NewChatMembersMessage) {
            if (message.chat.id != GROUP_ID) return

            for (user in message.newChatMembers) {
                if (user.id == bot.id) {
                    val welcomeText = messages["welcome"]
                        ?.replace("{user}", mentionPlayer(message.sender.id, message.sender.firstName))

                    bot.sendVideo(message, Text.parseHtml(welcomeText), "https://telegra.ph/file/f58a164a9fd00f85b5466.mp4")
                }
            }
        }
    }
}