package io.oreto.burger

import io.jooby.Kooby
import io.jooby.MediaType
import io.jooby.annotations.Path

@Path("/asset")
class AssetModule : Kooby({

    val mediaTypeMap = mapOf(App.Application.Asset.Types.css.name to MediaType.CSS
            , App.Application.Asset.Types.js.name to MediaType.JS)

    val group = config.getString("assets.bundlePath")

    path("/$group") {
        get("/{$typeParamName}/{$packageParamName}") {
            val type: String = ctx.path(typeParamName).value()
            ctx.setResponseType(mediaTypeMap.getOrDefault(type, MediaType.TEXT))
            App.app.getPackage(ctx.path(packageParamName).value())?.packages?.get(type)?.contents ?: ""
        }

        get("/{$typeParamName}/{$packageParamName}/files") {
            ctx.setResponseType(MediaType.JSON)
            App.app.getPackage(ctx.path(packageParamName).value())
                    ?.packages?.get(ctx.path(typeParamName).value())?.files?.map {
                it.path
            } ?: ""
        }
    }
}) {
    companion object {
        val typeParamName = "type"
        val packageParamName = "packageName"
    }
}