package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.Optional;

import javafx.collections.ObservableList;
import seedu.address.MainApp;
import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;

/**
 * Import contacts from xml file.
 */
public class ImportCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "import";
    public static final String COMMAND_ALIAS = "i";
    public static final String MESSAGE_SUCCESS = "Import successful.";
    public static final String MESSAGE_ERROR = "Import error.";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds all persons in the XML file onto "
            + "the current address book.\n"
            + "Parameters: FILE_PATH \n"
            + "Example: " + COMMAND_WORD + " friendsContacts.xml";

    private String filePath;

    public ImportCommand(String filePath) {
        this.filePath = filePath;
    }

    @Override
    protected CommandResult executeUndoableCommand() throws CommandException {
        requireNonNull(model);
        try {
            Optional<ReadOnlyAddressBook> newContacts = MainApp.getBackup().readAddressBook(filePath);
            ReadOnlyAddressBook newList = newContacts.orElse(null);

            if (newList != null) {
                ObservableList<ReadOnlyPerson> personList = newList.getPersonList();

                for (ReadOnlyPerson p : personList) {
                    model.addPerson(p);
                }
            }
            return new CommandResult(String.format(MESSAGE_SUCCESS));
        } catch (DataConversionException e) {
            throw new CommandException(MESSAGE_ERROR);
        } catch (DuplicatePersonException e) {
            throw new CommandException(AddCommand.MESSAGE_DUPLICATE_PERSON);
        }catch (IOException e) {
            throw new CommandException(MESSAGE_ERROR);
        }
    }
}
