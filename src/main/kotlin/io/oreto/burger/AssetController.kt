package io.oreto.burger

import io.jooby.Context
import io.jooby.MediaType
import io.jooby.annotations.GET
import io.jooby.annotations.Path
import io.jooby.annotations.PathParam
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Path("/asset")
class AssetController : AppController() {
    override val log: Logger
        get() = LoggerFactory.getLogger(this.javaClass)

    val mediaTypeMap = mapOf(App.Application.Asset.Types.css.name to MediaType.CSS
            , App.Application.Asset.Types.js.name to MediaType.JS)

    @GET("/{type}/{packageName}")
    fun index(@PathParam type: String, @PathParam packageName: String, context: Context): String {
        App.app.
        context.setResponseType(mediaTypeMap.getOrDefault(type, MediaType.TEXT))
        return App.app.getPackage(packageName)?.packages?.get(type)?.contents ?: ""
    }

    @GET("/{type}/{packageName}/files")
    fun files(@PathParam type: String, @PathParam packageName: String, context: Context): List<String>? {
        context.setResponseType(MediaType.JSON)
        val assetsPath = App.config.getString("assets.path")
        val assetsPattern = App.config.getString("assets.pattern")?.replace("/*", "")
        return App.app.getPackage(packageName)?.packages?.get(type)?.files?.map {
            it.path.replace(assetsPath, assetsPattern ?: "/assets")
        }
    }
}