import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.util.ArrayList;

/**
 * A class the defines how the user can interact with any open Maps. (Currently) Consists of three tools: 
 * the barrier, accelerator, and unit tools. each of these tools, when active, will allow the user to add objects of the 
 * corresponding type to the currently open map by either clicking or clicking and dragging (depending on the tool).
 */
public class ToolPallete extends JInternalFrame{
    //statics:
    /**
     * Strings representing all available tools.
     */
    public static String[] toolTypes = {
        "Barrier",
        "Accelerator",
        "Unit",
        "Selection"
    };
    
    /**
     * Strings representing the description for each tool.
     */
    public static String[] toolTips = {
        "Click and drag on create a new Barrier.",
        "Click and drag on empty space to create a new Accelerator.",
        "Click on empty space to create a new SpawnPoint.",
        "Allows the modification and placement of \nany entities that have already been placed."
    };
    
    /**
     * names of the different types of terrains.
     */
    public static String[] terrainTypes = {
        "Unknown"
    };
    
    /**
     * images for the terrain types for the barriers.
     */
    public static BufferedImage[] terrainImages = {
        null
    };
    
    /**
     * colors that can be used for accelerators
     */
    public static Color[] acceleratorColors = {
        Color.green,
        Color.black,
        Color.blue,
        Color.cyan,
        Color.darkGray,
        Color.gray,
        Color.lightGray,
        Color.magenta,
        Color.orange,
        Color.pink,
        Color.yellow
    };
    
    /**
     * string representations of the colors that can be used for accelerators
     */
    public static String[] acceleratorColorStrings = {
        "Green",
        "Black",
        "Blue",
        "Cyan",
        "Dark Gray",
        "Gray",
        "Light Gray",
        "Magenta",
        "Orange",
        "Pink",
        "Yellow"
    };
        
    /**
     * Strings representing the name of all supported unit types.
     */
    public static String[] unitTypes = {
        "Marker"
    };
    
    /**
     * descriptions of every unit type that is currently supported.
     */
    public static String[] unitDescriptions = {
        "A Marker type that represents that a unit should go there but whos type has not yet been set.\nAny of these that remain when the map is added to the server will be removed."
    };
    
    /**
     * the dimensions of every unit type that is currently supported.
     */
    public static Dimension[] unitSizes = {
        new Dimension(20, 20)
    };
    
    /**
     * the graphic representation of instances of each unit type.
     */
    public static BufferedImage[] unitImages = {
        null
    };
    
    private MainGui gui;
    private JPanel main = new JPanel();
    
    private int selectedToolID = 0;
    private String selectedToolString = toolTypes[0];
    private JPanel[] extras = new JPanel[toolTypes.length];
    private JComboBox tools = new JComboBox(toolTypes);
    private JTextArea toolTip = new JTextArea();
    
    /**
     * the one and only constructor.
     */
    public ToolPallete(MainGui gui){
        super("ToolPallete", false, true, false, true);
        this.gui = gui;
        
        //tools
        tools.setSelectedIndex(0);
        tools.addActionListener(new ToolListener());
        toolTip.setText(toolTips[0]);
        
        extras[0] = new BarrierPanel();
        extras[1] = new AccelPanel();
        extras[2] = new UnitPanel();
        extras[3] = new SelectionPanel();
        
        //all
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.add(tools);
        main.add(toolTip);
        main.add(extras[0]);
        getContentPane().add(main);
        pack();
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setLocation(getWidth(), 0);
        setVisible(true);
    }
    
    /**
     * determines whether or not the barrier tool is active.
     * 
     * @return true if the barrier tool is active. False otherwise.
     */
    public boolean isBarrierToolActive(){
        return selectedToolID == 0;
    }
    
    public int getTerrainType(){
        return ((BarrierPanel)extras[0]).getTerrainType();
    }
    
    public String getTerrainTypeString(){
        return ((BarrierPanel)extras[0]).getTerrainTypeString();
    }
    
    public boolean isAcceleratorToolActive(){
        return selectedToolID == 1;
    }
    
    public String getAccelName(){
        String temp = ((AccelPanel)extras[1]).getName();
        if(temp == null || temp.equals("")){
            return null;
        }
        return temp;
    }
    
