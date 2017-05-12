package com.rallyhealth.implicits

import com.rallyhealth.{EventDrivenRulesSession, Extractor}
import com.rallyhealth.vocab.Fact
import org.drools.KnowledgeBase

import scala.reflect.ClassTag


/**
 * Convenience object for fact and fact set manipulations
 */
object FactImplicits {

  implicit class RichFact[T <: Fact](val fact: T) extends AnyVal {

    /**
     * Converts a fact to a set of its constituent elements. The resultant set will contain the fact itself
     */
    def toSet: Set[Fact] = Extractor(fact)

    def contains(f: T ): Boolean = fact.toSet.contains(f)

    def ++ (f: Fact): Set[Fact] = fact.toSet ++ f.toSet

    def ++ (f: Set[Fact]): Set[Fact] = fact.toSet ++ f
  }

  //implicit def fact2richFact[T <: Fact](fact: T): RichFact[T] = RichFact(fact)

  implicit class RichFactSet(val factSet: Set[Fact]) extends AnyVal {

    /**
     * Each fact in this fact set is converted into a set of its constituent elements and those sets are combined
     */
    def expand: Set[Fact] = Extractor(factSet)

    def ++ (f: Fact): Set[Fact] = f ++ factSet.asInstanceOf[Set[Fact]]

    def filterByType[T <: Fact]()(implicit factCls: ClassTag[T]): Set[T] =
    //using collect here generates an incomprehensible error that yields incomprehensible results when googled
      factSet.flatMap {
        case entry if factCls.unapply(entry).isDefined => Some(entry.asInstanceOf[T])
        case _ => None
      }

    def alphabetize: Seq[Fact] = factSet.toSeq.sortBy(_.getClass.getSimpleName)

    /**
     * Applies the specified inference rules and post processing and returns the inferences as a fact set.
     */
    def eventBasedInferences(knowledgeBase: KnowledgeBase ): Set[Fact] =
      new EventDrivenRulesSession(knowledgeBase, factSet.expand).getInferences()


  }
}