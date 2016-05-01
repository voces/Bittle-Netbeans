package org.bittle.beansmod;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

@OptionsPanelController.TopLevelRegistration(
        categoryName = "#OptionsCategory_Name_Bittle",
        iconBase = "org/bittle/beansmod/bittle32.png",
        keywords = "#OptionsCategory_Keywords_Bittle",
        keywordsCategory = "Bittle",
        id = "BittleOptions",
        position = 0
)
@org.openide.util.NbBundle.Messages({"OptionsCategory_Name_Bittle=Bittle", "OptionsCategory_Keywords_Bittle=Bittle, log in, register, sync, lol, idk"})
public final class BittleOptionsPanelController extends OptionsPanelController {

    private static final BittleOptionsPanelController instance;
    private static BittlePanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;
    
    // Singleton 
    static{
        instance = new BittleOptionsPanelController();
    }
    
    private BittleOptionsPanelController() {}
    
    public static BittleOptionsPanelController getInstance(){
        return instance;
    }
    
    public void logIn(){
        getPanel().logIn();
    }
    
    public void logOut(){
        getPanel().logOut();
    }
    
    public void stopWaiting(){
        getPanel().stopWaiting();
    }
    
    public void setLoginState(boolean state){
        getPanel().setLoginState(state);
    }

    @Override
    public void update() {
        getPanel().load();
        changed = false;
    }

    @Override
    public void applyChanges() {
        SwingUtilities.invokeLater(() -> {
                getPanel().store();
                changed = false;
            });
    }

    @Override
    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    @Override
    public boolean isValid() {
        return getPanel().valid();
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
    
    private BittlePanel getPanel() {
        if(panel == null)
            panel = new BittlePanel(instance);
        return panel;
    }

    void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }

}
