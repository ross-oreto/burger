package io.oreto.burger

import io.jooby.Context
import io.jooby.annotations.GET
import io.jooby.annotations.Path
import io.jooby.annotations.PathParam
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Path("/burgers/{year}")
class AppController {
    private val log: Logger = LoggerFactory.getLogger(AppController::class.java)
    private val defaultPages = Burger.list.size + 1

    fun getBaseRoutePath(context: Context): String {
        val pathString: String = context.pathString()
        return pathString.substring(0
                , StringUtils.lastOrdinalIndexOf(
                    if (pathString.endsWith('/')) pathString.subSequence(0, pathString.length - 1) else pathString
                    , "/"
                    , context.path().size())
        )
    }

    @GET
    fun index(@PathParam year: Int, context: Context): Any {
        val pages: Int = App.config.getInt("burger.pages").or(defaultPages)
        return views.burger.template(null, "landing", pages, pages, year, null, getBaseRoutePath(context), App.app)
    }

    @GET
    @Path("/{page}")
    fun page(@PathParam year: Int, @PathParam page: Int?, context: Context): Any {
        val pages: Int = App.config.getInt("burger.pages").or(defaultPages)
        val index: Int = page ?: defaultPages
        val burger: Burger? = Burger.at(index)
        val pageId: String = burger?.id ?: (if (index == 0) "credits" else "landing")
        return views.burger.template(burger, pageId, index, pages, year, Taster.list, getBaseRoutePath(context), App.app)
    }
}