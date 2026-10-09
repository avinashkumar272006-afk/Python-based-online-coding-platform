package com.codingplatform.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StudentProgressTest {

    @Test
    public void testProgressPercentageCalculation() {
        StudentProgress progress = new StudentProgress();
        progress.setTotalExercises(10);
        progress.setSubmittedExercises(5);
        assertEquals(50, progress.getProgressPercentage());
    }

    @Test
    public void testProgressPercentageWithZeroExercises() {
        StudentProgress progress = new StudentProgress();
        progress.setTotalExercises(0);
        progress.setSubmittedExercises(0);
        assertEquals(0, progress.getProgressPercentage());
    }

    @Test
    public void testProgressPercentageRounding() {
        StudentProgress progress = new StudentProgress();
        progress.setTotalExercises(3);
        progress.setSubmittedExercises(1);
        assertEquals(33, progress.getProgressPercentage());
    }
}
