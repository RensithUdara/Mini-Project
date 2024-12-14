import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

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
        memoryBlocks = new ArrayList<>();
        initializeMemoryBlocks();
    }

    private void initializeMemoryBlocks() {
        memoryBlocks.add(new MemoryBlock(200));
        memoryBlocks.add(new MemoryBlock(300));
        memoryBlocks.add(new MemoryBlock(100));
        memoryBlocks.add(new MemoryBlock(500));
        memoryBlocks.add(new MemoryBlock(50));
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("First Fit Memory Allocation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);

        tableModel = new MemoryTableModel(memoryBlocks);
        memoryTable = createMemoryTable();

        JPanel inputPanel = createInputPanel();
        JScrollPane tableScrollPane = new JScrollPane(memoryTable);

        frame.setLayout(new BorderLayout(10, 10));
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(tableScrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JTable createMemoryTable() {
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 18));
        table.setRowHeight(40);
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setSelectionForeground(Color.BLACK);
        table.setDefaultRenderer(Object.class, new CustomRenderer());
        return table;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 245));

        JLabel processLabel = new JLabel("Process Size (KB):");
        processLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField processField = new JTextField(10);
        processField.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton allocateButton = createStyledButton("Allocate Memory");
        JButton deallocateButton = createStyledButton("Deallocate Block");
        JButton resetButton = createStyledButton("Reset Memory");

        allocateButton.addActionListener(e -> handleAllocate(processField));
        deallocateButton.addActionListener(e -> handleDeallocate());
        resetButton.addActionListener(e -> handleReset());

        panel.add(processLabel);
        panel.add(processField);
        panel.add(allocateButton);
        panel.add(deallocateButton);
        panel.add(resetButton);

        return panel;
    }

    private void handleAllocate(JTextField processField) {
        try {
            int processSize = Integer.parseInt(processField.getText());
            if (processSize <= 0) throw new NumberFormatException();
            allocateMemory(processSize);
            tableModel.fireTableDataChanged();
            processField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please enter a valid positive process size.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeallocate() {
        String blockInput = JOptionPane.showInputDialog("Enter Block Number to Deallocate:");
        if (blockInput != null) {
            try {
                int blockIndex = Integer.parseInt(blockInput) - 1;
                deallocateMemory(blockIndex);
                tableModel.fireTableDataChanged();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid block number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleReset() {
        resetMemory();
        tableModel.fireTableDataChanged();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void allocateMemory(int processSize) {
        for (int i = 0; i < memoryBlocks.size(); i++) {
            MemoryBlock block = memoryBlocks.get(i);
            if (!block.isOccupied && block.blockSize >= processSize) {
                block.allocatedSize = processSize;
                block.isOccupied = true;
                JOptionPane.showMessageDialog(null, "Process allocated to Block " + (i + 1), "Allocation Successful", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "No suitable block found for process size " + processSize + " KB.", "Allocation Failed", JOptionPane.ERROR_MESSAGE);
    }

    private void deallocateMemory(int blockIndex) {
        if (blockIndex < 0 || blockIndex >= memoryBlocks.size()) {
            JOptionPane.showMessageDialog(null, "Invalid block number.", "Deallocation Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        MemoryBlock block = memoryBlocks.get(blockIndex);
        if (block.isOccupied) {
            block.allocatedSize = 0;
            block.isOccupied = false;
            JOptionPane.showMessageDialog(null, "Block " + (blockIndex + 1) + " deallocated.", "Deallocation Successful", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Block " + (blockIndex + 1) + " is already free.", "Deallocation Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetMemory() {
        for (MemoryBlock block : memoryBlocks) {
            block.allocatedSize = 0;
            block.isOccupied = false;
        }
        JOptionPane.showMessageDialog(null, "All memory blocks have been reset.", "Reset Successful", JOptionPane.INFORMATION_MESSAGE);
    }

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
                case 0: return rowIndex + 1;
                case 1: return block.blockSize;
                case 2: return block.allocatedSize;
                case 3: return block.blockSize - block.allocatedSize;
                case 4: return block.isOccupied ? "Yes" : "No";
            }
            return null;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
    }

    static class CustomRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel(value.toString());
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);

            if (row % 2 == 0) {
                label.setBackground(new Color(240, 240, 240));
            } else {
                label.setBackground(Color.WHITE);
            }

            if (column == 4) {
                String isOccupied = value.toString();
                label.setBackground(isOccupied.equals("Yes") ? new Color(144, 238, 144) : new Color(255, 99, 71));
            }

            return label;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FirstFitMemoryAllocationSwing().createAndShowGUI());
    }
}
