package fop.view.menu;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import fop.view.MainFrame;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;

@SuppressWarnings("serial")
public class AboutView extends MenuView {
	
	private JEditorPane ep;
	
	private static final String ABOUT_TEXT = new StringBuilder()
			.append("<html><body style=\"text-align: center;\">")
			.append("<h2 style=\"margin-top: 0;\">~ Saboteur ~</h2>")
			.append("FOP-Projekt WiSe 20/21")
			.append("<p>Hauptautor: Dominik Beese</p>")
			.append("<p>")
			.append("Font made by <a href=\"https://www.dafont.com/de/cat-franken-deutsch.font\">Peter Wiegel</a>")
			.append("<br>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a>, <a href=\"https://www.flaticon.com/authors/dinosoftlabs\" title=\"DinosoftLabs\">DinosoftLabs</a>, <a href=\"https://www.flaticon.com/authors/creaticca-creative-agency\" title=\"Creaticca Creative Agency\">Creaticca Creative Agency</a>, <a href=\"https://www.flaticon.com/authors/those-icons\" title=\"Those Icons\">Those Icons</a>, <a href=\"https://www.flaticon.com/authors/smashicons\" title=\"Smashicons\">Smashicons</a> and <a href=\"https://www.flaticon.com/authors/kiranshastry\" title=\"Kiranshastry\">Kiranshastry</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a>")
			.append("</p>")
			.append("<p style=\"font-size: 0.85em; font-style: italic;\">Keine Haftung für Bugs, Systemabstürze, Datenverlust und rauchende Grafikkarten.</p>")
			.append("</body></html>")
			.toString();
	
	public AboutView(MainFrame window) {
		super(window, "Über 'Saboteur'");
	}
	
	@Override
	protected void addContent(JPanel contentPanel) {
		contentPanel.setLayout(new GridBagLayout());
		
		// about text //
		GridBagConstraints aboutTextConstraints = new GridBagConstraints();
		aboutTextConstraints.weightx = 1.0;
		aboutTextConstraints.weighty = 1.0;
		aboutTextConstraints.fill = GridBagConstraints.BOTH;
		aboutTextConstraints.insets = new Insets(0, 2, 2, 2);
		aboutTextConstraints.gridx = 0;
		aboutTextConstraints.gridy = 0;
		ep = new JEditorPane();
		ep.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		ep.setBackground(contentPanel.getBackground().brighter());
		ep.setContentType("text/html");
		ep.addHyperlinkListener(e -> { // clickable html links
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) try {
				Desktop.getDesktop().browse(new URI(e.getURL().toString()));
			} catch (IOException | URISyntaxException ignore) {}
		});
		ep.setEditable(false);
		ep.setSelectionColor(new Color(0, 0, 0, 0));
		ep.setText(ABOUT_TEXT);
		contentPanel.add(new JScrollPane(ep), aboutTextConstraints);
		
		// back button //
		GridBagConstraints rightImageConstraints = new GridBagConstraints();
		rightImageConstraints.insets = new Insets(2, 2, 0, 2);
		rightImageConstraints.gridx = 0;
		rightImageConstraints.gridy = 1;
		JButton backButton = createButton("Zurück");
		backButton.addActionListener(evt -> getWindow().setView(new MainMenu(getWindow())));
		contentPanel.add(backButton, rightImageConstraints);
	}
	
	@Override
	public void onResize() {
		super.onResize();
		int size = Math.min(getWidth(), getHeight());
		ep.setFont(ep.getFont().deriveFont(size / 30f));
	}
	
}
