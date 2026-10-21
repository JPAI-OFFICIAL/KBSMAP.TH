package com.example

import com.example.data.model.LocationPoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testLocationPointDmsAndMapsUrl() {
        val point = LocationPoint(
            latitude = 13.7563,
            longitude = 100.5018,
            altitude = 15.0,
            speed = 10.0f
        )

        val dms = point.toDmsString()
        assertTrue(dms.contains("N"))
        assertTrue(dms.contains("E"))

        val url = point.getGoogleMapsUrl()
        assertEquals("https://www.google.com/maps/search/?api=1&query=13.7563,100.5018", url)

        val speedKmh = point.getSpeedKmh()
        assertEquals(36.0f, speedKmh, 0.01f)
    }
}
