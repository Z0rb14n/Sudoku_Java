package util;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import static java.awt.event.MouseEvent.*;

// Represents a wrapper for the robot
public class RobotWrapper extends Robot {
    private static boolean isNonMac;
    private static final int NORMAL_DELAY = 20;
    private static final String ALLCAPS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ~%(){}|<>?";
    private static final String CAPS_CORRESPONDING = "abcdefghijklmnopqrstuvwxyz`590[]\\,./";
    private static final String ALLALTS = "º¡™£¢∞§¶•ªå∫ç∂ƒ©˙ˆ∆˚¬µ˜øπœ®ß†¨√∑≈¥Ω–";
    private static final String ALTS_CORRESPONDING = "0123456789abcdfghijklmnopqrstuvwxyz-";
    private static final String ALLSAlts = "ÅıÇÎ´Ï˝ÓˆÔÒÂ˜Ø∏Œ‰Íˇ¨◊„˛Á¸‚⁄€‹›ﬁﬂ‡°·—±”’»ÚÆ¯˘¿";
    private static final String SAlts_CORRESPONDING = "abcdefghijklmnopqrstuvwxyz0123456789-=[]\\;',./";
    private static final HashMap<Character, Integer> Keys = new HashMap<>();
    private static final HashMap<Character, Character> Caps = new HashMap<>();
    private static final HashMap<Character, Character> Alts = new HashMap<>();
    private static final HashMap<Character, Character> SAlt = new HashMap<>();
    private final static int DISPLAY_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
    private final static int DISPLAY_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;

    // Constructor. Yeah. About that..
    public RobotWrapper() throws AWTException {
        this(true);
    }

    // Constructor, but a boolean if you use a delay
    public RobotWrapper(boolean doDelay) throws AWTException {
        this(doDelay, NORMAL_DELAY);
    }

    // Constructor, but a boolean if you use a delay
    public RobotWrapper(boolean doDelay, int delayAmount) throws AWTException {
        super();
        initLettersNonMac();
        if (doDelay) setAutoDelay(delayAmount);
    }

    //<editor-fold desc="Convenience Code">
    // MODIFIES: this
    // EFFECTS: initializes the keys/caps/Alts/Salts for Non-Mac operating Systems
    private static void initLettersNonMac() {
        isNonMac = true;
        initUniversal();
        // INIT WINDOWS KEY - I CANNOT TEST SINCE I'M A MAC USER
        System.gc();
    }

    // MODIFIES: this
    // EFFECTS: initializes the Keys/caps/ALts/Salts
    private static void initLetters() {
        isNonMac = false;
        initUniversal();
        Keys.put((char) 8984, KeyEvent.VK_META); //can use ⌘, mac command key, int code 8984
        assert (ALLALTS.length() == ALTS_CORRESPONDING.length());
        //WARNING: I,N,U NEEDS TO BE PRESSED TWICE
        for (int i = 0; i < ALLALTS.length(); i++) {
            Alts.put(ALLALTS.charAt(i), ALTS_CORRESPONDING.charAt(i));
        }
        assert (ALLSAlts.length() == SAlts_CORRESPONDING.length());
        for (int i = 0; i < ALLSAlts.length(); i++) {
            SAlt.put(ALLSAlts.charAt(i), SAlts_CORRESPONDING.charAt(i));
        }
        System.gc();
    }

