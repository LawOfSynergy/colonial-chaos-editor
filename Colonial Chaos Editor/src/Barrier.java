import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.swing.border.LineBorder;
import java.util.ArrayList;

public class Barrier extends Entity implements Resizable{
    private int terrainType;
    private String terrainString;
    private BufferedImage image;

    public Barrier(Map map, int x, int y, int w, int h, int type){
        this.map = map;
        setID();
        properties = new PropertiesDialog(this);
        setBorder(new LineBorder(Color.black, 8));
        setLocation(x, y);
        setOpaque(false);
        if(w < 30){
            w = 30;
        }
        if(h < 30){
            h = 30;
        }
        setSize(w, h);
        setTerrain(type);
        setVisible(true);
        DragListener drag = new DragListener();
        addMouseListener(drag);
        addMouseMotionListener(drag);
        setName("Barrier " + id);
    }
    
    public Barrier(Map map, int x, int y, int w, int h, String type){
        this(map, x, y, w, h, 0);
        setTerrain(type);
    }
    
    public Barrier(Map map, Rectangle rect, String type){
        this(map, rect.x, rect.y, rect.width, rect.height, type);
    }
    
    public Barrier(Map map, ArrayList<Object> args){
        this(map, (Rectangle)args.remove(0), (String)args.remove(0));
    }
    
    public boolean isToolActive(){
        return map.isBarrierToolActive() || map.isSelectionToolActive();
    }
    
    protected void setID(){
        id = map.getNextID(0);
    }
    
    public void setTerrain(String type){
        int index = -1;
        for(int i = 0; i < ToolPallete.terrainTypes.length; i++){
            if(ToolPallete.terrainTypes[i].equals(type)){
                index = i;
            }
        }
        setTerrain(index);
    }
    
    public void setTerrain(int type){
        if(type == -1){
            type = 0;
        }
        terrainType = type;
        terrainString = ToolPallete.terrainTypes[type];
        image = ToolPallete.terrainImages[type];
    }
    
    protected void resetProperties(){
        properties = new PropertiesDialog(this);
    }

    public void paintComponent(Graphics g){
        if(image == null){
            g.setColor(Color.blue);
            g.fillRect(0, 0, getWidth(), getHeight());
        }else{
            int width = getWidth(), height = getHeight();//get this entity's width and height
            int iw = image.getWidth(), ih = image.getHeight();//gets the images width and height
            int nhi = (int)((double)width/iw + 0.5);
            int nvi = (int)((double)height/iw + 0.5);
            BufferedImage temp = new BufferedImage(nhi*iw, nvi*ih, BufferedImage.TYPE_INT_ARGB);
            Graphics tempg = temp.createGraphics();
            for(int v = 0; v < nvi; v++){
                for(int h = 0; h < nhi; h++){
                    tempg.drawImage(image, h*iw, v*ih, null);
                }
            }
            g.drawImage(temp.getScaledInstance(width, height, Image.SCALE_DEFAULT), 0, 0, null);
        }
    }

    public String getIDString(){
        return getName();
    }
    
    public int getType(){
        return 0;
    }
    
    public ArrayList<Object> format(){
        ArrayList<Object> temp = new ArrayList<Object>();
        temp.add(getClass().getName().toUpperCase());
        temp.add(getBounds());
        temp.add(terrainString);
        return temp;
    }
    
    public void initPropertiesPanel(){
        propertiesPanel = new BarrierPropertiesPanel();
        propertiesPanel.update();
    }
    
    public class BarrierPropertiesPanel extends PropertiesPanel{
        protected JPanel sizePanel = new JPanel();
        protected JLabel sizeLabel = new JLabel("Size:");
        protected JTextField sizeField = new JTextField();
        protected JComboBox terrains = new JComboBox(ToolPallete.terrainTypes);
        
        public BarrierPropertiesPanel(){
            super();
            
            sizePanel.setLayout(new BoxLayout(sizePanel, BoxLayout.X_AXIS));
            sizePanel.add(sizeLabel);
            sizePanel.add(CM.chs(5));
            sizeField.setEditable(false);
            sizePanel.add(sizeField);
            add(sizePanel);
            
            add(terrains);
            terrains.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    commit();
                }
            });
        }
        
        public void update(){
            super.update();
            sizeField.setText(Barrier.this.getWidth() + "x" + Barrier.this.getHeight());
            terrains.setSelectedIndex(terrainType);
        }
        
        public void commit(){
            super.commit();
            setTerrain(terrains.getSelectedIndex());
        }
    }
}