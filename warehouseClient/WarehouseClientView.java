package warehouseClient;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

public class WarehouseClientView {
	public void createAndUseView() {
		useWindowsTheme();
		
		JFrame frame = new JFrame("Warehouse client");
		final JLabel label = new JLabel("Test");
		frame.getContentPane().add(label);
		//center it
		frame.setLocationRelativeTo(null);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	private void useWindowsTheme() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}
		catch(Exception exception) {
		}
	}
}
