/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.gustavolopezxyz.common.data.CategoryWithParent
import me.gustavolopezxyz.common.data.MissingCategory
import me.gustavolopezxyz.common.data.toDto
import me.gustavolopezxyz.common.db.CategoryRepository
import me.gustavolopezxyz.common.ui.CategoriesList
import me.gustavolopezxyz.common.ui.CreateCategoryForm
import me.gustavolopezxyz.common.ui.EditCategoryForm
import me.gustavolopezxyz.common.ui.common.AppButton
import me.gustavolopezxyz.common.ui.common.AppTextButton
import me.gustavolopezxyz.common.ui.common.FormTitle
import me.gustavolopezxyz.common.ui.common.ScreenTitle
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import org.koin.java.KoinJavaComponent.inject

@Composable
fun ManageCategoriesScreen() {
    val categoryRepository by inject<CategoryRepository>(CategoryRepository::class.java)
    val categories by categoryRepository.allAsFlow().mapToList().map { list ->
        list.map { it.toDto() }
    }.collectAsState(emptyList(), Dispatchers.IO)
    val snackbar by inject<SnackbarHostState>(SnackbarHostState::class.java)

    val scope = rememberCoroutineScope()

    var isCreatingOpen by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<CategoryWithParent?>(null) }
    var deleting by remember { mutableStateOf<CategoryWithParent?>(null) }

    fun createCategory(name: String, parentCategoryId: Long?) {
        isCreatingOpen = false

        scope.launch(Dispatchers.IO) {
            categoryRepository.create(name, parentCategoryId)

            snackbar.showSnackbar("The category was created")
        }
    }

    fun editCategory() {
        val modified = editing!!.toCategory()
        editing = null

        scope.launch(Dispatchers.IO) {
            categoryRepository.update(
                categories.find { it.categoryId == modified.categoryId }?.toCategory()!!, modified
            )

            snackbar.showSnackbar("The category was modified")
        }
    }

    fun deleteCategory() {
        val category = deleting!!.toCategory()
        deleting = null

        if (categoryRepository.countTransactions(category.categoryId) > 0) {
            scope.launch {
                snackbar.showSnackbar("The category ${category.name} has transactions and can't be deleted")
            }

            return
        }

        if (categoryRepository.countChildren(category.categoryId) > 0) {
            scope.launch {
                snackbar.showSnackbar("The category ${category.name} has children and can't be deleted")
            }

            return
        }

        scope.launch(Dispatchers.IO) {
            categoryRepository.delete(category.categoryId)

            snackbar.showSnackbar("The category ${category.name} was deleted")
        }
    }

    if (isCreatingOpen) {
        Dialog(onCloseRequest = { isCreatingOpen = false }, title = "Create a Category") {
            Card(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxWidth().padding(AppDimensions.Default.padding.medium)) {
                    CreateCategoryForm(categories = categories.filter { it.parentCategoryId == null },
                        onCreate = ::createCategory,
                        onCancel = { isCreatingOpen = false })
                }
            }
        }
    }

    if (editing != null) {
        Dialog(onCloseRequest = { editing = null }, title = "Edit category ${editing?.name}") {
            Card(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxWidth().padding(AppDimensions.Default.padding.medium)) {
                    EditCategoryForm(categories = categories.filter { it.parentCategoryId == null },
                        value = categories.find { it.categoryId == editing?.categoryId } ?: MissingCategory.toDto(),
                        onValueChange = { editing = it },
                        onEdit = ::editCategory,
                        onCancel = { editing = null })
                }
            }
        }
    }

    if (deleting != null) {
        Dialog(onCloseRequest = { editing = null }, title = "Delete category ${deleting?.fullname()}") {
            Card(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxWidth().padding(AppDimensions.Default.padding.medium)) {
                    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium)) {
                        FormTitle("Delete category ${deleting?.fullname()}")

                        Spacer(modifier = Modifier.fillMaxWidth())

                        Text("Do you really wish to delete the category ${deleting?.fullname()}?")

                        Spacer(modifier = Modifier.fillMaxWidth())

                        Spacer(modifier = Modifier.fillMaxWidth())

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(
                                AppDimensions.Default.spacing.medium,
                                Alignment.End
                            )
                        ) {
                            AppButton(
                                onClick = ::deleteCategory,
                                colors = ButtonDefaults.buttonColors(MaterialTheme.colors.error),
                                text = "Delete"
                            )

                            AppTextButton(onClick = { deleting = null }, "Cancel")
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(AppDimensions.Default.padding.large),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.large)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            ScreenTitle {
                Text("Categories")
            }

            AppButton(onClick = { isCreatingOpen = !isCreatingOpen }, "Create category")
        }

        Spacer(modifier = Modifier.fillMaxWidth())

        CategoriesList(categories = categories, onSelect = { editing = it.copy() }, onDelete = { deleting = it.copy() })
    }
}