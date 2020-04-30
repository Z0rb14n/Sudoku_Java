/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author adminasaurus
 */
public class ParsingException extends Exception {
    public ParsingException() {
        super();
    }
    public ParsingException(String l){
        super(l);
    }
    public ParsingException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
