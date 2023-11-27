> :warning: Due to the end of the Cross Promotion Platform, this repository is now deprecated.

# CrossDK for Unity

## Overview

This repo contains the CrossDK interface for Unity. It allows you to cross-promote your application catalog in your Unity project.

## Requirements

**Unity** version **>= 2019.4.40**

**iOS** version **>= 11.0**

CrossDK for Unity is available with iOS 11 minimal target version, but the `CrossDKOverlay` is only available since iOS 14. CrossDK provides support in order to handle cases where the `CrossDKOverlay` is not available (see [Overlay Delegate](#overlay-delegate)).

**Android** version **>= Android 5.0**

**Android API** version **>= API 21**

The Android version is using **Kotlin 1.8.0** and **Gradle plugin 4.2.2**

For Gradle version, please make sure to use a compatible version as described here [Here](https://kotlinlang.org/docs/gradle-configure-project.html#apply-the-plugin)

This [Documentation](https://docs.unity3d.com/Manual/android-gradle-overview.html) describes which Gradle version to use depending on Unity version 

***Please note*** that if you enable one or both of these build parameters in Player's Publish Settings, you need to update the corresponding files : 
- if `Custom Main Gradle Template` enabled, update **Assets\Plugins\Android\mainTemplate.gradle**
- if `Custom Launcher Gradle Template` enabled, update **Assets\Plugins\Android\launcherTemplate.gradle**

Add the following code at the top of the file :

```rb
configurations.all {
    resolutionStrategy.eachDependency { DependencyResolveDetails details ->
        if (details.requested.group == 'org.jetbrains.kotlin') {
            details.useVersion '1.8.0'
            details.because 'stable version for android cross sdk'
        }
    }
}
```

## Installation

For installing the CrossDK follow the steps below:
> Please note that because of the change in files structure in this version, if you have an older implementation of the CrossDK in your project you should delete the old CrossDK files and reimport the new version (see steps below). make sure also to delete generated launcher and main Templates files inside Assets/plugins/Android of your project

### Using Unity package manager

To use CrossDK in your Unity project, You can integrate the CrossDK as a package by opening the package manager window on your unity editor and choose: add package from git url then fill in the git url of this repository (example using https: https://github.com/Adikteev/crossdk-unity.git), the package manager will automatically add the package as a dependency to your project.

You can then drag the **CrossDK prefab** (located in `Packages\com.adikteev.crossdk\Runtime\Prefabs\CrossDK.prefab`) into your scene.

For more information on using git url with unity package manager visit [unity docs steps](https://docs.unity3d.com/Manual/upm-ui-giturl.html)

### Manually

You can also integrate the CrossDK as a package by downloading the repository content and importing the `package.json` file in your package manager window.
You can then drag the **CrossDK prefab** (located in `Packages\com.adikteev.crossdk\Runtime\Prefabs\CrossDK.prefab`) into your scene.

### iOS using CocoaPods 

To install CocoaPods on MacOS, add the following entry:

```rb
$ sudo gem install cocoapods
```

The CrossDK pod is automatically installed in the Xcode project when building with Unity, thanks to [External Dependency Manager for Unity](https://github.com/googlesamples/unity-jar-resolver). 

If you already use CocoaPods in your Unity project, you should consider adding your pods with [EDM4U](https://github.com/googlesamples/unity-jar-resolver) as well. 

(If you encounter compilation issues, you should close Xcode, delete the derived data, relaunch Xcode, perform a clean build of the project, and then build again.)

## Common configuration

All the methods you'll need to call are in the `CrossDKSingleton` script on the **CrossDK prefab**, and they all are public and static. Thus, you can call them from anywhere at anytime just by adding `using CrossDK;` at the top of any script.

In order to display an overlay properly, CrossDK requires some information. Since CrossDK won't work without these, you should set them up as soon as possible. In the following example, we use the setup function inside a `Start` event, but it's up to you to set it up wherever you like:

```csharp
CrossDKSingleton.CrossDKConfigWithAppId(string appId, string apiKey, string userId, string deviceId)
```

```csharp
using UnityEngine;
using CrossDK;

public class CrossDKSample : MonoBehaviour
{
    private void Start()
    {
        CrossDKSingleton.Config(
            <YOUR APP ID>,
            <YOUR API KEY>,
            <USER ID (optional)>,
            <DEVICE ID (optional)>);
    }
}
```

You can also enter this information on the CrossDK prefab and check autoCallConfig to let it call the config method automatically during the `Awake` event.

Note: The CrossDK prefab is not destroyed during scenes changes, so you only need to drag it into your first scene.

## Usage

Here are the configurations for each overlay format :  
- `OverlayFormat.Banner`: settle its position between `OverlayPosition.Bottom` or `OverlayPosition.BottomRaised`, with or without a close button (the close button is Android only).
- `OverlayFormat.MidSize`: settle its position between `OverlayPosition.Bottom` or `OverlayPosition.BottomRaised`, with or without a close button.
- `OverlayFormat.Interstitial`: settle it with or without a close button, with or without a rewarded.

```csharp
CrossDKSingleton.DisplayOverlayWithFormat(OverlayFormat format, OverlayPosition position, bool withCloseButton, bool isRewarded)
```

```csharp
using UnityEngine;
using CrossDK;

public class CrossDKSample : MonoBehaviour
{
    public void DisplayMidSizeOverlayExample()
    {
        CrossDKSingleton.DisplayOverlay(
            OverlayFormat.MidSize, 
            OverlayPosition.Bottom, 
            true, 
            false);
    }
}
```

A `DismissOverlay()` method is available in order to prevent screen changes:

```csharp
CrossDKSingleton.DismissOverlay()
```

```csharp
using UnityEngine;
using CrossDK;

public class CrossDKSample : MonoBehaviour
{
    public void DismissExample()
    {
        CrossDKSingleton.DismissOverlay();
    }
}
```

## Preload

You can preload overlays before displaying it on screen, this feature is particularly useful on mid size and interstitial format which contain video asset that may take time to load:

```csharp
using UnityEngine;
using CrossDK;

public class CrossDKSample : MonoBehaviour
{
    public void PreloadOverlayExample()
    {
        CrossDKSingleton.LoadOverlay(
            OverlayFormat.MidSize, 
            OverlayPosition.Bottom, 
            true, 
            false);
    }
}
```
Make sure to leave enough time for the assets to load fully
A preloaded overlay becomes expired after '30 minutes' of its load, when this delay expires you can no longer show it on screen.
To monitor the preload action you can use these delagates:

- `CrossDKSingleton.overlayWillStartPreloadDelegate`: called when the preload starts
- `CrossDKSingleton.overlayDidFinishPreloadDelegate`: called when the preload finishes
- `CrossDKSingleton.overlayPreloadExpiredDelegate`: called when the preload expires

Please not that when displaying a not fully loaded mid size or interstitial you will only see a banner because of the video not being fully prepared.

## Overlay Delegate

Additionally, many delegates are available if you want to monitor what is happening with the `CrossDKOverlay`:

For instance, you can track when the user is rewarded with the delegate:
```csharp
CrossDKSingleton.overlayDidRewardUserWithRewardDelegate
```

```csharp
using UnityEngine;
using CrossDK;

public class CrossDKDelegatesSample : MonoBehaviour
{
    private void Start()
    {
        CrossDKSingleton.overlayDidRewardUserWithRewardDelegate += OverlayDidRewardUserWithRewardExample;
    }

    private void OnDestroy()
    {
        CrossDKSingleton.overlayDidRewardUserWithRewardDelegate -= OverlayDidRewardUserWithRewardExample;
    }

    private void OverlayDidRewardUserWithRewardExample(string message)
    {
        Debug.Log("User was rewarded");
    }
}
```
## Debug

You can enable logs for the cross promotion sdk by setting the log level to verbose, error or none using the following method:

```csharp
using UnityEngine;
using CrossDK;

void Awake()
    {
        CrossDKSingleton.SetDebugMode(DebugLevel.Verbose);
    }
```
- `DebugLevel.Verbose`: for verbose logging
- `DebugLevel.Error`: for error logging
- `DebugLevel.None`: for disabling logger

> We recommend setting up this config before initializing the SDK.

You can check the [crossdk-ios](https://github.com/Adikteev/crossdk-ios) and the [crossdk-android](https://github.com/Adikteev/crossdk-android) repositories for more details.

That’s all you need to know!
