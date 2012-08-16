package de.dfki.embots.bml.feedback;

/**
 * Represents a feedback message. Can be of the following types:
 * Status, Warning or Error. The feedback has the following components:
 * 
 * - type (status, warning or error)
 * - description (should be non-empty)
 * - reference to BML block that caused this feedback (optional)
 * - source (optional)
 *
 * @author Michael Kipp
 */
public class BMLFeedback
{

    private Type _type;
    private Subtype _subtype;
    private String _source = null; // optional
    private String _referenceToBmlBlock = null; // optional
    private String _description; // compulsory

    public enum Type
    {

        STATUS, WARNING, ERROR;
    }

    public enum Subtype
    {

        STARTED, FINISHED;
    }

    public BMLFeedback(Type type)
    {
        _type = type;
    }

    public BMLFeedback(Type type, String desc)
    {
        this(type);
        _description = desc;
    }

    public BMLFeedback(Type type, String source, String desc)
    {
        this(type, desc);
        _source = source;
    }

    public BMLFeedback(Type type, String source, String ref, String desc)
    {
        this(type, desc);
        _source = source;
        _referenceToBmlBlock = ref;
    }

    public BMLFeedback(Type type, Subtype subtype, String source,
            String ref)
    {
        this(type);
        _subtype = subtype;
        _source = source;
        _referenceToBmlBlock = ref;
    }

    /**
     * @return XML representation of the feedback message.
     */
    public String toXML()
    {
        StringBuilder sb = new StringBuilder("<" + _type.name());
        if (_subtype != null) {
            sb.append(" type=\"" + _subtype.name().toLowerCase() + "\"");
        }
        sb.append(">\n");
        if (_source != null) {
            sb.append("  <SOURCE>" + _source + "</SOURCE>\n");
        }
        if (_referenceToBmlBlock != null) {
            sb.append("  <BML-ID>" + _referenceToBmlBlock + "</BML-ID>\n");
        }
        if (_description != null) {
            sb.append("  <DESCRIPTION>" + _description + "</DESCRIPTION>\n");
        }
        sb.append("</" + _type.name() + ">");
        return sb.toString();
    }
}
