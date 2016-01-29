# MD5Importer #

This importer imports MD5 format into Java Monkey Engine with its own skeletal animation system. It fully supports jME Savale interface.

## Updates ##
  * [05/11/2009](http://code.google.com/p/md5importer/wiki/Updates) MD5Importer Concurrent version 1.1 for jME2.0 release.
  * [05/10/2009](http://code.google.com/p/md5importer/wiki/Updates) MD5Importer Concurrent version 1.0 for jME2.0 release. MD5Importer for jME2.0 and jME1.0 version 1.3 release.
  * [08/07/2008](http://code.google.com/p/md5importer/wiki/Updates) MD5Importer jME1.0 and MD5Importer jME2.0 version 1.2.4 both Java6 and Java5 release.
  * [08/07/2008](http://code.google.com/p/md5importer/wiki/Updates) MDViewer jME2.0 version 1.2.0 release.
  * [07/22/2008](http://code.google.com/p/md5importer/wiki/Updates) MD5Importer jME1.0 and MD5Importer jME2.0 version 1.2.3 both Java6 and Java5 release.
  * [07/07/2008](http://code.google.com/p/md5importer/wiki/Updates) MD5Importer jME1.0 and MD5Importer jME2.0 version 1.2.2 both Java6 and Java5 release.
  * [06/19/2008](http://code.google.com/p/md5importer/wiki/Updates) MD5Importer jME1.0 and MD5Importer jME2.0 version 1.2.1 Java5 release.
  * [06/11/2008](http://code.google.com/p/md5importer/wiki/Updates) MD5Importer jME1.0 and MD5Importer jME2.0 version 1.2.1 release.
  * [06/10/2008](http://code.google.com/p/md5importer/wiki/Updates) MD5Importer jME1.0 and MD5Importer jME2.0 version 1.2.0 release.
  * [06/09/2008](http://code.google.com/p/md5importer/wiki/Updates) MD5Importer jME1.0 and MD5Importer jME2.0 quick fix.
  * [06/09/2008](http://code.google.com/p/md5importer/wiki/Updates) MD5Importer jME1.0 v1.1.0 and MD5Importer jME2.0 v1.1.0 released
  * [05/28/2008](http://code.google.com/p/md5importer/wiki/Updates) MD5Viewer v1.1.1 released
  * [05/27/2008](http://code.google.com/p/md5importer/wiki/Updates) MD5Viewer v1.1 released
  * [05/21/2008](http://code.google.com/p/md5importer/wiki/Updates) MD5Viewer released
  * [05/03/2008](http://code.google.com/p/md5importer/wiki/Updates) Performance boost
  * [05/02/2008](http://code.google.com/p/md5importer/wiki/Updates) MD5Importer jME1.0 v1.0.1 and MD5Importer jME2.0 v1.0.0 released

## Current Versions ##

  * [MD5Importer Concurrent jME2.0 - v1.1](http://md5importer.googlecode.com/files/MD5Importer-Concurrent-1.1.jar)
  * [MD5Importer jME2.0 - v1.3](http://md5importer.googlecode.com/files/MD5Importer-jME2.0-v1.3.jar)
  * [MD5Importer jME1.0 - v1.3](http://md5importer.googlecode.com/files/MD5Importer-jME1.0-v1.3.jar)

**Please check out the source code from svn for the lastest version and example tests.**

## SVN URL ##

  * MD5Importer Concurrent https://md5importer.googlecode.com/svn/trunk/md5importer-concurrent
  * MD5Importer jME2.0 https://md5importer.googlecode.com/svn/trunk/md5importer-jME2.0
  * MD5Importer jME1.0 https://md5importer.googlecode.com/svn/trunk/md5importer-jME1.0

## Current Features ##

**1. Concurrent update** - Allows the animation and mesh geometry update process to be performed in a separate thread from the rendering thread to enhance performance and more importantly scalability. Assuming there is enough computing power available, executing multiple character updates cost roughly the same as executing a single character update.

**2. Skeletal animation**

**3. Texturing** - Basic texturing using jME ResourceLocatorTool.

**4. Animation fading** - Allows smooth fading between two animations.

**5. jME Savable** - Allows saving loaded model into binary format for distribution.

**6. Dependent child** - Allows a loaded ModelNode to share its parent's skeleton during animation. This allows artists to create one single skeleton and skin meshes such as changable armor pieces on it. When export the these armor pieces, you do not have to worry about seperating the skeleton at all. All you need to do is export each piece of armor with the entire skeleton structure all together. Then after loading that armor piece, use attachDependent(ModelNode) method to add the newly loaded piece to an existing piece (chestArmor.attachDependent(headArmor)).

**7. Animation speed control** - Allowing you to directly control the speed of the animation. This can be useful when you want to play your animation in slow motion or fast motion mode. Think of Matrix

**8. Animation repeat type** - Inherit jME controller's repeat types. This includes wrap, clam and cycle modes.

**9. Fast cloning** - Allows fast cloning of ModelNode and JointAnimation. This allows users to clone models and animations in a matter of milliseconds for multiple entities use. Fast cloning is about 10 times faster than jME CloneImportExport.

**10. Normal sharing** - Allows vertices with same geometric location to share the same normal vector.

**11. Special texture extension support** - Allows user to specify texture extension to be used when loading binary exported mesh.

## Known issues ##

None.

## Discussion ##

[Discussion here](http://www.jmonkeyengine.com/jmeforum/index.php?topic=7323.0)