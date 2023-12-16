package javamusicplayerjframegui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class JavaGUIMusicPlayerJFrame extends JFrame implements ActionListener {

    private JTextField filePathField;
    private JButton playButton;
    private JButton pauseButton;
    private JButton chooseButton;
    private JButton loopButton;
    private JButton volumeButton;
    private JSlider volumeSlider;
    private boolean isPaused;
    private boolean isLooping = false;
    private JFileChooser fileChooser;
    private AdvancedPlayer player;
    private Thread playerThread;

    public JavaGUIMusicPlayerJFrame() {
        super("Music Player");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        filePathField = new JTextField(25);
        playButton = new JButton("Play");
        pauseButton = new JButton("Pause");
        chooseButton = new JButton("Choose File");
        loopButton = new JButton("Loop");
        volumeButton = new JButton("Change Volume");
        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        isPaused = false;
        isLooping = false;

        playButton.addActionListener(this);
        pauseButton.addActionListener(this);
        chooseButton.addActionListener(this);
        loopButton.addActionListener(this);
        volumeButton.addActionListener(this);

        // Add change listener for volume slider
        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (player != null) {
                    float volume = volumeSlider.getValue() / 100.0f;
                    player.setVolume(volume);
                }
            }
        });

        add(filePathField);
        add(chooseButton);
        add(playButton);
        add(pauseButton);
        add(loopButton);
        add(volumeButton);
        add(volumeSlider);

        fileChooser = new JFileChooser(".");
        fileChooser.setFileFilter(new FileNameExtensionFilter("MP3 Files", "mp3"));

        setSize(500, 150);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == playButton) {
            playMusic();
        } else if (event.getSource() == pauseButton) {
            pauseMusic();
        } else if (event.getSource() == chooseButton) {
            chooseFile();
        } else if (event.getSource() == loopButton) {
            toggleLoop();
        } else if (event.getSource() == volumeButton) {
            // Handle volume change button (optional)
            // You can add specific functionality here if needed
        }
    }

    private void playMusic() {
        if (player != null && playerThread != null && playerThread.isAlive()) {
            player.close();
            playerThread.interrupt();
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(filePathField.getText());
            player = new AdvancedPlayer(fileInputStream);
            playerThread = new Thread(() -> {
                try {
                    player.setPlayBackListener(new PlaybackListener() {
                        @Override
                        public void playbackFinished(PlaybackEvent evt) {
                            if (!isLooping) {
                                SwingUtilities.invokeLater(() -> {
                                    playButton.setEnabled(true);
                                    pauseButton.setEnabled(false);
                                    loopButton.setEnabled(true);
                                });
                            }
                        }
                    });
                    player.play();
                } catch (JavaLayerException e) {
                    e.printStackTrace();
                }
            });
            playerThread.start();

            playButton.setEnabled(false);
            pauseButton.setEnabled(true);
            loopButton.setEnabled(false);
            volumeButton.setEnabled(true);

            if (isLooping) {
                loopButton.setEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pauseMusic() {
        if (player != null) {
            player.close();
        }

        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
        loopButton.setEnabled(true);
        volumeButton.setEnabled(false);
    }

    private void chooseFile() {
        fileChooser.setCurrentDirectory(new File("."));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void toggleLoop() {
        isLooping = !isLooping;
        if (isLooping) {
            loopButton.setText("Stop Loop");
        } else {
            loopButton.setText("Loop");
        }
    }

    public static void main(String[] args) {
        new JavaGUIMusicPlayerJFrame();
    }
}
