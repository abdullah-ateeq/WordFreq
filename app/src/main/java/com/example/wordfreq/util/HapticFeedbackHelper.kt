package com.example.wordfreq.util

import android.view.HapticFeedbackConstants
import android.view.View

object HapticFeedbackHelper {

    fun performHaptic(view: View?, enabled: Boolean) {
        if (!enabled || view == null) return
        view.performHapticFeedback(
            HapticFeedbackConstants.KEYBOARD_TAP,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        )
    }

    fun performConfirmHaptic(view: View?, enabled: Boolean) {
        if (!enabled || view == null) return
        view.performHapticFeedback(
            HapticFeedbackConstants.LONG_PRESS,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        )
    }
}
