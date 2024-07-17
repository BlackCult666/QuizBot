package eu.blackcult.database

import com.mongodb.BasicDBObject
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document

class MongoWrapper {
    private val client: MongoClient = MongoClients.create("mongodb://localhost:27017")
    private val database: MongoDatabase = client.getDatabase("quizBot")
    private var collection: MongoCollection<Document> = database.getCollection("users")

    // Method to add player to database.
    fun addPlayer(id: Long, firstName: String) {
        val document = Document("id", id)
        document.append("firstName", firstName)
        document.append("correctAnswers", 0)
        document.append("wrongAnswers", 0)
        document.append("actualStreak", 0)
        document.append("bestStreak", 0)
        collection.insertOne(document)
    }

    // Method to check if user exists.
    fun playerExists(id: Long): Boolean {
        val player = collection.find(Document("id", id)).first()
        return player != null
    }

    // Method to get user stats.
    fun getStats(id: Long): PlayerStats {
        val player = collection.find(Document("id", id)).first()
        val firstName = player?.getString("firstName") ?: ""
        val correctAnswers = player?.getInteger("correctAnswers") ?: 0
        val wrongAnswers = player?.getInteger("wrongAnswers") ?: 0
        val actualStreak = player?.getInteger("actualStreak") ?: 0
        val bestStreak = player?.getInteger("bestStreak") ?: 0

        return PlayerStats(id, firstName, correctAnswers, wrongAnswers, actualStreak, bestStreak)
    }

    // Method to update user's first name.
    fun updateFirstName(id: Long, newFirstName: String) {
        val filter = Document("id", id)
        val update = BasicDBObject("\$set", Document("firstName", newFirstName))
        collection.updateOne(filter, update)
    }

    // Method to increment a user's correct answers number.
    fun incrementCorrectAnswers(id: Long) {
        val filter = Document("id", id)
        val update = BasicDBObject("\$inc", BasicDBObject("correctAnswers", 1))
        collection.updateOne(filter, update)
    }

    // Method to increment a user's wrong answers number.
    fun incrementWrongAnswers(id: Long) {
        val filter = Document("id", id)
        val update = BasicDBObject("\$inc", BasicDBObject("wrongAnswers", 1))
        collection.updateOne(filter, update)
    }

    // Method to increment the actual player's streak.
    fun incrementActualStreak(id: Long) {
        val filter = Document("id", id)
        val update = BasicDBObject("\$inc", BasicDBObject("actualStreak", 1))
        collection.updateOne(filter, update)
    }

    // Method to set the actual player streak to 0.
    fun resetActualStreak(id: Long) {
        val filter = Document("id", id)
        val update = BasicDBObject("\$set", BasicDBObject("actualStreak", 0))
        collection.updateOne(filter, update)
    }

    // Method to update the user's best streak.
    fun updateBestStreak(id: Long) {
        val player = collection.find(Document("id", id)).first()
        val actualStreak = player?.getInteger("actualStreak") ?: 0
        val bestStreak = player?.getInteger("bestStreak") ?: 0

        if (actualStreak > bestStreak) {
            val update = BasicDBObject("\$set", BasicDBObject("bestStreak", actualStreak))
            collection.updateOne(Document("id", id), update)
        }
    }
}

data class PlayerStats(
    val id: Long,
    val firstName: String,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val actualStreak: Int,
    val bestStreak: Int
)