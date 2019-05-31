package da.gammla;

import da.gammla.anchored_table.AnchoredTable;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.*;

public class Main {
    public static void main(String[] args){

        AccountsCluster accounts = new AccountsCluster();

        AnchoredTable settings = null;
        try {
            settings = new AnchoredTable(new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/DaGammla/AccountManager/settings.xml"));
        } catch (Exception e) {
            e.printStackTrace();
            settings = new AnchoredTable("Settings");
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            if (settings.hasAnchor("skin")) {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if (settings.getData("skin").equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        File file = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/DaGammla/AccountManager/save.dams");
        if (file.exists()) {

            if (settings.hasAnchor("pass_p")){
                boolean wrong_password = true;
                while (wrong_password){
                    String answer = getPassword();
                    if (answer == null)
                        System.exit(0);

                    if (answer.equals(settings.getData("pass_p"))) {
                        wrong_password = false;
                    } else {
                        JFrame frame1 = optionPaneFrame();
                        if (JOptionPane.showConfirmDialog(frame1, "Wrong Password!", "Da Accounts Manager", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE) == JOptionPane.CLOSED_OPTION) {
                            System.exit(0);
                        }
                        frame1.dispose();
                    }


                }
            }
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                accounts = (AccountsCluster) in.readObject();
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            File file_enc = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/DaGammla/AccountManager/enc_save.des");
            if (file_enc.exists()) {
                boolean wrong_password = true;
                while (wrong_password) {
                    try {
                        String answer = getPassword();
                        if (answer == null)
                            System.exit(0);
                        byte[] bytes = Encryptor.decryptFile(file_enc.getAbsolutePath(), answer);

                        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                        ObjectInput in = null;
                        try {
                            in = new ObjectInputStream(bis);
                            accounts = (AccountsCluster) in.readObject();
                            in.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        wrong_password = false;
                    } catch (Exception e) {
                        wrong_password = true;
                        JFrame frame = optionPaneFrame();
                        if (JOptionPane.showConfirmDialog(frame, "Wrong Password!", "Da Accounts Manager", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE) == JOptionPane.CLOSED_OPTION) {
                            System.exit(0);
                        }
                        frame.dispose();
                    }
                }
            }
        }

        Gui gui = new Gui(accounts, settings);
    }


    static JFrame optionPaneFrame(){
        JFrame frame = new JFrame("Da Accounts Manager");
        frame.setLocationRelativeTo(null);
        frame.setUndecorated(true);
        frame.setVisible(true);
        return frame;
    }

    static String getPassword(){
        JFrame frame = optionPaneFrame();
        JPasswordField pass = new JPasswordField(){
            @Override
            public void addNotify() {
                super.addNotify();
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Thread.sleep(50);
                            requestFocusInWindow();
                            Thread.sleep(100);
                            requestFocusInWindow();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                };
                thread.start();
            }
        };
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel label = new JLabel("Enter your password:");
        panel.add(label);
        panel.add(pass);
        int option = JOptionPane.showConfirmDialog(frame, panel, "Da Accounts Manager", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(option == JOptionPane.OK_OPTION) {
            String password = new String(pass.getPassword());
            frame.dispose();
            return password;
        }
        return null;
    }
}
