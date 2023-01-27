/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.map
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.*
import me.gustavolopezxyz.common.db.CategoryRepository
import org.koin.java.KoinJavaComponent.inject

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun CategoriesScreen() {
    val categoryRepository by inject<CategoryRepository>(CategoryRepository::class.java)
    val categories by categoryRepository.allAsFlow().mapToList().map { list ->
        list.map { it.toDto() }
    }.collectAsState(emptyList(), Dispatchers.IO)
    val snackbar by inject<SnackbarHostState>(SnackbarHostState::class.java)

    var isCreatingOpen by remember { mutableStateOf(false) }

    var editing by remember { mutableStateOf<CategoryWithParent?>(null) }

    fun createCategory(name: String, parentCategoryId: Long?) {
        isCreatingOpen = false

        GlobalScope.launch(Dispatchers.IO) {
            categoryRepository.create(name, parentCategoryId)

            snackbar.showSnackbar("The category was created")
        }
    }

    fun editCategory() {
        val modified = editing!!.toCategory()
        editing = null

        GlobalScope.launch(Dispatchers.IO) {
            categoryRepository.update(
                categories.find { it.categoryId == modified.categoryId }?.toCategory()!!, modified
            )

            snackbar.showSnackbar("The category was modified")
        }
    }

    if (isCreatingOpen) {
        Dialog(onCloseRequest = { isCreatingOpen = false }, title = "Create a Category") {
            Card(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxWidth().padding(Constants.Size.Medium.dp)) {
                    CreateCategoryForm(
                        categories = categories.filter { it.parentCategoryId == null },
                        onCreate = ::createCategory,
                        onCancel = { isCreatingOpen = false }
                    )
                }
            }
        }
    }

    if (editing != null) {
        Dialog(onCloseRequest = { editing = null }, title = "Edit an Account") {
            Card(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxWidth().padding(Constants.Size.Medium.dp)) {
                    EditCategoryForm(
                        categories = categories.filter { it.parentCategoryId == null },
                        value = categories.find { it.categoryId == editing?.categoryId } ?: MissingCategory.toDto(),
                        onValueChange = { editing = it },
                        onEdit = ::editCategory,
                        onCancel = { editing = null })
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(Constants.Size.Large.dp),
        verticalArrangement = Arrangement.spacedBy(Constants.Size.Large.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            ScreenTitle("Categories")

            Button(onClick = { isCreatingOpen = !isCreatingOpen }) {
                Text("Create category")
            }
        }

        Spacer(modifier = Modifier.fillMaxWidth())

        CategoriesList(categories) { editing = it.copy() }
    }
}