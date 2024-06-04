package util;

public class OCRException extends RuntimeException {
    public OCRException() {
        super();
    }

    public OCRException(String msg) {
        super(msg);
    }

    public OCRException(Throwable parent) {
        super(parent);
    }

    public OCRException(String msg, Throwable parent) {
        super(msg, parent);
    }
}
