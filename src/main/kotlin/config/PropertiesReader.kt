package config

import java.util.*


object PropertiesReader {
    private const val SETTINGS = "settings.properties"
    private val properties = Properties()

    init {
        val file = this::class.java.classLoader.getResourceAsStream(SETTINGS)
        properties.load(file)
    }
    fun getProperty(key: String) : String = properties.getProperty(key)
}