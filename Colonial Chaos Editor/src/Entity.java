import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

public abstract class Entity extends JPanel{
    private static PropertiesDialog openDialog = null;
    
    private ArrayList<DeleteListener> listeners = new ArrayList<DeleteListener>();
    private Entity THIS = this;
    
    protected int id;
    protected PropertiesPanel propertiesPanel;
    protected PropertiesDialog properties;
    protected Map map;
    
    /**
     * override to make sure the map and all related objects register the change to this objects name.
     */
    public void setName(String s){
        super.setName(s);
        map.update();
    }
    
    /**
     * centers this object onto the point represented by the x-/y-coordinates.
     */
    public void centerOn(int x, int y){
        setLocation(x - getWidth()/2, y - getHeight()/2);
    }
    
    /**
     * centers the object onto a point.
     */
    public void centerOn(Point p){
        centerOn(p.x, p.y);
    }
    
    /**
     * shifts this object to the x and y coordinates using the given point as an offset.
     */
    public void shift(int x, int y, Point offset){
        if(offset != null){
            int nx = getX(), ny = getY();
            nx += x - offset.x;
            ny += y - offset.y;
            setLocation(nx, ny);
        }
    }
    
    public void addDeleteListener(DeleteListener listener){
        listeners.add(listener);
    }
    
    public void removeDeleteListener(DeleteListener listener){
        listeners.remove(listeners.indexOf(listener));
    }
    
    public DeleteListener[] getDeleteListeners(){
        return listeners.toArray(new DeleteListener[listeners.size()]);
    }
    
