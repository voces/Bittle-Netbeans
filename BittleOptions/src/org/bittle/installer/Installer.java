package org.bittle.installer;

import org.bittle.beansmod.Connection;

public class Installer extends org.openide.modules.ModuleInstall {

    @Override
    public boolean closing() {
        Connection.getInstance().close();
        return true;
    }
}
