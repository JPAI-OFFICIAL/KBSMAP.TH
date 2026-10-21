package com.example.data.model

data class LocationPoint(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double = 0.0,
    val accuracy: Float = 0f,
    val speed: Float = 0f,
    val bearing: Float = 0f,
    val timestamp: Long = System.currentTimeMillis(),
    val address: String? = null
) {
    fun toDmsString(): String {
        val latDirection = if (latitude >= 0) "N" else "S"
        val lonDirection = if (longitude >= 0) "E" else "W"
        return "${formatDms(latitude)} $latDirection, ${formatDms(longitude)} $lonDirection"
    }

    private fun formatDms(deg: Double): String {
        val absDeg = Math.abs(deg)
        val d = absDeg.toInt()
        val m = ((absDeg - d) * 60).toInt()
        val s = String.format("%.2f", (absDeg - d - m / 60.0) * 3600.0)
        return "${d}°${m}'${s}\""
    }

    fun getGoogleMapsUrl(): String {
        return "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
    }

    fun getSpeedKmh(): Float = speed * 3.6f
}
