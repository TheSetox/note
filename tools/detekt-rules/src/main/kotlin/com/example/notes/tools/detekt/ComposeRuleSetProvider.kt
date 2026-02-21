package com.example.notes.tools.detekt

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class ComposeRuleSetProvider : RuleSetProvider {
    override val ruleSetId = "compose-custom"

    override fun instance(config: Config): RuleSet =
        RuleSet(
            id = ruleSetId,
            rules = listOf(ComposeFunctionNamingRule(config)),
        )
}
