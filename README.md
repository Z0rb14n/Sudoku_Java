# Sudoku_Java
Sudoku solver written in Java. It has two modes - a logical sudoku solver, which can solve most simple puzzles (such as ones in newspapers), and a fallback recursive solver, that can solve all puzzles that have a solution. It can read from input files (e.g. input.txt) and write solutions to output files (e.g. output.txt), or directly receive the puzzle from sudoku puzzle websites and input the solutions automatically, although the Optical Character Recognition (or OCR) is a little slow.

For the graphical version, you can input the top left coordinate of the input box, the width and height of the input box, and a given delay before it takes a screen capture.

Note that if it cannot properly read the data, the program will beep and stop executing.

## Instructions for running

There currently isn't a release or a build, so you would have to manually compile it yourself. To do that, open up the files in a Java IDE, navigate to `sudokujava.ui.Main` and run it.
For a terminal based version (without a GUI or any user input), navigate to `sudokujava.algorithm.SudokuJava.Main`. File locations may need to be changed.

### Prerequisites

The Tess4J OCR library, and Java 8 or newer. Note that this was tested using Java 8 on the Mac OS X operating system, so it may not work for other OSes or older versions of Java.