package com.example.notes.core.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typography tokens for notes editor and list surfaces.
 */
@Immutable
data class NotesTypography(
    val editorTitle: TextStyle,
    val editorBody: TextStyle,
    val sectionTitle: TextStyle,
    val cardTitle: TextStyle,
    val body: TextStyle,
    val bodySmall: TextStyle,
    val label: TextStyle,
    val caption: TextStyle,
) {
    companion object {
        fun default(): NotesTypography =
            NotesTypography(
                editorTitle =
                    TextStyle(
                        fontSize = 32.sp,
                        lineHeight = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                editorBody =
                    TextStyle(
                        fontSize = 18.sp,
                        lineHeight = 28.sp,
                    ),
                sectionTitle =
                    TextStyle(
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                cardTitle =
                    TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                body =
                    TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                    ),
                bodySmall =
                    TextStyle(
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                    ),
                label =
                    TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                    ),
                caption =
                    TextStyle(
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                    ),
            )
    }
}
