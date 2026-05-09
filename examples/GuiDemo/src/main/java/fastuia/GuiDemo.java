package fastuia;

import fastuia.FastUIA;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * GUI Demo for FastUIA - Interactive testing of UI Automation methods
 */
public class GuiDemo extends JFrame {
    private FastUIA uia;
    private JTextArea outputArea;
    private JTextField valueField;
    private long currentElement = 0;

    public GuiDemo() {
        uia = new FastUIA();
        initComponents();
    }

    private void initComponents() {
        setTitle("FastUIA Interactive Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        // Main panel with scroll
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Element Info Panel
        JPanel elementPanel = createElementPanel();
        mainPanel.add(elementPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Actions Panel
        JPanel actionsPanel = createActionsPanel();
        mainPanel.add(actionsPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Traversal Panel
        JPanel traversalPanel = createTraversalPanel();
        mainPanel.add(traversalPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Output Panel
        JPanel outputPanel = createOutputPanel();
        mainPanel.add(outputPanel);

        scrollPane.setViewportView(mainPanel);
        add(scrollPane);
    }

    private JPanel createElementPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Element Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JButton(new AbstractAction("Get Focused Element") {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentElement = uia.GetFocusedElement();
                if (currentElement != 0) {
                    log("Focused element handle: " + currentElement);
                    updateElementInfo();
                } else {
                    log("No focused element found");
                }
            }
        }), gbc);

        gbc.gridx = 1;
        panel.add(new JButton(new AbstractAction("Get Control Type") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentElement == 0) {
                    log("Error: No element selected");
                    return;
                }
                String type = uia.GetControlType(currentElement);
                log("Control Type: " + type);
            }
        }), gbc);

        gbc.gridx = 2;
        panel.add(new JButton(new AbstractAction("Get Name") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentElement == 0) {
                    log("Error: No element selected");
                    return;
                }
                String name = uia.GetName(currentElement);
                log("Name: " + name);
            }
        }), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JButton(new AbstractAction("Get Value") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentElement == 0) {
                    log("Error: No element selected");
                    return;
                }
                String value = uia.GetValue(currentElement);
                log("Value: " + value);
            }
        }), gbc);

        gbc.gridx = 1;
        panel.add(new JButton(new AbstractAction("Get Bounding Rect") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentElement == 0) {
                    log("Error: No element selected");
                    return;
                }
                int[] rect = uia.GetBoundingRect(currentElement);
                if (rect != null) {
                    log(String.format("Bounding Rect: x=%d, y=%d, width=%d, height=%d", 
                        rect[0], rect[1], rect[2], rect[3]));
                } else {
                    log("Could not get bounding rect");
                }
            }
        }), gbc);

        gbc.gridx = 2;
        panel.add(new JButton(new AbstractAction("Get Selection") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentElement == 0) {
                    log("Error: No element selected");
                    return;
                }
                String selection = uia.GetSelection(currentElement);
                log("Selection: " + selection);
            }
        }), gbc);

        return panel;
    }

    private JPanel createActionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Actions"));

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 5, 5));

        buttonPanel.add(new JButton(new AbstractAction("Invoke") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentElement == 0) {
                    log("Error: No element selected");
                    return;
                }
                uia.Invoke(currentElement);
                log("Invoked element");
            }
        }));

        buttonPanel.add(new JButton(new AbstractAction("Expand") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentElement == 0) {
                    log("Error: No element selected");
                    return;
                }
                uia.Expand(currentElement);
                log("Expanded element");
            }
        }));

        buttonPanel.add(new JButton(new AbstractAction("Collapse") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentElement == 0) {
                    log("Error: No element selected");
                    return;
                }
                uia.Collapse(currentElement);
                log("Collapsed element");
            }
        }));

        buttonPanel.add(new JButton(new AbstractAction("Scroll Top-Left") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentElement == 0) {
                    log("Error: No element selected");
                    return;
                }
                uia.Scroll(currentElement, 0, 0);
                log("Scrolled to top-left");
            }
        }));

        buttonPanel.add(new JButton(new AbstractAction("Get Value") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentElement == 0) {
                    log("Error: No element selected");
                    return;
                }
                String value = uia.GetValue(currentElement);
                valueField.setText(value != null ? value : "");
                log("Got value: " + value);
            }
        }));

        buttonPanel.add(new JButton(new AbstractAction("Set Value") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentElement == 0) {
                    log("Error: No element selected");
                    return;
                }
                String value = valueField.getText();
                if (value != null && !value.isEmpty()) {
                    uia.SetValue(currentElement, value);
                    log("Set value: " + value);
                } else {
                    log("Error: Please enter a value");
                }
            }
        }));

        buttonPanel.add(new JButton(new AbstractAction("Set Selection") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentElement == 0) {
                    log("Error: No element selected");
                    return;
                }
                uia.SetSelection(currentElement, "");
                log("Selected element");
            }
        }));

        buttonPanel.add(new JButton(new AbstractAction("Scroll Center") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentElement == 0) {
                    log("Error: No element selected");
                    return;
                }
                uia.Scroll(currentElement, 50, 50);
                log("Scrolled to center");
            }
        }));

        buttonPanel.add(new JButton(new AbstractAction("Scroll Bottom-Right") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentElement == 0) {
                    log("Error: No element selected");
                    return;
                }
                uia.Scroll(currentElement, 100, 100);
                log("Scrolled to bottom-right");
            }
        }));

        panel.add(buttonPanel, BorderLayout.CENTER);

        // Input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Element Value:"));
        valueField = new JTextField(30);
        inputPanel.add(valueField);
        panel.add(inputPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTraversalPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 5, 5));
        panel.setBorder(new TitledBorder("Traversal"));

        panel.add(new JButton(new AbstractAction("Get Parent") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentElement == 0) {
                    log("Error: No element selected");
                    return;
                }
                long parent = uia.GetParent(currentElement);
                if (parent != 0) {
                    currentElement = parent;
                    log("Moved to parent element: " + currentElement);
                    updateElementInfo();
                } else {
                    log("No parent element found");
                }
            }
        }));

        panel.add(new JButton(new AbstractAction("Get First Child") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentElement == 0) {
                    log("Error: No element selected");
                    return;
                }
                long child = uia.GetFirstChild(currentElement);
                if (child != 0) {
                    currentElement = child;
                    log("Moved to first child: " + currentElement);
                    updateElementInfo();
                } else {
                    log("No child element found");
                }
            }
        }));

        panel.add(new JButton(new AbstractAction("Get Next Sibling") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentElement == 0) {
                    log("Error: No element selected");
                    return;
                }
                long sibling = uia.GetNextSibling(currentElement);
                if (sibling != 0) {
                    currentElement = sibling;
                    log("Moved to next sibling: " + currentElement);
                    updateElementInfo();
                } else {
                    log("No next sibling found");
                }
            }
        }));

        panel.add(new JButton(new AbstractAction("Get Previous Sibling") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentElement == 0) {
                    log("Error: No element selected");
                    return;
                }
                long sibling = uia.GetPreviousSibling(currentElement);
                if (sibling != 0) {
                    currentElement = sibling;
                    log("Moved to previous sibling: " + currentElement);
                    updateElementInfo();
                } else {
                    log("No previous sibling found");
                }
            }
        }));

        return panel;
    }

    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Output Log"));
        
        outputArea = new JTextArea(15, 60);
        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JButton clearButton = new JButton("Clear Log");
        clearButton.addActionListener(e -> outputArea.setText(""));
        panel.add(clearButton, BorderLayout.SOUTH);
        
        return panel;
    }

    private void updateElementInfo() {
        String name = uia.GetName(currentElement);
        String type = uia.GetControlType(currentElement);
        int[] rect = uia.GetBoundingRect(currentElement);
        
        log("--- Element Info ---");
        log("Handle: " + currentElement);
        log("Name: " + (name != null ? name : "N/A"));
        log("Control Type: " + (type != null ? type : "N/A"));
        if (rect != null) {
            log(String.format("Rect: x=%d, y=%d, w=%d, h=%d", rect[0], rect[1], rect[2], rect[3]));
        }
        log("-------------------");
    }

    private void log(String message) {
        outputArea.append(message + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            GuiDemo demo = new GuiDemo();
            demo.setVisible(true);
        });
    }
}
