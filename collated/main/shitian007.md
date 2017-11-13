# shitian007
###### \java\seedu\room\logic\AutoComplete.java
``` java
/**
 * AutoComplete class integrated into {@code Logic} to keep track of current set
 * of autocomplete suggestions according to user input
 */
public class AutoComplete {

    public static final String[] BASE_COMMANDS = { "add", "addEvent", "addImage", "backup", "edit", "select", "delete",
        "deleteByTag", "deleteEvent", "deleteImage", "deleteTag", "clear", "find", "list", "highlight", "history",
        "import", "exit", "help", "undo", "redo", "sort", "swaproom", "switch", "prev", "next"
    };
    private ArrayList<String> personsStringArray;
    private String[] autoCompleteList;
    private Model model;

    public AutoComplete(Model model) {
        this.model = model;
        autoCompleteList = BASE_COMMANDS;
        personsStringArray = new ArrayList<String>();
        this.updatePersonsArray();
    }

    /**
     * Updates AutoComplete suggestions according to user typed input
     * @param userInput determines suggestions
     */
    public void updateAutoCompleteList(String userInput) {
        switch (userInput) {
        case "":
            this.resetAutocompleteList();
            break;
        case "find":
            this.autoCompleteList = getConcatResidentsArray("find");
            break;
        case "edit":
            this.autoCompleteList = getConcatResidentsArray("edit");
            break;
        case "delete":
            this.autoCompleteList = getConcatResidentsArray("delete");
            break;
        case "select":
            this.autoCompleteList = getConcatResidentsArray("select");
            break;
        case "addImage":
            this.autoCompleteList = getConcatResidentsArray("addImage");
            break;
        case "deleteImage":
            this.autoCompleteList = getConcatResidentsArray("deleteImage");
            break;
        default:
            return;
        }
    }

    /**
     * @param command typed in by the user
     * @return String array of suggestions with the index/name of the list of the displayed residents appended
     */
    private String[] getConcatResidentsArray(String command) {
        String[] newAutoCompleteList = new String[personsStringArray.size()];
        for (int i = 0; i < personsStringArray.size(); i++) {
            if (command.equals("find")) {
                newAutoCompleteList[i] = command + " " + personsStringArray.get(i);
            } else {
                newAutoCompleteList[i] = command + " " + (i + 1);
            }
        }
        return newAutoCompleteList;
    }

    /**
     * Reset {@code autoCompleteList} list to base commands
     */
    public void resetAutocompleteList() {
        this.autoCompleteList = BASE_COMMANDS;
    }

    /**
     * Update {@code personStringArray} when list in {@code Model} model modified
     */
    public void updatePersonsArray() {
        personsStringArray.clear();
        for (ReadOnlyPerson resident: model.getFilteredPersonList()) {
            personsStringArray.add(resident.getName().toString());
        }
    }

    /**
     * @return Last updated array of suggestions
     */
    public String[] getAutoCompleteList() {
        return autoCompleteList;
    }
}
```
###### \java\seedu\room\logic\commands\AddImageCommand.java
``` java
/**
 * Command handling the addition of an image to a resident currently in the resident book
 */
public class AddImageCommand extends UndoableCommand {
    public static final String COMMAND_WORD = "addImage";
    public static final String COMMAND_ALIAS = "ai";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds an image to the resident identified "
            + "by the index number used in the last resident listing. "
            + "Existing Image will be replaced by the new image.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "url/[ Image Url ]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + "url//Users/username/Downloads/person-placeholder.jpg";
    public static final String MESSAGE_VALID_IMAGE_FORMATS = "Allowed formats: JPG/JPEG/PNG/BMP";

    public static final String MESSAGE_ADD_IMAGE_SUCCESS = "Successfully changed image for Resident: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This resident already exists in the resident book.";

    private final Index index;
    private final String newImageUrl;

    /**
     * @param index of the resident {@code Person} in the current displayed list whose image is to be updated
     * @param newImageUrl url to the new replacing image
     */
    public AddImageCommand(Index index, String newImageUrl) {
        requireNonNull(index);

        this.index = index;
        this.newImageUrl = newImageUrl;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        if (!(new File(newImageUrl).exists())) {
            throw new CommandException(Messages.MESSAGE_INVALID_IMAGE_URL);
        }

        ReadOnlyPerson resident = lastShownList.get(index.getZeroBased());
        Person editedPerson = editResidentImage(resident);
        createResidentImage(editedPerson);

        try {
            model.updatePerson(resident, editedPerson);
        } catch (DuplicatePersonException dpe) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("The target resident cannot be missing");
        }
        model.updateFilteredPersonListPicture(PREDICATE_SHOW_ALL_PERSONS, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_ADD_IMAGE_SUCCESS, editedPerson.getName()));
    }

    /**
     * Updates the image url for the specified resident
     * @param resident to whose {@code Picture} is to be edited
     * @return {@code Person} with updated {@code Picture}
     */
    public Person editResidentImage(ReadOnlyPerson resident) {
        Name name = resident.getName();
        Phone phone = resident.getPhone();
        Email email = resident.getEmail();
        Room room = resident.getRoom();
        Timestamp timestamp = resident.getTimestamp();
        Set<Tag> tags = resident.getTags();

        Person editedPerson =  new Person(name, phone, email, room, timestamp, tags);
        if (checkJarResourcePath(resident)) {
            editedPerson.getPicture().setJarResourcePath();
        }

        editedPerson.getPicture().setPictureUrl(name.toString() + phone.toString() + ".jpg");
        return editedPerson;
    }

    /**
     * @param resident whose image is to be checked
     * @return true if in production mode (jar file)
     */
    public boolean checkJarResourcePath(ReadOnlyPerson resident) {
        File picture = new File(resident.getPicture().getPictureUrl());
        return (picture.exists()) ? false : true;
    }

    /**
     * @param resident whose attributes would be used to generate image file
     */
    public void createResidentImage(ReadOnlyPerson resident) {
        File picFile = new File(newImageUrl);
        try {
            if (resident.getPicture().checkJarResourcePath()) {
                ImageIO.write(ImageIO.read(picFile), "jpg", new File(resident.getPicture().getJarPictureUrl()));
            } else {
                ImageIO.write(ImageIO.read(picFile), "jpg", new File(resident.getPicture().getPictureUrl()));
            }
        } catch (Exception e) {
            System.out.println("Cannot create Person Image");
        }
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddImageCommand)) {
            return false;
        }

        // state check
        AddImageCommand ai = (AddImageCommand) other;
        return index.equals(ai.index) && newImageUrl.equals(ai.newImageUrl);
    }

}
```
###### \java\seedu\room\logic\commands\DeleteImageCommand.java
``` java
/**
 * Allows deletion of an image for a specified resident
 */
public class DeleteImageCommand extends UndoableCommand {
    public static final String COMMAND_WORD = "deleteImage";
    public static final String COMMAND_ALIAS = "di";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds an image to the resident identified "
            + "by the index number used in the last resident listing. "
            + "Existing Image will be reset to placeholder image.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "Example: " + COMMAND_WORD + " 3 ";

    public static final String MESSAGE_RESET_IMAGE_SUCCESS = "Successfully deleted image for Resident: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This resident already exists in the resident book.";

    private final Index index;

    /**
     * @param index of the resident {@code Person} in the list whose image is to be deleted
     */
    public DeleteImageCommand(Index index) {
        requireNonNull(index);
        this.index = index;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        ReadOnlyPerson resident = lastShownList.get(index.getZeroBased());
        Person editedResident = resetPersonImage(resident);

        try {
            model.updatePerson(resident, editedResident);
        } catch (DuplicatePersonException dpe) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("The target resident cannot be missing");
        }
        model.updateFilteredPersonListPicture(PREDICATE_SHOW_ALL_PERSONS, editedResident);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_RESET_IMAGE_SUCCESS, editedResident.getName()));
    }

    /**
     * @param resident whose image url within {@code Picture} is to be reset
     * @return {@code Person} with {@code Picture} reset
     */
    public Person resetPersonImage(ReadOnlyPerson resident) {
        Name name = resident.getName();
        Phone phone = resident.getPhone();
        Email email = resident.getEmail();
        Room room = resident.getRoom();
        Timestamp timestamp = resident.getTimestamp();
        Set<Tag> tags = resident.getTags();

        Person editedResident =  new Person(name, phone, email, room, timestamp, tags);
        return editedResident;
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteImageCommand)) {
            return false;
        }

        // state check
        DeleteImageCommand ai = (DeleteImageCommand) other;
        return index.equals(ai.index);
    }
}
```
###### \java\seedu\room\logic\commands\HighlightCommand.java
``` java
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

    public static final String MESSAGE_RESET_HIGHLIGHT = "Removed all highlights on Residents.";
    public static final String MESSAGE_NONE_HIGHLIGHTED = "No Highlighted Residents.";

    public static final String MESSAGE_PERSONS_HIGHLIGHTED_SUCCESS = "Highlighted residents with tag: %1$s";
    public static final String MESSAGE_TAG_NOT_FOUND = "Tag not found: %1$s";

    private final String highlightTag;

    /**
     * Creates a HighlightCommand to highlight the specified list of residents {@code Person}
     * @param highlightTag specified to determine which residents are highlighted
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
                return new CommandResult(String.format(MESSAGE_PERSONS_HIGHLIGHTED_SUCCESS, highlightTag));
            } catch (TagNotFoundException e) {
                throw new CommandException(String.format(MESSAGE_TAG_NOT_FOUND, highlightTag));
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
```
###### \java\seedu\room\logic\Logic.java
``` java
    /**
     * Updates Picture of resident {@code Person} within model
     * @param resident whose picture is to be updated
     */
    void updatePersonListPicture(Person resident);

    /**
     * Updates autocomplete list within {@code Logic}
     * @param userInput
     */
    void updateAutoCompleteList(String userInput);

    /**
     * @return the latest array of suggestions from {@code Logic}
     */
    String[] getAutoCompleteList();
```
###### \java\seedu\room\logic\LogicManager.java
``` java
    @Override
    public void updateAutoCompleteList(String userInput) {
        autoCompleteList.updateAutoCompleteList(userInput);
    }

    @Override
    public String[] getAutoCompleteList() {
        return autoCompleteList.getAutoCompleteList();
    }

    @Override
    public void updatePersonListPicture(Person person) {
        model.updateFilteredPersonListPicture(PREDICATE_SHOW_ALL_PERSONS, person);
    }
```
###### \java\seedu\room\logic\parser\AddImageCommandParser.java
``` java
/**
 * Parses the given {@code String} of arguments in the context of the AddImageCommand
 * and returns an AddImageCommand object for execution.
 * @throws InvalidImageFormatException if user input url does not specify a valid image format
 * @throws ParseException if the user input does not conform to the expected command format
 */
public class AddImageCommandParser implements Parser<AddImageCommand> {
    @Override
    public AddImageCommand parse(String args) throws ParseException {
        requireNonNull(args);

        Index index;
        String url;
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_IMAGE_URL);

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
            url = validImageFormat(argMultimap.getValue(PREFIX_IMAGE_URL).get());
        } catch (InvalidImageFormatException e) {
            throw new ParseException(String.format(MESSAGE_INVALID_IMAGE_FORMAT,
                AddImageCommand.MESSAGE_VALID_IMAGE_FORMATS));
        } catch (Exception e) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddImageCommand.MESSAGE_USAGE));
        }
        return new AddImageCommand(index, url);
    }

    /**
     * @param imageUrl actual image url
     * @return image url if it is of the valid format
     * @throws InvalidImageFormatException if image format is invalid
     */
    public String validImageFormat(String imageUrl) throws InvalidImageFormatException {
        String validFormatRegex = "(.+(\\.(?i)(jpg|jpeg|png|bmp))$)";
        if (imageUrl.matches(validFormatRegex)) {
            return imageUrl;
        } else {
            throw new InvalidImageFormatException(MESSAGE_INVALID_COMMAND_FORMAT);
        }
    }
}
```
###### \java\seedu\room\logic\parser\DeleteImageCommandParser.java
``` java
/**
 * Parses the given {@code String} of arguments in the context of the DeleteImageCommand
 * and returns an DeleteImageCommand object for execution.
 * @throws ParseException if the user input does not conform the expected format
 */
public class DeleteImageCommandParser implements Parser<DeleteImageCommand> {
    @Override
    public DeleteImageCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new DeleteImageCommand(index);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteImageCommand.MESSAGE_USAGE));
        }
    }
}
```
###### \java\seedu\room\logic\parser\HighlightCommandParser.java
``` java
/**
 * Parses input arguments and creates a new HighlightCommand object
 */
public class HighlightCommandParser implements Parser<HighlightCommand> {

    public final String unhighlightArg = "-";
    /**
     * Parses the given {@code String} of arguments in the context of the HighlightCommand
     * and returns an HighlightCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public HighlightCommand parse(String args) throws ParseException {
        String highlightTag = args.trim();
        if (validTag(highlightTag) || highlightTag.equals(unhighlightArg)) {
            return new HighlightCommand(highlightTag);
        } else {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HighlightCommand.MESSAGE_USAGE));
        }
    }

    private boolean validTag(String highlightTag) {
        return !highlightTag.isEmpty();
    }
}

```
###### \java\seedu\room\logic\parser\ResidentBookParser.java
``` java
        case HighlightCommand.COMMAND_WORD:
        case HighlightCommand.COMMAND_ALIAS:
            return new HighlightCommandParser().parse(arguments);
```
###### \java\seedu\room\model\Model.java
``` java
    /**
     * Updates the highlight status of persons with the specified tag
     * @throws TagNotFoundException if input tag name does not exist
     */
    void updateHighlightStatus(String highlightTag) throws TagNotFoundException;

    /**
     * Removes all highlighting of all persons in the
     * @throws NoneHighlightedException
     */
    void resetHighlightStatus() throws NoneHighlightedException;

```
###### \java\seedu\room\model\ModelManager.java
``` java
    @Override
    public void updateFilteredPersonListPicture(Predicate<ReadOnlyPerson> predicate, Person person) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
        for (ReadOnlyPerson p : filteredPersons) {
            if (p.getName().toString().equals(person.getName().toString())
                    && p.getPhone().toString().equals(person.getPhone().toString())) {
                p.getPicture().setPictureUrl(person.getPicture().getPictureUrl());
            }
        }
        indicateResidentBookChanged();
    }
```
###### \java\seedu\room\model\ModelManager.java
``` java
    /**
     * Updates the highlight status of a resident if tag matches input tag
     */
    public void updateHighlightStatus(String highlightTag) throws TagNotFoundException  {
        residentBook.updateHighlightStatus(highlightTag);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        indicateResidentBookChanged();
    }

    /**
     * Removes the highlight status of all residents
     */
    public void resetHighlightStatus() throws NoneHighlightedException {
        residentBook.resetHighlightStatus();
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        indicateResidentBookChanged();
    }
```
###### \java\seedu\room\model\person\exceptions\NoneHighlightedException.java
``` java
/**
 * Signals that there is no resident currently highlighted
 */
public class NoneHighlightedException extends IllegalArgumentException {
    public NoneHighlightedException(String message) {
        super(message);
    }
}
```
###### \java\seedu\room\model\person\exceptions\TagNotFoundException.java
``` java
/**
 * Signals that the tag name specified in the operation does not exist in resident book
 */
public class TagNotFoundException extends IllegalArgumentException {
    public TagNotFoundException(String message) {
        super(message);
    }
}

```
###### \java\seedu\room\model\person\Person.java
``` java
    /**
     * @param replacement Set of tags to replace the person's current set of tags
     */
    public void setTags(Set<Tag> replacement) {
        tags.set(new UniqueTagList(replacement));
    }

    // Setter for person's highlightStatus status
    public void setHighlightStatus(boolean val) {
        this.highlightStatus = val;
    }

    // Getter for person's highlightStatus status
    public boolean getHighlightStatus() {
        return this.highlightStatus;
    }
```
###### \java\seedu\room\model\person\Picture.java
``` java
/**
 * Represents the picture object of the resident {@code Person} in the resident book.
 */
public class Picture {

    public static final int PIC_WIDTH = 100;
    public static final int PIC_HEIGHT = 100;

    public static final String FOLDER_NAME = "contact_images";

    public static final String BASE_URL = System.getProperty("user.dir")
            + "/data/" + FOLDER_NAME + "/";

    public static final String PLACEHOLDER_IMAGE = System.getProperty("user.dir")
            + "/src/main/resources/images/placeholder_person.png";

    public static final String BASE_JAR_URL = System.getProperty("user.dir") + "/data/contact_images/";

    public static final String PLACEHOLDER_JAR_URL = "/images/placeholder_person.png";

    private String pictureUrl;
    private String jarPictureUrl;
    private boolean jarResourcePath;

    public Picture() {
        this.pictureUrl = PLACEHOLDER_IMAGE;
        this.jarPictureUrl = PLACEHOLDER_JAR_URL;
        this.jarResourcePath = false;
    }

    /**
     * @return image resource {@code pictureUrl} for development
     */
    public String getPictureUrl() {
        return pictureUrl;
    }

    /**
     * @return image resource {@code jarPictureUrl} for production (jar)
     */
    public String getJarPictureUrl() {
        return jarPictureUrl;
    }

    /**
     * Sets jar {@code jarResourcePath} path to true
     */
    public void setJarResourcePath() {
        this.jarResourcePath = true;
    }

    /**
     * Checks if image is to be retrieved in jar
     */
    public boolean checkJarResourcePath() {
        return this.jarResourcePath;
    }

    /**
     * Sets name of image which will be appended to contact_images directory
     * @param pictureUrl set as resource path for both dev and production
     */
    public void setPictureUrl(String pictureUrl) {
        if (pictureUrl.contains("/")) {
            String[] splitStrings = pictureUrl.split("/");
            String pictureName = splitStrings[splitStrings.length - 1];
            this.pictureUrl = BASE_URL + pictureName;
            this.jarPictureUrl = BASE_JAR_URL + pictureName;
        } else {
            this.pictureUrl = BASE_URL + pictureUrl;
            this.jarPictureUrl = BASE_JAR_URL + pictureUrl;
        }
    }

    /**
     * Resets {@code pictureUrl} and {@code jarPictureUrl} of {@code Person} to original placeholder url
     */
    public void resetPictureUrl() {
        this.pictureUrl = PLACEHOLDER_IMAGE;
        this.jarPictureUrl = PLACEHOLDER_JAR_URL;
    }

}
```
###### \java\seedu\room\model\person\ReadOnlyPerson.java
``` java
    ObjectProperty<Picture> pictureProperty();
    Picture getPicture();
```
###### \java\seedu\room\model\person\UniquePersonList.java
``` java
    /**
     * Updates the highlight status of the persons with the specified tag
     */
    public void updateHighlightStatus(String highlightTag) throws TagNotFoundException {
        resetHighlightStatusHelper();
        boolean tagFound = false;
        for (Person person : this.internalList) {
            for (Tag t : person.getTags()) {
                if (t.getTagName().equals(highlightTag)) {
                    tagFound = true;
                    person.setHighlightStatus(true);
                }
            }
        }
        if (!tagFound) {
            throw new TagNotFoundException("No Such Tag Exists");
        }
    }

    /**
     * Removes highlighting of everyone
     */
    public void resetHighlightStatus() throws NoneHighlightedException {
        boolean highlightReset = resetHighlightStatusHelper();
        if (!highlightReset) {
            throw new NoneHighlightedException("No Residents Highlighted");
        }
    }

    /**
     * @return true if at least one resident's highlight status has been reset
     */
    public boolean resetHighlightStatusHelper() {
        boolean highlightReset = false;
        for (Person person : this.internalList) {
            if (person.getHighlightStatus()) {
                person.setHighlightStatus(false);
                highlightReset = true;
            }
        }
        return highlightReset;
    }

```
###### \java\seedu\room\model\ResidentBook.java
``` java
    /**
     * Updates highlight status of person with specified tag
     */
    public void updateHighlightStatus(String highlightTag) throws TagNotFoundException {
        try {
            if (!this.tags.contains(new Tag(highlightTag))) {
                throw new TagNotFoundException("No such Tag Exists");
            } else {
                persons.updateHighlightStatus(highlightTag);
            }
        } catch (IllegalValueException e) {
            throw new TagNotFoundException("No such Tag Exists");
        }
    }

    /**
     * Removes highlight status of all persons
     */
    public void resetHighlightStatus() throws NoneHighlightedException {
        persons.resetHighlightStatus();
    }
```
###### \java\seedu\room\model\tag\Tag.java
``` java
    // Getter for tagColor
    public String getTagColor() {
        return this.tagColor;
    }

    // Setter for tagColor
    public void setTagColor(String color) {
        this.tagColor = color;
    }
```
###### \java\seedu\room\ui\CommandBox.java
``` java
    /**
     * Initializes suggestions and binds it to {@code commandTextField}
     */
    public void initAutoComplete() {
        suggestions = SuggestionProvider.create((Arrays.asList(logic.getAutoCompleteList())));
        TextFields.bindAutoCompletion(commandTextField, suggestions);
    }

    /**
     * Updates AutoCompleteList according to current {@code commandTextField} input
     */
    public void updateAutoCompleteList() {
        logic.updateAutoCompleteList(commandTextField.getText());
        suggestions.clearSuggestions();
        suggestions.addPossibleSuggestions(Arrays.asList(logic.getAutoCompleteList()));
    }
```
###### \java\seedu\room\ui\MainWindow.java
``` java
        PersonPanel personPanel = new PersonPanel(logic);
        personPanelPlaceholder.getChildren().add(personPanel.getRoot());
```
###### \java\seedu\room\ui\PersonPanel.java
``` java
/**
 * An UI Component that displays additional information for a selected {@code Person}
 */
public class PersonPanel extends UiPart<Region> {

    private static final String FXML = "PersonPanel.fxml";

    private final Logger logger = LogsCenter.getLogger(this.getClass());
    private final Logic logic;

    private ReadOnlyPerson resident;

    @FXML
    private ImageView picture;
    @FXML
    private VBox informationPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private FlowPane tags;
    @FXML
    private Button addImageButton;
    @FXML
    private Button resetImageButton;

    public PersonPanel(Logic logic) {
        super(FXML);
        this.logic = logic;
        loadDefaultScreen();
        registerAsAnEventHandler(this);
    }

    /**
     * Sets the default parameters when the app starts up and no one is selected
     */
    private void loadDefaultScreen() {
        name.textProperty().setValue("No Resident Selected");
        phone.textProperty().setValue("-");
        address.textProperty().setValue("-");
        email.textProperty().setValue("-");
        enableButtons(false);
    }

    /**
     * Loads the properties of the selected {@code Person}
     * This method is called whenever an update is made to the selected resident or a new resident is selected
     * @param resident whose properties are updated in the Person Panel UI
     */
    private void loadPersonInformation(ReadOnlyPerson resident) {
        this.resident = updatePersonFromLogic(resident);
        name.textProperty().setValue(resident.getName().toString());
        phone.textProperty().setValue(resident.getPhone().toString());
        address.textProperty().setValue(resident.getRoom().toString());
        email.textProperty().setValue(resident.getEmail().toString());
        initTags();
        initImage();
        enableButtons(true);
    }

    /**
     * @param state of button set
     */
    private void enableButtons(boolean state) {
        this.addImageButton.setDisable(!state);
        this.resetImageButton.setDisable(!state);
    }

    /**
     * @param resident whose image is to be updated within the filtered persons list
     * @return a {@code Person} that is the updated resident
     */
    private ReadOnlyPerson updatePersonFromLogic(ReadOnlyPerson resident) {
        List<ReadOnlyPerson> personList = logic.getFilteredPersonList();
        for (ReadOnlyPerson p : personList) {
            if (p.getName().toString().equals(resident.getName().toString())
                    && p.getPhone().toString().equals(resident.getPhone().toString())) {
                return p;
            }
        }
        return null;
    }

    /**
     * Sets a background color for each tag of the selected {@code Person}
     */
    private void initTags() {
        tags.getChildren().clear();
        resident.getTags().forEach(tag -> {
            Label tagLabel = new Label(tag.tagName);
            tagLabel.setStyle("-fx-background-color: " + tag.getTagColor());
            tags.getChildren().add(tagLabel);
        });
    }

    /**
     * Initializes image for the selected resident {@code Person}
     */
    private void initImage() {
        try {
            initProjectImage();
        } catch (Exception pfnfe) {
            try {
                initJarImage();
            } catch (Exception jfnfe) {
                ;
            }
        }
        picture.setFitHeight(resident.getPicture().PIC_HEIGHT);
        picture.setFitWidth(resident.getPicture().PIC_WIDTH);
    }

    /**
     * GUI Button handler for adding image to resident
     */
    @FXML
    private void handleAddImage() {
        FileChooser picChooser = new FileChooser();
        File selectedPic = picChooser.showOpenDialog(null);
        if (selectedPic != null) {
            try {
                resident.getPicture().setPictureUrl(resident.getName().toString()
                    + resident.getPhone().toString() + ".jpg");
                logic.updatePersonListPicture((Person) resident);
                FileInputStream fileStream;
                if (resident.getPicture().checkJarResourcePath()) {
                    ImageIO.write(ImageIO.read(selectedPic), "jpg",
                        new File(resident.getPicture().getJarPictureUrl()));
                    fileStream = new FileInputStream(resident.getPicture().getJarPictureUrl());
                } else {
                    ImageIO.write(ImageIO.read(selectedPic), "jpg",
                        new File(resident.getPicture().getPictureUrl()));
                    fileStream = new FileInputStream(resident.getPicture().getPictureUrl());
                }
                Image newPicture = new Image(fileStream);
                picture.setImage(newPicture);
            } catch (Exception e) {
                System.out.println(e + "Cannot set Image of resident");
            }
        }
    }

    /**
     * GUI Button handler for resetting a resident's image
     */
    @FXML
    private void handleResetImage() {
        try {
            resident.getPicture().resetPictureUrl();
            if (resident.getPicture().checkJarResourcePath()) {
                initJarImage();
            } else {
                initProjectImage();
            }
        } catch (Exception e) {
            System.out.println("Placeholder Image not found");
        }
    }

    /**
     * Handle loading of image during development
     * @throws FileNotFoundException when image url is invalid
     */
    public void initProjectImage() throws FileNotFoundException {
        File picFile = new File(resident.getPicture().getPictureUrl());
        FileInputStream fileStream = new FileInputStream(picFile);
        Image personPicture = new Image(fileStream);
        picture.setImage(personPicture);
        informationPane.getChildren().add(picture);
    }

    /**
     * Handle loading of image in production (i.e. from jar file)
     * @throws FileNotFoundException when image url is invalid
     */
    public void initJarImage() throws FileNotFoundException {
        InputStream in = this.getClass().getResourceAsStream(resident.getPicture().getJarPictureUrl());
        Image personPicture = new Image(in);
        picture.setImage(personPicture);
        resident.getPicture().setJarResourcePath();
        informationPane.getChildren().add(picture);
    }


    @Subscribe
    private void handlePersonPanelSelectionChangedEvent(PersonPanelSelectionChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        loadPersonInformation(event.getNewSelection().person);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonPanel)) {
            return false;
        }

        return false;
    }
}
```
###### \resources\view\DarkTheme.css
``` css
.auto-complete-popup .list-cell {
    -fx-background-color: transparent;
    -fx-font-size: 11pt;
}

.auto-complete-popup .clipped-container {
    -fx-font-family: "Verdana";
    -fx-background-color: grey;
    -fx-background-radius: 2px;
}
```
###### \resources\view\PersonPanel.fxml
``` fxml
<HBox prefHeight="400" fx:id="personPanel" xmlns="http://javafx.com/javafx/8.0.111" xmlns:fx="http://javafx.com/fxml/1">
    <children>
      <VBox fx:id="personDetailsBox" alignment="CENTER" maxHeight="300.0">
        <children>
          <ImageView fx:id="picture" fitHeight="1.0" fitWidth="1.0" />
          <Label fx:id="name" text="\$name" />
          <FlowPane fx:id="tags" alignment="CENTER">
               <padding>
                  <Insets bottom="5.0" top="5.0" />
               </padding></FlowPane>
          <HBox alignment="CENTER" prefHeight="10.0" prefWidth="200.0">
            <children>
              <Label text="Phone: " />
              <Label fx:id="phone" text="\$phone" />
            </children>
          </HBox>
          <HBox alignment="CENTER" prefHeight="10.0">
            <children>
              <Label text="Room Address: " />
              <Label fx:id="address" text="\$address" />
            </children>
          </HBox>
          <HBox alignment="TOP_CENTER" prefHeight="25.0">
            <children>
              <Label text="Email: " />
              <Label fx:id="email" text="\$email" />
            </children>
          </HBox>
            <HBox alignment="CENTER" prefHeight="50.0" prefWidth="200.0">
               <children>
                  <Button fx:id="addImageButton" mnemonicParsing="false" onAction="#handleAddImage" text="+Image">
                     <HBox.margin>
                        <Insets right="20.0" />
                     </HBox.margin>
                  </Button>
                  <Button fx:id="resetImageButton" mnemonicParsing="false" onAction="#handleResetImage" text="-Image" />
               </children>
               <padding>
                  <Insets left="50.0" right="50.0" />
               </padding>
            </HBox>
        </children>
      </VBox>
    </children>
</HBox>
```
