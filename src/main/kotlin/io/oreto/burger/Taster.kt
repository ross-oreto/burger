package io.oreto.burger

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import java.nio.file.Paths

class Taster(val id: Int
             , val name: String
             , val nationality: String
             , val occupation: String
             , val birthPlace: String
             , val shortName: String) {

    companion object Burgers {
        private val mapper = jacksonObjectMapper()

        val list: List<Taster> = (mapper.readValue(
                File(Paths.get("tasters.json").toString())
        ) as List<Taster>).sortedBy { it.id }

        fun at(index: Int): Taster? {
            return list.getOrNull(index)
        }
    }
}