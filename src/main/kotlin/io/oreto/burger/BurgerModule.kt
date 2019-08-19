package io.oreto.burger

import io.jooby.Kooby

class BurgerModule : Kooby({

    path("/${group.name}/{${group.year}}") {
        get("/") {
            views.burger.template(null, null
                    , Server(ctx).pathParam(group.year.name, ctx.path(group.year.name).intValue())
                    .pathParam(group.page.name, group.page.defaultValue)
                    .param("pages", group.page.defaultValue)
                    .param("pageId", "landing")
                    .withJs())
        }

        get("/{${group.page.name}}") {
            val page: Int = ctx.path(group.page.name).intValue()

            val burger: Burger? = Burger.at(page)
            val pageId: String = burger?.id ?: (if (page == 0) "credits" else "landing")

            views.burger.template(burger, Taster.list
                    , Server(ctx).pathParam(group.year.name, ctx.path(group.year.name).intValue())
                    .pathParam(group.page.name, page)
                    .param("pages", group.page.defaultValue)
                    .param("pageId", pageId)
                    .withJs())
        }
    }
}) {
    companion object {
        interface Grouped { val name: String }
        interface Named {
            val name: String
            val defaultValue: Any?
        }
        val group = BurgerGroup("burgers")

        class BurgerGroup(override val name: String): Grouped {
            inner class Year(override val name: String = "year"
                             , override val defaultValue: Any? = 2019): Named

            inner class Page(override val name: String = "page"
                             , override val defaultValue: Any? = Burger.list.size + 1): Named

            val page = Page()
            val year = Year()
        }
    }
}