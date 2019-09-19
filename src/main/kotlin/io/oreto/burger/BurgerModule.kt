package io.oreto.burger

import io.jooby.Kooby
import io.jooby.ModelAndView
import io.oreto.burger.Module.Companion.ajax

class BurgerModule : Kooby({

    path("/${burgersViewModel.group}") {

        get("/{${burgerViewModel.city}}/{${burgerViewModel.state}}") {
            val rank: Int = Burger.count() + 1
            val pages: Int = rank

            val server: Server = Server(ctx, burgersViewModel.group)
                    .pathParam(burgersViewModel.rank, rank.toString())
                    .arg(burgersViewModel.pages, pages.toString()).build()

            ctx.pathString()

            burgersViewModel.model = BurgersViewModel.Model(
                    listOf()
                    , Taster.list
                    , ctx.path(burgerViewModel.city).toString().capitalize()
                    , ctx.path(burgerViewModel.state).toString().toUpperCase()
                    , rank
                    , pages
                    , server.assets)

            ModelAndView(burgersViewModel.view)
                    .put(modelName, burgersViewModel)
        }

        get("/{${burgerViewModel.city}}/{${burgerViewModel.state}}/{${burgersViewModel.rank}}") {
            val ajax: Boolean = ajax(ctx)
            val rank: Int = ctx.path(burgersViewModel.rank).intValue()
            val burgerCount: Int = Burger.count()
            val pages: Int = burgerCount + 1

            if (ajax) {
                if (rank > 0) {
                    burgerViewModel.model = BurgerViewModel.Model(Burger.findAll()
                            .getOrNull(rank - 1), rank)
                    ModelAndView(burgerViewModel.view)
                            .put(modelName, burgerViewModel)
                } else {
                    ModelAndView(creditsView.view)
                            .put(creditsView.tasters, Taster.list)
                }
            } else {
                val server: Server = Server(ctx, burgersViewModel.group)
                        .arg(burgersViewModel.pages, pages.toString()).build()

                val page: Int = pages - rank
                burgersViewModel.model = BurgersViewModel.Model(
                        Burger.findAll()
                                .reversed().subList(0, if (page > burgerCount) burgerCount else page)
                        , Taster.list
                        , ctx.path(burgerViewModel.city).toString().capitalize()
                        , ctx.path(burgerViewModel.state).toString().toUpperCase()
                        , rank, pages, server.assets)

                ModelAndView(burgersViewModel.view)
                        .put(modelName, burgersViewModel)
            }
        }
    }
}) {
    companion object {
        const val modelName = "viewModel"
        const val templateExt = "peb"
        const val defaultCity = "Nashville"
        const val defaultState = "TN"

        val burgersViewModel = BurgersViewModel()
        val burgerViewModel = BurgerViewModel()
        val creditsView = CreditsView()

        open class BurgersGroup(val group: String = "burgers"
                                , val city: String = "city"
                                , val state: String = "state"
                                , val tasters: String = "tasters")

        open class BurgerViewModel(open val view: String = "burger.$templateExt"
                              , val burger: String = "burger") : BurgersGroup() {
            var model: Model? = null
            data class Model(val burger: Burger?, val rank: Int)
        }

        open class BurgersViewModel(val pages: String = "Pages"
                                    , val rank: String = "Rank") : BurgersGroup() {
            open val view: String = "$group.$templateExt"
            var model: Model? = null

            data class Model(val burgers: List<Burger>
                             , val tasters: List<Taster>
                             , val city: String
                             , val state: String
                             , val rank: Int
                             , val pages: Int
                             , val assets: Server.Assets)
        }

        open class CreditsView(val view: String = "credits.$templateExt") : BurgersGroup()
    }
}