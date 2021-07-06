package allthepairs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Yan David
 */
public final class AllthePairs extends javax.swing.JFrame {

    int turned, count, minutes, seconds;
    Timer timer, tclock;
    JPanel panelContent;
    String status;
    JLabel clockLabel;

    /**
     * Creates new form AllthePairs
     */
    public AllthePairs() {
        turned = 0;
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        initComponents();
        panelContent = new JPanel();
        createTools();
        status = "STARTED";
    }

    public void initGame(int size) {
        turned = 0;
        createDashboard(size);
        if (timer != null) {
            if (timer.isRunning()) {
                timer.stop();
            }
        }
        status = "STARTED";
        clockLabel.setText("Here the time");
        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public void createTools() {

        panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.Y_AXIS));
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEADING));
        panelTop.setSize(new Dimension(this.WIDTH, (int) (this.HEIGHT * 0.2)));
        JButton startButton = new JButton("Start game");
        clockLabel = new JLabel("Here the time");
        startButton.addActionListener((ActionEvent e) -> {
            initGame(4);
        });
        panelTop.add(startButton);
        panelTop.add(clockLabel);
        panelContent.add(panelTop);
        setContentPane(panelContent);
    }

    public void startTimer() {
        minutes = 0;
        seconds = 0;
        ActionListener timerListener = (ActionEvent evt) -> {
            if (this.status.equals("FINISHED")) {
                tclock.stop();
            }
            clockLabel.setText((minutes > 9 ? minutes : "0" + minutes) + ":" + (seconds > 9 ? seconds : "0" + seconds));
            if (seconds == 59) {
                minutes++;
                seconds = 0;
            } else {
                seconds++;
            }
        };
        int timerDelay = 1000;
        tclock = new Timer(timerDelay, timerListener);

        tclock.start();
    }

    public void createDashboard(int size) {

        JPanel panelGrid = new JPanel(new GridLayout(size, size));
        panelGrid.setBackground(Color.ORANGE);

        File files[] = null;
        try {
            files = new File(getClass().getResource("/allthepairs/images/").toURI()).listFiles();
        } catch (URISyntaxException ex) {
            Logger.getLogger(AllthePairs.class.getName()).log(Level.SEVERE, null, ex);
        }
        ArrayList<File> filesList = null;
        if (files.length > 0) {
            int s = 1;
            filesList = new ArrayList<>();
            for (File file : files) {
                filesList.add(file);
                if (Math.pow(size, 2) / 2 == s) {
                    break;
                }
                s++;
            }
            s = 1;
            for (File file : files) {
                filesList.add(file);
                if (Math.pow(size, 2) / 2 == s) {
                    break;
                }
                s++;
            }
            filesList = randomList(filesList);
        }
        Object[][] objects = new Object[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                JButton button = new JButton();
                button.setLayout(new BorderLayout());
                button.setBackground(Color.DARK_GRAY);
                Object[] obj = new Object[4];
                obj[0] = button;
                obj[1] = new ImageIcon(getClass().getResource("/allthepairs/images/" + filesList.remove(new Random().nextInt(filesList.size())).getName()));
                obj[2] = false; //paired
                obj[3] = false; //turned
                objects[i][j] = obj;
                button.addActionListener((ActionEvent e) -> {
                    if (this.status.equals("STARTED")) {
                        startTimer();
                        this.status = "RUNNING";
                    }
                    if ((boolean) obj[2]) {
                        return;
                    }
                    if (turned == 2) {
                        turned = 0;

                        for (Object[] object : objects) {
                            for (int l = 0; l < objects[0].length; l++) {
                                Object[] o = (Object[]) object[l];
                                o[3] = false;
                                if ((boolean) o[2] != true) {
                                    ((JButton) o[0]).setIcon(null);
                                }
                            }
                        }
                    }
                    obj[3] = true;
                    turned++;
                    button.setIcon((Icon) obj[1]);

                    int turnedOnes = 0;
                    int first[] = new int[2], second[] = new int[2];
                    for (int k = 0; k < objects.length; k++) {
                        for (int l = 0; l < objects[0].length; l++) {
                            Object[] o = ((Object[]) objects[k][l]);
                            if (!(boolean) o[2] && (boolean) o[3]) {
                                turnedOnes++;
                                if (turnedOnes == 1) {
                                    first[0] = k;
                                    first[1] = l;
                                } else if (turnedOnes == 2) {
                                    second[0] = k;
                                    second[1] = l;
                                    if (((JButton) ((Object[]) objects[first[0]][first[1]])[0]).getIcon().toString().equals(((JButton) ((Object[]) objects[second[0]][second[1]])[0]).getIcon().toString())) {
                                        ((Object[]) objects[first[0]][first[1]])[2] = true;
                                        ((Object[]) objects[second[0]][second[1]])[2] = true;
                                        ((JButton) ((Object[]) objects[first[0]][first[1]])[0]).setBackground(Color.GREEN);
                                        ((JButton) ((Object[]) objects[second[0]][second[1]])[0]).setBackground(Color.GREEN);
                                    }
                                }
                            }
                        }

                    }
                    if (checkWin(objects)) {
                        this.status = "FINISHED";
                        makeWin(objects);
                    }
                });
                panelGrid.add(button);
            }
        }
        if(panelContent.getComponentCount() == 2){
            panelContent.remove(1);
        }
        panelContent.add(panelGrid);
    }

    public boolean checkWin(Object[][] objects) {
        for (Object[] object : objects) {
            for (int l = 0; l < objects[0].length; l++) {
                Object[] o = (Object[]) object[l];
                if ((boolean) o[2] != true) {
                    return false;
                }
            }
        }
        return true;
    }

    public void makeWin(Object[][] objects) {
        count = 0;
        String labelTime = clockLabel.getText();
        ActionListener timerListener = (ActionEvent evt) -> {
            for (Object[] object : objects) {
                for (int l = 0; l < objects[0].length; l++) {
                    Object[] o = (Object[]) object[l];
                    if (count % 2 == 0) {
                        ((JButton) o[0]).setBackground(Color.red);
                    } else {
                        ((JButton) o[0]).setBackground(Color.yellow);
                    }
                }
            }
            if (count % 2 != 0) {
                clockLabel.setText("");
            } else {
                clockLabel.setText(labelTime);
            }
            if (count == 10) {
                showOptionsAfterFinished();
                timer.stop();
            }
            count++;
        };
        int timerDelay = 350; // or whatever length of time needed
        timer = new Timer(timerDelay, timerListener);

        timer.start();
    }

    public void showOptionsAfterFinished() {
        String options[] = {"Volver a jugar", "Cerrar"};
        int optionSelected = JOptionPane.showOptionDialog(this, "Qué deseas hacer?", "Juego terminado", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, JOptionPane.ABORT);
        if (optionSelected == 0) {
            String optionsSize[] = {"4", "6"};
            int size = JOptionPane.showOptionDialog(this, "Indique el tamaño del tablero", "Tamaño del tablero", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, optionsSize, JOptionPane.ABORT);
            switch (size) {
                case 0:
                    initGame(4);
                    break;
                case 1:
                    initGame(6);
                    break;
                default:
                    initGame(6);
            }
        } else {
            this.dispose();
            System.exit(0);
        }
    }

    public ArrayList<File> randomList(ArrayList<File> files) {
        ArrayList<File> filesList = new ArrayList<>(files.size());
        while (!files.isEmpty()) {
            filesList.add(files.remove(new Random().nextInt(files.size())));
        }
        return filesList;
    }

    public void printList(ArrayList<File> files) {
        files.forEach(file -> {
            System.out.print(file.getName() + "  ");
        });
        System.out.println("");
        System.out.println("");
    }

    /**
     *
     * /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Alll the pairs");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AllthePairs.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AllthePairs.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AllthePairs.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AllthePairs.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AllthePairs().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
