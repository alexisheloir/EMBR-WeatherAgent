/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dfki.tracker;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Administrator
 */
public class ConfigForETracker {

    private static Properties properties = new Properties();

    // Path to the properties file
    private static String ETRACKER_CONFIG_FILE = "etracker_setup.conf";
    private static String actualconfigFilePath=System.getProperty("user.dir") + System.getProperty("file.separator") + ETRACKER_CONFIG_FILE;


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

      String presentation_Mode=  ConfigForETracker.getProperties("ETracker.Presentation.Style");
      System.out.println(presentation_Mode);
    }

}