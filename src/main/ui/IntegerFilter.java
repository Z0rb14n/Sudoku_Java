package ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public class IntegerFilter extends DocumentFilter {
    private final int maxLength;
    private final boolean allowEmpty;

    IntegerFilter(int maxLength, boolean allowEmpty) {
        super();
        this.maxLength = maxLength;
        this.allowEmpty = allowEmpty;
    }

    /**
     * Performs string insertion if result is valid.
     */
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getText(0, doc.getLength()));
        sb.insert(offset, string);

        if (test(sb.toString())) {
            super.insertString(fb, offset, string, attr);
        }
    }

    /**
     * Determines whether string is valid text: i.e. does not exceed length and empty if allowed.
     *
     * @param text string to test
     * @return True if string is an integer whose length is not longer than max length; empty if allowed.
     */
    private boolean test(String text) {
        if (text.isEmpty()) return allowEmpty;
        try {
            int result = Integer.parseInt(text);
            if (text.length() > maxLength) return false;
            return result >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Performs a replace operation if the text is valid.
     */
    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getText(0, doc.getLength()));
        sb.replace(offset, offset + length, text);

        if (test(sb.toString())) {
            super.replace(fb, offset, length, text, attrs);
        }
    }

    /**
     * Performs a remove operation if the text is valid.
     */
    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getText(0, doc.getLength()));
        sb.delete(offset, offset + length);

        if (test(sb.toString())) {
            super.remove(fb, offset, length);
        }
    }
}
