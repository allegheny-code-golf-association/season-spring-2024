package cfh.taxi.gui;

import java.awt.Color;
import java.awt.SystemColor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import javax.swing.JTextArea;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class InputTextArea extends JTextArea {

    private static final Color NORMAL_BACKGROUND = SystemColor.text;
    private static final Color WAITING_BACKGROUND = Color.YELLOW;
    
    private final PlainDocument doc = new PlainDocument();

    private int current = 0;
    private int waiting = 0;
    
    private final BlockingDeque<String> lines = new LinkedBlockingDeque<>();
    
    private final List<Listener> listeners = new ArrayList<>();

    InputTextArea() {
        super();
        setDocument(doc);
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) 
            throws BadLocationException {
                if (offset >= current) {
                    super.insertString(fb, offset, string, attr);
                    checkLines();
                }
            }
            
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
            throws BadLocationException {
                if (offset >= current) {
                    super.replace(fb, offset, length, text, attrs);
                    checkLines();
                }
            }
            
            @Override
            public void remove(FilterBypass fb, int offset, int length) 
            throws BadLocationException {
                if (offset >= current) {
                    super.remove(fb, offset, length);
                    checkLines();
                }
            }
        });
        
        setBackground(NORMAL_BACKGROUND);
    }
    
    void reset() {
        current = 0;
        waiting = 0;
        setCaretPosition(0);
        setBackground(NORMAL_BACKGROUND);
    }
    
    void clear() {
        setText(null);
        lines.clear();
        current = 0;
        waiting = 0;
        setBackground(NORMAL_BACKGROUND);
    }
    
    String readLine() {
        if (lines.isEmpty()) {
            waiting += 1;
            try {
                checkLines();
            } finally {
                waiting -= 1;
            }
        }
        if (lines.isEmpty()) {
            setBackground(WAITING_BACKGROUND);
            waiting += 1;
            for (Listener listener : listeners) {
                listener.waiting(waiting);
            }
            requestFocusInWindow();
            setCaretPosition(doc.getLength());
        }
        
        try {
            return lines.take();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return "";
        } finally {
            for (Listener listener : listeners) {
                listener.unblocking(waiting);
            }
            waiting -= 1;
            if (waiting <= 0) {
                setBackground(NORMAL_BACKGROUND);
                waiting = 0;
            }
        }
    }

    private void checkLines() {
        if (waiting > 0) {
            int available = doc.getLength() - current;
            if (available == 0)
                return;
            String text;
            try {
                text = doc.getText(current, available);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
                return;
            }
            int index = text.indexOf("\n");
            if (index == -1) 
                return;
            current += index + 1;
            lines.offer(text.substring(0, index));
        }
    }

    void addListener(Listener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }
    
    boolean removeListener(Listener listener) {
        return listeners.remove(listener);
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    interface Listener {
        void waiting(int count);
        void unblocking(int count);
    }
}
