/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.gustavolopezxyz.common.data.Category
import me.gustavolopezxyz.common.data.CategoryWithParent
import me.gustavolopezxyz.common.data.MissingCategory
import me.gustavolopezxyz.common.data.toDto
import me.gustavolopezxyz.common.db.CategoryRepository
import me.gustavolopezxyz.common.navigation.NavController
import me.gustavolopezxyz.common.navigation.Screen
import me.gustavolopezxyz.common.ui.CategoriesGroupedList
import me.gustavolopezxyz.common.ui.CreateCategoryForm
import me.gustavolopezxyz.common.ui.EditCategoryForm
import me.gustavolopezxyz.common.ui.common.AppButton
import me.gustavolopezxyz.common.ui.common.ScreenTitle
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun ManageCategoriesScreen(viewModel: ManageCategoriesViewModel) {
    val scope = rememberCoroutineScope()
    val categories by viewModel.getCategories().map { list -> list.map { it.toDto() } }
        .collectAsState(emptyList(), scope.coroutineContext)
    var isCreatingOpen by remember { mutableStateOf(false) }
    var categoryBeingEdited by remember { mutableStateOf<CategoryWithParent?>(null) }

    fun createCategory(name: String, parentCategoryId: Long?) {
        isCreatingOpen = false

        scope.launch(Dispatchers.IO) {
            viewModel.createCategory(name, parentCategoryId)
        }
    }

    fun editCategory() {
        val modified = categoryBeingEdited!!.copy()
        categoryBeingEdited = null

        scope.launch(Dispatchers.IO) {
            viewModel.editCategory(modified.toCategory())
        }
    }

    if (isCreatingOpen) {
        Dialog(onCloseRequest = { isCreatingOpen = false }, title = "Create an Category") {
            Card(modifier = Modifier.fillMaxSize(), shape = RoundedCornerShape(0)) {
                Box(modifier = Modifier.fillMaxWidth().padding(AppDimensions.Default.padding.medium)) {
                    CreateCategoryForm(categories = categories, ::createCategory, onCancel = { isCreatingOpen = false })
                }
            }
        }
    }

    if (categoryBeingEdited != null) {
        Dialog(onCloseRequest = { categoryBeingEdited = null }, title = "Edit a Category") {
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
            onSelect = { viewModel.navigateToCategoriesSummary(it) },
            onEdit = { categoryBeingEdited = it.copy() }
        )
    }
}

class ManageCategoriesViewModel(private val navController: NavController) : KoinComponent {
    private val snackbar: SnackbarHostState by inject()
    private val categoryRepository: CategoryRepository by inject()

    fun getCategories() = categoryRepository.allAsFlow().mapToList(Dispatchers.IO)

    suspend fun createCategory(name: String, parentCategoryId: Long?) {
        categoryRepository.create(name, parentCategoryId)

        snackbar.showSnackbar("The category was created")
    }

    suspend fun editCategory(modified: Category) {
        categoryRepository.update(modified)

        snackbar.showSnackbar("The category was modified")
    }

    fun navigateToCategoriesSummary(category: CategoryWithParent) {
        navController.navigate(Screen.CategoryMonthlySummary.route(category.categoryId))
    }
}