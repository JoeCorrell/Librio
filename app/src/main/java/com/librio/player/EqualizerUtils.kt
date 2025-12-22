package com.librio.player

import android.media.audiofx.Equalizer
import kotlin.math.roundToInt

fun normalizeEqPresetName(raw: String): String {
    val cleaned = raw.trim().uppercase()
    return when {
        cleaned in listOf("BASS_INCREASED", "BASSBOOST", "BASS BOOST", "BASS_INCRESSED") -> "BASS_INCREASED"
        cleaned.contains("BASS") && cleaned.contains("INCREAS") -> "BASS_INCREASED"
        cleaned in listOf("BASS_REDUCED", "BASS_REDUCER") -> "BASS_REDUCED"
        cleaned.contains("BASS") && cleaned.contains("REDUC") -> "BASS_REDUCED"
        cleaned in listOf("TREBLE_INCREASED", "TREBLE BOOST", "TREBLEBOOST") -> "TREBLE_INCREASED"
        cleaned.contains("TREBLE") && cleaned.contains("INCREAS") -> "TREBLE_INCREASED"
        cleaned in listOf("TREBLE_REDUCED", "TREBLE_REDUCER") -> "TREBLE_REDUCED"
        cleaned.contains("TREBLE") && cleaned.contains("REDUC") -> "TREBLE_REDUCED"
        cleaned in listOf("VOCAL_BOOST", "VOCAL") -> "VOCAL_BOOST"
        cleaned.contains("VOCAL") -> "VOCAL_BOOST"
        cleaned == "FLAT" -> "FLAT"
        cleaned in listOf("DEFAULT", "OFF", "NONE") -> "DEFAULT"
        else -> "DEFAULT"
    }
}

fun applyEqualizerPreset(eq: Equalizer, presetName: String) {
    val preset = normalizeEqPresetName(presetName)
    val bandCount = eq.numberOfBands.toInt()
    if (bandCount <= 0) {
        eq.enabled = false
        return
    }

    val range = eq.bandLevelRange
    if (range.size < 2) {
        eq.enabled = false
        return
    }
    val minLevel = range[0].toInt()
    val maxLevel = range[1].toInt()
    val boost = (maxLevel * 0.35f).roundToInt().coerceIn(minLevel, maxLevel).toShort()
    val cut = (minLevel * 0.35f).roundToInt().coerceIn(minLevel, maxLevel).toShort()
    val lowBandCount = (bandCount / 2).coerceAtLeast(1)
    val highBandStart = (bandCount * 2 / 3).coerceIn(0, bandCount - 1)
    val midBand = (bandCount / 2).coerceIn(0, bandCount - 1)

    resetEqualizerBands(eq)

    when (preset) {
        "DEFAULT" -> {
            eq.enabled = false
        }
        "FLAT" -> {
            eq.enabled = true
        }
        "BASS_INCREASED" -> {
            eq.enabled = true
            for (i in 0 until bandCount) {
                val level = if (i < lowBandCount) boost else 0
                eq.setBandLevel(i.toShort(), level.toShort())
            }
        }
        "BASS_REDUCED" -> {
            eq.enabled = true
            for (i in 0 until bandCount) {
                val level = if (i < lowBandCount) cut else 0
                eq.setBandLevel(i.toShort(), level.toShort())
            }
        }
        "TREBLE_INCREASED" -> {
            eq.enabled = true
            for (i in 0 until bandCount) {
                val level = if (i >= highBandStart) boost else 0
                eq.setBandLevel(i.toShort(), level.toShort())
            }
        }
        "TREBLE_REDUCED" -> {
            eq.enabled = true
            for (i in 0 until bandCount) {
                val level = if (i >= highBandStart) cut else 0
                eq.setBandLevel(i.toShort(), level.toShort())
            }
        }
        "VOCAL_BOOST" -> {
            eq.enabled = true
            eq.setBandLevel(midBand.toShort(), boost)
        }
    }
}

private fun resetEqualizerBands(eq: Equalizer) {
    val bandCount = eq.numberOfBands.toInt()
    if (bandCount <= 0) return
    for (i in 0 until bandCount) {
        eq.setBandLevel(i.toShort(), 0)
    }
}
