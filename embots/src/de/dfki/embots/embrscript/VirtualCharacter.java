package de.dfki.embots.embrscript;

/**
 * Characters to choose.
 *
 * @author Michael Kipp
 */
public enum VirtualCharacter
{
    AMBER("Amber", Gender.FEMALE),
    ALFONSE("Alfonse", Gender.MALE);

    public enum Gender {
        MALE, FEMALE;
    }

    private String _name;
    private Gender _gender;

    VirtualCharacter(String name, Gender gender) {
        _name = name;
        _gender = gender;
    }

    public String getName() {
        return _name;
    }

    public Gender getGender() {
        return _gender;
    }

    public boolean isMale() {
        return _gender.equals(Gender.MALE);
    }
    
    public static VirtualCharacter getDefault() {
        return ALFONSE;
    }
}
