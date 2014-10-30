import java.awt.*;
import javax.swing.*;
import java.io.File;

public class UpdateDriver
{
    private UpdateDriver(){}
    
    public static void main(String[] args){
        JFrame frame = new JFrame();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        Update update = new Update();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showOpenDialog(frame);
        String file = fileChooser.getSelectedFile().getAbsolutePath();
        update.setVersion(2);
        update.newMapProperty("Gravity", "double", "1.0", "The force of gravity on this map");
        update.newMapProperty("Arcane Modifier", "double", "1.0", "All effects of all spells are modified by this value");
        update.newMapProperty("Mana Saturation", "double", "1.0", "Modifies the mana/energy capacity of all players on this map by this value");
        update.newMapProperty("Mana Regen Modifier", "double", "1.0", "Modifies the mana/energy regeneration rate of all players on this map by this value");
        update.newMapProperty("Experience Aquisition Modifier", "double", "1.0", "Modifies the amount of experience each player gets for killing (or being in range of a dieing) unit");
        update.newMapProperty("Experience Aquisition Range", "int", "300", "The range in which players gain experience from characters that die nearby./nEven if the killing player is out of this range, he/she will still get full experience for the kill");
        update.newMapProperty("Nonparticipant Experience Aquisition Modifier", "double", "1.0", "The amount of experience recieved by a player who did not actually participate in the combat is modified by this amount");
        update.newTerrainType("Grass", file);
        update.newTerrainType("Tall Grass", file);
        update.newTerrainType("Rocky", file);
        update.newTerrainType("Jagged", file);
        update.newTerrainType("Village", file);
        update.newTerrainType("City", file);
        update.newUnitType("Ancient Hydra", "An extremely powerful unit that splits into two Hydras upon its death.\nThis unit is both ranged and melee", new Dimension(60, 80), file);
        update.newUnitType("Hydra", "A powerful melee unit that splits into two Hydra Spawns upon its death.", new Dimension(45, 60), file);
        update.newUnitType("Hydra Spawn", "A moderately powerful melee unit that splits into two Hydra Spawnlings upon its death.", new Dimension(30, 40), file);
        update.newUnitType("Hydra Spawnling", "A weak melee unit.", new Dimension(15, 20), file);
        update.newUnitType("Footman", "A moderately powerful melee unit", new Dimension(20, 20), file);
        update.newUnitType("Rifleman", "A moderately powerful ranged unit", new Dimension(20, 20), file);
        update.save();
        System.exit(1);
    }
}
