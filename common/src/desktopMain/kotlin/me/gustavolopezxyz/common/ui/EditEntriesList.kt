package me.gustavolopezxyz.common.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.ext.toCurrency


@Composable
fun EditedEntriesListItem(
    entry: EditEntryDto, onEdit: (EditEntryDto) -> Unit, onDeleteToggle: (EditEntryDto) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().background(
            when {
                entry.toDelete -> Color.Red.copy(.5f)
                entry.wasEdited -> MaterialTheme.colors.secondary
                else -> Color.Unspecified
            }
        ).padding(FormEntriesListDefault.rowPadding)
    ) {
        Row(
            modifier = Modifier.weight(FormEntriesListDefault.actionsWeight),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalAlignment = Alignment.Top
        ) {
            val iconSize = Modifier.size(20.dp)

            if (entry.toDelete) {
                IconButton(onClick = { onDeleteToggle(entry) }) {
                    Icon(Icons.Default.ArrowBack, "restore", iconSize)
                }
            } else {
                IconButton(onClick = { onEdit(entry) }) {
                    Icon(Icons.Default.Edit, "edit", iconSize)
                }
                IconButton(onClick = { onDeleteToggle(entry) }) {
                    Icon(Icons.Default.Delete, "delete", iconSize)
                }
            }
        }

        Text(
            entry.accountName,
            modifier = Modifier.weight(FormEntriesListDefault.contentWeight)
                .padding(FormEntriesListDefault.rowCellPadding),
            overflow = TextOverflow.Ellipsis
        )

        MoneyText(
            entry.amount,
            entry.currency.toCurrency(),
            modifier = Modifier.weight(FormEntriesListDefault.amountWeight)
                .padding(FormEntriesListDefault.rowCellPadding),
            commonStyle = TextStyle.Default.copy(textAlign = TextAlign.End)
        )
    }
}


@Composable
fun EditedEntriesList(
    entries: List<EditEntryDto>,
    onEdit: (EditEntryDto) -> Unit,
    onDeleteToggle: (EditEntryDto) -> Unit,
    totals: @Composable() (() -> Unit) = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Constants.Size.Small.dp)
    ) {
        Text("Current entries", style = MaterialTheme.typography.h5)

        if (entries.isEmpty()) {
            Text("No entries")
        }

        Column {
            entries.forEach {
                EditedEntriesListItem(entry = it, onEdit = onEdit, onDeleteToggle = onDeleteToggle)
                Divider(Modifier.fillMaxWidth())
            }
        }

        totals()
    }
}

@Preview
@Composable
fun EditEntriesListPreview() {
    EditedEntriesList(entries = listOf(
        EditEntryDto(1, 1, "Income", "USD", 100.0, LocalDate(2023, 1, 13), LocalDate(2023, 1, 13)),
        EditEntryDto(2, 99, "Expenses", "USD", -10.0, LocalDate(2023, 1, 14), LocalDate(2023, 1, 14)),
    ), onEdit = {}, onDeleteToggle = {})
}