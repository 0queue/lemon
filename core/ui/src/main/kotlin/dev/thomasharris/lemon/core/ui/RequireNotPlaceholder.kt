package dev.thomasharris.lemon.core.ui

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun <T : Any> requireNotPlaceholder(value: T?): T {
    contract {
        returns() implies (value != null)
    }

    return requireNotNull(value) { "Item is a placeholder" }
}
