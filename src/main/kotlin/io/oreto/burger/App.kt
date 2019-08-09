package io.oreto.burger

import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import io.jooby.Environment
import io.jooby.Kooby
import io.jooby.ServerOptions
import io.jooby.rocker.RockerModule
import io.jooby.runApp
import java.io.File

class App :Kooby({

    routerOptions { isIgnoreTrailingSlash = true }

    serverOptions {
        port = System.getenv("PORT")?.toInt() ?: environment.config.getInt("server.port")
    }

    onStarting {
        log.info("starting app")
        app = application(environment, serverOptions, contextPath)
        App.config = config
    }

    onStarted {
        log.info("app started. ${app.baseUrl}")
        log.info("environment: ${app.environment.activeNames}")
    }

    onStop {
        log.info("stopping app")
    }

    assets("/assets/*", config.getString("assets.path"))

    install(RockerModule())
    mvc(AppController())
}) {
    companion object {
        lateinit var app: Application
        lateinit var config: Config

        fun application(env: Environment
                    , serverOptions: ServerOptions?
                    , contextPath: String): Application {
            val scheme = "http"
            val port = serverOptions?.port ?: env.config.getInt("server.port")

            val host = "localhost"
            val baseUrl = "$scheme://$host:$port$contextPath"

            return Application(env, scheme, host, port, contextPath, baseUrl)
        }

        fun exists(path: String, config: Config = App.config): Boolean {
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
                       , val baseUrl: String) {

        val conf: Config = environment.config
        val assets: List<Asset> =
                if (exists("assets.package", conf))
                    conf.getObject("assets.package")
                    .entries
                    .map {
                        @Suppress("UNCHECKED_CAST") // this should be the structure of the config
                        val assetMap = it.value.unwrapped() as Map<String, List<String>>
                        Asset(it.key
                            , assetMap.getOrDefault("css", listOf())
                            , assetMap.getOrDefault("js", listOf())
                                , conf.getConfig("assets")) }
                else listOf()

        fun getPackage(name: String): Asset? {
            return assets.findLast { it.packageName == name }
        }

        class Asset(val packageName: String, val css: List<String>, val js: List<String>, val conf: Config) {
            val path = conf.getString("path")
            val cssPackage: String = css.map {
                if (it.endsWith(".css"))
                    File("$path/$it").readText()
                else { }
            }.joinToString("\n")

            val jsPackage: String = css.map {
                File("$path/$it").readText()
            }.joinToString("\n")

            companion object {
                fun getPackageCss(conf: Config, packageName: String, names: List<String> = listOf()): String {
                    return ""
                }
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