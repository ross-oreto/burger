package io.oreto.burger

import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import io.jooby.Environment
import io.jooby.Kooby
import io.jooby.ServerOptions
import io.jooby.rocker.RockerModule
import io.jooby.runApp
import java.nio.file.Paths

class App :Kooby({

    val config: Config = environment.config

    routerOptions { isIgnoreTrailingSlash = true }

    serverOptions {
        port = config.getInt("server.port")
    }

    onStarting {
        log.info("starting app")
        info = appInfo(environment, serverOptions, contextPath)
    }

    onStarted {
        log.info("app started. ${info.baseUrl}")
        log.info("environment: ${info.environment.activeNames}")
    }

    onStop {
        log.info("stopping app")
    }

    assets("/assets/*", Paths.get("src/main/resources/public"))

    install(RockerModule())
    mvc(AppController())

}) {
    companion object {
        lateinit var info: AppInfo

        fun appInfo(env: Environment
                    , serverOptions: ServerOptions?
                    , contextPath: String): AppInfo {
            val scheme = "http"
            val port = serverOptions?.port ?: env.config.getInt("server.port")

            val host = "localhost"
            val baseUrl = "$scheme://$host:$port$contextPath"

            return AppInfo(env, scheme, host, port, contextPath, baseUrl)
        }

        fun exists(path: String, config: Config = App.info.environment.config): Boolean {
            return try {
                config.hasPath(path)
            } catch (e: ConfigException.BadPath) {
                false
            }
        }

        fun prop(path: String): String? {
            return if (exists(path)) info.environment.config.getString(path) else null
        }

        fun propInt(path: String): Int? {
            return if (exists(path)) info.environment.config.getInt(path) else null
        }
    }

    data class AppInfo(val environment: Environment
                       , val scheme: String
                       , val host: String
                       , val port: Int
                       , val path: String
                       , val baseUrl: String)

    enum class ENV {
        local
        , dev
        , uat
        , prod
        , test
        , other
    }

    fun isLocal(): Boolean {
        return App.info.environment.isActive(ENV.local.name)
    }

    fun isDev(): Boolean {
        return App.info.environment.isActive(ENV.dev.name)
    }

    fun isUat(): Boolean {
        return App.info.environment.isActive(ENV.uat.name)
    }

    fun isProd(): Boolean {
        return App.info.environment.isActive(ENV.prod.name)
    }

    fun isTest(): Boolean {
        return App.info.environment.isActive(ENV.test.name)
    }
}

fun main(args: Array<String>) {
    runApp(args, App::class)
}