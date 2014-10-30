import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.util.ArrayList;

public class Accelerator extends Entity implements Resizable{
    private double xFactor;
    private double yFactor;
    private int colorIndex = 0;
    private Color color;
    
    public Accelerator(Map map, int x, int y, int w, int h, String name, double xFactor, double yFactor, String colorString){
        this.map = map;
        this.xFactor = xFactor;
        this.yFactor = yFactor;
        setColor(colorString);
        setID();
        setName(name + " " + id);
        setLocation(x, y);
        if(w < 30){
            w = 30;
        }
        if(h < 30){
            h = 30;
        }
        setSize(w, h);
        setOpaque(false);
        setBorder(new LineBorder(Color.black, 8));
        setVisible(true);
        propertiesPanel = new AcceleratorPropertiesPanel();
        propertiesPanel.update();
        properties = new PropertiesDialog(this);
        DragListener drag = new DragListener();
        addMouseListener(drag);
        addMouseMotionListener(drag);
    }
    
    public Accelerator(Map map, Rectangle rect, String name, double xf, double yf, String colorString){
        this(map, rect.x, rect.y, rect.width, rect.height, name, xf, yf, colorString);
    }
    
    public Accelerator(Map map, ArrayList<Object> args){
        this(map, (Rectangle)args.remove(0), (String) args.remove(0), (double)((Double)args.remove(0)), (double)((Double)args.remove(0)), (String)args.remove(0));
    }
    
    public boolean isToolActive(){
        return map.isAcceleratorToolActive() || map.isSelectionToolActive();
    }
    
    protected void setID(){
        id = map.getNextID(getType());
    }
    
    protected void resetProperties(){
        properties = new PropertiesDialog(this);
    }

    public void paintComponent(Graphics g){
        BufferedImage temp = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics tempg = temp.createGraphics();
        tempg.setColor(color);
        tempg.fillRect(0, 0, getWidth(), getHeight());
        float[] scales = { 1f, 1f, 1f, 0.5f };
        float[] offsets = new float[4];
        RescaleOp rop = new RescaleOp(scales, offsets, null);
        ((Graphics2D)g).drawImage(temp, rop, 0, 0);
    }
    
    public int getType(){
        return 1;
    }
    
    public ArrayList<Object> format(){
        ArrayList<Object> temp = new ArrayList<Object>();
        temp.add(getClass().getName().toUpperCase());
        temp.add(getBounds());
        temp.add(getName());
        temp.add(new Double(xFactor));
        temp.add(new Double(yFactor));
        temp.add(ToolPallete.acceleratorColorStrings[colorIndex]);
        return temp;
    }
    
    public void initPropertiesPanel(){
        propertiesPanel = new AcceleratorPropertiesPanel();
        propertiesPanel.update();
    }
    
    public void setColor(String s){
        int temp = 0;
        for(int i = 0; i < ToolPallete.acceleratorColorStrings.length; i++){
            String t = ToolPallete.acceleratorColorStrings[i];
            if(t.equals(s)){
                temp = i;
                break;
            }
        }
        setColor(temp);
    }
    
    public void setColor(int i){
        colorIndex = i;
        color = ToolPallete.acceleratorColors[i];
        repaint();
    }
    
    public class AcceleratorPropertiesPanel extends PropertiesPanel{
        protected JPanel sizePanel = new JPanel();
        protected JLabel sizeLabel = new JLabel("Size:");
        protected JTextField sizeField = new JTextField();
        
        protected JPanel xFactorPanel = new JPanel();
        protected JLabel xFactorLabel = new JLabel("X Factor:");
        protected JFormattedTextField xFactorField = new JFormattedTextField();
        
        protected JPanel yFactorPanel = new JPanel();
        protected JLabel yFactorLabel = new JLabel("Y Factor:");
        protected JFormattedTextField yFactorField = new JFormattedTextField();
        
        protected JComboBox colorChooser = new JComboBox(ToolPallete.acceleratorColorStrings);
        
        public AcceleratorPropertiesPanel(){
            super();
            
            sizePanel.setLayout(new BoxLayout(sizePanel, BoxLayout.X_AXIS));
            sizePanel.add(sizeLabel);
            sizePanel.add(CM.chs(5));
            sizeField.setEditable(false);
            sizePanel.add(sizeField);
            add(sizePanel);
            
            xFactorPanel.setLayout(new BoxLayout(xFactorPanel, BoxLayout.X_AXIS));
            xFactorPanel.add(xFactorLabel);
            xFactorPanel.add(CM.chs(5));
            xFactorField.setValue(new Double(1.0));
            xFactorPanel.add(xFactorField);
            add(xFactorPanel);
            
            yFactorPanel.setLayout(new BoxLayout(yFactorPanel, BoxLayout.X_AXIS));
            yFactorPanel.add(yFactorLabel);
            yFactorPanel.add(CM.chs(5));
            yFactorField.setValue(new Double(1.0));
            yFactorPanel.add(yFactorField);
            add(yFactorPanel);
            
            colorChooser.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    commitColor();
                }
            });
            add(colorChooser);
            
            update();
        }
        
        public void update(){
            super.update();
            sizeField.setText(Accelerator.this.getWidth() + "x" + Accelerator.this.getHeight());
            xFactorField.setValue(new Double(xFactor));
            yFactorField.setValue(new Double(yFactor));
            colorChooser.setSelectedIndex(colorIndex);
        }
        
        public void commit(){
            super.commit();
            xFactor = (double)((Double)xFactorField.getValue());
            yFactor = (double)((Double)yFactorField.getValue());
            commitColor();
        }
        
        public void commitColor(){
            colorIndex = colorChooser.getSelectedIndex();
            color = ToolPallete.acceleratorColors[colorIndex];
            Accelerator.this.repaint();
        }
    }
}
