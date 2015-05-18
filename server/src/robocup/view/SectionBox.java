package robocup.view;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

/**
 * Abstract class for different sections for the {@link GUI}
 */
@SuppressWarnings("serial")
public abstract class SectionBox extends JPanel {
	public JLabel panelTitle;
	private boolean visible = true;
	private JPanel contentpanel = new JPanel();
//	private LayoutManager layoutmgr = contentpanel.getLayout();
	
	/**
	 * Create a sectionBox
	 * @param title 	the title of the section
	 */
	public SectionBox(String title) {
		super.setLayout(new MigLayout("wrap 1", "[grow]", "[grow]"));
		contentpanel.setBorder(BorderFactory.createTitledBorder(""));
		panelTitle = new JLabel("[-] " + title);
		panelTitle.setCursor(new Cursor(Cursor.HAND_CURSOR));
		panelTitle.addMouseListener(new MouseAdapter()  
		{  
		    public void mouseClicked(MouseEvent e)  {
		    	visible = !visible;
	    		SectionBox box = ((SectionBox)((JLabel)e.getSource()).getParent());
	    		String title = box.panelTitle.getText().substring(3);
		    	if(visible){
		    		box.panelTitle.setText("[-]" + title);
		    		box.superAdd(contentpanel, "growx, growy");
		    	}else{
		    		box.removeAll();
		    		box.panelTitle.setText("[+]" + title);
		    		box.superAdd(box.panelTitle);
		    	}
		    	box.revalidate();
		    	box.repaint();
		    }
		});
		superAdd(panelTitle, "wrap");
		superAdd(contentpanel, "growx, growy");
	}
	

	public void superAdd(Component item, Object constraints){
		super.add(item, constraints);
	}
	
	public void superAdd(Component item){
		super.add(item);
	}
	
	@Override
	public void setLayout(LayoutManager mgr){
		if(contentpanel != null)
			contentpanel.setLayout(mgr);
	}

	@Override
	public void add(Component item, Object constr){
		contentpanel.add(item, constr);
	}

	@Override
	public Component add(Component item){
		contentpanel.add(item);
		return item;
	}
	
	public String getTitle(){
		return panelTitle.getText().substring(4);
	}
	
	/**
	 * Will be called when {@code SSL_Referee} or {@code SSL_DetectionRobot} is handled
	 */
	public abstract void update();
}
