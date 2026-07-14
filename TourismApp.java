import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
class TouristUser {

    private String username;
    private String password;
    private String fullName;
    private String phone;

    TouristUser(String username, String password, String fullName, String phone){
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getPhone() { return phone; }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
class AuthManager {

    private TouristUser[] users = new TouristUser[100];
    private int userCount = 0;
    private TouristUser loggedInUser = null;

    private int generatedOtp;

    AuthManager() {
        users[userCount++] = new TouristUser("sangs", "12345", "Sangeetha", "9618417136");
        users[userCount++] = new TouristUser("admin", "1234", "Admin User", "9876543210");
    }

    // ================= REGISTER =================
    boolean register(String username, String password, String fullName, String phone) {

        if (!phone.matches("\\d{10}")) {
            System.out.println("Invalid mobile number! Please enter exactly 10 digits.");
            return false;
        }

        for (int i = 0; i < userCount; i++) {
            if (users[i].getUsername().equalsIgnoreCase(username)) {
                System.out.println("Username already exists!");
                return false;
            }
        }

        users[userCount++] = new TouristUser(username, password, fullName, phone);

        System.out.println("Registration successful!");
        return true;
    }

    // ================= LOGIN =================
    boolean login(String username, String password, Scanner sc) {

        for (int i = 0; i < userCount; i++) {
            TouristUser user = users[i];

            if (user.getUsername().equalsIgnoreCase(username)) {

                // ✅ Correct password
                if (user.getPassword().equals(password)) {
                    loggedInUser = user;
                    return true;
                }

                // ❌ Wrong password
                System.out.println("Password is wrong!");
                PlaceModule.printPowerHeader("LOGIN FAILED");
                boolean resetDone = resetPassword(sc, user);

                if (resetDone) {
                    System.out.println("Password reset successful. You can login again.");
                } else {
                    System.out.println("Password reset failed.");
                }
                return false;
            }
        }

        // ❌ Username not found
        System.out.println("Username not found!");
        PlaceModule.printPowerHeader("LOGIN FAILED");
        boolean resetDone = resetBoth(sc);

        if (resetDone) {
            System.out.println("Please login again with new credentials.\n");
        }

        return false;
    }

    // ================= RESET PASSWORD =================
    boolean resetPassword(Scanner sc, TouristUser user) {

        // 📱 Ask mobile first
        System.out.print("Enter your registered mobile number: ");
        String mobile = sc.nextLine();

        if (!user.getPhone().equals(mobile)) {
            System.out.println("Mobile number does not match!");
            return false;
        }

        // 🔐 OTP verify
        if (!verifyMobileWithRetry(sc, user.getPhone())) return false;

        // 🔁 Update password
        System.out.print("Enter new password: ");
        String newPass = sc.nextLine();

        user.setPassword(newPass);

        System.out.println("Password updated successfully!");
        return true;   // ⭐ IMPORTANT
    }
    // ================= RESET BOTH =================
    boolean resetBoth(Scanner sc) {

        int attempts = 0;
        TouristUser user = null;

        // 📱 Mobile retry
        while (attempts < 5) {

            System.out.print("Enter your registered mobile number: ");
            String mobile = sc.nextLine();

            user = findByPhone(mobile);

            if (user != null) break;

            attempts++;
            System.out.println("Mobile not found! Attempts left: " + (5 - attempts));
        }

        if (user == null) {
            System.out.println("Too many attempts. Try later!");
            return false;
        }

        // 🔐 OTP
        if (!verifyMobileWithRetry(sc, user.getPhone())) return false;

        // 🔁 Update
        System.out.print("Enter new username: ");
        String newUser = sc.nextLine();

        System.out.print("Enter new password: ");
        String newPass = sc.nextLine();

        user.setUsername(newUser);
        user.setPassword(newPass);

        System.out.println("\nPlease login again with updated credentials.");
        return true;    
    }

    TouristUser findByPhone(String phone) {
        for (int i = 0; i < userCount; i++) {
            if (users[i].getPhone().equals(phone)) {
                return users[i];
            }
        }
        return null;
    }

    // ================= FIND USERNAME =================
    void findUsernameByPhone(String phone) {

        TouristUser user = findByPhone(phone);

        if (user != null) {
            System.out.println("Your username is: " + user.getUsername());
        } else {
            System.out.println("Mobile not found!");
        }
    }

    // ================= RESET PASSWORD BY PHONE =================
    boolean resetPasswordByPhone(String phone, Scanner sc) {

        TouristUser user = findByPhone(phone);

        if (user == null) {
            System.out.println("Mobile number not found!");
            return false;
        }

        if (!verifyMobileWithRetry(sc, phone)) return false;

        System.out.print("Enter new password: ");
        String newPass = sc.nextLine();

        user.setPassword(newPass);

        System.out.println("Password updated successfully!");
        return true;
    }

    // ================= OTP =================
    boolean verifyMobileWithRetry(Scanner sc, String phone) {

        int attempts = 0;

        generatedOtp = (int)(Math.random() * 9000 + 1000);

        System.out.println("OTP sent to mobile: " + phone);
        System.out.println("DEBUG OTP: " + generatedOtp);

        while (attempts < 5) {

            System.out.print("Enter OTP: ");
            int otp = sc.nextInt();
            sc.nextLine();

            if (otp == generatedOtp) {
                System.out.println("OTP Verified Successfully!");
                return true;
            }

            attempts++;
            System.out.println("Wrong OTP! Attempts left: " + (5 - attempts));
        }

        System.out.println("⚠ Account temporarily blocked due to multiple attempts!");
        return false;
    }
    boolean isLoggedIn() {
        return loggedInUser != null;
    }
}
class PlaceModule {

    static Scanner sc = new Scanner(System.in);

    // ================= COLORS =================
    static final String RESET = "\u001B[0m";
    static final String CYAN = "\u001B[36m";
    static final String YELLOW = "\u001B[33m";
    static final String GREEN = "\u001B[32m";
    static final String BLUE = "\u001B[34m";
    static final String RED = "\u001B[31m";
    static final String PURPLE = "\u001B[35m";

    static final int WIDTH = 90;
    // ================= CENTER =================
    static void center(String text, String color) {
        int pad = (WIDTH - text.length()) / 2;
        if (pad > 0) System.out.print(repeat(" ", pad));
        System.out.println(color + text + RESET);
    }

    static String repeat(String s, int count) {
        if (count <= 0) return "";
        StringBuilder sb = new StringBuilder(s.length() * count);
        for (int i = 0; i < count; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    static void printPowerHeader(String title) {

        String border = "╔" + repeat("═", title.length() + 6) + "╗";
        String bottom = "╚" + repeat("═", title.length() + 6) + "╝";

        // middle line split into parts
        String left = "║   ";
        String right = "   ║";

        int width = 90;

        // TOP
        int pad = (width - border.length()) / 2;
        System.out.println(repeat(" ", pad) + CYAN + border + RESET);

        // MIDDLE (split coloring)
        String content = left + GREEN + title + CYAN + right;

        pad = (width - (left + title + right).length()) / 2;
        System.out.println(repeat(" ", pad) + CYAN + content + RESET);

        // BOTTOM
        pad = (width - bottom.length()) / 2;
        System.out.println(repeat(" ", pad) + CYAN + bottom + RESET);
    }

    static void line() {
        center("══════════════════════════════════════════════════════════════════════════════", CYAN);
    }
    static void printCentered(String text) {
        int width = 90;
        int pad = (width - text.length()) / 2;
        if (pad > 0) System.out.print(repeat(" ", pad));
        System.out.println(text);
    }
    // ================= HEADER =================
    static void mainHeader() {

        line();

        String[] art = {
            "████████╗ ██████╗ ██╗   ██╗██████╗ ██╗███████╗████████╗",
            "╚══██╔══╝██╔═══██╗██║   ██║██╔══██╗██║██╔════╝╚══██╔══╝",
            "   ██║   ██║   ██║██║   ██║██████╔╝██║███████╗   ██║   ",
            "   ██║   ██║   ██║██║   ██║██╔══██╗██║╚════██║   ██║   ",
            "   ██║   ╚██████╔╝╚██████╔╝██║  ██║██║███████║   ██║   ",
            "   ╚═╝    ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝   ╚═╝   ",
            "",
            "    ██████╗ ██╗      █████╗  ██████╗███████╗███████╗   ",
            "    ██╔══██╗██║     ██╔══██╗██╔════╝██╔════╝██╔════╝   ",
            "    ██████╔╝██║     ███████║██║     █████╗  ███████╗   ",
            "    ██╔═══╝ ██║     ██╔══██║██║     ██╔══╝  ╚════██║   ",
            "    ██║     ███████╗██║  ██║╚██████╗███████╗███████║   ",
            "    ╚═╝     ╚══════╝╚═╝  ╚═╝ ╚═════╝╚══════╝╚══════╝   "
        };

        System.out.println(CYAN);

        for (String line : art) {
            printCentered(line);   // ✅ same as VehicleRentalApp style
        }

        line();

        System.out.println(RESET);
    }

    // ================= STATE MENU =================
    static void showStateMenu() {
        System.out.println(CYAN + "\nSELECT STATE:" + RESET);

        System.out.println(GREEN + "1. Andhra Pradesh");
        System.out.println("2. Gujarat");
        System.out.println("3. Himachal Pradesh");
        System.out.println("4. Karnataka");
        System.out.println("5. Kerala");
        System.out.println("6. Maharashtra");
        System.out.println("7. Rajasthan");
        System.out.println("8. Tamil Nadu");
        System.out.println("9. Telangana");
        System.out.println("10. Uttar Pradesh" + RESET);

        System.out.println(RED + "0. Exit" + RESET);
    }

    // ================= STATE HEADER =================
    static void stateHeader(String state) {
        line();
        center(state.toUpperCase(), GREEN);
        line();
    }

    // ================= PLACE =================
    static void display(String place, String desc, double fee) {

        printPowerHeader("PLACE DETAILS");

        System.out.println(YELLOW + "Place     : " + RESET + place);
        System.out.println(YELLOW + "Rating    : " + RESET + "4.5 / 5");
        System.out.println(YELLOW + "Entry Fee : " + RESET + "Rs. " + fee);
        System.out.println(YELLOW + "Timing    : " + RESET + "9 AM - 5 PM");
        System.out.println(YELLOW + "About     : " + RESET + desc);

        GlobalData.bill += fee;
        System.out.println(GREEN + "\nCurrent Bill: Rs. " + GlobalData.bill + RESET);
    }

    static void serviceMenu() {
        // 🚗 VEHICLE
        System.out.print("\nDo you want vehicle rental? (yes/no): ");
        if (sc.nextLine().equalsIgnoreCase("yes")) {
            double vehicleCost = VehicleRentalApp.start(); // get cost
            if (vehicleCost > 0) {
                GlobalData.bill += vehicleCost;
            }
        }
        // 🏨 HOTEL
        System.out.print("\nDo you want hotel booking? (yes/no): ");
        if (sc.nextLine().equalsIgnoreCase("yes")) {
            double total=HotelBooking.start(); // later you can return cost like vehicle
            if (total > 0) {
                GlobalData.bill += total;
            }
        }

        //  BILLING
        if (GlobalData.bill > 0) {
            System.out.print("\nDo you want to see bill now? (yes/no): ");
            if (sc.nextLine().equalsIgnoreCase("yes")) {
                BillingSystem.generateBill();
            }
        }
    }

    static int readIntChoice(int min, int max) {
        while (true) {
            String input = sc.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println(RED + "Invalid choice! Enter " + min + "-" + max + " or 0 to exit" + RESET);
                continue;
            }
            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.println(RED + "Invalid choice! Enter " + min + "-" + max + " or 0 to exit" + RESET);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println(RED + "Invalid choice! Enter " + min + "-" + max + " or 0 to exit" + RESET);
            }
        }
    }
    // ================= STATE SESSION =================
    static void stateSession(int state) {

        while (true) {

            printPowerHeader(getStateName(state).toUpperCase());

            System.out.println(CYAN + "Select Place:" + RESET);

            int c;

            if (state == 1) {
                System.out.println("1. Tirupati \n2. Araku Valley \n0. Exit");

                while (true) {
                    c = readIntChoice(0, 2);
                    if (c == 0) return;
                    else if (c == 1) { display("Tirupati", "Famous Temple", 70); break; }
                    else if (c == 2) { display("Araku Valley", "Nature Hills", 50); break; }
                    else System.out.println(RED + "Invalid choice! Enter 1-2 or 0 to exit" + RESET);
                }
            }

            else if (state == 2) {
                System.out.println("1. Dwarka \n2. Statue of Unity \n0. Exit");

                while (true) {
                    c = readIntChoice(0, 2);
                    if (c == 0) return;
                    else if (c == 1) { display("Dwarka", "Krishna Temple", 50); break; }
                    else if (c == 2) { display("Statue of Unity", "Tallest Statue", 100); break; }
                    else System.out.println(RED + "Invalid choice! Enter 1-2 or 0 to exit" + RESET);
                }
            }

            else if (state == 3) {
                System.out.println("1. Manali \n2. Shimla \n0. Exit");

                while (true) {
                    c = readIntChoice(0, 2);
                    if (c == 0) return;
                    else if (c == 1) { display("Manali", "Snow Hills", 80); break; }
                    else if (c == 2) { display("Shimla", "Hill Station", 70); break; }
                    else System.out.println(RED + "Invalid choice! Enter 1-2 or 0 to exit" + RESET);
                }
            }

            else if (state == 4) {
                System.out.println("1. Hampi \n2. Coorg \n0. Exit");
                while (true) {
                    c = readIntChoice(0, 2);

                    if (c == 0) return;
                    else if (c == 1) { display("Hampi", "Ancient Ruins", 60); break; }
                    else if (c == 2) { display("Coorg", "Coffee Hills", 50); break; }
                    else System.out.println(RED + "Invalid choice! Enter 1-2 or 0 to exit" + RESET);
                }
            }

            else if (state == 5) {
                System.out.println("1. Munnar \n2. Alleppey \n0. Exit");

                while (true) {
                    c = readIntChoice(0, 2);
                    if (c == 0) return;
                    else if (c == 1) { display("Munnar", "Tea Gardens", 60); break; }
                    else if (c == 2) { display("Alleppey", "Backwaters", 70); break; }
                    else System.out.println(RED + "Invalid choice! Enter 1-2 or 0 to exit" + RESET);
                }
            }

            else if (state == 6) {
                System.out.println("1. Lonavala \n2. Ajanta \n0. Exit");

                while (true) {
                    c = readIntChoice(0, 2);
                    if (c == 0) return;
                    else if (c == 1) { display("Lonavala", "Hill Station", 40); break; }
                    else if (c == 2) { display("Ajanta", "Ancient Art", 80); break; }
                    else System.out.println(RED + "Invalid choice! Enter 1-2 or 0 to exit" + RESET);
                }
            }

            else if (state == 7) {
                System.out.println("1. Jaipur \n2. Jaisalmer \n0. Exit");

                while (true) {
                    c = readIntChoice(0, 2);
                    if (c == 0) return;
                    else if (c == 1) { display("Jaipur", "Pink City", 60); break; }
                    else if (c == 2) { display("Jaisalmer", "Golden Desert", 80); break; }
                    else System.out.println(RED + "Invalid choice! Enter 1-2 or 0 to exit" + RESET);
                }
            }

            else if (state == 8) {
                System.out.println("1. Ooty \n2. Madurai \n3. Rameshwaram \n0. Exit");

                while (true) {
                    c = readIntChoice(0, 3);
                    if (c == 0) return;
                    else if (c == 1) { display("Ooty", "Hill Station", 60); break; }
                    else if (c == 2) { display("Madurai", "Temple City", 40); break; }
                    else if (c == 3) { display("Rameshwaram", "Spiritual Island", 50); break; }
                    else System.out.println(RED + "Invalid choice! Enter 1-3 or 0 to exit" + RESET);
                }
            }

            else if (state == 9) {
                System.out.println("1. Charminar \n2. Bhadrachalam \n0. Exit");

                while (true) {
                    c = readIntChoice(0, 2);
                    if (c == 0) return;
                    else if (c == 1) { display("Charminar", "Hyderabad Icon", 30); break; }
                    else if (c == 2) { display("Bhadrachalam", "Temple Town", 40); break; }
                    else System.out.println(RED + "Invalid choice! Enter 1-2 or 0 to exit" + RESET);
                }
            }

            else if (state == 10) {
                System.out.println("1. Taj Mahal \n2. Agra Fort \n3. Ayodhya \n4. Mathura \n0. Exit");

                while (true) {
                    c = readIntChoice(0, 4);
                    if (c == 0) return;
                    else if (c == 1) { display("Taj Mahal", "Symbol of Love", 50); break; }
                    else if (c == 2) { display("Agra Fort", "Mughal Architecture", 40); break; }
                    else if (c == 3) { display("Ayodhya", "Religious City", 30); break; }
                    else if (c == 4) { display("Mathura", "Krishna Birthplace", 30); break; }
                    else System.out.println(RED + "Invalid choice! Enter 1-4 or 0 to exit" + RESET);
                }
            }
            serviceMenu();
            System.out.println();
            System.out.print("Explore more places in SAME state? ("
                + GREEN + "YES" + RESET + "/"
                + RED + "NO" + RESET + "): ");

            String ans = sc.nextLine().trim();

            if (ans.equalsIgnoreCase("yes") || ans.equalsIgnoreCase("y")) {
                continue;   // 🔁 stay in same state
            } else {
                break;      // ❌ exit same state
            }
        }
        afterStateFlow();
    }
    // ================= MAIN =================
    public static void start(AuthManager authManager) {
        if (!authManager.isLoggedIn()) {
            System.out.println("Unauthorized access. Please login first.");
            return;
        }
        mainHeader();
        showStateMenu();
        int state;
        while (true) {
            String input = sc.nextLine();
            if (!input.matches("\\d+")) {
                System.out.println(RED + "Invalid input! Enter numbers only." + RESET);
                continue;
            }
            state = Integer.parseInt(input);
            if (state == 0) {
                System.out.println(RED + "THANK YOU!" + RESET);
                System.exit(0);
            }
            if (state >= 1 && state <= 10) {
                stateSession(state);
                break; // valid
            }
            System.out.println(RED + "Invalid state! Please enter again (1-10 or 0 to exit)" + RESET);
        }
    }
    static String getStateName(int s) {
         switch (s) {
            case 1: return "Andhra Pradesh";
            case 2: return "Gujarat";
            case 3: return "Himachal Pradesh";
            case 4: return "Karnataka";
            case 5: return "Kerala";
            case 6: return "Maharashtra";
            case 7: return "Rajasthan";
            case 8: return "Tamil Nadu";
            case 9: return "Telangana";
            case 10: return "Uttar Pradesh";
            default: return "Unknown State";
        }
    }
    // ================= AFTER STATE FLOW (FIXED) =================
    static void afterStateFlow() {

        while (true) {

            System.out.println(PURPLE + "\nWHAT NEXT?" + RESET);
            System.out.println("1. Another State");

            if (GlobalData.bill > 0) {
                System.out.println("2. Billing Only");
            }

            System.out.println("3. Exit");

            System.out.print("Enter choice: ");
            String input = sc.nextLine();

            // ❌ invalid (not number)
            if (!input.matches("\\d+")) {
                System.out.println(RED + "Invalid input! Enter numbers only." + RESET);
                continue; // 🔁 ask again
            }

            int ch = Integer.parseInt(input);

            // ✅ VALID OPTIONS
            if (ch == 1) {
                showStateMenu();

                while (true) {
                    String stateInput = sc.nextLine();

                    if (!stateInput.matches("\\d+")) {
                        System.out.println(RED + "Invalid input!" + RESET);
                        continue;
                    }

                    int state = Integer.parseInt(stateInput);

                    if (state >= 1 && state <= 10) {
                        stateSession(state);
                        return;
                    } else if (state == 0) {
                        System.out.println(RED + "THANK YOU!" + RESET);
                        System.exit(0);
                    } else {
                        System.out.println(RED + "Enter valid state (1-10 or 0 to exit)" + RESET);
                    }
                }
            }

            else if (ch == 2 && GlobalData.bill > 0) {
                BillingSystem.generateBill();
                return;
            }

            else if (ch == 3) {
                System.out.println(RED + "THANK YOU!" + RESET);
                System.exit(0);
            }

            // ❌ wrong number
            else {
                System.out.println(RED + "Invalid choice! Enter 1, 2 or 3." + RESET);
            }
        }
    }
}
class VehicleRentalApp{

    static Scanner sc = new Scanner(System.in);
    static String[] vehicleTypes = new String[10];
    static String[] models = new String[10];
    static String[] plans = new String[10];
    static double[] amounts = new double[10];
    static String[] licenses = new String[10];

    static int count = 0;
    // ANSI COLOR CODES
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";
    public static final String PURPLE = "\u001B[35m";
    public static final String BLUE = "\u001B[34m";
    public static final String WHITE_BRIGHT = "\033[0;97m";

    static final int SCREEN_WIDTH = 85;

    // ================= VISUAL HELPERS =================
    
    static void printCentered(String text) {
        int padding = (SCREEN_WIDTH - text.length()) / 2;
        if (padding > 0) System.out.print(PlaceModule.repeat(" ", padding));
        System.out.println(text);
    }
    static void printSectionSeparator() {
        System.out.println("\n" + BLUE + PlaceModule.repeat("═", SCREEN_WIDTH) + RESET);
    }
    static void typeMessage(String message, String color) {
        System.out.print(color);
        for (char c : message.toCharArray()) {
            System.out.print(c);
            try { Thread.sleep(15); } catch (Exception e) {}
        }
        System.out.println(RESET);
    }

    static void showLoadingBar(String task) {
        System.out.print("\n" + PURPLE + BOLD + "  " + task + " ");
        for (int i = 0; i < 20; i++) {
            System.out.print("█");
            try { Thread.sleep(40); } catch (Exception e) {}
        }
        System.out.println(GREEN + " [DONE]" + RESET + "\n");
    }

    // ================= SAFE INPUT =================
    static int getValidNumber(int min, int max) {
        while (true) {
            String input = sc.next();
            if (!input.matches("\\d+")) {
                typeMessage("  [SYSTEM ERROR]: Invalid input detected. Returning to Welcome Page...\n", RED + BOLD);
                return -1;
            }
            int value = Integer.parseInt(input);
            if (value < min || value > max) {
                System.out.print(RED + "  Invalid choice. Please enter between " + min + " and " + max + ": " + RESET);
            } else {
                return value;
            }
        }
    }

    static int readIntChoice(int min, int max) {
        while (true) {
            String input = sc.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println(RED + "Invalid choice! Enter " + min + "-" + max + " or 0 to exit" + RESET);
                continue;
            }
            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.println(RED + "Invalid choice! Enter " + min + "-" + max + " or 0 to exit" + RESET);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println(RED + "Invalid choice! Enter " + min + "-" + max + " or 0 to exit" + RESET);
            }
        }
    }

