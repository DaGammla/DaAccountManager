package da.gammla;

import da.gammla.anchored_table.AnchoredTable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.util.Random;

public class Gui extends JFrame {
    private JPanel panel;
    private JTabbedPane tabbedPane1;
    private JTextField platform_field;
    private JTextField email_field;
    private JTextField username_field;
    private JTextField password_field;
    private JButton edit_button;
    private JButton delete_button;
    private JComboBox selection_box;
    private JButton copy_platform;
    private JButton copy_email;
    private JButton copy_username;
    private JButton copy_password;
    private JButton add_account_button;
    private JSpinner pasword_length_spinner;
    private JCheckBox lower_case_letters_box;
    private JCheckBox capital_letters_box;
    private JCheckBox digits_box;
    private JCheckBox punctuations_box;
    private JTextArea additional_pool_area;
    private JButton generate_button;
    private JTextField random_password_field;
    private JButton save_password_button;
    private JCheckBox use_password_box;
    private JTextField options_password_field;
    private JCheckBox encrypt_save_box;
    private JComboBox skin_box;

    private boolean editing = false;

    AccountsCluster accounts;
    AnchoredTable settings;

    Gui self = this;

    Gui(AccountsCluster accounts, AnchoredTable settings){
        super("Da Account Manager");
        this.accounts = accounts;
        this.settings = settings;


        applyAccountsToSelectionBox();
        skin_box.addItem("Default");
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            skin_box.addItem(info.getName());
        }
        loadFromSettings();

