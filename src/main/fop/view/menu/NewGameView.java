package fop.view.menu;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import fop.controller.GameController;
import fop.io.FontReader;
import fop.io.IconReader;
import fop.view.MainFrame;
import fop.view.game.GameView;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

@SuppressWarnings("serial")
public final class NewGameView extends MenuView {
	
	private JPanel configurationPanel;
	
	private GridLayout buttonPanelLayout;
	
	private List<JLabel> idList;
	private List<JTextField> nameList;
	private List<JToggleButton> computerList;
	
	public NewGameView(MainFrame window) {
		super(window, "Neues Spiel");
	}
	
	@Override
	protected void addContent(JPanel contentPanel) {
		contentPanel.setLayout(new GridBagLayout());
		
		// configuration panel //
		GridBagConstraints configurationPanelConstraints = new GridBagConstraints();
		configurationPanelConstraints.weightx = 1.0;
		configurationPanelConstraints.weighty = 1.0;
		configurationPanelConstraints.fill = GridBagConstraints.BOTH;
		configurationPanelConstraints.insets = new Insets(0, 2, 2, 2);
		configurationPanelConstraints.gridx = 0;
		configurationPanelConstraints.gridy = 0;
		GridBagLayout configurationLayout = new GridBagLayout();
		configurationLayout.rowHeights = new int[101];
		configurationLayout.rowWeights = new double[101];
		configurationLayout.rowWeights[100] = Double.MIN_VALUE;
		configurationPanel = new JPanel(configurationLayout);
		idList = new LinkedList<>();
		nameList = new LinkedList<>();
		computerList = new LinkedList<>();
		JScrollPane scrollPane = new JScrollPane(configurationPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		contentPanel.add(scrollPane, configurationPanelConstraints);
		
		// button panel //
		GridBagConstraints buttonPanelConstraints = new GridBagConstraints();
		buttonPanelConstraints.insets = new Insets(2, 2, 0, 2);
		buttonPanelConstraints.gridx = 0;
		buttonPanelConstraints.gridy = 1;
		JPanel buttonPanel = new JPanel(buttonPanelLayout = new GridLayout(0, 2, 10, 0));
		
		JButton backButton = createButton("Zurück");
		backButton.addActionListener(evt -> getWindow().setView(new MainMenu(getWindow())));
		buttonPanel.add(backButton);
		
		JButton startButton = createButton("Starten");
		startButton.addActionListener(evt -> startGame());
		buttonPanel.add(startButton);
		
		contentPanel.add(buttonPanel, buttonPanelConstraints);
		
		// add default players //
		addRow();
		String playerName = System.getProperty("user.name");
		if (playerName == null || playerName.isBlank())
			playerName = "Spieler 1";
		else if (playerName.matches("(\\w+\\s*)+")) playerName = Arrays.stream(playerName.split("\\s+"))
				.map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase()).collect(Collectors.joining(" "));
		nameList.get(0).setText(playerName);
		
		addRow();
		nameList.get(1).setText("Spieler 2");
		// computerList.get(1).setSelected(true); // default second player to computer
		
		updateRows();
	}
	
