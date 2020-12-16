package pers.giyn.LiveChat.GUI.chat;

import pers.giyn.LiveChat.client.UserController;
import pers.giyn.LiveChat.GUI.friend.Friend;
import pers.giyn.LiveChat.client.utils.Utils;
import pers.giyn.LiveChat.client.file.chattingRecord.ChattingRecord;
import pers.giyn.LiveChat.client.file.chattingRecord.ChattingRecordManager;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author 许继元
 */
public class ChattingGUI {
    public JFrame frame;
    public JButton sendButton;
    public JButton toolImageButton, toolFileButton;
    public JTextArea msgArea;
    public JTextArea editArea;
    public JScrollPane jScrollPane;
    public UserController callback;
    public ChattingPanel chattingPanel;
    public Friend friend;
    public ArrayList<ChattingRecord> chattingRecords;

    public void update() {
        this.frame.setTitle(friend.toString());
        this.chattingPanel.friend = friend;

        if (friend.state == 1) {
            this.sendButton.setEnabled(true);
            this.sendButton.setText("发送");
            this.toolImageButton.setEnabled(true);
            this.toolFileButton.setEnabled(true);
            this.editArea.setEnabled(true);
        } else {
            this.sendButton.setEnabled(false);
            this.sendButton.setText("该好友当前不在线");
            this.toolImageButton.setEnabled(false);
            this.toolFileButton.setEnabled(false);
            this.editArea.setEnabled(false);
        }
    }

    public ChattingGUI(UserController callback, Friend friend) {
        this.friend = friend;
        this.callback = callback;

        try {
            this.chattingRecords = ChattingRecordManager.readChattingRecord(callback.id, friend.id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        frame = new JFrame();
        frame.setBackground(pers.giyn.LiveChat.GUI.utils.Utils.Theme.ThemeColor1);

        Toolkit t = Toolkit.getDefaultToolkit();
        Dimension d = t.getScreenSize();

        frame.setBounds((d.width - d.width / 3) / 2, (d.height - d.height / 3) / 2, 700, 700);
        frame.setIconImage(new ImageIcon(ChattingGUI.class.getResource("/pers/giyn/LiveChat/GUI/assets/icon.png")).getImage());
        frame.setResizable(true);
        frame.setLayout(new BorderLayout());
        frame.add(creatSouth(), BorderLayout.SOUTH);
        frame.add(creatCenter(), BorderLayout.CENTER);
        frame.setVisible(true);

        this.callback = callback;
        this.frame.addWindowListener(new CloseWindow());
        this.loadChattingRecord();
        this.update();
    }

    public void loadChattingRecord() {
        if (this.chattingRecords == null) {
            return;
        }

        for (ChattingRecord chattingRecord : this.chattingRecords) {
            if (chattingRecord.state == 0) {
                this.chattingPanel.addSent(chattingRecord.msg, 1);
            } else if (chattingRecord.state == 1) {
                this.chattingPanel.addReceived(chattingRecord.msg, 1);
            } else if (chattingRecord.state == 2) {
                this.chattingPanel.addSent(chattingRecord.msg, 2);
            } else if (chattingRecord.state == 3) {
                this.chattingPanel.addReceived(chattingRecord.msg, 2);
            } else if (chattingRecord.state == 4) {
                this.chattingPanel.addSent(chattingRecord.msg, 3);
            } else if (chattingRecord.state == 5) {
                this.chattingPanel.addReceived(chattingRecord.msg, 3);
            }
        }
    }

    public JScrollPane creatCenter() {
        msgArea = new JTextArea();
        msgArea.setFont(new Font("menlo", Font.BOLD, 17));

        chattingPanel = new ChattingPanel(new Friend(callback.id, ""), this.friend);
        chattingPanel.setBackground(new Color(232, 232, 232));

        jScrollPane = new JScrollPane(chattingPanel);
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane.setBackground(new Color(255, 255, 255));

        return jScrollPane;
    }

    public JPanel creatSouth() {
        JPanel jp = new JPanel();
        jp.setBackground(new Color(255, 255, 255));
        jp.setLayout(new BorderLayout());
        jp.setPreferredSize(new Dimension(0, 200));

        editArea = new JTextArea();
        editArea.setFont(new Font("menlo", Font.BOLD, 17));

        sendButton = new JButton("Send");
        sendButton.setBounds(0, 0, 260, 30);
        sendButton.setFont(new Font("menlo", Font.BOLD, 17));
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sendButton.setContentAreaFilled(false);
        sendButton.setFocusPainted(false);

        Action sendAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!"".equals(editArea.getText())) {
                    sendMsg(editArea.getText(), 1);
                }
                editArea.setText("");
            }
        };
        sendButton.addActionListener(sendAction);
        editArea.getInputMap().put(KeyStroke.getKeyStroke('\n', InputEvent.CTRL_DOWN_MASK), "send");
        editArea.getActionMap().put("send", sendAction);

