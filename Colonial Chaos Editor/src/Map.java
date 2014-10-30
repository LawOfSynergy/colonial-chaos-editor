import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.util.ArrayList;
import java.util.Collections;

public class Map extends JPanel implements Scrollable{
    private static ArrayList<MapProperty> supportedMapProperties = new ArrayList<MapProperty>();
    
    public MainGui gui;
    public AddListener listener;
    
    private Map THIS = this;
    
    private JScrollPane viewer;
    
    private ArrayList<Entity> entities;
    private ArrayList<Accelerator> accelerators;
    private ArrayList<Barrier> barriers;
    private ArrayList<SpawnPoint> units;
    private ArrayList<ArrayList<? extends Entity>> allGroups = new ArrayList<ArrayList<? extends Entity>>();
    
    private ArrayList<ArrayList<Integer>> entityIDs = new ArrayList<ArrayList<Integer>>();
    private int[] lastEntityIDs = new int[3];
    
    private MapPropertiesDialog properties;
    private ViewProperties view;
    private TabComponent tab;
    private Entity selectedPanel = null;

    /**
     * default constructor. Maps must have a reference to the MainGui that they belong to.
     */
    public Map(MainGui gui){
        setLayout(null);
        this.gui = gui;
        setName("Untitled");
        properties = new MapPropertiesDialog();
        view = new ViewProperties();
        listener = new AddListener();
        addMouseListener(listener);
        addMouseMotionListener(listener);
        entities = new ArrayList<Entity>();
        accelerators = new ArrayList<Accelerator>();
        barriers = new ArrayList<Barrier>();
        units = new ArrayList<SpawnPoint>();
        entityIDs.add(new ArrayList<Integer>());
        entityIDs.add(new ArrayList<Integer>());
        entityIDs.add(new ArrayList<Integer>());
        
        allGroups.add(units);
        allGroups.add(accelerators);
        allGroups.add(barriers);
    }
    
    public void setViewer(JScrollPane p){
        viewer = p;
    }
    
    public JScrollPane getViewer(){
        return viewer;
    }
    
    public void debug(){
        System.out.println(allGroups);
        System.out.println(units);
        System.out.println(accelerators);
        System.out.println(barriers);
        System.out.println(entities);
    }
    
    /**
     * constructor that creates a map using custom parameters.
     */
    public Map(MainGui gui, ArrayList<Object> args){
        this(gui);
        setName((String)args.remove(0));
        setBounds((Rectangle) args.remove(0));
    }
    
