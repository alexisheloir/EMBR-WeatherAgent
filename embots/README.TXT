************************************************************
THE EMBOTS SYSTEM: INSTALLTION AND STARTING

06 Sep 2010

DFKI, Embodied Agents Research Group
Contact: Michael Kipp, michael.kipp@dfki.de
         Alexis Heloir, alexis.heloir@dfki.de

************************************************************
INSTALLING EMBOTS
************************************************************

1) Download & install the EMBR avatar from

   http://embots.dfki.de/EMBR

2) Download & install ActiveMQ (e.g. version 5.4) from

   http://activemq.apache.org/download.html

3) Download & install MARY (version 4) from

   http://mary.dfki.de/Download

   For English speech synthesis, install the following voices:
   - Prudence
   - Poppy
   - Obadiah
   - Spike

   For Geman voice, install the bits3 mal voice (only one german voice
   is currently available)

4) in the bin directories, Modify the ".bat" (Windows) or ".sh"
   (Mac/Linux) files:

   - make MARYDIR point to your MARY installation

5) if needed, Buildind EMBOTS is easier under Netbeans

   - embots.jar should appear in a newly created /dist directory

************************************************************
RUNNING EMBOTS
************************************************************

2) Start EMBR

3) Windows:   Start one of the .bat files in /bin
   Mac/Linux: Start one of the .sh from a shell in /bin

************************************************************
PERFORMANCE
************************************************************

1) To speed up speech synthesis (MARY system), activate the cache:
   - go to the MARY installation directory
   - open conf/marybase.config
   - search for "cache" and set this to "true"
   - now every utterance that has been played before will be loaded from cache
   - also remember: every installed voice increases the initial start-up time
     and the amount of necessary working memory


************************************************************
JAVADOC / PROGRAMMING API
************************************************************

To create javadoc documentation, run the "javadoc" ANT target of the build.xml
file. In Netbeans you can do this by right-clicking on "build.xml" in the
file view, and choosing "javadoc" to run. The documentation will be build in the
directory: doc/javadoc
