package io.oreto.burger

import io.jooby.Context
import io.jooby.MediaType
import io.jooby.annotations.GET
import io.jooby.annotations.Path
import io.jooby.annotations.PathParam
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Path("/asset")
class AssetController {
    private val log: Logger = LoggerFactory.getLogger(AssetController::class.java)

    val mediaTypeMap = mapOf(App.Application.Asset.Types.css.name to MediaType.CSS
            , App.Application.Asset.Types.js.name to MediaType.JS)

    @GET("/{type}/{packageName}")
    fun index(@PathParam type: String, @PathParam packageName: String, context: Context): String {
        context.setResponseType(mediaTypeMap.getOrDefault(type, MediaType.TEXT))
        val test = App.app.getPackage(packageName)?.packages?.getOrDefault(type, "")
        return App.app.getPackage(packageName)?.packages?.getOrDefault(type, "") ?: ""
    }
}