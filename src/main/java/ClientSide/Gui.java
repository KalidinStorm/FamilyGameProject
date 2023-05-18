package ClientSide;

import Shared.Game;
import Shared.GameServer;
import Shared.JsonSerializer;
import Shared.Requests.StartServerRequest;
import Shared.Responses.ActiveServerResponse;
import Shared.Responses.GetGameListResponse;
import Shared.Responses.StartServerResponse;
import Shared.Responses.StopServerResponse;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;

public class Gui extends JFrame implements ActionListener {
    private final ServerConnector sc = new ServerConnector(new JsonSerializer());
    JButton button1, button2, button3, button4;
    JButton launchButton, stopButton;
    JPanel panel1, panel2, panel3, panel4;
    Game game;
    GameServer gameServer;

    public Gui() {
        setLayout(new GridLayout(2, 2));
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SetPanel1();

        panel3 = new JPanel();
        panel3.setLayout(new FlowLayout());
        panel3.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(panel3);

        panel2 = new JPanel();
        panel2.setLayout(new FlowLayout());
        panel2.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(panel2);


        panel4 = new JPanel();
        panel4.setLayout(new FlowLayout());
        panel4.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(panel4);
        SetPanel4();

    }

    private void SetPanel4(){
        panel4.removeAll();
        validate();
        repaint();
        try {
            ActiveServerResponse response = sc.doPost("/GetActiveServer",null,null, ActiveServerResponse.class);
            if(response.isActive()){
                panel4.setLayout(new GridLayout(6, 1));

                JLabel titleLabel = new JLabel("Active Server");
                titleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
                titleLabel.setPreferredSize(new Dimension(230, 15));
                panel4.add(titleLabel);

                System.out.println(response.getGameName());
                JLabel gameTitle = new JLabel(response.getGameName());
                gameTitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);
                gameTitle.setPreferredSize(new Dimension(230, 15));
                panel4.add(gameTitle);

                JLabel serverName = new JLabel(response.getServerName());
                serverName.setAlignmentX(JLabel.CENTER_ALIGNMENT);
                serverName.setPreferredSize(new Dimension(230, 15));
                panel4.add(serverName);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                JLabel startTime = new JLabel("Launched at: " + sdf.format(response.getTs()));
                startTime.setAlignmentX(JLabel.CENTER_ALIGNMENT);
                startTime.setPreferredSize(new Dimension(230, 15));
                panel4.add(startTime);

                JLabel ip = new JLabel("IP Address: " + response.getIp());
                ip.setAlignmentX(JLabel.CENTER_ALIGNMENT);
                ip.setPreferredSize(new Dimension(230, 15));
                panel4.add(ip);

                // create the launch button
                stopButton = new JButton("Stop");
                stopButton.addActionListener(this);
                stopButton.setPreferredSize(new Dimension(230, 30));
                panel4.add(stopButton, BorderLayout.SOUTH);
            }else{
                JLabel titleLabel;
                titleLabel = new JLabel("No Active Server");
                titleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
                panel4.add(titleLabel);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        validate();
        repaint();
    }

    private void SetPanel1(){
        panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //panel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        List<Game> games;
        JLabel titleLabel;
        JPanel listPanel;
        try {
            games = sc.doPost("/GetGameList",null,null, GetGameListResponse.class).getGameList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        titleLabel = new JLabel("Games");
        //titleLabel.setPreferredSize(new Dimension(panel1.getWidth(), 50));
        titleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        panel1.add(titleLabel);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setPreferredSize(new Dimension(250, 200));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel1.add(scrollPane);

        // Add click listeners to each game label
        for (Game game : games) {
            JLabel gameLabel = new JLabel(game.getTitle());
            gameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            gameLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    SetPanel2(game);
                    //changeGameWindow(game);
                }
            });
            listPanel.add(gameLabel);
        }
        add(panel1);
    }

    private void SetPanel2(Game game){
        this.game = game;
        panel2.removeAll();
        panel3.removeAll();
        validate();
        repaint();


        JLabel titleLabel;
        JPanel listPanel;

        List<GameServer> gameServers = Arrays.asList(game.getServers());

        titleLabel = new JLabel("Servers");
        //titleLabel.setPreferredSize(new Dimension(panel1.getWidth(), 50));
        titleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        panel2.add(titleLabel);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setPreferredSize(new Dimension(250, 200));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel2.add(scrollPane);

        // Add click listeners to each game label
        for (GameServer gameServer : gameServers) {
            JLabel gameLabel = new JLabel(gameServer.getName());
            gameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            gameLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    SetPanel3(gameServer);
                    System.out.println(gameServer.getName());
                    //changeGameWindow(game);
                }
            });
            listPanel.add(gameLabel);
        }
        add(panel2);
        validate();
        repaint();
    }

    private void SetPanel3(GameServer gameServer) {
        this.gameServer = gameServer;
        panel3.removeAll();
        validate();
        repaint();

        JTextArea descriptionTextArea;


        // create the clickable link for the discord URL
        JLabel discordLink = new JLabel("<html><u>" + "Server Discord Link:</u><br><u>"+ gameServer.getDiscordURL() + "</u></html>");
        //discordLink.setFont(new Font("Arial", Font.PLAIN, 18));
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
        panel3.add(discordLinkPanel, BorderLayout.NORTH);

        // create the description text area
        descriptionTextArea = new JTextArea(gameServer.getDescription());
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.setPreferredSize(new Dimension(230, 100));
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionTextArea);
        panel3.add(descriptionScrollPane, BorderLayout.CENTER);

        // create the launch button
        launchButton = new JButton("Launch");
        launchButton.addActionListener(this);
        panel3.add(launchButton, BorderLayout.SOUTH);

        validate();
        repaint();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == stopButton) {
            try {
                sc.doPost("/StopServer",null,null,StopServerResponse.class);
                SetPanel4();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }if (e.getSource() == launchButton){
            try {
                StartServerResponse response = sc.doPost("/StartServer",new StartServerRequest(game,gameServer),null, StartServerResponse.class);
                SetPanel4();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static void main(String[] args) {
        Gui gui = new Gui();
        gui.setVisible(true);
    }
}
