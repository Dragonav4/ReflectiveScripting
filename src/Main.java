import javax.swing.*;
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { // Event Dispatch Thread
            new ModelingFrameworkSample().setVisible(true);
        });
    }
}
