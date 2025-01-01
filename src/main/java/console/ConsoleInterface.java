
package console;

import urlUtils.UrlUtils;
import java.util.*;
import java.awt.Desktop;
import java.net.URI;

public class ConsoleInterface {
    private static String currentUserId = null;
    private static Scanner scanner = new Scanner(System.in);

    public static void start() {
        while (true) {
            if (currentUserId == null) {
                System.out.println("\n1. Create new user");
                System.out.println("2. Switch user");
                System.out.println("3. Exit");
            } else {
                String currentUserName = UrlUtils.getUserName(currentUserId);
                System.out.println("\nCurrent user: " + currentUserName);
                System.out.println("1. Create short URL");
                System.out.println("2. Open short URL");
                System.out.println("3. Delete URL");
                System.out.println("4. Switch user");
                System.out.println("5. Exit");
            }

            System.out.print("\nEnter your choice: ");
            String choice = scanner.nextLine();

            if (currentUserId == null) {
                handleUnauthenticatedChoice(choice);
            } else {
                handleAuthenticatedChoice(choice);
            }
        }
    }

    private static void handleUnauthenticatedChoice(String choice) {
        switch (choice) {
            case "1":
                createNewUser();
                break;
            case "2":
                switchUser();
                break;
            case "3":
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }

    private static void handleAuthenticatedChoice(String choice) {
        switch (choice) {
            case "1":
                createShortUrl();
                break;
            case "2":
                openLongUrl();
                break;
            case "3":
                deleteUrl();
                break;
            case "4":
                switchUser();
                break;
            case "5":
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }

    private static void createNewUser() {
        System.out.print("Enter username: ");
        String userName = scanner.nextLine();
        currentUserId = UrlUtils.createUser(userName);
        System.out.println("User created successfully!");
    }

    private static void switchUser() {
        Map<String, String> users = UrlUtils.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users exist! Please create a user first.");
            return;
        }

        System.out.println("\nAvailable users:");
        for (String userName : users.values()) {
            System.out.println(userName);
        }

        System.out.print("\nEnter username (if user doesn't exist, new user will be created): ");
        String userName = scanner.nextLine();
        
        if (UrlUtils.userExists(userName)) {
            currentUserId = UrlUtils.getUserIdByName(userName);
            System.out.println("Switched to user: " + userName);
        } else {
            currentUserId = UrlUtils.createUser(userName);
            System.out.println("User created successfully!");
        }
    }

    private static void createShortUrl() {
        System.out.print("Enter long URL: ");
        String longUrl = scanner.nextLine();
        System.out.print("Enter click limit: ");
        String clickLimit = scanner.nextLine();
        int clickLimitInt = 5;
        try {
            clickLimitInt = Integer.parseInt(clickLimit);
        } catch (NumberFormatException e) {
            System.out.print("Could not parse click limit. Defaulting to 5.");
        }
        String shortUrl = UrlUtils.shortenURL(longUrl, clickLimitInt, currentUserId);
        System.out.println("Short URL: " + shortUrl);
    }

    private static void openLongUrl() {
        System.out.print("Enter short URL code: ");
        String shortCode = scanner.nextLine();
        String longUrl = UrlUtils.getLongURL(shortCode);
        
        if (longUrl == null) {
            System.out.println("Invalid or expired URL!");
            return;
        }

        try {
            if (UrlUtils.getRemainingClicks(shortCode) <= 0) {
                System.out.println("Attention! This URL has reached its click limit!");
            }
            Desktop.getDesktop().browse(new URI(longUrl));
        } catch (Exception e) {
            System.out.println("Error opening URL: " + e.getMessage());
        }
    }

    private static void deleteUrl() {
        System.out.println("\nEnter short URL to delete (0 to cancel): ");
        String shortUrl = scanner.nextLine();
        
        if (!shortUrl.equals("0")) {
            if (UrlUtils.deleteUrl(shortUrl, currentUserId)) {
                System.out.println("URL deleted successfully!");
            } else {
                System.out.println("Failed to delete URL or URL not found!");
            }
        }
    }
}
