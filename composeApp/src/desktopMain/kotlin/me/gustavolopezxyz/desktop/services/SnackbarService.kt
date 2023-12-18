/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.services

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult

class SnackbarService(snackbarHostState: SnackbarHostState? = null) {
    var snackbar: SnackbarHostState? = snackbarHostState

    suspend fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration =
            if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite
    ): SnackbarResult? {
        return snackbar?.showSnackbar(
            message,
            actionLabel,
            withDismissAction,
            duration
        )
    }
}