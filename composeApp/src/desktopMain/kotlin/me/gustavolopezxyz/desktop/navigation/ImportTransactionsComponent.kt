/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.navigation

import com.arkivanov.decompose.value.MutableValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.datetime.LocalDate
import me.gustavolopezxyz.common.ext.datetime.nowLocalDateTime
import me.gustavolopezxyz.common.worldparsers.ExcelParser
import me.gustavolopezxyz.common.worldparsers.ExternalTransaction
//import net.sandius.rembulan.Table
//import net.sandius.rembulan.Variable
//import net.sandius.rembulan.compiler.CompilerChunkLoader
//import net.sandius.rembulan.env.RuntimeEnvironments
//import net.sandius.rembulan.exec.DirectCallExecutor
//import net.sandius.rembulan.impl.StateContexts
//import net.sandius.rembulan.lib.StandardLibrary
//import net.sandius.rembulan.load.ChunkLoader
import org.dhatim.fastexcel.reader.ReadableWorkbook
import org.dhatim.fastexcel.reader.Sheet
import org.kodein.di.DI
import org.kodein.di.DIAware
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Files


class ImportTransactionsComponent(
    override val di: DI,
    val onImportTransaction: () -> Unit,
    val onSyncTransactions: () -> Unit,
    val onCancel: (() -> Unit)? = null,
) : DIAware {

    var filePath = MutableValue("")
    var bankTransactions = MutableValue(emptyList<ExternalTransaction>())
    var fileExists = MutableValue(false)

    fun setFilePath(p: String) {
        this.filePath.value = p

        this.fileExists.value = filePath.value.isNotBlank() && File(filePath.value).exists()
    }

    fun readFile() {
        val reader = ExcelParser(
            fileLocation = filePath.value, firstContentRow = 10
        )

        val newBankTransactions = mutableListOf<ExternalTransaction>()

        reader.parseIgnoringNulls { row ->
            val date = row.getCellRawValue(1).let {
                if (it.isPresent) {
                    val parts = it.get().split('/')

                    LocalDate(
                        year = parts[2].take(4).toInt(),
                        monthNumber = parts[1].take(2).toInt(),
                        dayOfMonth = parts[0].take(2).toInt()
                    )
                } else {
                    nowLocalDateTime().date

                }

            }

            val reference = row.getCellRawValue(2).let {
                if (it.isPresent) it.get().trimStart('0') else ""
            }

            val description = row.getCellRawValue(3).let {
                if (it.isPresent) it.get().trim() else ""
            }

            val amount = row.getCellRawValue(4).let {
                if (it.isPresent) it.get().toDouble() else 0.0
            }

            newBankTransactions.add(ExternalTransaction(description, date, reference, amount))
        }

        this.bankTransactions.value = newBankTransactions
    }
}

class LoaderModule {
    val parser: InputFileParse = InputFileParse()
    val transformer: Transform = Transform()
}


class InputFileParse(val offsetRow: Int = 0) {
    fun parse(path: String): Flow<List<String>> {
        return flow {
            FileInputStream(path).use { inputStream ->
                ReadableWorkbook(inputStream).use { wb ->
                    val sheet: Sheet = wb.firstSheet

                    for (row in sheet.openStream().skip(offsetRow.toLong())) {
                        emit(row.map { it.asString() })
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }
}

class Transform {
    fun dummy() {
        val program = "print('hello world!')"

        val input = InputStream.nullInputStream()
        val output = Files.createTempFile("wimm-out", null)
        val err = Files.createTempFile("wimm-err", null)

// initialise state
//        val state = StateContexts.newDefaultInstance()
//        val env: Table = StandardLibrary.`in`(RuntimeEnvironments.system(input, output.toFile().outputStream(), err.toFile().outputStream())).installInto(state)

// compile
//        val loader: ChunkLoader = CompilerChunkLoader.of("hello_world")
//        val main = loader.loadTextChunk(Variable(env), "hello", program)


// execute
//        DirectCallExecutor.newExecutor().call(state, main)
    }
}