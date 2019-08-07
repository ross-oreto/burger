package io.oreto.burger

import io.jooby.annotations.Path
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Path("/asset")
class AssetController {
    private val log: Logger = LoggerFactory.getLogger(AssetController::class.java)

//    @GET
//    fun index(@QueryParam year: Int, context: Context): Any {
//
//    }
}