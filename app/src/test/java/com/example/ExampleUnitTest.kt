package com.example

import com.example.data.engine.TrainingPriorityEngine
import com.example.data.local.entity.BaselineProfileEntity
import com.example.data.model.SkillCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  private val engine = TrainingPriorityEngine()

  @Test
  fun `test exact 5-factor weighted scoring formula`() {
    // weakness (30%), importance (25%), errors (20%), time (15%), performance (10%)
    val expectedScore = (100.0 * 0.30) + (100.0 * 0.25) + (100.0 * 0.20) + (100.0 * 0.15) + (100.0 * 0.10)
    assertEquals(100.0, expectedScore, 0.001)

    val actualScore = engine.calculateLearningValue(
      weaknessSeverity = 100.0,
      importanceForC1 = 100.0,
      recurringErrors = 100.0,
      timeSinceLastTraining = 100.0,
      recentPerformance = 100.0
    )
    assertEquals(100.0, actualScore, 0.001)
  }

  @Test
  fun `test individual factor contributions adhere strictly to specified weights`() {
    // Test 30% Weakness
    val weaknessOnly = engine.calculateLearningValue(100.0, 0.0, 0.0, 0.0, 0.0)
    assertEquals(30.0, weaknessOnly, 0.001)

    // Test 25% Importance for C1
    val importanceOnly = engine.calculateLearningValue(0.0, 100.0, 0.0, 0.0, 0.0)
    assertEquals(25.0, importanceOnly, 0.001)

    // Test 20% Recurring Errors
    val errorsOnly = engine.calculateLearningValue(0.0, 0.0, 100.0, 0.0, 0.0)
    assertEquals(20.0, errorsOnly, 0.001)

    // Test 15% Time Since Last Training
    val timeOnly = engine.calculateLearningValue(0.0, 0.0, 0.0, 100.0, 0.0)
    assertEquals(15.0, timeOnly, 0.001)

    // Test 10% Recent Performance
    val performanceOnly = engine.calculateLearningValue(0.0, 0.0, 0.0, 0.0, 100.0)
    assertEquals(10.0, performanceOnly, 0.001)
  }

  @Test
  fun `test highest value activity selection based on multi-factor scoring`() {
    val activityA = TrainingPriorityEngine.TrainingActivity(
      id = "reading_comp",
      title = "Leseverstehen",
      category = SkillCategory.LESEVERSTEHEN,
      weaknessSeverity = 20.0,
      importanceForC1 = 70.0,
      recurringErrorsScore = 10.0,
      timeSinceLastTraining = 20.0,
      recentPerformanceNeed = 10.0
    )

    val activityB = TrainingPriorityEngine.TrainingActivity(
      id = "spontaneous_interaction",
      title = "Interaktions-Intelligenz",
      category = SkillCategory.INTERAKTION,
      weaknessSeverity = 70.0, // high weakness (30% weight)
      importanceForC1 = 95.0,  // critical C1 importance (25% weight)
      recurringErrorsScore = 80.0, // unresolved errors (20% weight)
      timeSinceLastTraining = 60.0, // time elapsed (15% weight)
      recentPerformanceNeed = 50.0  // recent struggle (10% weight)
    )

    val best = engine.selectHighestValueActivity(listOf(activityA, activityB))

    assertEquals("spontaneous_interaction", best.activity.id)
    assertEquals(1, best.rank)
    assertTrue("Priority score should reflect top learning value", best.normalizedPriorityPercent >= 70)
  }

  @Test
  fun `test baseline profile entity captures all 8 core skills`() {
    val profile = BaselineProfileEntity(
      id = "primary_user_profile",
      listeningScore = 65,
      speakingScore = 55,
      interactionScore = 60,
      readingScore = 80,
      writingScore = 62,
      grammarScore = 70,
      vocabularyScore = 68,
      mediationScore = 58,
      overallC1Score = 65,
      functionalReadinessPct = 60,
      primaryLimitingFactor = "Spontanes Sprechen & Reaktionslatenz",
      recommendedStartingEngine = "Sprech- & Interaktions-Simulator",
      isCompleted = true
    )

    assertEquals(65, profile.listeningScore)
    assertEquals(55, profile.speakingScore)
    assertEquals(60, profile.interactionScore)
    assertEquals(80, profile.readingScore)
    assertEquals(62, profile.writingScore)
    assertEquals(70, profile.grammarScore)
    assertEquals(68, profile.vocabularyScore)
    assertEquals(58, profile.mediationScore)
    assertTrue(profile.isCompleted)
  }
}
