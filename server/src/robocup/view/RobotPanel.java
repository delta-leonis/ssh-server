package robocup.view;

import javax.swing.JPanel;

import robocup.model.Point;

/**
*
* @author mark
*/
public class RobotPanel extends JPanel{
	 /**
     * Creates new form robotPanel1
     */
	
	private int id;
	private String status;
	private String role;
	private Point point;
    public RobotPanel(int robotId) {
    	id = robotId;
    	status = "offline";
    	role = "unavailable";
        initComponents();
    }
    
    public void setToDefault(){
    	status = "offline";
    	robotStatusField.setText(status);
    	robotPositionField.setText("null");
    	role = "unavailable";
    	robotBehaviorField.setText(role);
    }
    
    public void setrobotId(int id){
    	robotId.setText("Id: "+Integer.toString(id));
    	this.id = id;
    }
    
    public int getRobotId(){
    	return id;
    }
    
    public void setStatus(String status){
    	robotStatusField.setText(status);
    	this.status = status;
    }
    
    public void setPoint(Point point){
    	robotPositionField.setText(""+ Math.round(point.getX()) + "," + Math.round(point.getY()));
    	this.point = point;
    }
    
    public void setRole(String role){
    	this.role = role;
    	robotBehaviorField.setText(role);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        robotId = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        robotStatusField = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        robotPositionField = new javax.swing.JTextPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        robotBehaviorField = new javax.swing.JTextPane();
        
        setBorder(javax.swing.BorderFactory.createEtchedBorder());


        robotId.setText("Id: "+Integer.toString(id));

        robotStatusField.setEditable(false);
        robotStatusField.setText("offline");
        jScrollPane1.setViewportView(robotStatusField);

        robotPositionField.setEditable(false);
        jScrollPane2.setViewportView(robotPositionField);

        robotBehaviorField.setEditable(false);
        jScrollPane3.setViewportView(robotBehaviorField);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(robotId)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(robotId, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextPane robotBehaviorField;
    private javax.swing.JLabel robotId;
    private javax.swing.JTextPane robotPositionField;
    private javax.swing.JTextPane robotStatusField;
    // End of variables declaration//GEN-END:variables
}


   