package io.oreto.burger

import com.typesafe.config.Config
import io.jooby.Context
import io.jooby.Environment
import io.jooby.Route
import java.io.File
import java.nio.file.Paths

open class Server(context: Context) {

    companion object {
        private const val serverJs = "server.js"
        const val name = "server"

        fun baseJs(environment: Environment, routes: List<Route>, config: Config): File {
            val file = File(Paths.get(config.getString("assets.path"), "js", serverJs).toString())
            file.writeText("""if(typeof $name === 'undefined') $name = { };
$name.env = [${environment.activeNames.map { "'$it'" }.joinToString(", ") { it }}];
$name.routes = ${routes.map { routeToJs(it) }};
$name.route = function(name) { return this.routes.find(function(r) { return r.name === name; }); };
""".trimIndent())
            return file
        }

        private fun routeToJs(namedRoute: App.Application.NamedRoute): String {
            return """{ name:'${namedRoute.name}'
                , pattern:'${namedRoute.route.pattern}'
               , method:'${namedRoute.route.method}'
               , returnType:'${namedRoute.route.returnType}'
               , pathKeys:${namedRoute.route.pathKeys.map{ "'$it'" } }
               , produces:${namedRoute.route.produces.map{ "'$it'" } }
               , consumes:${namedRoute.route.consumes.map{ "'$it'" } }
               , pathParams: {}
               , query: {}
               , queryString: ${queryStringJsFun()}
                , toString: ${urlToJsFun(namedRoute.route.pathKeys)}
        }""".trimIndent()
        }

        private fun routeToJs(route: Route): String {
            return routeToJs(App.Application.NamedRoute(route))
        }

        private fun queryStringJsFun(): String {
            return """function(query) {
            query = query == null ? this.query : query;
            var params = [];
            for(q in query) params.push(q+'='+query[q]);
            return params.length > 0 ? '?' + params.join('&') : '';
        }""".trimIndent()
        }

        private fun urlToJsFun(pathKeys: List<String>): String {
            val starVarName = "x"
            val args: String = (pathKeys + "query").joinToString(", ") { if(it == "*") starVarName else it }
            return """function($args) {
            var s = this.pattern; 
            ${pathKeys.map {
                val varName = if(it == "*") starVarName else it
                "s = s.replace('{$it}', $varName == null ? this.pathParams['$it'] : $varName);"
            }.joinToString("\n") { it } }
            return s + this.queryString(query);
        }""".trimIndent()
        }
    }

    val url: Url = Url(context)
    val args: MutableMap<String, String?> = mutableMapOf()
    val assets: Assets? = null
    var js: String = ""

    fun pathParam(name: String, value: String?): Server {
        url.pathParams[name] = value
        return this
    }

    fun query(name: String, value: String?): Server {
        url.query[name] = value
        return this
    }

    fun arg(name: String, value: String?): Server {
        args[name] = value
        return this
    }

    fun getPathParam(name: String): Any? {
        return url.pathParams[name]
    }

    fun getQuery(name: String): Any? {
        return url.query[name]
    }

    fun getArg(name: String): Any? {
        return args[name]
    }

    data class Url(val context: Context) {
        val path: String = context.pathString()
        val pattern: String = context.route.pattern
        val pathKeys: List<String> = context.route.pathKeys
        val pathParams: MutableMap<String, String?> = context.pathMap()
        val query: MutableMap<String, String?> = context.queryMap()
    }

    data class Assets(val packageName: String
                      , val js: List<String> = listOf()
                      , val css: List<String> = listOf())

    private fun paramsToString(params: MutableMap<String, String?>): String {
        return params.map {
            entryToString(it.key, it.value)
        }.joinToString(", ") { it }
    }

    private fun entryToString(key: String, value: String?): String {
        return if (value == null) {
            "null"
        } else {
            val typeKey = key.split("_")
            val type = if (typeKey.size > 1) typeKey[0] else null
            if (type == null) "$key: '$value'"
            else "$key: $value"
        }
    }

    fun withJs(): Server {
        val serverVar = """{
                        url: {
                            path: '${url.path}'
                            , pattern: '${url.pattern}'
                            , pathKeys: ${url.pathKeys.map{ "'$it'" } }
                            , pathParams: { ${paramsToString(url.pathParams)} }
                            , query: { ${paramsToString(url.query)} }
                            , queryString: ${queryStringJsFun()}
                            , toString: ${urlToJsFun(url.pathKeys)}
                        }
                        , args: { ${paramsToString(args)} }
                        , getArg: function(name) { return args[name]; } 
                        , getQuery: function(name) { return url.query[name]; } 
                        , getPathParam: function(name) { return url.pathParams[name]; } 
                    }
        """.trimIndent().replace("\n", "").replace(Regex("\\s+"), " ")
        js = """<script type="application/javascript">var $name=$serverVar;</script>"""
        return this
    }
}
