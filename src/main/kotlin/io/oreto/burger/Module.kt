package io.oreto.burger

import io.jooby.Context

class Module {

    companion object {
        private const val AJAX_HEADER = "X-Requested-With"

        fun ajax(context: Context): Boolean {
            return context.headerMap().containsKey(AJAX_HEADER) && context.header(AJAX_HEADER).value() == "XMLHttpRequest"
        }
    }
}