package burp;
import java.awt.Component;
import javax.swing.SwingUtilities;



public class BurpExtender implements IBurpExtender, ITab
{ 
    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    
    public void registerExtenderCallbacks (IBurpExtenderCallbacks callbacks) 
    {
        // keep a reference to our callbacks object
        this.callbacks = callbacks;
        Globals.callbacks = callbacks;
        helpers = callbacks.getHelpers();
        Globals.helpers = callbacks.getHelpers();
        Globals.longNames = true;
        callbacks.setExtensionName("Extractor");
        
        Globals.init();
        
        setupGUI();
    } 
    
    @Override
    public String getTabCaption()
    {
        mainGui.loadFromProjectSettings(); //this is a good time to attempt loading settings (burp is ready)
        return "Extractor";
    }

    extenderTab mainGui;
    
    @Override
    public Component getUiComponent()
    {
        return mainGui;
    }
    
    private void setupGUI() {
	SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Create our initial UI components
                mainGui = new extenderTab();             
                callbacks.addSuiteTab(BurpExtender.this);
            }
        });
    }
        
} 
