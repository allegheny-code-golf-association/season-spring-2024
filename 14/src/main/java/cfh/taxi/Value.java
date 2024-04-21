package cfh.taxi;

public abstract class Value {

    public static Value createValue(String value) {
        if (value == null) throw new IllegalArgumentException("null value");
        
        return new StringValue(value);
    }
    
    public static Value createValue(double value) {
        return new NumberValue(value);
    }
    
    public abstract boolean isNumber();
    public abstract boolean isString();
    
    public abstract double number(Location location) throws TaxiException;
    public abstract String string(Location location) throws TaxiException;

    ////////////////////////////////////////////////////////////////////////////
    
    private static class NumberValue extends Value {
        
        private final double value;
        
        private NumberValue(double value) {
            this.value = value;
        }

        @Override
        public boolean isNumber() {
            return true;
        }

        @Override
        public boolean isString() {
            return false;
        }
        
        @Override
        public double number(Location location) {
            return value;
        }
        
        @Override
        public String string(Location location) throws TaxiException {
            throw new TaxiException(location + " cannot handle non-string values");
        }
        
        @Override
        public String toString() {
            if (value == (int) value) 
                return Integer.toString((int) value);
            else
                return Double.toString(value);
        }
    }
    
    private static class StringValue extends Value {
        
        private final String value;
        
        private StringValue(String value) {
            assert value != null;
            
            this.value = value;
        }

        @Override
        public boolean isNumber() {
            return false;
        }

        @Override
        public boolean isString() {
            return true;
        }
        
        @Override
        public double number(Location location) throws TaxiException {
            throw new TaxiException(location + " requires a numerical value");
        }
        
        @Override
        public String string(Location location) throws TaxiException {
            return value;
        }

        @Override
        public String toString() {
            String text = value.replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r")
                               .replaceAll("\\t", "\\\\t").replaceAll("\"", "\\\\\"");
            return "\"" + text + "\"";
        }
    }
}
