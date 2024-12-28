import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.Arrays;

public class ModelingFrameworkSample extends JFrame {
    private static final String DATA_FOLDER = "data/";
    private static final String SCRIPTS_FOLDER = "src/scripts/";
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 500;
    private DefaultTableModel tableModel;
    private JList<String> modelList, dataList;
    private Controller modelController;
    private JButton runModelButton, runScriptButton, createAndRunScriptButton;

    public ModelingFrameworkSample() {

        super("Modelling framework sample");
        initFrame();
        JPanel leftPanel = createLeftPanel();
        JPanel rightPanel = createRightPanel();

        // Add panels to the main container
        getContentPane().setLayout(new BorderLayout(5, 5));
        getContentPane().add(leftPanel, BorderLayout.WEST);
        getContentPane().add(rightPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    /**
     * Configures the main frame settings.
     */
    private void initFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);
    }

    private JPanel createLeftPanel() {
        // --- LEFT SIDE (Panel for choosing model and data.txt) ---
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setLayout(new BorderLayout(5, 5));
        // Title of leftPanel
        JLabel selectModelLabel = new JLabel("Select model and data");
        leftPanel.add(selectModelLabel, BorderLayout.NORTH);

        // Two lists(models and data files) one to one
        JPanel listPanel = new JPanel(new GridLayout(1, 2, 5, 5));

        modelList = new JList<>(new String[]{"Model1", "Model2", "Model3", "MultiAgentSim"});
        modelList.addListSelectionListener(_ -> handleModelSelection());

        dataList = new JList<>(new String[]{"data1.txt", "data2.txt", "data3.txt"});
        dataList.addListSelectionListener(_ -> handleDataSelection());

        // Scrolling
        JScrollPane modelScroll = new JScrollPane(modelList);
        JScrollPane dataScroll = new JScrollPane(dataList);

        // Add scroll panes to the list panel
        listPanel.add(modelScroll);
        listPanel.add(dataScroll);

        // Add list panel to the left panel
        leftPanel.add(listPanel, BorderLayout.CENTER);

        runModelButton = new JButton("Run model");
        runModelButton.addActionListener(_ -> onRunModelButtonClicked());
        runModelButton.setEnabled(false);

        leftPanel.add(runModelButton, BorderLayout.SOUTH);

        return leftPanel;
    }

    private void handleModelSelection() {
        try {
            String modelName = modelList.getSelectedValue();
            modelController = new Controller(modelName);
        } catch (Exception ex) {
            modelController = null;
            runModelButton.setEnabled(false);
            runScriptButton.setEnabled(false);
            createAndRunScriptButton.setEnabled(false);
            showErrorMessage(ex.getMessage());
        }
    }

    private void handleDataSelection() {
        String selectedData = dataList.getSelectedValue();
        try {
            if (modelController == null)
                throw new Exception("Please select model first");
            modelController.readDataFrom(DATA_FOLDER + selectedData);
            refreshTable();
            runModelButton.setEnabled(modelController.isInitialized());
            runScriptButton.setEnabled(modelController.isInitialized());
            createAndRunScriptButton.setEnabled(modelController.isInitialized());
        } catch (Exception ex) {
            runModelButton.setEnabled(false);
            runScriptButton.setEnabled(false);
            createAndRunScriptButton.setEnabled(false);
            showErrorMessage(ex.getMessage());
        }
    }

    private JPanel createRightPanel() {
        // --- RIGHT SIDE (Panel for table and additional buttons) ---
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        tableModel = new DefaultTableModel(new Object[][]{}, new String[]{}); // row1Col1, r2c2 for OBJ[][] and String[] are headers
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // ---Bottom buttons ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        runScriptButton = new JButton("Run script from file");
        runScriptButton.setEnabled(false);
        createAndRunScriptButton = new JButton("Create and run ad hoc script");
        createAndRunScriptButton.setEnabled(false);
        bottomPanel.add(runScriptButton);
        bottomPanel.add(createAndRunScriptButton);

        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        runScriptButton.addActionListener(_ -> onRunScriptButtonClicked());
        createAndRunScriptButton.addActionListener(_ -> onCreateAndRunAdHocScript());
        return rightPanel;
    }

    private void onRunScriptButtonClicked() {
        if (modelController == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please run a model first, so that we have data to pass to the script.",
                    "No model data",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        JFileChooser fileChooser = new JFileChooser(SCRIPTS_FOLDER);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File scriptFile = fileChooser.getSelectedFile();
            try {
                modelController.runScriptFromFile(scriptFile.getPath());
                refreshTable();
            } catch (Exception e) {
                showErrorMessage("Error reading or executing script: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }



    /**
     * Executes a script passed as a code string.
     */
    private void executeScriptFromString(String scriptText) {
        try {
            modelController.runScript(scriptText);
            refreshTable();
        }
        catch(Exception ex) {
            showErrorMessage(ex.getMessage());
        }
    }


    private void showErrorMessage(String text) {
        JOptionPane.showMessageDialog(this, text, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Handles the "Run model" button click event.
     */
    private void onRunModelButtonClicked() {
        try {
            modelController.runModel();
            refreshTable();
        } catch (Exception ex) {
            showErrorMessage("Error running model: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void onCreateAndRunAdHocScript() {
        if (modelController == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please run a model first, so that we have data to pass to the script.",
                    "No model data",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        JTextArea textArea = new JTextArea(10, 40);
        textArea.setText("// Write your Groovy script here:\n");
        int option = JOptionPane.showConfirmDialog(
                this,
                new JScrollPane(textArea),
                "Enter your ad hoc Groovy script",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (option == JOptionPane.OK_OPTION) {
            String scriptText = textArea.getText();
            try {
                //Calls method which takes and executing String
                executeScriptFromString(scriptText);

                JOptionPane.showMessageDialog(
                        this,
                        "Ad hoc script executed successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception e) {
                showErrorMessage("Error reading or executing script: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void refreshTable() {
        String csvResults = modelController.getResultAsCSV();
        String[][] data = parseCsvForTable(csvResults);
        if (data.length == 0) {
            JOptionPane.showMessageDialog(this, "No data to display.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String[] columnNames = data[0];
        String[][] tableData = Arrays.copyOfRange(data, 1, data.length);
        tableModel.setDataVector(tableData, columnNames);
    }

    private String[][] parseCsvForTable(String csv) {
        String[] rows = csv.split("\n");
        String[][] data = new String[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            data[i] = rows[i].split(",");
        }
        return data;
    }
}
