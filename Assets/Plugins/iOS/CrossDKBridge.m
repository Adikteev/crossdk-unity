//
//  CrossDKBridge.m
//  CrossDKUnityBridge
//
//  Created by Adikteev on 29/03/2022.
//

#import "CrossDKBridge.h"

@interface CrossDKOverlayDelegate : NSObject<CrossDKOverlayDelegate>
@property (nonatomic, weak) CrossDKBridge *parentBridge;
- (instancetype)initWithParentBridge:(CrossDKBridge *)parentBridge;
@end

@interface CrossDKBridge ()
@property (nonatomic, strong) CrossDKOverlay* crossDKOverlay;
@property (nonatomic, strong) CrossDKOverlayDelegate *crossDKOverlayDelegate;
@end

@implementation CrossDKBridge

NSString* makeNSString (const char* string) {
    if (string) {
        return [NSString stringWithUTF8String: string];
    } else {
        return [NSString stringWithUTF8String: ""];
    }
}

char* makeCString(NSString *str) {
    const char* string = [str UTF8String];
    if (string == NULL) {
        return NULL;
    }

    char *buffer = (char*)malloc(strlen(string) + 1);
    strcpy(buffer, string);
    return buffer;
}

void unitySendMessage(const char *method, NSString *message) {
    NSLog(@"%@", message);
    UnitySendMessage("CrossDK", method, makeCString(message));
}

/// Sets up configuration for `CrossDK`.
///
/// - Parameters:
///     - appId: current application's App Store ID
///     - apiKey: authorization API key
///     - userId: user's ID
void crossDKConfigWithAppId(const char *appId, const char *apiKey, const char *userId) {
    NSString *nsAppID = makeNSString(appId);
    NSString *nsApiKey = makeNSString(apiKey);
    NSString *nsUserId = makeNSString(userId);
    [
        CrossDKConfig.shared
        setupWithAppId:nsAppID
        apiKey:nsApiKey
        userId:nsUserId
    ];
}

/// Displays an Overlay view.
///
/// - Parameters:
///     - format: banner, mid_size, interstitial overlay
///     - position: banner and mid_size overlay position
///     - withCloseButton: mid_size and interstitial overlay close button
///     - isRewarded: provides some kind of value for the user (interstitial format only)
 - (void) displayOverlayWithFormat:(OverlayFormat)isFormat position:(OverlayPosition)isPosition withCloseButton:(BOOL*)isWithCloseButton isRewarded:(BOOL*)isIsRewarded {
     _crossDKOverlay = [[CrossDKOverlay alloc] init];
     UIWindow *window = [[UIApplication sharedApplication] keyWindow];
     self.crossDKOverlayDelegate = [[CrossDKOverlayDelegate alloc] initWithParentBridge: self];
     self.crossDKOverlay.delegate = self.crossDKOverlayDelegate;

     if (window != nil) {
         [
             self.crossDKOverlay
             displayWithWindow:window
             format:isFormat
             position:isPosition
             withCloseButton:isWithCloseButton
             isRewarded:isIsRewarded
         ];
     }
 }

/// Dismisses an overlay view.
- (void) dismissOverlay {
    _crossDKOverlay = [[CrossDKOverlay alloc] init];
    UIWindow *window = [[UIApplication sharedApplication] keyWindow];
    if (window != nil) {
        [
            self.crossDKOverlay
            dismissWithWindow:window
        ];
    }
}

@end

static CrossDKBridge* delegateObject = nil;

/// Dismisses an overlay view.
void dismissOverlay() {
    if (delegateObject == nil)
        delegateObject = [[CrossDKBridge alloc] init];

    [delegateObject dismissOverlay];
}

/// Displays an Overlay view.
///
/// - Parameters:
///     - format: banner, mid_size, interstitial overlay
///     - position: banner and mid_size overlay position
///     - withCloseButton: mid_size and interstitial overlay close button
///     - isRewarded: provides some kind of value for the user (interstitial format only)
 void displayOverlayWithFormat(OverlayFormat format, OverlayPosition position, BOOL* withCloseButton, BOOL* isRewarded) {
     if (delegateObject == nil)
         delegateObject = [[CrossDKBridge alloc] init];

     [delegateObject displayOverlayWithFormat:format position:position withCloseButton:withCloseButton isRewarded:isRewarded];
 }

@implementation CrossDKOverlayDelegate

-(instancetype)initWithParentBridge:(CrossDKBridge *)parentBridge {
    self = [super init];
    if ( self ) {
        self.parentBridge = parentBridge;
    }
    return self;
}

-(void)overlayWillStartPresentation {
    unitySendMessage("OverlayWillStartPresentation", @"Overlay will start presentation");
}

-(void)overlayDidFinishPresentation {
    unitySendMessage("OverlayDidFinishPresentation", @"Overlay did finish presentation");
}

-(void)overlayWillStartDismissal {
    unitySendMessage("OverlayWillStartDismissal", @"Overlay will start dismissal");
}

-(void)overlayDidFinishDismissal {
    unitySendMessage("OverlayDidFinishDismissal", @"Overlay did finish dismissal");
}

-(void)overlayStartsPlayingVideo {
    unitySendMessage("OverlayStartsPlayingVideo", @"Overlay starts playing video");
}

-(void)overlayPlayedHalfVideo {
    unitySendMessage("OverlayPlayedHalfVideo", @"Overlay played half video");
}

-(void)overlayDidFinishPlayingVideo {
    unitySendMessage("OverlayDidFinishPlayingVideo", @"Video overlay did finish playing video");
}

-(void)overlayShowsRecommendedAppInAppStore {
    unitySendMessage("OverlayShowsRecommendedAppInAppStore", @"Overlay shows recommended app in AppStore");
}

-(void)overlayDidRewardUserWithReward {
    unitySendMessage("OverlayDidRewardUserWithReward", @"Overlay did reward user with reward");
}

-(void)overlayDidFailToLoadWithError:(NSError * _Nonnull)error {
    unitySendMessage("OverlayDidFailToLoadWithError", [NSString stringWithFormat:@"Overlay did fail to load with error : %ld", (long)error]);
}

-(void)overlayUnavailableWithError:(enum OverlayError)error {
    switch(error) {
        case OverlayErrorUnsupportedOSVersion:
            unitySendMessage("OverlayUnavailableWithError", @"Overlay error: unsupported iOS Version");
            break;
        case OverlayErrorUnavailableWindowScene:
            unitySendMessage("OverlayUnavailableWithError", @"Overlay error: unavailable window scene");
            break;
        case OverlayErrorUnavailableRecommendation:
            unitySendMessage("OverlayUnavailableWithError", @"Overlay error: unavailable recommendation");
            break;
        case OverlayErrorNoConfiguration:
            unitySendMessage("OverlayUnavailableWithError", @"Overlay error: no configuration");
            break;
    }
}

@end
