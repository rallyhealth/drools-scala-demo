package com.rallyhealth

import org.drools.KnowledgeBaseFactory


object IncentivesKnowledgeBase {
  lazy val knowledgeBase = {
    val kb = KnowledgeBaseFactory.newKnowledgeBase()
    kb.addKnowledgePackages(IncentivesRuleBuilder.builder.getKnowledgePackages)
    kb
  }
}