    // ================= WELCOME =================
    static void showBanner() {
        System.out.print("\033[H\033[2J"); 
        System.out.flush();        
        System.out.println(CYAN + BOLD);
        printCentered("██╗   ██╗███████╗██╗  ██╗██╗ ██████╗██╗     ███████╗");
        printCentered("██║   ██║██╔════╝██║  ██║██║██╔════╝██║     ██╔════╝");
        printCentered("██║   ██║█████╗  ███████║██║██║     ██║     █████╗  ");
        printCentered("╚██╗ ██╔╝██╔══╝  ██╔══██║██║██║     ██║     ██╔══╝  ");
        printCentered(" ╚████╔╝ ███████╗██║  ██║██║╚██████╗███████╗███████╗");
        printCentered("  ╚═══╝  ╚══════╝╚═╝  ╚═╝╚═╝ ╚═════╝╚══════╝╚══════╝");
        PlaceModule.printPowerHeader("PREMIUM RENTAL MANAGEMENT SYSTEM");
    }

    static boolean validateLicense(String id) {
        return id.matches("^[A-Z]{2}\\d{14,15}$");
    }

    static void showVehicleDetails(String type, String model) {
        PlaceModule.printPowerHeader("VEHICLE SPECIFICATIONS");
        typeMessage("  [SELECTED MODEL] : " + model, BOLD + YELLOW);
        System.out.println(WHITE_BRIGHT);
        if (type.equals("Car")) {
            System.out.println("  * Mileage: 18 - 23 km/l\n  * Comfortable and safe for family trips (AC & GPS Enabled)\n  * Best for city and highway driving | Full-to-Full Fuel Policy");
        } else if (type.equals("Bike")) {
            System.out.println("  * Mileage: 40 - 50 km/l\n  * Best for quick travel and traffic | Performance Disc Brakes\n  * Easy handling and fuel efficient | Helmet provided on request");
        } else {
            System.out.println("  * Mileage: 45 - 55 km/l\n  * Lightweight and easy to ride | Large Under-seat Storage\n  * Perfect for daily short travel | USB Charging Port Included");
        }
        System.out.println(CYAN + PlaceModule.repeat("═", SCREEN_WIDTH) + RESET);
    }

