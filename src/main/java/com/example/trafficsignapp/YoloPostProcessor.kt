package com.example.trafficsignapp

import android.graphics.RectF
import kotlin.math.exp
/**
 * Utility to parse the raw YOLOv11 output tensor into
 * actual bounding-boxes, scores and class IDs.
 */
private fun sigmoid(x: Float): Float = 1f / (1f + exp(-x))

object YoloPostProcessor {

    fun parseYoloOutput(
        raw: FloatArray,
        imageW: Int,
        imageH: Int,
        confThreshold: Float
    ): Triple<List<RectF>, List<Float>, List<Int>> {

        val numPreds = 8400        // number of boxes
        val rowLen = 66            // 4 bbox + 1 obj conf + 61 classes
        val numClasses = 61

        val boxes = mutableListOf<RectF>()
        val scores = mutableListOf<Float>()
        val classIds = mutableListOf<Int>()

        for (i in 0 until numPreds) {
            val offset = i * rowLen
            val cx = raw[offset]
            val cy = raw[offset + 1]
            val w  = raw[offset + 2]
            val h  = raw[offset + 3]


            val objLogit = raw[offset + 4]
            val objConf  = sigmoid(objLogit)

            // Get class with highest score
            var maxClsConf = 0f
            var bestCls = -1
            for (c in 0 until numClasses) {
                val clsLogit = raw[offset + 5 + c]
                val clsConf  = sigmoid(clsLogit)
                if (clsConf > maxClsConf) {
                    maxClsConf = clsConf
                    bestCls    = c
                }
            }

            val finalScore = objConf * maxClsConf
            if (finalScore < confThreshold) continue

            // Convert normalized xywh to pixel box
            val left   = (cx - w / 2f) * imageW
            val top    = (cy - h / 2f) * imageH
            val right  = (cx + w / 2f) * imageW
            val bottom = (cy + h / 2f) * imageH
            
            boxes += RectF(left, top, right, bottom)
            scores += finalScore
            classIds += bestCls
        }
        return Triple(boxes, scores, classIds)
    }
}