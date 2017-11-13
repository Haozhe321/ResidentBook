# shitian007
###### \java\seedu\room\logic\AutoCompleteTest.java
``` java
public class AutoCompleteTest {

    private AutoComplete autoComplete;
    private Model model = new ModelManager(getTypicalResidentBook(), new UserPrefs());

    @Before
    public void setUp() {
        autoComplete = new AutoComplete(model);
    }

    @Test
    public void assert_baseCommandsMatchUponCreation_success() {
        String[] baseCommands = autoComplete.getAutoCompleteList();
        assertTrue(Arrays.equals(AutoComplete.BASE_COMMANDS, baseCommands));
    }

    @Test
    public void assert_autoCompleteListNamesUpdate_success() {
        List<ReadOnlyPerson> residents = model.getFilteredPersonList();
        autoComplete.updateAutoCompleteList("find");
        String[] updatedList = autoComplete.getAutoCompleteList();

        for (int i = 0; i < residents.size(); i++) {
            String findPersonString = "find " + residents.get(i).getName().toString();
            assertTrue(findPersonString.equals(updatedList[i]));
        }
    }

    @Test
    public void assert_autoCompleteListIndexUpdate_success() {
        List<ReadOnlyPerson> residents = model.getFilteredPersonList();
        autoComplete.updateAutoCompleteList("edit");
        String[] updatedList = autoComplete.getAutoCompleteList();

        for (int i = 0; i < residents.size(); i++) {
            String editResidentIndex = "edit " + (i + 1);
            assertTrue(editResidentIndex.equals(updatedList[i]));
        }
    }

    @Test
    public void assert_resetAutoCompleteListMatchBaseCommands_success() {
        autoComplete.resetAutocompleteList();
        String[] baseCommands = autoComplete.getAutoCompleteList();
        assertTrue(Arrays.equals(AutoComplete.BASE_COMMANDS, baseCommands));
    }

    @Test
    public void assert_autoCompleteListResetOnEmptyStringInput_success() {
        autoComplete.updateAutoCompleteList("");
        String[] baseCommands = autoComplete.getAutoCompleteList();
        assertTrue(Arrays.equals(AutoComplete.BASE_COMMANDS, baseCommands));
    }
}
```
###### \java\seedu\room\logic\commands\AddCommandTest.java
``` java
        @Override
        public void updateFilteredPersonListPicture(Predicate<ReadOnlyPerson> predicate, Person editedPerson) {
            fail("This method should not be called.");
        }
```
###### \java\seedu\room\logic\commands\AddCommandTest.java
``` java
        @Override
        public void updateHighlightStatus(String highlightTag) throws TagNotFoundException {
            fail("This method should not be called.");
        }

        @Override
        public void resetHighlightStatus() throws NoneHighlightedException {
            fail("This method should not be called.");
        }
```
###### \java\seedu\room\logic\commands\AddImageCommandTest.java
``` java
public class AddImageCommandTest {

    private Model model = new ModelManager(getTypicalResidentBook(), new UserPrefs());

    @Test
    public void execute_imageUrlValid_success() throws Exception {
        final String validUrl = Picture.PLACEHOLDER_IMAGE;
        Person editedPerson = (Person) model.getFilteredPersonList().get(0);
        editedPerson.getPicture().setPictureUrl(validUrl);
        AddImageCommand addImageCommand = prepareCommand(INDEX_FIRST_PERSON, validUrl);

        String expectedMessage = String.format(AddImageCommand.MESSAGE_ADD_IMAGE_SUCCESS,
                editedPerson.getName().toString());

        Model expectedModel = new ModelManager(new ResidentBook(model.getResidentBook()), new UserPrefs());
        expectedModel.updatePerson(model.getFilteredPersonList().get(0), editedPerson);
        expectedModel.updateFilteredPersonListPicture(Model.PREDICATE_SHOW_ALL_PERSONS, editedPerson);

        assertCommandSuccess(addImageCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidPersonIndex_failure() throws Exception {
        final String validUrl = "Invalid Image Url";
        final Index invalidIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        Person editedPerson = (Person) model.getFilteredPersonList().get(0);
        AddImageCommand addImageCommand = prepareCommand(invalidIndex, validUrl);

        String expectedMessage = String.format(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX,
                editedPerson.getName().toString());

        assertCommandFailure(addImageCommand, model, expectedMessage);
    }

    @Test
    public void execute_invalidImageUrlValid_failure() throws Exception {
        final String validUrl = "Invalid Image Url";
        Person editedPerson = (Person) model.getFilteredPersonList().get(0);
        AddImageCommand addImageCommand = prepareCommand(INDEX_FIRST_PERSON, validUrl);

        String expectedMessage = String.format(Messages.MESSAGE_INVALID_IMAGE_URL,
                editedPerson.getName().toString());

        assertCommandFailure(addImageCommand, model, expectedMessage);
    }

    @Test
    public void equals() {
        Index index1 = Index.fromOneBased(1);
        Index index2 = Index.fromOneBased(2);
        String urlAlice = "/pictures/Alice.jpg";
        String urlBob = "/pictures/Bob.jpg";
        AddImageCommand addImageCommandAliceIndex1 = new AddImageCommand(index1, urlAlice);
        AddImageCommand addImageCommandAliceIndex1Duplicate = new AddImageCommand(index1, urlAlice);
        AddImageCommand addImageCommandAliceIndex2 = new AddImageCommand(index2, urlAlice);
        AddImageCommand addImageCommandBobIndex1 = new AddImageCommand(index1, urlBob);
        AddImageCommand addImageCommandBobIndex2 = new AddImageCommand(index2, urlBob);

        // same object -> returns true
        assertTrue(addImageCommandAliceIndex1.equals(addImageCommandAliceIndex1));

        // different object same arguments -> returns true
        assertTrue(addImageCommandAliceIndex1.equals(addImageCommandAliceIndex1Duplicate));

        // different indexes -> returns false
        assertFalse(addImageCommandAliceIndex1.equals(addImageCommandAliceIndex2));

        // different image url -> returns false
        assertFalse(addImageCommandAliceIndex1.equals(addImageCommandBobIndex1));

        // different image and index -> returns false
        assertFalse(addImageCommandAliceIndex1.equals(addImageCommandBobIndex2));

        // different object type -> returns false
        assertFalse(addImageCommandAliceIndex1.equals(index1));

        // null -> returns false
        assertFalse(addImageCommandAliceIndex1.equals(null));
    }

    /**
     * Returns an {@code AddImageCommand} with parameters {@code index} and {@code imageURL}
     */
    private AddImageCommand prepareCommand(Index index, String imageUrl) {
        AddImageCommand addImageCommand = new AddImageCommand(index, imageUrl);
        addImageCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        return addImageCommand;
    }
}
```
###### \java\seedu\room\logic\commands\DeleteImageCommandTest.java
``` java
public class DeleteImageCommandTest {

    private Model model = new ModelManager(getTypicalResidentBook(), new UserPrefs());

    @Test
    public void execute_validPersonIndex_success() throws Exception {
        Person editedPerson = (Person) model.getFilteredPersonList().get(0);
        editedPerson.getPicture().setPictureUrl(Picture.PLACEHOLDER_IMAGE);
        DeleteImageCommand deleteImageCommand = prepareCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteImageCommand.MESSAGE_RESET_IMAGE_SUCCESS,
                editedPerson.getName().toString());

        Model expectedModel = new ModelManager(new ResidentBook(model.getResidentBook()), new UserPrefs());
        expectedModel.updatePerson(model.getFilteredPersonList().get(0), editedPerson);
        expectedModel.updateFilteredPersonListPicture(Model.PREDICATE_SHOW_ALL_PERSONS, editedPerson);

        assertCommandSuccess(deleteImageCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidPersonIndex_failure() throws Exception {
        Person editedPerson = (Person) model.getFilteredPersonList().get(0);
        final Index invalidIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteImageCommand deleteImageCommand = prepareCommand(invalidIndex);

        String expectedMessage = String.format(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX,
                editedPerson.getName().toString());

        assertCommandFailure(deleteImageCommand, model, expectedMessage);
    }

    @Test
    public void equals() {
        Index index1 = Index.fromOneBased(1);
        Index index2 = Index.fromOneBased(2);
        DeleteImageCommand deleteImageIndex1 = new DeleteImageCommand(index1);
        DeleteImageCommand deleteImageIndex1Duplicate = new DeleteImageCommand(index1);
        DeleteImageCommand deleteImageIndex2 = new DeleteImageCommand(index2);

        // same object -> returns true
        assertTrue(deleteImageIndex1.equals(deleteImageIndex1));

        // different object same values -> returns true
        assertTrue(deleteImageIndex1.equals(deleteImageIndex1Duplicate));

        // different argument -> returns false
        assertFalse(deleteImageIndex1.equals(deleteImageIndex2));

        // different object type -> returns false
        assertFalse(deleteImageIndex1.equals(index1));

        // null -> returns false
        assertFalse(deleteImageIndex1.equals(null));
    }

    private DeleteImageCommand prepareCommand(Index index) {
        DeleteImageCommand deleteImageCommand = new DeleteImageCommand(index);
        deleteImageCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        return deleteImageCommand;
    }
}
```
###### \java\seedu\room\logic\commands\HighlightCommandTest.java
``` java
/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code SwaproomCommand}.
 */
public class HighlightCommandTest {

    private Model model = new ModelManager(getTypicalResidentBook(), new UserPrefs());

    @Test
    public void execute_validTag_success() throws Exception {
        List<Tag> listOfTags = model.getResidentBook().getTagList();
        String highlightTag = listOfTags.get(0).getTagName();
        HighlightCommand highlightCommand = prepareCommand(highlightTag);

        String expectedMessage = String.format(HighlightCommand.MESSAGE_PERSONS_HIGHLIGHTED_SUCCESS, highlightTag);

        ModelManager expectedModel = new ModelManager(model.getResidentBook(), new UserPrefs());
        expectedModel.updateHighlightStatus(highlightTag);

        assertCommandSuccess(highlightCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidTag_throwsCommandException() throws Exception {
        String nonExistentTag = getNonExistentTag();
        HighlightCommand highlightCommand = prepareCommand(nonExistentTag);

        String expectedMessage = String.format(HighlightCommand.MESSAGE_TAG_NOT_FOUND, nonExistentTag);
        assertCommandFailure(highlightCommand, model, expectedMessage);
    }

    @Test
    public void execute_noTag_throwsCommandException() {
        String emptyTag = "";
        HighlightCommand highlightCommand = prepareCommand(emptyTag);

        String expectedMessage = String.format(HighlightCommand.MESSAGE_TAG_NOT_FOUND, emptyTag);
        assertCommandFailure(highlightCommand, model, expectedMessage);
    }

    @Test
    public void execute_removeHighlight_success() {
        String removeHighlight = "-";
        HighlightCommand highlightCommand = prepareCommand(removeHighlight);

        String expectedMessage = HighlightCommand.MESSAGE_RESET_HIGHLIGHT;

        ModelManager expectedModel = new ModelManager(model.getResidentBook(), new UserPrefs());
        List<Tag> listOfTags = model.getResidentBook().getTagList();
        String highlightTag = listOfTags.get(0).getTagName();
        model.updateHighlightStatus(highlightTag);

        assertCommandSuccess(highlightCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_removeHighlightNoneHighlighted_throwsNoneHighlightedException() {
        String removeHighlight = "-";
        HighlightCommand highlightCommand = prepareCommand(removeHighlight);

        String expectedMessage = HighlightCommand.MESSAGE_NONE_HIGHLIGHTED;

        assertCommandFailure(highlightCommand, model, expectedMessage);
    }

    public String getNonExistentTag() {
        String nonExistentTag = "No such tag exists";
        try {
            List<Tag> listOfTags = model.getResidentBook().getTagList();
            while (listOfTags.contains(new Tag(nonExistentTag))) {
                System.out.println("while");
                nonExistentTag += nonExistentTag;
            }
            return nonExistentTag;
        } catch (IllegalValueException e) {
            System.out.println("");
        }
        return nonExistentTag;
    }

    @Test
    public void equals() {
        String tagRa = "RA";
        String tagExchange = "Exchange";
        HighlightCommand highlightCommandRa = new HighlightCommand(tagRa);
        HighlightCommand highlightCommandRaDuplicate = new HighlightCommand(tagRa);
        HighlightCommand highlightCommandExchange = new HighlightCommand(tagExchange);

        // same object -> returns true
        assertTrue(highlightCommandRa.equals(highlightCommandRa));

        // different object same values -> returns true
        assertTrue(highlightCommandRa.equals(highlightCommandRaDuplicate));

        // different argument -> returns false
        assertFalse(highlightCommandRa.equals(highlightCommandExchange));

        // different object type -> returns false
        assertFalse(highlightCommandRa.equals(tagRa));

        // null -> returns false
        assertFalse(highlightCommandRa.equals(null));
    }

    /**
     * Returns a {@code HighlightCommand} with the parameter {@code highlightTag}.
     */
    private HighlightCommand prepareCommand(String highlightTag) {
        HighlightCommand highlightCommand = new HighlightCommand(highlightTag);
        highlightCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        return highlightCommand;
    }
}

```
###### \java\seedu\room\logic\parser\AddImageCommandParserTest.java
``` java
public class AddImageCommandParserTest {

    private AddImageCommandParser parser = new AddImageCommandParser();

    @Test
    public void parse_allFieldsValid_success() {
        String validInput = " " + INDEX_FIRST_PERSON.getOneBased() + " url/" + Picture.PLACEHOLDER_IMAGE;
        AddImageCommand expectedCommand = new AddImageCommand(INDEX_FIRST_PERSON, Picture.PLACEHOLDER_IMAGE);

        assertParseSuccess(parser, validInput, expectedCommand);
    }

    @Test
    public void parse_indexNonInteger_failure() {
        String invalidIndexArgs = "one url/" + Picture.PLACEHOLDER_IMAGE;
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddImageCommand.MESSAGE_USAGE);

        assertParseFailure(parser, invalidIndexArgs, expectedMessage);
    }

    @Test
    public void execute_invalidImageFormat_failure() throws Exception {
        String invalidImageFormatUrl = " " + INDEX_FIRST_PERSON.getOneBased() + " url/"
            + Picture.PLACEHOLDER_IMAGE + "g";
        String expectedMessage = String.format(MESSAGE_INVALID_IMAGE_FORMAT,
            AddImageCommand.MESSAGE_VALID_IMAGE_FORMATS);

        assertParseFailure(parser, invalidImageFormatUrl, expectedMessage);
    }

    @Test
    public void parse_invalidArgNumber_failure() {
        String invalidArgs = "1";
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddImageCommand.MESSAGE_USAGE);

        assertParseFailure(parser, invalidArgs, expectedMessage);
    }
}
```
###### \java\seedu\room\logic\parser\DeleteImageCommandParserTest.java
``` java
public class DeleteImageCommandParserTest {

    private DeleteImageCommandParser parser = new DeleteImageCommandParser();

    @Test
    public void parse_validIndex_success() {
        String validInput = " " + INDEX_FIRST_PERSON.getOneBased();
        DeleteImageCommand expectedCommand = new DeleteImageCommand(INDEX_FIRST_PERSON);

        assertParseSuccess(parser, validInput, expectedCommand);
    }

    @Test
    public void parse_indexNonInteger_failure() {
        String invalidIndexArgs = "one ";
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteImageCommand.MESSAGE_USAGE);

        assertParseFailure(parser, invalidIndexArgs, expectedMessage);
    }

}

```
###### \java\seedu\room\logic\parser\HighlightCommandParserTest.java
``` java
public class HighlightCommandParserTest {

    private HighlightCommandParser parser = new HighlightCommandParser();

    @Test
    public void parse_validTag_success() {
        assertParseSuccess(parser, " RA", new HighlightCommand("RA"));
    }

    @Test
    public void parse_validUnhighlight_success() {
        assertParseSuccess(parser, " -", new HighlightCommand("-"));
    }

    @Test
    public void parse_invalidArgs_failure() {
        String emptyArg = "";
        assertParseFailure(parser, emptyArg,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, HighlightCommand.MESSAGE_USAGE));
    }

}
```
###### \java\seedu\room\model\ModelManagerTest.java
``` java
    @Test
    public void updatePersonPictureTest() throws IllegalValueException, PersonNotFoundException {
        ResidentBook residentBook = new ResidentBookBuilder().withPerson(TEMPORARY_JOE).build();
        UserPrefs userPrefs = new UserPrefs();
        ModelManager modelManager = new ModelManager(residentBook, userPrefs);

        //modelManager has nobody in it -> returns false
        assertFalse(modelManager.equals(null));

        Person editedPerson = (Person) modelManager.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        editedPerson.getPicture().setPictureUrl("TestUrl");

        modelManager.updateFilteredPersonListPicture(PREDICATE_SHOW_ALL_PERSONS, editedPerson);
        assertTrue(modelManager.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()).equals(editedPerson));

    }
```
