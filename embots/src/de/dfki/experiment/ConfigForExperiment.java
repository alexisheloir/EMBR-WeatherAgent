/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dfki.experiment;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Administrator
 */
public class ConfigForExperiment {

    private static Properties properties = new Properties();

    // Path to the properties file
    private static String EXPERIMENT_CONFIG_FILE = "experiment_setup.conf";
    private static String actualconfigFilePath=System.getProperty("user.dir") + System.getProperty("file.separator") + EXPERIMENT_CONFIG_FILE;


    //are the properties file already loaded?
    private static boolean fileLoaded = false;

    /**
     * Get the value of a key
     * @param key
     * @return
     */
    public static String getProperties(String key) {

        if(!fileLoaded){
            try {
                properties.load(new FileInputStream(actualconfigFilePath));
                fileLoaded = true;
            } catch (Exception e) {
                throw new IllegalArgumentException("The Properties files could not be loaded. Please " +
                        "make sure that the classpath the file: "+actualconfigFilePath,e);
            }
        }
        return properties.getProperty(key);
    }

    public static void main(String args[]){

      String eapth=  ConfigForExperiment.getProperties("Experiment.Setup.Path");
      System.out.println(eapth);
    }

}
