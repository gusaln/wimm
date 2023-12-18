/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogWindow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.gustavolopezxyz.common.data.CategoryWithParent
import me.gustavolopezxyz.common.data.MissingCategory
import me.gustavolopezxyz.common.data.toDto
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.desktop.navigation.ManageCategoriesComponent
import me.gustavolopezxyz.desktop.ui.CategoriesGroupedList
import me.gustavolopezxyz.desktop.ui.CreateCategoryForm
import me.gustavolopezxyz.desktop.ui.EditCategoryForm
import me.gustavolopezxyz.desktop.ui.common.AppButton
import me.gustavolopezxyz.desktop.ui.common.ScreenTitle
import org.kodein.di.instance

@Composable
fun ManageCategoriesScreen(component: ManageCategoriesComponent) {
    val scope = rememberCoroutineScope()
    val categories by component.getCategories().map { list -> list.map { it.toDto() } }
        .collectAsState(emptyList(), scope.coroutineContext)
    var isCreatingOpen by remember { mutableStateOf(false) }
    var categoryBeingEdited by remember { mutableStateOf<CategoryWithParent?>(null) }
    val snackbar: SnackbarHostState by component.di.instance()

    fun createCategory(name: String, parentCategoryId: Long?) {
        isCreatingOpen = false

        component.createCategory(name, parentCategoryId)
        scope.launch(Dispatchers.IO) {
            snackbar.showSnackbar("The category was created")
        }
    }

    fun editCategory() {
        val modified = categoryBeingEdited!!.copy()
        categoryBeingEdited = null

        component.editCategory(modified.toCategory())
        scope.launch(Dispatchers.IO) {
            snackbar.showSnackbar("The category was modified")
        }
    }

    if (isCreatingOpen) {
        DialogWindow(onCloseRequest = { isCreatingOpen = false }, title = "Create an Category") {
            Card(modifier = Modifier.fillMaxSize(), shape = RoundedCornerShape(0)) {
                Box(modifier = Modifier.fillMaxWidth().padding(AppDimensions.Default.padding.medium)) {
                    CreateCategoryForm(categories = categories, ::createCategory, onCancel = { isCreatingOpen = false })
                }
            }
        }
    }

    if (categoryBeingEdited != null) {
        DialogWindow(onCloseRequest = { categoryBeingEdited = null }, title = "Edit a Category") {
            Card(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxWidth().padding(AppDimensions.Default.padding.medium)) {
                    EditCategoryForm(
                        categories = categories,
                        value = categoryBeingEdited ?: MissingCategory.toDto(),
                        onValueChange = { categoryBeingEdited = it },
                        onEdit = ::editCategory,
                        onCancel = { categoryBeingEdited = null }
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(AppDimensions.Default.padding.large),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.large)
    ) {
//        Header
        Row(modifier = Modifier.fillMaxWidth()) {
            ScreenTitle("Categories")

            Spacer(Modifier.weight(1f))

            AppButton(onClick = { isCreatingOpen = !isCreatingOpen }, "Create category")
        }

        Spacer(modifier = Modifier.fillMaxWidth())

        CategoriesGroupedList(
            categories,
            onSelect = { component.onShowCategorySummary(it.categoryId) },
            onEdit = { categoryBeingEdited = it.copy() }
        )
    }
}