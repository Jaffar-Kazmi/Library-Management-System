package com.library.view;

import com.library.controller.LoginController;
import com.library.model.Librarian;
import com.library.model.Reader;
import com.library.model.User;

import javax.swing.*;
import java.awt.*;

public class LibraryGUI extends JFrame implements LoginController.LoginCallBack {

    private static final String LAUNCH_PANEL = "Launch";
    private static final String LOGIN_LIBRARIAN_PANEL = "LoginLibrarian";
    private static final String LOGIN_READER_PANEL = "LoginReader";
    private static final String DASHBOARD_LIBRARIAN_PANEL = "DashboardLibrarian";
    private static final String DASHBOARD_READER_PANEL = "DashboardReader";


    private JPanel mainPanel;
    private CardLayout cardLayout;
    private LaunchPanel launchPanel;
    private LoginPanel librarianLoginPanel;
    private LoginPanel readerLoginPanel;

    private LoginController librarianLoginController;
    private LoginController readerLoginController;

    private User currentUser;

    public LibraryGUI() {
        setTitle("Good Books");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(700, 700));

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);

        launchPanel = new LaunchPanel(
                e-> showLibrarianLogin(),
                e-> showReaderLogin()
        );

        librarianLoginPanel = new LoginPanel("Librarian");
        readerLoginPanel = new LoginPanel("Reader");

        // Create controllers
        librarianLoginController = new LoginController(librarianLoginPanel, this);
        readerLoginController = new LoginController(readerLoginPanel, this);

        librarianLoginPanel.setController(librarianLoginController);
        readerLoginPanel.setController(readerLoginController);

        mainPanel.add(launchPanel, "launch");
        mainPanel.add(librarianLoginPanel, LOGIN_LIBRARIAN_PANEL);
        mainPanel.add(readerLoginPanel, LOGIN_READER_PANEL);

        // Start on launch
        CardLayout layout = (CardLayout) mainPanel.getLayout();
        layout.show(mainPanel, LAUNCH_PANEL);
    }

    private void showLibrarianLogin() {
        librarianLoginPanel.clearAll();
        cardLayout.show(mainPanel, LOGIN_LIBRARIAN_PANEL);
    }

    private void showReaderLogin() {
        readerLoginPanel.clearAll();
        cardLayout.show(mainPanel, LOGIN_READER_PANEL);
    }

    @Override
    public void onLoginSuccess(User user) {
        this.currentUser = user;

        if (user instanceof Librarian) {
            JOptionPane.showMessageDialog(librarianLoginPanel, "Librarian Dashboard will be displayed here");
        } else if (user instanceof Reader){
            JOptionPane.showMessageDialog(readerLoginPanel, "Reader Dashboard will be displayed here");
        }
    }




    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryGUI().setVisible(true));
    }
}
