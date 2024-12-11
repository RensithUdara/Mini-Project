import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
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
        frame.setSize(600, 400);

        // Create the table model and table
        tableModel = new MemoryTableModel(memoryBlocks);
        memoryTable = new JTable(tableModel);

        // Create GUI elements
        JLabel processLabel = new JLabel("Process Size (KB):");
        JTextField processField = new JTextField(10);
        JButton allocateButton = new JButton("Allocate Memory");
        JButton deallocateButton = new JButton("Deallocate Block");
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
        private final String[] columnNames = {"Block Number", "Block Size (KB)", "Allocated Size (KB)", "Free Size (KB)", "Occupied (True/False)"};
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
                    return block.isOccupied;
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
            FirstFitMemoryAllocationSwing simulator = new FirstFitMemoryAllocationSwing();
            simulator.createAndShowGUI();
        });
    }
}