    // ================= MAIN =================
    public static double start() {
        while (true) {
            showBanner();

            printCentered(BOLD + WHITE_BRIGHT + "CHOOSE A VEHICLE CATEGORY TO CONTINUE" + RESET);
            System.out.println();
            System.out.println(YELLOW + "  1. CAR    -> Rs.100/hr | Rs.800/day | Rs.5000/week");
            System.out.println("  2. BIKE   -> Rs.50/hr  | Rs.400/day | Rs.2500/week");
            System.out.println("  3. SCOOTY -> Rs.30/hr  | Rs.300/day | Rs.2000/week" + RESET);

            System.out.print(CYAN + BOLD + "\n  Enter Choice (1-3): " + RESET);
            int choice = getValidNumber(1, 3);
            if (choice == -1) continue;
            
            printSectionSeparator();

            String vehicleType = "";
            String model = "";
            int seater = 0;

            if (choice == 1) {
                vehicleType = "Car";
                typeMessage("  [CATEGORY SELECTED]: CAR", GREEN + BOLD);
                System.out.println("  Choose seating capacity:\n  1. 4 Seater (Hatchback)\n  2. 5 Seater (Sedan/SUV)\n  3. 7 Seater (MPV)");
                int seat = getValidNumber(1, 3);
                if (seat == -1) continue;

                String[][] cars = {{"Swift","i20","Baleno","Altroz","Tiago"},{"Creta","City","Verna","Nexon","Brezza"},{"Innova","Ertiga","XUV700","Scorpio","Safari"}};
                seater = (seat == 1) ? 4 : (seat == 2) ? 5 : 7;
                System.out.println(YELLOW + "\n  Available Inventory Check:" + RESET);
                for (int i = 0; i < 5; i++) System.out.println("  " + (i+1) + ". " + cars[seat-1][i]);
                System.out.print("\n  Select your car (1-5): ");
                int c = getValidNumber(1, 5);
                if (c == -1) continue;
                model = cars[seat-1][c-1];
            } else if (choice == 2) {
                vehicleType = "Bike";
                typeMessage("  [CATEGORY SELECTED]: BIKE", GREEN + BOLD);
                String[] bikes = {"Apache","Pulsar","R15","Duke","FZ"};
                System.out.println(YELLOW + "  Available Inventory Check:" + RESET);
                for (int i = 0; i < bikes.length; i++) System.out.println("  " + (i+1) + ". " + bikes[i]);
                System.out.print("\n  Select your bike (1-5): ");
                int b = getValidNumber(1, 5);
                if (b == -1) continue;
                model = bikes[b-1];
            } else {
                vehicleType = "Scooty";
                typeMessage("  [CATEGORY SELECTED]: SCOOTY", GREEN + BOLD);
                String[] scooty = {"Activa","Jupiter","Access","Dio","Pleasure"};
                System.out.println(YELLOW + "  Available Inventory Check:" + RESET);
                for (int i = 0; i < scooty.length; i++) System.out.println("  " + (i+1) + ". " + scooty[i]);
                System.out.print("\n  Select your scooty (1-5): ");
                int s = getValidNumber(1, 5);
                if (s == -1) continue;
                model = scooty[s-1];
            }

            showVehicleDetails(vehicleType, model);
            printSectionSeparator();
            System.out.print("Do you have a valid driving license? ("
                + GREEN + "YES" + RESET + "/"
                + RED + "NO" + RESET + "): ");

            if (sc.next().equalsIgnoreCase("no")) {
                typeMessage("  ERROR: Legal documentation required for rental. Access Denied.\n", RED + BOLD);
                try { Thread.sleep(1500); } catch (Exception e) {}
                continue;
            }

            String license = "";
            int attempts = 0;
            boolean valid = false;
            while (attempts < 5) {
                System.out.print("  Enter License ID (Example: TG41520250001465): ");
                license = sc.next().toUpperCase();
                if (validateLicense(license)) {
                    valid = true;
                    showLoadingBar("VERIFYING LICENSE");
                    typeMessage("  LICENSE VERIFIED SUCCESSFULLY...\n", GREEN + BOLD);
                    break;
                } else {
                    attempts++;
                    typeMessage("  VERIFICATION FAILED: Invalid format.", RED);
                    System.out.println("  Attempts left: " + (5 - attempts));
                }
            }
            if (!valid) continue;

            printSectionSeparator();
            PlaceModule.printPowerHeader("RENTAL DURATION");
            System.out.println("  1. Hours Plan\n  2. Days Plan\n  3. Weeks Plan");
            int plan = getValidNumber(1, 3);
            if (plan == -1) continue;

            System.out.print("\n  Enter total duration: ");
            int duration = getValidNumber(1, 100);
            if (duration == -1) continue;

            String durationText = (plan == 1) ? duration + " Hours" : (plan == 2) ? duration + " Days" : duration + " Weeks";
            double rate = (choice==1)?(plan==1?100:plan==2?800:5000) : (choice==2)?(plan==1?50:plan==2?400:2500) : (plan==1?30:plan==2?300:2000);
            System.out.print("Add Damage Protection Insurance? (Rs.150) ("
                + GREEN + "YES" + RESET + "/"
                + RED + "NO" + RESET + "): ");
            double insurance = sc.next().equalsIgnoreCase("yes") ? 150 : 0;
            double total = (rate * duration) + insurance;

            printSectionSeparator();
            PlaceModule.printPowerHeader("BILLING SUMMARY");
            System.out.println("  [VEHICLE]   : " + vehicleType + " - " + model);
            if (vehicleType.equals("Car")) System.out.println("  [CAPACITY]  : " + seater + " Seater");
            System.out.println("  [PLAN]      : " + durationText);
            if (insurance > 0) System.out.println("  [INSURANCE] : Rs.150.0");
            typeMessage("  [TOTAL]     : Rs." + total, BOLD + GREEN);

            System.out.print("\n  Confirm Booking? (yes/no): ");
            if (sc.next().equalsIgnoreCase("no")) {

                typeMessage("  Transaction Aborted!", RED);

                while (true) {
                    System.out.print("\nDo you want to:\n1. Try Again\n2. Skip Vehicle Booking\nEnter choice: ");

                    String choiceInput = sc.next();

                    if (!choiceInput.matches("\\d+")) {
                        System.out.println("Invalid input! Enter numbers only.");
                        continue;
                    }
                    choice = Integer.parseInt(choiceInput);
                    if (choice == 1) {
                        break; // 🔁 goes back to booking loop
                    }
                    else if (choice == 2) {
                        return 0; // ❌ skip vehicle booking completely
                    }
                    else {
                        System.out.println("Invalid choice! Enter 1 or 2.");
                    }
                }

                continue; // retry booking
            }
            if (count >= 10) {
                System.out.println(RED + "Maximum booking limit reached!" + RESET);
                break;   // stops booking loop
            }
            
            vehicleTypes[count] = vehicleType;
            models[count] = model;
            plans[count] = durationText;
            amounts[count] = total;
            licenses[count] = license;

            count++;
            System.out.print("Do you want another vehicle booking? ("
                + GREEN + "YES" + RESET + "/"
                + RED + "NO" + RESET + "): ");
            if (!sc.next().equalsIgnoreCase("yes")) {
                printSectionSeparator();
                PlaceModule.printPowerHeader("OFFICIAL TRANSACTION RECEIPT");

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                typeMessage("  DATE/TIME : " + dtf.format(LocalDateTime.now()), WHITE_BRIGHT);

                System.out.println("\n  BOOKED VEHICLES:\n");

                double finalTotal = 0;

                for (int i = 0; i < count; i++) {

                    System.out.println("  =============================");
                    typeMessage("  BOOKING ID    : VR-" + (int)(Math.random()*900000 + 100000), WHITE_BRIGHT);
                    typeMessage("  VEHICLE MODEL : " + vehicleTypes[i] + " - " + models[i], WHITE_BRIGHT);
                    typeMessage("  PLAN          : " + plans[i], WHITE_BRIGHT);
                    typeMessage("  AMOUNT        : Rs." + amounts[i], GREEN + BOLD);
                    typeMessage("  LICENSE REF   : " + 
                        (licenses[i].length() >= 4 ? licenses[i].substring(0,4) : licenses[i]) 
                        + "XXXXXXXX", WHITE_BRIGHT);

                    finalTotal += amounts[i];
                }

                System.out.println("  =============================");

                typeMessage("  FINAL TOTAL   : Rs." + finalTotal, GREEN + BOLD);

                typeMessage("  BOOKING STATUS: CONFIRMED", GREEN + BOLD);

                System.out.println("\n" + CYAN + PlaceModule.repeat("═", SCREEN_WIDTH) + RESET);

                printCentered(PURPLE + BOLD + "THANK YOU FOR CHOOSING OUR RENTAL SERVICE" + RESET);
                printCentered(PURPLE + "Safe driving! Always wear your helmet/seatbelt." + RESET);
                for(int i=0;i<10;i++){
                    vehicleTypes[i] = null;
                    models[i] = null;
                    plans[i] = null;
                    amounts[i] = 0;
                    licenses[i] = null;
                }
                count = 0;
                // return total
                return finalTotal;
            }
        }
        return 0.0;
    }
}
class HotelBooking {
    static Scanner sc = new Scanner(System.in);

