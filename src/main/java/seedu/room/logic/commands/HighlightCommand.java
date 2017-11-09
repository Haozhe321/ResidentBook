package seedu.room.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.room.logic.commands.exceptions.CommandException;
import seedu.room.model.person.exceptions.NoneHighlightedException;
import seedu.room.model.person.exceptions.TagNotFoundException;

//@@author shitian007
/**
 * Adds a person to the address book.
 */
public class HighlightCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "highlight";
    public static final String COMMAND_ALIAS = "hl";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Highlights names with the specified tag. "
            + "Parameters: " + "tag."
            + "Example: " + COMMAND_WORD + " "
            + "friends";

    public static final String MESSAGE_RESET_HIGHLIGHT = "Removed all highlighted Residents.";
    public static final String MESSAGE_NONE_HIGHLIGHTED = "No Highlighted Residents.";

    public static final String MESSAGE_PERSONS_HIGHLIGHTED_SUCCESS = "Highlighted persons with tag: ";
    public static final String MESSAGE_TAG_NOT_FOUND = "Tag not found: ";

    private final String highlightTag;

    /**
     * Creates an HighlightCommand to add the specified {@code ReadOnlyPerson}
     */
    public HighlightCommand(String highlightTag) {
        this.highlightTag = highlightTag;
    }

    @Override
    protected CommandResult executeUndoableCommand() throws CommandException {
        requireNonNull(model);
        if (highlightTag.equals("-")) {
            try {
                model.resetHighlightStatus();
                return new CommandResult(MESSAGE_RESET_HIGHLIGHT);
            } catch (NoneHighlightedException e) {
                throw new CommandException(MESSAGE_NONE_HIGHLIGHTED);
            }
        } else {
            try {
                model.updateHighlightStatus(highlightTag);
                return new CommandResult(MESSAGE_PERSONS_HIGHLIGHTED_SUCCESS + highlightTag);
            } catch (TagNotFoundException e) {
                throw new CommandException(MESSAGE_TAG_NOT_FOUND + highlightTag);
            }
        }
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof HighlightCommand)) {
            return false;
        }

        // state check
        HighlightCommand hl = (HighlightCommand) other;
        return highlightTag.equals(hl.highlightTag);
    }
}