    public double getAccelXFactor(){
        return ((AccelPanel)extras[1]).getXFactor();
    }
    
    public double getAccelYFactor(){
        return ((AccelPanel)extras[1]).getYFactor();
    }
    
    public Color getAccelColor(){
        return ((AccelPanel)extras[1]).getColor();
    }
    
    public int getAccelColorIndex(){
        return ((AccelPanel)extras[1]).getColorIndex();
    }
    
    public String getAccelColorString(){
        return ((AccelPanel)extras[1]).getColorString();
    }
    
    public void clearAccel(){
        ((AccelPanel)extras[1]).clear();
    }
    
    public boolean isUnitToolActive(){
        return selectedToolID == 2;
    }
    
    public String getSelectedUnitTypeString(){
        return ((UnitPanel)extras[2]).getSelectedUnitTypeString();
    }
    
    public int getSelectedUnitTypeID(){
        return ((UnitPanel)extras[2]).getSelectedUnitTypeID();
    }
    
    public boolean isSelectionToolActive(){
        return selectedToolID == 3;
    }
    
    public static void init(ArrayList<ArrayList<Object>> unitUpdates, ArrayList<ArrayList<Object>> terrainUpdates){
        try{
            ArrayList<String> terraNames = new ArrayList<String>();
            ArrayList<BufferedImage> tempTerrains = new ArrayList<BufferedImage>();
            terraNames.add("Unknown");
            tempTerrains.add(null);
            
            for(int i = 0; i < terrainUpdates.size(); i++){
                ArrayList<Object> temp = terrainUpdates.get(i);
                terraNames.add((String)temp.get(0));
                tempTerrains.add(ImageIO.read((File)temp.get(1)));
            }
            
            terrainTypes = terraNames.toArray(new String[terraNames.size()]);
            terrainImages = tempTerrains.toArray(new BufferedImage[tempTerrains.size()]);
            
            ArrayList<String> tempTypes = new ArrayList<String>();
            ArrayList<String> tempDescs = new ArrayList<String>();
            ArrayList<Dimension> tempSizes = new ArrayList<Dimension>();
            ArrayList<BufferedImage> tempImages = new ArrayList<BufferedImage>();
            
            tempTypes.add("Marker");
            tempDescs.add("A Marker type that represents that a unit should go there but whos type has not yet been set.\nAny of these that remain when the map is added to the server will be removed.");
            tempSizes.add(new Dimension(20, 20));
            tempImages.add(null);
            
            for(ArrayList<Object> u : unitUpdates){
                tempTypes.add((String)u.remove(0));
                tempDescs.add((String)u.remove(0));
                tempSizes.add((Dimension)u.remove(0));
                tempImages.add(ImageIO.read((File)u.remove(0)));
            }
            
            unitTypes = tempTypes.toArray(new String[tempTypes.size()]);
            unitDescriptions = tempDescs.toArray(new String[tempDescs.size()]);
            unitSizes = tempSizes.toArray(new Dimension[tempSizes.size()]);
            unitImages = tempImages.toArray(new BufferedImage[tempImages.size()]);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
        
    private class SelectionPanel extends JPanel{
        public SelectionPanel(){
            setVisible(false);
        }
    }
    
    private class BarrierPanel extends JPanel{
        private JComboBox terrain = new JComboBox(terrainTypes);
        private String terrainTypeString = terrainTypes[0];
        private int terrainTypeID = 0;
        
        public BarrierPanel(){
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            
            add(terrain);
            terrain.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    JComboBox cb = (JComboBox)e.getSource();
                    terrainTypeID = cb.getSelectedIndex();
                    terrainTypeString = terrainTypes[terrainTypeID];
                    ToolPallete.this.pack();
                    ToolPallete.this.repaint();
                }
            });
        }
        
        public int getTerrainType(){
            return terrainTypeID;
        }
        
