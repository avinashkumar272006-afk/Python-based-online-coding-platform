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

    @Test
    public void testProgressPercentageZeroApprovedWithSubmissions() {
        StudentProgress progress = new StudentProgress();
        progress.setTotalExercises(0);
        progress.setSubmittedExercises(5);
        assertEquals(0, progress.getProgressPercentage());
    }

    @Test
    public void testProgressPercentageCannotExceed100Percent() {
        StudentProgress progress = new StudentProgress();
        progress.setTotalExercises(5);
        progress.setSubmittedExercises(8);
        assertEquals(100, progress.getProgressPercentage());
    }

    @Test
    public void testProgressPercentageNegativeValuesHandledSafely() {
        StudentProgress progress = new StudentProgress();
        progress.setTotalExercises(-5);
        progress.setSubmittedExercises(2);
        assertEquals(0, progress.getProgressPercentage());

        progress.setTotalExercises(10);
        progress.setSubmittedExercises(-1);
        assertEquals(0, progress.getProgressPercentage());
    }

    @Test
    public void testProgressPercentageComplete() {
        StudentProgress progress = new StudentProgress();
        progress.setTotalExercises(10);
        progress.setSubmittedExercises(10);
        assertEquals(100, progress.getProgressPercentage());
    }
}
