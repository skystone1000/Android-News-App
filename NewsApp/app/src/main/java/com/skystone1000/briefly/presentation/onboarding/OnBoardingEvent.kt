package com.skystone1000.briefly.presentation.onboarding

/**
 * User intents emitted by the onboarding screen.
 */
sealed class OnBoardingEvent {
    data object SaveAppEntry : OnBoardingEvent()
}
