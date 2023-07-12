package com.adikteev.unityadapter

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.adikteev.crossdk.CrossDKConfig
import com.adikteev.crossdk.network.OnInitCrossDKListener
import com.adikteev.crossdk.views.CrossDKInterstitialView
import com.adikteev.crossdk.views.CrossDKMidSizeView
import com.adikteev.crossdk.views.CrossDKView
import com.adikteev.crossdk.views.listener.CrossDKContentCallback
import com.adikteev.crossdk.views.listener.CrossDKLoadCallback
import com.adikteev.crossdk.views.listener.CrossDKRewardedCallback
import com.adikteev.crossdk.views.position.CrossDKPosition
import com.unity3d.player.UnityPlayer


/*
**CrossDKBridge for showing CrossDK android format
 */

public class CrossDKBridge {
    private var mUnityPlayerActivity: Activity = UnityPlayer.currentActivity
    private var mOpenedOverlayFormat: OverlayFormat = OverlayFormat.NONE
    private var mCrossDKView: CrossDKView? = null
    private var mCrossDKMidSizeView: CrossDKMidSizeView? = null
    private var mCrossDKInterstitialView: CrossDKInterstitialView? = null

    ///////////////////////////////////////////////////////////////////////////
    // CONFIG
    ///////////////////////////////////////////////////////////////////////////

    public fun setupDebugMode(debugLevel: Int) {
        CrossDKConfig.Setting.logLevel = when (DebugLevel.fromInt(debugLevel)) {
            DebugLevel.VERBOSE -> CrossDKConfig.LOG.VERBOSE
            DebugLevel.ERROR -> CrossDKConfig.LOG.ERROR
            else -> CrossDKConfig.LOG.NONE
        }
    }

