package seedu.address.logic.commands;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.CsvParser;
import seedu.address.logic.parser.VcfParser;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Role;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.tag.Tag;

/**
 * Imports contact data from a file into the address book.
 * <p>
 * Supports both CSV and VCF file formats:
 * <ul>
 *     <li>CSV: Name, Phone, Email, Address, Role, Tags (optional)</li>
 *     <li>VCF: vCard format with structured contact fields</li>
 * </ul>
 * Skips duplicate entries and shows error messages for invalid rows.
 */
public class ImportCommand extends Command {

    public static final String COMMAND_WORD = "import";
    public static final String IMPORT_DIR_PREFIX = "imports/";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Imports all data from the given file name located at "
            + getImportsDirAbsolutePath()
            + "\nFile names are case-sensitive.\n"
            + "Parameters: FILENAME (must end with .csv or .vcf)\n"
            + "Example: " + COMMAND_WORD + " addressbook.csv";

    public static final String MESSAGE_CONSTRAINTS = "File name must end with .csv or .vcf";
    public static final String MESSAGE_READ_INPUT_ERROR = "Error reading file due to: ";
    public static final String MESSAGE_ERROR_DURING_IMPORT = "Import failed due to the following errors: \n";
    public static final String MESSAGE_EMPTY_FILE = "No contacts were imported. The file has no contacts. "
        + "Please check your file.";
    public static final String MESSAGE_SUCCESS = "Successfully imported %d contacts!";

    private static final Logger logger = Logger.getLogger(ImportCommand.class.getName());

    private final Path filePath;

    public ImportCommand(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        logger.info("Executing ImportCommand with file: " + filePath);

        List<String> errors = new ArrayList<>();
        List<Person> importedPersons = parseFile(filePath, errors);

        if (importedPersons.isEmpty() && errors.isEmpty()) {
            throw new CommandException(MESSAGE_EMPTY_FILE);
        }

        int startingRowNumber = filePath.toString().toLowerCase().endsWith(".csv") ? 2 : 1;
        List<String> duplicateErrors = addPersonsToModel(model, importedPersons, startingRowNumber);

        StringBuilder resultMessage = new StringBuilder();

        int successfullyImportedCount = importedPersons.size();

        if (successfullyImportedCount > 0) {
            resultMessage.append(String.format(MESSAGE_SUCCESS, successfullyImportedCount));
        }

        if (!duplicateErrors.isEmpty()) {
            if (successfullyImportedCount > 0) {
                resultMessage.append("\nHowever, some duplicate entries were found and skipped:\n");
            } else {
                resultMessage.append(MESSAGE_ERROR_DURING_IMPORT);
            }
            duplicateErrors.forEach(error -> resultMessage.append(error).append("\n"));
        }

        if (!errors.isEmpty()) {
            if (successfullyImportedCount > 0 || !duplicateErrors.isEmpty()) {
                resultMessage.append("\nAdditionally, some rows had errors and were skipped:\n");
            } else {
                resultMessage.append(MESSAGE_ERROR_DURING_IMPORT);
            }
            errors.forEach(error -> resultMessage.append(error).append("\n"));
        }


        if (successfullyImportedCount == 0 && duplicateErrors.isEmpty() && !errors.isEmpty()) {
            throw new CommandException(resultMessage.toString());
        }

        return new CommandResult(resultMessage.toString());
    }


    private List<Person> parseFile(Path filePath, List<String> errors) throws CommandException {
        assert filePath != null : "File path should not be null";
        assert errors != null : "Errors list should not be null";

        try {
            if (filePath.toString().toLowerCase().endsWith(".csv")) {
                return importCsv(filePath.toString(), errors);
            } else if (filePath.toString().toLowerCase().endsWith(".vcf")) {
                List<Person> persons = VcfParser.parseVcf(filePath.toString());
                errors.addAll(VcfParser.getLastParseErrors());
                return persons;
            } else {
                throw new CommandException("Unsupported file type. Only .csv and .vcf files are supported.");
            }
        } catch (IOException e) {
            throw new CommandException(MESSAGE_READ_INPUT_ERROR + e.getMessage());
        }
    }


