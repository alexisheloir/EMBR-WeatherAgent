package de.dfki.embots.embrscript;

/**
 *
 * @author Michael Kipp
 */
public class EMBRComment implements EMBRElement {

    public String comment = "";

    public void offset(long d) {}
    
    public EMBRComment(String comment) {
        this.comment = comment;
    }
    public String toScript() {
        return comment.length() > 0 ? "\n# " + comment : "";
    }
}
