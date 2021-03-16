package fop.view;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import fop.io.IconReader;
import fop.view.menu.MainMenu;
import javax.swing.JFrame;

/**
 * 
 * Das Fenster für alle Ansichten ({@link View}) dar.
 *
 */
@SuppressWarnings("serial")
public final class MainFrame extends JFrame {
	
	private View activeView;
	
	public MainFrame() {
		super("Saboteur");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(400, 240));
		addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent componentEvent) {
				onResize();
			}
			
		});
		addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_F11)
					setExtendedState(getExtendedState() == Frame.MAXIMIZED_BOTH ? Frame.NORMAL : Frame.MAXIMIZED_BOTH);
			}
			
		});
		
		// size //
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		double scale = dimension.width > 2000 ? 0.3 : 0.5;
		dimension.width *= scale;
		dimension.height *= scale;
		setPreferredSize(dimension);
		
		// icon //
		setIconImage(IconReader.readIcon("logo"));
		
		// view //
		setView(new MainMenu(this));
		
		// location //
		pack();
		setLocationRelativeTo(null);
	}
	
	/**
	 * Wird aufgerufen, damit die sich aktive Ansicht an die neue Fenstergröße anpassen kann.
	 */
	public void onResize() {
		activeView.onResize();
	}
	
	/**
	 * Setzt die aktuelle Ansicht des Fensters.
	 * @param view die neue Ansicht
	 */
	public void setView(View view) {
		if (getExtendedState() == Frame.NORMAL)
			view.setWindowSize(this, activeView);
		activeView = view;
		activeView.setSize(getContentPane().getSize());
		activeView.onResize();
		setContentPane(view);
		requestFocus();
	}
	
}
