package pers.giyn.LiveChat.GUI.chat;

import pers.giyn.LiveChat.client.UserController;
import pers.giyn.LiveChat.client.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

/**
 * @author 许继元
 */
public class RightBubble extends JPanel {
    @Override
    protected void paintComponent(final Graphics g) {
        final Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(115, 220, 22));

        int strokeThickness = 3;
        int bottomLineY = getHeight() - strokeThickness;
        int arrowSize = 12;
        int width = getWidth() - arrowSize - (strokeThickness * 2);
        int padding = strokeThickness / 2;
        g2d.fillRect(padding, padding, width, bottomLineY);
        int radius = 5;
        RoundRectangle2D.Double rect = new RoundRectangle2D.Double(padding, padding, width, bottomLineY, radius, radius);

        Polygon arrow = new Polygon();
        arrow.addPoint(width, 8);
        arrow.addPoint(width + arrowSize, 10);
        arrow.addPoint(width, 12);

        Area area = new Area(rect);
        area.add(new Area(arrow));
        g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        g2d.setStroke(new BasicStroke(strokeThickness));
        g2d.draw(area);
    }

    public static RightBubble create(String msg) {
        RightBubble msgPanel = new RightBubble();
        JTextArea text = new JTextArea();

        text.setEditable(false);
        text.setBackground(new Color(115, 220, 22));
        text.setFont(new Font("menlo", Font.BOLD, 15));
        text.setForeground(new Color(0, 0, 0));
        text.setText(msg);

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(0, 0, 0, 0));

        msgPanel.setLayout(new BorderLayout());
        rightPanel.add(text);

        JPanel tempPanel = new JPanel();
        tempPanel.setBackground(new Color(0, 0, 0, 0));
        rightPanel.add(tempPanel);
        msgPanel.add(rightPanel, BorderLayout.EAST);

        return msgPanel;
    }

    public static RightBubble createFile(String name) {
        RightBubble msgPanel = new RightBubble();
        ImageIcon image;
        image = new ImageIcon(UserController.class.getResource("/pers/giyn/LiveChat/GUI/assets/whiteFile.png"));

        if (image.getIconHeight() > image.getIconWidth()) {
            if (image.getIconHeight() > 100) {
                image.setImage(image.getImage().getScaledInstance(image.getIconWidth() * 100 / image.getIconHeight(), 100, Image.SCALE_DEFAULT));
            }
        } else {
            if (image.getIconWidth() > 100) {
                image.setImage(image.getImage().getScaledInstance(100, image.getIconHeight() * 100 / image.getIconWidth(), Image.SCALE_DEFAULT));
            }
        }
        JLabel text = new JLabel(image);
        text.setText(name);
        text.setFont(new Font("menlo", Font.BOLD, 15));
        text.setForeground(new Color(1.0f, 1.0f, 1.0f));

        GroupLayout jPanel1Layout = new GroupLayout(msgPanel);
        msgPanel.setLayout(jPanel1Layout);

        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap(171, Short.MAX_VALUE)
                                .addComponent(text)
                                .addGap(22, 22, 22))
        );

        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(text)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        return msgPanel;
    }

    public static RightBubble createImage(String base64) {
        RightBubble msgPanel = new RightBubble();
        ImageIcon image;
        image = Utils.Base64Utils.base642Image(base64);

        if (image != null) {
            if (image.getIconHeight() > image.getIconWidth()) {
                if (image.getIconHeight() > 500) {
                    image.setImage(image.getImage().getScaledInstance(image.getIconWidth() * 500 / image.getIconHeight(), 500, Image.SCALE_DEFAULT));
                }
            } else {
                if (image.getIconWidth() > 500) {
                    image.setImage(image.getImage().getScaledInstance(500, image.getIconHeight() * 500 / image.getIconWidth(), Image.SCALE_DEFAULT));
                }
            }
            JLabel text = new JLabel(image);

            GroupLayout jPanel1Layout = new GroupLayout(msgPanel);
            msgPanel.setLayout(jPanel1Layout);

            jPanel1Layout.setHorizontalGroup(
                    jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addContainerGap(171, Short.MAX_VALUE)
                                    .addComponent(text)
                                    .addGap(22, 22, 22))
            );

            jPanel1Layout.setVerticalGroup(
                    jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(text)
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            return msgPanel;
        } else {
            return null;
        }
    }
}
