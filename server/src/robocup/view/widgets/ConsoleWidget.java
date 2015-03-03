package robocup.view.widgets;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;
import robocup.Main;
import robocup.view.WidgetBox;

@SuppressWarnings("serial")
public class ConsoleWidget extends WidgetBox {

	private Object[] header = { "ID", "tijd", "level", "bericht" };
	private Object[][] data = {};
	private Logger LOGGER = Logger.getLogger(Main.class.getName());
	private DefaultTableModel model;

	/**
	 * Create ConsoleWidget
	 */
	public ConsoleWidget() {
		super("Console");
		setLayout(new MigLayout("", "[grow]", "[grow]"));

		JTable table = new JTable(new DefaultTableModel(data, header));
		table.setEnabled(false);
		model = (DefaultTableModel) table.getModel();
		add(new JScrollPane(table), "growy, growx");
		correctColumnWidth(table);

		LOGGER.addHandler(new LoggerHandler());
	}

	/**
	 * Handler for LOGGER messages
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
	 * Format millis to readable output
	 * @param milliseconds since 1970 (Unix)
	 * @return String in HH:mm:ss format
	 */
	public String toTime(long millis) {
		return (new SimpleDateFormat("HH:mm:ss")).format(new Date(millis));
	}

	@Override
	public void update() {
	}

}
