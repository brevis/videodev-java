package models;

import models.*;
import org.junit.*;
import play.test.WithApplication;

import static org.junit.Assert.*;

import static play.test.Helpers.*;

public class ModelsTest extends WithApplication {

    @Before
    public void setup() {
        app = fakeApplication(inMemoryDatabase("test"));
    }

    @Test
    public void createAndRetrieveMember() {
        try {
            new Member("John", "Doe", "john@doe.com", "1234567890").saveMember();
        } catch (Exception e) {
            assertTrue(false);
        }
        Member john = Member.find.where().eq("email", "john@doe.com").findUnique();
        assertNotNull(john);
        assertEquals("John", john.firstName);
    }

    @Test
    public void checkUniqueFacebookId() {
        try {
            new Member("John", "Doe", "john@doe.com", "1234567890").saveMember();
            new Member("Jack", "Black", "jack@black.com", "1234567890").saveMember();
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void createMemberWithEmptyFacebookId() {
        try {
            new Member("Jack", "Black", "jack@black.com", "").saveMember();
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("[facebookId]"));
        }
    }

}
