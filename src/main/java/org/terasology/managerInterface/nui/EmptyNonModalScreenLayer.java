
package org.terasology.managerInterface.nui;

import org.terasology.rendering.nui.CoreScreenLayer;

public class EmptyNonModalScreenLayer extends CoreScreenLayer {

    
    @Override
    public void initialise() {
    }

    @Override
    public boolean isModal() {
        return false;
    }
    
    @Override
    public boolean isReleasingMouse() {
        return true;
    }
}