    // MODIFIES: this
    // EFFECTS: initializes the keys/caps only
    private static void initUniversal() {
        for (char i = 'a'; i < 'z' + 1; i++) {
            Keys.put(i, i - 'a' + KeyEvent.VK_A);
        }
        for (char i = '0'; i < '9' + 1; i++) {
            Keys.put(i, i - '0' + KeyEvent.VK_0);
        }
        //essentially, shift the char int values to that of the key event VK values.
        Keys.put('`', KeyEvent.VK_BACK_QUOTE);
        Keys.put('-', KeyEvent.VK_MINUS);
        Keys.put('=', KeyEvent.VK_EQUALS);
        Keys.put('!', KeyEvent.VK_EXCLAMATION_MARK);
        Keys.put('@', KeyEvent.VK_AT);
        Keys.put('#', KeyEvent.VK_NUMBER_SIGN);
        Keys.put('$', KeyEvent.VK_DOLLAR);
        Keys.put('^', KeyEvent.VK_CIRCUMFLEX);
        Keys.put('&', KeyEvent.VK_AMPERSAND);
        Keys.put('*', KeyEvent.VK_ASTERISK);
        Keys.put('_', KeyEvent.VK_UNDERSCORE);
        Keys.put('+', KeyEvent.VK_PLUS);
        Keys.put('\t', KeyEvent.VK_TAB);
        Keys.put('\n', KeyEvent.VK_ENTER);
        Keys.put('[', KeyEvent.VK_OPEN_BRACKET);
        Keys.put(']', KeyEvent.VK_CLOSE_BRACKET);
        Keys.put('\\', KeyEvent.VK_BACK_SLASH);
        Keys.put(':', KeyEvent.VK_COLON);
        Keys.put(';', KeyEvent.VK_SEMICOLON);
        Keys.put('\'', KeyEvent.VK_QUOTE);
        Keys.put('"', KeyEvent.VK_QUOTEDBL);
        Keys.put(',', KeyEvent.VK_COMMA);
        Keys.put('.', KeyEvent.VK_PERIOD);
        Keys.put('/', KeyEvent.VK_SLASH);
        Keys.put(' ', KeyEvent.VK_SPACE);
        assert (ALLCAPS.length() == CAPS_CORRESPONDING.length());
        for (int i = 0; i < ALLCAPS.length(); i++) {
            Caps.put(ALLCAPS.charAt(i), CAPS_CORRESPONDING.charAt(i));
        }
    }

    /**
     * Clicks the mouse at specified position
     *
     * @param x x position to click at
     * @param y y position to click at
     */
    public void clickAt(int x, int y) {
        Point location = MouseInfo.getPointerInfo().getLocation();
        int initialX = (int) location.getX();
        int initialY = (int) location.getY();
        mouseMove(x, y);
        button1();
        mouseMove(initialX, initialY);
    }

    /**
     * Clicks the mouse at specified position
     *
     * @param x          x position to click at
     * @param y          y position to click at
     * @param mouseEvent mouse type
     */
    public void clickAt(int x, int y, MouseEvent mouseEvent) {
        if (mouseEvent.getButton() != BUTTON1 && mouseEvent.getButton() != BUTTON2 && mouseEvent.getButton() != BUTTON3) {
            throw new IllegalArgumentException("clickAt called with invalid MouseEvent " + mouseEvent);
        }
        Point location = MouseInfo.getPointerInfo().getLocation();
        int initialX = (int) location.getX();
        int initialY = (int) location.getY();
        mouseMove(x, y);
        if (mouseEvent.getButton() == BUTTON1) {
            button1();
        } else if (mouseEvent.getButton() == BUTTON2) {
            button2();
        } else if (mouseEvent.getButton() == BUTTON3) {
            button3();
        }
        mouseMove(initialX, initialY);
    }

