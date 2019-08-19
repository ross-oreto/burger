package io.oreto.burger

import io.jooby.Kooby

class BurgerModule : Kooby({
    val defaultPages = Burger.list.size + 1

    val yearParamName = "year"
    val pageParamName = "page"

    path("/burgers/{$yearParamName}") {
        get("/") {
            views.burger.template(null, null
                    , Server(ctx).pathParam(yearParamName, ctx.path(yearParamName).intValue())
                    .pathParam(pageParamName, defaultPages)
                    .param("pages", defaultPages)
                    .param("pageId", "landing")
                    .withJs())
        }

        get("/{$pageParamName}") {
            val page: Int = ctx.path(pageParamName).intValue()

            val burger: Burger? = Burger.at(page)
            val pageId: String = burger?.id ?: (if (page == 0) "credits" else "landing")

            views.burger.template(burger, Taster.list
                    , Server(ctx).pathParam(yearParamName, ctx.path(yearParamName).intValue())
                    .pathParam(pageParamName, page)
                    .param("pages", defaultPages)
                    .param("pageId", pageId)
                    .withJs())
        }
    }
})