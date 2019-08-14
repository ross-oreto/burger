package io.oreto.burger

import org.slf4j.Logger

abstract class AppController {
    abstract val log: Logger

    class Server(val app: App.Application
                 , val page: Page
                 , val routes: List<Route>
                 , val vars: Map<String, Any?> = mapOf()
                 , val assets: Assets? = null) {

        val jsVars = """<script type="application/javascript">
            var server = {
                env: [${app.environment.activeNames.map{ "'$it'" }.joinToString { ", " } }]
                
                page: {
                    path: '${page.path}'
                    , pattern: '${page.pattern}'
                    , params: { 
                        ${ page.params.map { "${it.key}: ${if(it.value is String) "'${it.value}'" else it.value }" }
                        .joinToString { "\n" } } 
                    }
                    , query: { 
                        ${ page.query.map { "${it.key}: ${if(it.value is String) "'${it.value}'" else it.value }" }
                        .joinToString { "\n" } } 
                    }
                }
            };
        </script>""".trimIndent()

        data class Page(val path: String
                         , val pattern: String
                         , val params: MutableMap<String, String?> = mutableMapOf()
                         , val query: MutableMap<String, String?> = mutableMapOf())

        data class Route(val pattern: String
                         , val method: String
                         , val returnType: String
                         , val pathKeys: List<String> = listOf()
                         , val produces: List<String> = listOf()
                         , val consumes: List<String> = listOf())

        data class Assets(val packageName: String
                          , val js: List<String> = listOf()
                          , val css: List<String> = listOf())
    }
}