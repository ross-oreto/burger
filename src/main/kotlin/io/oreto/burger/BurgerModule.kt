package io.oreto.burger

import io.jooby.Kooby

class BurgerModule : Kooby({
    val defaultPages = Burger.list.size + 1

    get("/") {
        val pages: Int = App.config.getInt("burger.pages").or(defaultPages)
        val year = ctx.path("year").intValue()
        views.burger.template(null, "landing", pages, pages, year, null, "/burgers", App.app, Server(ctx).withJs())
    }

    get("/{page}") {
        val pages: Int = App.config.getInt("burger.pages").or(defaultPages)

        val year = ctx.path("year").intValue()
        val page: Int? = ctx.path("page").intValue()
        val index: Int = page ?: defaultPages
        val burger: Burger? = Burger.at(index)
        val pageId: String = burger?.id ?: (if (index == 0) "credits" else "landing")

        views.burger.template(burger, pageId, index, pages, year, Taster.list, "/burgers", App.app, Server(ctx).withJs())
    }
})