    static String bookingId = "";
    static int roomCost = 0;
    static int foodCost = 0;
    // ANSI COLOR CODES
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";
    public static final String PURPLE = "\u001B[35m";
    public static final String BLUE = "\u001B[34m";
    public static final String WHITE_BRIGHT = "\033[0;97m";
    static final int SCREEN_WIDTH = 90;
    static void printCentered(String text) {
        int padding = (SCREEN_WIDTH - text.length()) / 2;
        if (padding > 0) System.out.print(PlaceModule.repeat(" ", padding));
        System.out.println(text);
    }
    static void showBanner() {        
            System.out.println(CYAN + BOLD);
            printCentered("██╗  ██╗ ██████╗ ████████╗███████╗██╗");
            printCentered("██║  ██║██╔═══██╗╚══██╔══╝██╔════╝██║");
            printCentered("███████║██║   ██║   ██║   █████╗  ██║");
            printCentered("██╔══██║██║   ██║   ██║   ██╔══╝  ██║");
            printCentered("██║  ██║╚██████╔╝   ██║   ███████╗███████╗");
            printCentered("╚═╝  ╚═╝ ╚═════╝    ╚═╝   ╚══════╝╚══════╝");

            System.out.println();

            printCentered("███████╗███████╗██████╗ ██╗   ██╗██╗ ██████╗███████╗███████╗");
            printCentered("██╔════╝██╔════╝██╔══██╗██║   ██║██║██╔════╝██╔════╝██╔════╝");
            printCentered("███████╗█████╗  ██████╔╝██║   ██║██║██║     █████╗  ███████╗");
            printCentered("╚════██║██╔══╝  ██╔══██╗╚██╗ ██╔╝██║██║     ██╔══╝  ╚════██║");
            printCentered("███████║███████╗██║  ██║ ╚████╔╝ ██║╚██████╗███████╗███████║");
            printCentered("╚══════╝╚══════╝╚═╝  ╚═╝  ╚═══╝  ╚═╝ ╚═════╝╚══════╝╚══════╝");
        }
    public static double start() {
        showBanner();
        // ROOM YES / NO
        System.out.println(YELLOW + "Do you want a room ? (yes/no)" + RESET);
        PlaceModule.printPowerHeader("Hotel Booking");
        String room = sc.next().toLowerCase();

        if (room.equals("no")) {
            System.out.println(RED + "Exit Successfully..." + RESET);
            return 0.0;
        }

        if (!room.equals("yes")) {
            System.out.println(RED + "Invalid Input" + RESET);
            return 0.0;
        }

        // HOTELS
        String hotels[] = {
                "Taj Hotel",
                "Royal Palace",
                "Sea View Resort",
                "Grand Inn",
                "Sunrise Hotel"
        };
        int hotelChoice = 0;

        while (true) {
            System.out.println(BLUE + "\nAvailable Hotels" + RESET);

            for (int i = 0; i < hotels.length; i++) {
                System.out.println((i + 1) + " "+ hotels[i]);
            }

            System.out.print("Select Hotel : ");
            hotelChoice = sc.nextInt();

            if (hotelChoice >= 1 && hotelChoice <= 5) {
                System.out.println(GREEN + "Selected Hotel : "
                        + hotels[hotelChoice - 1] + RESET);
                break;
            } else {
                System.out.println(RED + "Invalid Hotel Choice Enter Again" + RESET);
            }
        }

        // HOTEL CATEGORY
        int category = 0;

        while (true) {
            System.out.println(BLUE + "\nRoom Categories" + RESET);
            System.out.println("1  AC");
            System.out.println("2  Non-AC");
            System.out.println("3  Deluxe");

            System.out.print("Enter Category : ");
            category = sc.nextInt();

            if (category >= 1 && category <= 3) {
                if (category == 1) {
                    roomCost = 3000;
                    System.out.println(GREEN + "AC Room Selected" + RESET);
                } else if (category == 2) {
                    roomCost = 2000;
                    System.out.println(GREEN + "Non-AC Room Selected" + RESET);
                } else {
                    roomCost = 5000;
                    System.out.println(GREEN + "Deluxe Room Selected" + RESET);
                }
                break;
            } else {
                System.out.println(RED + "Invalid Category Enter Again" + RESET);
            }
        }

        // ROOM TYPE
        int type = 0;

        while (true) {
            System.out.println(BLUE + "\nWhich Type Of Room Do You Want Sir/Madam ?" + RESET);
            System.out.println("1 1BHK");
            System.out.println("2 2BHK");
            System.out.println("3 3BHK");

            System.out.print("Enter Room Type : ");
            type = sc.nextInt();

            if (type >= 1 && type <= 3) {
                if (type == 1) {
                    roomCost += 1000;
                    System.out.println(GREEN + "1BHK Selected" + RESET);
                } else if (type == 2) {
                    roomCost += 2000;
                    System.out.println(GREEN + "2BHK Selected" + RESET);
                } else {
                    roomCost += 3000;
                    System.out.println(GREEN + "3BHK Selected" + RESET);
                }
                break;
            } else {
                System.out.println(RED + "Invalid Room Type Enter Again" + RESET);
            }
        }

        // ID PROOF
        int idChoice = 0;

        while (true) {
            System.out.println(BLUE + "\nIdentity Proof" + RESET);
            System.out.println("1  Aadhaar");
            System.out.println("2  PAN");
            System.out.println("3  Voter ID");

            System.out.print("Select Identity Proof : ");
            idChoice = sc.nextInt();

            if (idChoice >= 1 && idChoice <= 3) {
                break;
            } else {
                System.out.println(RED + "Invalid Choice Enter Again" + RESET);
            }
        }

        sc.nextLine();

        // VALIDATE ID
        while (true) {

            if (idChoice == 1) {
                System.out.print("Enter Aadhaar Number : ");
                String aadhaar = sc.nextLine();

                if (aadhaar.matches("[0-9]{12}")) {
                    break;
                } else {
                    System.out.println(RED + "Invalid Aadhaar Number" + RESET);
                }
            }

            else if (idChoice == 2) {
                System.out.print("Enter PAN Number : ");
                String pan = sc.nextLine();

                if (pan.matches("[A-Z]{5}[0-9]{4}[A-Z]{1}")) {
                    break;
                } else {
                    System.out.println(RED + "Invalid PAN Number" + RESET);
                }
            }

            else {
                System.out.print("Enter Voter ID : ");
                String voter = sc.nextLine();

                if (voter.matches("[A-Z]{3}[0-9]{7}")) {
                    break;
                } else {
                    System.out.println(RED + "Invalid Voter ID" + RESET);
                }
            }
        }

        // MOBILE NUMBER
        String mobile = "";

        while (true) {
            System.out.print("Enter Mobile Number : ");
            mobile = sc.next();

            if (mobile.matches("[9876][0-9]{9}")) {
                System.out.println(GREEN + "Mobile Number Verified" + RESET);
                break;
            } else {
                System.out.println(RED + "Invalid Mobile Number" + RESET);
            }
        }

        // AVAILABLE ROOMS
        int rooms[] = {
                101, 102, 103, 104, 105,
                201, 202, 203, 204, 205,
                301, 302, 303, 304, 305,
                401, 402, 403, 404, 405,
                501, 502, 503, 504, 505
        };

        System.out.println(BLUE + "\nAvailable Rooms" + RESET);

        for (int i = 0; i < rooms.length; i++) {
            System.out.print(rooms[i] + " ");
        }
        boolean validRoom = false;
        int roomNo;

        while (true) {
            System.out.print("\nPick One Room : ");
            roomNo = sc.nextInt();

            boolean found = false;
            for (int r : rooms) {
                if (r == roomNo) {
                    found = true;
                    break;
                }
            }

            if (found) break;
            else System.out.println("Invalid Room Number. Try again.");
        }
        bookingId = "BK" + (int)(Math.random() * 10000);

        System.out.println(GREEN + "\nRoom Booking Successful" + RESET);
        System.out.println(CYAN + "Booking ID : " + bookingId + RESET);

        // FOOD SECTION
        System.out.println(YELLOW + "\nDo You Want Food ? (yes/no)" + RESET);
        String food = sc.next().toLowerCase();

        if (food.equals("yes")) {

            boolean order = true;

            while (order) {
                int foodType = 0;
                boolean valid = false;

                while (!valid) {

                    PlaceModule.printPowerHeader("Food Categories");
                    System.out.println("1  Veg");
                    System.out.println("2  Non-Veg");
                    System.out.print("Enter choice: ");
                    foodType=sc.nextInt();
                    switch (foodType) {
                        case 1:
                            System.out.println("Veg selected");
                            valid = true;
                            break;

                        case 2:
                            System.out.println("Non-Veg selected");
                            valid = true;
                            break;

                        default:
                            System.out.println("Invalid Food Category");

                            System.out.println("\nDo you want to:");
                            System.out.println("1. Re-enter");
                            System.out.println("2. Exit");
                            System.out.print("Enter choice: ");

                            int opt = sc.nextInt();

                            if (opt == 2) {
                                System.out.println("Exiting...");
                                return 0.0; // stop program
                            }
                            // if opt == 1 → loop continues
                    }
                }
                if (foodType == 1) {

                    String veg[] = {
                            "Idli",
                            "Dosa",
                            "Meals",
                            "Paneer Curry",
                            "Veg Biryani"
                    };

                    int price[] = {40, 60, 120, 180, 150};

                    System.out.println(GREEN + "\nVeg Menu" + RESET);

                    for (int i = 0; i < veg.length; i++) {
                        System.out.println((i + 1) + ". "
                                + veg[i] + " - Rs" + price[i]);
                    }

                    while (true) {

                        System.out.print("\nSelect Item (1-5) or 0 to Exit : ");
                        int item = sc.nextInt();

                        if (item == 0) {
                            break;
                        }

                        else if (item >= 1 && item <= 5) {

                            System.out.print("Enter Quantity : ");
                            int qty = sc.nextInt();

                            int total = price[item - 1] * qty;
                            foodCost += total;

                            System.out.println(GREEN +
                                    veg[item - 1] + " Ordered Successfully"
                                    + RESET);

                            System.out.println(YELLOW +
                                    "\nSelect Another Item"
                                    + RESET);
                        }

                        else {
                            System.out.println(RED + "Invalid Item Enter Again" + RESET);
                        }
                    }
                }

                else if (foodType == 2) {

                    String nonveg[] = {
                            "Chicken Biryani",
                            "Mutton Curry",
                            "Fish Fry",
                            "Chicken Fry",
                            "Prawns Curry"
                    };

                    int price[] = {220, 300, 250, 200, 350};

                    System.out.println(GREEN + "\nNon-Veg Menu" + RESET);

                    for (int i = 0; i < nonveg.length; i++) {
                        System.out.println((i + 1) + ". "
                                + nonveg[i] + " - Rs" + price[i]);
                    }

                    while (true) {

                        System.out.print("\nSelect Item (1-5) or 0 to Exit : ");
                        int item = sc.nextInt();

                        if (item == 0) {
                            break;
                        }

                        else if (item >= 1 && item <= 5) {

                            System.out.print("Enter Quantity : ");
                            int qty = sc.nextInt();

                            int total = price[item - 1] * qty;
                            foodCost += total;

                            System.out.println(GREEN +
                                    nonveg[item - 1] + " Ordered Successfully"
                                    + RESET);

                            System.out.println(YELLOW +
                                    "\nSelect Another Item"
                                    + RESET);
                        }

                        else {
                            System.out.println(RED + "Invalid Item Enter Again" + RESET);
                        }
                    }
                }

                else {
                    System.out.println(RED + "Invalid Food Category" + RESET);
                }

                System.out.print("Do you want to order more? (yes/no): ");
                String more = sc.next().toLowerCase();

                if (more.equals("no")) {
                    order = false;
                }
            }
            // FINAL BILL
            PlaceModule.printPowerHeader("FINAL BILL");

            System.out.println("Booking ID : " + bookingId);
            System.out.println("Mobile Number : " + mobile);
            System.out.println("Room Number : " + roomNo);

            System.out.println("--------------------------------");
            System.out.println("Room Bill : Rs" + roomCost);
            System.out.println("Food Bill : Rs" + foodCost);
        }
        GlobalData.bill+=roomCost+foodCost;
        return GlobalData.bill;
    }
}
class GlobalData {
    static double bill = 0;
}
class BillingSystem {

