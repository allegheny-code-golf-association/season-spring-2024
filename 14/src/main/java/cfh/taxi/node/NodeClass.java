package cfh.taxi.node;

public enum NodeClass {

    LAYOUT("Nodes used only for layout"),
    INOUT("Input, output"),
    QUEUE("Just for waiting"),
    CONVERSION("Locations that do some conversion"),
    TEST("Locations for testing"),
    NUMERIC("Locations that handle numeric passengers"),
    STRING("Locations that handle string passengers"),
    TAXI("Taxi specific");
    
    private String description;

    private NodeClass(String description) {
        this.description = description;
    }
    
    public String description() {
        return description;
    }
}
