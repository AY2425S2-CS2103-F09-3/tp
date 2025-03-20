package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.List;
import java.util.stream.Stream;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.TagsContainsKeywordsPredicate;
import seedu.address.model.person.PhoneNumberContainsKeywordsPredicate;
import seedu.address.model.person.Phone;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

  /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
  public FindCommand parse(String args) throws ParseException {

    ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_TAG);


    boolean tagPresent = arePrefixesPresent(argMultimap, PREFIX_TAG);
    boolean namePresent = arePrefixesPresent(argMultimap, PREFIX_NAME);
    boolean phonePresent = arePrefixesPresent(argMultimap, PREFIX_PHONE);
    boolean hasExactlyOnePrefix = (tagPresent ? 1 : 0) + (namePresent ? 1 : 0) + (phonePresent ? 1 : 0) == 1;
    

    if (!hasExactlyOnePrefix) {
      throw new ParseException(
        String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    if (arePrefixesPresent(argMultimap, PREFIX_NAME)) {
      List<String> keywords = argMultimap.getAllValues(PREFIX_NAME);
      if (keywords.isEmpty()) {
        throw new ParseException(
          String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
      }

      return new FindCommand(new NameContainsKeywordsPredicate(keywords));
    } else if (arePrefixesPresent(argMultimap, PREFIX_TAG)) {
      List<String> keywords = argMultimap.getAllValues(PREFIX_TAG);
      if (keywords.isEmpty()) {
        throw new ParseException(
          String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
      }

      return new FindCommand(new TagsContainsKeywordsPredicate(keywords));
    } else if (arePrefixesPresent(argMultimap, PREFIX_PHONE)) {
      List<String> keywords = argMultimap.getAllValues(PREFIX_PHONE);
      if (keywords.isEmpty()) {
        throw new ParseException(
          String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
      }

      for (String keyword : keywords) {
        if (!Phone.isValidPhone(keyword)) {
          throw new ParseException(MESSAGE_INVALID_PHONE);
        }
      }

      return new FindCommand(new PhoneNumberContainsKeywordsPredicate(keywords));
    } else {
      throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }
  }

  /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
  private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
    return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
  }

}
