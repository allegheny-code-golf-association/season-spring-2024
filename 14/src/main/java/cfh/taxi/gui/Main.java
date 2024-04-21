package cfh.taxi.gui;

import static java.awt.GridBagConstraints.*;

import static cfh.taxi.Program.InputOutput.Level.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.IllegalFormatConversionException;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import cfh.taxi.Location;
import cfh.taxi.Node;
import cfh.taxi.Path;
import cfh.taxi.Program;
import cfh.taxi.RoadMap;
import cfh.taxi.Taxi;
import cfh.taxi.TaxiException;
import cfh.taxi.Path.Instruction;
import cfh.taxi.Program.InputOutput;
import cfh.taxi.Program.InputOutput.Level;
import cfh.taxi.gui.InputTextArea.Listener;

public class Main {
    
    public static final String TITLE = "Taxi Program - " + Taxi.VERSION + "    \u00A9  by Carlos F Heuberger";
    private static final String MAP_FILE = "/cfh/taxi/resources/default.map";

    public static void main(String[] args) {
        Locale.setDefault(Locale.ROOT);
        String file;
        if (args.length >= 1) {
            if (args[0].charAt(0) != '/' && args[0].charAt(0) != '\\') {
                file = "/cfh/taxi/resources/" + args[0];
                if (!new File(file).exists()) {
                    file = args[0];
                }
            } else {
                file = args[0];
            }
        } else {
            file = MAP_FILE;
        }
        try (InputStream stream = Main.class.getResourceAsStream(file)) {
            if (stream == null) {
                System.err.println(file + " not found");
                return;
            }
            new Main(RoadMap.load(stream));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //==============================================================================================

    private static final Font MONOSPACED = new Font("monospaced", Font.PLAIN, 12);
    
    private static final String NORMAL_STYLE = "normal";
    private static final String ERROR_STYLE = "error";
    
    private static final String PREF_WINDOW_WIDTH = "windowWidth"; 
    private static final String PREF_WINDOW_HEIGHT = "windowHeight"; 
    private static final String PREF_WINDOW_STATE = "windowState"; 
    private static final String PREF_WINDOW_SPLIT = "windowSplit"; 
    private static final String PREF_PROG_DIR = "programDirectory"; 
    private static final String PREF_PROG_TEXT = "program";
    private static final String PREF_INPUT_TEXT = "inputText";
    private static final String PREF_SCALE = "scale";
    private static final String PREF_SHOW_NAMES = "showNames";
    private static final String PREF_SHOW_ROADS = "showConnect";
    private static final String PREF_LOG_PREFIX = "log:";
    private static final String PREF_ENABLE_ANIMATION = "enableAnimation";
    private static final String PREF_ANIMATION_VELOCITY = "animationVelocity";
    private final Preferences prefs = Preferences.userNodeForPackage(getClass());
    
    private final RoadMap map;

    private final JFrame frame;
    private final MapPanel mapPanel;
    
    private final JTextArea programText;
    private final InputTextArea inputText;
    private final JTextPane outputText;
    private final JTextArea logText;
    private JSplitPane splitPane;
    private final StyledDocument outputDocument;
    
    private JButton runButton;
    private final JToggleButton[] outputLevels;
    private final JToggleButton showNames;
    private final JToggleButton showRoads;
    private final JToggleButton enableAnimation;
    private final JRadioButton[] animationVelocity;
    
    private final MapPopupMenu mapPopup;
    
    private Program program = null;
    private final Program.InputOutput inpout;
    private JTabbedPane tabPane;
    
    private InfoWindow infoWindow = null;
    
    private Node destination = null;

    private Thread runThread = null;

    private Main(RoadMap map) {
        if (map == null) throw new IllegalArgumentException("null map");
        
        this.map = map;
        
        inpout = new Program.InputOutput() {
            
            @Override
            public void print(String format, Object... args) {
                try {
                    append(NORMAL_STYLE, String.format(format, args));
                } catch (IllegalFormatConversionException ex) {
                    append(NORMAL_STYLE, format + " " + Arrays.toString(args));
                    ex.printStackTrace();
                    logText.append(String.format("%s%n%s %s", ex, format, Arrays.toString(args)));
                }
                if (isLogging(DEBUG)) {
                    log(null, "PRINT: " + format, args);
                }
                outputText.setCaretPosition(outputDocument.getLength());
            }
            
            @Override
            public void error(String format, Object... args) {
                try {
                    append(ERROR_STYLE, String.format(format, args));
                } catch (IllegalFormatConversionException ex) {
                    append(ERROR_STYLE, format + " " + Arrays.toString(args));
                    ex.printStackTrace();
                    logText.append(String.format("%s%n%s %s", ex, format, Arrays.toString(args)));
                }
                if (isLogging(DEBUG)) {
                    log(null, "ERROR: " + format, args);
                }
                outputText.setCaretPosition(outputDocument.getLength());
            }

            @Override
            public boolean isLogging(Level level) {
                return level == null || outputLevels[level.ordinal()].isSelected();
            }

            @Override
            public void log(Level level, String format, Object... args) {
                if (isLogging(level)) {
                    try {
                        logText.append(String.format(format, args));
                    } catch (IllegalFormatConversionException ex) {
                        ex.printStackTrace();
                        logText.append(String.format("%s%n%s %s", ex, format, Arrays.toString(args)));
                    }
                    logText.append("\n");
                    logText.setCaretPosition(logText.getText().length());
                }
            }

            @Override
            public String readLine() {
                return inputText.readLine();
            }
        };
        
        mapPanel = new MapPanel(map);
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    onMouseClicked(e);
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }
        });
        
