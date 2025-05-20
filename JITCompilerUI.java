import javax.swing.*;
import java.awt.*;
import java.io.*;

public class JITCompilerUI {

    private JTextArea outputArea;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JITCompilerUI().createAndShowGUI());
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Java JIT Compiler");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 700);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Java JIT Compiler", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 28));
        panel.add(title, BorderLayout.NORTH);

        JTextArea codeArea = new JTextArea(14, 65);
        codeArea.setFont(new Font("Consolas", Font.PLAIN, 16));
        codeArea.setBorder(BorderFactory.createTitledBorder("Enter Java Method Body"));
        panel.add(new JScrollPane(codeArea), BorderLayout.CENTER);

        outputArea = new JTextArea(12, 65);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.WHITE);
        outputArea.setBorder(BorderFactory.createTitledBorder("Output Terminal"));
        panel.add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        JButton runButton = new JButton("Run Code");
        runButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        runButton.setBackground(new Color(34, 139, 34));
        runButton.setForeground(Color.WHITE);
        runButton.addActionListener(e -> runUserCode(codeArea.getText()));
        panel.add(runButton, BorderLayout.EAST);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void runUserCode(String userCode) {
        outputArea.setText(""); // Clear previous output

        // Capture System.out
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream combinedStream = new PrintStream(new TeeOutputStream(System.out, baos));
        PrintStream originalOut = System.out;
        System.setOut(combinedStream);

        try {
            String result = CodeExecutor.compileAndRun(userCode);
            if (result.equals("Compilation Failed!")) {
                outputArea.setText(result); // Display compilation error
            } else {
                outputArea.setText(result); // Display program output
            }
        } catch (Exception e) {
            e.printStackTrace();
            outputArea.setText("Error: " + e.getMessage());
        } finally {
            System.out.flush(); // Correct method to flush the output stream
            System.setOut(originalOut); // Restore System.out
        }
    }

    // Send output to both original System.out and baos
    static class TeeOutputStream extends OutputStream {
        private final OutputStream out1, out2;

        TeeOutputStream(OutputStream out1, OutputStream out2) {
            this.out1 = out1;
            this.out2 = out2;
        }

        public void write(int b) throws IOException {
            out1.write(b);
            out2.write(b);
        }
    }
}