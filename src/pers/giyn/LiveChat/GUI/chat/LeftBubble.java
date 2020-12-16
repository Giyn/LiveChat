package pers.giyn.LiveChat.GUI.chat;

import pers.giyn.LiveChat.client.UserController;
import pers.giyn.LiveChat.GUI.utils.Utils;
import pers.giyn.LiveChat.client.file.FileFolder;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.io.File;

/**
 * @author 许继元
 */
public class LeftBubble extends JPanel {
    private final int strokeThickness;
    private final int padding;

    {
        strokeThickness = 3;
        padding = strokeThickness >> 1;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        final Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(255, 255, 255));

        int arrowSize = 12;
        int x;
        x = padding + strokeThickness + arrowSize;
        int width;
        width = getWidth() - arrowSize - (strokeThickness * 2);
        int bottomLineY = getHeight() - strokeThickness;

        g2d.fillRect(x, padding, width, bottomLineY);
        g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        g2d.setStroke(new BasicStroke(strokeThickness));

        int radius = 5;
        RoundRectangle2D.Double rect = new RoundRectangle2D.Double(x, padding, width, bottomLineY, radius, radius);
        Polygon arrow = new Polygon();

        arrow.addPoint(20, 8);
        arrow.addPoint(0, 10);
        arrow.addPoint(20, 12);

        Area area = new Area(rect);
        area.add(new Area(arrow));
        g2d.draw(area);
    }

    public static LeftBubble create(String msg) {
        LeftBubble msgPanel = new LeftBubble();
        JTextArea text = new JTextArea();

        text.setEditable(false);
        text.setBackground(new Color(255, 255, 255));
        text.setFont(new Font("menlo", Font.BOLD, 15));
        text.setForeground(new Color(0, 0, 0));
        text.setText(msg);

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(0, 0, 0, 0));

        msgPanel.setLayout(new BorderLayout());
        JPanel tempPanel = new JPanel();
        tempPanel.setBackground(new Color(0, 0, 0, 0));

        rightPanel.add(tempPanel);
        rightPanel.add(text);

        msgPanel.add(rightPanel, BorderLayout.WEST);

        return msgPanel;
    }

    public static LeftBubble createImage(String base64) {
        LeftBubble msgPanel = new LeftBubble();
        ImageIcon image;
        image = pers.giyn.LiveChat.client.utils.Utils.Base64Utils.base642Image(base64);

        if (image == null) {
            return null;
        }
        JLabel text = new JLabel(image);

        if (image.getIconHeight() > image.getIconWidth()) {
            if (image.getIconHeight() > 500) {
                image.setImage(image.getImage().getScaledInstance(image.getIconWidth() * 500 / image.getIconHeight(), 500, Image.SCALE_DEFAULT));
            }
        } else {
            if (image.getIconWidth() > 500) {
                image.setImage(image.getImage().getScaledInstance(500, image.getIconHeight() * 500 / image.getIconWidth(), Image.SCALE_DEFAULT));
            }
        }
        GroupLayout msgPanelLayout = new GroupLayout(msgPanel);
        msgPanel.setLayout(msgPanelLayout);
        msgPanelLayout.setHorizontalGroup(
                msgPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(msgPanelLayout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(text)
                                .addContainerGap(162, Short.MAX_VALUE))
        );

        msgPanelLayout.setVerticalGroup(
                msgPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(msgPanelLayout.createSequentialGroup()
                                .addComponent(text)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        return msgPanel;
    }

    public static LeftBubble createFile(String name, String srcId) {
        LeftBubble msgPanel = new LeftBubble();
        ImageIcon image;
        image = new ImageIcon(UserController.class.getResource("/pers/giyn/LiveChat/GUI/assets/blackFile.png"));

        JButton text = Utils.createButton(name);
        text.setBorderPainted(false);
        text.setBorder(null);
        text.setIcon(image);
        text.setText(name);
        text.setFont(new Font("menlo", Font.BOLD, 15));
        text.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
        text.setForeground(new Color(0.0f, 0.0f, 0.0f));
        text.addActionListener(e -> {
            Desktop desk = Desktop.getDesktop();
            try {
                desk.open(new File(FileFolder.getDefaultDirectory() + "/" + srcId + "/files/" + name));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        if (image.getIconHeight() > image.getIconWidth()) {
            if (image.getIconHeight() > 100) {
                image.setImage(image.getImage().getScaledInstance(image.getIconWidth() * 100 / image.getIconHeight(), 100, Image.SCALE_DEFAULT));
            }
        } else {
            if (image.getIconWidth() > 100) {
                image.setImage(image.getImage().getScaledInstance(100, image.getIconHeight() * 100 / image.getIconWidth(), Image.SCALE_DEFAULT));
            }
        }

        GroupLayout msgPanelLayout = new GroupLayout(msgPanel);
        msgPanel.setLayout(msgPanelLayout);
        msgPanelLayout.setHorizontalGroup(
                msgPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(msgPanelLayout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(text)
                                .addContainerGap(162, Short.MAX_VALUE))
        );
        msgPanelLayout.setVerticalGroup(

                msgPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(msgPanelLayout.createSequentialGroup()
                                .addComponent(text)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        return msgPanel;
    }
}
