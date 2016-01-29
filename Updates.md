## 05/11/09 ##

**MD5Importer Concurrent version 1.1 for jME2.0 release**

1. Added the support for user to specify texture extension to be used when loading binary exported meshes.

## 05/10/09 ##

**MD5Importer Concurrent version 1.0 for jME2.0 release**

1. Concurrent version of MD5Importer that allows animation update process to be separated from the rendering thread to greatly enhance performance and more importantly scalability.

2. This version also includes the normal sharing functionality that allows vertex normals to be shared if the vertices share the same geometric position.

**MD5Importer jME1.0 and jME2.0 version 1.3 release.**

1. Fixed a cycle update bug.
2. Added in normal sharing functionality.

## 08/07/08 ##

**MD5Importer jME1.0 and MD5Importer jME2.0 version 1.2.4 both Java6 and Java5 release.**

1. Fixed a bug in JointAnimation that causes the clamped animation to revert back to the beginning of the animation.

2. Added in scale with controller speed parameter in setFading(...) method.

**MD5Viewer jME2.0 version 1.2.0 Win32 and Mac release.**

1. Updated to MD5Importer jME2.0 version 1.2.4.

## 07/22/08 ##

**MD5Importer jME1.0 and MD5Importer jME2.0 version 1.2.3 both Java6 and Java5 release.**

1. Fixed a bug in JointController that causes jittering and other incorrect behavior when using animation fading feature.
2. Added method invocations in ModelNode clone() method to preserve jME Spatial information such as CullHint.
3. Added reset() method in JointAnimation that resets the animation to the starting frame.

## 07/07/08 ##

**MD5Importer jME1.0 and MD5Importer jME2.0 version 1.2.2 both Java6 and Java5 release.**

Fixed a bug in JointAnimation that prevents the animation from updating faster than one frame per update cycle.

## 06/19/08 ##

**MD5Importer jME1.0 and MD5Importer jME2.0 version 1.2.1 Java5 release.**

As requested by a number of users. I compiled Java5 version for both jME1.0 and jME2.0 releases.

## 06/11/08 ##

**MD5Importer jME1.0 and MD5Importer jME2.0 version 1.2.1 release.**

Fixed an animation jittering bug caused by the false calculations returned by JointAnimation.

## 06/10/08 ##

**MD5Importer jME1.0 and MD5Importer jME2.0 version 1.2.0 release.**

Version 1.2.0 added new functionality of fast cloning ModelNode and JointAnimation which allows users to quickly clone models and animations for multiple entities to use a game scene. This is extremely useful for typical MMO genre games. See tests under test.model.md5.clone package for detailed speed comparison.

## 06/09/08 ##

**MD5Importer jME1.0 and MDImporter jME2.0 quick fix.**

Hard-coded problem in version 1.1.0. The new texture loading process does not allow users to specify texture formats. Now the v1.1.1 version makes use of MultiFormatResourceLocator to allow user specify their own image formats. MD5Importer no longer provides any hard-coded image format extensions anymore.

**MD5Importer jME1.0 v1.1.0 and MD5Importer jME2.0 v1.1.0 released.**
  * Refined Savable implementation to allow Texture caching defined by user.
  * Updated tests to demonstrate binary export without saving texture image data.

## 05/28/08 ##

**MD5Viewer v1.1.1 released.**
  * Refined select file design.
  * Removed setting default directory which may hang the application.

## 05/27/08 ##

**MD5Viewer v1.1 released.**
  * Added action speed control.
  * Added global scale control.

## 05/21/2008 ##

**Added MD5Viewer for quick and easy viewing.**
  * Download the Zip file from "Downloads" section.

## 05/03/2008 ##

**Performance boost thanks to Momoko\_Fan.**
  * Created temporary variables in Vertex and Triangle classes to reduce garbage creation.

## 05/02/2008 ##

**MD5Importer jME1.0 v1.0.1 released**
  * Fixed cycle wrap mode implementation.
**MD5Importer jME2.0 v1.0.0 released**
  * Support for jME2.0 version.