package io.oreto.burger

import org.slf4j.Logger

abstract class AppController {
    abstract val log: Logger

    class Server(app: App, route: Route, vars: Map<String, Any?> = mapOf(), assets: Assets? = null) {
        data class Route(val name: String
                         , val path: String
                         , val basePath: String
                         , val params: Map<String, Any?>
                         , val query: Map<String, Any?>)

        data class Assets(val js: List<String>, val css: List<String>)
    }
}