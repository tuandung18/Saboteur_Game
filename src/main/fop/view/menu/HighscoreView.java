package fop.view.menu;

import fop.io.ScoreEntryIO;
import fop.model.ScoreEntry;
import fop.view.MainFrame;
import fop.view.View;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HighscoreView extends MenuView {
    public HighscoreView(MainFrame window) {
        super(window, "Highscores");
    }

    @Override
    public void setWindowSize(JFrame window, View oldView) {
        super.setWindowSize(window, oldView);
    }

    @Override
    protected JButton createButton(String text) {
        return super.createButton(text);
    }

    @Override
    public void onResize() {
        super.onResize();
    }

    @Override
    protected void addContent(JPanel contentPanel) {

        contentPanel.setLayout(new GridBagLayout());
        GridBagConstraints aboutHighscoreView = new GridBagConstraints();
        aboutHighscoreView.weightx = 1.0;
        aboutHighscoreView.weighty = 1.0;
        aboutHighscoreView.fill = GridBagConstraints.BOTH;
        aboutHighscoreView.insets = new Insets(0, 2, 2, 2);
        aboutHighscoreView.gridx = 0;
        aboutHighscoreView.gridy = 0;
        String[] title = new String[]{
            "Date and time",
            "Name",
            "Scores",
        };
        ScoreEntry[] scoreEntries = ScoreEntryIO.loadScoreEntries().toArray(new ScoreEntry[0]);
        String [][] stats = new String [scoreEntries.length][3];
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy' 'HH:mm:ss");
        for(int line = 0; line < scoreEntries.length ; line++){
            stats[line][0] = scoreEntries[line].getDateTime().format(dateTimeFormatter).toString();
            stats[line][1] = scoreEntries[line].getName();
            stats[line][2] = String.valueOf(scoreEntries[line].getScore());
        }
        JTable scoreTable = new JTable(stats, title);
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        JTableHeader theader = scoreTable.getTableHeader();
        theader.setBackground(this.getBackground());
        ((DefaultTableCellRenderer)theader.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(scrollPane, aboutHighscoreView);


        // back button //
        GridBagConstraints rightImageConstraints = new GridBagConstraints();
        rightImageConstraints.insets = new Insets(2, 2, 0, 2);
        rightImageConstraints.gridx = 0;
        rightImageConstraints.gridy = 1;
        JButton backButton = createButton("Zurück");
        backButton.addActionListener(evt -> getWindow().setView(new MainMenu(getWindow())));
        contentPanel.add(backButton, rightImageConstraints);


    }


}
