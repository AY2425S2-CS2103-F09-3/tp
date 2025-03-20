package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Phone} matches any of the phone number given.
 */
public class PhoneNumberContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;

    public PhoneNumberContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Person person) {
        return keywords.stream()
                .anyMatch(keyword -> person.getPhone().toString().contains(keyword));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PhoneNumberContainsKeywordsPredicate)) {
            return false;
        }

        PhoneNumberContainsKeywordsPredicate otherPhoneNumberContainsKeywordsPredicate =
                (PhoneNumberContainsKeywordsPredicate) other;
        return keywords.equals(otherPhoneNumberContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
