package com.smarthostel;

import com.smarthostel.util.DistanceCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ============================================================================
 * UNIT TESTS FOR HAVERSINE DISTANCE CALCULATOR
 * ============================================================================
 */
public class DistanceCalculatorTest {

    @Test
    @DisplayName("Should return zero distance for identical coordinates")
    void testZeroDistanceForSameCoordinates() {
        double lat = 10.169830;
        double lon = 76.435740;
        double distance = DistanceCalculator.calculateDistance(lat, lon, lat, lon);
        assertEquals(0.0, distance, 0.01);
    }

    @Test
    @DisplayName("Should correctly calculate distance for close hostel proximity")
    void testProximityDistance() {
        // Point A: ASIET Hostel (10.169830, 76.435740)
        // Point B: Student 20 meters away (10.169950, 76.435800)
        double distance = DistanceCalculator.calculateDistance(10.169830, 76.435740, 10.169950, 76.435800);
        assertTrue(distance > 10.0 && distance < 25.0, "Expected distance around 15-20 meters, got " + distance);
    }

    @Test
    @DisplayName("Should validate within allowed radius correctly")
    void testWithinRadius() {
        assertTrue(DistanceCalculator.isWithinRadius(42.5, 100));
        assertTrue(DistanceCalculator.isWithinRadius(100.0, 100));
        assertFalse(DistanceCalculator.isWithinRadius(100.1, 100));
        assertFalse(DistanceCalculator.isWithinRadius(250.0, 100));
    }
}
