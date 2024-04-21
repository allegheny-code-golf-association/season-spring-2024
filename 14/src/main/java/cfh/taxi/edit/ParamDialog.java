package cfh.taxi.edit;

import static java.awt.GridBagConstraints.*;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class ParamDialog extends JDialog {

    private boolean okPressed = false;
    
    private final JTextField pixelField = new JTextField(10);
    private final JTextField passengersField = new JTextField(10);
    private final JTextField tankField = new JTextField(10);
    private final JTextField usageField = new JTextField(10);
    private final JTextField fareField = new JTextField(10);
    
    ParamDialog(Frame parent, double pixelKM, int passengers, double maxTank, double gasUsage, double fare) {
        super(parent, true);
        
        pixelField.setText(Double.toString(pixelKM));
        passengersField.setText(Integer.toString(passengers));
        tankField.setText(Double.toString(maxTank));
        usageField.setText(Double.toString(gasUsage));
        fareField.setText(Double.toString(fare));
        
        setLayout(new GridBagLayout());
        
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okPressed = true;
                dispose();
            }
        });
        
        JButton cancel = new JButton("cancel");
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
        add(new JLabel("Pixel / length:"), new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, WEST, NONE, insets, 0, 0));
        add(pixelField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, insets, 0, 0));
        add(new JLabel("Passengers:"), new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, WEST, NONE, insets, 0, 0));
        add(passengersField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, insets, 0, 0));
        add(new JLabel("Tank Capacity [gas]:"), new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, WEST, NONE, insets, 0, 0));
        add(tankField, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, insets, 0, 0));
        add(new JLabel("Gas Usage [length/gas]:"), new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, WEST, NONE, insets, 0, 0));
        add(usageField, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, insets, 0, 0));
        add(new JLabel("Fare [credits/length]:"), new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0, WEST, NONE, insets, 0, 0));
        add(fareField, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, insets, 0, 0));
        
        add(buttons, new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0, CENTER, HORIZONTAL, insets, 0, 0));
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    boolean showDialog() {
        setVisible(true);
        return okPressed;
    }
    
    boolean wasOkPressed() {
        return okPressed;
    }
    
    double pixel() {
        return Double.parseDouble(pixelField.getText());
    }
    
    int passengers() {
        return Integer.parseInt(passengersField.getText());
    }
    
    double maxTank() {
        return Double.parseDouble(tankField.getText());
    }
    
    double gasUsage() {
        return Double.parseDouble(usageField.getText());
    }
    
    double fare() {
        return Double.parseDouble(fareField.getText());
    }
}
