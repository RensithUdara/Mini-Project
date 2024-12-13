import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * FirstFitMemoryAllocationSwing
 * This program simulates the First Fit memory allocation algorithm using a Swing-based GUI.
 * It displays memory block details including True/False status for whether the block is occupied.
 */
public class FirstFitMemoryAllocationSwing {

    // Memory Block class
    static class MemoryBlock {
        int blockSize;
        int allocatedSize;
        boolean isOccupied;

        MemoryBlock(int blockSize) {
            this.blockSize = blockSize;
            this.allocatedSize = 0;
            this.isOccupied = false;
        }
    }

    private ArrayList<MemoryBlock> memoryBlocks;
    private JTable memoryTable;
    private MemoryTableModel tableModel;

    public FirstFitMemoryAllocationSwing() {
        // Initialize memory blocks
        memoryBlocks = new ArrayList<>();
        memoryBlocks.add(new MemoryBlock(200));
        memoryBlocks.add(new MemoryBlock(300));
        memoryBlocks.add(new MemoryBlock(100));
        memoryBlocks.add(new MemoryBlock(500));
        memoryBlocks.add(new MemoryBlock(50));
    }

    /**
     * Creates the GUI and sets up the actions.
     */
    public void createAndShowGUI() {
        // Create the frame
        JFrame frame = new JFrame("First Fit Memory Allocation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null); // Center the window on screen

        // Use a light theme for the background
        frame.getContentPane().setBackground(new Color(245, 245, 245));

        // Create the table model and table
        tableModel = new MemoryTableModel(memoryBlocks);
        memoryTable = new JTable(tableModel);
        memoryTable.setFont(new Font("Arial", Font.PLAIN, 14));
        memoryTable.setRowHeight(30);
        memoryTable.setSelectionBackground(new Color(173, 216, 230)); // Light blue selection color
        memoryTable.setSelectionForeground(Color.BLACK);

        // Add striped row color
        memoryTable.setDefaultRenderer(Object.class, (table, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel(value.toString());
            if (row % 2 == 0) {
                label.setBackground(new Color(240, 240, 240)); // Light gray for even rows
            } else {
                label.setBackground(Color.WHITE);
            }

            if (column == 4) { // Check the 'Occupied' column
                String isOccupied = (String) table.getValueAt(row, column);
                if (isOccupied.equals("Yes")) {
                    label.setBackground(new Color(144, 238, 144)); // Light green for Yes
                } else if (isOccupied.equals("No")) {
                    label.setBackground(new Color(255, 99, 71)); // Light red for No
                }
            }
            label.setOpaque(true);
            return label;
        });

        // Create GUI elements
        JLabel processLabel = new JLabel("Process Size (KB):");
        processLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField processField = new JTextField(10);
        processField.setFont(new Font("Arial", Font.PLAIN, 14));
        JButton allocateButton = createStyledButton("Allocate Memory");
        JButton deallocateButton = createStyledButton("Deallocate Block");
        JButton resetButton = createStyledButton("Reset Memory");

        // Layout setup
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(new Color(245, 245, 245)); // Match background
        inputPanel.add(processLabel);
        inputPanel.add(processField);
        inputPanel.add(allocateButton);
        inputPanel.add(deallocateButton);
        inputPanel.add(resetButton);

        JScrollPane tableScrollPane = new JScrollPane(memoryTable);

        // Add panels to the frame
        frame.setLayout(new BorderLayout(10, 10));
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(tableScrollPane, BorderLayout.CENTER);

        // Action listeners
        allocateButton.addActionListener(e -> {
            try {
                int processSize = Integer.parseInt(processField.getText());
                allocateMemory(processSize);
                tableModel.fireTableDataChanged();
                processField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid process size.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });

        deallocateButton.addActionListener(e -> {
            String blockInput = JOptionPane.showInputDialog(frame, "Enter Block Number to Deallocate:");
            if (blockInput != null) {
                try {
                    int blockIndex = Integer.parseInt(blockInput) - 1;
                    deallocateMemory(blockIndex);
                    tableModel.fireTableDataChanged();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid block number.",
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        resetButton.addActionListener(e -> {
            resetMemory();
            tableModel.fireTableDataChanged();
        });

        // Show the frame
        frame.setVisible(true);
    }

    /**
     * Allocates memory using the First Fit algorithm.
     *
     * @param processSize Size of the process to allocate (in KB).
     */
    private void allocateMemory(int processSize) {
        for (int i = 0; i < memoryBlocks.size(); i++) {
            MemoryBlock block = memoryBlocks.get(i);
            if (!block.isOccupied && block.blockSize >= processSize) {
                block.allocatedSize = processSize;
                block.isOccupied = true;
                JOptionPane.showMessageDialog(null, "Process allocated to Block " + (i + 1),
                        "Allocation Successful", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "No suitable block found for process size " + processSize + " KB.",
                "Allocation Failed", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Deallocates memory from a specific block.
     *
     * @param blockIndex The index of the block to deallocate.
     */
    private void deallocateMemory(int blockIndex) {
        if (blockIndex < 0 || blockIndex >= memoryBlocks.size()) {
            JOptionPane.showMessageDialog(null, "Invalid block number.",
                    "Deallocation Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        MemoryBlock block = memoryBlocks.get(blockIndex);
        if (block.isOccupied) {
            block.allocatedSize = 0;
            block.isOccupied = false;
            JOptionPane.showMessageDialog(null, "Block " + (blockIndex + 1) + " deallocated.",
                    "Deallocation Successful", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Block " + (blockIndex + 1) + " is already free.",
                    "Deallocation Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Resets all memory blocks to their initial state.
     */
    private void resetMemory() {
        for (MemoryBlock block : memoryBlocks) {
            block.allocatedSize = 0;
            block.isOccupied = false;
        }
        JOptionPane.showMessageDialog(null, "All memory blocks have been reset.",
                "Reset Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Custom Table Model for displaying memory block information.
     */
    static class MemoryTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Block Number", "Block Size (KB)", "Allocated Size (KB)", "Free Size (KB)", "Occupied (Yes/No)"};
        private final ArrayList<MemoryBlock> memoryBlocks;

        MemoryTableModel(ArrayList<MemoryBlock> memoryBlocks) {
            this.memoryBlocks = memoryBlocks;
        }

        @Override
        public int getRowCount() {
            return memoryBlocks.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            MemoryBlock block = memoryBlocks.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return rowIndex + 1;
                case 1:
                    return block.blockSize;
                case 2:
                    return block.allocatedSize;
                case 3:
                    return block.blockSize - block.allocatedSize;
                case 4:
                    return block.isOccupied ? "Yes" : "No";
            }
            return null;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
    }

    /**
     * Method to create styled buttons.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(100, 149, 237)); // Cornflower Blue
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.setBackground(button.getBackground().darker());
                Timer timer = new Timer(100, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        button.setBackground(new Color(100, 149, 237)); // Reset to original
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });
        return button;
    }

    public static void main(String[] args) {
        // Run the GUI application
        SwingUtilities.invokeLater(() -> new FirstFitMemoryAllocationSwing().createAndShowGUI());
    }
}
