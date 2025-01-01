import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import urlUtils.UrlUtils;
import java.util.Map;

public class UrlUtilsTest {
    @Test
    void shortenURL_shouldReturnShortCode() {
        String longUrl = "https://www.example.com/long-url";
        String userId = UrlUtils.generateUserId();
        String shortUrl = UrlUtils.shortenURL(longUrl, userId);
        assertNotNull(shortUrl);
        assertEquals(8, shortUrl.length());
    }

    @Test
    void getLongURL_shouldReturnLongURL() {
        String longUrl = "https://www.example.com/long-url";
        String userId = UrlUtils.generateUserId();
        String shortUrl = UrlUtils.shortenURL(longUrl, userId);
        String retrievedLongUrl = UrlUtils.getLongURL(shortUrl);
        assertEquals(longUrl, retrievedLongUrl);
    }

    @Test
    void getLongURL_shouldBlockAfterLimitReached() {
        String longUrl = "https://www.example.com/long-url";
        String userId = UrlUtils.generateUserId();
        String shortUrl = UrlUtils.shortenURL(longUrl, 2, userId);

        assertNotNull(UrlUtils.getLongURL(shortUrl));
        assertNotNull(UrlUtils.getLongURL(shortUrl));
        assertNull(UrlUtils.getLongURL(shortUrl));
    }

    @Test
    void getRemainingClicks_shouldTrackCorrectly() {
        String longUrl = "https://www.example.com/long-url";
        String userId = UrlUtils.generateUserId();
        String shortUrl = UrlUtils.shortenURL(longUrl, 3, userId);

        assertEquals(3, UrlUtils.getRemainingClicks(shortUrl));
        UrlUtils.getLongURL(shortUrl);
        assertEquals(2, UrlUtils.getRemainingClicks(shortUrl));
    }

    @Test
    void deleteUrl_shouldOnlyWorkForOwner() {
        String longUrl = "https://www.example.com/long-url";
        String userId = UrlUtils.generateUserId();
        String wrongUserId = UrlUtils.generateUserId();
        String shortUrl = UrlUtils.shortenURL(longUrl, userId);

        assertFalse(UrlUtils.deleteUrl(shortUrl, wrongUserId));
        assertTrue(UrlUtils.deleteUrl(shortUrl, userId));
        assertNull(UrlUtils.getLongURL(shortUrl));
    }

    @Test
    void isUrlActive_shouldCheckExpiration() throws InterruptedException {
        String longUrl = "https://www.example.com/long-url";
        String userId = UrlUtils.generateUserId();
        String shortUrl = UrlUtils.shortenURL(longUrl, userId);

        assertTrue(UrlUtils.isUrlActive(shortUrl));
        Thread.sleep(100);
        assertTrue(UrlUtils.isUrlActive(shortUrl));
    }

    @Test
    void createUser_shouldCreateNewUser() {
        String userName = "testUser";
        String userId = UrlUtils.createUser(userName);

        assertNotNull(userId);
        assertEquals(userName, UrlUtils.getUserName(userId));
    }

    @Test
    void userExists_shouldCheckUserExistence() {
        String userName = "testUser";
        UrlUtils.createUser(userName);

        assertTrue(UrlUtils.userExists(userName));
        assertFalse(UrlUtils.userExists("nonexistentUser"));
    }

    @Test
    void getUserIdByName_shouldReturnCorrectId() {
        String userName = "testUser123";
        String userId = UrlUtils.createUser(userName);

        assertEquals(userId, UrlUtils.getUserIdByName(userName));
        assertNull(UrlUtils.getUserIdByName("nonexistentUser"));
    }

    @Test
    void getAllUsers_shouldReturnUserMap() {
        String userName1 = "user1";
        String userName2 = "user2";
        String userId1 = UrlUtils.createUser(userName1);
        String userId2 = UrlUtils.createUser(userName2);

        Map<String, String> users = UrlUtils.getAllUsers();
        assertEquals(userName1, users.get(userId1));
        assertEquals(userName2, users.get(userId2));
    }

    @Test
    void generateUserId_shouldReturnUniqueIds() {
        String userId1 = UrlUtils.generateUserId();
        String userId2 = UrlUtils.generateUserId();

        assertNotNull(userId1);
        assertNotNull(userId2);
        assertNotEquals(userId1, userId2);
    }
}