        jp.add(createTool(), BorderLayout.NORTH);
        jp.add(new JScrollPane(editArea), BorderLayout.CENTER);
        jp.add(sendButton, BorderLayout.SOUTH);

        return jp;
    }

    public JPanel createTool() {
        JPanel jp = new JPanel();
        jp.setBackground(new Color(255, 255, 255));
        jp.setLayout(new GridLayout(1, 0));
        jp.setPreferredSize(new Dimension(0, 30));

        toolImageButton = new JButton("图片");
        toolImageButton.setFont(new Font("menlo", Font.BOLD, 17));
        toolImageButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toolImageButton.setContentAreaFilled(false);
        toolImageButton.setFocusPainted(false);

        Action imageAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser fd = new JFileChooser();

                fd.setAcceptAllFileFilterUsed(false);
                fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fd.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.getName().endsWith(".bmp") || f.getName().endsWith(".gif") || f.getName().endsWith(".jpg") || f.getName().endsWith(".png") || f.isDirectory();
                    }

                    @Override
                    public String getDescription() {
                        return "Picture(*.png/jpg/gif/bmp)";
                    }
                });

                if (fd.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    File file = fd.getSelectedFile();

                    try {
                        if (file != null) {
                            sendMsg(Utils.Base64Utils.readBase64(file), 2);
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };
        toolImageButton.addActionListener(imageAction);
        jp.add(toolImageButton);
        toolFileButton = new JButton("文件");
        toolFileButton.setFont(new Font("menlo", Font.BOLD, 17));
        toolFileButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toolFileButton.setContentAreaFilled(false);
        toolFileButton.setFocusPainted(false);

        Action fileAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fd = new JFileChooser();
                fd.setFileSelectionMode(JFileChooser.FILES_ONLY);

                if (fd.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    File file = fd.getSelectedFile();
                    if (file.length() > 1024 * 1024 * 10) {
                        callback.errorOccupy("文件大于10MB!");
                    } else {
                        sendMsg(file.getName(), 3);
                        new Thread(() -> {
                            byte[] data;
                            try {
                                data = Utils.FileUtils.readFile(file);

                                callback.sendFile(friend.id, file.getName(), data);
                            } catch (Exception e12) {
                                e12.printStackTrace();
                            }


                        }).start();
                    }
                }
            }
        };
        toolFileButton.addActionListener(fileAction);
        jp.add(toolFileButton);

        return jp;
    }

    public void sendMsg(String msg, int type) {
        try {
            if (type != 3) {
                callback.sendMsg(this.friend.id, msg, type);
            }
            this.chattingPanel.addSent(msg, type);
            if (type == 1) {
                chattingRecords.add(new ChattingRecord(callback.id, "", msg, (byte) 0));
            } else {
                if (type == 2) {
                    chattingRecords.add(new ChattingRecord(callback.id, "", msg, (byte) 2));
                } else if (type == 3) {
                    chattingRecords.add(new ChattingRecord(callback.id, "", msg, (byte) 4));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        jScrollPane.getViewport().setViewPosition(new Point(0, jScrollPane.getVerticalScrollBar().getMaximum()));
    }

    public void receiveMsg(String msg, int type) {
        this.chattingPanel.addReceived(msg, type);

        if (type == 1) {
            chattingRecords.add(new ChattingRecord(callback.id, "", msg, (byte) 1));
        } else {
            if (type == 2) {
                chattingRecords.add(new ChattingRecord(callback.id, "", msg, (byte) 3));
            } else if (type == 3) {
                chattingRecords.add(new ChattingRecord(callback.id, "", msg, (byte) 5));
            }
        }
        jScrollPane.getViewport().setViewPosition(new Point(0, jScrollPane.getVerticalScrollBar().getMaximum()));
    }

    class CloseWindow extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            try {
                ChattingRecordManager.saveChattingRecord(callback.id, friend.id, chattingRecords);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            callback.closeChattingPanel(friend);
            dispose();
        }
    }
    public void dispose() {
        this.frame.dispose();
    }
}
