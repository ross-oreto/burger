package io.oreto.burger

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import java.nio.file.Paths

class Burger(val id: Int
             , val restaurant: String
             , val plate: String
             , val taste: Double
             , val quality: Double
             , val temperature: Double
             , val presentation: Double
             , val price: String
             , val notes: String) {

    val total: Double = taste + quality + temperature + presentation

    companion object Burgers {
        private val mapper = jacksonObjectMapper()

        val list: List<Burger> = mapper.readValue(
                File(Paths.get("burgers.json").toString())
        )

        fun get(id: Int): Burger? {
            return list.find {
                it.id == id
            }
        }
    }
}