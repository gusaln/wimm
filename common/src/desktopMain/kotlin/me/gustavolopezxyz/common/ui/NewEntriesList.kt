package me.gustavolopezxyz.common.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.Currency
import me.gustavolopezxyz.common.data.MissingAccount
import me.gustavolopezxyz.common.data.getCurrency
import me.gustavolopezxyz.db.Account


object FormEntriesListDefault {
    val rowPadding = PaddingValues(12.dp, 8.dp)
    val rowCellPadding = PaddingValues(4.dp, 0.dp)

    const val actionsWeight = 2f
    const val contentWeight = 8f
    const val amountWeight = 2f
}

@Composable
fun TotalListItem(total: String = "Total", totalsByCurrency: Map<Currency, Double>) {
    totalsByCurrency.forEach {
        Row(modifier = Modifier.fillMaxWidth().padding(FormEntriesListDefault.rowPadding)) {
            Spacer(Modifier.weight(FormEntriesListDefault.actionsWeight))

            Text(
                total,
                modifier = Modifier.weight(FormEntriesListDefault.contentWeight)
                    .padding(FormEntriesListDefault.rowCellPadding),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.body1
            )

            MoneyText(
                amount = it.value,
                currency = it.key,
                modifier = Modifier.weight(FormEntriesListDefault.amountWeight)
                    .padding(FormEntriesListDefault.rowCellPadding),
                commonStyle = TextStyle.Default.copy(textAlign = TextAlign.End),
                valueStyle = MaterialTheme.typography.body1
            )
        }
    }
}

@Composable
fun NewEntriesListItem(entry: NewEntryDto, onEdit: (NewEntryDto) -> Unit, onDelete: (NewEntryDto) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(FormEntriesListDefault.rowPadding)
    ) {
        Row(
            modifier = Modifier.weight(FormEntriesListDefault.actionsWeight),
            horizontalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            IconButton(onClick = { onEdit(entry) }) {
                Icon(Icons.Default.Edit, "edit new entry")
            }

            IconButton(onClick = { onDelete(entry) }) {
                Icon(Icons.Default.Delete, "delete new entry")
            }
        }

        EntrySummaryText(
            entry.description,
            (entry.account ?: MissingAccount).name,
            modifier = Modifier.weight(FormEntriesListDefault.contentWeight)
                .padding(FormEntriesListDefault.rowCellPadding)
        )

        MoneyText(
            entry.amount,
            (entry.account ?: MissingAccount).getCurrency(),
            modifier = Modifier.weight(FormEntriesListDefault.amountWeight)
                .padding(FormEntriesListDefault.rowCellPadding),
            commonStyle = TextStyle.Default.copy(textAlign = TextAlign.End)
        )
    }
}


@Composable
fun NewEntriesList(
    entries: List<NewEntryDto>,
    onEdit: (NewEntryDto) -> Unit,
    onDelete: (NewEntryDto) -> Unit,
    name: @Composable() (() -> Unit)? = null,
    totals: @Composable() (() -> Unit) = {},
) {
    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Constants.Size.Small.dp)
    ) {
        name?.invoke() ?: Text("Current entries", style = MaterialTheme.typography.h5)

        if (entries.isEmpty()) {
            Text("No new entries yet")
        }

        Column {
            entries.forEach {
                NewEntriesListItem(entry = it, onEdit = onEdit, onDelete = onDelete)
                Divider(Modifier.fillMaxWidth())

            }
        }

        totals()
    }
}

@Preview
@Composable
fun NewEntriesListPreview() {
    val ac1 = Account(99, "Income", "USD", 0.0, 0.0)
    val ac2 = Account(2, "Expenses", "USD", 0.0, 0.0)

    NewEntriesList(entries = listOf(
        NewEntryDto("", "Cash", ac1, 100.0, LocalDate(2023, 1, 13)),
        NewEntryDto("", "Beer", ac2, -10.0, LocalDate(2023, 1, 14)),
    ), onEdit = {}, onDelete = {})
}