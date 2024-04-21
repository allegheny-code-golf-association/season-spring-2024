package cfh.taxi.gui;

import java.awt.Font;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import cfh.taxi.Location;
import cfh.taxi.Node;
import cfh.taxi.RoadMap;
import cfh.taxi.node.NodeClass;

public class HelpDialog extends JDialog {

    private static final Font MONOSPACED = new Font("monospaced", Font.PLAIN, 12);

    HelpDialog(JFrame parent, RoadMap map) {
        super(parent, true);

        initGUI(map);
        setLocationRelativeTo(parent);
    }
    
    private void initGUI(RoadMap map) {
        JComponent general = newJTextArea(
                "Taxi: The Programming Language\n is based on http://www.bigzaphod.org/taxi/\n" +
                "\n" +
                "Taxi is case sensitive!\n" +
                "Statements must not wrap over lines.\n" +
                "Whitespaces at the beginning and end of the lines are ignored.\n" +
                "\n" +
                "Click on a location to display its name and functionality.\n" +
                "<CTRL>-Click to add a locations name to the script.\n" +
                "Check the popup menu (right-click) on the map panel.\n" +
                "    *(e.g. Go to: click on destination the click on start node)\n" +
                "");
        JComponent commands = newJTextArea(
                "[<label>]\n" +
                "  Marks the actual script line with the given <label> for the \"switch plan\" command.\n" +
                "  A redefinition overwrites all previous labels with the same name.\n" +
                "\n" +
                "Switch to plan \"<label>\".\n" +
                "  Execution continues after the given <label>.\n" +
                "\n" +
                "Switch to plan \"<label>\" if no one is waiting.\n" +
                "  jump to <label> if no passenger is waiting at the current location.\n" +
                "\n" +
                "<passenger> is waiting at [the ]<location>.\n" +
                "  Creates a new passenger at the given location.\n" +
                "  A <passenger> can be a string (\"example\") or a number (123.456).\n" +
                "  Backslash '\\' is used for special characters in a string:\n" +
                "    '\\n' for newline, '\\r' for carriage return, '\\t' for tab\n" +
                "\n" +
                "Pickup a[nother] passenger going to [the ]<location>.\n" +
                "  Pickups a passenger waiting at the current location going to the given <location>.\n" +
                "\n" +
                "Go to <location>: (north|east|south|west) {<ordinal>[st|nd|rd|th] (left|right)}[,...].\n" +
                "  Drives to <location> using the given directions.\n" +
                "  Example from Post Office to Writer's Depot:\n" +
                "    Go to Writer's Depot: south 1st right, 1st left, 2nd left.\n" +
                "");
        JComponent hello = newJTextArea(
                "[  Hello World  ]\n" +
                "\n" +
                "\"Hello World\\n\" is waiting at Writer's Depot.\n" +
                "Go to Writer's Depot: west 1st left, 1st right, 1st left, 1st right, 1st left, 2nd left.\n" +
                "Pickup a passenger going to Post Office.\n" +
                "Go to Post Office: north 1st right, 2nd right, 1st left.\n" +
                "Go to Taxi Garage: north 1st right, 1st left, 1st right.\n" +
                "");
        
        SortedSet<Node> sorted = new TreeSet<Node>(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.getName().compareTo(o2.getName());
            }
            
        });
        sorted.addAll(map.nodes());
        StringBuilder builder = new StringBuilder();
        builder.append("<h2>Alphabetical</h2>\n");
        builder.append("<table border=\"0\"><tbody>\n");
        builder.append("<tr><th style=\"text-align: left;\">Location</th>" +
        		"<th style=\"text-align: left;\">Function</th></tr>\n");
        for (Node node : sorted) {
            if (node instanceof Location) {
                builder.append(String.format("<tr><td>%s</td><td>%s</td></tr>\n", node.getName(), node.description()));
            }
        }
        builder.append("</tbody></table>");
        
        for (NodeClass nodeClass : NodeClass.values()) {
            if (nodeClass == NodeClass.LAYOUT)
                continue;
            sorted.clear();
            for (Node node : map.nodes()) {
                if (node.nodeClass().contains(nodeClass)) {
                    sorted.add(node);
                }
            }
            builder.append("<p/>");
            builder.append("<h2>").append(nodeClass.description()).append("</h2>\n");
            builder.append("<table border=\"0\"><tbody>\n");
            builder.append("<tr><th style=\"text-align: left;\">Location</th>" +
            "<th style=\"text-align: left;\">Function</th></tr>\n");
            for (Node node : sorted) {
                if (node instanceof Location) {
                    builder.append(String.format("<tr><td>%s</td><td>%s</td></tr>\n", node.getName(), node.description()));
                }
            }
            builder.append("</tbody></table>");
        }
        
        JComponent nodes = createHelpTab(builder.toString());
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("General", general);
        tabs.addTab("Commands", commands);
        tabs.addTab("Nodes", nodes);
        tabs.addTab("Sample", hello);
        add(tabs);
        
        setSize(700, 500);
        validate();
    }
    
    private JComponent newJTextArea(String text) {
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setFont(MONOSPACED);
        return new JScrollPane(area);
    }
    
    private JComponent createHelpTab(String text) {
        JEditorPane pane = new JEditorPane("text/html", text);
        pane.setEditable(false);
        pane.setCaretPosition(0);
        return new JScrollPane(pane);
    }
}