    public fun config(appId: String, apiKey: String, userId: String?, deviceId: String?) {
        CrossDKConfig.Builder()
            .apiKey(apiKey)
            .appId(appId)
            .userId(userId)
            .deviceId(deviceId)
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

    public fun loadOverlay(
        format: Int,
        position: Int,
        isCloseButtonVisible: Boolean,
        isRewarded: Boolean
    ) {
        mUnityPlayerActivity.runOnUiThread {
            overlayWillStartPreload()
            mOpenedOverlayFormat = OverlayFormat.fromInt(format)
            createFormat(position, isCloseButtonVisible, isRewarded)
            when (mOpenedOverlayFormat) {
                OverlayFormat.BANNER -> {
                    mCrossDKView?.load(getCrossDKLoadCallback())
                }
                OverlayFormat.MID_SIZE -> {
                    mCrossDKMidSizeView?.load(getCrossDKLoadCallback())
                }
                OverlayFormat.INTERSTITIAL -> {
                    mCrossDKInterstitialView?.load(getCrossDKLoadCallback())
                }
                else -> {
                    unitySendOverlayError("Overlay error: unsupported format requested")
                }
            }
        }
    }

    public fun displayOverlay(
        format: Int,
        position: Int,
        isCloseButtonVisible: Boolean,
        isRewarded: Boolean
    ) {
        overlayWillStartPresentation()
        mUnityPlayerActivity.runOnUiThread {
            mOpenedOverlayFormat = OverlayFormat.fromInt(format)
            createFormat(position, isCloseButtonVisible, isRewarded)
            when (mOpenedOverlayFormat) {
                OverlayFormat.BANNER -> {
                    mCrossDKView?.show()
                }
                OverlayFormat.MID_SIZE -> {
                    mCrossDKMidSizeView?.show()
                }
                OverlayFormat.INTERSTITIAL -> {
                    mCrossDKInterstitialView?.show()
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
                    mCrossDKView?.dismissView(true)
                    destroyViews()
                }
                OverlayFormat.MID_SIZE -> {
                    mCrossDKMidSizeView?.dismissView(true)
                    destroyViews()
                }
                OverlayFormat.INTERSTITIAL -> {
                    mCrossDKInterstitialView?.dismissView(true)
                    destroyViews()
                }
                OverlayFormat.NONE -> overlayDidFinishDismissal()
            }
        }
    }

    public fun destroyViews() {
        mUnityPlayerActivity.runOnUiThread {
            when (mOpenedOverlayFormat) {
                OverlayFormat.BANNER -> {
                    mCrossDKView?.destroy()
                    val parentView = mCrossDKView?.parent
                    if (parentView is ViewGroup) {
                        parentView.removeView(mCrossDKView)
                    }
                    mCrossDKView = null
                }
                OverlayFormat.MID_SIZE -> {
                    mCrossDKMidSizeView?.destroy()
                    val parentView = mCrossDKMidSizeView?.parent
                    if (parentView is ViewGroup) {
                        parentView.removeView(mCrossDKMidSizeView)
                    }
                    mCrossDKMidSizeView = null
                }
                OverlayFormat.INTERSTITIAL -> {
                    mCrossDKInterstitialView?.destroy()
                    val parentView = mCrossDKInterstitialView?.parent
                    if (parentView is ViewGroup) {
                        parentView.removeView(mCrossDKInterstitialView)
                    }
                    mCrossDKInterstitialView = null
                }
                else -> mOpenedOverlayFormat = OverlayFormat.NONE
            }
            mOpenedOverlayFormat = OverlayFormat.NONE
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // LISTENERS
    ///////////////////////////////////////////////////////////////////////////

    private fun overlayWillStartPreload() {
        unitySendMessage("OverlayWillStartPreload", "Overlay will start preload");
    }

    private fun overlayDidFinishedPreload() {
        unitySendMessage("OverlayDidFinishPreload", "Overlay did finish preload");
    }

    private fun overlayPreloadExpired() {
        unitySendMessage("OverlayPreloadExpired", "Overlay preload expired");
    }

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
        unitySendMessage(
            "OverlayShowsRecommendedAppInAppStore",
            "Overlay shows recommended app in AppStore"
        );
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

    private fun createFormat(
        position: Int,
        isCloseButtonVisible: Boolean,
        isRewarded: Boolean
    ) {
        when {
            mOpenedOverlayFormat == OverlayFormat.BANNER && mCrossDKView == null -> {
                mCrossDKView = CrossDKView(mUnityPlayerActivity)
                mCrossDKView?.setCrossDKContentCallback(getCrossDKContentCallback())
                mCrossDKView?.setCloseButtonVisibility(if (isCloseButtonVisible) View.VISIBLE else View.INVISIBLE)
                mUnityPlayerActivity.addContentView(mCrossDKView, getLayoutParams())
                mCrossDKView?.setPosition(if (position == 0) CrossDKPosition.BOTTOM else CrossDKPosition.BOTTOM_RAISED)
            }
            mOpenedOverlayFormat == OverlayFormat.MID_SIZE && mCrossDKMidSizeView == null -> {
                mCrossDKMidSizeView = CrossDKMidSizeView(mUnityPlayerActivity)
                mCrossDKMidSizeView?.setCrossDKContentCallback(getCrossDKContentCallback())
                mCrossDKMidSizeView?.setCloseButtonVisibility(if (isCloseButtonVisible) View.VISIBLE else View.INVISIBLE)
                mUnityPlayerActivity.addContentView(mCrossDKMidSizeView, getLayoutParams())
                mCrossDKMidSizeView?.setPosition(if (position == 0) CrossDKPosition.BOTTOM else CrossDKPosition.BOTTOM_RAISED)
            }
            mOpenedOverlayFormat == OverlayFormat.INTERSTITIAL && mCrossDKInterstitialView == null -> {
                mCrossDKInterstitialView = CrossDKInterstitialView(mUnityPlayerActivity)
                mCrossDKInterstitialView?.setCrossDKContentCallback(getCrossDKContentCallback())
                mCrossDKInterstitialView?.setCloseButtonVisibility(if (isCloseButtonVisible) View.VISIBLE else View.INVISIBLE)
                mCrossDKInterstitialView?.setRewarded(isRewarded,
                    object : CrossDKRewardedCallback {
                        override fun onUserRewarded() {
                            overlayDidFinishPlayingVideo()
                            overlayDidRewardUserWithReward()
                        }
                    })
                mUnityPlayerActivity.addContentView(mCrossDKInterstitialView, getLayoutParams())
            }
        }
    }

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

    public enum class DebugLevel(val value: Int) {
        NONE(0),
        ERROR(1),
        VERBOSE(2);

        companion object {
            fun fromInt(value: Int) = values().first { it.value == value }
        }
    }

    private fun getCrossDKLoadCallback() = object : CrossDKLoadCallback {
        override fun onRecommendationLoaded() {
            overlayDidFinishedPreload()
        }

        override fun onRecommendationLoadFailure() {
            overlayDidFailToLoadWithError(java.lang.Exception("Recommendation load failure"))
            destroyViews()
        }

        override fun onRecommendationExpired() {
            overlayPreloadExpired()
            destroyViews()
        }
    }

    private fun getCrossDKContentCallback() = object : CrossDKContentCallback {
        override fun onConfigurationError() {
            unitySendOverlayError("Overlay error: configuration error")
        }

        override fun onNoRecommendation() {
            unitySendOverlayError("Overlay error: unavailable recommendation")
            destroyViews()
        }

        override fun onShowContentError() {
            unitySendOverlayError("Overlay error: show content error")
        }

        override fun onRecommendationDisplayed() {
            overlayDidFinishPresentation()
        }

        override fun onRecommendationClicked() {
            overlayShowsRecommendedAppInAppStore()
        }

        override fun onRecommendationClosed() {
            overlayWillStartDismissal()
            overlayDidFinishDismissal()
            destroyViews()
        }

        override fun onUnsupportedApiVersion() {
            unitySendOverlayError("Overlay error: unsupported Api version")
        }
    }
}