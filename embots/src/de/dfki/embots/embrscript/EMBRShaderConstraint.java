package de.dfki.embots.embrscript;

/**
 * 
 * @author Oliver Schoenleben
 * @author Michael Kipp
 */
public class EMBRShaderConstraint extends EMBRConstraint {

    public EMBRShaderKey key;
    public double shade; // ~ this could/should be renamed to "value"

    public EMBRShaderConstraint(EMBRShaderKey key, double shade) {
        this.key = key;
        this.shade = shade;
    }

    public EMBRShaderConstraint() {
        // This constructor intentionally left blank
    }

    public String toScript() {
        return "  BEGIN SHADER" + // ~ real exact name in EMBRScript?
                "\n    SHADER_KEY:" + key.toScript() +
                "\n    SHADER_VALUE:" + shade +
                "\n  END\n";
    }
}
