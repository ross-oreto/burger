package io.oreto.burger

import io.jooby.Kooby
import io.jooby.ModelAndView
import io.oreto.burger.Module.Companion.ajax

class BurgerModule : Kooby({

    path("/${burgersViewModel.group}/{${burgersViewModel.year}}") {
        get("/") {
            val year: Int = ctx.path(burgersViewModel.year).intValue()
            val pages: Int = Burger.count(year) + 1

            burgersViewModel.model = BurgersViewModel.Model(
                    listOf()
                    , Taster.list
                    , Server(ctx)
                        .pathParam(burgersViewModel.year, year.toString())
                        .pathParam(burgersViewModel.rank, (Burger.count(year) + 1).toString())
                        .arg(burgersViewModel.pages, pages.toString())
                    .withJs())

            ModelAndView(burgersViewModel.view)
                    .put(modelName, burgersViewModel)
        }

        get("/{${burgersViewModel.rank}}") {
            val ajax: Boolean = ajax(ctx)
            val year: Int = ctx.path(burgersViewModel.year).intValue()
            val rank: Int = ctx.path(burgersViewModel.rank).intValue()
            val burgerCount: Int = Burger.count(year)
            val pages: Int = burgerCount + 1
            val page: Int = pages - rank

            if (ajax) {
                if (rank > 0) {
                    ModelAndView(burgerView.view)
                            .put(burgerView.burger, Burger.at(year, page))
                            .put(burgerView.rank, rank)
                } else {
                    ModelAndView(creditsView.view)
                            .put(creditsView.tasters, Taster.list)
                }
            } else {
                burgersViewModel.model = BurgersViewModel.Model(
                        Burger.findAll(year).subList(0, if (page > burgerCount) burgerCount else page)
                        , Taster.list
                        , Server(ctx)
                            .pathParam(burgersViewModel.year, year.toString())
                            .pathParam(burgersViewModel.rank, rank.toString())
                            .arg(burgersViewModel.pages, pages.toString()).withJs())
                ModelAndView(burgersViewModel.view)
                        .put(modelName, burgersViewModel)
            }
        }
    }
}) {
    companion object {
        const val modelName = "viewModel"
        const val templateExt = "peb"
        val burgersViewModel = BurgersViewModel()
        val burgerView = BurgerView()
        val creditsView = CreditsView()

        open class BurgersGroup(val group: String = "burgers", val year: String = "num_year")

        open class BurgerView(open val view: String = "burger.$templateExt"
                              , val burger: String = "burger"
                              , val rank: String = "num_rank") : BurgersGroup()

        open class BurgersViewModel(val pages: String = "num_pages") : BurgerView() {
            override val view: String = "$group.$templateExt"
            var model: Model? = null

            data class Model(val burgers: List<Burger>, val tasters: List<Taster>, val server: Server)
        }

        open class CreditsView(val view: String = "credits.$templateExt"
                               , val tasters: String = "tasters") : BurgersGroup()
    }
}