package ServerSide;

import Shared.GameServer;


import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarFile;
import java.awt.*;


public class ServerAdder extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JTextField nameField, descriptionField, discordURLField;
    private JButton submitButton;
    private JLabel dropLabel;
    private JComboBox<String> comboBox;
    private String jarPath;

    public ServerAdder() {
            super("Add Server");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new GridLayout(6, 1));

            JPanel titlePanel = new JPanel();
            titlePanel.setLayout(new FlowLayout());
            JLabel titleLabel = new JLabel("Add a Server");
            titlePanel.add(titleLabel);

            JPanel comboBoxPanel = new JPanel();
            comboBoxPanel.setLayout(new FlowLayout());
            String[] games = {"Minecraft", "Valheim"};
            comboBox = new JComboBox<>(games);
            comboBoxPanel.add(comboBox);

            nameField = new JTextField();
            descriptionField = new JTextField();
            discordURLField = new JTextField();
            dropLabel = new JLabel("Drop server jar here", SwingConstants.CENTER);
            dropLabel.setPreferredSize(new Dimension(800, 300));
            dropLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            dropLabel.setTransferHandler(new TransferHandler() {
                private static final long serialVersionUID = 1L;

                public boolean canImport(TransferHandler.TransferSupport support) {
                    return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
                }

                public boolean importData(TransferHandler.TransferSupport support) {
                    if (!canImport(support)) {
                        return false;
                    }

                    Transferable transferable = support.getTransferable();
                    try {
                        @SuppressWarnings("unchecked")
                        java.util.List<File> files = (java.util.List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                        if (files.size() != 1) {
                            return false;
                        }
                        File file = files.get(0);
                        if (!file.getName().endsWith(".jar")) {
                            return false;
                        }
                        jarPath = file.getAbsolutePath();
                        dropLabel.setText(file.getName());
                    } catch (Exception e) {
                        return false;
                    }
                    return true;
                }
            });

            submitButton = new JButton("Submit");
            submitButton.addActionListener(this);

            add(titlePanel);
            add(comboBoxPanel);
            add(new JLabel("Name:"));
            add(nameField);
            add(new JLabel("Description:"));
            add(descriptionField);
            add(new JLabel("Discord URL:"));
            add(discordURLField);
            add(dropLabel);
            add(submitButton);

            pack();
            setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        AddServer(nameField.getText(), descriptionField.getText(), discordURLField.getText(), jarPath, (String)comboBox.getSelectedItem());
    }

    public static void main(String[] args) {
        new ServerAdder();
    }

    public void AddServer(String name, String description, String discordURL, String jarPath, String game) {
        GameServer gameServer = new GameServer(name,description,discordURL);
        ServerDatabase serverDatabase = new InFileServerDatabase();
        serverDatabase.AddServer(game, gameServer, jarPath);
        // Add server code here
    }

    public static void main3(String[] args) throws IOException, InterruptedException {
        GameServer gameServer = new GameServer("test","test","test");
        //Blob jarBlob = JarToBlober.Blobify("src/main/java/ServerSide/TestJar.jar");
        ServerDatabase serverDatabase = new InFileServerDatabase();
        serverDatabase.AddServer("Minecraft", gameServer, "src/main/java/ServerSide/TestJar.jar");
    }



    public static void main2(String[] args) throws IOException, InterruptedException {
        String command = "jar cf src/main/java/ServerSide/Database/GameServers/TestJar.jar -C src/main/java/ServerSide/Database/GameServers Minecraft_TestWorld";
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        //Blob blobedjar = JarToBlober.Blobify("src/main/java/ServerSide/Database/GameServers/TestJar.jar");
        //File file = new File("src/main/java/ServerSide/Database/GameServers/TestJar.jar");
        //ProcessesJar(file);
    }


    private void RestoreFile() throws IOException {
        String jarFilePath = "src/main/java/ServerSide/Database/GameServers/TestJar.jar";
        String outputDirectoryPath = "src/main/java/ServerSide/Database/GameServers";

        Path outputDirectory = Paths.get(outputDirectoryPath);
        Files.createDirectories(outputDirectory);

        JarFile jarFile = new JarFile(jarFilePath);
        jarFile.stream().forEach(entry -> {
            try {
                Path entryPath = outputDirectory.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.copy(jarFile.getInputStream(entry), entryPath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }




}