    public void fireDelete(){
        map.addUnusedID(getType(), id);
        ActionEvent temp = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "delete");
        for(DeleteListener d : listeners){
            d.actionPerformed(temp);
        }
    }
    
    public abstract void paintComponent(Graphics g);
    
    public String getIDString(){
        return getName();
    }
    
    public PropertiesPanel getPropertiesPanel(){
        if(propertiesPanel == null){
            initPropertiesPanel();
        }
        return propertiesPanel;
    }
    
    protected abstract void initPropertiesPanel();
    
    /**
     * initializes this entity's id.
     */
    protected abstract void setID();
    
    public abstract int getType();
    
    public abstract boolean isToolActive();
    
    public int getID(){
        return id;
    }
    
    protected abstract void resetProperties();
    
    public PropertiesDialog getProperties(){
        if(properties == null){
            resetProperties();
        }
        return properties;
    }
    
    public void showProperties(Point p){
        properties.show(p.x, p.y);
    }
    
    public String toFileString(){
        return "NEWENTITY";
    }
    
    public abstract ArrayList<Object> format();
    
    public class PropertiesPanel extends JPanel{
        protected JPanel namePanel = new JPanel();
        protected JLabel nameLabel = new JLabel("Name:");
        protected JTextField nameField = new JTextField(10);
        
        protected JPanel locPanel = new JPanel();
        protected JLabel locLabel = new JLabel("Location:");
        protected JTextField locField = new JTextField();
        
        public PropertiesPanel(){
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            
            namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
            namePanel.add(nameLabel);
            namePanel.add(CM.chs(5));
            namePanel.add(nameField);
            add(namePanel);
            
            locPanel.setLayout(new BoxLayout(locPanel, BoxLayout.X_AXIS));
            locPanel.add(locLabel);
            locPanel.add(CM.chs(5));
            locField.setEditable(false);
            locPanel.add(locField);
            add(locPanel);
        }
        
        public void update(){
            nameField.setText(Entity.this.getName());
            locField.setText("(" + Entity.this.getX() + ", " + Entity.this.getY() + ")");
        }
        
        public void commit(){
            Entity.this.setName(nameField.getText());
        }
    }
    
    public class PropertiesDialog extends JInternalFrame{
        private JButton okButton = new JButton("OK");
        private JButton upButton = new JButton("Move Up");
        private JButton downButton = new JButton("Move Down");
        private JButton deleteButton = new JButton("Delete");
        private Entity owner;
        
        public PropertiesDialog(Entity own) throws IllegalArgumentException{
            super();
            setFrameIcon(null);
            if(own == null){
                throw new IllegalArgumentException("Owner must be non-null");
            }
            owner = own;
            
            getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
            
            add(getPropertiesPanel());
            
            upButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    map.moveUp(owner);
                }
            });
            
            downButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    map.moveDown(owner);
                }
            });
            
            deleteButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    fireDelete();
                }
            });
            
            okButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    propertiesPanel.commit();
                    propertiesPanel.update();
                    close();
                }
            });
            
            JPanel temp = new JPanel();
            temp.setLayout(new BoxLayout(temp, BoxLayout.X_AXIS));
            temp.add(upButton);
            temp.add(CM.chg());
            add(temp);
            
            temp = new JPanel();
            temp.setLayout(new BoxLayout(temp, BoxLayout.X_AXIS));
            temp.add(downButton);
            temp.add(CM.chg());
            add(temp);
            
            temp = new JPanel();
            temp.setLayout(new BoxLayout(temp, BoxLayout.X_AXIS));
            temp.add(deleteButton);
            temp.add(CM.chg());
            add(temp);
            
            temp = new JPanel();
            temp.setLayout(new BoxLayout(temp, BoxLayout.X_AXIS));
            temp.add(okButton);
            temp.add(CM.chg());
            add(temp);
            
            pack();
        }
        
        public void show(int x, int y){
            setLocation(x, y);
            propertiesPanel.update();
            if(openDialog != null && openDialog != this){
                openDialog.close();
            }
            openDialog = this;
            setVisible(true);
        }
        
        public void close(){
            setVisible(false);
            propertiesPanel.update();
            openDialog = null;
        }
    }
    
    public class DragListener extends MouseAdapter{
        private boolean isResize = false;
        private Point offset = null;
        private int  direction = 0;
        
        public void mousePressed(MouseEvent e){
            if(isToolActive()){
                Component c = e.getComponent();
                map.selectPanel(THIS);
                if(e.getButton() != MouseEvent.BUTTON2 && isToolActive()){
                    map.selectPanel((Entity) c);
                }
                if(e.getButton() == MouseEvent.BUTTON1){
                    if(c instanceof Resizable){
                        int ex = e.getX(), ey = e.getY(), cw = c.getWidth(), ch = c.getHeight(), temp = 8;
                        if((ex > 0 && ex < temp)||(ex > cw - temp && ex < cw)){
                            isResize = true;
                            if(ex > 0 && ex < temp){
                                direction = 3;
                            }else{
                                direction = 1;
                            }
                        }else if((ey > 0 && ey < temp)||(ey > ch - temp && ey < ch)){
                            isResize = true;
                            if(ey > 0 && ey < temp){
                                direction = 0;
                            }else{
                                direction = 2;
                            }
                        }else{
                            offset = e.getPoint();
                        }
                    }else{
                        offset = e.getPoint();
                    }
                }
                if(e.getButton() == MouseEvent.BUTTON3){
                    showProperties(MouseInfo.getPointerInfo().getLocation());
                }
            }else{
                e.translatePoint(getX(), getY());
                map.listener.mousePressed(e);
            }
        }
            
        public void mouseDragged(MouseEvent e){
            if(isToolActive()){
                int temp = 8;
                Entity ent = (Entity)e.getComponent();
                map.scrollRectToVisible(new Rectangle(new Point(getX() + e.getX(), getY() + e.getY()), new Dimension(10, 10)));
                if(!isResize){
                    int x = e.getX(), y = e.getY();
                    ent.shift(x, y, offset);
                }else{
                    int ex = e.getX(), ey = e.getY(), cx = ent.getX(), cy = ent.getY(), cw = ent.getWidth(), ch = ent.getHeight(),nx = 0, ny = 0, nw = 0, nh = 0;
                    switch(direction){
                        case 0:
                            nx = cx;
                            ny = cy + ey;
                            nw = cw;
                            nh = ch-= ey;
                            break;
                        case 1:
                            nx = cx;
                            ny = cy;
                            nw = ex;
                            nh = ch;
                            break;
                        case 2:
                            nx = cx;
                            ny = cy;
                            nw = cw;
                            nh = ey;
                            break;
                        case 3:
                            nx = cx + ex;
                            ny = cy;
                            nw = cw - ex;
                            nh = ch;
                            break;
                    }
                    if(nw <=  2*temp + 10|| nh <= 2*temp + 10){
                        repaint();
                        return;
                    }
                    ent.setBounds(nx, ny, nw, nh);
                }
                repaint();
            }else{
                e.translatePoint(getX(), getY());
                map.listener.mouseDragged(e);
            }
        }
            
        public void mouseReleased(MouseEvent e){
            isResize = false;
            offset = null;
            direction = 0;
            if(!isToolActive()){
                e.translatePoint(getX(), getY());
                map.listener.mouseReleased(e);
            }
        }
    }
}