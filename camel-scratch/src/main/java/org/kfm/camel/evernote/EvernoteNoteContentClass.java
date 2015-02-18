/**
 * 
 */
package org.kfm.camel.evernote;


/**
 * @author eevl
 *
 */

/*
 * Evernote ContentClass "mapping"
 * "livescribe.page.1"			==		original Evernote Content for Sky product launch
 * "livescribe.page.1_0_1"		==		"livescribe.page.1" + "cak=" in player launch url
 * "livescribe.page.1_0_2"		==		""livescribe.page.1_0_1" + evernote embedded UI Setting PNG and Logo Inactive Frost GIF hash and type as <en-media> element
 * 
 * "livescribe.session.1"		==		original Evernote Content for Sky product launch
 * "livescribe.session.1_0_1"	==		"livescribe.session.1" + "cak=" in player launch url
 * "livescribe.session.1_0_2"	==		"livescribe.session.1_0_1" + evernote embedded UI Setting PNG and Logo Inactive Frost GIF hash and type as <en-media> element
 */

/*
 * EDAM_NOTE_CONTENT_CLASS_LEN_MIN	i32	3
 * The minimum length of the content class attribute of a note.
 * EDAM_NOTE_CONTENT_CLASS_LEN_MAX	i32	32
 * The maximum length of the content class attribute of a note.
 */

public enum EvernoteNoteContentClass {

    PAGE_VERSION_1_0_0(1100, "livescribe.page.1"),
    PAGE_VERSION_1_0_1(1101, "livescribe.page.1_0_1"),
    PAGE_VERSION_1_0_2(1102, "livescribe.page.1_0_2"),
    SESSION_VERSION_1_0_0(2100, "livescribe.session.1"),
    SESSION_VERSION_1_0_1(2101, "livescribe.session.1_0_1"),
    SESSION_VERSION_1_0_2(2102, "livescribe.session.1_0_2");

    private Integer versionNum;
    private String label;

    EvernoteNoteContentClass(Integer versionNum, String label) {
        this.versionNum = versionNum;
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
    
    public Integer getVersion() {
        return this.versionNum;
    }
    
    public boolean labelEqualsIgnoreCase(String label) {
    	return this.label.equalsIgnoreCase(label);
    }

    public EvernoteNoteContentClass getEvernoteNoteContentClass(Integer versionNum) {
        for (EvernoteNoteContentClass evernoteNoteContentClass : EvernoteNoteContentClass.values())
            if (evernoteNoteContentClass.versionNum.equals(versionNum))
                return evernoteNoteContentClass;
        return null;
    }

    public static EvernoteNoteContentClass getEvernoteNoteContentClass(String label) {
        for (EvernoteNoteContentClass evernoteNoteContentClass : EvernoteNoteContentClass.values())
            if (evernoteNoteContentClass.label.equalsIgnoreCase(label))
                return evernoteNoteContentClass;
        return null;
    }
}
