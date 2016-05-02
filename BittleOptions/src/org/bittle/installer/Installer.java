package org.bittle.installer;

import org.bittle.utilities.Connection;

public class Installer extends org.openide.modules.ModuleInstall {

    // Close the connection to the server when the IDE closes 
    @Override
    public boolean closing() {
        Connection.getInstance().close();
        return true;
    }
}
