package io.oreto.burger

import io.jooby.Context

open class Server(context: Context) {

    val app: App.Application = App.app
    val page: Page = Page(context.pathString(), context.route.pattern)
    val routes: List<Route> = listOf()
    val vars: Vars = Vars()
    val assets: Assets? = null
    var js: String = ""

    class Vars {
        val str: MutableMap<String, String?> = mutableMapOf()
        val num: MutableMap<String, Number?> = mutableMapOf()
        val bool: MutableMap<String, Boolean?> = mutableMapOf()

        val strs: MutableMap<String, List<String?>> = mutableMapOf()
        val nums: MutableMap<String, List<Number?>> = mutableMapOf()
        val bools: MutableMap<String, List<Boolean?>> = mutableMapOf()

        override fun toString(): String {
            return (str.map { "${it.key}: ${if (it.value == null) "null" else "'${it.value}'"}" } +
                    num.map { "${it.key}: ${it.value ?: "null"}" } +
                    bool.map { "${it.key}: ${it.value ?: "null"}" } +
                    strs.map { "${it.key}: [${it.value.map { s: String? -> if (s == null) "null" else "'$s'" }}]" } +
                    nums.map { "${it.key}: [${it.value.map { n: Number? -> n?.toString() ?: "null" }}]" } +
                    bools.map { "${it.key}: [${it.value.map { b: Boolean? -> b?.toString() ?: "null" }}]" }
                    ).joinToString { "\n,\t" }
        }
    }

    data class Page(val path: String
                    , val pattern: String
                    , val pathParams: Vars = Vars()
                    , val query: Vars = Vars())

    data class Route(val pattern: String
                     , val method: String
                     , val returnType: String
                     , val pathKeys: List<String> = listOf()
                     , val produces: List<String> = listOf()
                     , val consumes: List<String> = listOf())

    data class Assets(val packageName: String
                      , val js: List<String> = listOf()
                      , val css: List<String> = listOf())

    fun withJs(): Server {
        val environments: String = app.environment.activeNames.map { "'$it'" }.joinToString { ", " }
        js = """<script type="application/javascript">
                    var server = {
                        env: [$environments]
                        
                        , page: {
                            path: '${page.path}'
                            , pattern: '${page.pattern}'
                            , params: { 
                                ${page.pathParams} 
                            }
                            , query: { 
                                ${page.query} 
                            }
                        }
                    };
                    </script>""".trimIndent()
        return this
    }
}
