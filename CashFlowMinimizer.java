import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class CashFlowMinimizer extends JFrame {
    private JTextField payerField, receiverField, amountField;
    private DefaultTableModel tableModel;
    private HashMap<String, Integer> netBalance;

    public CashFlowMinimizer() {
        setTitle("Cash Flow Minimizer");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Panel for input
        JPanel inputPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        payerField = new JTextField();
        receiverField = new JTextField();
        amountField = new JTextField();
        JButton addButton = new JButton("Add Transaction");

        inputPanel.add(new JLabel("Payer:"));
        inputPanel.add(new JLabel("Receiver:"));
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(payerField);
        inputPanel.add(receiverField);
        inputPanel.add(amountField);

        // Table to display transactions
        String[] columnNames = {"Payer", "Receiver", "Amount"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable transactionTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(transactionTable);

        JButton minimizeButton = new JButton("Minimize Transactions");
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);

        addButton.addActionListener(e -> addTransaction());
        minimizeButton.addActionListener(e -> minimizeCashFlow(resultArea));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(minimizeButton);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(new JScrollPane(resultArea), BorderLayout.EAST);

        netBalance = new HashMap<>();
    }

    private void addTransaction() {
        String payer = payerField.getText().trim();
        String receiver = receiverField.getText().trim();
        int amount;

        try {
            amount = Integer.parseInt(amountField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid amount!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (payer.isEmpty() || receiver.isEmpty() || amount <= 0) {
            JOptionPane.showMessageDialog(this, "Enter valid transaction details!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        tableModel.addRow(new Object[]{payer, receiver, amount});
        netBalance.put(payer, netBalance.getOrDefault(payer, 0) - amount);
        netBalance.put(receiver, netBalance.getOrDefault(receiver, 0) + amount);

        payerField.setText("");
        receiverField.setText("");
        amountField.setText("");
    }

    private void minimizeCashFlow(JTextArea resultArea) {
        java.util.List<Integer> balances = new ArrayList<>(netBalance.values());
        java.util.List<String> names = new ArrayList<>(netBalance.keySet());


        PriorityQueue<Integer> positive = new PriorityQueue<>(Collections.reverseOrder());
        PriorityQueue<Integer> negative = new PriorityQueue<>();

        for (int i = 0; i < balances.size(); i++) {
            if (balances.get(i) > 0) {
                positive.add(i);
            } else if (balances.get(i) < 0) {
                negative.add(i);
            }
        }

        StringBuilder result = new StringBuilder("Optimized Transactions:\n");
        while (!positive.isEmpty() && !negative.isEmpty()) {
            int creditor = positive.poll();
            int debtor = negative.poll();

            int min = Math.min(balances.get(creditor), -balances.get(debtor));
            balances.set(creditor, balances.get(creditor) - min);
            balances.set(debtor, balances.get(debtor) + min);

            result.append(names.get(debtor)).append(" pays ₹").append(min)
                  .append(" to ").append(names.get(creditor)).append("\n");

            if (balances.get(creditor) > 0) {
                positive.add(creditor);
            }
            if (balances.get(debtor) < 0) {
                negative.add(debtor);
            }
        }

        resultArea.setText(result.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CashFlowMinimizer().setVisible(true));
    }
}
