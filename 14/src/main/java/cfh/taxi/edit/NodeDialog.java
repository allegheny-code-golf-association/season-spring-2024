package cfh.taxi.edit;

import static java.awt.GridBagConstraints.*;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import cfh.taxi.node.NodeType;

public class NodeDialog extends JDialog {

    private boolean okPressed = false;
    
    private final JLabel messageLabel;
    private final JTextField coordx;
    private final JTextField coordy;
    private final JComboBox<NodeType> types;
    private final JTextField nodeName;

    private final JButton okButton;
    
    NodeDialog(JFrame parent) {
        super(parent, "Node", true);
        
        messageLabel = new JLabel();
        
        coordx = new JTextField(5);
        coordy = new JTextField(5);
        
        nodeName = new JTextField(20);
        
        types = new JComboBox<>(NodeType.values());
        types.setSelectedItem(NodeType.INTERSECTION);
        types.setEditable(false);
        types.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                switch (e.getStateChange()) {
                    case ItemEvent.DESELECTED:
                        if (e.getItem().toString().equals(nodeName.getText())) {
                            nodeName.setText(null);
                        }
                        break;
                    case ItemEvent.SELECTED:
                        if (nodeName.getText().isEmpty()) {
                            nodeName.setText(e.getItem().toString());
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        
        okButton = newJButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okPressed = true;
                dispose();
            }
        });
        
        JButton cancel = newJButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okPressed = false;
                dispose();
            }
        });
        
        Box buttons = Box.createHorizontalBox();
        buttons.add(Box.createHorizontalGlue());
        buttons.add(okButton);
        buttons.add(Box.createHorizontalGlue());
        buttons.add(cancel);
        buttons.add(Box.createHorizontalGlue());
        
        Insets insets = new Insets(2, 2, 2, 2);
        setLayout(new GridBagLayout());
        add(messageLabel,        new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, WEST, NONE, insets , 0, 0));
        add(new JLabel("Pos:"),  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, WEST, NONE, insets , 0, 0));
        add(coordx,              new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, EAST, NONE, insets , 0, 0));
        add(coordy,              new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, WEST, NONE, insets , 0, 0));
        add(new JLabel("Type:"), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, WEST, NONE, insets , 0, 0));
        add(types,               new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0, WEST, NONE, insets , 0, 0));
        add(new JLabel("Name:"), new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, WEST, NONE, insets , 0, 0));
        add(nodeName,            new GridBagConstraints(1, 3, 2, 1, 0.0, 0.0, WEST, HORIZONTAL, insets , 0, 0));
        add(buttons,             new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, CENTER, HORIZONTAL, insets , 0, 0));
    }
    
    void setNodeName(String name) {
        nodeName.setText(name);
    }
    
    void setNodeType(NodeType type) {
        types.setSelectedItem(type);
    }
    
    boolean showNew(String message, int x, int y) {
        coordx.setText(Integer.toString(x));
        coordy.setText(Integer.toString(y));
        nodeName.setText(null);
        types.setSelectedItem(NodeType.INTERSECTION);
        setFieldsEnabled(true);
        messageLabel.setText(message);
        pack();
        setLocationRelativeTo(rootPane);
        setVisible(true);
        return okPressed;
    }
    
    void showNode(String message, int x, int y) {
        coordx.setText(Integer.toString(x));
        coordy.setText(Integer.toString(y));
        setFieldsEnabled(false);
        messageLabel.setText(message);
        pack();
        setLocationRelativeTo(rootPane);
        setVisible(true);
    }
    
    private void setFieldsEnabled(boolean enabled) {
        types.setEnabled(enabled);
    }

    int coordX() {
        return Integer.parseInt(coordx.getText()); 
    }
    
    int coordY() {
        return Integer.parseInt(coordy.getText()); 
    }
    
    String nodeName() {
        return nodeName.getText();
    }
    
    NodeType nodeType() {
        return (NodeType) types.getSelectedItem();
    }
    
    private JButton newJButton(String text) {
        JButton button = new JButton(text);
        return button;
    }
}
