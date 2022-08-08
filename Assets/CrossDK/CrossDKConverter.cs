using System.Runtime.InteropServices;
using UnityEngine;

namespace CrossDK
{
    public class CrossDKConverter
    {
        /* Interface to native implementation */

#if UNITY_IOS
        [DllImport("__Internal")]
        private static extern void crossDKConfigWithAppId(string appId, string apiKey, string userId, string deviceId);

        [DllImport("__Internal")]
        private static extern void dismissOverlay();

        [DllImport("__Internal")]
        private static extern void displayOverlayWithFormat(int format, int position, bool withCloseButton, bool isRewarded);
#elif UNITY_ANDROID       
        private static AndroidJavaObject crossDKWrapper;
        private const string CONFIG = "config";
        private const string DISMISS = "dismissOverlay";
        private const string DISPLAY = "displayOverlay";
        //[DllImport("CrossDK")]
        //private static extern void crossDKConfigWithAppId(string appId, string apiKey, string userId);

        //[DllImport("CrossDK")]
        //private static extern void dismissOverlay();

        //[DllImport("CrossDK")]
        //private static extern void displayOverlayWithFormat(int format, int position, bool withCloseButton, bool isRewarded);
#endif

        /* Public interface for use inside C# code */

        public static void CrossDKConfigWithAppId(string appId = "", string apiKey = "", string userId = "", string deviceId = "")
        {
#if UNITY_EDITOR
            Debug.Log("CrossDKConfigWithAppId called in editor");
#elif UNITY_IOS
            crossDKConfigWithAppId(appId, apiKey, userId, deviceId);
#endif
#if UNITY_ANDROID
            crossDKWrapper = new AndroidJavaObject("com.adikteev.unityadapter.CrossDKBridge");

            object[] parameters = new object[3];
            parameters[0] = appId;
            parameters[1] = apiKey;
            parameters[2] = userId;

            crossDKWrapper.Call(CONFIG, parameters);
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
