package database

import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document

class DatabaseWrapper {
    val mongoClient: MongoClient
    val mongoDatabase: MongoDatabase
    val collection: MongoCollection<Document>

    init {
        mongoClient = MongoClient(MongoClientURI("mongodb://localhost:27017"))
        mongoDatabase = mongoClient.getDatabase("triviabot")
        collection = mongoDatabase.getCollection("players")
    }

    fun addPlayer(id: Long) {
        val document = Document("id", id)
        document.append("correctAnswers", 0)
        document.append("wrongAnswers", 0)
        document.append("actualStreak", 0)
        document.append("bestStreak", 0)
        collection.insertOne(document)
    }

    fun checkPlayer(id: Long) {
        val player = collection.find(Document("id", id)).first()
        if (player == null) {
            addPlayer(id)
        }
    }

    fun getCorrectAnswers(id: Long): Int {
        val player = collection.find(Document("id", id)).first()
        return player["correctAnswers"] as Int
    }

    fun getWrongAnswers(id: Long): Int {
        val player = collection.find(Document("id", id)).first()
        return player["wrongAnswers"] as Int
    }

    fun getActualStreak(id: Long): Int {
        val player = collection.find(Document("id", id)).first()
        return player["actualStreak"] as Int
    }

    fun getBestStreak(id: Long): Int {
        val player = collection.find(Document("id", id)).first()
        return player["bestStreak"] as Int
    }

    fun incrementCorrectNumber(id: Long) {
        checkPlayer(id)
        val player = collection.find(Document("id", id)).first()
        val incValue = BasicDBObject("correctAnswers", 1)
        val intModifier = BasicDBObject("\$inc", incValue)
        collection.updateOne(player, intModifier)
    }

    fun incrementErrorsNumber(id: Long) {
        checkPlayer(id)
        val player = collection.find(Document("id", id)).first()
        val incValue = BasicDBObject("wrongAnswers", 1)
        val intModifier = BasicDBObject("\$inc", incValue)
        collection.updateOne(player, intModifier)
    }

    fun incrementStreak(id: Long) {
        checkPlayer(id)
        val player = collection.find(Document("id", id)).first()
        val incValue = BasicDBObject("actualStreak", 1)
        val intModifier = BasicDBObject("\$inc", incValue)
        collection.updateOne(player, intModifier)
    }

    fun resetActualStreak(id: Long) {
        checkPlayer(id)
        val player = collection.find(Document("id", id)).first()
        val incValue = BasicDBObject("actualStreak", 0)
        val intModifier = BasicDBObject("\$set", incValue)
        collection.updateOne(player, intModifier)
    }

    fun checkBestStreak(id: Long) {
        checkPlayer(id)
        val player = collection.find(Document("id", id)).first()
        if (getActualStreak(id) > getBestStreak(id)) {
            val incValue = BasicDBObject("bestStreak", getActualStreak(id))
            val intModifier = BasicDBObject("\$set", incValue)
            collection.updateOne(player, intModifier)
        }
    }
}
