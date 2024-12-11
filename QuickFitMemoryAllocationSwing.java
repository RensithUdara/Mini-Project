import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * QuickFitMemoryAllocationSwing
 * This program simulates the Quick Fit memory allocation algorithm using a Swing-based GUI.
 * It organizes memory into fixed-size categories and allocates quickly for frequent, fixed-size requests.
 */
public class QuickFitMemoryAllocationSwing {

    // Fixed categories of block sizes for Quick Fit
    private final int[] categories = {50, 100, 200, 300, 500}; // Block sizes
    private final HashMap<Integer, LinkedList<Integer>> freeLists; // Free lists for each category
    private JTable memoryTable;
    private QuickFitTableModel tableModel;

    public QuickFitMemoryAllocationSwing() {
        // Initialize free lists for each category
        freeLists = new HashMap<>();
        for (int size : categories) {
            LinkedList<Integer> freeList = new LinkedList<>();
            for (int i = 0; i < 5; i++) { // Add 5 blocks of each size initially
                freeList.add(size);
            }
            freeLists.put(size, freeList);
        }
    }

    /**
     * Creates the GUI and sets up the actions.
     */
    public void createAndShowGUI() {
        // Create the frame
        JFrame frame = new JFrame("Quick Fit Memory Allocation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        // Create the table model and table
        tableModel = new QuickFitTableModel(freeLists);
        memoryTable = new JTable(tableModel);

        // Create GUI elements
        JLabel processLabel = new JLabel("Process Size (KB):");
        JTextField processField = new JTextField(10);
        JButton allocateButton = new JButton("Allocate Memory");
        JButton deallocateButton = new JButton("Deallocate Memory");
        JButton resetButton = new JButton("Reset Memory");

        // Layout setup
        JPanel inputPanel = new JPanel();
        inputPanel.add(processLabel);
        inputPanel.add(processField);
        inputPanel.add(allocateButton);
        inputPanel.add(deallocateButton);
        inputPanel.add(resetButton);

        JScrollPane tableScrollPane = new JScrollPane(memoryTable);

        // Add panels to the frame
        frame.setLayout(new BorderLayout());
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
            String sizeInput = JOptionPane.showInputDialog(frame, "Enter Block Size to Deallocate:");
            if (sizeInput != null) {
                try {
                    int blockSize = Integer.parseInt(sizeInput);
                    deallocateMemory(blockSize);
                    tableModel.fireTableDataChanged();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid block size.",
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
     * Allocates memory for a process using the Quick Fit algorithm.
     *
     * @param processSize The size of the process to allocate (in KB).
     */
    private void allocateMemory(int processSize) {
        for (int size : categories) {
            if (processSize <= size && !freeLists.get(size).isEmpty()) {
                freeLists.get(size).removeFirst();
                JOptionPane.showMessageDialog(null, "Process of size " + processSize + " KB allocated in block size " + size + " KB.",
                        "Allocation Successful", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "No suitable block found for process size " + processSize + " KB.",
                "Allocation Failed", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Deallocates memory from a specific category.
     *
     * @param blockSize The size of the block to deallocate.
     */
    private void deallocateMemory(int blockSize) {
        if (!freeLists.containsKey(blockSize)) {
            JOptionPane.showMessageDialog(null, "Invalid block size.",
                    "Deallocation Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        freeLists.get(blockSize).add(blockSize);
        JOptionPane.showMessageDialog(null, "Block of size " + blockSize + " KB deallocated.",
                "Deallocation Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Resets all free lists to their initial state.
     */
    private void resetMemory() {
        freeLists.clear();
        for (int size : categories) {
            LinkedList<Integer> freeList = new LinkedList<>();
            for (int i = 0; i < 5; i++) { // Add 5 blocks of each size initially
                freeList.add(size);
            }
            freeLists.put(size, freeList);
        }
        JOptionPane.showMessageDialog(null, "Memory has been reset.",
                "Reset Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Custom Table Model for displaying memory free lists.
     */
    static class QuickFitTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Block Size (KB)", "Free Blocks"};
        private final HashMap<Integer, LinkedList<Integer>> freeLists;

        QuickFitTableModel(HashMap<Integer, LinkedList<Integer>> freeLists) {
            this.freeLists = freeLists;
        }

        @Override
        public int getRowCount() {
            return freeLists.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            int size = (int) freeLists.keySet().toArray()[rowIndex];
            switch (columnIndex) {
                case 0:
                    return size;
                case 1:
                    return freeLists.get(size).size(); // Number of free blocks in this category
            }
            return null;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
    }

    /**
     * Main method to start the application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QuickFitMemoryAllocationSwing simulator = new QuickFitMemoryAllocationSwing();
            simulator.createAndShowGUI();
        });
    }
}
