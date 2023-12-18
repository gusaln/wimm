/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.di

import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.db.CategoryRepository
import me.gustavolopezxyz.common.db.EntryRepository
import me.gustavolopezxyz.common.db.TransactionRepository
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance

fun initDependencyInjection(appDeclarations: DI.MainBuilder.() -> Unit): DI {
    return DI {
        appDeclarations()

        import(baseModule())
        import(platformModule())
    }
}

fun baseModule(): DI.Module {
    return DI.Module(name = "BASE") {
        bindProvider { AccountRepository(instance()) }
        bindProvider { CategoryRepository(instance()) }
        bindProvider { TransactionRepository(instance()) }
        bindProvider { EntryRepository(instance()) }
    }
}
