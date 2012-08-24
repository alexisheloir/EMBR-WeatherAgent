/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.experiment;

import javax.jms.JMSException;

/**
 *
 * @author Max
 */
public class QuestionPosing {
    
    private Experiment exp;
    
    public QuestionPosing(Experiment exp) {
        this.exp = exp;
    }
    
    public void intro() throws JMSException {
        //create and send presentation
        String textString = "Let me pose some questions to you";
        String bmldoc = "";
        bmldoc = bmldoc.concat("<bml id=\"b5\">");
        bmldoc = bmldoc.concat("<speech id=\"s\">");
        bmldoc = bmldoc.concat("<text><sync id=\"0\" />" + textString + "</text>");
        bmldoc = bmldoc.concat("</speech>");
        bmldoc = bmldoc.concat("<face id=\"f0\" type=\"eyebrows\" stroke_start=\"s:0\" /></bml>");
        exp.sendBML(bmldoc);
    }
    
    public void question(String cityName) throws JMSException{
        String textString = "What was the weather in "+cityName+"?";
        String bmldoc = "";
        bmldoc = bmldoc.concat("<bml id=\"b6\">");
        bmldoc = bmldoc.concat("<speech id=\"s\">");
        bmldoc = bmldoc.concat("<text><sync id=\"0\" />" + textString + "</text>");
        bmldoc = bmldoc.concat("</speech>");
        bmldoc = bmldoc.concat("<face id=\"f0\" type=\"eyebrows\" stroke_start=\"s:0\" /></bml>");
        exp.sendBML(bmldoc);
    }
}
