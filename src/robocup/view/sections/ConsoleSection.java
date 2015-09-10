package robocup.view.sections;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;
import robocup.Main;
import robocup.view.SectionBox;
/**
 * instance of {@link SectionBox} that displays all messages send to the {@link Logger} (use {@code Logger.getLogger(Main.class.getName())} to acces the {@link Logger}
 */
@SuppressWarnings("serial")
public class ConsoleSection extends SectionBox {

	private Object[] header = { "ID", "tijd", "level", "bericht" };
	private Object[][] data = {};
	private Logger LOGGER = Logger.getLogger(Main.class.getName());
	private DefaultTableModel model;
	private JTable table;

	/**
	 * Create ConsoleSection
	 */
	public ConsoleSection() {
		super("Console");
		setLayout(new MigLayout("wrap 1", "[grow]", "[][grow]"));

		JButton clearButton = new JButton("Clear log");
		clearButton.addActionListener(new ButtonListener());
		add(clearButton);
		
		table = new JTable(new DefaultTableModel(data, header));
		table.setEnabled(false);
		model = (DefaultTableModel) table.getModel();
		add(new JScrollPane(table), "growy, growx");
		correctColumnWidth(table);

		LOGGER.addHandler(new LoggerHandler());
	}

	/**
	 * Handler for {@link Logger} messages
	 */
	private class LoggerHandler extends Handler {
		@Override
		public void close() throws SecurityException {
		}

		@Override
		public void flush() {
		}

		@Override
		public void publish(LogRecord record) {
			model.addRow(new Object[] { record.getSequenceNumber(), toTime(record.getMillis()), record.getLevel(),
					record.getMessage() });
			table.scrollRectToVisible(table.getCellRect(table.getRowCount()-1, 0, true));
			
		}
	}


	/**
	 * ActionListener to clear the console
	 *
	 */
	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			switch (((JButton) e.getSource()).getText()) {
			case "Clear log":
				model.setRowCount(0);
			}
		}
	}
	
	/**
	 * Adjust column width
	 * @param table
	 */
	private void correctColumnWidth(JTable table) {
		table.getColumnModel().getColumn(0).setMinWidth(50);
		table.getColumnModel().getColumn(0).setMaxWidth(50);
		table.getColumnModel().getColumn(1).setMinWidth(70);
		table.getColumnModel().getColumn(1).setMaxWidth(70);
		table.getColumnModel().getColumn(2).setMinWidth(100);
		table.getColumnModel().getColumn(2).setMaxWidth(100);
	}

	/**
	 * Format {@link System.currentTimeMillis()} to readable output
	 * @param millis milliseconds since 1970 (normal Unix timestamp)
	 * @return {@link String} in HH:mm:ss format
	 */
	private String toTime(long millis) {
		return (new SimpleDateFormat("HH:mm:ss")).format(new Date(millis));
	}

	@Override
	public void update() {
	}

}