package config

import java.util.*


object PropertiesReader {
    private const val MESSAGES = "messages.properties"
    private val properties = Properties()

    init {
        val file = this::class.java.classLoader.getResourceAsStream(MESSAGES)
        properties.load(file)
    }
    fun getProperty(key: String) : String = properties.getProperty(key)
}