    static Scanner sc = new Scanner(System.in);

    static final String RESET = "\u001B[0m";
    static final String CYAN = "\u001B[36m";
    static final String GREEN = "\u001B[32m";
    static final String YELLOW = "\u001B[33m";
    static final String RED = "\u001B[31m";
    static final String BOLD = "\u001B[1m";

    static void printCentered(String text) {
        int width = 90;
        int pad = (width - text.length()) / 2;
        if (pad > 0) System.out.print(PlaceModule.repeat(" ", pad));
        System.out.println(text);
    }

    static void showBanner() {
        System.out.print("\033[H\033[2J");
        System.out.flush();

        System.out.println(CYAN + BOLD);

        printCentered("██████╗ ██╗██╗     ██╗     ██╗███╗   ██╗ ██████╗ ");
        printCentered("██╔══██╗██║██║     ██║     ██║████╗  ██║██╔════╝ ");
        printCentered("██████╔╝██║██║     ██║     ██║██╔██╗ ██║██║  ███╗");
        printCentered("██╔══██╗██║██║     ██║     ██║██║╚██╗██║██║   ██║");
        printCentered("██████╔╝██║███████╗███████╗██║██║ ╚████║╚██████╔╝");
        printCentered("╚═════╝ ╚═╝╚══════╝╚══════╝╚═╝╚═╝  ╚═══╝ ╚═════╝ ");

        PlaceModule.printPowerHeader("PREMIUM BILLING MANAGEMENT SYSTEM");

        System.out.println(RESET);
    }

