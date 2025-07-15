package com.example.trafficsignapp
object TrafficSignLabels {
    private val labels = listOf(
        "Speed Limit 20",
        "Speed Limit 30",
        "Speed Limit 50",
        "Speed Limit 60",
        "Speed Limit 80",
        "End of Speed Limit 80",
        "Speed Limit 100",
        "Speed Limit 120",
        "No Overtaking",
        "No Overtaking (trucks)",
        "Right-of-way at next intersection",
        "Priority road",
        "Yield",
        "Stop",
        "No vehicles",
        "No trucks",
        "No entry",
        "General Caution",
        "Dangerous curve left",
        "Dangerous curve right",
        "Double curve",
        "Bumpy road",
        "Slippery road",
        "Road narrows (both sides)",
        "Road work",
        "Traffic signals",
        "Pedestrians",
        "Children crossing",
        "Bicycles crossing",
        "Beware of ice/snow",
        "Wild animals crossing",
        "End speed + passing limits",
        "Turn right ahead",
        "Turn left ahead",
        "Ahead only",
        "Go straight or right",
        "Go straight or left",
        "Keep right",
        "Keep left",
        "Roundabout mandatory",
        "End of no overtaking",
        "End of no overtaking (trucks)"
    )

    /** Get the label for a class index, or "Unknown" if out of range */
    operator fun get(idx: Int): String =
        labels.getOrNull(idx) ?: "Unknown"
}