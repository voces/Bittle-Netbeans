/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

    private BittlePanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    Connection connection = Connection.getInstance();
    private boolean changed;
    private String rootpath;

    public void update() {
        getPanel().load();
        changed = false;
    }

    public void applyChanges() {
        SwingUtilities.invokeLater(() -> {
                getPanel().store();
                changed = false;
            });
    }

    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    public boolean isValid() {
        return getPanel().valid();
    }

    public boolean isChanged() {
        return changed;
    }

    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }

    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
    
    public String getRoot(){
        return this.rootpath;
    }
    
    public void setRoot(String newRoot){
        String oldRoot = this.rootpath;
        this.rootpath = newRoot;
        pcs.firePropertyChange("root", oldRoot, newRoot);
    }

    private BittlePanel getPanel() {
        if (panel == null) {
            panel = new BittlePanel(this, this.connection);
        }
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
