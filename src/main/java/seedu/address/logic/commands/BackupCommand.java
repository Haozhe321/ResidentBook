package seedu.address.logic.commands;

import seedu.address.MainApp;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * Create backup copy of address book.
 */
public class BackupCommand extends UndoableCommand {
    public static final String COMMAND_WORD = "backup";
    public static final String COMMAND_ALIAS = "b";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Creates backup copy of address book.";

    public static final String MESSAGE_SUCCESS = "New backup created";

    public static final String MESSAGE_ERROR = "Error creating backup";

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        try {
            MainApp.backup.backupAddressBook(model.getAddressBook());
            return new CommandResult(String.format(MESSAGE_SUCCESS));
        } catch (IOException e) {
            return new CommandResult(String.format(MESSAGE_ERROR) + e.getMessage());
        }

    }
}
