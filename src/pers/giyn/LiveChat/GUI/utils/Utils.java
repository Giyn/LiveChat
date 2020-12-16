package pers.giyn.LiveChat.GUI.utils;

import javax.swing.*;
import java.awt.*;

/**
 * @author 许继元
 */
public class Utils {
    public static void showErrorMsg(String msg, String title, JFrame frame) {
        JOptionPane.showMessageDialog(frame, msg, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void showMsgMsg(String msg, String title, JFrame frame) {
        JOptionPane.showMessageDialog(frame, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void createText(String TEXT, JPanel parent) {
        JLabel JL = new JLabel(TEXT);
        JL.setFont(new Font("黑体", Font.BOLD, 20));
        parent.add(JL);
    }

    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("menlo", Font.BOLD, 17));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        return button;
    }

    public static class Theme {
        public static Color ThemeColor1 = new Color(0.5f, 0.0f, 0.0f);
        public static Color ThemeColor2 = new Color(255, 255, 255);
        public static Color buttonColor = new Color(255, 255, 255);
        public static Font titleFont = new Font("宋体", Font.BOLD, 16);
        public static Font buttonFont = new Font("宋体", Font.BOLD, 14);
    }
}
