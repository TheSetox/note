package com.example.notes.tools.detekt

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtNamedFunction

class ComposeFunctionNamingRule(
    config: Config,
) : Rule(config) {
    override val issue =
        Issue(
            id = "ComposeFunctionNaming",
            severity = Severity.Style,
            description = "Composable functions should use PascalCase names.",
            debt = Debt.FIVE_MINS,
        )

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        if (!function.isComposable()) {
            return
        }

        val functionName = function.name ?: return
        if (!functionName.first().isUpperCase()) {
            report(
                CodeSmell(
                    issue = issue,
                    entity = Entity.atName(function),
                    message = "Composable function `$functionName` should start with an uppercase letter.",
                ),
            )
        }
    }

    private fun KtNamedFunction.isComposable(): Boolean =
        annotationEntries.any { entry ->
            val annotationName = entry.shortName?.asString()
            val annotationType = entry.typeReference?.text
            annotationName == "Composable" || annotationType?.endsWith("Composable") == true
        }
}
