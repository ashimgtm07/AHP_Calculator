import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AHP extends JFrame {
    private int numberOfCriteria;
    private String[] criteriaNames;
    private double[][] pairwiseMatrix;
    private JTextField numberOfCriteriaField;
    private JTextField[] criteriaNameFields;
    private JRadioButton[][] influenceButtons;
    private ButtonGroup[][] influenceGroups;
    private JRadioButton[][] scaleButtons;
    private ButtonGroup[][] scaleGroups;
    private JTextArea resultArea;
    private JLabel consistencyLabel;

    public AHP() {
        setTitle("AHP Calculator");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initial Panel to get the number of criteria
        JPanel initialPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        initialPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initialPanel.add(new JLabel("Enter number of criteria:"));
        numberOfCriteriaField = new JTextField();
        initialPanel.add(numberOfCriteriaField);

        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    numberOfCriteria = Integer.parseInt(numberOfCriteriaField.getText());
                    showCriteriaNamesPanel();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(AHP.this, "Please enter a valid number.");
                }
            }
        });
        initialPanel.add(nextButton);

        add(initialPanel, BorderLayout.CENTER);
    }
    private void showCriteriaNamesPanel() {
        getContentPane().removeAll();
        setSize(400, 300);
        JPanel criteriaPanel = new JPanel(new GridLayout(numberOfCriteria + 1, 2, 10, 10));
        criteriaPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        criteriaNameFields = new JTextField[numberOfCriteria];
        for (int i = 0; i < numberOfCriteria; i++) {
            criteriaPanel.add(new JLabel("Criteria " + (i + 1) + ":"));
            criteriaNameFields[i] = new JTextField();
            criteriaPanel.add(criteriaNameFields[i]);
        }
        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                criteriaNames = new String[numberOfCriteria];
                for (int i = 0; i < numberOfCriteria; i++) {
                    criteriaNames[i] = criteriaNameFields[i].getText();
                }
                showPairwiseComparisonPanel();
            }
        });
        criteriaPanel.add(nextButton);
        add(criteriaPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    private void showPairwiseComparisonPanel() {
        getContentPane().removeAll();
        setTitle("Pairwise Comparisons");
        setSize(800, 600);
        JPanel mainPanel = new JPanel(new GridLayout((numberOfCriteria * (numberOfCriteria - 1)) / 2 + 2, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pairwiseMatrix = new double[numberOfCriteria][numberOfCriteria];
        influenceButtons = new JRadioButton[(numberOfCriteria * (numberOfCriteria - 1)) / 2][2];
        influenceGroups = new ButtonGroup[(numberOfCriteria * (numberOfCriteria - 1)) / 2][1];
        scaleButtons = new JRadioButton[(numberOfCriteria * (numberOfCriteria - 1)) / 2][9];
        scaleGroups = new ButtonGroup[(numberOfCriteria * (numberOfCriteria - 1)) / 2][1];
        int row = 0;
        JPanel headerPanel = new JPanel(new GridLayout(1, 3));
        headerPanel.add(new JLabel("Criteria Comparison", JLabel.CENTER));
        headerPanel.add(new JLabel("Influence", JLabel.CENTER));
        headerPanel.add(new JLabel("Scale (1 to 9)", JLabel.CENTER));
        mainPanel.add(headerPanel);
        for (int i = 0; i < numberOfCriteria; i++) {
            for (int j = i + 1; j < numberOfCriteria; j++) {
                JPanel rowPanel = new JPanel(new GridLayout(1, 3));
                // Criteria Comparison Label
                rowPanel.add(new JLabel(criteriaNames[i] + " vs " + criteriaNames[j], JLabel.CENTER));
                // Influence selection
                JPanel influencePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                ButtonGroup influenceGroup = new ButtonGroup();
                JRadioButton influenceA = new JRadioButton(criteriaNames[i]);
                JRadioButton influenceB = new JRadioButton(criteriaNames[j]);
                influenceGroup.add(influenceA);
                influenceGroup.add(influenceB);
                influencePanel.add(influenceA);
                influencePanel.add(influenceB);
                rowPanel.add(influencePanel);
                influenceButtons[row][0] = influenceA;
                influenceButtons[row][1] = influenceB;
                influenceGroups[row][0] = influenceGroup;
                // Scale selection
                JPanel scalePanel = new JPanel(new FlowLayout());
                ButtonGroup scaleGroup = new ButtonGroup();
                for (int k = 1; k <= 9; k++) {
                    JRadioButton rb = new JRadioButton(String.valueOf(k));
                    scaleGroup.add(rb);
                    scaleButtons[row][k - 1] = rb;
                    scalePanel.add(rb);
                }
                rowPanel.add(scalePanel);

                scaleGroups[row][0] = scaleGroup;

                mainPanel.add(rowPanel);
                row++;
            }
        }
        JButton calculateButton = new JButton("Calculate AHP");
        calculateButton.addActionListener(new CalculateAHPActionListener());
        mainPanel.add(calculateButton);
        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    private class CalculateAHPActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Reset pairwise matrix
            pairwiseMatrix = new double[numberOfCriteria][numberOfCriteria];
            for (int i = 0; i < numberOfCriteria; i++) {
                pairwiseMatrix[i][i] = 1;
            }
            int row = 0;
            for (int i = 0; i < numberOfCriteria; i++) {
                for (int j = i + 1; j < numberOfCriteria; j++) {
                    boolean found = false;
                    for (int k = 0; k < 9; k++) {
                        if (scaleButtons[row][k].isSelected()) {
                            double value = k + 1;
                            if (influenceButtons[row][0].isSelected()) {
                                pairwiseMatrix[i][j] = value;
                                pairwiseMatrix[j][i] = 1.0 / value;
                            } else if (influenceButtons[row][1].isSelected()) {
                                pairwiseMatrix[i][j] = 1.0 / value;
                                pairwiseMatrix[j][i] = value;
                            }
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        JOptionPane.showMessageDialog(AHP.this, "Please select a scale for " + criteriaNames[i] + " vs " + criteriaNames[j]);
                        return;
                    }
                    row++;
                }
            }
            // Normalize the pairwise comparison matrix
            double[][] normalizedMatrix = normalizeMatrix(pairwiseMatrix);

            // Calculate the priority vector (weights)
            double[] priorityVector = calculatePriorityVector(normalizedMatrix);

            // Calculate the consistency ratio
            double consistencyRatio = calculateConsistencyRatio(pairwiseMatrix, priorityVector);

            // Display the results in a new frame
            showResults(priorityVector, consistencyRatio);
        }
    }
    private void showResults(double[] priorityVector, double consistencyRatio) {
        JFrame resultFrame = new JFrame("AHP Results");
        resultFrame.setSize(400, 300);
        resultFrame.setLayout(new BorderLayout());
        resultArea = new JTextArea(10, 50);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Priority Vector (Weights)"));
        resultArea.setText("Priority Vector (Weights):\n");
        for (int i = 0; i < numberOfCriteria; i++) {
            resultArea.append(String.format("%s: %.4f\n", criteriaNames[i], priorityVector[i]));
        }
        consistencyLabel = new JLabel(String.format("Consistency Ratio (CR) = %.2f%%", consistencyRatio * 100), JLabel.CENTER);
        resultFrame.add(scrollPane, BorderLayout.CENTER);
        resultFrame.add(consistencyLabel, BorderLayout.SOUTH);
        // Check CR and provide guidance if needed
        if (consistencyRatio > 0.1) {
            double targetCR = 0.1; // Adjust this threshold as needed
            double currentCRPercent = consistencyRatio * 100;
            // Construct the message
            String message = String.format("The Consistency Ratio (CR) is %.2f%%, which is higher than %.2f%%.\n", currentCRPercent, targetCR * 100);
            message += "Please consider adjusting your pairwise comparisons to reduce CR:\n";
            message += "- Re-evaluate your comparison values.\n";
            message += "- Ensure consistency in judgments. For instance, if A is judged as 3 times more important than B, and B is 2 times more important than C, then A should ideally be 6 times more important than C.\n";
            message += "- Avoid extreme values unless absolutely necessary.\n";
            message += "\n";
            message += "You can modify the scale (1 to 9) selections to achieve a lower CR.";
            JOptionPane.showMessageDialog(resultFrame,
                    message,
                    "High Consistency Ratio",
                    JOptionPane.WARNING_MESSAGE);
        }
        resultFrame.setVisible(true);
    }
    private double[][] normalizeMatrix(double[][] matrix) {
        double[] columnSum = new double[numberOfCriteria];
        for (int j = 0; j < numberOfCriteria; j++) {
            for (int i = 0; i < numberOfCriteria; i++) {
                columnSum[j] += matrix[i][j];
            }
        }
        double[][] normalizedMatrix = new double[numberOfCriteria][numberOfCriteria];
        for (int i = 0; i < numberOfCriteria; i++) {
            for (int j = 0; j < numberOfCriteria; j++) {
                normalizedMatrix[i][j] = matrix[i][j] / columnSum[j];
            }
        }
        return normalizedMatrix;
    }
    private double[] calculatePriorityVector(double[][] normalizedMatrix) {
        double[] priorityVector = new double[numberOfCriteria];

        for (int i = 0; i < numberOfCriteria; i++) {
            for (int j = 0; j < numberOfCriteria; j++) {
                priorityVector[i] += normalizedMatrix[i][j];
            }
            priorityVector[i] /= numberOfCriteria;
        }
        return priorityVector;
    }
    private double calculateConsistencyRatio(double[][] matrix, double[] priorityVector) {
        double[] weightedSumVector = new double[numberOfCriteria];
        for (int i = 0; i < numberOfCriteria; i++) {
            for (int j = 0; j < numberOfCriteria; j++) {
                weightedSumVector[i] += matrix[i][j] * priorityVector[j];
            }
        }
        double[] consistencyVector = new double[numberOfCriteria];
        for (int i = 0; i < numberOfCriteria; i++) {
            consistencyVector[i] = weightedSumVector[i] / priorityVector[i];
        }
        double lambdaMax = 0;
        for (double v : consistencyVector) {
            lambdaMax += v;
        }
        lambdaMax /= numberOfCriteria;
        double CI = (lambdaMax - numberOfCriteria) / (numberOfCriteria - 1);
        double RI = getRandomIndex(numberOfCriteria);
        return CI / RI;
    }
    private double getRandomIndex(int n) {
        double[] RI = {0.00, 0.00, 0.58, 0.90, 1.12, 1.24, 1.32, 1.41, 1.45}; // RI values for 1 to 9 criteria
        return (n <= 9) ? RI[n] : 1.49; // Default RI for n > 9 is approximately 1.49
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AHP frame = new AHP();
            frame.setVisible(true);
        });
    }
}
