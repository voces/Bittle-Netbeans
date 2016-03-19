/*
 * This file defines and creates the primary options panel for Bittle
 */

/**
 * Registers the options container inside of NetBeans
 * id: The unique name for the options panel
 *     - This is used when you want to create secondary panels inside this panel
 * categoryName: A 'variable' for the name of the options panel
 * iconBase: The path of the icon to represent the panel
 * keywords: A 'variable' to hold the searchable keywords for the panel
 * keywordsCategory: What panel the keywords belong to
 *                   - In this case, just use the unique ID for this panel
 */
@OptionsPanelController.ContainerRegistration(
        id = "BittleOptionsMain", 
        categoryName = "#OptionsCategory_Name_BittleOptionsMain", 
        iconBase = "org/bittle/optons/bittle32.png", 
        keywords = "#OptionsCategory_Keywords_BittleOptionsMain", 
        keywordsCategory = "BittleOptionsMain"
)

/**
 * Sets values to the 'variables' defined in the container registration
 * - Sets the categoryName to "Bittle"
 * - Defines all the searchable keywords for this panel
 */
@NbBundle.Messages(
        value = {
            "OptionsCategory_Name_BittleOptionsMain=Bittle", 
            "OptionsCategory_Keywords_BittleOptionsMain=Bittle, bittle, log in, bittle login, bittle log in, bittle register, register"
        }
)
package org.bittle.optons;

import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;
