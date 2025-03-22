package seedu.address.logic.commands;
//find test
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.PhoneNumberContainsKeywordsPredicate;
import seedu.address.model.person.TagsContainsKeywordsPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        NameContainsKeywordsPredicate nameFirstPredicate =
                new NameContainsKeywordsPredicate(Collections.singletonList("first"));
        NameContainsKeywordsPredicate nameSecondPredicate =
                new NameContainsKeywordsPredicate(Collections.singletonList("second"));

        FindCommand findFirstNameCommand = new FindCommand(nameFirstPredicate);
        FindCommand findSecondNameCommand = new FindCommand(nameSecondPredicate);

        // same object -> returns true
        assertTrue(findFirstNameCommand.equals(findFirstNameCommand));

        // same values -> returns true
        FindCommand findFirstNameCommandCopy = new FindCommand(nameFirstPredicate);
        assertTrue(findFirstNameCommand.equals(findFirstNameCommandCopy));

        // different types -> returns false
        assertFalse(findFirstNameCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstNameCommand.equals(null));

        // different person -> returns false
        assertFalse(findFirstNameCommand.equals(findSecondNameCommand));

        TagsContainsKeywordsPredicate tagFirstPredicate =
                new TagsContainsKeywordsPredicate(Collections.singletonList("first"));
        TagsContainsKeywordsPredicate tagSecondPredicate =
                new TagsContainsKeywordsPredicate(Collections.singletonList("second"));

        FindCommand findFirstTagCommand = new FindCommand(tagFirstPredicate);
        FindCommand findSecondTagCommand = new FindCommand(tagSecondPredicate);

        // same object -> returns true
        assertTrue(findFirstTagCommand.equals(findFirstTagCommand));

        // same values -> returns true
        FindCommand findFirstTagCommandCopy = new FindCommand(tagFirstPredicate);
        assertTrue(findFirstTagCommand.equals(findFirstTagCommandCopy));

        // different types -> returns false
        assertFalse(findFirstTagCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstTagCommand.equals(null));

        // different person -> returns false
        assertFalse(findFirstTagCommand.equals(findSecondTagCommand));

        // different predicate -> returns false
        assertFalse(findFirstTagCommand.equals(findFirstNameCommand));

        PhoneNumberContainsKeywordsPredicate phoneFirstPredicate =
                new PhoneNumberContainsKeywordsPredicate(Collections.singletonList("123"));
        PhoneNumberContainsKeywordsPredicate phoneSecondPredicate =
                new PhoneNumberContainsKeywordsPredicate(Collections.singletonList("456"));

        FindCommand findFirstPhoneCommand = new FindCommand(phoneFirstPredicate);
        FindCommand findSecondPhoneCommand = new FindCommand(phoneSecondPredicate);

        // same object -> returns true
        assertTrue(findFirstPhoneCommand.equals(findFirstPhoneCommand));

        // same values -> returns true
        FindCommand findFirstPhoneCommandCopy = new FindCommand(phoneFirstPredicate);
        assertTrue(findFirstPhoneCommand.equals(findFirstPhoneCommandCopy));

        // different types -> returns false
        assertFalse(findFirstPhoneCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstPhoneCommand.equals(null));

        // different person -> returns false
        assertFalse(findFirstPhoneCommand.equals(findSecondPhoneCommand));

        // different predicate -> returns false
        assertFalse(findFirstPhoneCommand.equals(findFirstTagCommand));
    }

    @Test
    public void execute_zeroKeywords_noPersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        NameContainsKeywordsPredicate namePredicate = prepareNamePredicate(" ");
        TagsContainsKeywordsPredicate tagPredicate = prepareTagPredicate(" ");
        FindCommand nameCommand = new FindCommand(namePredicate);
        FindCommand tagCommand = new FindCommand(tagPredicate);

        expectedModel.updateFilteredPersonList(namePredicate);
        assertCommandSuccess(nameCommand, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());

        expectedModel.updateFilteredPersonList(tagPredicate);
        assertCommandSuccess(tagCommand, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());

    }

    @Test
    public void execute_multipleNameKeywords_multiplePersonsFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        NameContainsKeywordsPredicate predicate = prepareNamePredicate("Kurz Elle Kunz");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(CARL, ELLE, FIONA), model.getFilteredPersonList());
    }

    @Test
    public void execute_singleTagKeywords_multiplePersonsFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        TagsContainsKeywordsPredicate predicate = prepareTagPredicate("friends");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(ALICE, BENSON, DANIEL), model.getFilteredPersonList());
    }

    @Test
    public void execute_singlePhoneKeyword_singlePersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        PhoneNumberContainsKeywordsPredicate predicate = preparePhonePredicate("94351253");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.singletonList(ALICE), model.getFilteredPersonList());
    }

    @Test
    public void toStringMethod() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Arrays.asList("keyword"));
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private NameContainsKeywordsPredicate prepareNamePredicate(String userInput) {
        return new NameContainsKeywordsPredicate(Arrays.asList(userInput.split("\\s+")));
    }

    /**
     * Parses {@code userInput} into a {@code TagsContainsKeywordsPredicate}.
     */
    private TagsContainsKeywordsPredicate prepareTagPredicate(String userInput) {
        return new TagsContainsKeywordsPredicate(Arrays.asList(userInput.split("\\s+")));
    }

    /**
     * Parses {@code userInput} into a {@code PhoneNumberContainsKeywordsPredicate}.
     */
    private PhoneNumberContainsKeywordsPredicate preparePhonePredicate(String userInput) {
        return new PhoneNumberContainsKeywordsPredicate(Arrays.asList(userInput.split("\\s+")));
    }

}
