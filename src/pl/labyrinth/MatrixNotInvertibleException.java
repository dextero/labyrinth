package pl.labyrinth;

/**
 * Created with IntelliJ IDEA.
 * User: dex
 * Date: 12/28/12
 * Time: 12:21 PM
 */
public class MatrixNotInvertibleException extends Exception {
    public MatrixNotInvertibleException(String message) {
        super(message);
    }

    public MatrixNotInvertibleException(String message, Throwable throwable) {
        super(message, throwable);
    }
}