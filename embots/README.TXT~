************************************************************
THE EMBOTS SYSTEM: INSTALLTION AND STARTING

06 Sep 2010

DFKI, Embodied Agents Research Group
Contact: Michael Kipp, michael.kipp@dfki.de

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

4) Modify "embots.bat" (Windows) or "embots.sh" (Mac/Linux):

   - make MARYDIR point to your MARY installation

5) Build EMBOTS, easiest under Netbeans

   - embots.jar should appear in a newly created /dist directory

************************************************************
RUNNING EMBOTS
************************************************************

1) Start ActiveMQ in a separate shell
   -> takes some time to get ready

2) Start EMBR

3) Windows:   Start embots.bat
   Mac/Linux: Start embots.sh from a shell

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