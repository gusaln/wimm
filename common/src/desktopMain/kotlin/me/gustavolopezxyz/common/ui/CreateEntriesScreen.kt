/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.datetime.atTime
import me.gustavolopezxyz.common.data.getCurrency
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.db.EntryRepository
import me.gustavolopezxyz.common.db.RecordRepository
import me.gustavolopezxyz.common.ext.toMoney
import me.gustavolopezxyz.db.Database
import org.koin.java.KoinJavaComponent.inject

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun CreateEntriesScreen(navController: NavController) {
    val db by remember { inject<Database>(Database::class.java) }
    val accountRepository by remember { inject<AccountRepository>(AccountRepository::class.java) }
    val recordRepository by remember { inject<RecordRepository>(RecordRepository::class.java) }
    val entriesRepository by remember { inject<EntryRepository>(EntryRepository::class.java) }
    val snackbar by remember { inject<SnackbarHostState>(SnackbarHostState::class.java) }

    val accounts by accountRepository.allAsFlow().mapToList().collectAsState(listOf())

    RegularLayout(menu = { Text("Emtpy real state") }) {
        RecordTransactionForm(accounts) { newTransactionDto ->
            db.transaction {
                val reference = recordRepository.create(newTransactionDto.description)
                val recordId = recordRepository.findByReference(reference)!!.id

                newTransactionDto.entries.forEach {
                    entriesRepository.create(
                        it.description,
                        it.amount.toMoney(it.account!!.getCurrency()),
                        it.account.id,
                        recordId,
                        it.incurred_at.atTime(0, 0),
                        it.recorded_at.atTime(0, 0)
                    )
                }

                GlobalScope.launch {
                    snackbar.showSnackbar("Transaction recorded")
                }

                navController.navigate(Screen.Dashboard.name)
            }
        }
    }
}