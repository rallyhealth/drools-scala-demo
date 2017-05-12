package com.rallyhealth

import com.rallyhealth.vocab.incentives._
import org.joda.time.DateTime
import org.junit.{Test, Assert}
import com.rallyhealth.implicits.FactImplicits._
import Fixtures._

class IncentivesEngineTest {

  @Test def employeePassesBodyFat(): Unit = {

    val now = DateTime.now

    println("---- Background facts for employee incentives plan ----")
    wellnessPlan.alphabetize.foreach(println)
    println()

    val reading = BodyFatReading(Employee("DONALD.FAGEN"), 25, now, now)
    println("---- Body fat percentage reading event ----")
    println(reading)
    println()

    val inferences = {

      val incentivesEngineOutput =
        (wellnessPlan ++ reading).eventBasedInferences(IncentivesKnowledgeBase.knowledgeBase)
      (incentivesEngineOutput -- wellnessPlan) -- Set(reading)
    }

    println("---- Inferences after processing a body fat percentage of 25 ----")
    inferences.alphabetize.foreach(println)

    Assert.assertEquals(
      Eligible(Employee("DONALD.FAGEN"),Reward(100,"dollars")),
      inferences.filterByType[Eligible].head
    )

  }

  @Test def employeeFailsBodyFat(): Unit = {

    val now = DateTime.now

    println("---- Background facts for employee incentives plan ----")
    wellnessPlan.alphabetize.foreach(println)
    println()

    val reading = BodyFatReading(Employee("DONALD.FAGEN"), 28, now, now)
    println("---- Body fat percentage reading event ----")
    println(reading)
    println()

    val inferences = {

      val incentivesEngineOutput =
        (wellnessPlan ++ reading).eventBasedInferences(IncentivesKnowledgeBase.knowledgeBase)
      (incentivesEngineOutput -- wellnessPlan) -- Set(reading)
    }

    println("---- Inferences after processing a body fat percentage of 28 ----")
    inferences.alphabetize.foreach(println)

    Assert.assertEquals(
      Failed(Employee("DONALD.FAGEN"),BasicActivity("BODY.FAT.TARGET"), now, now),
      inferences.filterByType[Failed].head
    )

  }

  @Test def employeeFailsBodyFatCompletesAlternative: Unit = {

    val now = DateTime.now

    println("---- Background facts for employee incentives plan ----")
    wellnessPlan.alphabetize.foreach(println)
    println()

    val reading = BodyFatReading(Employee("DONALD.FAGEN"), 28, now, now)
    val completesFitnessChallenge =
      Completed(Employee("DONALD.FAGEN"), BasicActivity("COMPANY.FITNESS.CHALLENGE"), now.plusDays(5), now.plusDays(5))
    println("---- Events ----")
    println(reading)
    println(completesFitnessChallenge)
    println()

    val inferences = {

      val incentivesEngineOutput =
        (wellnessPlan ++
          reading ++
          completesFitnessChallenge).eventBasedInferences(IncentivesKnowledgeBase.knowledgeBase)
      (incentivesEngineOutput -- wellnessPlan) -- Set(reading, completesFitnessChallenge)
    }

    println("---- Inferences after processing events ----")
    inferences.alphabetize.foreach(println)

    Assert.assertTrue(
      inferences.contains(
        Failed(Employee("DONALD.FAGEN"),BasicActivity("BODY.FAT.TARGET"), now, now)
      )
    )

    Assert.assertTrue(
      inferences.contains(
        Unlocked(
          Choice("BODY.FAT.OR.CHALLENGE",BasicActivity("BODY.FAT.TARGET"),BasicActivity("COMPANY.FITNESS.CHALLENGE"))
        )
      )
    )

    Assert.assertTrue(
      inferences.contains(
        Eligible(Employee("DONALD.FAGEN"),Reward(100,"dollars"))
      )
    )

  }

}
