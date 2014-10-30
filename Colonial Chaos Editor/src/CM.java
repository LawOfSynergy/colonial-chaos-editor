import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * CM stands for Convience Methods.
 * provides shortened names for longer names such as Box.createHorizontalGlue()
 * 
 * @author Kenneth Ryan Stimson
 * @version 7:37 PM Thursday, March 3, 2011
 */
public final class CM{
    private CM(){}

    public static Component chg(){
        return Box.createHorizontalGlue();
    }
    
    public static Component cvg(){
        return Box.createVerticalGlue();
    }
    
    public static Component chs(int i){
        return Box.createHorizontalStrut(i);
    }
    
    public static Component cvs(int i){
        return Box.createVerticalStrut(i);
    }
}
