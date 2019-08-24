package io.oreto.burger

import io.jooby.Context

object Module {
    private const val AJAX_HEADER = "X-Requested-With"
    fun ajax(context: Context): Boolean {
        return context.headerMap().containsKey(AJAX_HEADER) && context.header(AJAX_HEADER).value() == "XMLHttpRequest"
    }
}