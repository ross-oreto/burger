package io.oreto.burger

import io.jooby.Context
import io.jooby.annotations.GET
import io.jooby.annotations.Path
import io.jooby.annotations.QueryParam
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Path("/asset")
class AssetController {
    private val log: Logger = LoggerFactory.getLogger(AssetController::class.java)

    @GET
    fun index(@QueryParam packageName: String, context: Context): String {
        return App.app.getPackage(packageName)?.cssPackage ?: ""
    }
}