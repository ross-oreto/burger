package io.oreto.burger

interface Grouped { val name: String }
interface Named {
    val name: String
    val defaultValue: Any?
}