public class CommandTestUtil {
 
     public static final String VALID_NAME_AMY = "Amy Bee";
     public static final String VALID_NAME_ALICE = "Alice";
     public static final String VALID_NAME_BOB = "Bob Choo";
     public static final String VALID_PHONE_AMY = "11111111";
     public static final String VALID_PHONE_BOB = "22222222";
     public static final String VALID_ADDRESS_BOB = "Block 123, Bobby Street 3";
     public static final String VALID_TAG_HUSBAND = "husband";
     public static final String VALID_TAG_FRIEND = "friend";
     public static final String VALID_TAG_COLLEAGUE = "colleague";
 
     public static final String NAME_DESC_AMY = " " + PREFIX_NAME + VALID_NAME_AMY;
     public static final String NAME_DESC_ALICE = " " + PREFIX_NAME + VALID_NAME_ALICE;
     public static final String NAME_DESC_BOB = " " + PREFIX_NAME + VALID_NAME_BOB;
     public static final String PHONE_DESC_AMY = " " + PREFIX_PHONE + VALID_PHONE_AMY;
     public static final String PHONE_DESC_BOB = " " + PREFIX_PHONE + VALID_PHONE_BOB;
     public static final String ADDRESS_DESC_AMY = " " + PREFIX_ADDRESS + VALID_ADDRESS_AMY;
     public static final String ADDRESS_DESC_BOB = " " + PREFIX_ADDRESS + VALID_ADDRESS_BOB;
     public static final String TAG_DESC_FRIEND = " " + PREFIX_TAG + VALID_TAG_FRIEND;
     public static final String TAG_DESC_COLLEAGUE = " " + PREFIX_TAG + VALID_TAG_COLLEAGUE;
     public static final String TAG_DESC_HUSBAND = " " + PREFIX_TAG + VALID_TAG_HUSBAND;
 
     public static final String INVALID_NAME_DESC = " " + PREFIX_NAME + "James&"; // '&' not allowed in names
}
