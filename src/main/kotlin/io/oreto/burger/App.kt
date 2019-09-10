package io.oreto.burger

import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import io.jooby.*
import io.jooby.pebble.PebbleModule
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Paths


class App :Kooby({

    routerOptions { ignoreTrailingSlash = true }

    serverOptions {
        port = System.getenv("PORT")?.toInt() ?: environment.config.getInt("server.port")
    }

    onStarting {
        log.info("starting app")
        serverJs = Server.baseJs(environment, routes, config)
        app = application(environment, serverOptions, contextPath, routes)
    }

    onStarted {
        log.info("app started. ${app.baseUrl}")
        log.info("environment: ${app.environment.activeNames}")
    }

    onStop {
        log.info("stopping app")
    }

    assets(config.getString("assets.pattern"), config.getString("assets.path"))

    before {
        attribute(Server.name, Server(ctx))
    }

    install(PebbleModule())
    use(AssetModule())
    use(BurgerModule())
}) {
    companion object {
        val IS_WINDOWS = System.getProperty("os.name").toLowerCase().startsWith("windows")
        val IS_BASH = !IS_WINDOWS

        lateinit var serverJs: File
        lateinit var app: Application

        fun application(env: Environment
                        , serverOptions: ServerOptions?
                        , contextPath: String
                        , routes: List<Route>): Application {
            val scheme = "http"
            val port = serverOptions?.port ?: env.config.getInt("server.port")

            val host = "localhost"
            val baseUrl = "$scheme://$host:$port$contextPath"

            return Application(env, scheme, host, port, contextPath, baseUrl, routes)
        }

        fun exists(path: String, config: Config): Boolean {
            return try {
                config.hasPath(path)
            } catch (e: ConfigException.BadPath) {
                false
            }
        }
    }

    class Application(val environment: Environment
                       , val scheme: String
                       , val host: String
                       , val port: Int
                       , val path: String
                       , val baseUrl: String
                      , val routes: List<Route>) {

        val conf: Config = environment.config

        private val assetsConfig = conf.getConfig("assets")
        private val min = environment.isActive(ENV.uat.name) || environment.isActive(ENV.prod.name)
        private val assets: List<Asset> =
                if (exists("assets.package", conf))
                    conf.getObject("assets.package")
                    .entries
                    .map {
                        Asset(it.key, assetsConfig, min) }
                else listOf()

        fun getPackage(name: String): Asset? {
            return assets.findLast { it.packageName == name }
        }

        class NamedRoute(val route: Route) {
            companion object {
                fun routeToName(route: Route): String {
                    return route.pattern.substring(1)
                            .replace("{", "by-")
                            .replace("}", "")
                            .replace("/", "-")
                }
            }

            val name: String = routeToName(route)
        }

        class Asset(val packageName: String, val conf: Config, val minify: Boolean = false) {

            data class Pack(val name: String, val type: String, val files: List<File>, val minify: Boolean = false) {
                var contents = ""

                init {
                    GlobalScope.launch { contents = if(minify) minify(files) else combine(files) }
                }
            }

            val packages = mapOf(
                    Types.css.name to Pack(packageName
                                    , Types.css.name
                                    , packFiles(Types.css.name, packageName, conf)
                                    , minify)
                    , Types.js.name to Pack(packageName
                                    , Types.js.name
                                    , listOf(serverJs) + packFiles(Types.js.name, packageName, conf)
                                    , minify)
            )

            companion object {
                fun packFiles(type: String
                                    , packageName: String
                                    , conf: Config
                                    , files: MutableList<File> = mutableListOf()
                                    , names: MutableList<String> = mutableListOf()): List<File> {
                    if (!names.contains(packageName)) {
                        names.add(packageName)

                        val path = conf.getString("path")
                        if (exists("package.$packageName.$type", conf)) {
                            conf.getStringList("package.$packageName.$type")
                                    .forEach { name: String ->
                                        if (name.endsWith(".$type")) {
                                            val newFile = File("$path/$name")
                                            if (newFile.exists() && files.none { f: File -> f.path == newFile.path }) {
                                                files.add(newFile)
                                            }
                                        } else {
                                            packFiles(type, name, conf, files, names)
                                        }
                                    }
                        }
                    }
                    return files
                }

                fun combine(files: List<File>): String {
                    return if (files.isNotEmpty()) {  files.joinToString("\n") { it.readText() } } else ""
                }

                fun minify(files: List<File>): String {
                    return if (files.isNotEmpty()) {
                        val exe = Paths.get(".", "minify", "minify").toString() +
                                if (IS_WINDOWS) ".exe" else ""

                        val fileNames = files.map { it.path }.joinToString(" ") { it }
                        val cmd = "$exe $fileNames"
                        println(cmd)

                        val process: Process = Runtime.getRuntime().exec(cmd)
                        val reader = BufferedReader(
                                InputStreamReader(process.inputStream))
                        val min = reader.readLines().joinToString("\n")
                        reader.close()
                        min
                    } else ""
                }
            }

            enum class Types {
                css, js
            }
        }

        fun isLocal(): Boolean {
            return environment.isActive(ENV.local.name)
        }

        fun isDev(): Boolean {
            return environment.isActive(ENV.dev.name)
        }

        fun isUat(): Boolean {
            return environment.isActive(ENV.uat.name)
        }

        fun isProd(): Boolean {
            return environment.isActive(ENV.prod.name)
        }

        fun isTest(): Boolean {
            return environment.isActive(ENV.test.name)
        }
    }

    enum class ENV {
        local
        , dev
        , uat
        , prod
        , test
        , other
    }
}

fun main(args: Array<String>) {
    runApp(args, App::class)
}