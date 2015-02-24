package models;

import models.*;
import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class ModelsTest extends WithApplication {

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
    }

    @Test
    public void createAndRetrieveMember() {
        new Member("John", "Doe", "john@doe.com", "1234567890").save();
        Member john = Member.find.where().eq("email", "john@doe.com").findUnique();
        assertNotNull(john);
        assertEquals("John", john.firstName);
    }

    @Test
    public void checkUniqueFacebookId() {
        Member john = new Member("John", "Doe", "john@doe.com", "1234567890");
        john.save();

        Member jack;
        try {
            jack = new Member("Jack", "Black", "jack@black.com", "1234567890");
            jack.save();
        } catch (Exception e) {
            jack = null;
        }
        assertNull(jack);
    }

    @Test
    public void createMemberWithEmptyFacebookId() {
        new Member("Jack", "Black", "jack@black.com", "").save();
        Member jack = Member.find.where().eq("email", "jack@black.com").findUnique();
        assertNull(jack);
    }

}