    private List<String> addPersonsToModel(Model model, List<Person> persons, int startingRowNumber) {
        assert model != null : "Model should not be null";
        assert persons != null : "Persons list should not be null";
        assert startingRowNumber > 0 : "Starting row number should be greater than zero";

        List<String> duplicateErrors = new ArrayList<>();
        boolean isVcf = filePath.toString().toLowerCase().endsWith(".vcf");

        List<Person> successfullyImportedPersons = new ArrayList<>();

        for (int i = 0; i < persons.size(); i++) {
            Person person = persons.get(i);
            assert person != null : "Person object should not be null";
            int rowNumber = i + startingRowNumber;

            try {
                model.addPerson(person);
                successfullyImportedPersons.add(person);
            } catch (DuplicatePersonException e) {
                String errorPrefix = isVcf ? person.getName().fullName + ": " : "Row " + rowNumber + " of CSV: ";
                duplicateErrors.add(errorPrefix + e.getMessage());
            }
        }

        persons.clear();
        persons.addAll(successfullyImportedPersons);

        return duplicateErrors;
    }

    /**
     * Imports a list of persons from a CSV file.
     *
     * @param filePath The path to the CSV file to be imported.
     * @param errors A list to store any errors encountered during the import process.
     * @return A list of {@code Person} objects parsed from the CSV file.
     * @throws IOException If an error occurs while reading the CSV file.
     */
    public static List<Person> importCsv(String filePath, List<String> errors) throws IOException {
        assert filePath != null : "File path should not be null";
        assert !filePath.trim().isEmpty() : "File path should not be empty";
        assert errors != null : "Errors list should not be null";

        List<Person> persons = new ArrayList<>();
        List<List<String>> rawData = CsvParser.parseCsv(filePath);
        assert rawData != null : "Parsed CSV data should not be null";

        for (int i = 0; i < rawData.size(); i++) {
            List<String> values = rawData.get(i);
            assert values != null : "Parsed row data should not be null";
            Person person = createPerson(values, i + 2, errors);
            if (person != null) {
                persons.add(person);
            }
        }
        assert persons != null : "Person list should not be null";
        return persons;
    }

    private static Person createPerson(List<String> values, int rowNumber, List<String> errors) {
        assert values != null : "Input values list should not be null";
        assert errors != null : "Errors list should not be null";
        assert rowNumber > 0 : "Row number should be positive";

        if (values.size() < 5) {
            errors.add("Row " + rowNumber + " of CSV: Missing required fields (Name, Phone, Email, Address, Role).");
            return null;
        }

        try {
            Name name = new Name(values.get(0).trim());
            Phone phone = new Phone(values.get(1).trim());
            Email email = new Email(values.get(2).trim());
            Address address = new Address(values.get(3).trim());
            Role role = new Role(values.get(4).trim());

            Set<Tag> tags = (values.size() > 5) ? parseTags(values.get(5)) : new HashSet<>();

            return new Person(name, phone, email, address, role, tags, Optional.empty());

        } catch (IllegalArgumentException e) {
            errors.add("Row " + rowNumber + " of CSV: " + e.getMessage());
            return null;
        }
    }

    private static Set<Tag> parseTags(String rawTags) {
        Set<Tag> tags = new HashSet<>();
        String[] tagArray = rawTags.split("[;,]");
        for (String tag : tagArray) {
            tag = tag.trim();
            if (!tag.isEmpty()) {
                tags.add(new Tag(tag));
            }
        }
        return tags;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ImportCommand)) {
            return false;
        }

        ImportCommand e = (ImportCommand) other;
        return filePath.equals(e.filePath);
    }

    public static String getImportsDirAbsolutePath() {
        return new java.io.File(IMPORT_DIR_PREFIX).getAbsolutePath();
    }

    public String toString() {
        return filePath.toString();
    }
}
