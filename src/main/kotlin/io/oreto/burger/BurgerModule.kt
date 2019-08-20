package io.oreto.burger

import io.jooby.Kooby

class BurgerModule : Kooby({

    path("/${group.name}/{${group.year.name}}") {

        get("/") {
            views.burger.template(null, null
                    , Server(ctx).pathParam(group.year.name, ctx.path(group.year.name).intValue())
                    .pathParam(group.page.name, group.page.defaultValue)
                    .arg(group.pages.name, group.pages.defaultValue)
                    .arg(group.pageId.name, group.pageId.defaultValue)
                    .withJs())
        }

        get("/{${group.page.name}}") {
            val page: Int = ctx.path(group.page.name).intValue()

            val burger: Burger? = Burger.at(page)
            val pageId: String = burger?.id ?: (if (page == 0) group.pageId.final else group.pageId.defaultValue.toString())

            views.burger.template(burger, Taster.list
                    , Server(ctx).pathParam(group.year.name, ctx.path(group.year.name).intValue())
                    .pathParam(group.page.name, page)
                    .arg(group.pages.name, group.pages.defaultValue)
                    .arg(group.pageId.name, pageId)
                    .withJs())
        }
    }
}) {
    companion object {
        val group = BurgerGroup("burgers")

        class BurgerGroup(override val name: String): Grouped {
            inner class Year(override val name: String = "year"
                             , override val defaultValue: Any? = 2019): Named

            inner class Page(override val name: String = "page"
                             , override val defaultValue: Any? = Burger.list.size + 1): Named

            inner class Pages(override val name: String = "pages"
                             , override val defaultValue: Any? = Burger.list.size + 1): Named

            inner class PageId(override val name: String = "pageId", val final: String = "credits"
                              , override val defaultValue: Any? = "landing"): Named

            val year = Year()
            val page = Page()
            val pages = Pages()
            val pageId = PageId()
        }
    }
}