    static void animate(String message) {
        System.out.print("\n" + CYAN + message);
        for (int i = 0; i < 5; i++) {
            try { Thread.sleep(200); } catch (Exception e) {}
            System.out.print(".");
        }
        System.out.println(" DONE" + RESET);
    }

    static void generateBill() {

        showBanner();

        double subtotal = GlobalData.bill;

        if (subtotal == 0) {
            System.out.println(RED + "No items added for billing!" + RESET);
            return;
        }

        System.out.println(YELLOW + "\nSubtotal: Rs. " + subtotal + RESET);

        double discount = 0;

        if (subtotal >= 3000)
            discount = subtotal * 0.15;
        else if (subtotal >= 2000)
            discount = subtotal * 0.10;
        else if (subtotal >= 1000)
            discount = subtotal * 0.05;

        double finalAmount = subtotal - discount;

        System.out.println(GREEN + "Discount: Rs. " + discount);
        System.out.println("Final Amount: Rs. " + finalAmount + RESET);

        System.out.println("\nSelect Payment Method:");
        System.out.println("1. Cash");
        System.out.println("2. Card/UPI");

        int choice;
        while (true) {
            String input = sc.nextLine();
            if (input.matches("\\d+")) {
                choice = Integer.parseInt(input);
                if (choice == 1 || choice == 2) break;
            }
            System.out.print("Enter valid choice (1 or 2): ");
        }
        double received = 0;
        int txnId = (int) (Math.random() * 900000) + 100000;

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        if (choice == 1) {

            System.out.println("Please pay Rs. " + finalAmount);
            animate("Processing Cash Payment");

            received = finalAmount;

            System.out.println(GREEN + "\nPayment Successful (Cash)");
            System.out.println("Amount Received: Rs. " + received + RESET);

        } else {

            System.out.print("Enter Card/UPI ID: ");
            sc.next();

            animate("Processing Online Payment");

            received = finalAmount;

            System.out.println(GREEN + "\nPayment Successful (UPI/Card)");
            System.out.println("Transaction ID: " + txnId + RESET);
        }
        System.out.println("Date & Time: " + dtf.format(now));
        System.out.print("Do you want receipt? ("
                + GREEN + "YES" + RESET + "/"
                + RED + "NO" + RESET + "): ");
        String rec = sc.next();
        if (rec.equalsIgnoreCase("yes")) {

            animate("Generating Receipt");

            PlaceModule.printPowerHeader("BILLING RECEIPT");

            System.out.println("Total Bill   : Rs. " + subtotal);
            System.out.println("Discount     : Rs. " + discount);
            System.out.println("Final Amount : Rs. " + finalAmount);
            System.out.println("Paid Amount  : Rs. " + received);
            System.out.println("Transaction  : " + txnId);
            System.out.println("Date & Time  : " + dtf.format(now));
            System.out.println("Status       : SUCCESS");

        } else {
            animate("Finalizing Transaction");
            System.out.println(GREEN + "THANK YOU" + RESET);
        }

        GlobalData.bill = 0;
    }
}
public class TourismApp {
    static Scanner sc = new Scanner(System.in);
    // ================= COLORS (inside TourismApp only) =================
    static final String RESET = "\u001B[0m";
    static final String RED = "\u001B[31m";
    static final String GREEN = "\u001B[32m";
    static final String YELLOW = "\u001B[33m";
    static final String BLUE = "\u001B[34m";
    static final String CYAN = "\u001B[36m";
    static final String PURPLE = "\u001B[35m";
    static final String BOLD = "\u001B[1m";
    static void printCentered(String text) {
        int width = 90;
        int pad = (width - text.length()) / 2;
        if (pad > 0) System.out.print(PlaceModule.repeat(" ", pad));
        System.out.println(text);
    }
    static void showWelcomeBanner() {
        System.out.println(CYAN + BOLD);

        printCentered("██╗    ██╗███████╗██╗      ██████╗ ██████╗ ███╗   ███╗███████╗");
        printCentered("██║    ██║██╔════╝██║     ██╔════╝██╔═══██╗████╗ ████║██╔════╝");
        printCentered("██║ █╗ ██║█████╗  ██║     ██║     ██║   ██║██╔████╔██║█████╗  ");
        printCentered("██║███╗██║██╔══╝  ██║     ██║     ██║   ██║██║╚██╔╝██║██╔══╝  ");
        printCentered("╚███╔███╔╝███████╗███████╗╚██████╗╚██████╔╝██║ ╚═╝ ██║███████╗");
        printCentered(" ╚══╝╚══╝ ╚══════╝╚══════╝ ╚═════╝ ╚═════╝ ╚═╝     ╚═╝╚══════╝");
        System.out.println(RESET);
        System.out.println(GREEN + " Welcome to TOURISM EXPERIENCE SYSTEM " + RESET);
        System.out.println(GREEN + "Explore India with Smart Booking System!" + RESET);
    }
    static void loginFailed() {
        System.out.println(RED + BOLD);
        PlaceModule.printPowerHeader("LOGIN FAILED");
    }
    public static void main(String[] args) {
        AuthManager auth = new AuthManager();

        showWelcomeBanner();
        while (true) {

            System.out.println(CYAN + BOLD + "\n================ MENU ================" + RESET);
            System.out.println(YELLOW + "1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit" + RESET);
            System.out.println(CYAN + "======================================" + RESET);
            System.out.print("Choose option: ");
            String option = sc.nextLine().trim();

            // ================= REGISTER =================
            if (option.equals("2")) {

                System.out.println("\n========== REGISTER FORM ==========");

                System.out.print("Full Name : ");
                String fullName = sc.nextLine();

                System.out.print("Username  : ");
                String username = sc.nextLine();
                System.out.print("Password  : ");
                String password = sc.nextLine();
                System.out.println("Phone Number: ");
                String phone = sc.nextLine();
                auth.register(username, password, fullName, phone);
            }

            // ================= LOGIN =================
            else if (option.equals("1")) {

                boolean loggedIn = false;

                while (!loggedIn) {

                    System.out.println(CYAN + BOLD);
                    PlaceModule.printPowerHeader("LOGIN FORM");
                    System.out.println(RESET);

                    System.out.print(YELLOW + " Username: " + RESET);
                    String username = sc.nextLine().trim();

                    System.out.print(YELLOW + " Password: " + RESET);
                    String password = sc.nextLine().trim();

                    System.out.println(CYAN + "----------------------------------------" + RESET);
                    // FORM VALIDATION
                    if (username.isEmpty() || password.isEmpty()) {
                        System.out.println("Fields cannot be empty!");
                        continue;
                    }
                    loggedIn = auth.login(username, password, sc);

                    if (loggedIn) {
                        showWelcomeBanner();
                        PlaceModule.start(auth);
                        return;
                    } 
                    else {

                        System.out.println(RED + BOLD);
                        System.out.println(YELLOW + "What would you like to do?" + RESET);
                        System.out.println(GREEN + "1. Retry Login");
                        System.out.println("2. Forgot Password");
                        System.out.println("3. Forgot Username");
                        System.out.println("4. Back to Main Menu" + RESET);

                        System.out.print(CYAN + "Choose option: " + RESET);
                        String ch = sc.nextLine();

                        if (ch.equals("2")) {
                            System.out.print("Enter registered mobile number: ");
                            String phone = sc.nextLine();
                            auth.resetPasswordByPhone(phone, sc);
                            continue;
                        }

                        else if (ch.equals("3")) {
                            System.out.print("Enter your registered mobile number: ");
                            String phone = sc.nextLine();
                            auth.findUsernameByPhone(phone);
                            continue;
                        }

                        else {
                            break; // back to main menu
                        }
                    }
                }
            }
            // ================= EXIT =================
            else if (option.equals("3")) {
                System.out.println("Thank you for using Tourism App!");
                break;
            }

            else {
                System.out.println("Invalid option! Try again.");
            }
        }
    }
}