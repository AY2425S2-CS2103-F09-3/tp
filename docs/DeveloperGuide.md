---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# Listify Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* Libraries used: [JavaFX](https://openjfx.io/), [Jackson](https://github.com/FasterXML/jackson), [JUnit5](https://github.com/junit-team/junit5), [ez-vcard](https://github.com/mangstadt/ez-vcard)
* This project is based on the AddressBook-Level3 project created by the [SE-EDU initiative](https://se-education.org).


--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### The find command

The find command follows the same high-level flow as other commands in the **Logic component**. To implement the find command, the [java.util.function.Predicate](https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html) class is extensively used. The internal `ArgumentMultiMap` class used in the add command parser is also made use of. Parsing is handled by the `FindCommandParser` class, a more lower-level sequence diagram showcasing how the valid input "n/John" is parsed is included below.
**NOTE**: The sequence diagram omits error checking logic for simplicity.

<puml src="diagrams/InnerFindCommandParser.puml" width="550" />
<puml src="diagrams/arePrefixesPresent.puml" width="550" />

**NOTE**: As like other sequence diagrams in this guide, the lifeline for `a:ArgumentMultiMap` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
Parsing other valid prefixes ("t/", "r/" "p/") is different to how "n/" is parsed in the use of other XYZContainsKeywordsPredicates than NameContainsKeywordsPredicate class (`TagsContainsKeywordsPredicate`, `RoleContainsKeywordsPredicate`, `PhoneNumberContainsKeywordsPredicate` respectively).

### Sort command

The `sort` command follows the standard flow of other commands in the **Logic component**, ensuring seamless integration with the system. The `SortCommand` class is responsible for sorting the list of contacts based on ascending or descending order. The parsing logic is handled by `SortCommandParser`.

Sorting is performed using Java’s `Comparator` interface. When executed, `SortCommand` updates the displayed list by applying the selected sorting criteria on name or phone (if duplicate name). The sorting options include:
- **Name** (alphabetical order)
- **Phone** (numerical order)

How the sorting command works:
1. The user inputs a valid sort command (e.g., `sort asc`).
2. `SortCommandParser` parses the input and extracts the sorting criterion.
3. `SortCommand` is created with the appropriate comparator.
4. The `Model`'s filtered contact list is updated with the sorted order.
5. The updated list is displayed in the UI.

A sequence diagram illustrating the parsing of the command `sort asc` is provided below:

<puml src="diagrams/SortCommandParser.puml" width="550" />

**NOTE**: As with other sequence diagrams, the destruction of temporary objects may not be fully represented due to PlantUML limitations.

By implementing `SortCommand`, the system ensures an organized and structured way for users to manage their contacts efficiently.

### Contact command

The `contact` command follows the standard flow of other commands in the **Logic component**, ensuring seamless integration with the system. The `ContactCommand` class is responsible for marking a person as contacted based on the current time when the command is executed and updating their last contacted time in the system. This command interacts primarily with the `Model` component to retrieve and update the person’s record. The parsing logic is handled by `ContactCommandParser`.

`Contact Command` operates by taking a zero-based `Index` as input and marking the corresponding person in the filtered person list as contacted. The updated person is then saved back into the `Model`.

How the contact command works:
1. The `execute(Model model)` method first retrieves the filtered list of persons.
2. If the given index is invalid (out of bounds), an error is thrown.
3. Otherwise, the corresponding `Person` object is retrieved and updated using the `markAsContacted()` method.
4. The updated `Person` is set back in the model, and the list is refreshed.
5. A success message is returned with the index of the marked person.

A sequence diagram illustrating the execution flow of `ContactCommand` when a user enters `contact 2`:

<puml src="diagrams/ContactCommand.puml" width="550" />

**NOTE**: As with other sequence diagrams, the destruction of temporary objects may not be fully represented due to PlantUML limitations.

### Export feature
The export feature exports the entire address book into a CSV or VCF file using the same command.

This is facilitated by the jackson-dataformat-csv and ez-vcard library, jackson is already used for Storage. Classes for this feature follow the structure of existing
Storage classes used for reading/writing to json storage with necessary modifications to facilitate CSV and VCF
formatting.

#### Added classes
* `CsvAddressBookStorage` and `VcfAddressBookStorage` — Implementations of AddressBookStorage with methods tailored to the target filetypes
* `CsvAdaptedPerson` and `VcfAdaptedPerson` — Adapted Person classes with datatypes suitable to be stored in the target filetype
* `CsvSerializableAddressBook` and `VcfSerializableAddressBook` — Holds Adapted Persons for processing in implementations of AddressBookStorage
* `VcfMapper` — Contains static methods to convert `VcfAdaptedPerson`s to vCard objects

#### Sequence flow of Export to CSV feature
<puml src="diagrams/ExportSequenceDiagram.puml" alt="ExportSequenceDiagram.puml" />

1. `ExportCommand::execute` is invoked, which instantiates a `CsvAddressBookStorage` with the filename
2. `ExportCommand::execute` invokes the `CsvAddressBookStorage::saveAddressBook` method with the `AddressBook` which saves all contacts in the address book to a file
3. `CsvAddressBookStorage::saveAddressBook` instantiates a new `CsvSerializableAddressBook` to format all `Person` object properties to CSV friendly formats.
4. `CsvAddressBookStorage::saveAddressBook` uses the `List` of `CsvAdaptedPersons` to create the export file using the Jackson library (ez-vcard library for VCF)

The above flow applies to VCF exports as well. However, the implementation logic in the methods of the above classes are very different as they
utilise different libraries.

#### Error handling

The export command handles several types of errors:
* Filename validity
* File with same name exists in `exports/` directory
* Address book is empty
* IO errors when creating file

### Import feature

The `import` command allows users to import contact data from CSV and VCF files into the address book.

#### Implementation

The import feature is implemented through the `ImportCommand` class, which supports both CSV and VCF file formats. The command works by parsing the specified file and adding valid contacts to the address book, while handling duplicates and reporting errors.

#### Class structure

The import functionality is implemented through several classes:

* `ImportCommandParser`: Parses user input and creates an ImportCommand object.
* `ImportCommand`: Executes the import operation.
* `CsvParser`: Handles parsing of CSV files.
* `VcfParser`: Handles parsing of VCF files using the ez-vcard library.

#### Sequence flow

The sequence diagram below illustrates how the `import` command works:

<puml src="diagrams/ImportSequenceDiagram.puml" />

How the `import` command works:

1. The user enters an import command (e.g., `import addressbook.csv`).
2. `LogicManager` passes the command to `AddressBookParser`.
3. `AddressBookParser` creates an `ImportCommandParser` to parse the arguments.
4. `ImportCommandParser` validates the file path and creates an `ImportCommand`.
5. `LogicManager` executes the `ImportCommand` with the current model.
6. `ImportCommand` determines the file type and calls the appropriate parser:
    - For CSV files: `CsvParser` reads and parses the CSV file, then `ImportCommand` processes the raw data and creates `Person` objects.
    - For VCF files: `VcfParser` reads and parses the VCF file using the ez-vcard library and returns a list of `Person` objects.
7. `ImportCommand` adds each valid person to the model.
8. Duplicate entries are detected during the `addPerson` operation.
9. A `CommandResult` is returned with a message indicating success or listing errors.

#### Error handling

The import command handles several types of errors:
* File format errors (invalid headers, malformed data).
* Validation errors (invalid phone numbers, emails, etc.).
* Duplicate entries (contacts that already exist in the address book).
* Duplicate entries within the imported file itself.

Errors are collected and reported to the user in the command result, allowing partial imports to succeed while clearly indicating which entries failed and why.

#### Design considerations:

**Aspect: How to handle duplicate entries:**

* **Alternative 1 (current choice):** Skip duplicates and report them to the user.
    * Pros: Prevents accidental data duplication and provides clear feedback about which entries were skipped.
    * Cons: Requires additional error handling and reporting logic.
    * The app identifies duplicate contacts by checking if either the Phone Number, Email, or both are exactly the same.

* **Alternative 2:** Allow duplicates to be added.
    * Pros: Simpler implementation.
    * Cons: Could lead to data integrity issues.

**Aspect: File location:**

* **Alternative 1 (current choice):** Use a fixed imports directory.
    * Pros: Simplifies the command syntax and provides a standard location for import files.
    * Con: Less flexible for users.

* **Alternative 2:** Allow arbitrary file paths.
    * Pros: More flexibility for users.
    * Cons: More complex command syntax and security considerations


### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* has a need to manage a significant number of contacts
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps
* wants to upload and manage multiple contacts at once

**Value proposition**: manage contacts faster than a typical mouse/GUI driven app


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a/an …​                                    | I want to …​                                                                               | So that I can…​                                                        |
|--------|--------------------------------------------|--------------------------------------------------------------------------------------------|------------------------------------------------------------------------|
| `* * *` | new user                                   | see usage instructions                                                                     | refer to instructions when I forget how to use the App                 |
| `* * *` | user                                       | add a new person                                                                           | so that I can save basic contact details                                                                       |
| `* * *` | user                                       | view all contacts                                                                          | so that I can see all the contacts in my address book                                                                       |
| `* * *` | user                                       | delete a person                                                                            | remove entries that I no longer need                                   |
| `* * *` | user                                       | find a person by name                                                                      | locate details of persons without having to go through the entire list |
| `* * *` | user                                       | find a person by number                                                                    | locate details of persons without having to go through the entire list |
| `* * *` | user	                                    | export contacts to a vCard (vcf) or Csv file                                               | easily share or import them into other applications                    |
| `* * *` | user                                       | identify duplicate contacts                                                                | maintain a clean address book                                          |
| `* * *` | user                                       | delete multiple contacts at once through tags                                              | maintain a clean address book easier                   |
| `* * *` | user                                       | add notes to certain contacts                                                              | get details of a contact                                               |
| `* * *` | event organiser                            | import contacts from a CSV file and VCF file                                               | add multiple contacts quickly                                       |
| `* *`  | event organiser                            | quickly type out contacts from handwritten contact details                                 | quickly add them to group chats          |
| `* *`  | user with many persons in the address book | sort contacts alphabetically                                                               | organise details of persons easily                                     |
| `* *`  | user                                       | restore accidentally deleted contacts                                                      | don't lose important information                              |
| `* *`  | user                                       | have the option to add contacts step-by-step for each contact detail                       | don't need to use unfamiliar flags |
| `*`    | user with wrong details of a person        | edit the persons information                                                               | modify a persons information                                           |
| `*`    | user contacting the same person frequently | mark a person as favourite                                                                 | quickly access the persons information                                 |
| `*`    | user                                       | set certain contacts as VIPs                                                               | find important contacts faster                                         |
| `*`    | user                                       | be warned before deleting a contact                                                        | don't accidentally remove important information                 |
| `*`    | user                                       | add multiple phone numbers or emails for a contact                                         | have alternate ways to contact that person        |
| `*`    | event organiser                            | tag contacts into groups                                                                   | categorize them                                                        |
| `*`    | event organiser                            | add multiple contacts in one command                                                       | save time when entering bulk data                              |
| `*`    | event organiser                            | bulk edit multiple contacts                                                                | update common details efficiently                                      |
| `*`    | user                                       | share selected contacts via email                                                          | send them to my colleagues                                        |
| `*`    | user                                       | enable dark mode                                                                           | use the application comfortably in low-light environments              |
| `*`    | event organiser                            | set reminders for follow-ups                                                               | stay on top of communication tasks                                     |
| `*`    | user                                       | search for a contact using partial matches                                                 | find people even if I don't remember their full details   |
| `*`    | user                                       | interact with the application fully through keyboard                                       | do tasks quickly                               |
| `*`    | user                                       | undo my last action                                                                        | recover from mistakes/typos                                            |
| `*`    | user                                       | view recently added contacts                                                               | verify my latest entries                                               |
| `*`    | event organiser                            | see autocomplete suggestions and use them                                                  | do tasks quicker                                          |
| `*`    | user                                       | view contact history (last modified, date added etc)                                       | track updates                                 |
| `*`    | user                                       | use natural language commands                                                              | don't have to memorize syntax                                        |
| `*`    | event organiser                            | email a group of contacts directly from the app                                            | send bulk communications                           |
| `*`    | event organiser                            | filter contacts by details quickly and get an updated list of matching contacts on the fly | find contacts easier |
| `*`    | event organiser                            | edit a contact's details                                                                   | update outdated information                                            |


### Use cases

(For all use cases below, the **System** is `Listify` and the **Actor** is the `user`, unless specified otherwise)

**Use case: Delete a person**

**MSS**

1.  User requests to list persons.
2.  Listify shows a list of persons.
3.  User requests to delete a specific person in the list.
4.  Listify deletes the person.

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

    * 3a1. Listify shows an error message.

      Use case resumes at step 2.

---

**Use case: View all contacts**

**MSS**

1.  User requests to list persons.
2.  Listify shows a list of persons.

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.
---

**Use case: Find person(s)**

**MSS**

1.  User requests to find all person(s) containing the same names/phone numbers/roles/tags as the provided names/phone numbers/roles/tags.
2.  Listify shows a list of person(s) the user requested.

    Use case ends.

**Extensions**

* 2a. The names/phone numbers/roles/tags in Listify do not contain any of the user provided names/phone numbers/roles/tags.

    * 2a1. Listify displays an empty list.

      Use case ends.

---
**Use case: Sort contacts**

**MSS**

1.  User requests to list persons.
2.  Listify shows a list of persons.
3.  User requests to sort contacts.
4.  Listify shows sorted list of persons.

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The list contains contacts with same name.

  * 3a1. Listify sorts the contacts by phone number.
      
    Use case resumes from step 4.

---
**Use case: Export contacts to CSV**

**MSS**

1.  User requests to export contacts to a CSV file.
2.  Listify exports all contacts to a CSV file.
3.  Listify shows a success message with the saved file location.<br>
    
    Use case ends.

**Extensions**

* 1a. The provided filename is invalid

    * 1a1. Listify shows an error message.
      
      Use case ends.

* 1b. The export file creation failed

    * 1b1. Listify shows an error message.
      
      Use case ends.

* 1c. A file with the given filename given already exists

    * 1c1. Listify shows an error message.
    
      Use case ends.
---
**Use case: Export contacts to VCF**<br>
Similar to Export contacts to CSV use case, except for the file type.

**MSS**
1.  User requests to export contacts to a VCF file.
2.  Listify exports all contacts to a VCF file.
3.  Listify shows a success message with the saved file location.<br>
    
    Use case ends.

**Extensions**
* 1a. The provided filename is invalid

    * 1a1. Listify shows an error message.

      Use case ends.

* 1b. The export file creation failed

    * 1b1. Listify shows an error message.

      Use case ends.

* 1c. A file with the given filename given already exists

    * 1c1. Listify shows an error message.
      
      Use case ends.

---
**Use case: Import contacts from CSV**

**MSS**

1.  User requests to import contacts from a CSV file.
2.  System validates the file path and format.
3.  System reads the CSV file and parses the contact data.
4.  System adds valid contacts to the address book.
5.  System displays a success message with the number of contacts imported.

    Use case ends.

**Extensions**

* 2a. File path is invalid or file does not exist.

    * 2a1. System displays an error message.

      Use case ends.

* 2b. File format is not CSV.

    * 2b1. System displays an error message.

      Use case ends.

* 3a. CSV file has an invalid header.

    * 3a1. System displays an error message.

      Use case ends.

* 3b. CSV file is empty.

    * 3b1. System displays a message indicating no contacts were imported.

      Use case ends.

* 4a. Some contacts have invalid data.

    * 4a1. System skips invalid contacts.
    * 4a2. System continues importing valid contacts.
    * 4a3. System reports the rows with errors in the result message.

      Use case resumes from step 5.

* 4b. Some contacts are duplicates of existing contacts.

    * 4b1. System skips duplicate contacts.
    * 4b2. System continues importing non-duplicate contacts.
    * 4b3. System reports the duplicate entries in the result message.

      Use case resumes from step 5.
---
**Use case: Import contacts from VCF**

**MSS**

1.  User requests to import contacts from a VCF file.
2.  System validates the file path and format.
3.  System reads the VCF file and parses the contact data.
4.  System adds valid contacts to the address book.
5.  System displays a success message with the number of contacts imported.

    Use case ends.

**Extensions**

* 2a. File path is invalid or file does not exist.

    * 2a1. System displays an error message.

      Use case ends.

* 2b. File format is not VCF.

    * 2b1. System displays an error message.

      Use case ends.

* 4a. Some contacts have invalid data.

    * 4a1. System skips invalid contacts.
    * 4a2. System continues importing valid contacts.
    * 4a3. System reports the contacts with errors in the result message.

      Use case resumes from step 5.

* 4b. Some contacts are duplicates of existing contacts.

    * 4b1. System skips duplicate contacts.
    * 4b2. System continues importing non-duplicate contacts.
    * 4b3. System reports the duplicate entries in the result message.

      Use case resumes from step 5.

---
**Use case: Edit persons information**

**MSS**

1.  User requests to list persons.
2.  Listify shows a list of persons.
3.  User requests to edit a specific person in the list.
4.  Listify updates the person information.

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The updated information is not valid.

    * 3a1. Listify shows an error message.

      Use case resumes at step 2.

**Use case: Mark a contact as contacted**

**MSS**

1.  User requests to list persons.
2.  Listify shows a list of persons.
3.  User requests to mark a specific person in the list as contacted.
4.  Listify updates the person's last contacted time.

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

    * 3a1. Listify shows an error message.

      Use case resumes at step 2.


**Use case: Delete multiple person(s) with the same tag**

**MSS**

1.  User requests to list persons.
2.  Listify shows a list of persons.
3.  User requests to delete any person(s) with the same provided tag.
4.  Listify deletes all person(s) with the provided tag.

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The provided tag doesn't match any existing tags.

    * 3a1. Listify shows 0 person(s) deleted.

      Use case resumes at step 2.

---

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2.  Should be able to hold up to 1000 persons without a noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  Should be portable in the form of a FAT Jar file.
5.  Programme size (excluding user data) should not exceed 5MB.
6.  The programme should not use more than 1GB of memory with no contacts added.
7.  The programme should be persistent.


### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS.
* **Contact** - An individual an organisation/party has interest in communicating with.
* **Contact Details** - Adjectives that can describe a contact including but not limited to Contact Name, Phone Number, and Email.
* **CSV File** - (Comma-Separated Values) a simple text file that stores data in a tabular format, where each line represents a row and values within a row are separated by commas.
* **VCF File** - (vCard or Virtual Contact File) a common file format used to store and exchange digital contact information across devices and platforms (iCloud, Google Contacts, etc.).
* **Event Organisers** – Tech-savvy event organisers who are fast typists and deal with large amounts of contacts.
* **Tag** - To associate a contact with a particular group.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>


### Launch and shutdown

1. Initial launch

    1. Download the jar file and copy into an empty folder

    1. Double-click the jar file<br> Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.


### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.



### Saving data

1. Export/Import Data

   1. Test case: Export data to a different format (CSV/VCF), delete all contacts, then import.

      Expected: All contacts should be restored after import.

2. Auto save on exit

    1. Test case: Modify data, then exit application.

       Expected: Changes are automatically saved without explicit save command.

### Importing data
**Prerequisites**
* Ensure the application is running and the imports directory is accessible.
* Prepare test CSV and VCF files with various scenarios (valid entries, invalid entries, duplicates).

**Test Cases**
1. Basic Import Functionality

    1. Execute: `import validfile.csv`

        Expected: Success message with the number of contacts imported.

2. File Format Handling

    1. Execute: `import validfile.vcf`

       Expected: Success message with the number of contacts imported.

    1. Execute: import `invalidfile.txt`

       Expected: Error message about unsupported file type.

3. Empty File Handling

    1. Import an empty CSV/VCF file.

       Expected: Error message stating no contacts in file.

4. Duplicate Handling

    1. Import a file with duplicate entries only.

       Expected: Error message with duplicates reported.

5. Invalid Data Handling

    1. Import a CSV/VCF file with invalid data (e.g., malformed email, invalid phone number).

       Expected: Error message listing invalid entries, valid entries still imported.

6. Large File Handling

    1. Import a CSV/VCF file with a large number of valid entries (e.g., 1000+).

       Expected: Success message, all valid entries imported.

7. Partial Import

    1. Import a file with some valid and some invalid entries.

       Expected: Success message for valid entries, error messages for invalid ones.



### Exporting contacts
1. Exporting contacts with valid filename and contacts added

   1. Prerequisites: Have contacts added in the app.

   1. Test case: export to valid CSV filename `export a.csv`.<br>
   Expected: File exports successfully.

   1. Test case: export to valid VCF filename `export b.vcf`.<br>
   Expected: File exports successfully.

1. Exporting contacts with invalid filename and contacts added.

   1. Prerequisites: Have contacts added in the app.

   1. Test case: export to invalid CSV filename `export .csv`.<br>
   Expected: Error message about invalid filename.

   1. Test case: export to invalid VCF filename `export !@#$%^'::'.vcf`.<br>
   Expected: Error message about invalid filename.

   1. Test case: export to invalid filename `export aaa`.<br>
   Expected: Error message about invalid filename.

1. Exporting contacts with no contacts added

   1. Prerequisites: run the `clear` command to remove all contacts.<br>
   Expected: All contacts removed from app.

   1. Test case: export to valid CSV filename `export a.csv`.<br>
   Expected: Error message about no contacts.

   1. Test case: export to valid VCF filename `export b.vcf`.<br>
   Expected: Error message about no contacts.



### Sort contacts
**Prerequisites**
* List all persons using the `list` command. Multiple persons in the list.

**Test Cases**
1. Sort contacts in ascending order

    1. Execute: `sort asc`
   
       Expected: Contacts sorted by name (then phone) in ascending order.

2. Sort contacts in descending order

    1. Execute: `sort desc`
   
       Expected: Contacts sorted by name (then phone) in descending order.

3. Sort an empty contact list

    1. Prerequisites: run the `clear` command to remove all contacts.
   
    2. Execute: `sort asc`
   
       Expected: No contacts to sort.



### Mark person as contacted
**Prerequisites**
* Ensure the application is populated with contacts.

**Test Cases**
1. Contact the 1st person in the list

    1. Execute: `contact 1`
   
       Expected: Contact 1 marked as contacted.

2. Invalid index number handling

    1. Execute: `contact 2147483648`
   
       Expected: Invalid index number.

3. Invalid string input handling

    1. Execute: `contact bob`
   
       Expected: Invalid command format or usage.



### Adding a person
**Prerequisites:**
* Listify must be running.

**Test Cases:**
1. Valid Inputs

    1. Execute: `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01 r/Vendor`
        
       Expected: Contact is added, success message shown.
   
    2. Execute: `add n/Betsy Crowe t/friend e/betsycrowe@example.com a/Newgate Prison p/1234567 r/Software Engineer t/criminal`
   
       Expected: Contact with multiple tags is added, success message shown.

2. Invalid Inputs 

    1. Execute: `add n/John Doe p/abcd e/email@com a/Somewhere r/Developer`
   
       Expected: Error message for invalid phone number. 
   
    2. Execute: `add` (with missing fields)
   
       Expected: Error message shown.



### Editing a person
**Prerequisites:**
* At least one person must be in the list.

**Test Cases:**
1. Basic Field Edits

    1. Execute: `edit 1 p/91234567 e/johndoe@example.com`

       Expected: First person’s phone and email are updated, success message shown.
   
    2. Execute: `edit 2 n/Betsy Crower t/`
   
       Expected: Second person's name is changed, tags are cleared.

2. Invalid Edits 

    1. Execute: `edit 0 p/1234567`
   
       Expected: Error message shown.

    2. Execute: `edit 1`
       
       Expected: Error message stating at least one field to edit must be provided.
   
    3. Execute: `edit x n/Test`
   
       Expected: Error message shown.



### Locating persons
**Prerequisites:**
* Add multiple people with varying names, phone numbers, roles, and tags.

**Test Cases:**
1. Name Search 

    1. Execute: `find n/John`
   
       Expected: All contacts with names containing "John" are listed.
   
    2. Execute: `find n/alex n/david`

       Expected: All contacts matching either keyword are shown.

2. Phone Search

    1. Execute: `find p/98765432`
   
       Expected: Contact with matching phone number is listed. 
   
    2. Execute: `find p/98765432 98761234`
   
       Expected: Error message shown stating phone number provided is invalid.

    3. Execute: `find p/98765432 p/98761234`
   
       Expected: Contacts with matching numbers shown.

3. Role/Tag Search 

    1. Execute: `find r/Software`
   
       Expected: Contacts with matching roles (e.g., Software Engineer) listed. 
   
    2. Execute: `find t/friend`
   
       Expected: Contacts with the `friend` tag listed.

4. Invalid Search 

    1. Execute: `find n/john p/98765432`
   
       Expected: Error message shown.



### Deleting multiple people with the same tag
**Prerequisites:**
* Ensure multiple contacts have a common tag.

**Test Cases:**
1. Valid Deletion by Tag

    1. Execute: `list`, then `deletewithtag colleagues`
   
       Expected: All contacts with tag `colleagues` are deleted.

2. Tag Not Found

    1. Execute: `deletewithtag nonexistenttag`
   
       Expected: No contacts deleted and "0 persons deleted!" message shown.

3. Case Insensitivity

    1. Execute: `deletewithtag FRIEND`
   
       Expected: Contacts with tag `friend` are deleted regardless of case.


---
## **Appendix: Planned Enhancements**

* Team size: 5

1. Allow more flexibility for `Name` field: Allow symbols such as s/o, hyphens, @, Arabic and Tamil names so that users can input names such as “Nagaratnam s/o Suppiah”, “Chloe-Jasmine”, “Tan Cheng Bok @ Adrian Tan”, "X Æ A-Xii" and “அபிஷேக்”).

2. More input choices for `contact` command: To allow flexibility to users by allowing other timing and not just current time.

3. Import and export command to include last contacted timing field: Currently, the import and export command only include the following fields: Name, Phone, Email, Address, Role, Tags. We plan to also import/export the `Last contacted` field.

4. Word wrap based on how wide the GUI window is (when input provided by user is too long, line break and specify clearly which field each line belongs to)

5. Input validation for phone numbers to allow alphanumeric chars such as (, ), + and spaces for a clearer view of users input numbers.

6. More sorting choices for `sort` command: Currently, the `sort` command only allows sorting by `name` and `phone`. We plan to allow users to also sort by `tag`, `role` and `last contacted`.

7. Allow multiple prefixes for `find` command: Currently, the `find` command only allows searching for contacts by one prefix. We plan to allow users to also search for contacts by multiple prefixes. Example: `find n/John e/john@gmail.com p/78989456`.

8. Flexibility with exports: Allow users to export only selected contacts that are listed in the app. Example: `find t/friends` followed by `export` will only export contacts with the `friends` tag.
