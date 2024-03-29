using System;
using UnityEngine;

namespace CrossDK
{
    public class CrossDKSingleton : MonoBehaviour
    {
        private static CrossDKSingleton _instance;

        public delegate void CrossDKDelegate(string message);
        public static CrossDKDelegate overlayWillStartPreloadDelegate;
        public static CrossDKDelegate overlayDidFinishPreloadDelegate;
        public static CrossDKDelegate overlayPreloadExpiredDelegate;
        public static CrossDKDelegate overlayWillStartPresentationDelegate;
        public static CrossDKDelegate overlayDidFinishPresentationDelegate;
        public static CrossDKDelegate overlayWillStartDismissalDelegate;
        public static CrossDKDelegate overlayDidFinishDismissalDelegate;
        public static CrossDKDelegate overlayStartsPlayingVideoDelegate;
        public static CrossDKDelegate overlayPlayedHalfVideoDelegate;
        public static CrossDKDelegate overlayDidFinishPlayingVideoDelegate;
        public static CrossDKDelegate overlayShowsRecommendedAppInAppStoreDelegate;
        public static CrossDKDelegate overlayDidRewardUserWithRewardDelegate;
        public static CrossDKDelegate overlayDidFailToLoadWithErrorDelegate;
        public static CrossDKDelegate overlayUnavailableWithErrorDelegate;

        [Header("SDK settings")]
        [SerializeField] private bool _autoCallConfig;
        [SerializeField] private string _appId;
        [SerializeField] private string _apiKey;
        [SerializeField] private string _userId;

        private void Awake()
        {
            if (_instance != null)
            {
                Destroy(this);
                return;
            }
            _instance = this;
            DontDestroyOnLoad(gameObject);

            if (_autoCallConfig)
            {
                Config(_appId, _apiKey, _userId);
            }
        }

        #region CrossDK Methods

        public static void SetDebugMode(DebugLevel level = DebugLevel.None)
        {
            CrossDKConverter.SetDebugMode(level);
        }

        public static void Config(string appId = "", string apiKey = "", string userId = "", string deviceId = "")
        {
            CrossDKConverter.CrossDKConfigWithAppId(appId, apiKey, userId, deviceId);
        }

        public static void SetDeviceId(string deviceId)
        {
            CrossDKConverter.SetDeviceId(deviceId);
        }

        public static void DismissOverlay()
        {
            CrossDKConverter.DismissOverlay();
        }

        public static void LoadOverlay(OverlayFormat format = OverlayFormat.Interstitial, OverlayPosition position = OverlayPosition.Bottom, bool withCloseButton = true, bool isRewarded = true)
        {
            CrossDKConverter.LoadOverlayWithFormat(format, position, withCloseButton, isRewarded);
        }

        public static void DisplayOverlay(OverlayFormat format = OverlayFormat.Interstitial, OverlayPosition position = OverlayPosition.Bottom, bool withCloseButton = true, bool isRewarded = true)
        {
            CrossDKConverter.DisplayOverlayWithFormat(format, position, withCloseButton, isRewarded);
            dismissKeyboard();
        }

        #endregion

        #region CrossDK Delegates

        //internal void Log(string message)
        //{
        //    Logger.Log(message);
        //}

        internal void OverlayWillStartPreload(string message)
        {
            overlayWillStartPreloadDelegate?.Invoke(message);
        }

        internal void OverlayDidFinishPreload(string message)
        {
            overlayDidFinishPreloadDelegate?.Invoke(message);
        }

        internal void OverlayPreloadExpired(string message)
        {
            overlayPreloadExpiredDelegate?.Invoke(message);
        }

        internal void OverlayWillStartPresentation(string message)
        {
            overlayWillStartPresentationDelegate?.Invoke(message);
        }

        internal void OverlayDidFinishPresentation(string message)
        {
            overlayDidFinishPresentationDelegate?.Invoke(message);
        }

        internal void OverlayWillStartDismissal(string message)
        {
            overlayWillStartDismissalDelegate?.Invoke(message);
        }

        internal void OverlayDidFinishDismissal(string message)
        {
            overlayDidFinishDismissalDelegate?.Invoke(message);
        }

        internal void OverlayStartsPlayingVideo(string message)
        {
            overlayStartsPlayingVideoDelegate?.Invoke(message);
        }

        internal void OverlayPlayedHalfVideo(string message)
        {
            overlayPlayedHalfVideoDelegate?.Invoke(message);
        }

        internal void OverlayDidFinishPlayingVideo(string message)
        {
            overlayDidFinishPlayingVideoDelegate?.Invoke(message);
        }

        internal void OverlayShowsRecommendedAppInAppStore(string message)
        {
            overlayShowsRecommendedAppInAppStoreDelegate?.Invoke(message);
        }

        internal void OverlayDidRewardUserWithReward(string message)
        {
            overlayDidRewardUserWithRewardDelegate?.Invoke(message);
        }

        internal void OverlayDidFailToLoadWithError(string message)
        {
            overlayDidFailToLoadWithErrorDelegate?.Invoke(message);
        }

        internal void OverlayUnavailableWithError(string message)
        {
            overlayUnavailableWithErrorDelegate?.Invoke(message);
        }

        #endregion

        // Dismiss keyboard if shown on screen

        private static void dismissKeyboard()
        {
            if (TouchScreenKeyboard.visible == true)
            {
                TouchScreenKeyboard keyboard = TouchScreenKeyboard.Open("", TouchScreenKeyboardType.Default);
                keyboard.active = false;
            }
        }
    }

    public enum OverlayFormat
    {
        Banner = 0,
        MidSize = 1,
        Interstitial = 2
    }

    public enum OverlayPosition
    {
        Bottom = 0,
        BottomRaised = 1
    }
    public enum DebugLevel
    {
        None = 0,
        Error = 1,
        Verbose = 2
    }
}