    /**
     * paints the component. should not be called directly.
     */
    public void paintComponent(Graphics g){
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());
    }
    
    /**
     * adds an entity to this map.
     */
    public void add(Entity e){
        gui.getLayeredPane().add(e.getProperties(), new Integer(200));
        view.add(e);
        e.addDeleteListener(new DeleteListener(){
            public void actionPerformed(ActionEvent ev){
                remove((Entity)ev.getSource());
            }
        });
        if(e instanceof SpawnPoint){
            add((SpawnPoint)e);
        }else if(e instanceof Accelerator){
            add((Accelerator) e);
        }else if(e instanceof Barrier){
            add((Barrier)e);
        }
    }
    
    /**
     * removes an entity from this map.
     */
    public void remove(Entity e){
        gui.getLayeredPane().remove(e.getProperties());
        view.remove(e);
        if(e instanceof SpawnPoint){
            remove((SpawnPoint)e);
        }else if(e instanceof Accelerator){
            remove((Accelerator) e);
        }else if(e instanceof Barrier){
            remove((Barrier)e);
        }
    }
    
    /**
     * removes the currently selected entity
     */
    public void removeCurrentPanel(){
        if(selectedPanel != null){
            remove(selectedPanel);
            update();
        }
    }
    
    /**
     * selectes an entity
     */
    public void selectPanel(Entity e){
        selectedPanel = e;
    }
    
    /**
     * adds an id to a collection of available ids for an entity of a certain class.
     * @param type the numeric representation of the class to which the id belongs.
     * @param id the id to be added to the list of currently available ids.
     */
    public void addUnusedID(int type, int id){
        entityIDs.get(type).add(new Integer(id));
        Collections.sort(entityIDs.get(type));
    }
    
    /**
     * gets a new id for an object specific to the type.
     * @param type the numeric representation of the class to which the requesting object belongs.
     */
    public int getNextID(int type){
        if(entityIDs.get(type).size() == 0){
            return lastEntityIDs[type]++;
        }else{
            return entityIDs.get(type).remove(0);
        }
    }
    
    /**
     * moves the entity up one layer.
     */
    public void moveUp(Entity e){
        if(e instanceof SpawnPoint){
            moveUp((SpawnPoint)e);
        }else{
            moveUp((Barrier)e);
        }
    }
    
    /**
     * moves the entity down one layer.
     */
    public void moveDown(Entity e){
        if(e instanceof SpawnPoint){
            moveDown((SpawnPoint)e);
        }else{
            moveDown((Barrier)e);
        }
    }
    
    private void add(Barrier b){
        barriers.add(0, b);
        update();
    }
    
    private void add(Accelerator a){
        accelerators.add(0, a);
        update();
    }
    
    private void add(SpawnPoint s){
        units.add(0, s);
        update();
    }
    
    private void remove(Barrier b){
        barriers.remove(barriers.indexOf(b));
        update();
    }
    
    private void remove(Accelerator a){
        accelerators.remove(accelerators.indexOf(a));
        update();
    }
    
    private void remove(SpawnPoint s){
        units.remove(units.indexOf(s));
        update();
    }
    
    private void moveUp(Barrier b){
        int pos = barriers.indexOf(b);
        if(pos > 0){
            barriers.remove(pos);
            barriers.add(pos - 1, b);
        }
        update();
    }
    
    private void moveUp(Accelerator a){
        int pos = accelerators.indexOf(a);
        if(pos > 0){
            accelerators.remove(pos);
            accelerators.add(pos - 1, a);
        }
        update();
    }
    
    private void moveUp(SpawnPoint s){
        int pos = units.indexOf(s);
        if(pos > 0){
            units.remove(pos);
            units.add(pos - 1, s);
        }
        update();
    }
    
    private void moveDown(Barrier b){
        int pos = barriers.indexOf(b);
        if(pos < barriers.size() - 1 && pos >= 0){
            barriers.remove(pos);
            barriers.add(pos + 1, b);
        }
        update();
    }
    
    private void moveDown(Accelerator a){
        int pos = accelerators.indexOf(a);
        if(pos < accelerators.size() - 1 && pos >= 0){
            accelerators.remove(pos);
            accelerators.add(pos + 1, a);
        }
        update();
    }
    
    private void moveDown(SpawnPoint s){
        int pos = units.indexOf(s);
        if(pos < units.size() - 1 && pos >= 0){
            units.remove(pos);
            units.add(pos + 1, s);
        }
        update();
    }
    
    public void selectTool(int tool){
        int i = -1;
        allGroups = new ArrayList<ArrayList<? extends Entity>>();
        switch(tool){
            case 0: 
                allGroups.add(barriers);
            case 1: 
                allGroups.add(accelerators);
            case 3:
            case 2:
                allGroups.add(units);
        }
        if(allGroups.indexOf(accelerators) == -1){
            allGroups.add(accelerators);
        }
        if(allGroups.indexOf(barriers) == -1){
            allGroups.add(barriers);
        }
        update();
    }
    
    /**
     * updates the map. called after any change to a unit's layer or the map's contents (added/removed entities) or when an entity's string representation has changed.
     */
    public void update(){
        entities = new ArrayList<Entity>();
        removeAll();
        for(ArrayList<? extends Entity> group : allGroups){
            for(Entity e : group){
                super.add(e);
                entities.add(e);
            }
        }
        repaint();
        revalidate();
        gui.repaint();
        view.updateAll();
    }
    
    /**
     * updates a specific map property.
     */
    public void updateProperty(ArrayList<Object> args){
        properties.updateProperty(args);
    }
    
    /**
     * updates the map's name and bounds.
     */
    public void update(String s, int w, int h){
        setName(s);
        setSize(w, h);
        tab.update();
    }
    
    /**
     * used to implement a scroll-saavy client
     */
    public int getScrollableUnitIncrement(Rectangle rect, int orientation, int direction){
        return 10;
    }
    
    /**
     * used to implement a scroll-saavy client
     */
    public int getScrollableBlockIncrement(Rectangle rect, int orientation, int direction){
        return 100;
    }
    
    /**
     * used to implement a scroll-saavy client
     */
    public boolean getScrollableTracksViewportWidth(){
        return false;
    }
    
    /**
     * used to implement a scroll-saavy client
     */
    public boolean getScrollableTracksViewportHeight(){
        return false;
    }
    
    /**
     * used to implement a scroll-saavy client
     */
    public Dimension getPreferredScrollableViewportSize(){
        return getPreferredSize();
    }
    
    /**
     * overridden to set both size and preferred size.
     */
    public void setSize(int w, int h){
        super.setSize(w, h);
        setPreferredSize(new Dimension(w, h));
    }
    
    /**
     * overridden to set both size and preferred size.
     */
    public void setSize(Dimension d){
        super.setSize(d);
        setPreferredSize(d);
    }
    
    /**
     * overridden to set both the bounds and preferred size.
     */
    public void setBounds(int x, int y, int w, int h){
        super.setBounds(x, y, w, h);
        setPreferredSize(new Dimension(w, h));
    }
    
    /**
     * overridden to set both the bounds and preferred size.
     */
    public void setBounds(Rectangle r){
        super.setBounds(r);
        setPreferredSize(r.getSize());
    }
    
    /**
     * gets this map's tab component.
     */
    public TabComponent getTab(){
        if(tab == null){
            tab = new TabComponent();
        }
        return tab;
    }
    
    /**
     * shows this map's properties dialog.
     */
    public void showViewProperties(boolean b){
        view.setVisible(b);
    }
    
    /**
     * gets this map's properties dialog.
     */
    public MapPropertiesDialog getProperties(){
        return properties;
    }
    
    /**
     * formats the map for storage in a .ser file.
     */
    public ArrayList<Object> format(){
        ArrayList<Object> temp = new ArrayList<Object>();
        temp.add(getName());
        temp.add(getBounds());
        return temp;
    }
    
    /**
     * gets the structure for this map, every property, and every entity representing the format that this map is saved in.
     */
    public ArrayList<Object> getFormatStructure(){
        ArrayList<Object> temp = new ArrayList<Object>();
        temp.add("MAP");
        for(Object e : properties.getPropertiesFormat()){
            temp.add(e);
        }
        for(Entity e : entities){
            temp.add(e.toFileString());
        }
        //don't forget to replace!
        return temp;
    }
    
    /**
     * formats every property and entity belonging to this map.
     */
    public ArrayList<ArrayList<Object>> formatAll(){
        selectTool(3);
        ArrayList<ArrayList<Object>> temp = new ArrayList<ArrayList<Object>>();
        temp.add(getFormatStructure());
        temp.add(format());
        for(ArrayList<Object> e : properties.format()){
            temp.add(e);
        }
        for(Entity e : entities){
            temp.add(e.format());
        }
        //don't forget to replace!
        return temp;
    }
    
    /**
     * saves this map to a .ser file named according to the name of this map.
     */
    public void save(){
        System.out.println("Map.save()");
        System.out.println(formatAll());
        try{
            File file = new File(getName() + ".ser");
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(formatAll());
            out.close();
            JOptionPane.showInternalMessageDialog(gui.getContentPane(), getName() + " was saved to: " + file.getAbsolutePath());
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    /**
     * initializes properties required by every map.
     */
    public static void init(ArrayList<ArrayList<String>> properties){
        for(ArrayList<String> p : properties){
            supportedMapProperties.add(new MapProperty(p.remove(0), p.remove(0), p.remove(0), p.remove(0)));
        }
    }
    
    /**
     * used so that the entities can check if their respective tool is currently active.
     */
    public boolean isBarrierToolActive(){
        return gui.isBarrierToolActive();
    }
    
    /**
     * used so that the entities can check if their respective tool is currently active.
     */
    public boolean isAcceleratorToolActive(){
        return gui.isAcceleratorToolActive();
    }
    
    /**
     * used so that the entities can check if their respective tool is currently active.
     */
    public boolean isUnitToolActive(){
        return gui.isUnitToolActive();
    }
    
    /**
     * used so that entities can check if the selection tool is active.
     */
    public boolean isSelectionToolActive(){
        return gui.isSelectionToolActive();
    }
    
    /**
     * an object representing one of this map's properties.
     */
    public static class MapProperty extends JPanel{
        private String[] args;
        private JLabel name;
        private JTextField string;
        private JFormattedTextField value;
        
        public MapProperty(String name, String type, String base, String description) throws IllegalArgumentException{
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            String[] tempArgs = {name, type, base, description};
            args = tempArgs;
            this.name = new JLabel(name + ": ");
            value = new JFormattedTextField();
            add(this.name);
            add(CM.chg());
            add(value);
            setToolTipText(description);
            if(type.equals("boolean")){
                value.setValue(new Boolean(Boolean.parseBoolean(base)));
            }else if(type.equals("int")){
                value.setValue(new Integer(Integer.parseInt(base)));
            }else if(type.equals("double")){
                value.setValue(new Double(Double.parseDouble(base)));
            }else if(type.equals("String")){
                string = new JTextField();
                string.setText(base);
                remove(value);
                value = null;
                add(string);
            }else{
                throw new IllegalArgumentException("type must be either \"boolean\", \"int\", \"double\", or \"String\""); 
            }
        }
        
        /**
         * gets this property's name.
         */
        public String getName(){
            return args[0];
        }
        
        /**
         * clones this property.
         */
        public MapProperty clone(){
            return new MapProperty(args[0], args[1], args[2], args[3]);
        }
        
        /**
         * updates this property.
         */
        public void updateProperty(ArrayList<Object> args){
            String type = (String)args.remove(0);
            if(type.equals(this.args[1])){
                if(string != null){
                    string.setText((String)args.remove(0));
                }else{
                    if(type.equals("boolean")){
                        value.setValue((Boolean)args.remove(0));
                    }else if(type.equals("int")){
                        value.setValue((Integer)args.remove(0));
                    }else if(type.equals("double")){
                        value.setValue((Double)args.remove(0));
                    }
                }
            }
        }
        
        /**
         * formats this property to be saved into a .ser file.
         */
        public ArrayList<Object> format(){
            ArrayList<Object> temp = new ArrayList<Object>();
            if(string != null){
                temp.add(string.getText());
            }else if(value != null){
                temp.add(value.getValue());
            }else{
                throw new NullPointerException();
            }
            return temp;
        }
        
        /**
         * gets a string representation of this property as it would be saved into a text file.
         */
        public String toFileString(){
            return "MAPPROPERTY/./" + args[0] + "/./" + args[1];//args[1] is so that the loader can check to see that the property is supported by the 
            //current update. it prevents type conflicts for properties that share the same name by eliminating anything that matches by name but not by type
        }
    }
    
    public class MapPropertiesDialog extends JInternalFrame{
        private JPanel main;
        private JLabel nameLabel;
        private JTextField nameField;
        private JLabel widthLabel;
        private JFormattedTextField widthField;
        private JLabel heightLabel;
        private JFormattedTextField heightField;
        private ArrayList<MapProperty> mapProperties;
        private JButton cancel;
        private JButton accept;
        
        /**
         * the one and only constructor.
         */
        public MapPropertiesDialog(){
            super("Map Properties", false, true, false, false);
            main = new JPanel();
            main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
            
            JPanel temp = new JPanel();
            temp.setLayout(new BoxLayout(temp, BoxLayout.X_AXIS));
            
            nameLabel = new JLabel("Name:");
            nameField = new JTextField(20);
            nameField.setText(THIS.getName());
            temp.add(nameLabel);
            temp.add(CM.chs(5));
            temp.add(nameField);
            main.add(temp);
            
            temp = new JPanel();
            temp.setLayout(new BoxLayout(temp, BoxLayout.X_AXIS));
            
            widthLabel = new JLabel("Width:");
            widthField = new JFormattedTextField();
            widthField.setValue(new Integer(THIS.getWidth()));
            temp.add(widthLabel);
            temp.add(CM.chs(5));
            temp.add(widthField);
            main.add(temp);
            
            temp = new JPanel();
            temp.setLayout(new BoxLayout(temp, BoxLayout.X_AXIS));
            
            heightLabel = new JLabel("Height:");
            heightField = new JFormattedTextField();
            heightField.setValue(new Integer(THIS.getHeight()));
            temp.add(heightLabel);
            temp.add(CM.chs(5));
            temp.add(heightField);
            main.add(temp);
            
            temp = new JPanel();
            temp.setLayout(new BoxLayout(temp, BoxLayout.Y_AXIS));
            mapProperties = new ArrayList<MapProperty>();
            for(MapProperty e : supportedMapProperties){
                MapProperty prop = e.clone();
                mapProperties.add(prop);
                temp.add(prop);
            }
            JScrollPane tempPane = new JScrollPane(temp, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            main.add(tempPane);
            
            temp = new JPanel();
            temp.setLayout(new BoxLayout(temp, BoxLayout.X_AXIS));
            
            cancel = new JButton("Cancel");
            cancel.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    reset();
                    close();
                }
            });
            
            accept = new JButton("Accept");
            accept.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    update();
                    close();
                }
            });
            
            gui.addPaneListener(new ChangeListener(){
                public void stateChanged(ChangeEvent e){
                    setVisible(false);
                    reset();
                }
            });
            
            temp.add(CM.chg());
            temp.add(cancel);
            temp.add(CM.chs(10));
            temp.add(accept);
            main.add(temp);
            getContentPane().add(main);
            setDefaultCloseOperation(HIDE_ON_CLOSE);
            pack();
        }
        
        public void pack(){
            super.pack();
            setSize(getWidth() + 100, getHeight());
            setPreferredSize(getSize());
            setMinimumSize(getSize());
            setMaximumSize(getSize());
        }
        
        /**
         * reverts the values in the text fields to this maps current name and dimensions.
         */
        public void reset(){
            nameField.setText(THIS.getName());
            widthField.setValue(new Integer(THIS.getWidth()));
            heightField.setValue(new Integer(THIS.getHeight()));
        }
        
        /**
         * updates this map's properties to the values input by the user.
         */
        public void update(){
            THIS.update(nameField.getText(), (Integer)widthField.getValue(), (Integer)heightField.getValue());
        }
        
        /**
         * hides and updates this properties dialog.
         */
        public void close(){
            setVisible(false);
            update();
        }
        
        /**
         * gets the formatting structure of each map property in preparation for being saved to a .ser file.
         */
        public ArrayList<Object> getPropertiesFormat(){
            ArrayList<Object> temp = new ArrayList<Object>();
            for(MapProperty e : mapProperties){
                temp.add(e.toFileString());
            }
            return temp;
        }
        
        /**
         * formats each property for  being saved to a .ser file.
         */
        public ArrayList<ArrayList<Object>> format(){
            ArrayList<ArrayList<Object>> temp = new ArrayList<ArrayList<Object>>();
            for(MapProperty e : mapProperties){
                temp.add(e.format());
            }
            return temp;
        }
        
        /**
         * updates a property.
         */
        public void updateProperty(ArrayList<Object> args){
            String s = (String)args.remove(0);
            for(MapProperty m : mapProperties){
                if(s.equals(m.getName())){
                    m.updateProperty(args);
                    return;
                }
            }
        }
    }
    
    /**
     * this maps's tab component.
     */
    public class TabComponent extends JPanel{
        private JLabel label;
        private JButton close;
        
        public TabComponent(){
            super(new FlowLayout(FlowLayout.LEFT, 0, 0));
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
            label = new JLabel(THIS.getName());
            close = new CloseButton();
            add(label);
            add(CM.chs(5));
            add(close);
            setVisible(true);
        }
        
        public void update(){
            label.setText(THIS.getName());
        }
        
        private class CloseButton extends JButton implements ActionListener {
            public CloseButton() {
                int size = 17;
                setPreferredSize(new Dimension(size, size));
                setToolTipText("Close this tab");
                setUI(new BasicButtonUI());
                setContentAreaFilled(false);
                setFocusable(false);
                setBorder(BorderFactory.createEtchedBorder());
                setBorderPainted(false);
                addMouseListener(new MouseAdapter(){
                    public void mouseEntered(MouseEvent e) {
                        AbstractButton button = (AbstractButton) e.getComponent();
                        button.setBorderPainted(true);
                    }

                    public void mouseExited(MouseEvent e) {
                        AbstractButton button = (AbstractButton) e.getComponent();
                        button.setBorderPainted(false);
                    }
                });
                setRolloverEnabled(true);
                addActionListener(this);
            }

            public void actionPerformed(ActionEvent e) {
                gui.close(THIS.getTab());
            }
            
            public void updateUI() {}
            
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.red);
                g.fillRect(0, 0, getWidth(), getHeight());
                Graphics2D g2 = (Graphics2D) g.create();
                //shift the image for pressed buttons
                if (getModel().isPressed()) {
                    g2.translate(1, 1);
                }
                g2.setStroke(new BasicStroke(2));
                g2.setColor(Color.BLACK);
                if (getModel().isRollover()) {
                    g2.setColor(Color.MAGENTA);
                }
                int delta = 6;
                g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
                g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
                g2.dispose();
            }
        }
    }
    
    /**
     * MouseAdapter that listens for when the user tries to add an entity.
     */
    public class AddListener extends MouseAdapter{
        private Point origin = null;
        
        public void mousePressed(MouseEvent e){
            if(!isSelectionToolActive()){
                if(e.getButton() == MouseEvent.BUTTON1 && (gui.isBarrierToolActive() || (gui.isAcceleratorToolActive() && gui.getAccelName() != null && gui.getAccelXFactor() > 0 && gui.getAccelYFactor() > 0))){
                    origin = e.getPoint();
                }
            }
        }
        
        public void mouseReleased(MouseEvent me){
            if(!isSelectionToolActive()){
                if(me.getButton() == MouseEvent.BUTTON1){
                    if(gui.isUnitToolActive()){
                        SpawnPoint temp = new SpawnPoint(THIS, gui.getSelectedUnitType(), me.getX(), me.getY());
                        add((Entity)temp);
                        selectedPanel = temp;
                        return;
                    }else if(origin != null){
                        int nx = 0, ny = 0, nw = 0, nh = 0;
                        int mx = me.getX(), my = me.getY(), ox = origin.x, oy = origin.y;
                        if(mx > ox){
                            nx = ox;
                            nw = mx - ox;
                        }else if(mx < ox){
                            nx = mx;
                            nw = ox - mx;
                        }else{
                            nx = mx;
                        }
                        if(my > oy){
                            ny = oy;
                            nh = my - oy;
                        }else if(my < oy){
                            ny = my;
                            nh = oy - my;
                        }else{
                            ny = my;
                        }
                        Entity b = null;
                        if(gui.isBarrierToolActive()){
                            b = new Barrier(Map.this, nx, ny, nw, nh, gui.getTerrainType());
                        }else if(gui.isAcceleratorToolActive()){
                            b = new Accelerator(Map.this, nx, ny, nw, nh, gui.getAccelName(), gui.getAccelXFactor(), gui.getAccelYFactor(), gui.getAccelColorString());
                            gui.clearAccel();
                        }
                        add(b);
                        selectedPanel = b;
                    }
                }
                origin = null;
            }
        }
            
        public void mouseDragged(MouseEvent e) {
            if(!isSelectionToolActive()){
                Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
                scrollRectToVisible(r);
            }
        }
    }
    
    /**
     * views a string representation for every entity in this map and allows the user to hide individual and related groups temporarily.
     */
    public class ViewProperties extends JInternalFrame{
        private MainViewControl all = new MainViewControl(true, "All", null);
        private MainViewControl barriers = new MainViewControl(false, "Barriers", all);
        private MainViewControl accelerators = new MainViewControl(false, "Accelerators", all);
        private MainViewControl units = new MainViewControl(true, "Units", all);
        private MainViewControl[] unitControls = new MainViewControl[ToolPallete.unitTypes.length];
        private ArrayList<SubViewControl> controls = new ArrayList<SubViewControl>();
        private JPanel main = new JPanel();
        
        public ViewProperties()
        {
            super("View Properties", false, true, false, false);
            for(int i = 0; i < ToolPallete.unitTypes.length; i++)
            {
                unitControls[i] = new MainViewControl(false, ToolPallete.unitTypes[i], units);
            }
            setSize(250, 500);
            setLocation((gui.getWidth()/2) - (getWidth()/2), (gui.getHeight()/2) - (getHeight()/2));
            getContentPane().add(new JScrollPane(main, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
            main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
            updateAll();
            gui.getLayeredPane().add(this, new Integer(200));
            setDefaultCloseOperation(HIDE_ON_CLOSE);
        }
        
        public void add(Entity e)
        {
            controls.add(new SubViewControl(e));
            updateAll();
        }
        
        public void remove(Entity e)
        {
            SubViewControl temp = null;
            for(SubViewControl s : controls){
                if(s.entity == e){
                    temp = s;
                }
            }
            if(temp != null){
                controls.remove(controls.indexOf(temp));
            }
            main.remove(temp);
            temp.removeSelf();
            validate();
        }
        
        public void updateAll()
        {
            main.removeAll();
            all.update();
            all.addAll();
            main.repaint();
            validate();
        }
        
        /**
         * base class for all panels that are used in the Advanced View Dialog.
         */
            
        public abstract class ViewControl extends JPanel
        {
            protected MainViewControl owner;
            
            private JLabel label = new JLabel();
            private JButton reset = new JButton("Reset");
            private JCheckBox cb = new JCheckBox();
            
            protected ViewControl(String name, boolean isSub)
            {
                setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
                label.setText(name);
                add(label);
                add(Box.createHorizontalGlue());
                if(!isSub){//boolean to represent whether or not this ViewControl represents an individual Entity
                    reset.addActionListener(new ResetListener());
                    add(reset);
                    add(Box.createHorizontalGlue());
                }else
                {
                    reset = null;
                }
                cb.addItemListener(new ViewListener());
                add(cb);
            }
            
            public boolean isSelected()
            {
                return cb.isSelected();
            }
            
            public void setSelected(boolean flag)
            {
                cb.setSelected(flag);
            }
            
            public String getText()
            {
                return label.getText();
            }
            
            public void setText(String s)
            {
                label.setText(s);
            }
            
            public abstract void hide();
            public abstract void show();
            public abstract void reset();
            public abstract void addAll();
            public abstract void update();
            
            /**
             * listens for the checkbox.
             * when checked, it either hides the Entity it represents, or forces all ViewControls in the group to hide.
             * when unchecked, it either reveals the entity it represents, or allows other ViewControls to be shown.
             */
             
            public class ViewListener implements ItemListener
            {
                public void itemStateChanged(ItemEvent e)
                {
                    if(e.getStateChange() == ItemEvent.SELECTED)
                    {
                        hide();
                    }else
                    {
                        show();
                    }
                }
            }
            
            /**
             * MainViewControls are the only ones that have a reset button.
             * if its owner is not currently selected (or it has no owner), then it will force all ViewControls in the group
             * to be shown.
             */
            
            public class ResetListener implements ActionListener
            {
                public void actionPerformed(ActionEvent e)
                {
                    reset();
                }
            }
        }
        
        /**
         * represents a group (or subgroup) of ViewControls. used to control every ViewControl in the group at a time.
         */
        public class MainViewControl extends ViewControl
        {
            private boolean hasSubGroups;
            private ArrayList<SubViewControl> subs = new ArrayList<SubViewControl>();
            private ArrayList<MainViewControl> subGroups = new ArrayList<MainViewControl>();
            
            /**
             * @param hasSubGroups: determines whether it contains MainViewControls or SubViewControls
             * @param s: name of group. string that will be displayed in the dialog.
             * @param owner: supergroup to which this object belongs. null if this is not a subgroup
             */
            public MainViewControl(boolean hasSubGroups, String s, MainViewControl owner)
            {
                super(s, false);
                this.owner = owner;
                if(owner != null){
                    owner.add(this);
                }
                this.hasSubGroups = hasSubGroups;
            }
            
            public void add(ViewControl v)
            {
                if(hasSubGroups && v instanceof MainViewControl)
                {
                    subGroups.add((MainViewControl)v);
                }else if(!hasSubGroups && v instanceof SubViewControl)
                {
                    subs.add((SubViewControl)v);
                }
            }
            
            public void remove(MainViewControl v)
            {
                int i = subGroups.indexOf(v);
                if(i != -1)
                {
                    subGroups.remove(i);
                }
            }
            
            public void remove(SubViewControl v)
            {
                int i = subs.indexOf(v);
                if(i != -1)
                {
                    subs.remove(i);
                }
            }
            
            public void show()
            {
                if(owner == null || !owner.isSelected())
                {
                    setSelected(false);
                }else
                {
                    setSelected(true);
                }
            }
            
            public void hide()
            {
                setSelected(true);
                for(ViewControl v : subGroups)
                {
                    v.hide();
                }
                for(ViewControl v : subs)
                {
                    v.hide();
                }
            }
            
            public void reset()
            {
                if(owner == null || !owner.isSelected())
                {
                    setSelected(false);
                    if(hasSubGroups){
                        for(ViewControl v : subGroups)
                        {
                            v.reset();
                        }
                    }else{
                        for(ViewControl v : subs)
                        {
                            v.reset();
                        }
                    }
                }
            }
            
            public void addAll()
            {
                main.add(this);
                if(hasSubGroups)
                {
                    for(ViewControl v : subGroups){
                        v.addAll();
                    }
                }else
                {
                    for(ViewControl v : subs){
                        v.addAll();
                    }   
                }
            }
            
            public void update()
            {
                if(hasSubGroups)
                {
                    for(ViewControl v : subGroups)
                    {
                        v.update();
                    }
                }else
                {
                    for(int i = 0; i < subs.size(); i++)
                    {
                        subs.get(i).update();
                    }
                }
                Collections.sort(subs);
            }
        }
        
        /**
         * A ViewControl that represents an individual Entity.
         */
        public class SubViewControl extends ViewControl implements Comparable
        {
            public Entity entity;
            
            public SubViewControl(Entity e)
            {
                super(e.getIDString(), true);
                entity = e;
                update();
            }
            
            public void show()
            {
                if(owner == null ||!owner.isSelected())
                {
                    setSelected(false);
                    entity.setVisible(true);
                }else
                {   
                    setSelected(true);
                }
            }
            
            public void hide()
            {
                setSelected(true);
                entity.setVisible(false);
            }
            
            public void reset()
            {
                show();
            }
            
            public void addAll()
            {
                main.add(this);
            }
            
            public void update()
            {
                setText(entity.getIDString());
                if(entity instanceof SpawnPoint)
                {
                    SpawnPoint temp = (SpawnPoint) entity;
                    if(owner != null){
                        owner.remove(this);
                    }
                    owner = unitControls[temp.getUnitTypeID()];
                    owner.add(this);
                }else
                {
                    if(owner == null)
                    {
                        if(entity instanceof Accelerator){
                            owner = accelerators;
                            owner.add(this);
                        }else if(entity instanceof Barrier){
                            owner = barriers;
                            owner.add(this);
                        }
                    }
                }
            }
            
            public int compareTo(Object o)
            {
                try{
                    SubViewControl temp = (SubViewControl) o;
                    int i = entity.getID();
                    int ti = temp.entity.getID();
                    if(i < ti)
                    {
                        return -1;
                    }else if(i == ti)
                    {
                        return 0;
                    }
                    return 1;
                }catch (NullPointerException ex){
                    return 0;
                }
            }
            
            public void removeSelf(){
                owner.remove(this);
            }
        }
    }
}