package com.example.notes.tools.detekt

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import kotlin.test.Test
import kotlin.test.assertEquals

class ComposeFunctionNamingRuleTest {
    private val rule = ComposeFunctionNamingRule(Config.empty)

    @Test
    fun `reports lower camel case composable function names`() {
        val code =
            """
            import androidx.compose.runtime.Composable

            @Composable
            fun notesAppRoot() = Unit
            """.trimIndent()

        val findings = rule.lint(code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `does not report pascal case composable function names`() {
        val code =
            """
            import androidx.compose.runtime.Composable

            @Composable
            fun NotesAppRoot() = Unit
            """.trimIndent()

        val findings = rule.lint(code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `does not report non composable functions`() {
        val code =
            """
            fun notesAppRoot() = Unit
            """.trimIndent()

        val findings = rule.lint(code)

        assertEquals(0, findings.size)
    }
}