        selection_box.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selection_box.getSelectedIndex() <= 0){
                    platform_field.setText("");
                    email_field.setText("");
                    username_field.setText("");
                    password_field.setText("");
                    edit_button.setEnabled(editing);
                    delete_button.setEnabled(editing);
                } else {
                    Account acc = accounts.get(selection_box.getSelectedIndex() - 1);
                    platform_field.setText(acc.getPlatform());
                    email_field.setText(acc.getEmail());
                    username_field.setText(acc.getUsername());
                    password_field.setText(acc.getPassword());
                    edit_button.setEnabled(true);
                    delete_button.setEnabled(true);
                }
            }
        });

        edit_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!editing) {
                    startEditing();
                } else {
                    int select_index = selection_box.getSelectedIndex();
                    if (select_index > 0) {
                        accounts.set(selection_box.getSelectedIndex() - 1,
                                new Account(platform_field.getText(), email_field.getText(), username_field.getText(), password_field.getText()));
                        applyAccountsToSelectionBox();
                        endEditing();
                        selection_box.setSelectedIndex(select_index);
                    } else {
                        accounts.add(new Account(platform_field.getText(), email_field.getText(), username_field.getText(), password_field.getText()));
                        applyAccountsToSelectionBox();
                        endEditing();
                        selection_box.setSelectedIndex(accounts.size());
                    }

                    saveAccounts();
                }
            }
        });

        delete_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!editing){
                    int answer = JOptionPane.showConfirmDialog(self, "Are you sure you want to DELETE" + System.lineSeparator() + "this account from the database?", "Delete Account", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (answer == JOptionPane.YES_OPTION){
                        accounts.remove(selection_box.getSelectedIndex() - 1);
                        applyAccountsToSelectionBox();
                        saveAccounts();
                    }
                } else {
                    int select_index = selection_box.getSelectedIndex();
                    endEditing();
                    applyAccountsToSelectionBox();
                    selection_box.setSelectedIndex(select_index);
                }
            }
        });

        add_account_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startEditing();
                selection_box.setSelectedIndex(0);
                edit_button.setEnabled(true);
                delete_button.setEnabled(true);
            }
        });

        copy_platform.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection stringSelection = new StringSelection(platform_field.getText());
                clipboard.setContents(stringSelection, null);
            }
        });
        copy_email.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection stringSelection = new StringSelection(email_field.getText());
                clipboard.setContents(stringSelection, null);
            }
        });
        copy_username.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection stringSelection = new StringSelection(username_field.getText());
                clipboard.setContents(stringSelection, null);
            }
        });
        copy_password.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection stringSelection = new StringSelection(password_field.getText());
                clipboard.setContents(stringSelection, null);
            }
        });

        generate_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                StringBuilder characters = new StringBuilder(additional_pool_area.getText());

                if(lower_case_letters_box.isSelected())
                    characters.append("abcdefghijklmnopqrstuvwxyz");

                if (capital_letters_box.isSelected())
                    characters.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ");

                if (digits_box.isSelected())
                    characters.append("0123456789");

                if(punctuations_box.isSelected())
                    characters.append("!@#$%&*()_+-=[]|,.;:/?><");

                Random rnd = new Random();

                int length = (int) pasword_length_spinner.getValue();

                StringBuilder password = new StringBuilder();

                for (int i = 0; i < length; i++)
                    password.append(characters.toString().toCharArray()[rnd.nextInt(characters.length())]);

                random_password_field.setText(password.toString());

            }
        });

        save_password_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editing) {
                    JOptionPane.showConfirmDialog(self, "Can't save this password as there" + System.lineSeparator() + "are still unsaved changes left" + System.lineSeparator() + "on the \"Accounts\" tab", "Unsaved Changes", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    return;
                } else {
                    add_account_button.doClick();
                    password_field.setText(random_password_field.getText());
                    tabbedPane1.setSelectedIndex(0);
                }
            }
        });


        skin_box.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setLookAndFeel();
                saveSettings();
            }
        });

        use_password_box.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSettings();

                encrypt_save_box.setEnabled(use_password_box.isSelected());
                options_password_field.setEnabled(use_password_box.isSelected());

            }
        });
        encrypt_save_box.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (encrypt_save_box.isSelected()) {
                    int answer = JOptionPane.showConfirmDialog(self, "Encryption your Accounts might be dangerous!" + System.lineSeparator() + "The data might then be secured from hackers but is also" + System.lineSeparator() + "impossible to be restored once you lose your password." + System.lineSeparator() + "Do you still want to encrypt your save file?", "Da Account Manager", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (answer != JOptionPane.YES_OPTION)
                        encrypt_save_box.setSelected(false);
                }

                saveSettings();
            }
        });
        options_password_field.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                saveSettings();
            }
            public void removeUpdate(DocumentEvent e) {
                saveSettings();
            }
            public void insertUpdate(DocumentEvent e) {
                saveSettings();
            }
        });

        setContentPane(panel);
        pack();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                if (editing){
                    int answer = JOptionPane.showConfirmDialog(self, "There are unsaved changes!" + System.lineSeparator() + "Do you wish to save them?", "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (answer == JOptionPane.YES_OPTION) {
                        edit_button.doClick();
                    } else if (answer == JOptionPane.NO_OPTION){
                        System.exit(0);
                    } else if (answer == JOptionPane.CANCEL_OPTION || answer == JOptionPane.CLOSED_OPTION){
                        return;
                    }
                }

                System.exit(0);
            }
        };

        addWindowListener(exitListener);

        setLocationRelativeTo(null);

        setDefaultLookAndFeelDecorated(true);
        setResizable(false);


        setVisible(true);

        pack();
    }

    void saveAccounts(){
        File file = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/DaGammla/AccountManager/save.dams");
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(accounts);
            out.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        if (encrypt_save_box.isSelected()){
            try {
                encryptAccounts();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void encryptAccounts() {
        try {
            Encryptor.encryptFile(
                    FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/DaGammla/AccountManager/save.dams",
                    FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/DaGammla/AccountManager/enc_save.des",
                    options_password_field.getText());
            File file = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/DaGammla/AccountManager/save.dams");
            if (file.exists()) {
                Files.delete(file.toPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    void startEditing(){
        editing = true;
        platform_field.setEditable(true);
        email_field.setEditable(true);
        username_field.setEditable(true);
        password_field.setEditable(true);
        selection_box.setEnabled(false);
        add_account_button.setEnabled(false);
        edit_button.setText("Save");
        edit_button.setBackground(Color.GREEN);
        delete_button.setText("Cancel");
        delete_button.setBackground(Color.RED);
    }

    void endEditing(){
        editing = false;
        platform_field.setEditable(false);
        email_field.setEditable(false);
        username_field.setEditable(false);
        password_field.setEditable(false);
        selection_box.setEnabled(true);
        add_account_button.setEnabled(true);
        edit_button.setText("Edit");
        edit_button.setBackground(panel.getBackground());
        delete_button.setText("Delete");
        delete_button.setBackground(panel.getBackground());
    }


    void applyAccountsToSelectionBox(){
        selection_box.removeAllItems();
        selection_box.addItem("*");
        for (int i = 0; i < accounts.size(); i++) {

            if (!accounts.get(i).getUsername().trim().equals("")) {
                selection_box.addItem(accounts.get(i).getPlatform() + " - " + accounts.get(i).getUsername());
            } else if (!accounts.get(i).getEmail().trim().equals("")) {
                selection_box.addItem(accounts.get(i).getPlatform() + " - " + accounts.get(i).getEmail());
            } else {
                int same_platform = 0;

                for (int j = 0; j < selection_box.getItemCount() - 1; j++) {
                    if (accounts.get(j).getPlatform().trim().toLowerCase().equals(accounts.get(i).getPlatform().trim().toLowerCase())){
                        same_platform++;
                    }
                }

                selection_box.addItem(accounts.get(i).getPlatform() + (same_platform > 0 ? " (" + same_platform + ")" : ""));
            }
        }
    }

    void setLookAndFeel(){
        if (skin_box.getSelectedIndex() == 0) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                UIManager.setLookAndFeel(UIManager.getInstalledLookAndFeels()[skin_box.getSelectedIndex() - 1].getClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        SwingUtilities.updateComponentTreeUI(this);
        pack();
    }

    void saveSettings(){
        settings.setData("skin", (String) skin_box.getSelectedItem());
        settings.setData("password", use_password_box.isSelected() ? "true" : "false");
        settings.setData("encrypted", encrypt_save_box.isSelected() ? "true" : "false");
        try {

            if (use_password_box.isSelected() && encrypt_save_box.isSelected() || !use_password_box.isSelected()) {
                settings.removeData("pass_p");
            }
            if (!encrypt_save_box.isSelected() || !encrypt_save_box.isEnabled()){
                File file = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/DaGammla/AccountManager/enc_save.des");
                if (file.exists()) {
                    Files.delete(file.toPath());
                }
            }
            if (use_password_box.isSelected() && !encrypt_save_box.isSelected()) {
                settings.setData("pass_p", options_password_field.getText());
            }

            settings.saveToXML(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/DaGammla/AccountManager/settings.xml");

            saveAccounts();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void loadFromSettings(){
        if (settings.hasAnchor("skin")){
            for (int i = 0; i < skin_box.getItemCount(); i++) {
                if (settings.getData("skin").equals(skin_box.getItemAt(i))){
                    skin_box.setSelectedIndex(i);
                }
            }
        }

        if (settings.hasAnchor("password")){
            if (settings.getData("password").equals("true")) {
                use_password_box.setSelected(true);
                encrypt_save_box.setEnabled(true);
                options_password_field.setEnabled(true);
                options_password_field.setText(settings.getData("pass_p"));
            }
        }

        if (settings.hasAnchor("encrypted")){
            if (settings.getData("encrypted").equals("true"))
                encrypt_save_box.setSelected(true);
        }
    }

    private void createUIComponents() {
        additional_pool_area = new HintTextArea("Additional Pool");

        SpinnerNumberModel model = new SpinnerNumberModel(16, 3, 255, 1);
        pasword_length_spinner = new JSpinner(model);

    }
}
