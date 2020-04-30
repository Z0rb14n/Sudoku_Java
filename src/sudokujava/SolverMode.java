/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudokujava;

import java.awt.Point;

/**
 *
 * @author adminasaurus
 */
public class SolverMode {

    public boolean isImage;
    byte[][] tileArray;
    boolean writeToFile = false;
    boolean hideNotFoundCandidateMsg = true;
    boolean showCandidateRemovalMsg = true;
    boolean hideNoBlankWereFound = false;
    Point topLeft;
    int imageWidth;
    int imageHeight;
    int imageCaptureDelay;
    Speed speed;
    
    public SolverMode(byte[][] tileArray,
                      boolean writeToFile,
                      boolean hideCandidateNotFoundMsg,
                      boolean showCandidateRemovalMsg,
                      boolean hideNoBlankWereFound,
                      char speed) {
        if (tileArray.length != 9) {
            throw new IllegalArgumentException();
        }
        for (byte[] bytes : tileArray) {
            if (bytes.length != 9) throw new IllegalArgumentException();
        }
        this.tileArray = tileArray;
        this.writeToFile = writeToFile;
        this.hideNotFoundCandidateMsg = hideCandidateNotFoundMsg;
        this.showCandidateRemovalMsg = showCandidateRemovalMsg;
        this.hideNoBlankWereFound = hideNoBlankWereFound;
        this.speed = Speed.getSpeed(speed);
        if (this.speed == null) throw new IllegalArgumentException();
        isImage = false;
    }
    
    public SolverMode(int topLeftX, int topLeftY, int imageWidth, int imageHeight, int imageDelay) {
        isImage = true;
        this.topLeft = new Point(topLeftX,topLeftY);
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.imageCaptureDelay = imageDelay;
        this.speed = SudokuJava.DEFAULT;
        System.out.println("Image top left coord: " + topLeft.getX() + "," + topLeft.getY() + " width: " + imageWidth + ", height: " + imageHeight + ", delay: " + imageCaptureDelay);
    }
    
    public enum Speed {
        FAST {
            @Override
            public char characterRepresentation() {
                return '0';
            }
        },
        MEDIUM {
            @Override
            public char characterRepresentation() {
                return '1';
            }
        },
        SLOW {
            @Override
            public char characterRepresentation() {
                return '2';
            }
        },
        VERY_SLOW {
            @Override
            public char characterRepresentation() {
                return '3';
            }
        },
        REALLY_SLOW {
            @Override
            public char characterRepresentation() {
                return '4';
            }
        },
        RECURSE {
            @Override
            public char characterRepresentation() {
                return 'R';
            }
        };
        public char characterRepresentation() {
            return (char) -1;
        }
        public static Speed getSpeed(char input) {
            for (Speed sp : Speed.values()) {
                if (sp.characterRepresentation() == input) return sp;
            }
            return null;
        }
        
        public boolean isGreaterThan(Speed speed) {
            if (this == RECURSE) return speed != RECURSE;
            if (speed == RECURSE) return false;
            return this.characterRepresentation() >= speed.characterRepresentation();
        }
    }
}