	private void addRow() {
		int idx = nameList.size();
		
		// id
		JLabel id = new JLabel(String.format("%d.", idx + 1));
		id.setFont(FontReader.readMenuFont());
		idList.add(id);
		
		GridBagConstraints idC = new GridBagConstraints();
		idC.anchor = GridBagConstraints.EAST;
		idC.weightx = 1.0;
		idC.insets = new Insets(2, 0, 2, 4);
		idC.gridx = 0;
		idC.gridy = idx;
		configurationPanel.add(id, idC);
		
		// name
		JTextField name = new JTextField();
		name.setFont(FontReader.readMenuFont());
		nameList.add(name);
		
		GridBagConstraints nameC = new GridBagConstraints();
		nameC.insets = new Insets(2, 4, 2, 4);
		nameC.gridx = 1;
		nameC.gridy = idx;
		configurationPanel.add(name, nameC);
		
		// computer
		JToggleButton computer = new ToggleComputerButton();
		computerList.add(computer);
		
		GridBagConstraints computerC = new GridBagConstraints();
		computerC.anchor = GridBagConstraints.WEST;
		computerC.weightx = 1.0;
		computerC.insets = new Insets(2, 4, 2, 0);
		computerC.gridx = 2;
		computerC.gridy = idx;
		configurationPanel.add(computer, computerC);
		
		// listener
		Consumer<Boolean> setEnabled = (enabled) -> {
			id.setEnabled(enabled);
			name.setEnabled(enabled);
			computer.setEnabled(enabled);
		};
		MouseListener enabler = new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				setEnabled.accept(true);
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				setEnabled.accept(true);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				updateRows();
			}
			
		};
		id.addMouseListener(enabler);
		name.addMouseListener(enabler);
		computer.addMouseListener(enabler);
		
		name.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateRows();
			}
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateRows();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateRows();
			}
			
		});
		
		name.addFocusListener(new FocusListener() {
			
			@Override
			public void focusGained(FocusEvent e) {
				updateRows();
			}
			
			@Override
			public void focusLost(FocusEvent e) {}
			
		});
		
		configurationPanel.revalidate();
		onResize();
	}
	
	private void updateRows() {
		int count = nameList.size();
		
		// add row if all rows have names
		if (nameList.stream().allMatch(tf -> !tf.getText().isBlank())) {
			addRow();
			count++;
		}
		
		// delete row if row is unused
		if (count > 1) for (int i = 0; i < count - 1; i++)
			if (nameList.get(i).getText().isBlank() && nameList.get(i).getText().isBlank()
					&& !nameList.get(i).hasFocus() && !nameList.get(i).hasFocus()) {
						configurationPanel.remove(idList.remove(i));
						configurationPanel.remove(nameList.remove(i));
						configurationPanel.remove(computerList.remove(i));
						configurationPanel.revalidate();
						repaint();
						count--;
						i--;
					}
		
		// reorder layout indices
		GridBagLayout layout = (GridBagLayout) configurationPanel.getLayout();
		for (int i = 0; i < count; i++) {
			GridBagConstraints idC = layout.getConstraints(idList.get(i));
			idC.gridy = i;
			layout.setConstraints(idList.get(i), idC);
			idList.get(i).setText(String.format("%d.", i + 1));
			GridBagConstraints idName = layout.getConstraints(nameList.get(i));
			idName.gridy = i;
			layout.setConstraints(nameList.get(i), idName);
			GridBagConstraints idComputer = layout.getConstraints(computerList.get(i));
			idComputer.gridy = i;
			layout.setConstraints(computerList.get(i), idComputer);
		}
		
		// enable all but last row
		for (int i = 0; i < count - 1; i++) {
			idList.get(i).setEnabled(true);
			nameList.get(i).setEnabled(true);
			computerList.get(i).setEnabled(true);
		}
		
		// disable last row if not focused
		if (!idList.get(count - 1).hasFocus() && !nameList.get(count - 1).hasFocus()) {
			idList.get(count - 1).setEnabled(false);
			nameList.get(count - 1).setEnabled(false);
			computerList.get(count - 1).setEnabled(false);
		}
	}
	
	private void startGame() {
		updateRows();
		int count = nameList.size() - 1;
		
		GameController.reset();
		
		for (int i = 0; i < count; i++)
			GameController.addPlayer(nameList.get(i).getText().strip(), computerList.get(i).isSelected());
		
		getWindow().setView(new GameView(getWindow()));
		
		GameController.startGame();
	}
	
	@Override
	public void onResize() {
		super.onResize();
		int size = Math.min(getWidth(), getHeight());
		
		Consumer<JComponent> resize = comp -> comp.setFont(comp.getFont().deriveFont(size / 18f));
		
		for (JLabel id : idList)
			resize.accept(id);
		
		for (JTextField name : nameList) {
			resize.accept(name);
			// fix text field width
			if (size > 0) {
				int width = (getWidth() - 8) / 3; // (total width - insets) * weight
				// calculate correct heights
				name.setMinimumSize(null);
				name.setPreferredSize(null);
				// set correct widths
				name.setMinimumSize(new Dimension(width, name.getMinimumSize().height));
				name.setPreferredSize(new Dimension(width, name.getPreferredSize().height));
			}
		}
		
		for (JToggleButton computer : computerList) {
			int height = nameList.get(0).getPreferredSize().height;
			Dimension dim = new Dimension(height, height);
			computer.setMinimumSize(dim);
			computer.setPreferredSize(dim);
		}
		
		buttonPanelLayout.setHgap(size / 25);
	}
	
	private static class ToggleComputerButton extends JToggleButton {
		
		private static final Image human = IconReader.readIcon("human");
		private static final Image computer = IconReader.readIcon("computer");
		
		public ToggleComputerButton() {
			setFocusable(false);
			setContentAreaFilled(false);
		}
		
		@Override
		public Icon getDisabledIcon() {
			return UIManager.getLookAndFeel().getDisabledIcon(this, getIcon());
		}
		
		@Override
		public Icon getIcon() {
			int size = (int) (Math.min(getWidth(), getHeight()) * 0.85);
			if (size == 0) return null;
			Image icon = isSelected() ? computer : human;
			return new ImageIcon(icon.getScaledInstance(size, size, Image.SCALE_SMOOTH));
		}
		
	}
	
}
