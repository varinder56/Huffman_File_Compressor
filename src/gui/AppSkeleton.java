package gui;
import java.awt.*;
import javax.swing.*;

public class AppSkeleton extends JFrame {

    //! theme colors
    private final Color sidebarColor = new Color(24, 32, 49);
    private final Color accentColor = new Color(76, 110, 245);
    private final Color backgroundColor = new Color(245, 247, 252);
    private final Color normalTextColor = new Color(225, 231, 243);
    private final Color secondaryColor = new Color(160, 174, 200);
    private final Color separatorColor = new Color(75, 84, 101);

    //! Main content area
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    //! Sidebar navigation
    private final JPanel navigationPanel = new JPanel(new GridLayout(0, 1, 0, 6));

    public AppSkeleton() {

        setTitle("Huffman File Compressor");
        setSize(1150, 740);
        setMinimumSize(new Dimension(950, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        createSidebar();
        createContentArea();

        cardLayout.show(contentPanel, "Home");
    }

    private void createSidebar() {

        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(235, 0));
        sidebar.setBackground(sidebarColor);
        sidebar.setBorder(BorderFactory.createEmptyBorder(28, 0, 22, 0));

        //! Header
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 7));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(4, 25, 25, 17));

        JLabel logo = new JLabel("HUFFMAN");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel subtitle = new JLabel("FILE COMPRESSOR");
        subtitle.setForeground(secondaryColor);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));

        header.add(logo);
        header.add(subtitle);

        //! Separator below the header
        JSeparator separator = new JSeparator();
        separator.setForeground(separatorColor);
        separator.setBackground(sidebarColor);

        JPanel headerSection = new JPanel(new BorderLayout());
        headerSection.setOpaque(false);
        headerSection.add(header, BorderLayout.NORTH);

        JPanel separatorWrapper = new JPanel(new BorderLayout());
        separatorWrapper.setOpaque(false);
        separatorWrapper.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 17));
        separatorWrapper.add(separator, BorderLayout.CENTER);

        headerSection.add(separatorWrapper, BorderLayout.SOUTH);

        //! Sidebar navigation
        navigationPanel.setOpaque(false);

        addNavigationButton("Compression", "Compression");
        addNavigationButton("Decompression", "Decompression");
        addNavigationButton("Show History", "History");
        addNavigationButton("About", "About");

        //! Assemble sidebar
        sidebar.add(headerSection, BorderLayout.NORTH);
        sidebar.add(navigationPanel, BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
    }

    private void addNavigationButton(String label, String pageName) {

        JButton button = new JButton(label);

        button.setFont(new Font("SansSerif", Font.PLAIN, 13));
        button.setForeground(normalTextColor);
        button.setBackground(sidebarColor);
        button.setHorizontalAlignment(JButton.LEFT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(14, 32, 14, 8));

        button.addActionListener(event -> showPage(pageName));

        navigationPanel.add(button);
    }

    private void createContentArea() {

        contentPanel.setBackground(backgroundColor);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 34, 30, 34));

        contentPanel.add(new Home(this), "Home");
        contentPanel.add(new Compression(), "Compression");
        contentPanel.add(new Decompression(), "Decompression");
        contentPanel.add(new History(), "History");
        

        add(contentPanel, BorderLayout.CENTER);
    }

    public void showPage(String pageName) {

        cardLayout.show(contentPanel, pageName);

        for (int i = 0; i < navigationPanel.getComponentCount(); i++) {

            JButton button = (JButton) navigationPanel.getComponent(i);

            String buttonPage = switch (i) {
                case 0 -> "Compression";
                case 1 -> "Decompression";
                case 2 -> "History";
                case 3 -> "About";
                default -> "";
            };

            boolean selected = buttonPage.equals(pageName);

            button.setBackground(selected ? accentColor : sidebarColor);
            button.setForeground(selected ? Color.WHITE : normalTextColor);
        }
    }
}