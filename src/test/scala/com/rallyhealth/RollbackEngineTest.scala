package com.rallyhealth


import com.rallyhealth.vocab.{Fact, Rollback, Event, Nullify}
import com.rallyhealth.vocab.incentives._
import org.joda.time.DateTime
import org.junit.{Test, Assert}
import com.rallyhealth.implicits.FactImplicits._
import DateTimeImplicits._
import Fixtures._

class RollbackEngineTest {

  @Test def rollbackBodyFatReading(): Unit = {

    val now = DateTime.now

    val bodyFat = BodyFatReading(Employee("DONALD.FAGEN"), 28, now, now)

    val fitness =
      Completed(
        Employee("DONALD.FAGEN"),
        BasicActivity("COMPANY.FITNESS.CHALLENGE"), now.plusDays(5), now.plusDays(5)
      )

    val events =
      bodyFat ++ fitness ++ Nullify(bodyFat, now.plusDays(10), now.plusDays(10))

    println("---- Processing the following events through the Rollback Engine ----")
    events.filterByType[Event].toSeq.sortBy(_.timeOfReceipt).foreach(println)
    println()

    val rollbackInstance =
      new RollbackEngine(wellnessPlan ++ events, IncentivesKnowledgeBase.knowledgeBase)

    println("---- Rollback facts ----")
    rollbackInstance.inferencesToReverse.foreach(println)


    val underlying =
      rollbackInstance.inferencesToReverse.toSet[Fact].filterByType[Rollback].map(_.assertion)

    Assert.assertTrue(
      underlying.contains(
        Eligible(Employee("DONALD.FAGEN"),Reward(100,"dollars"))
      )
    )

    Assert.assertTrue(
      underlying.contains(
        Failed(Employee("DONALD.FAGEN"),BasicActivity("BODY.FAT.TARGET"), now, now)
      )
    )

    Assert.assertTrue(
      underlying.contains(
        Unlocked(BasicActivity("COMPANY.FITNESS.CHALLENGE"))
      )
    )

  }

}
