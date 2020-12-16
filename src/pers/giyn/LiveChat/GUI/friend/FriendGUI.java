package pers.giyn.LiveChat.GUI.friend;

import pers.giyn.LiveChat.client.UserController;
import pers.giyn.LiveChat.GUI.Login;
import pers.giyn.LiveChat.GUI.utils.Utils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * @author 许继元
 */
public class FriendGUI {
    public JFrame frame;
    public ArrayList<Friend> friendsList;
    public ArrayList<Friend> applyFriendsList;
    public DefaultMutableTreeNode rootNode, onLine, offLine;
    public UserController callback;
    public JButton addFriendsButton,
            deleteFriendButton, showApplicationButton;
    public boolean isDeleting = false;
    public JTree tree;

    public FriendGUI(UserController callback) {
        this(new ArrayList<>(), new ArrayList<>(), callback);
    }

    public FriendGUI(ArrayList<Friend> friendsList,
                     ArrayList<Friend> applyFriendsList,
                     UserController callback) {
        this.friendsList = friendsList;
        this.applyFriendsList = applyFriendsList;
        this.callback = callback;
        init();
        updateFriend();
    }

    public void updateFriend() {
        this.onLine.removeAllChildren();
        this.offLine.removeAllChildren();

        for (Friend friend : this.friendsList) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(friend);
            if (friend.state == 1) {
                this.onLine.add(node);
            } else if (friend.state == 0) {
                this.offLine.add(node);
            }
        }
        this.tree.updateUI();
    }

    public void init() {
        this.frame = new JFrame("好友列表");
        this.frame.setSize(400, 450);
        this.frame.setIconImage(new ImageIcon(Login.class.getResource("/pers/giyn/LiveChat/GUI/assets/icon.png")).getImage());
        this.frame.setLocationRelativeTo(null);
        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createTool(), BorderLayout.NORTH);

        rootNode = new DefaultMutableTreeNode("好友");
        onLine = new DefaultMutableTreeNode("在线好友");
        rootNode.add(onLine);
        offLine = new DefaultMutableTreeNode("离线好友");
        rootNode.add(offLine);

        this.tree = new JTree(rootNode);
        this.tree.setShowsRootHandles(true);
        this.tree.addTreeSelectionListener(e -> {
            try {
                Friend friend = (Friend) ((DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent()).getUserObject();

                if (isDeleting) {
                    ArrayList<Friend> tempArrayList = new ArrayList<>();
                    tempArrayList.add(new Friend(friend.id, friend.nickName));
                    if (JOptionPane.showConfirmDialog(frame,
                            "确定删除该好友吗?\n警告: 该删除操作不可恢复!",
                            "警告",
                            JOptionPane.YES_NO_CANCEL_OPTION) == JOptionPane.YES_OPTION) {
                        callback.deleteFriends(tempArrayList);
                    }
                } else {
                    callback.openChattingPanel(friend);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        panel.add(new JScrollPane(tree), BorderLayout.CENTER);
        this.frame.setContentPane(panel);
    }

    public JPanel createTool() {
        JPanel jp = new JPanel();
        jp.setBackground(new Color(196, 232, 188));
        jp.setLayout(new GridLayout(1, 0));
        jp.setPreferredSize(new Dimension(0, 30));

        addFriendsButton = Utils.createButton("添加");
        addFriendsButton.addActionListener(e -> addFriendInput());

        jp.add(addFriendsButton);

        deleteFriendButton = Utils.createButton("删除");
        deleteFriendButton.addActionListener(new ActionListener() {

            public Color switchColor = new Color(0.5f, 0.0f, 0.0f);

            @Override
            public void actionPerformed(ActionEvent e) {
                isDeleting = !isDeleting;
                Color tempColor = deleteFriendButton.getForeground();
                deleteFriendButton.setForeground(switchColor);
                switchColor = tempColor;
            }
        });
        jp.add(deleteFriendButton);

        showApplicationButton = Utils.createButton("通知");
        showApplicationButton.addActionListener(e -> applicationList());
        jp.add(showApplicationButton);

        return jp;
    }

    public void addFriendInput() {
        JDialog dialog = new JDialog(this.frame, "请输入好友ID:", true);
        dialog.setBounds(400, 200, 350, 150);
        dialog.setLayout(new BorderLayout());
        JButton confirm = Utils.createButton("确定");
        JTextField idFieldArea = new JTextField();

        confirm.addActionListener(e -> {
            if ("".equals(idFieldArea.getText())) {
                return;
            }
            ArrayList<Friend> tempArrayList = new ArrayList<>();
            tempArrayList.add(new Friend(idFieldArea.getText(), ""));
            callback.addFriends(tempArrayList);
            Utils.showMsgMsg("好友申请已发送!", "通知", frame);
            dialog.setVisible(false);
        });

        idFieldArea.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    confirm.doClick();
                }
            }
        });
        idFieldArea.setFont(new Font("menlo", Font.BOLD, 17));
        dialog.add(idFieldArea, BorderLayout.NORTH);
        dialog.add(confirm, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this.frame);
        dialog.setVisible(true);
    }

    public void applicationList() {
        JDialog dialog = new JDialog(this.frame, "好友申请", true);
        dialog.setBounds(400, 200, 350, 500);
        dialog.setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));

        for (Friend friend : this.applyFriendsList) {
            JButton tempButton = Utils.createButton(new Friend(friend.id, friend.nickName).toString());
            JPanel tempPanel = new JPanel();

            tempPanel.setLayout(new BorderLayout());
            tempButton.setBounds(0, 0, 0, 40);
            tempButton.addActionListener(e -> {
                ArrayList<Friend> tempArrayList = new ArrayList<>();
                String[] tempStrings = tempButton.getText().split("@");

                panel.remove(tempPanel);
                panel.updateUI();

                applyFriendsList.remove(tempButton.getText());
                tempArrayList.add(new Friend(tempStrings[1], tempStrings[0]));
                callback.addFriends(tempArrayList);
            });

            tempPanel.add(tempButton, BorderLayout.NORTH);
            panel.add(tempPanel);
        }
        dialog.add(new JScrollPane(panel), BorderLayout.NORTH);
        dialog.setLocationRelativeTo(this.frame);
        dialog.setVisible(true);
    }
}
