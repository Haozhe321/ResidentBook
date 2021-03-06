package seedu.room.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Prefix definitions */
    public static final Prefix PREFIX_NAME = new Prefix("n/");
    public static final Prefix PREFIX_PHONE = new Prefix("p/");
    public static final Prefix PREFIX_EMAIL = new Prefix("e/");
    public static final Prefix PREFIX_ROOM = new Prefix("r/");
    public static final Prefix PREFIX_TAG = new Prefix("t/");
    public static final Prefix PREFIX_TEMPORARY = new Prefix("temp/");
    public static final Prefix PREFIX_TITLE = new Prefix("ti/");
    public static final Prefix PREFIX_DESCRIPTION = new Prefix("des/");
    public static final Prefix PREFIX_LOCATION = new Prefix("loc/");
    public static final Prefix PREFIX_DATETIME = new Prefix("time/");
    public static final Prefix PREFIX_IMAGE_URL = new Prefix("url/");
}
