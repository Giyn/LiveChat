package pers.giyn.LiveChat.GUI;

import pers.giyn.LiveChat.GUI.utils.Utils;
import pers.giyn.LiveChat.client.UserController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author 许继元
 */
public class Login implements KeyListener {
    private final JFrame frame;
    private JTextField idField;
    private JPasswordField passwordField;
    private final UserController callback;
    private boolean logined = false;

    public Login(UserController callback) {
        frame = new JFrame("LiveChat");
        frame.setBackground(Utils.Theme.ThemeColor1);

        Toolkit t = Toolkit.getDefaultToolkit();
        Dimension d = t.getScreenSize();

        frame.setBounds((d.width - d.width / 3) / 2, (d.height - d.height / 3) / 2, 510, 380);
        frame.setIconImage(new ImageIcon(Login.class.getResource("/pers/giyn/LiveChat/GUI/assets/icon.png")).getImage());
        frame.setResizable(false);

        JPanel northPanel = creatNorth();
        JPanel westPanel = creatWest();
        JPanel centerPanel = creatCenter();
        JPanel southPanel = creatSouth();
        JPanel eastPanel = creatEast();

        frame.add(northPanel, BorderLayout.NORTH);
        frame.add(westPanel, BorderLayout.WEST);
        frame.add(southPanel, BorderLayout.SOUTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(eastPanel, BorderLayout.EAST);
        frame.setVisible(true);

        this.callback = callback;
        this.frame.addWindowListener(new CloseWindow());
    }

    public JPanel creatNorth() {
        JPanel jp = new JPanel();
        jp.setBackground(new Color(255, 255, 255));
        jp.setLayout(null);
        jp.setPreferredSize(new Dimension(0, 190));

        ImageIcon in = new ImageIcon(Login.class.getResource("/pers/giyn/LiveChat/GUI/assets/logo.png"));
        JLabel cc = new JLabel(in);
        cc.setBounds(0, 0, 500, 190);
        cc.setOpaque(false);
        jp.add(cc);

        return jp;
    }

    public JPanel creatWest() {
        JPanel jp = new JPanel();
        jp.setBackground(new Color(255, 255, 255));
        jp.setLayout(null);
        jp.setPreferredSize(new Dimension(140, 0));

        JLabel IDLabel = new JLabel("ID:");
        IDLabel.setBounds(122, 10, 100, 30);
        IDLabel.setFont(new Font("menlo", Font.BOLD, 15));
        jp.add(IDLabel);

        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setBounds(105, 42, 200, 30);
        passwordLabel.setFont(new Font("menlo", Font.BOLD, 15));
        jp.add(passwordLabel);

        return jp;
    }

    public JPanel creatCenter() {
        JPanel jp = new JPanel();
        jp.setBackground(new Color(255, 255, 255));
        jp.setLayout(null);
        jp.setPreferredSize(new Dimension(0, 220));

        idField = new JTextField(10);
        idField.setBounds(11, 10, 200, 30);
        idField.setFont(new Font("menlo", Font.BOLD, 17));
        idField.addFocusListener(new JTextFieldHandler(idField, "ID"));
        idField.setOpaque(false);
        idField.addKeyListener(this);
        jp.add(idField);

        passwordField = new JPasswordField(18);
        passwordField.setBounds(11, 42, 200, 30);
        passwordField.setFont(new Font("menlo", Font.BOLD, 17));
        passwordField.addFocusListener(new JPasswordFieldHandler(passwordField, "密码"));
        passwordField.setOpaque(false);
        passwordField.addKeyListener(this);
        jp.add(passwordField);

        return jp;
    }

    public JPanel creatSouth() {
        JPanel jp = new JPanel();
        jp.setBackground(new Color(255, 255, 255));
        jp.setLayout(null);
        jp.setPreferredSize(new Dimension(0, 40));

        JButton loginButton = Utils.createButton("登录");
        loginButton.setBounds(180, 0, 140, 30);
        loginButton.addActionListener(new LoginHandler(this));
        loginButton.setMargin(new Insets(0,0,0,0));
        jp.add(loginButton);

        return jp;
    }

    public JPanel creatEast() {
        JPanel jp = new JPanel();
        jp.setBackground(new Color(255, 255, 255));
        jp.setLayout(null);
        jp.setPreferredSize(new Dimension(130, 0));

        return jp;
    }

    public boolean checkInput() {
        return !"".equals(idField.getText()) && !"".equals(String.valueOf(passwordField.getPassword()));
    }

    class LoginHandler implements ActionListener {
        Login uiFrame;

        public LoginHandler(Login uiFrame) {
            super();
            this.uiFrame = uiFrame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (uiFrame.checkInput()) {
                try {
                    if (uiFrame.callback.login(idField.getText(), String.valueOf(passwordField.getPassword()))) {
                        uiFrame.logined = true;
                        uiFrame.dispose();
                    }
                } catch (Exception e1) {
                    Utils.showErrorMsg(e1.getMessage(), "错误", frame);
                    e1.printStackTrace();
                }
            }
        }
    }

    static class JTextFieldHandler implements FocusListener {
        private final String str;
        private final JTextField text1;

        public JTextFieldHandler(JTextField text1, String str) {
            this.text1 = text1;
            this.str = str;
        }

        @Override
        public void focusGained(FocusEvent e) {
            if (text1.getText().equals(str)) {
                text1.setText("");
                text1.setForeground(Color.BLACK);
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            if ("".equals(text1.getText())) {
                text1.setForeground(Color.gray);
                text1.setText(str);
            }
        }
    }

    static class JPasswordFieldHandler implements FocusListener {
        private final String str;
        private final JPasswordField text1;

        public JPasswordFieldHandler(JPasswordField text1, String str) {
            this.text1 = text1;
            this.str = str;
        }

        @Override
        public void focusGained(FocusEvent e) {
            if (String.valueOf(text1.getPassword()).equals(str)) {
                text1.setText("");
                text1.setEchoChar('*');
                text1.setForeground(Color.BLACK);
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            if ("".equals(String.valueOf(text1.getPassword()))) {
                text1.setEchoChar((char) (0));
                text1.setForeground(Color.gray);
                text1.setText(str);
            }
        }
    }

    class CloseWindow extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            dispose();
        }
    }

    public void dispose() {
        if (!logined) {
            this.callback.exit();
        }
        this.frame.dispose();
    }

    int preKey = 0;

    @Override
    public void keyPressed(KeyEvent event) {
        preKey = event.getKeyCode();
    }

    @Override
    public void keyReleased(KeyEvent event) {
        if ("ENTER".equals(KeyEvent.getKeyText(event.getKeyCode())) && "ENTER".equals(KeyEvent.getKeyText(preKey)) || event.getKeyCode() == 10 && preKey == 10) {
            if (this.checkInput()) {
                try {
                    if (this.callback.login(idField.getText(), String.valueOf(passwordField.getPassword()))) {
                        logined = true;
                        dispose();
                    }
                } catch (Exception e) {
                    Utils.showErrorMsg(e.getMessage(), "错误", frame);
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent event) {
    }
}
