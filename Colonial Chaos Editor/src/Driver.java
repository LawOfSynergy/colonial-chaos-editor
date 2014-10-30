/**
 * the main Driver class.
 */
public class Driver
{
    private Driver(){}
    
    /**
     * the main driver method.
     * @param args unused.
     */
    public static void main(String[] args){
        MainGui.init();
        new MainGui();
    }
}
