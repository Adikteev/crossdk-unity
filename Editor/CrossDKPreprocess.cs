using System.IO;
using UnityEditor;
using UnityEditor.Build;
using UnityEditor.Build.Reporting;

public class CrossDKPreprocess : IPreprocessBuildWithReport
{
    public int callbackOrder { get { return 0; } }
    public void OnPreprocessBuild(BuildReport report)
    {
#if UNITY_ANDROID
        string packageFolder = "Packages/com.adikteev.crossdk";
        string assetsFolder = "Assets";
        string androidFolder = "Plugins/Android";

        string launcherTemplateName = "launcherTemplate.gradle";
        string mainTemplateName = "mainTemplate.gradle";

        string androidFolderInAssets = $"{assetsFolder}/{androidFolder}";
        string launcherTemplatePathInAssets = $"{androidFolderInAssets}/{launcherTemplateName}";
        string mainTemplatePathInAssets = $"{androidFolderInAssets}/{mainTemplateName}";

#if UNITY_2019
        launcherTemplateName = "launcherTemplate2019.gradle";
        mainTemplateName = "mainTemplate2019.gradle";
#endif
        if (!Directory.Exists(androidFolderInAssets))
        {
            Directory.CreateDirectory(androidFolderInAssets);
        }
        if (!File.Exists(launcherTemplatePathInAssets))
        {
            string launcherTemplatePathInPackage = $"{packageFolder}/{androidFolder}/{launcherTemplateName}";
            FileUtil.CopyFileOrDirectory(launcherTemplatePathInPackage, launcherTemplatePathInAssets);
        }
        if (!File.Exists(mainTemplatePathInAssets))
        {
            string mainTemplatePathInPackage = $"{packageFolder}/{androidFolder}/{mainTemplateName}";
            FileUtil.CopyFileOrDirectory(mainTemplatePathInPackage, mainTemplatePathInAssets);
        }
#endif
    }
}