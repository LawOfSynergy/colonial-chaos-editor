import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Update implements Serializable{
    private int version;
    private ArrayList<ArrayList<String>> mapProperties = new ArrayList<ArrayList<String>>();
    private ArrayList<ArrayList<Object>> unitTypes = new ArrayList<ArrayList<Object>>();
    private ArrayList<ArrayList<Object>> terrainTypes = new ArrayList<ArrayList<Object>>();
    
    public void newMapProperty(String name, String type, String baseValue, String description){
        ArrayList<String> property = new ArrayList<String>();
        property.add(name);
        property.add(type);
        property.add(baseValue);
        property.add(description);
        mapProperties.add(property);
    }
    
    public void newUnitType(String name, String description, Dimension size, String imageURL){
        ArrayList<Object> unitType = new ArrayList<Object>();
        unitType.add(name);
        unitType.add(description);
        unitType.add(size);
        unitType.add(new File(imageURL));
        unitTypes.add(unitType);
    }
    
    public void newTerrainType(String name, String imageURL){
        ArrayList<Object> temp = new ArrayList<Object>();
        temp.add(name);
        temp.add(new File(imageURL));
        terrainTypes.add(temp);
    }
    
    public void setVersion(int i){
        version = i;
    }
    
    public int getVersion(){
        return version;
    }
    
    public ArrayList<ArrayList<String>> getProperties(){
        return mapProperties;
    }
    
    public ArrayList<ArrayList<Object>> getUnitTypes(){
        return unitTypes;
    }
    
    public ArrayList<ArrayList<Object>> getTerrainTypes(){
        return terrainTypes;
    }
    
    public void save(){
        try{
            File file = new File("YVGDC Map Editor Updates.ser");
            file.delete();
            file = new File("YVGDC Map Editor Updates.ser");
            ObjectOutputStream out  = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(this);
            out.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}