    /**
     * Convenience: presses Mouse1 and releases it
     */
    public void button1() {
        mousePress(InputEvent.BUTTON1_DOWN_MASK);
        mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    /**
     * Convenience: presses Mouse2 and releases it
     */
    public void button2() {
        mousePress(InputEvent.BUTTON2_DOWN_MASK);
        mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
    }

    /**
     * Convenience: presses Mouse3 and releases it
     */
    public void button3() {
        mousePress(InputEvent.BUTTON3_DOWN_MASK);
        mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
    }

    /**
     * Runs keyPress but holds shift before running
     *
     * @param a_key key code of key to press
     */
    public void keyPressCapitalized(int a_key) {
        keyPress(KeyEvent.VK_SHIFT);
        keyPressRelease(a_key);
        keyRelease(KeyEvent.VK_SHIFT);
    }

    /**
     * Presses and releases a key
     *
     * @param a_key key code of key to press and release
     */
    public void keyPressRelease(int a_key) {
        keyPress(a_key);
        keyRelease(a_key);
    }

    /**
     * Holds down key from key code of hold and then types all keys from key codes given
     *
     * @param hold  key code of key to hold down
     * @param types key codes of keys to type
     */
    public void holdType(int hold, int... types) {
        keyPress(hold);
        for (int type : types) {
            keyPressRelease(type);
        }
        keyRelease(hold);
    }

    /**
     * Holds down all keys in hold and then types the string
     *
     * @param hold  list of keys to hold down
     * @param types String to enter
     */
    public void holdType(int[] hold, String types) {
        for (int lol : hold) {
            keyPress(lol);
        }
        for (int i = 0; i < types.length(); i++) {
            simplerType(types.charAt(i));
        }
        for (int lol : hold) {
            keyRelease(lol);
        }
    }

    /**
     * Holds down all keys in hold, and then types the integer key codes
     *
     * @param hold  list of integer key codes to hold down
     * @param types integer key codes of keys to type
     */
    public void holdType(int[] hold, int... types) {
        for (int lol : hold) {
            keyPress(lol);
        }
        for (int type : types) {
            keyPressRelease(type);
        }
        for (int lol : hold) {
            keyRelease(lol);
        }
    }

    /**
     * Holds down all keys in hold and then types the characters
     *
     * @param hold  list of integer key codes of keys to hold down
     * @param types list of characters to type
     */
    public void holdType(int[] hold, char... types) {
        for (int lol : hold) {
            keyPress(lol);
        }
        for (char type : types) {
            simplerType(type);
        }
        for (int lol : hold) {
            keyRelease(lol);
        }
    }

    /**
     * Holds down keycode hold and then types all characters
     *
     * @param hold  integer key code of key to hold down
     * @param types characters to type afterwards
     */
    public void holdType(int hold, char... types) {
        keyPress(hold);
        for (char type : types) {
            simplerType(type);
        }
        keyRelease(hold);
    }

    /**
     * Holds down key from key code hold and then types the string
     *
     * @param hold integer keycode of key to hold
     * @param list String to type
     */
    public void holdType(int hold, String list) {
        if (list == null || list.length() == 0) {
            return;
        }
        keyPress(hold);
        for (int i = 0; i < list.length(); i++) {
            simplerType(list.charAt(i));
        }
        keyRelease(hold);
    }

    /**
     * Types a character by comparing it to the existing HashMaps
     *
     * @param character character to type
     * @throws IllegalArgumentException upon entering a character that cannot be typed
     */
    public void simplerType(char character) {
        if (Keys.containsKey(character)) {
            keyPressRelease(Keys.get(character));
        } else if (Caps.containsKey(character)) {
            keyPressCapitalized(Keys.get(Caps.get(character)));
        } else if (!isNonMac) {
            if (Alts.containsKey(character)) {
                if (character == 'ˆ' || character == '˜' || character == '¨') {
                    holdType(KeyEvent.VK_ALT, Alts.get(character), Alts.get(character)); //alt-i,alt-n,alt-u needs double press
                } else {
                    holdType(KeyEvent.VK_ALT, Alts.get(character));
                }
            } else if (SAlt.containsKey(character)) {
                holdType(new int[]{KeyEvent.VK_ALT, KeyEvent.VK_SHIFT}, character);
            } else {
                throw new IllegalArgumentException("Cannot type character " + character);
            }
        }
    }

    /**
     * Runs simplerType() on every character in the string
     *
     * @param a string to type
     */
    public void simplerType(String a) {
        if (a == null || a.length() == 0) {
            return;
        }
        for (int i = 0; i < a.length(); i++) {
            simplerType(a.charAt(i));
        }
    }

    /**
     * Returns mouse position lol
     *
     * @return point (location) of mouse pointer
     */
    public static Point getMousePosition() {
        return MouseInfo.getPointerInfo().getLocation();
    }
    //</editor-fold>

    /**
     * Takes a screenshot of the whole screen and saves to the given path.
     */
    public BufferedImage screenShot() {
        return screenShot(new Rectangle(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT));
    }

    /**
     * Takes a screenshot of the given area.
     *
     * @param area section of screen to take a screenshot of
     */
    public BufferedImage screenShot(Rectangle area) {
        return super.createScreenCapture(area);
    }

    /**
     * Takes a screenshot of a given area specified by a top left point, width and height.
     *
     * @param x      top left horizontal coordinate of screenshot - starts at 0
     * @param y      top left vertical coordinate of screenshot - starts at 0
     * @param width  width of screenshot
     * @param height height of screenshot
     */
    public BufferedImage screenShot(int x, int y, int width, int height) {
        return screenShot(new Rectangle(x, y, width, height));
    }
}
