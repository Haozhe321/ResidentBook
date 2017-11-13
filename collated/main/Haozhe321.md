# Haozhe321
###### \java\seedu\room\commons\events\ui\ChangeMonthRequestEvent.java
``` java

/**
 * This event will be raised during the PrevCommand or the NextCommand
 */
public class ChangeMonthRequestEvent extends BaseEvent {
    public final int targetIndex;

    public ChangeMonthRequestEvent(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    public int getTargetIndex() {
        return targetIndex;
    }
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
```
###### \java\seedu\room\logic\commands\AddCommand.java
``` java
    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        requireNonNull(model);
        try {
            model.addPerson(toAdd);
            if (toAdd.getTimestamp().getExpiryTime() != null) {
                String successMessage = String.format(MESSAGE_SUCCESS, toAdd);
                return new CommandResult(successMessage, MESSAGE_TEMPORARY_PERSON);
            }
            return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
        } catch (DuplicatePersonException e) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

    }
```
###### \java\seedu\room\logic\commands\DeleteByTagCommand.java
``` java
/**
 * Deletes a person identified by a tag supplied
 */
public class DeleteByTagCommand extends UndoableCommand {
    public static final String COMMAND_WORD = "deletebytag";
    public static final String COMMAND_ALIAS = "dbt";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the persons identified by the tag supplied in this argument\n"
            + "Parameters: TAG\n"
            + "Example: " + COMMAND_WORD + " friends";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Persons with the following tag: %1$s";

    private final Tag toRemove;

    public DeleteByTagCommand(String tagName) throws IllegalValueException {
        this.toRemove = new Tag(tagName);
    }


    @Override
    public CommandResult executeUndoableCommand() throws CommandException {

        try {
            model.deleteByTag(toRemove);
        } catch (IllegalValueException e) {
            assert false : "Tag provided must be valid";
        }

        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, toRemove));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteByTagCommand // instanceof handles nulls
                && this.toRemove.equals(((DeleteByTagCommand) other).toRemove)); // state check
    }
}


```
###### \java\seedu\room\logic\commands\NextCommand.java
``` java

/**
 * The command to go to the next month in the calendar
 */
public class NextCommand extends Command {
    public static final String COMMAND_WORD = "next";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Go to the next month in the calendar\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SWITCH_TAB_SUCCESS = "Switched to next month on calendar";


    @Override
    public CommandResult execute() {
        EventsCenter.getInstance().post(new ChangeMonthRequestEvent(1));
        return new CommandResult(MESSAGE_SWITCH_TAB_SUCCESS);
    }
}
```
###### \java\seedu\room\logic\commands\PrevCommand.java
``` java
/**
 * The command to go to the previous month in the calendar
 */
public class PrevCommand extends Command {
    public static final String COMMAND_WORD = "prev";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Go to the previous month in the calendar\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SWITCH_TAB_SUCCESS = "Switched to previous month on calendar";


    @Override
    public CommandResult execute() {
        EventsCenter.getInstance().post(new ChangeMonthRequestEvent(0));
        return new CommandResult(MESSAGE_SWITCH_TAB_SUCCESS);
    }


}
```
###### \java\seedu\room\logic\parser\DeleteByTagCommandParser.java
``` java
/**
 * Parses input arguments and creates a new DeleteByTagCommand object
 */
public class DeleteByTagCommandParser implements Parser<DeleteByTagCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteByTagCommand
     * and returns an DeleteByTagCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteByTagCommand parse(String args) throws ParseException {
        try {
            return new DeleteByTagCommand(args);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteByTagCommand.MESSAGE_USAGE));
        }
    }

}

```
###### \java\seedu\room\logic\parser\ParserUtil.java
``` java
    public static Optional<Timestamp> parseTimestamp(Optional<String> timestamp) throws IllegalValueException,
            NumberFormatException {
        return timestamp.isPresent() ? Optional.of(new Timestamp(Long.parseLong(timestamp.get()))) : Optional.empty();
    }
```
###### \java\seedu\room\logic\parser\ResidentBookParser.java
``` java
        case PrevCommand.COMMAND_WORD:
            return new PrevCommand();

        case NextCommand.COMMAND_WORD:
            return new NextCommand();
```
###### \java\seedu\room\model\Model.java
``` java
    /**
     * Delete all persons with the given tag
     */
    void deleteByTag(Tag tag) throws IllegalValueException, CommandException;
```
###### \java\seedu\room\model\ModelManager.java
``` java
    @Override
    public synchronized void deleteByTag(Tag tag) throws IllegalValueException, CommandException {
        residentBook.removeByTag(tag);
        indicateResidentBookChanged();
    }
```
###### \java\seedu\room\model\person\Person.java
``` java
    @Override
    public ObjectProperty<Timestamp> timestampProperty() {
        return timestamp;
    }

    @Override
    public Timestamp getTimestamp() {
        return timestamp.get();
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp.set(requireNonNull(timestamp));
    }

```
###### \java\seedu\room\model\person\Timestamp.java
``` java
/**
 Create a timestamp in each person to record the time created and time that this temporary person will expire
 */
public class Timestamp {

    public static final String MESSAGE_TIMESTAMP_CONSTRAINTS =
            "Days to expire cannot be negative";

    private LocalDateTime creationTime = null;
    private LocalDateTime expiryTime = null; //after construction, a null expiryTime means this person will not expire
    private long daysToLive;

    public Timestamp(long day) throws IllegalValueException {
        this.creationTime = LocalDateTime.now().withNano(0).withSecond(0).withMinute(0);
        if (!isValidTimestamp(day)) {
            throw new IllegalValueException(MESSAGE_TIMESTAMP_CONSTRAINTS);
        }
        if (day > 0) {
            this.expiryTime = this.creationTime.plusDays(day).withNano(0).withSecond(0).withMinute(0);
        }
        this.daysToLive = day;
    }

    //overloaded constructor
    public Timestamp(String expiry) {
        this.expiryTime = LocalDateTime.parse(expiry);
        this.expiryTime = this.expiryTime.withNano(0).withSecond(0).withMinute(0);
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public long getDaysToLive() {
        return daysToLive;
    }

    /**
     * following method returns null if this person does not expiry
     * @return time of expiry
     */
    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    /**
     *
     * @return the expiry time of the timestamp in String
     */
    public String toString() {
        if (this.expiryTime == null) {
            return "null";
        } else {
            return this.expiryTime.toString();
        }
    }


    /**
     * Returns true if a given long is a valid timestamp.
     */
    public static boolean isValidTimestamp(long test) {
        return (test >= 0);
    }

}
```
###### \java\seedu\room\model\person\UniquePersonList.java
``` java
    /**
     * Removes the persons who have the tag supplied
     *
     * @throws CommandException if no one has this tag
     */
    public void removeByTag(Tag tag) throws CommandException {
        Iterator<Person> itr = this.iterator();
        int numRemoved = 0;
        while (itr.hasNext()) {
            Person p = itr.next();
            if (p.getTags().contains(tag)) {
                itr.remove();
                numRemoved++;
            }
        }
        if (numRemoved == 0) {
            throw new CommandException(Messages.MESSAGE_INVALID_TAG_FOUND);
        }
    }
```
###### \java\seedu\room\model\person\UniquePersonList.java
``` java
    public ObservableList<Person> getInternalList() {
        return internalList;
    }
```
###### \java\seedu\room\model\ResidentBook.java
``` java
    /**
     * delete temporary persons on start up of the app
     */
    public void deleteTemporary() {
        UniquePersonList personsList = this.getUniquePersonList();

        Iterator<Person> itr = personsList.iterator(); //iterator to iterate through the persons list
        while (itr.hasNext()) {
            Person person = itr.next();
            LocalDateTime personExpiry = person.getTimestamp().getExpiryTime();
            LocalDateTime current = LocalDateTime.now();
            if (personExpiry != null) { //if this is a temporary contact
                if (current.compareTo(personExpiry) == 1) { //if current time is past the time of expiry
                    itr.remove();
                }
            }
        }
    }
```
###### \java\seedu\room\model\ResidentBook.java
``` java
    public void removeByTag(Tag tag) throws IllegalValueException, CommandException {
        persons.removeByTag(tag);
    }
```
###### \java\seedu\room\ui\AnchorPaneNode.java
``` java
/**
 * Create an anchor pane that can store additional data.
 */
public class AnchorPaneNode extends AnchorPane {

    public final Color yellow = Color.web("#CA9733");
    public final Color green = Color.web("#336D1C");
    private LocalDate date; // Date associated with this pane
    private final Background focusBackground = new Background(new BackgroundFill(
            green, CornerRadii.EMPTY, Insets.EMPTY));
    private final Background todayBackground = new Background(new BackgroundFill(
            Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY));
    private final Background unfocusBackground = new Background(new BackgroundFill(
            yellow, CornerRadii.EMPTY, Insets.EMPTY));


    /**
     * Create a anchor pane node. Date is not assigned in the constructor.
     * @param children children of the anchor pane
     */
    public AnchorPaneNode(Node... children) {
        super(children);
        this.setupPane();

        this.setOnMouseClicked((e) -> {
            if (this.getBackground() == focusBackground) {
                this.revertBackground();
            } else {
                this.focusGrid();
            }
        });

    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * set up this AnchorPane with predefined style and set the background to be unfocused
     */
    public void setupPane() {
        this.setBackgroundUnfocused();
        this.setStyle("-fx-border-width: 2;");
        this.setStyle("-fx-border-color: white;");
    }

    /**
     *Focus on the Grid when the mouse clicks on it
     */

    public void focusGrid() {
        if (this.getBackground() != todayBackground) {
            this.requestFocus();
            this.backgroundProperty().bind(Bindings
                    .when(this.focusedProperty())
                    .then(focusBackground)
                    .otherwise(unfocusBackground)
            );
        }

    }

    public void setBackgroundUnfocused() {
        this.backgroundProperty().unbind();
        this.backgroundProperty().setValue(unfocusBackground);
    }

    /**
     * Put the background to it's original state
     */
    public void revertBackground() {
        this.backgroundProperty().unbind();
        this.backgroundProperty().setValue(unfocusBackground);
    }


    /**
     *Make the Anchorpane that represents today's date light up
     */
    public void lightUpToday() {
        this.backgroundProperty().unbind();
        this.backgroundProperty().setValue(todayBackground);
    }


}
```
###### \java\seedu\room\ui\CalendarBox.java
``` java

/**
 * Create a CalendarBox Object to pass to the CalendarBoxPanel to be displayed
 */
public class CalendarBox {

    private ArrayList<AnchorPaneNode> allCalendarDays;
    private VBox view;
    private Text calendarTitle;
    private GridPane calendar;
    private GridPane dayLabels;
    private HBox titleBar;
    private YearMonth currentYearMonth;
    private final Color yellow = Color.web("#CA9733");
    private Logic logic;
    private HashMap<LocalDate, ArrayList<ReadOnlyEvent>> hashEvents;
    private Text[] dayNames = new Text[]{ new Text("Sunday"), new Text("Monday"), new Text("Tuesday"),
        new Text("Wednesday"), new Text("Thursday"), new Text("Friday"),
        new Text("Saturday") };


    /**
     * Create a month-based calendar filled with dates and events
     * @param yearMonth the month of the calendar to create the calendar
     * @param logic containing the events to populate
     */
    public CalendarBox(YearMonth yearMonth, Logic logic) {
        this.logic = logic;
        currentYearMonth = yearMonth;
        allCalendarDays = new ArrayList<>(35);

        makeCalendarSkeleton();
        makeCalendarNavigationTool();
        populateCalendar(yearMonth, logic.getFilteredEventList());

        // Create the calendar view
        view = new VBox(titleBar, dayLabels, calendar);
        VBox.setMargin(titleBar, new Insets(0, 0, 10, 0));
    }


    /**
     * Set the days of the calendar to correspond to the appropriate date, with the events populated
     * @param yearMonth year and month of month to render
     * @param eventList list of events to populate
     */
    public void populateCalendar(YearMonth yearMonth, ObservableList<ReadOnlyEvent> eventList) {
        hashEvents = new HashMap<LocalDate, ArrayList<ReadOnlyEvent>>();
        hashEvents = eventsHashMap(eventList);

        LocalDate calendarDate = dateForCalendarPage(yearMonth);
        populateDays(calendarDate);
        changeCalenderTitle(yearMonth);
    }

    //////////////////////////////////// Methods to create the calendar ///////////////////////////////////////////////

    /**
     * Create the title of the calendar and set style
     */
    private void makeCalenderTitle() {
        this.calendarTitle = new Text();
        calendarTitle.setFill(yellow);
        calendarTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
    }

    //Change the title of the calendar according to the month of the calendar
    private void changeCalenderTitle(YearMonth yearMonth) {
        calendarTitle.setText(yearMonth.getMonth().toString() + " " + String.valueOf(yearMonth.getYear()));
    }


    /**
     * Make the buttons for users to press to go previous month or next month
     * @param previousMonth Button to go to previous month
     * @param nextMonth Button to go to next month
     */
    private void makeButtons(Button previousMonth, Button nextMonth) {
        previousMonth.setOnAction(e -> previousMonth());
        nextMonth.setOnAction(e -> nextMonth());

    }

    /**
     * Create the title bar for the calendar above the calendar grids
     * @param titleBar titleBar represented by a HBox
     * @param previousMonth Button for previous month
     * @param nextMonth Button for next month
     */
    private void makeCalendarTitleBar(HBox titleBar, Button previousMonth, Button nextMonth) {
        HBox.setMargin(previousMonth, new Insets(0, 13, 0, 13));
        HBox.setMargin(nextMonth, new Insets(0, 13, 0, 13));
        titleBar.setAlignment(Pos.BASELINE_CENTER);
    }

    /**
     * Create the entire navigation tool for the calender, i.e. title, previous-month button, next-month button
     */
    private void makeCalendarNavigationTool() {
        makeCalenderTitle();

        Button previousMonth = new Button(" PREV ");
        Button nextMonth = new Button(" NEXT ");

        makeButtons(previousMonth, nextMonth);

        this.titleBar = new HBox(previousMonth, calendarTitle, nextMonth);
        makeCalendarTitleBar(titleBar, previousMonth, nextMonth);
    }

    /**
     * Make the skeleton for the calendar, i.e. grids for one month, and label for days of the week
     */
    private void makeCalendarSkeleton() {
        // Create the calendar grid pane
        this.calendar = new GridPane();
        createGrid(calendar);

        // Create the days of the weeks from Sunday to Saturday
        this.dayLabels = new GridPane();
        makeDays(dayNames, dayLabels);
    }

    /**
     * Make the days in a week on the calendar
     * @param dayNames a Text array containing all the days in a week
     * @param gridPane the overall pane for the calendar
     */
    private void makeDays(Text[] dayNames, GridPane gridPane) {
        gridPane.setPrefWidth(600);
        int col = 0;
        for (Text txt : dayNames) {
            txt.setFill(Color.WHITE);
            AnchorPane ap = new AnchorPane();
            ap.setId("calendarDaysPane");
            ap.setPrefSize(200, 10);
            ap.setBottomAnchor(txt, 5.0);
            ap.getChildren().add(txt);
            txt.setTextAlignment(TextAlignment.CENTER);
            ap.setStyle("-fx-text-align: center;");
            gridPane.add(ap, col++, 0);
        }
    }

    /**
     * Create 5 by 7 grids inside calendar
     * @param calendar
     */
    private void createGrid(GridPane calendar) {

        calendar.setPrefSize(500, 450);
        calendar.setGridLinesVisible(true);
        // Create rows and columns with anchor panes for the calendar
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                AnchorPaneNode ap = new AnchorPaneNode();
                ap.setPrefSize(200, 90);
                calendar.add(ap, j, i);
                allCalendarDays.add(ap);
            }
        }
    }

    ///////////////////////////// Methods related to populating events on the calendar //////////////////////////////

    /**
     * Add the event's name on the calendar grid
     * @param ap AnchorPaneNode that we are adding the event to
     * @param eventText Text for the event(s)
     */
    private void addEventName(AnchorPaneNode ap, Text eventText) {
        ap.setBottomAnchor(eventText, 5.0);
        ap.setLeftAnchor(eventText, 5.0);
        ap.getChildren().add(eventText);
    }

    /**
     * Create a HashMap of LocalDate and Arraylist of ReadOnlyEvent to use for populating events on calendar
     * Each key in the HashMap can contain one or more events in the value of the HashMap, stored using an ArrayList
     * @param eventList list of ReadOnlyEvent
     * @return HashMap of LocalDate and Arraylist of ReadOnlyEvent
     */
    private HashMap<LocalDate, ArrayList<ReadOnlyEvent>> eventsHashMap(ObservableList<ReadOnlyEvent> eventList) {
        HashMap<LocalDate, ArrayList<ReadOnlyEvent>> hashEvents = new HashMap<LocalDate, ArrayList<ReadOnlyEvent>>();
        for (ReadOnlyEvent event: eventList) {
            if (hashEvents.containsKey(event.getDatetime().getLocalDateTime().toLocalDate())) {
                hashEvents.get(event.getDatetime().getLocalDateTime().toLocalDate()).add(event);
            } else {
                ArrayList<ReadOnlyEvent> newEventList = new ArrayList<ReadOnlyEvent>();
                newEventList.add(event);
                hashEvents.put(event.getDatetime().getLocalDateTime().toLocalDate(), newEventList);
            }
        }

        return hashEvents;
    }


    /**
     * Method to calculate the date of first day in a page of calendar
     * @param yearMonth the YearMonth for this calendar page
     * @return the LocalDate for this month/page of the calendar
     */
    private LocalDate dateForCalendarPage(YearMonth yearMonth) {
        // Get the date we want to start with on the calendar
        LocalDate calendarDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);

        // Dial back the day until it is SUNDAY (unless the month starts on a sunday)
        while (!calendarDate.getDayOfWeek().toString().equals("SUNDAY")) {
            calendarDate = calendarDate.minusDays(1);
        }
        return calendarDate;
    }

    /**
     * Set up the AnchorPaneNode to prepare for date and event population
     * @param node Individual AnchorPaneNode
     */
    private void setupAnchorPaneNode(AnchorPaneNode node) {
        node.setId("calendarCell");
        if (node.getChildren().size() != 0) {
            node.getChildren().remove(0);
        }
        node.getChildren().clear();
    }

    /**
     * Light up today's grid
     * @param node Individual AnchorPaneNode
     * @param calendarDate
     */
    private void setupToday(AnchorPaneNode node, LocalDate calendarDate) {
        if (calendarDate.equals(LocalDate.now())) {
            node.lightUpToday();
        } else {
            node.revertBackground();
        }
    }

    /**
     * add the date number to the grids
     */
    private void addDates(LocalDate calendarDate, AnchorPaneNode ap) {
        Text txt = new Text(String.valueOf(calendarDate.getDayOfMonth()));
        ap.setDate(calendarDate);
        ap.setTopAnchor(txt, 10.0);
        ap.setLeftAnchor(txt, 5.0);
        ap.getChildren().add(txt);
    }

    /**
     * Create a String that represents the events in a day to fit into a grid in the calendar
     * @param eventInADay ArrayList of events in a day
     * @return String that represents all events in a day
     */
    private String populateDayEvents(ArrayList<ReadOnlyEvent> eventInADay) {
        int numEvents = 0;
        String eventTitles = "";
        for (ReadOnlyEvent actualEvent: eventInADay) {

            //if number of events is already more than 2, populate only 2 and tell users there are more events
            if (numEvents == 2) {
                eventTitles = eventTitles + "and more...";
                break;
            }
            String eventTitle = actualEvent.getTitle().toString();
            if (eventTitle.length() > 8) {
                eventTitle = eventTitle.substring(0, 8) + "...";
            }
            eventTitles = eventTitles + eventTitle + "\n";
            numEvents++;
        }
        return eventTitles;
    }

    /**
     * Populate the days and it's corresponding event(if any) in the calendar
     * @param calendarDate the LocalDate referenced to populate this calendar
     */
    private void populateDays(LocalDate calendarDate) {
        for (AnchorPaneNode ap : allCalendarDays) {
            setupAnchorPaneNode(ap);
            setupToday(ap, calendarDate);
            addDates(calendarDate, ap);

            if (hashEvents.containsKey(calendarDate)) {
                ArrayList<ReadOnlyEvent> eventInADay = hashEvents.get(calendarDate);
                Text eventText = new Text(populateDayEvents(eventInADay));
                addEventName(ap, eventText);
            }
            calendarDate = calendarDate.plusDays(1);

        }
    }

    /////////////////////////////////////// Other methods ////////////////////////////////////////////////

    /**
     * Move the month back by one. Repopulate the calendar with the correct dates.
     */
    public void previousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        populateCalendar(currentYearMonth, logic.getFilteredEventList());
    }

    public void refreshCalendar(Logic logic) {
        populateCalendar(currentYearMonth, logic.getFilteredEventList());
    }

    /**
     * Move the month forward by one. Repopulate the calendar with the correct dates.
     */
    public void nextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        populateCalendar(currentYearMonth, logic.getFilteredEventList());
    }



    public VBox getView() {
        return view;
    }

    public ArrayList<AnchorPaneNode> getAllCalendarDays() {
        return allCalendarDays;
    }

    public void setAllCalendarDays(ArrayList<AnchorPaneNode> allCalendarDays) {
        this.allCalendarDays = allCalendarDays;
    }
}

```
###### \java\seedu\room\ui\CalendarBoxPanel.java
``` java

/**
 * Panel containing the calendar
 */
public class CalendarBoxPanel extends UiPart<Region> {
    private static final String FXML = "CalendarBox.fxml";

    @FXML
    private Pane calendarPane;

    private CalendarBox calendarBox;

    public CalendarBoxPanel(Logic logic) {
        super(FXML);
        calendarBox = new CalendarBox(YearMonth.now(), logic);
        calendarPane.getChildren().add(calendarBox.getView());
    }

    public CalendarBox getCalendarBox() {
        return calendarBox;
    }


    public void freeResources() {
        calendarPane = null;
    }
}
```
###### \java\seedu\room\ui\MainWindow.java
``` java
        calandarBoxPanel = new CalendarBoxPanel(this.logic);
        calendarPlaceholder.getChildren().add(calandarBoxPanel.getRoot());
```
###### \java\seedu\room\ui\MainWindow.java
``` java
    @Subscribe
    public void handleCalenderBoxPanelChange(EventBookChangedEvent event) {
        switchTab(1);
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        calandarBoxPanel.getCalendarBox().refreshCalendar(this.logic);
    }

    @Subscribe
    public void handleChangeMonthCommand(ChangeMonthRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        if (event.getTargetIndex() == 0) {
            calandarBoxPanel.getCalendarBox().previousMonth();
        } else if (event.getTargetIndex() == 1) {
            calandarBoxPanel.getCalendarBox().nextMonth();
        }
    }

```
###### \java\seedu\room\ui\PersonCard.java
``` java
    /**
     * Get the color related to a specified tag
     * @param tag the tag that we want to get the colour for
     * @return color of the tag in String
     */
    private static String getColorForTag(String tag) {
        if (!tagColor.containsKey(tag)) { //if the hashmap does not have this tag
            String chosenColor = colors.get(random.nextInt(colors.size()));
            tagColor.put(tag, chosenColor); //put the tag and color in
        }
        return tagColor.get(tag);
    }

    /**
     * initialise the tag with the colors and the tag name
     */
    private void initTags(ReadOnlyPerson person) {
        person.getTags().forEach(tag -> {
            Label tagLabel = new Label(tag.tagName);
            String randomizedTagColor = getColorForTag(tag.tagName);
            tagLabel.setStyle("-fx-background-color: " + randomizedTagColor);
            tag.setTagColor(randomizedTagColor);
            tags.getChildren().add(tagLabel);

        });
    }
```
###### \resources\view\CalendarBox.fxml
``` fxml
<Pane fx:id="calendarPane"
      prefHeight="447.0"
      prefWidth="600.0"
      xmlns="http://javafx.com/javafx/8.0.60"
      xmlns:fx="http://javafx.com/fxml/1"
/>

```
