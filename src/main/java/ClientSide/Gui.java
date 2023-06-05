package ClientSide;

import Shared.Game;
import Shared.GameServer;
import Shared.Responses.ActiveServerResponse;


import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;

public class Gui extends JFrame implements ActionListener, GameListPresenter.View {
    JButton launchButton, stopButton;
    JPanel gameSelection, serverSelection, serverDetails, activeServerInfo;
    GameListPresenter presenter;

    public Gui() {
        presenter = new GameListPresenter(this);
        setLayout(new GridLayout(2, 2));
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SetGameSelection();

        serverDetails = new JPanel();
        serverDetails.setLayout(new FlowLayout());
        serverDetails.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(serverDetails);

        serverSelection = new JPanel();
        serverSelection.setLayout(new FlowLayout());
        serverSelection.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(serverSelection);

        activeServerInfo = new JPanel();
        activeServerInfo.setLayout(new FlowLayout());
        activeServerInfo.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(activeServerInfo);
        SetActiveServerInfo();
    }

    public void SetActiveServerInfo(){
        activeServerInfo.removeAll();
        validate();
        repaint();
        presenter.PopulateActiveServerInfo();
        validate();
        repaint();
    }

    public void PopulateActiveServerInfo(ActiveServerResponse response){
        if(response.isActive()){
            activeServerInfo.setLayout(new GridLayout(6, 1));

            JLabel titleLabel = new JLabel("Active Server");
            titleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            titleLabel.setPreferredSize(new Dimension(230, 15));
            activeServerInfo.add(titleLabel);

            System.out.println(response.getGameName());
            JLabel gameTitle = new JLabel(response.getGameName());
            gameTitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            gameTitle.setPreferredSize(new Dimension(230, 15));
            activeServerInfo.add(gameTitle);

            JLabel serverName = new JLabel(response.getServerName());
            serverName.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            serverName.setPreferredSize(new Dimension(230, 15));
            activeServerInfo.add(serverName);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            JLabel startTime = new JLabel("Launched at: " + sdf.format(response.getTs()));
            startTime.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            startTime.setPreferredSize(new Dimension(230, 15));
            activeServerInfo.add(startTime);

            JLabel ip = new JLabel("IP Address: " + response.getIp());
            ip.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            ip.setPreferredSize(new Dimension(230, 15));
            activeServerInfo.add(ip);

            // create the launch button
            stopButton = new JButton("Stop");
            stopButton.addActionListener(this);
            stopButton.setPreferredSize(new Dimension(230, 30));
            activeServerInfo.add(stopButton, BorderLayout.SOUTH);
        }else{
            JLabel titleLabel;
            titleLabel = new JLabel("No Active Server");
            titleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            activeServerInfo.add(titleLabel);
        }
    }

    private void SetGameSelection(){
        gameSelection = new JPanel();
        gameSelection.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        gameSelection.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //panel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel;

        titleLabel = new JLabel("Games");
        //titleLabel.setPreferredSize(new Dimension(panel1.getWidth(), 50));
        titleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        gameSelection.add(titleLabel);
        presenter.PopulateGameList();
        add(gameSelection);
    }

    @Override
    public void PopulateGameSelection(List<Game> games) {
        JPanel listPanel;
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setPreferredSize(new Dimension(250, 200));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        gameSelection.add(scrollPane);

        // Add click listeners to each game label
        for (Game game : games) {
            JLabel gameLabel = new JLabel(game.getTitle());
            gameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            gameLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    presenter.PopulateServerSelection(game);
                }
            });
            listPanel.add(gameLabel);
        }
    }

    public void PopulateServerSelection(List<GameServer> gameServers){
        serverSelection.removeAll();
        serverDetails.removeAll();
        validate();
        repaint();

        JLabel titleLabel;
        JPanel listPanel;

        titleLabel = new JLabel("Servers");
        titleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        serverSelection.add(titleLabel);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setPreferredSize(new Dimension(250, 200));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        serverSelection.add(scrollPane);

        // Add click listeners to each game label
        for (GameServer gameServer : gameServers) {
            JLabel gameLabel = new JLabel(gameServer.getName());
            gameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            gameLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    presenter.PopulateServerDetails(gameServer);
                    System.out.println(gameServer.getName());
                }
            });
            listPanel.add(gameLabel);
        }
        add(serverSelection);
        validate();
        repaint();
    }

    public void PopulateServerDetails(GameServer gameServer) {
        serverDetails.removeAll();
        validate();
        repaint();
        JTextArea descriptionTextArea;

        // create the clickable link for the discord URL
        JLabel discordLink = new JLabel("<html><u>" + "Server Discord Link:</u><br><u>"+ gameServer.getDiscordURL() + "</u></html>");
        discordLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        discordLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    Desktop.getDesktop().browse(new URI(gameServer.getDiscordURL()));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });

        JPanel discordLinkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        discordLinkPanel.add(discordLink);
        serverDetails.add(discordLinkPanel, BorderLayout.NORTH);

        // create the description text area
        descriptionTextArea = new JTextArea(gameServer.getDescription());
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.setPreferredSize(new Dimension(230, 100));
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionTextArea);
        serverDetails.add(descriptionScrollPane, BorderLayout.CENTER);

        // create the launch button
        launchButton = new JButton("Launch");
        launchButton.addActionListener(this);
        serverDetails.add(launchButton, BorderLayout.SOUTH);

        validate();
        repaint();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == stopButton) {
            presenter.StopServer();
        }if (e.getSource() == launchButton){
            presenter.StartServer();
        }
    }

    public static void main(String[] args) {
        Gui gui = new Gui();
        gui.setVisible(true);
    }
}