        public String getTerrainTypeString(){
            return terrainTypeString;
        }
    }
    
    private class AccelPanel extends JPanel{
        private JLabel nameLabel = new JLabel("Name:");
        private JTextField nameField = new JTextField();
        private JLabel xFactorLabel = new JLabel("X Factor:");
        private JFormattedTextField xFactorField = new JFormattedTextField();
        private JLabel yFactorLabel = new JLabel("Y Factor:");
        private JFormattedTextField yFactorField = new JFormattedTextField();
        private JComboBox colorChooser = new JComboBox(acceleratorColorStrings);
        private JPanel preview = new JPanel(){
            public void paintComponent(Graphics g){
                BufferedImage temp = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics gi = temp.createGraphics();
                gi.setColor(selectedColor);
                gi.fillRect(0, 0, getWidth(), getHeight());
                float[] scales = { 1f, 1f, 1f, 0.5f };
                float[] offsets = new float[4];
                RescaleOp rop = new RescaleOp(scales, offsets, null);
                ((Graphics2D)g).drawImage(temp, rop, 0, 0);
            }
        };
        
        private int selectedColorIndex = 0;
        private Color selectedColor = Color.green;
        private String selectedColorString = "Green";
        
        public AccelPanel(){
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            JPanel temp = new JPanel();
            temp.setLayout(new BoxLayout(temp, BoxLayout.X_AXIS));
            temp.add(nameLabel);
            temp.add(CM.chs(5));
            nameField.setText("Accelerator");
            temp.add(nameField);
            add(temp);
            
            xFactorField.setValue(new Double(1.0));
            temp = new JPanel();
            temp.setLayout(new BoxLayout(temp, BoxLayout.X_AXIS));
            temp.add(xFactorLabel);
            temp.add(CM.chs(5));
            temp.add(xFactorField);
            add(temp);
            
            yFactorField.setValue(new Double(1.0));
            temp = new JPanel();
            temp.setLayout(new BoxLayout(temp, BoxLayout.X_AXIS));
            temp.add(yFactorLabel);
            temp.add(CM.chs(5));
            temp.add(yFactorField);
            add(temp);
            
            colorChooser.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    selectedColorIndex = colorChooser.getSelectedIndex();
                    selectedColor = acceleratorColors[selectedColorIndex];
                    selectedColorString = acceleratorColorStrings[selectedColorIndex];
                    preview.repaint();
                }
            });
            add(colorChooser);
            
            preview.setPreferredSize(new Dimension(0, 20));
            add(preview);
        }
        
        public String getName(){
            if(nameField.getText() == null || nameField.getText().equals("")){
                return null;
            }
            return nameField.getText();
        }
        
        public double getXFactor(){
            return (double)((Double)xFactorField.getValue());
        }
        
        public double getYFactor(){
            return (double)((Double)yFactorField.getValue());
        }
        
        public int getColorIndex(){
            return selectedColorIndex;
        }
        
        public Color getColor(){
            return selectedColor;
        }
        
        public String getColorString(){
            return selectedColorString;
        }
        
        public void clear(){
            nameField.setText("Accelerator");
        }
    }
    
    private class UnitPanel extends JPanel{
        private JComboBox units = new JComboBox(unitTypes);
        private JTextArea description = new JTextArea();
        private String selectedUnitString = unitTypes[0];
        private int selectedUnitID = 0;
        
        public UnitPanel(){
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            add(units);
            units.setSelectedIndex(0);
            units.addActionListener(new UnitListener());
            add(description);
            description.setText(unitDescriptions[0]);
        }
        
        public String getSelectedUnitTypeString(){
            return selectedUnitString;
        }
        
        public int getSelectedUnitTypeID(){
            return selectedUnitID;
        }
    
        private class UnitListener implements ActionListener{
            public void actionPerformed(ActionEvent e){
                JComboBox cb = (JComboBox)e.getSource();
                selectedUnitID = cb.getSelectedIndex();
                selectedUnitString = unitTypes[selectedUnitID];
                description.setText(unitDescriptions[selectedUnitID]);
                ToolPallete.this.pack();
                ToolPallete.this.repaint();
            }
        }
    }
    
    public class ToolListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            JComboBox cb = (JComboBox)e.getSource();
            selectedToolID = cb.getSelectedIndex();
            selectedToolString = toolTypes[selectedToolID];
            for(JPanel p : extras){
                main.remove(p);
            }
            main.add(extras[selectedToolID]);
            toolTip.setText(toolTips[selectedToolID]);
            gui.selectTool(selectedToolID);
            pack();
            repaint();
        }
    }
}
