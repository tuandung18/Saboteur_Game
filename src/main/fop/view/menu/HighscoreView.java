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
        // Date time formatting
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy' 'HH:mm:ss");
        // Rows: Score entries
        ScoreEntry[] rows = ScoreEntryIO.loadScoreEntries().toArray(new ScoreEntry[0]);
        // Columns: Date time, name, scores
        String [][] columns = new String [rows.length][3];
        // Fill each row with data from columns
        for(int line = 0; line < rows.length ; line++){
            columns[line][0] = rows[line].getDateTime().format(dateTimeFormatter).toString();
            columns[line][1] = rows[line].getName();
            columns[line][2] = String.valueOf(rows[line].getScore());
        }
        JTable scoreTable = new JTable(columns, title);
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
