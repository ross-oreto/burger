package io.oreto.burger

import io.jooby.Kooby
import io.jooby.ModelAndView
import io.oreto.burger.Module.Companion.ajax

class BurgerModule : Kooby({

    path("/${burgersGroup.name}/{${burgersGroup.year}}") {
        get("/") {
            val year: Int = ctx.path(burgersGroup.year).intValue()
            val pages: Int = Burger.count(year) + 1

            burgersGroup.burgersViewModel.model = BurgersGroup.BurgersViewModel.Model(
                    listOf()
                    , Taster.list
                    , Server(ctx)
                        .pathParam(burgersGroup.year, year.toString())
                        .pathParam(burgersGroup.burgersViewModel.rank, (Burger.count(year) + 1).toString())
                        .arg(burgersGroup.burgersViewModel.pages, pages.toString())
                    .withJs())

            ModelAndView(burgersGroup.burgersViewModel.view)
                    .put(modelName, burgersGroup.burgersViewModel)
        }

        get("/{${burgersGroup.burgersViewModel.rank}}") {
            val ajax: Boolean = ajax(ctx)
            val year: Int = ctx.path(burgersGroup.year).intValue()
            val rank: Int = ctx.path(burgersGroup.burgersViewModel.rank).intValue()
            val burgerCount: Int = Burger.count(year)
            val pages: Int = burgerCount + 1
            val page: Int = pages - rank

            if (ajax) {
                if (rank > 0) {
                    burgersGroup.burgerViewModel.model = BurgersGroup.BurgerViewModel.Model(Burger.at(year, page), rank)
                    ModelAndView(burgersGroup.burgerViewModel.view)
                            .put(modelName, burgersGroup.burgerViewModel)
                } else {
                    burgersGroup.creditsViewModel.model = BurgersGroup.CreditsViewModel.Model(Taster.list)
                    ModelAndView(burgersGroup.creditsViewModel.view)
                            .put(modelName, burgersGroup.creditsViewModel)
                }
            } else {
                burgersGroup.burgersViewModel.model = BurgersGroup.BurgersViewModel.Model(
                        Burger.findAll(year).subList(0, if (page > burgerCount) burgerCount else page)
                        , Taster.list
                        , Server(ctx)
                            .pathParam(burgersGroup.year, year.toString())
                            .pathParam(burgersGroup.burgersViewModel.rank, rank.toString())
                            .arg(burgersGroup.burgersViewModel.pages, pages.toString()).withJs())
                ModelAndView(burgersGroup.burgersViewModel.view)
                        .put(modelName, burgersGroup.burgersViewModel)
            }
        }
    }
}) {
    companion object {
        const val modelName = "viewModel"
        const val templateExt = "peb"
        val burgersGroup: BurgersGroup = BurgersGroup()

        class BurgersGroup(val name: String = "burgers", val year: String = "num_year") {
            class BurgersViewModel(var model: Model? = null
                                   , val view: String = "burgers.$templateExt"
                                   , val rank: String = "num_rank"
                                   , val pages: String = "num_pages") {
                data class Model(val burgers: List<Burger>, val tasters: List<Taster>, val server: Server)
            }
            class BurgerViewModel(var model: Model? = null
                                  , val view: String = "burger.$templateExt") {
                data class Model(val burger: Burger?, val rank: Int)
            }
            class CreditsViewModel(var model: Model? = null
                                   , val view: String = "credits.$templateExt") {
                data class Model(val tasters: List<Taster>)
            }

            val burgerViewModel = BurgerViewModel()
            val burgersViewModel = BurgersViewModel()
            val creditsViewModel = CreditsViewModel()
        }
    }
}