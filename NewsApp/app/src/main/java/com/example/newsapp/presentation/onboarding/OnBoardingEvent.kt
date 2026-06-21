package com.example.newsapp.presentation.onboarding

/**
 * User intents emitted by the onboarding screen.
 */
sealed class OnBoardingEvent {
    data object SaveAppEntry : OnBoardingEvent()
}
