package io.oreto.burger

import io.jooby.Kooby
import io.oreto.burger.Module.ajax

class BurgerModule : Kooby({

    path("/${group.name}/{${group.year.name}}") {

        get("/") {
            val year: Int = ctx.path(group.year.name).intValue()
            val pages: Int = Burger.count(year) + 1
            views.burgers.template(listOf()
                    , Server(ctx).pathParam(group.year.name, year)
                    .pathParam(group.rank.name, Burger.count(year) + 1)
                    .arg(group.pages.name, pages)
                    .withJs(), Taster.list)
        }

        get("/{${group.rank.name}}") {
            val ajax: Boolean = ajax(ctx)
            val year: Int = ctx.path(group.year.name).intValue()
            val rank: Int = ctx.path(group.rank.name).intValue()
            val burgerCount: Int = Burger.count(year)
            val pages: Int = burgerCount + 1
            val page: Int = pages - rank

            if (ajax) {
                if (rank > 0) {
                    views.burger.template(Burger.at(year, page), rank)
                } else {
                    views.credits.template(Taster.list)
                }
            }
            else views.burgers.template(
                    Burger.findAll(year).subList(0, if (page > burgerCount) burgerCount else page)
                    , Server(ctx)
                    .pathParam(group.year.name, year)
                    .pathParam(group.rank.name, rank)
                    .arg(group.pages.name, pages).withJs(), Taster.list)
        }
    }
}) {
    companion object {
        val group = BurgerGroup("burgers")

        class BurgerGroup(override val name: String): Grouped {
            inner class Year(override val name: String = "year"
                             , override val defaultValue: Int = 2019): Named

            inner class Rank(override val name: String = "rank"
                             , override val defaultValue: Int = Burger.list.size): Named

            inner class Pages(override val name: String = "pages"
                             , override val defaultValue: Int = Burger.list.size): Named

            val year = Year()
            val rank = Rank()
            val pages = Pages()
        }
    }
}