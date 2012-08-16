
semaine.components = \
    |eu.semaine.components.meta.SystemManager| \
	|eu.semaine.components.mary.SpeechPreprocessor| \
    |eu.semaine.components.mary.SpeechBMLRealiser| \
    |eu.semaine.components.dummy.DummyVisualFML2BML| \
    |de.dfki.embots.framework.behavior.BMLRealizer| \
    |de.dfki.embots.modules.audio.EMBOTSAudioPlayer| \
    |de.dfki.embots.framework.behavior.GestureGenerator| \
    |de.dfki.embots.framework.connect.EMBRConnector| \
	|de.dfki.experiment.Experiment|

semaine.systemmanager.gui = true

semaine.systemmanager.ignorestalled = SpeechPreprocessor SpeechBMLRealiser
semaine.stateinfo-config = stateinfo.config
semaine.character-config = character-config.xml