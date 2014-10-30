import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.imageio.*;

import javax.swing.filechooser.FileFilter;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.awt.image.BufferedImage;

/**
 * the actual gui application.
 */
public class MainGui extends JFrame{
    /**
     * JTabbedPane that allows the user to have multiple maps open simultaneously.
     */
    private JTabbedPane pane;
    /**
     * allows the user to show/hide the tool pallete from the menubar
     */
    private JCheckBoxMenuItem palleteView;
    /**
     * the tool pallete being used for this session. determines how the user can interact with any currently open map.
     */
    private ToolPallete pallete;
    /**
     * the currently selected map.
     */
    private Map currentMap;
    /**
     * file chooser used to load other maps into the editor.
     */
    private JFileChooser fileChooser = new JFileChooser();
    /**
     * filter used by the filechooser.
     */
    private SerFilter serFilter = new SerFilter();
    /**
     * filter used by the filechooser.
     */
    private TxtFilter txtFilter = new TxtFilter();
    /**
     * filter used by the filechooser.
     */
    private ImageFilter imageFilter = new ImageFilter();
    
    /**
     * the one and only constructor.
     * Initializes the application.
     */
    public MainGui(){
        pane = new JTabbedPane();
        pallete = new ToolPallete(this);
        getLayeredPane().add(pallete, new Integer(200));
        
        addKeyListener(new KeyboardDeleteListener());
        
        //initilize menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu menu;
        JMenuItem menuItem;
        JCheckBoxMenuItem cbMenuItem;
        
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription("Conatins file operations");
        menuBar.add(menu);

        menuItem = new JMenuItem("New Map", KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                newMap();
            }
        });
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Save Map", KeyEvent.VK_S);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                saveMap();
            }
        });
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Save All Maps", KeyEvent.VK_E);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                saveAll();
            }
        });
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Close Map", KeyEvent.VK_C);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                closeMap();
            }
        });
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Close All Maps", KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                closeAll();
            }
        });
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Load Map", KeyEvent.VK_L);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                loadMap();
            }
        });
        menu.add(menuItem);
        
        menu = new JMenu("View");
        menu.setMnemonic(KeyEvent.VK_V);
        menu.getAccessibleContext().setAccessibleDescription("Viewing options for the Application");
        menuBar.add(menu);
        
        palleteView = new JCheckBoxMenuItem("Tool Pallete", true);
        palleteView.setMnemonic(KeyEvent.VK_T);
        palleteView.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e){
                pallete.setVisible((e.getStateChange() == ItemEvent.SELECTED));
            }
        });
        menu.add(palleteView);
        
        menu.addSeparator();
        
        menuItem = new JMenuItem("Advanced", KeyEvent.VK_A);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(currentMap != null){
                    currentMap.showViewProperties(true);
                }
            }
        });
        menu.add(menuItem);
        setJMenuBar(menuBar);
        
        menuItem = new JMenuItem("Map Properties", KeyEvent.VK_M);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(currentMap != null)
                    currentMap.getProperties().show(true);
            }
        });
        menu.add(menuItem);
        setJMenuBar(menuBar);
        
        pallete.addComponentListener(new ComponentAdapter(){
            public void componentHidden(ComponentEvent e){
                palleteView.setSelected(false);
            }
        });
        
        pane.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e){
                try{
                    currentMap = (Map)((JScrollPane)pane.getSelectedComponent()).getViewport().getView();
                }catch(Exception ex){
                    //oh well
                }
            }
        });
        
        //initialize the fileChooser
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(false);
        
        //initialize rest of frame
        getContentPane().add(pane);
        setTitle("Colonial Chaos Map Editor Version 2");
        setExtendedState(MAXIMIZED_BOTH);
        setPreferredSize(getSize());
        pane.setDoubleBuffered(true);
        setVisible(true);
    }
    
    public void debug(){
        currentMap.debug();
    }
    
    /**
     * creates a new, empty map and adds it to the tabbed pane.
     */
    public void newMap(){
        currentMap = new Map(this);
        JScrollPane scroll = new JScrollPane(currentMap, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        currentMap.setViewer(scroll);
        pane.add(scroll);
        pane.setTabComponentAt(pane.indexOfComponent(scroll), currentMap.getTab());
        getLayeredPane().add(currentMap.getProperties(), new Integer(200));
    }
    
    /**
     * saves the currently selected map.
     */
    public void saveMap(){
        System.out.println("MainGui.saveMap()");
        if(currentMap != null){
            currentMap.save();
        }
    }
    
    /**
     * saves all open maps.
     */
    public void saveAll(){
        System.out.println("MainGui.saveAll()");
    }
    
    /**
     * Closes the currently selected map. Note: Does not save first!
     */
    public void closeMap(){
        if(currentMap != null){
            pane.remove(pane.indexOfComponent(currentMap.getViewer()));
        }
    }
    
    /**
     * Closes the map whos tab component's exit button has been pressed. Note: Does not save first!
     */
    public void close(Map.TabComponent mapTab){
        pane.remove(pane.indexOfTabComponent(mapTab));
    }
    
    /**
     * Closes all open maps. Note: Does not save first!
     */
    public void closeAll(){
        pane.removeAll();
        currentMap = null;
    }
    
    /**
     * opens a file chooser and loads the selected map. Does nothing if the user cancels or closes the file chooser.
     */
    public void loadMap(){
        fileChooser.setFileFilter(serFilter);
        int temp = fileChooser.showOpenDialog(getContentPane());
        if(temp == JFileChooser.APPROVE_OPTION){
            load(fileChooser.getSelectedFile());
        }
    }
    
    /**
     * loads the file selected by the file chooser.
     */
    public void load(File serFile){
        try{
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(serFile));
            ArrayList<ArrayList<Object>> file = (ArrayList<ArrayList<Object>>)in.readObject();
            System.out.println(file);
            ArrayList<Object> format = file.remove(0);
            format.remove(0);
            Map map = new Map(this, file.remove(0));
            String type = null;
            ArrayList<Object> params = null;
            while(format.size() > 0){
                type = (String)format.remove(0);
                params = file.remove(0);
                if(type.equals("MAP")){
                }else if(type.equals("MAPPROPERTY")){
                    map.updateProperty(params);
                }else if(type.equals("NEWENTITY")){
                    System.out.println(params);
                    map.add(newEntity(map, params));
                }
            }
            JScrollPane scroll = new JScrollPane(map, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            map.setViewer(scroll);
            pane.add(scroll);
            pane.setTabComponentAt(pane.indexOfComponent(scroll), map.getTab());
            getLayeredPane().add(map.getProperties(), new Integer(200));
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    /**
     * parses an ArrayList of objects into an entity and adds it to a map.
     * @param map the map that the parsed entity will be added to. Note: the entity does not get added to the map in this method.
     * The map is passed into the entity's constructor as it is required apon creation and cannot be changed afterwards. The actual
     * adding is done within the load method.
     * @param args the arraylist of the entity's stats/fields
     * @return the newly parsed entity
     */
    public Entity newEntity(Map map, ArrayList<Object> args){
        ArrayList<Object> newArgs = new ArrayList<Object>(args.subList(1, args.size()));
        String temp = (String)args.remove(0);
        if(temp.equals("BARRIER")){
            return new Barrier(map, newArgs);
        }else if(temp.equals("ACCELERATOR")){
            return new Accelerator(map, newArgs);
        }else if(temp.equals("SPAWNPOINT")){
            return new SpawnPoint(map, newArgs);
        }
        return null;
    }
    
    /**
     * not yet implemented. will allow the user to add a new background layer.
     */
    public void newLayer(Map destination){
        System.out.println("MainGui.newLayer(Map)");
    }
    
    /**
     * adds a ChangeListener to the JTabbedPane
     * @param listener the listener to be added to the JTabbedPane
     */
    public void addPaneListener(ChangeListener listener){
        pane.addChangeListener(listener);
    }
    
    /**
     * removes a ChangeListener from the JTabbedPane
     * @param listener the listener to be removed from the JTabbedPane
     */
    public void removePaneListener(ChangeListener listener){
        pane.removeChangeListener(listener);
    }
    
    /**
     * checks whether or not the barrier tool (in the ToolPallete class) is active
     * 
     * @return true if the barrier tool is active. False otherwise.
     */
    public boolean isBarrierToolActive(){
        return pallete.isBarrierToolActive();
    }
    
    /**
     * gets the current terrain type
     */
    public int getTerrainType(){
        return pallete.getTerrainType();
    }
    
    /**
     * gets the String representation of the current terrain type
     */
    public String getTerrainTypeString(){
        return pallete.getTerrainTypeString();
    }
    
    /**
     * checks whether or not the accelerator tool (in the ToolPallete class) is active
     * 
     * @return true if the accelerator tool is active. False otherwise.
     */
    public boolean isAcceleratorToolActive(){
        return pallete.isAcceleratorToolActive();
    }
    
    /**
     * gets the name that will be used for the next accelerator.
     * 
     * @return a string that will make up part of the next created accelerator's id string.
     */
    public String getAccelName(){
        return pallete.getAccelName();
    }
    
    /**
     * returns the horizontal acceleration factor being used to create new accelerators.
     * 
     * @return a double representing the horizontal acceleration factor.
     */
    public double getAccelXFactor(){
        return pallete.getAccelXFactor();
    }
    
    /**
     * returns the verticalal acceleration factor being used to create new accelerators.
     * 
     * @return a double representing the vertical acceleration factor.
     */
    public double getAccelYFactor(){
        return pallete.getAccelYFactor();
    }
    
    public Color getAccelColor(){
        return pallete.getAccelColor();
    }
    
    public int getAccelColorIndex(){
        return pallete.getAccelColorIndex();
    }
    
    public String getAccelColorString(){
        return pallete.getAccelColorString();
    }
    
    /**
     * clears the accelerator name for the tool pallete. used immediately after an accelerator has been created. 
     */
    public void clearAccel(){
        pallete.clearAccel();
    }
    
    /**
     * checks whether or not the unit tool (in the ToolPallete class) is active
     * 
     * @return true if the unit tool is active. False otherwise.
     */
    public boolean isUnitToolActive(){
        return pallete.isUnitToolActive();
    }
    
    /**
     * returns a string representing the unit type of newly created SpawnPoints.
     * 
     * @return a string representing the unit type of newly created SpawnPoints.
     */
    public String getSelectedUnitType(){
        return pallete.getSelectedUnitTypeString();
    }
    
    /**
     * returns an int representing the unit type of newly created SpawnPoints.
     * 
     * @return a int representing the unit type of newly created SpawnPoints.
     */
    public int getSelectedUnitTypeID(){
        return pallete.getSelectedUnitTypeID();
    }
    
    public boolean isSelectionToolActive(){
        return pallete.isSelectionToolActive();
    }
    
    /**
     * registers the selection of a tool.
     */
    public void selectTool(int tool){
        for(int i = 0; i < pane.getTabCount(); i++){
            JScrollPane temp = (JScrollPane)pane.getComponentAt(i);
            ((Map)temp.getViewport().getView()).selectTool(tool);
        }
    }
    
    /**
     * gets the extension of any file passed to this method.
     */
    public String getExtension(File f){
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if(i > 0 && i < s.length() - 1){
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }
    
    public String getFileName(File f){
        String s = f.getName();
        int si = s.lastIndexOf('\\');
        int ei = s.lastIndexOf('.');
        return s.substring(si+1, ei);
    }
    
    /**
     * selectes a map.
     */
    public void selectMap(Map map){
        currentMap = map;
    }
    
    /**
     * initializes the application by setting values according to the passed update.
     * 
     * @param u the update to be used for the current session.
     */
    public static void init(){
        Update update = null;
        try{
            File lastUpdateFile = new File("YVGDC Map Editor Updates.ser");
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(lastUpdateFile));
            update = (Update)in.readObject();
            in.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            ToolPallete.init(update.getUnitTypes(), update.getTerrainTypes());
            Map.init(update.getProperties());
        }
    }
    
    /**
     * gets a buffered image from a file chosen by the user.
     */
    public BufferedImage getImage(){
        fileChooser.setFileFilter(imageFilter);
        int temp = fileChooser.showOpenDialog(getContentPane());
        if(temp != JFileChooser.APPROVE_OPTION){
            return null;
        }
        try{
            return ImageIO.read(fileChooser.getSelectedFile());
        }catch(Exception ex){
            System.out.println("Error loading image");
            return null;
        }
    }
    
    /**
     * listens for when the user presses the delete key. that deletes the current map's currently selected entity.
     */
    public class KeyboardDeleteListener extends KeyAdapter{
        public void keyTyped(KeyEvent ke){
            if(ke.getKeyCode() == KeyEvent.VK_DELETE){
                System.out.println("delete");
                currentMap.removeCurrentPanel();
            }
        }
    }
    
    /**
     * file filter that only allows for files with the extension ".ser"
     */
    public class SerFilter extends FileFilter{
        public boolean accept(File f){
            if(f.isDirectory()){
                return true;
            }
            String s = getExtension(f);
            if(s != null && s.equals("ser") && !getFileName(f).equals("YVGDC Map Editor Updates")){
                return true;
            }
            return false;
        }
        
        public String getDescription(){
            return ".ser";
        }
    }
    
    /**
     * file filter that only allows for files with the extension ".txt"
     */
    public class TxtFilter extends FileFilter{
        public boolean accept(File f){
            if(f.isDirectory()){
                return true;
            }
            String s = getExtension(f);
            if(s != null && s.equals("txt")){
                return true;
            }
            return false;
        }
        
        public String getDescription(){
            return ".txt";
        }
    }
    
    /**
     * file filter that only allows for files with the extension ".png"
     */
    public class ImageFilter extends FileFilter{
        public boolean accept(File f){
            if(f.isDirectory()){
                return true;
            }
            String s = getExtension(f);
            if(s != null && s.equals("png")){
                return true;
            }
            return false;
        }
        
        public String getDescription(){
            return ".png";
        }
    }
}
        