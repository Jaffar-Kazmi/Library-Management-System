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
    private LibrarianDashboardPanel librarianDashboard;
    private ReaderDashboardPanel readerDashboard;

    private LoginController librarianLoginController;
    private LoginController readerLoginController;

    private User currentUser;

    public LibraryGUI() {
        setTitle("Good Books");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(700, 700));

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);

        launchPanel = new LaunchPanel();
        launchPanel.addLibrarianButtonListener(e -> showLibrarianLogin());
        launchPanel.addReaderButtonListener(e -> showReaderLogin());

        // dumy librarian
        // launchPanel.addLibrarianButtonListener(e -> showLibrarianDashboard());

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
            showLibrarianDashboard((Librarian) user);
        } else if (user instanceof Reader){
            showReaderDashboard((Reader) user);
        }
    }

    private void showLibrarianDashboard(Librarian librarian) {
        if(librarianDashboard == null) {
            librarianDashboard = new LibrarianDashboardPanel(librarian);

            librarianDashboard.addLogoutListener(e -> handleLogout());

            mainPanel.add(librarianDashboard, DASHBOARD_LIBRARIAN_PANEL);
        }
        cardLayout.show(mainPanel, DASHBOARD_LIBRARIAN_PANEL);
    }

    // Dumy Librarian to bypass login
    private void showLibrarianDashboard() {
        Librarian dummyLibrarian = new Librarian("admin", "123", "Syed Jaffar Raza Kazmi"); // dummy object
        LibrarianDashboardPanel dashboard = new LibrarianDashboardPanel(dummyLibrarian);

        mainPanel.add(dashboard, "librarianDashboard");
        CardLayout layout = (CardLayout) mainPanel.getLayout();
        layout.show(mainPanel, "librarianDashboard");
    }



    private void showReaderDashboard(Reader reader) {
        JOptionPane.showMessageDialog(this, "Reader Dashboard coming soon");
        cardLayout.show(mainPanel, LAUNCH_PANEL);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            currentUser = null;
            librarianDashboard = null;
            readerDashboard = null;

            if (mainPanel.getComponentCount()>3) {
                mainPanel.remove(3);
            }
            if (mainPanel.getComponentCount()>3) {
                mainPanel.remove(3);
            }
            cardLayout.show(mainPanel, LAUNCH_PANEL);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryGUI().setVisible(true));
    }
}