        JButton help = newJButton("Help", "shows a help screen");
        help.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doHelp();
            }
        });
        
        JButton load = newJButton("Load", "load a text file as new program script, any changes are lost");
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLoad();
            }
        });
        JButton save = newJButton("Save", "save the current program script in a text file");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doSave();
            }
        });
        
        final JComboBox<String> scale = new JComboBox<>(new String[] {"400", "200", "150", "100", "70", "50", "25"});
        scale.setEditable(true);
        scale.setMaximumSize(new Dimension(10, 30));
        scale.setToolTipText("scale the map");
        scale.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = (String) scale.getSelectedItem();
                try {
                    int factor = Integer.parseInt(text);
                    if (factor < 1) {
                        scale.setSelectedItem(Integer.toString((int) (mapPanel.getScale()*100)));
                        throw new NumberFormatException("negative scale: " + factor);
                    }
                    mapPanel.setScale(factor / 100.0);
                    prefs.put(PREF_SCALE, text);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                            frame, ex.getMessage(), "Number Format Exception", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        scale.setSelectedItem(prefs.get(PREF_SCALE, "100"));
        
        JButton compile = newJButton("Compile", "compile the current program script");
        compile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doCompile();
            }
        });
        
        runButton = newJButton("RUN", "run the actual program script after compiling it if necessary");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doRun();
            }
        });
        
        JButton clear = newJButton("Clear", "clear output and log panel, hold <CTRL> to also clear input");
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean ctrl = (e.getModifiers() & ActionEvent.CTRL_MASK) != 0; 
                doClear(ctrl);
            }
        });
        
        JButton quit = newJButton("QUIT", "terminates the IDE, any changes are lost");
        quit.setForeground(Color.RED);
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doQuit();
            }
        });
       
        Box buttons = Box.createHorizontalBox();
        buttons.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        
        buttons.add(help);
        buttons.add(Box.createHorizontalGlue());
        buttons.add(load);
        buttons.add(save);
        buttons.add(Box.createHorizontalGlue());
        buttons.add(new JLabel("Scale: "));
        buttons.add(scale);
        buttons.add(Box.createHorizontalGlue());
        buttons.add(compile);
        buttons.add(runButton);
        buttons.add(Box.createHorizontalGlue());
        buttons.add(clear);
        buttons.add(Box.createHorizontalGlue());
        buttons.add(quit);
        
        programText = new JTextArea(16, 60);
        programText.setFont(MONOSPACED);
        programText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                program = null;
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                program = null;
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                program = null;
            }
        });
        String prg = prefs.get(PREF_PROG_TEXT, "");
        if (prg.startsWith(":")) {
            File file = new File(prg.substring(1));
            try (BufferedReader inp = new BufferedReader(new FileReader(file))) {
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = inp.readLine()) != null) {
                    builder.append(line).append("\n");
                }
                prg = builder.toString();
            } catch (IOException ex) {
                ex.printStackTrace();
                prg = "ERROR: unable to read from " + file;
            }
        }
        programText.setText(prg);
        
        inputText = new InputTextArea();
        inputText.setFont(MONOSPACED);
        inputText.setText(prefs.get(PREF_INPUT_TEXT, ""));
        inputText.setToolTipText("input for the program script, turns yellow if waiting for input");
        inputText.addListener(new Listener() {
            @Override
            public void unblocking(int count) {
            }
            @Override
            public void waiting(int count) {
                tabPane.setSelectedIndex(tabPane.indexOfTab("Input/Output"));
            }
        });
        
        outputText = new JTextPane() {
            @Override
            public boolean getScrollableTracksViewportWidth() {
                return false;
            }
        };
        outputText.setFont(MONOSPACED);
        outputText.setToolTipText("output of the program script");
        outputDocument = outputText.getStyledDocument();
        
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setAlignment(def, 0);
        Style normal = outputDocument.addStyle(NORMAL_STYLE, def);
        Style error = outputDocument.addStyle(ERROR_STYLE, normal);
        StyleConstants.setBold(error, true);
        StyleConstants.setForeground(error, Color.RED);
        
        JSplitPane io = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        io.setTopComponent(newJScrollPane(inputText));
        io.setBottomComponent(newJScrollPane(outputText));
        io.setResizeWeight(0.5);
        
        logText = new JTextArea();
        logText.setFont(MONOSPACED);
        
        showNames = newJCheckBox("Names", "draw names of location on map");
        showNames.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapPanel.setShowNames(showNames.isSelected());
                prefs.putBoolean(PREF_SHOW_NAMES, showNames.isSelected());
            }
        });
        boolean prefShow;
        prefShow = prefs.getBoolean(PREF_SHOW_NAMES, false);
        showNames.setSelected(prefShow);
        mapPanel.setShowNames(prefShow);
        
        showRoads = newJCheckBox("Roads", "show roads on map");
        showRoads.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapPanel.setShowRoads(showRoads.isSelected());
                prefs.putBoolean(PREF_SHOW_ROADS, showRoads.isSelected());
            }
        });
        prefShow = prefs.getBoolean(PREF_SHOW_ROADS, false);
        showRoads.setSelected(prefShow);
        mapPanel.setShowRoads(prefShow);
        
        Level[] levels = InputOutput.Level.values();
        outputLevels = new JToggleButton[levels.length];
        ActionListener levelListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JToggleButton but = (JToggleButton) e.getSource();
                prefs.putBoolean(PREF_LOG_PREFIX+but.getText(), but.isSelected());
            }
        };
        for (int i = 0; i < outputLevels.length; i++) {
            JToggleButton but = newJCheckBox(levels[i].name(), levels[i].tooltip());
            but.setSelected(prefs.getBoolean(PREF_LOG_PREFIX+but.getText(), i < 2));
            but.addActionListener(levelListener);
            outputLevels[i] = but;
        }
        
        enableAnimation = newJCheckBox("enable", "enable animation of program execution on the map");
        enableAnimation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapPanel.enableAnimation(enableAnimation.isSelected());
                prefs.putBoolean(PREF_ENABLE_ANIMATION, enableAnimation.isSelected());
            }
        });
        boolean prefEnable = prefs.getBoolean(PREF_ENABLE_ANIMATION, false);
        mapPanel.enableAnimation(prefEnable);
        enableAnimation.setSelected(prefEnable);
        
        String[] velocities = {"0.25", "0.5", "1.0", "2.0", "5.0"};
        animationVelocity = new JRadioButton[velocities.length];
        ButtonGroup velocityGroup = new ButtonGroup();
        ActionListener velocityListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JRadioButton cb = (JRadioButton) e.getSource();
                if (cb.isSelected()) {
                    prefs.put(PREF_ANIMATION_VELOCITY, cb.getText());
                    mapPanel.setAnimationVelocity(Double.parseDouble(cb.getText()));
                }
            }
        };
        String prefVelocity = prefs.get(PREF_ANIMATION_VELOCITY, "1.0");
        for (int i = 0; i < animationVelocity.length; i++) {
            JRadioButton but = newJRadioButton(velocities[i], "set animation velocity to " + velocities[i]);
            but.addActionListener(velocityListener);
            velocityGroup.add(but);
            but.setSelected(velocities[i].equals(prefVelocity ));
            animationVelocity[i] = but;
        }
        mapPanel.setAnimationVelocity(Double.parseDouble(prefVelocity));
        
        GridBagConstraints gbcLine = new GridBagConstraints();
        gbcLine.anchor = LINE_START;
        gbcLine.fill = HORIZONTAL;
        gbcLine.gridwidth = REMAINDER;
        gbcLine.gridx = 0;
        gbcLine.weightx = 1.0;
        
        GridBagConstraints gbcTitle = new GridBagConstraints();
        gbcTitle.fill = HORIZONTAL;
        gbcTitle.gridwidth = REMAINDER;
        gbcTitle.gridx = 0;
        gbcTitle.insets = new Insets(12, 0, 0, 0);
        gbcTitle.weightx = 1.0;
        
        JPanel settings = new JPanel(new GridBagLayout());
        settings.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        settings.add(new JLabel("DATA"), gbcTitle);
        settings.add(newJLabel("Passengers: " + map.maxPassengers()), gbcLine);
        settings.add(newJLabel("Size:       " + map.pixelUnit() + " pixel/unit"), gbcLine);
        settings.add(newJLabel("Capacity:   " + map.capacity() + " gas"), gbcLine);
        settings.add(newJLabel("Gas Usage:  " + map.gasUsage() + " unit/gas"), gbcLine);
        settings.add(newJLabel("Fare:       " + map.fare() + " credits/unit"), gbcLine);
        
        settings.add(new JLabel("ANIMATION"), gbcTitle);
        settings.add(enableAnimation, gbcLine);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = LINE_START;
        settings.add(newJLabel("Velocity: "), gbc);
        for (JComponent cb : animationVelocity) {
            settings.add(cb, gbc);
        }

        settings.add(new JLabel("LOG"), gbcTitle);
        for (JComponent button : outputLevels) {
            settings.add(button, gbcLine);
        }
        
        settings.add(new JLabel("DEBUG"), gbcTitle);
        settings.add(showNames, gbcLine);
        settings.add(showRoads, gbcLine);
        
        gbcLine.weighty = 1.0;
        gbcLine.anchor = FIRST_LINE_START;
        settings.add(Box.createGlue(), gbcLine); // dummy for filling
        
        tabPane = new JTabbedPane();
        tabPane.addTab("Program", newJScrollPane(programText));
        tabPane.addTab("Input/Output", io);
        tabPane.addTab("Log", newJScrollPane(logText));
        tabPane.addTab("Settings", newJScrollPane(settings));
        
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(newJScrollPane(mapPanel));
        splitPane.setRightComponent(tabPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(prefs.getInt(PREF_WINDOW_SPLIT, 400));
        
        mapPopup = new MapPopupMenu();
        
        int width = prefs.getInt(PREF_WINDOW_WIDTH, 1000);
        int height = prefs.getInt(PREF_WINDOW_HEIGHT, 800);
        int state = prefs.getInt(PREF_WINDOW_STATE, JFrame.NORMAL);
        state &= ~JFrame.ICONIFIED;
        
        frame = new JFrame(TITLE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                rememberPrefs();
            }
        });
        frame.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                rememberWindow();
            }
        });
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                rememberWindow();
            }
        });
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(buttons, BorderLayout.NORTH);
        frame.add(splitPane, BorderLayout.CENTER);
        frame.setSize(width, height);
        frame.setExtendedState(state);
        frame.validate();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        programText.requestFocusInWindow();
    }

    private JLabel newJLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(MONOSPACED);
        return label;
    }
    
    private JButton newJButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        return button;
    }
    
    private JToggleButton newJCheckBox(String text, String tooltip) {
        JToggleButton button = new JCheckBox(text);
        button.setToolTipText(tooltip);
        button.setMargin(new Insets(0, 0, 0, 0));
        return button;
    }
    
    private JRadioButton newJRadioButton(String text, String tooltip) {
        JRadioButton button = new JRadioButton(text);
        button.setToolTipText(tooltip);
        button.setMargin(new Insets(0, 0, 0, 0));
        return button;
    }
    
    private JScrollPane newJScrollPane(Component view) {
        JScrollPane pane = new JScrollPane(view);
        return pane;
    }
    
    private void doHelp() {
        HelpDialog help = new HelpDialog(frame, map);
        help.setVisible(true);
    }
    
    private void doLoad() {
        String path = prefs.get(PREF_PROG_DIR, ".");
        JFileChooser chooser = new JFileChooser(path);
        if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION)
            return;
        
        path = chooser.getCurrentDirectory().getAbsolutePath();
        prefs.put(PREF_PROG_DIR, path);
        File file = chooser.getSelectedFile();
        
        StringBuilder builder = new StringBuilder();
        try (BufferedReader inp = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = inp.readLine()) != null) {
                builder.append(line).append('\n');
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "FileNotFoundException", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "IOException", JOptionPane.ERROR_MESSAGE);
            return;
        }
        programText.setText(builder.toString());
    }
    
    private void doSave() {
        String path = prefs.get(PREF_PROG_DIR, ".");
        JFileChooser chooser = new JFileChooser(path);
        if (chooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION)
            return;
        
        path = chooser.getCurrentDirectory().getAbsolutePath();
        prefs.put(PREF_PROG_DIR, path);
        File file = chooser.getSelectedFile();
        if (file.exists()) {
            if (JOptionPane.showConfirmDialog(
                    frame, 
                    file.getName() + " already exists. Overwrite?",
                    "Confirm",
                    JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
                return;
        }
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
            out.write(programText.getText());
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "IOException", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void doCompile() {
        Thread thread = new Thread(this::compile);
        thread.setName("compiler");
        thread.setDaemon(true);
        thread.start();
    }
    
    private synchronized void doRun() {
        if (runThread != null) {
            runThread.interrupt();
            return;
        }
        runThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (program == null) {
                        compile();
                    }
                    if (program != null) {
                        try {
                            program.addListener(mapPanel.getCar());
                            program.run();
                        } catch (TaxiException ex) {
                            ex.printStackTrace();
                            inpout.error("%n%s%n%n", ex.getMessage());
                            inpout.log(null, ex.getMessage());
                        } finally {
                            program.removeListener(mapPanel.getCar());
                            inputText.reset();
                        }
                    }
                } finally {
                    runThread = null;
                    runButton.setText("RUN");
                }
            }
        });
        runThread.setName("runner");
        runThread.setDaemon(true);
        runThread.start();
        runButton.setText("STOP");
    }
    
    private void doClear(boolean ctrl) {
        if (ctrl) {
            inputText.clear();
        } else {
            inputText.reset();
        }
        outputText.setText(null);
        logText.setText(null);
        Thread thread = new Thread(mapPanel::clear);
        thread.setDaemon(true);
        thread.start();
    }
    
    private void doQuit() {
        int option = JOptionPane.showConfirmDialog(frame, "Really quit?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            frame.dispose();
        }
    }
        
    private void compile() {
        String text = programText.getText();
        rememberProgram();
        Program prg;
        try {
            prg = new Program(map, inpout, text);
        } catch (ParseException ex) {
            ex.printStackTrace();
            int line = ex.getErrorOffset() - 1;
            if (line >= 0) {
                try {
                    int start = programText.getLineStartOffset(line);
                    int end = programText.getLineEndOffset(line);
                    programText.select(start, end);
                    programText.requestFocusInWindow();
                } catch (BadLocationException ex1) {
                    ex1.printStackTrace();
                }
            }
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "ParseException", JOptionPane.ERROR_MESSAGE);
            return;
        }
        program = prg;
    }

    private void rememberPrefs() {
        rememberProgram();
        rememberWindow();
    }
    
    private void rememberProgram() {
        String text = programText.getText();
        if (text.length() <= Preferences.MAX_VALUE_LENGTH) {
            prefs.put(PREF_PROG_TEXT, text);
        } else {
            File file = new File(System.getProperty("user.home", "."), "taxi.txt");
            try (FileWriter out = new FileWriter(file)) {
                out.write(text);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    frame, "unable to write to " + file, "ERROR", JOptionPane.ERROR_MESSAGE);
                prefs.put(PREF_PROG_TEXT, "");
                return;
            }
            prefs.put(PREF_PROG_TEXT, ":" + file.getAbsolutePath());
        }
        
        prefs.put(PREF_INPUT_TEXT, inputText.getText());
    }
    
    private void rememberWindow() {
        Dimension size = frame.getSize();
        prefs.putInt(PREF_WINDOW_WIDTH, size.width);
        prefs.putInt(PREF_WINDOW_HEIGHT, size.height);
        prefs.putInt(PREF_WINDOW_STATE, frame.getExtendedState());
        prefs.putInt(PREF_WINDOW_SPLIT, splitPane.getDividerLocation());
    }
    
    private void onMouseClicked(MouseEvent e) {
        Node node = searchNode(e);
        if (node != null) {
            boolean ctrl = (e.getModifiers() & MouseEvent.CTRL_MASK) != 0;
            boolean shft = (e.getModifiers() & MouseEvent.SHIFT_MASK) != 0;
            if (ctrl) {
                if (node instanceof Location) {
                    insertProgram(node.getName());
                }
            } else {
                if (destination != null) {
                    List<Node> route = map.route(node, destination);
                    Path path = RoadMap.constructPath(route);
                    if (shft) {
                        StringBuilder builder = new StringBuilder();
                        builder.append(path.start());
                        for (Instruction instr : path.instructions()) {
                            builder.append(", ");
                            builder.append(instr.count()).append(instr.isTurnLeft()?" l":" r");
                        }
                        insertProgram("%s.%n", builder);
                    } else {
                        insertProgram("%s.%n", path);
                    }
                    mapPanel.setCursor(null);
                    mapPanel.setDestination(null);
                    mapPanel.setRoute(route);
                    destination = null;
                } else {
                    String text = String.format("%s (%d,%d)  -  %s:\n  %s", 
                            node.getName(), node.x(), node.y(), node.type(), node.description());
                    if (infoWindow != null) {
                        infoWindow.dispose();
                        infoWindow = null;
                    }
                    infoWindow = new InfoWindow(text);
                    infoWindow.show(e.getXOnScreen()-32, e.getYOnScreen()-16);
                }
            } 
        }
    }

    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            if (destination != null) {
                mapPanel.setCursor(null);
                mapPanel.setDestination(null);
                destination = null;
            } else {
                Node node = searchNode(e);
                mapPopup.setNode(node);
                mapPopup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
    
    private Node searchNode(MouseEvent e) {
        int x = (int) Math.round(e.getX() / mapPanel.getScale());
        int y = (int) Math.round(e.getY() / mapPanel.getScale());
        return map.searchNode(x, y, 25);
    }
    
    private void insertProgram(String format, Object... args) {
        programText.replaceSelection(String.format(format, args));
    }
    
    private void append(String style, String text) {
        try {
            outputDocument.insertString(outputDocument.getLength(), text, outputDocument.getStyle(style));
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }
    
    //=============================================================================================
    
    private class MapPopupMenu extends JPopupMenu {
        
        private Node node = null;

        MapPopupMenu() {
            JMenuItem item;
            item = new JMenuItem("Go to <here> <start>");
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (node instanceof Location) {
                        insertProgram("Go to %s: ", node.getName());
                        mapPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                        mapPanel.setDestination(node);
                    } else {
                        mapPanel.setCursor(null);
                        mapPanel.setDestination(null);
                    }
                    destination = node;
                }
            });
            add(item);
            item = new JMenuItem("Pickup <to here>");
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    insertProgram("Pickup a passenger going to %s.%n", node.getName());
                }
            });
            add(item);
            item = new JMenuItem("Waiting <at here>");
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    insertProgram("\"text\" is waiting at %s.%n", node.getName());
                }
            });
            add(item);
        }
        
        void setNode(Node node) {
            this.node = node;
        }
    }
}
