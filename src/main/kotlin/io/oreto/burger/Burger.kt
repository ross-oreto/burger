package io.oreto.burger

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Burger(val id: String
             , val restaurant: String
             , val plate: String
             , val city: String
             , val state: String
             , val tastes: List<Double>
             , val qualities: List<Double>
             , val temperatures: List<Double>
             , val presentations: List<Double>
             , val price: String
             , val notes: String
             , val date: String) {

    val taste: Double = tastes.sum() / tastes.size
    val quality: Double = qualities.sum() / qualities.size
    val temperature: Double = temperatures.sum() / temperatures.size
    val presentation: Double = presentations.sum() / presentations.size
    val prettyDate: String = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            .format(DateTimeFormatter.ofPattern("MM dd, yyyy"))

    val total: Double = taste + quality + temperature + presentation

    companion object Burgers {

        private val mapper = jacksonObjectMapper()

        val list: List<Burger> = (mapper.readValue(
                File(Paths.get("burgers.json").toString())
        ) as List<Burger>).sortedBy { it.total }

        private fun page(l: List<Burger>, offset: Int? = null, max: Int? = null, ascending: Boolean = false)
                : List<Burger> {
            val results =
                    if (ascending) l.sortedBy { it.total }
                    else l.sortedByDescending { it.total }
            return when {
                offset == null && max == null -> results
                offset == null -> results.take(max!!)
                max == null -> results.drop(offset)
                else -> results.subList(offset, max)
            }
        }

        fun findAll(offset: Int? = null, max: Int? = null, ascending: Boolean = false) : List<Burger> {
            return page(list, offset, max, ascending)
        }

        fun get(id: String): Burger? {
            return list.find {
                it.id == id
            }
        }

        fun count(): Int {
            return list.size
        }
    }
}