package io.oreto.burger

import io.jooby.Kooby
import io.jooby.ModelAndView
import io.oreto.burger.Module.Companion.ajax

class BurgerModule : Kooby({

    path("/${burgersViewModel.group}/{${burgersViewModel.year}}") {
        get("/") {
            val year: Int = ctx.path(burgersViewModel.year).intValue()
            val rank: Int = Burger.count(year) + 1
            val pages: Int = rank

            val server: Server = Server(ctx)
                    .pathParam(burgersViewModel.year, year.toString())
                    .pathParam(burgersViewModel.rank, rank.toString())
                    .arg(burgersViewModel.pages, pages.toString())
                    .withJs()

            burgersViewModel.model = BurgersViewModel.Model(
                    listOf()
                    , Taster.list
                    , year, rank, pages, server.js)

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
                    burgerViewModel.model = BurgerViewModel.Model(Burger.at(year, page), rank)
                    ModelAndView(burgerViewModel.view)
                            .put(modelName, burgerViewModel)
                } else {
                    ModelAndView(creditsView.view)
                            .put(creditsView.tasters, Taster.list)
                }
            } else {
                val server: Server = Server(ctx)
                        .pathParam(burgersViewModel.year, year.toString())
                        .pathParam(burgersViewModel.rank, rank.toString())
                        .arg(burgersViewModel.pages, pages.toString()).withJs()

                burgersViewModel.model = BurgersViewModel.Model(
                        Burger.findAll(year).subList(0, if (page > burgerCount) burgerCount else page)
                        , Taster.list, year, rank, pages, server.js)

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
        val burgerViewModel = BurgerViewModel()
        val creditsView = CreditsView()

        open class BurgersGroup(val group: String = "burgers"
                                , val year: String = "num_year"
                                , val tasters: String = "tasters")

        open class BurgerViewModel(open val view: String = "burger.$templateExt"
                              , val burger: String = "burger") : BurgersGroup() {
            var model: Model? = null
            data class Model(val burger: Burger?, val rank: Int)
        }

        open class BurgersViewModel(val pages: String = "num_pages"
                                    , val rank: String = "num_rank") : BurgersGroup() {
            val view: String = "$group.$templateExt"
            var model: Model? = null

            data class Model(val burgers: List<Burger>
                             , val tasters: List<Taster>
                             , val year: Int
                             , val rank: Int
                             , val pages: Int
                             , val js: String? = null)
        }

        open class CreditsView(val view: String = "credits.$templateExt") : BurgersGroup()
    }
}