package io.oreto.burger

import io.jooby.Context
import io.jooby.annotations.GET
import io.jooby.annotations.Path
import io.jooby.annotations.PathParam
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Path("/burgers/{year}")
class AppController {
    private val log: Logger = LoggerFactory.getLogger(AppController::class.java)
    private val defaultPages = 11

    fun getParentRoutePath(context: Context): String {
        return context.pathString().substring(0, context.pathString().lastIndexOf('/'))
    }

    @GET
    fun index(@PathParam year: Int, context: Context): Any {
        val pages: Int = App.info.environment.config.getInt("burger.pages").or(defaultPages)
        return views.burger.template(pages, pages, year, null, context.pathString())
    }

    @GET
    @Path("/{page}")
    fun page(@PathParam year: Int, @PathParam page: Int?, context: Context): Any {
        val pages: Int = App.info.environment.config.getInt("burger.pages").or(defaultPages)
        val index: Int = page ?: 11
        val burger: Burger? = Burger.get(index)
        return views.burger.template(index, pages, year, burger, getParentRoutePath(context))
    }
}