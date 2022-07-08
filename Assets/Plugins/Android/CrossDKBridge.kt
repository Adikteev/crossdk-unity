package com.adikteev.unityadapter

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.adikteev.crossdk.CrossDKConfig
import com.adikteev.crossdk.network.OnInitCrossDKListener
import com.adikteev.crossdk.views.CrossDKInterstitialView
import com.adikteev.crossdk.views.CrossDKMidSizeView
import com.adikteev.crossdk.views.CrossDKView
import com.adikteev.crossdk.views.listener.CrossDKContentCallback
import com.adikteev.crossdk.views.position.CrossDKPosition
import com.unity3d.player.UnityPlayer

/*
**CrossDKBridge for showing CrossDK android format
 */

public class CrossDKBridge {
    private var mUnityPlayerActivity: Activity = UnityPlayer.currentActivity
    private var mOpenedOverlayFormat: OverlayFormat = OverlayFormat.NONE
    private lateinit var mCrossDKView: CrossDKView
    private lateinit var mCrossDKMidSizeView: CrossDKMidSizeView
    private lateinit var mCrossDKInterstitialView: CrossDKInterstitialView

    ///////////////////////////////////////////////////////////////////////////
    // CONFIG
    ///////////////////////////////////////////////////////////////////////////

    public fun config(appId: String, apiKey: String, userId: String) {
        CrossDKConfig.Builder()
            .apiKey(apiKey)
            .appId(appId)
            .setup(mUnityPlayerActivity, object : OnInitCrossDKListener {
                override fun onInitSuccess() {}
                override fun onInitFailure(exception: Exception?) {
                    unitySendOverlayError("Overlay error: configuration error")
                }
            })
    }

    ///////////////////////////////////////////////////////////////////////////
    // DISPLAY
    ///////////////////////////////////////////////////////////////////////////

    public fun displayOverlay(format: Int, position: Int, isCloseButtonVisible: Boolean, isRewarded: Boolean) {
        val crossDKContentCallback = object: CrossDKContentCallback {
            override fun onConfigurationError() {
                unitySendOverlayError("Overlay error: configuration error")
            }

            override fun onNoRecommendation() {
                unitySendOverlayError("Overlay error: unavailable recommendation")
            }

            override fun onShowContentError() {
                unitySendOverlayError("Overlay error: show content error")
            }

            override fun onUnsupportedApiVersion() {
                unitySendOverlayError("Overlay error: unsupported Api version")
            }
        }
        mUnityPlayerActivity.runOnUiThread {
            if (mOpenedOverlayFormat != OverlayFormat.NONE) dismissOverlay()
            val overlayFormat = OverlayFormat.fromInt(format)
            mOpenedOverlayFormat = overlayFormat
            when (overlayFormat) {
                OverlayFormat.BANNER -> {
                    mCrossDKView = CrossDKView(mUnityPlayerActivity)
                    mCrossDKView.setCrossDKContentCallback(crossDKContentCallback)
                    mCrossDKView.setCloseButtonVisibility(if (isCloseButtonVisible) View.VISIBLE else View.INVISIBLE)
                    mUnityPlayerActivity.addContentView(mCrossDKView, getLayoutParams())
                    mCrossDKView.setPosition(if (position == 0) CrossDKPosition.BOTTOM else CrossDKPosition.BOTTOM_RAISED)
                }
                OverlayFormat.MID_SIZE -> {
                    mCrossDKMidSizeView = CrossDKMidSizeView(mUnityPlayerActivity)
                    mCrossDKMidSizeView.setCrossDKContentCallback(crossDKContentCallback)
                    mCrossDKMidSizeView.setCloseButtonVisibility(if (isCloseButtonVisible) View.VISIBLE else View.INVISIBLE)
                    mUnityPlayerActivity.addContentView(mCrossDKMidSizeView, getLayoutParams())
                    mCrossDKMidSizeView.setPosition(if (position == 0) CrossDKPosition.BOTTOM else CrossDKPosition.BOTTOM_RAISED)
                }
                OverlayFormat.INTERSTITIAL -> {
                    mCrossDKInterstitialView = CrossDKInterstitialView(mUnityPlayerActivity)
                    mCrossDKInterstitialView.setCrossDKContentCallback(crossDKContentCallback)
                    mCrossDKInterstitialView.setCloseButtonVisibility(if (isCloseButtonVisible) View.VISIBLE else View.INVISIBLE)
                    mUnityPlayerActivity.addContentView(mCrossDKInterstitialView, getLayoutParams())
                }
                else -> {
                    unitySendOverlayError("Overlay error: unsupported format requested")
                }
            }
        }
    }

    public fun dismissOverlay() {
        mUnityPlayerActivity.runOnUiThread {
            when (mOpenedOverlayFormat) {
                OverlayFormat.BANNER -> {
                    mCrossDKView.dismissView(true)
                }
                OverlayFormat.MID_SIZE -> {
                    mCrossDKMidSizeView.dismissView(true)
                }
                OverlayFormat.INTERSTITIAL -> {
                    mCrossDKInterstitialView.dismissView(true)
                }
                else -> {}
            }
            mOpenedOverlayFormat = OverlayFormat.NONE
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // LISTENERS
    ///////////////////////////////////////////////////////////////////////////

    private fun overlayWillStartPresentation() {
        unitySendMessage("OverlayWillStartPresentation", "Overlay will start presentation");
    }

    private fun overlayDidFinishPresentation() {
        unitySendMessage("OverlayDidFinishPresentation", "Overlay did finish presentation");
    }

    private fun overlayWillStartDismissal() {
        unitySendMessage("OverlayWillStartDismissal", "Overlay will start dismissal");
    }

    private fun overlayDidFinishDismissal() {
        unitySendMessage("OverlayDidFinishDismissal", "Overlay did finish dismissal");
    }

    private fun overlayStartsPlayingVideo() {
        unitySendMessage("OverlayStartsPlayingVideo", "Overlay starts playing video");
    }

    private fun overlayPlayedHalfVideo() {
        unitySendMessage("OverlayPlayedHalfVideo", "Overlay played half video");
    }

    private fun overlayDidFinishPlayingVideo() {
        unitySendMessage("OverlayDidFinishPlayingVideo", "Video overlay did finish playing video");
    }

    private fun overlayShowsRecommendedAppInAppStore() {
        unitySendMessage("OverlayShowsRecommendedAppInAppStore", "Overlay shows recommended app in AppStore");
    }

    private fun overlayDidRewardUserWithReward() {
        unitySendMessage("OverlayDidRewardUserWithReward", "Overlay did reward user with reward");
    }

    private fun overlayDidFailToLoadWithError(exception: Exception) {
        unitySendMessage(
            "OverlayDidFailToLoadWithError",
            "Overlay did fail to load with error: ${exception.message}"
        )
    }

    ///////////////////////////////////////////////////////////////////////////
    // HELPERS
    ///////////////////////////////////////////////////////////////////////////

    private fun getLayoutParams(): FrameLayout.LayoutParams {
        val adParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT
        )
        adParams.gravity = Gravity.BOTTOM
        return adParams
    }

    private fun unitySendOverlayError(message: String) {
        unitySendMessage("OverlayUnavailableWithError", message)
    }

    private fun unitySendMessage(method: String, message: String) {
        UnityPlayer.UnitySendMessage("CrossDK", method, message)
    }

    private enum class OverlayFormat(val value: Int) {
        NONE(-1),
        BANNER(0),
        MID_SIZE(1),
        INTERSTITIAL(2);

        companion object {
            fun fromInt(value: Int) = values().first { it.value == value }
        }
    }
}