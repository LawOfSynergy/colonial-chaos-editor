import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.swing.border.LineBorder;
import java.util.ArrayList;

public class SpawnPoint extends Entity{
    private BufferedImage image = null;
    private int unitTypeID = 0;
    private String unitTypeString = null;

    public SpawnPoint(Map map, String type, int x, int y){
        this.map = map;
        setID();
        properties = new PropertiesDialog(this);
        setUnitType(type);
        centerOn(x, y);
        setBorder(new LineBorder(Color.black, 1));
        DragListener drag = new DragListener();
        addMouseListener(drag);
        addMouseMotionListener(drag);
        setName("Unit " + id);
    }
    
    public SpawnPoint(Map map, ArrayList<Object> args){
        this.map = map;
        setID();
        properties = new PropertiesDialog(this);
        setUnitType((String)args.remove(0));
        setLocation((int)((Integer)args.remove(0)), (int)((Integer)args.remove(0)));
        setBorder(new LineBorder(Color.black, 1));
        DragListener drag = new DragListener();
        addMouseListener(drag);
        addMouseMotionListener(drag);
        setName("Unit " + id);
    }
    
    public void paintComponent(Graphics g){
        if(image == null){
            g.setColor(Color.red);
            g.fillRect(0, 0, getWidth(), getHeight());
        }else{
            g.drawImage(image.getScaledInstance(getWidth(), getHeight(), Image.SCALE_DEFAULT), 0, 0, null);
        }
    }
    
    public boolean isToolActive(){
        return map.isUnitToolActive() || map.isSelectionToolActive();
    }
    
    protected void setID(){
        id = map.getNextID(getType());
    }

    public int getUnitTypeID(){
        return unitTypeID;
    }
    
    public void setUnitType(String s){
        for(int i = 0; i < ToolPallete.unitTypes.length; i++){
            if(s.equals(ToolPallete.unitTypes[i])){
                setUnitType(i);
                return;
            }
        }
        setUnitType(0);
    }
    
    public void setUnitType(int i){
        unitTypeID = i;
        unitTypeString = ToolPallete.unitTypes[i];
        image = ToolPallete.unitImages[i];
        setSize(ToolPallete.unitSizes[i]);
        setName(unitTypeString + " " + id);
        propertiesPanel.update();
        repaint();
    }
    
    public int getType(){
        return 2;
    }
    
    public ArrayList<Object> format(){
        ArrayList<Object> temp = new ArrayList<Object>();
        temp.add(getClass().getName().toUpperCase());
        temp.add(ToolPallete.unitTypes[unitTypeID]);
        temp.add(getX());
        temp.add(getY());
        return temp;
    }
    
    protected void resetProperties(){
        properties = new PropertiesDialog(this);
    }
    
    protected void initPropertiesPanel(){
        propertiesPanel = new SpawnPointPropertiesPanel();
    }
    
    public class SpawnPointPropertiesPanel extends PropertiesPanel{
        protected JComboBox unitType = new JComboBox(ToolPallete.unitTypes);
        
        public SpawnPointPropertiesPanel(){
            super();
            add(unitType);
            unitType.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    commit();
                }
            });
        }
        
        public void update(){
            super.update();
            unitType.setSelectedIndex(unitTypeID);
        }
        
        public void commit(){
            super.commit();
            setUnitType(unitType.getSelectedIndex());
        }
    }
}
