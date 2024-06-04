package cli;

import org.junit.Test;

public class TestCLI {
    @Test
    public void testDataFile() {
        SudokuCLI.main(new String[]{"-f", "./data/textInput.txt", "-o", "./data/output.txt"});
    }

    @Test
    public void testImageFile() {
        SudokuCLI.main(new String[]{"-f", "./data/imageInput.txt"});
    }
}
