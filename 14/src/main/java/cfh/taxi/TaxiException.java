package cfh.taxi;

public class TaxiException extends Exception {

    public TaxiException(String message) {
        super(message);
    }

    public TaxiException(Throwable ex) {
        super(ex);
    }
}
