package com.acl.updater.gui;

import com.acl.updater.UpdateAllFiles;
import com.acl.updater.file.MetadataFileUpdater;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Properties;

/**
 * Main windows of the program
 */
public class MainWindows {

    private static final Logger LOGGER = LogManager.getLogger(MetadataFileUpdater.class);
    private static final String FILEPATH_TO_PROPERTIES = ".." + File.separator + "config" + File.separator + "gui.properties";

    private JFrame mainFrame;

    private JButton fileChooserButton;

    private JButton executeUpdate;

    private JButton exitButton;

    private JFileChooser fileChooser;

    private JTextField filePath;

    private JCheckBox backupCheckBox;

    private Properties properties;

    public MainWindows() {
        LOGGER.info("STARTING");
        initButton();
        loadProperties();
        initFrame();
    }

    private void initFrame() {
        JPanel panel = new JPanel();

        GridLayout gridLayout = new GridLayout(5,1);
        gridLayout.setHgap(20);
        gridLayout.setVgap(20);

        panel.setLayout(gridLayout);
        panel.add(fileChooserButton);
        panel.add(filePath);
        panel.add(backupCheckBox);
        panel.add(executeUpdate);
        panel.add(exitButton);

        mainFrame.add(panel);
        mainFrame.setSize(400, 270);
        mainFrame.setResizable(false);
        mainFrame.setVisible(true);

        mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    private void initButton() {
        mainFrame = new JFrame("Update Calibre library");

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        fileChooserButton = new JButton("Choose Directory");
        executeUpdate = new JButton("Start Conversion");
        exitButton = new JButton("Exit");
        backupCheckBox = new JCheckBox("Backup Directory");

        filePath = new JTextField("");

        fileChooserButton.setVisible(true);
        executeUpdate.setVisible(true);
        filePath.setVisible(true);
        exitButton.setVisible(true);
        backupCheckBox.setVisible(true);

        addActionListener();
    }

    private void addActionListener() {
        executeUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UpdateAllFiles updateAllFiles = new UpdateAllFiles(filePath.getText(), backupCheckBox.isSelected());
                updateAllFiles.update();

                JOptionPane.showInternalMessageDialog(mainFrame.getContentPane(), "Conversion finished",

                        "information", JOptionPane.INFORMATION_MESSAGE);
                LOGGER.info("FINISHED");
            }
        });

        fileChooserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showOpenDialog(mainFrame);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String selectedPath = fileChooser.getSelectedFile().getAbsolutePath();

                    LOGGER.info("Selected path: {}", selectedPath);
                    filePath.setText(selectedPath);
                    properties.setProperty("filePath", selectedPath);

                    updatePropertiesFile();

                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });
    }

    private void loadProperties() {
        properties = new Properties();
        try  {
            properties.load(new FileInputStream(FILEPATH_TO_PROPERTIES));

            filePath.setText(properties.getProperty("filePath"));
        } catch (IOException e) {
            LOGGER.error("Property file not found, ", e);
        }
    }

    private void updatePropertiesFile() {
        try (OutputStream output = new FileOutputStream(FILEPATH_TO_PROPERTIES)) {

            // save properties to project root folder
            properties.store(output, null);

        } catch (IOException ex) {
            LOGGER.error("Impossible to save data", ex);
        }
    }
    public static void main(String[] args) {
        new MainWindows();
    }
}
