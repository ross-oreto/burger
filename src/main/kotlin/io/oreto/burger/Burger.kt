package io.oreto.burger

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import java.nio.file.Paths

class Burger(val id: String
             , val restaurant: String
             , val plate: String
             , val tastes: List<Double>
             , val qualities: List<Double>
             , val temperatures: List<Double>
             , val presentations: List<Double>
             , val price: String
             , val notes: String) {

    val taste: Double = tastes.sum() / tastes.size
    val quality: Double = qualities.sum() / qualities.size
    val temperature: Double = temperatures.sum() / temperatures.size
    val presentation: Double = presentations.sum() / presentations.size

    val total: Double = taste + quality + temperature + presentation

    companion object Burgers {
        private val mapper = jacksonObjectMapper()

        val list: List<Burger> = (mapper.readValue(
                File(Paths.get("burgers.json").toString())
        ) as List<Burger>).sortedByDescending { it.total }

        fun get(id: String): Burger? {
            return list.find {
                it.id == id
            }
        }

        fun at(index: Int): Burger? {
            return list.getOrNull(index - 1)
        }
    }
}