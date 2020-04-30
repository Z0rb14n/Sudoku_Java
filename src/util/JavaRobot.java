/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.tess4j.*; //enables OCR on text
/**
 *
 * @author adminasaurus
 */
public class JavaRobot /* implements KeyListener, not implemeneted lol*/{
    //<editor-fold defaultstate="collapsed" desc="Global variables">
    static HashMap<Character, Integer> Keys = new HashMap<>();
    static HashMap<Character, Character> Caps = new HashMap<>();
    static HashMap<Character, Character> Alts = new HashMap<>();
    static HashMap<Character, Character> SAlt = new HashMap<>();
    final static String DESKTOP = "/Users/adminasaurus/Desktop/";
    final static int DISPLAYHEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
    final static int DISPLAYWIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    final static byte BUTTON1 = 1;
    final static byte BUTTON2 = 2;
    final static byte BUTTON3 = 3;
    static int mouseX = -1;
    static int mouseY = -1;
    static int rate = 1000/60;
    
    //pixels are indexed at 0
    final static int NORMALDELAY = 100;
    Robot bot;
    //</editor-fold>
    public JavaRobot(){
        setupKeys();
        try {
          bot = new Robot();
        } 
        catch (Throwable e) {
            System.out.println("Robot could not be initialized.");
            e.printStackTrace();
        }
    }
    public void start(){
        //add test for key input, but it requires a WINDOW of sorts REEEEEEEE
        // put all repeating code here
        final Runnable lol = new Runnable() {
            @Override
            public void run() {
                Point mousePos = MouseInfo.getPointerInfo().getLocation();
                mouseX = (int) mousePos.getX();
                mouseY = (int) mousePos.getY();
                //System.out.println(mouseX + ", " + mouseY);
            }
        };
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(lol, 0, rate, TimeUnit.MILLISECONDS);
    }
    //<editor-fold defaultstate="collapsed" desc="Convenience Code">
    /**
     * Delays the execution of the thread in milliseconds
     * @param ms the time to wait in milliseconds
     */
    public void delay(int ms){
        if (ms < 0) throw new IllegalArgumentException("delay() called with negative value");
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            Logger.getLogger(JavaRobot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Sets up the HashMaps to contain the correct key codings
     */
    protected static void setupKeys() {
        for(char i = 'a'; i < 'z' + 1; i++) Keys.put(i, i-'a'+KeyEvent.VK_A);
        for(char i = '0'; i < '9' + 1; i++) Keys.put(i, i-'0' + KeyEvent.VK_0);
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
        Keys.put(',', KeyEvent.VK_COLON);
        Keys.put('\'', KeyEvent.VK_QUOTE); 
        Keys.put('"', KeyEvent.VK_QUOTEDBL); 
        Keys.put(',', KeyEvent.VK_COMMA); 
        Keys.put('.', KeyEvent.VK_PERIOD);
        Keys.put('/', KeyEvent.VK_SLASH); 
        Keys.put(' ', KeyEvent.VK_SPACE); 
        Keys.put((char)8984, KeyEvent.VK_META); //can use ⌘, mac command key, int code 8984
        final String ALLCAPS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ~%(){}|<>?";
        final String CAPS_CORRESPONDING = "abcdefghijklmnopqrstuvwxyz`590[]\\,./";
        assert(ALLCAPS.length() == CAPS_CORRESPONDING.length());
        for(int i = 0; i < ALLCAPS.length(); i++)Caps.put(ALLCAPS.charAt(i),CAPS_CORRESPONDING.charAt(i));
        final String ALLALTS = "º¡™£¢∞§¶•ªå∫ç∂ƒ©˙ˆ∆˚¬µ˜øπœ®ß†¨√∑≈¥Ω–";
        final String ALTS_CORRESPONDING = "0123456789abcdfghijklmnopqrstuvwxyz-";
        assert(ALLALTS.length() == ALTS_CORRESPONDING.length());
        //WARNING: I,N,U NEEDS TO BE PRESSED TWICE
        for(int i = 0; i < ALLALTS.length(); i++) Alts.put(ALLALTS.charAt(i), ALTS_CORRESPONDING.charAt(i));
        final String ALLSAlts = "ÅıÇÎ´Ï˝ÓˆÔÒÂ˜Ø∏Œ‰Íˇ¨◊„˛Á¸‚⁄€‹›ﬁﬂ‡°·—±”’»ÚÆ¯˘¿";
        final String SAlts_CORRESPONDING = "abcdefghijklmnopqrstuvwxyz0123456789-=[]\\;\',./";
        assert(ALLSAlts.length() == SAlts_CORRESPONDING.length());
        for(int i = 0; i < ALLSAlts.length(); i++) SAlt.put(ALLSAlts.charAt(i), SAlts_CORRESPONDING.charAt(i));
        System.gc();
    }
    /**
     * Clicks the mouse at specified position
     * @param x x position to click at
     * @param y y position to click at
     */
    public void clickAt(int x, int y){
        Point lol = MouseInfo.getPointerInfo().getLocation();
        int lolx = (int) lol.getX();
        int loly = (int )lol.getY();
        mouseMove(x,y);
        button1();
        mouseMove(lolx,loly);
    }
    /**
     * Clicks the mouse at specified position
     * @param x x position to click at
     * @param y y position to click at
     * @param MouseEvent mouse event to click e.g. mouse1
     */
    public void clickAt(int x, int y, byte MouseEvent){
        if (MouseEvent != BUTTON1 && MouseEvent != BUTTON2 && MouseEvent != BUTTON3) throw new IllegalArgumentException("clickAt called with invalid MouseEvent " + MouseEvent);
        Point lol = MouseInfo.getPointerInfo().getLocation();
        int lolx = (int) lol.getX();
        int loly = (int )lol.getY();
        mouseMove(x,y);
        if (MouseEvent == BUTTON1) button1();
        if (MouseEvent == BUTTON2) button2();
        if (MouseEvent == BUTTON3) button3();
        mouseMove(lolx,loly);
    }
    public void clickAt(int x, int y, boolean yes){
        if (yes == false){
            mouseMove(x,y);
            button1();
        }else {
            clickAt(x,y);
        }
    }
    /**
     * Runs simplerType() on every character in the string
     * @param a 
     */
    public void simplerType(String a) {
        if (a == null || a.length() == 0) return;
        for (int i = 0; i < a.length(); i++) simplerType(a.charAt(i));
    }
    /**
     * Convenience: presses Mouse1
     */
    public void button1() {
        bot.mousePress(InputEvent.BUTTON1_MASK);
        bot.mouseRelease(InputEvent.BUTTON1_MASK);
    }
    /**
     * Convenience: Presses Mouse2
     */
    public void button2() {
        bot.mousePress(InputEvent.BUTTON2_MASK);
        bot.mouseRelease(InputEvent.BUTTON2_MASK);
    }
    /**
     * Convenience: Presses Mouse3
     */
    public void button3() {
        bot.mousePress(InputEvent.BUTTON3_MASK);
        bot.mouseRelease(InputEvent.BUTTON3_MASK);
    }
    /**
     * Presses and Releases the key of code a_key
     * @param a_key integer keyCode of key to press
     */
    public void keyPress(int a_key) {
        bot.keyPress(a_key);
        bot.keyRelease(a_key);
    }
    /**
     * Runs keyPress but holds shift before running
     * @param a_key keycode of key to press
     */
    public void keyPressCapitalized(int a_key) {
        bot.keyPress(KeyEvent.VK_SHIFT);
        keyPress(a_key);
        bot.keyRelease(KeyEvent.VK_SHIFT);
    }
    /**
     * Delays the robot with NORMALDELAY milliseconds
     */
    public void delayBot() {
        bot.delay(NORMALDELAY);
    }
    /**
     * Moves the mouse to a specified location
     * @param x horizontal position to move the mouse to
     * @param y vertical position to move the mouse to
     */
    public void mouseMove(int x, int y) {
        bot.mouseMove(x, y);
    }
    /**
     * Scrolls the mouse wheel by a specified amount
     * @param amount amount to scroll the mousewheel
     */
    public void scrollWheel(int amount) {
        bot.mouseWheel(amount);
    }
    /**
     * Holds down keycode Hold and then types all keycodes types
     * @param hold keycode of key to hold down
     * @param types keycodes of keys to press
     */
    public void holdType(int hold, int... types){
        bot.keyPress(hold);
        for(int type : types) keyPress(type);
        bot.keyRelease(hold);
    }
    /**
     * Holds down keycode hold and then types all characters
     * @param hold integer keycode to hold down
     * @param types characters to type afterwards
     */
    public void holdType(int hold, char... types){
        bot.keyPress(hold);
        for(char type : types) simplerType(type);
        bot.keyRelease(hold);
    }
    /**
     * Holds down keycode hold and then types the string
     * @param hold integer keycode to hold
     * @param list String to type
     */
    public void holdType(int hold, String list){
        if (list == null || list.length() == 0) return;
        bot.keyPress(hold);
        for(int i = 0; i < list.length(); i++) simplerType(list.charAt(i));
        bot.keyRelease(hold);
    }
    /**
     * Holds down all keys in hold, and then types the integer keycodes
     * @param hold list of integer keycodes to hold down
     * @param types integer keycodes to type
     */
    public void holdType(int[] hold, int... types){
        for(int lol: hold) bot.keyPress(lol);
        for(int type: types) keyPress(type);
        for(int lol: hold) bot.keyRelease(lol);
    }
    /**
     * Holds down all keys in hold and then types the characters
     * @param hold list of integer keycodes to hold down
     * @param types list of characters to type
     */
    public void holdType(int[] hold, char... types){
        for(int lol: hold) bot.keyPress(lol);
        for(char type: types) simplerType(type);
        for(int lol: hold) bot.keyRelease(lol);
    }
    /**
     * Holds down all keys in hold and then types the string
     * @param hold list of keys to hold down
     * @param types String to enter
     */
    public void holdType(int[] hold, String types){
        for(int lol: hold) bot.keyPress(lol);
        for(int i = 0; i < types.length(); i++) simplerType(types.charAt(i));
        for(int lol: hold) bot.keyRelease(lol);
    }
    /**
     * Types a character by comparing it to the existing HashMaps
     * @param character character to type
     * @throws IllegalArgumentException upon entering a character that cannot be typed
     */
    public void simplerType(char character){
        if (Keys.containsKey(character)) keyPress(Keys.get(character));
        else if (Caps.containsKey(character)) keyPressCapitalized(Keys.get(Caps.get(character)));
        else if (Alts.containsKey(character)){
          if (character == 'ˆ' || character == '˜' || character == '¨') holdType(KeyEvent.VK_ALT,Alts.get(character),Alts.get(character)); //alt-i,alt-n,alt-u needs double press
          else holdType(KeyEvent.VK_ALT,Alts.get(character));
        } else if (SAlt.containsKey(character)) holdType(new int[]{KeyEvent.VK_ALT,KeyEvent.VK_SHIFT}, character);
        else throw new IllegalArgumentException("Cannot type character " + character);
    }
    public void simplerType(byte number){
        simplerType(number + "");
    }
    /**
     * Gets the color at a given pixel on the screen
     * @param x x coordinate on screen
     * @param y y coordinate on screen
     * @return color of pixel at (x,y)
     */
    public Color colorAt(int x, int y){
        return bot.getPixelColor(x,y);
    }
    /**
     * Gets the color at current mouse position
     * @return Color of pixel at current mouse position
     */
    public Color colorAt(){
        Point lol = MouseInfo.getPointerInfo().getLocation();
        int lolx = (int) lol.getX();
        int loly = (int )lol.getY();
        return bot.getPixelColor(lol.x,lol.y);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Screenshot Code">
    /**
     * Returns a bufferedImage screenshot of whole screen
     * @return contents of whole screen
     */
    public BufferedImage screenShot(){
        try {
          return bot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        } 
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Takes a screenshot with the given format specified by the path
     * Format is one of "jpeg","jpg","png","bmp","wbmp","gif"
     * Saves screenshot to the given path
     * Screenshot is of the whole screen
     * @param path Path to save screenshot to
     */
    public void screenShot(String path) {
        if (path == null || path.length() < 5) throw new IllegalArgumentException("BRUH WHAT");
        String ext4 = path.substring(path.length() - 4, path.length());
        String ext3 = path.substring(path.length()-3, path.length());
        boolean testJPEG = ext4.compareToIgnoreCase("jpeg") == 0;
        boolean testJPG = ext3.compareToIgnoreCase("jpg") == 0;
        boolean testPNG = ext3.compareToIgnoreCase("png") == 0;
        boolean testBMP = ext3.compareToIgnoreCase("bmp") == 0;
        boolean testWBMP = ext4.compareToIgnoreCase("wbmp") == 0;
        boolean testGIF = ext3.compareToIgnoreCase("gif") == 0;
        String mode = null;
        if (testJPEG) mode = "jpeg";
        else if (testJPG) mode = "jpg";
        else if (testPNG) mode = "png";
        else if (testBMP) mode = "bmp";
        else if (testWBMP) mode = "wbmp";
        else if (testGIF) mode = "gif";
        else throw new IllegalArgumentException("BRUH WHAT");
        File output = new File(path);
        try {
          ImageIO.write(bot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())), mode, output);
        } 
        catch(Exception e) {
          e.printStackTrace();
        }
    }
    /**
     * Returns a bufferedImage screenshot of the given rectangle
     * @param area area of screen to screenshot
     * @return contents of screen inside rectangle
     */
    public BufferedImage screenShot(Rectangle area){
        try {
          bot.createScreenCapture(area);
        } 
        catch(Exception e) {
          e.printStackTrace();
        }
        return null;
    }
    /**
     * Takes a screenshot with the given format specified by the path
     * Format is one of "jpeg","jpg","png","bmp","wbmp","gif"
     * The screenshot is of the specific area specified by the rectangle
     * Saves to that path
     * @param area Section of screen to take a screenshot of
     * @param path Path to save screenshot to
     */
    public void screenShot(Rectangle area, String path) {
        if (path == null || path.length() < 5) throw new IllegalArgumentException("BRUH WHAT");
        String ext4 = path.substring(path.length() - 4, path.length());
        String ext3 = path.substring(path.length()-3, path.length());
        boolean testJPEG = ext4.compareToIgnoreCase("jpeg") == 0;
        boolean testJPG = ext3.compareToIgnoreCase("jpg") == 0;
        boolean testPNG = ext3.compareToIgnoreCase("png") == 0;
        boolean testBMP = ext3.compareToIgnoreCase("bmp") == 0;
        boolean testWBMP = ext4.compareToIgnoreCase("wbmp") == 0;
        boolean testGIF = ext3.compareToIgnoreCase("gif") == 0;
        String mode = null;
        if (testJPEG) mode = "jpeg";
        else if (testJPG) mode = "jpg";
        else if (testPNG) mode = "png";
        else if (testBMP) mode = "bmp";
        else if (testWBMP) mode = "wbmp";
        else if (testGIF) mode = "gif";
        else throw new IllegalArgumentException("BRUH WHAT");
        File output = new File(path);
        try {
          ImageIO.write(bot.createScreenCapture(area), mode, output);
        } 
        catch(Exception e) {
          e.printStackTrace();
        }
    }
    /**
     * Returns a screenshot of given area
     * Screenshot is of the screen specified by x/y/height/height coords
     * @param x top left horizontal coordinate of screenshot - starts at 0?
     * @param y top left vertical coordinate of screenshot - starts at 0?
     * @param width width of screenshot
     * @param height height of screenshot
     * @return contents of screen
     */
    public BufferedImage screenShot(int x, int y, int width, int height){
        try {
            return bot.createScreenCapture(new Rectangle(x,y,width,height));
        } 
        catch(Exception e) {
          e.printStackTrace();
        }
        return null;
    }
    /**
     * Returns a screenshot of given area
     * Screenshot is of the screen specified by x/y/height/height coords
     * @param topLeft top left coordinate
     * @param width width of screenshot
     * @param height height of screenshot
     * @return contents of screen
     */
    public BufferedImage screenShot(Point topLeft, int width, int height){
        try {
            return bot.createScreenCapture(new Rectangle((int) topLeft.getX(),(int) topLeft.getY(),width,height));
        } 
        catch(Exception e) {
          e.printStackTrace();
        }
        return null;
    }
    /**
     * Takes a screenshot with the given format specified by the path
     * Format is one of "jpeg","jpg","png","bmp","wbmp","gif"
     * Saves screenshot to the given path
     * Screenshot is of the screen specified by x/y/height/height coords
     * @param x top left horizontal coordinate of screenshot - starts at 0?
     * @param y top left vertical coordinate of screenshot - starts at 0?
     * @param width width of screenshot
     * @param height height of screenshot
     * @param path path to save screenshot to
     */
    public void screenShot(int x, int y, int width, int height, String path) {
        if (path == null || path.length() < 5) throw new IllegalArgumentException("BRUH WHAT");
        String ext4 = path.substring(path.length() - 4, path.length());
        String ext3 = path.substring(path.length()-3, path.length());
        boolean testJPEG = ext4.compareToIgnoreCase("jpeg") == 0;
        boolean testJPG = ext3.compareToIgnoreCase("jpg") == 0;
        boolean testPNG = ext3.compareToIgnoreCase("png") == 0;
        boolean testBMP = ext3.compareToIgnoreCase("bmp") == 0;
        boolean testWBMP = ext4.compareToIgnoreCase("wbmp") == 0;
        boolean testGIF = ext3.compareToIgnoreCase("gif") == 0;
        String mode = null;
        if (testJPEG) mode = "jpeg";
        else if (testJPG) mode = "jpg";
        else if (testPNG) mode = "png";
        else if (testBMP) mode = "bmp";
        else if (testWBMP) mode = "wbmp";
        else if (testGIF) mode = "gif";
        else throw new IllegalArgumentException("BRUH WHAT");
        File output = new File(path);
        try {
          ImageIO.write(bot.createScreenCapture(new Rectangle(x,y,width,height)), mode, output);
        } 
        catch(Exception e) {
          e.printStackTrace();
        }
    }
    /**
     * Takes a screenshot with the given format specified by the path
     * Format is one of "jpeg","jpg","png","bmp","wbmp","gif"
     * Saves screenshot to the given path
     * Screenshot is of the screen specified by x/y/height/height coords
     * @param x top left horizontal coordinate of screenshot - starts at 0?
     * @param y top left vertical coordinate of screenshot - starts at 0?
     * @param width width of screenshot
     * @param height height of screenshot
     * @param path_file File object containing path to save screenshot to
     */
    public void screenShot(int x, int y, int width, int height, File path_file) {
      String path = path_file.getAbsolutePath();
      String ext4 = path.substring(path.length() - 4, path.length());
      String ext3 = path.substring(path.length()-3, path.length());
      boolean testJPEG = ext4.compareToIgnoreCase("jpeg") == 0;
      boolean testJPG = ext3.compareToIgnoreCase("jpg") == 0;
      boolean testPNG = ext3.compareToIgnoreCase("png") == 0;
      boolean testBMP = ext3.compareToIgnoreCase("bmp") == 0;
      boolean testWBMP = ext4.compareToIgnoreCase("wbmp") == 0;
      boolean testGIF = ext3.compareToIgnoreCase("gif") == 0;
      String mode = null;
      if (testJPEG) mode = "jpeg";
      else if (testJPG) mode = "jpg";
      else if (testPNG) mode = "png";
      else if (testBMP) mode = "bmp";
      else if (testWBMP) mode = "wbmp";
      else if (testGIF) mode = "gif";
      try {
        ImageIO.write(bot.createScreenCapture(new Rectangle(x,y,width,height)), mode, path_file);
      } 
      catch(Exception e) {
        e.printStackTrace();
      }
    }
    //</editor-fold>
}
