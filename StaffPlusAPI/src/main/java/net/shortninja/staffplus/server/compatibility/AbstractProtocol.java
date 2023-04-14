package net.shortninja.staffplus.server.compatibility;

import net.shortninja.staffplus.IStaffPlus;

public abstract class AbstractProtocol implements IProtocol {

    protected IStaffPlus staffPlus;


    public AbstractProtocol(IStaffPlus staffPlus) {
        this.staffPlus = staffPlus;
    }


}
