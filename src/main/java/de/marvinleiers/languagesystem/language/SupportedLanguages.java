package de.marvinleiers.languagesystem.language;

public enum SupportedLanguages
{
    ENGLISH("en"), GERMAN("de");

    private String lang;

    SupportedLanguages(String lang)
    {
        this.lang = lang;
    }

    public String getLanguage()
    {
        return lang;
    }
}
