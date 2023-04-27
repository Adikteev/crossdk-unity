using System;
using System.Runtime.InteropServices;
using UnityEngine;

namespace CrossDK
{
    public class CrossDKConverter
    {
        /* Interface to native implementation */

#if UNITY_IOS
        [DllImport("__Internal")]
        private static extern void crossDKConfigWithAppId(string appId, string apiKey, string userId);

        [DllImport("__Internal")]
        private static extern void setDeviceId(string deviceId);

        [DllImport("__Internal")]
        private static extern void dismissOverlay();

        [DllImport("__Internal")]
        private static extern void displayOverlayWithFormat(int format, int position, bool withCloseButton, bool isRewarded);

        [DllImport("__Internal")]
        private static extern void loadOverlayWithFormat(int format, int position, bool withCloseButton, bool isRewarded);
#elif UNITY_ANDROID       
        private static AndroidJavaObject crossDKWrapper = new AndroidJavaObject("com.adikteev.unityadapter.CrossDKBridge");
        private const string CONFIG = "config";
        private const string SETUPDEBUGMODE = "setupDebugMode";
        private const string DISMISS = "dismissOverlay";
        private const string LOAD = "loadOverlay";
        private const string DISPLAY = "displayOverlay";
#endif

        /* Public interface for use inside C# code */
        public static void SetDebugMode(DebugLevel level)
        {
#if UNITY_ANDROID
            object[] parameters = new object[1];
            parameters[0] = (int)level;
            crossDKWrapper.Call(SETUPDEBUGMODE, parameters);
#endif
        }


        public static void CrossDKConfigWithAppId(string appId = "", string apiKey = "", string userId = "", string deviceId = "")
        {
#if UNITY_EDITOR
            Debug.Log("CrossDKConfigWithAppId called in editor");
#elif UNITY_IOS
            crossDKConfigWithAppId(appId, apiKey, userId);
#endif
#if UNITY_ANDROID
            object[] parameters = new object[4];
            parameters[0] = appId;
            parameters[1] = apiKey;
            parameters[2] = userId;
            parameters[3] = deviceId;

            crossDKWrapper.Call(CONFIG, parameters);
#endif
        }

        public static void SetDeviceId(string deviceId)
        {
#if UNITY_EDITOR
            Debug.Log("SetDeviceId called in editor");
#elif UNITY_IOS
            setDeviceId(deviceId);
#endif
#if UNITY_ANDROID
            // Not available yet
#endif
        }

        public static void DismissOverlay()
        {
#if UNITY_EDITOR
            Debug.Log("DismissOverlay called in editor");
#elif UNITY_IOS
            dismissOverlay();
#endif
#if UNITY_ANDROID
            crossDKWrapper.Call(DISMISS);
#endif
        }

        public static void LoadOverlayWithFormat(OverlayFormat format, OverlayPosition position, bool withCloseButton, bool isRewarded)
        {
#if UNITY_EDITOR
            Debug.Log("DisplayOverlayWithFormat called in editor");
#elif UNITY_IOS
            loadOverlayWithFormat((int)format, (int)position, withCloseButton, isRewarded);
#endif
#if UNITY_ANDROID
            object[] parameters = new object[4];
            parameters[0] = (int)format;
            parameters[1] = (int)position;
            parameters[2] = withCloseButton;
            parameters[3] = isRewarded;

            crossDKWrapper.Call(LOAD, parameters);
#endif
        }

        public static void DisplayOverlayWithFormat(OverlayFormat format, OverlayPosition position, bool withCloseButton, bool isRewarded)
        {
#if UNITY_EDITOR
            Debug.Log("DisplayOverlayWithFormat called in editor");
#elif UNITY_IOS
            displayOverlayWithFormat((int)format, (int)position, withCloseButton, isRewarded);
#endif
#if UNITY_ANDROID
            object[] parameters = new object[4];
            parameters[0] = (int)format;
            parameters[1] = (int)position;
            parameters[2] = withCloseButton;
            parameters[3] = isRewarded;

            crossDKWrapper.Call(DISPLAY, parameters);
#endif
        }
    }
}
