package myhouse.com.flags;

import java.util.ArrayList;
import java.util.Collection;

public class FlagManager {
    
    private ArrayList<FlagState> FlagStates = new ArrayList<>();

    public FlagManager(Collection<Flag> DefaultFlags) {
        for(Flag flag:DefaultFlags) FlagStates.add(new FlagState(flag, true));
    }
    
    public void enableFlag(Flag flag){
        for(FlagState flagS:FlagStates){
            if(flagS.getFlag().equals(flag)){
                if(!flagS.isActive()) {
                    flagS.setActive(true);
                    return;
                }
            }
        }
        FlagStates.add(new FlagState(flag, true));
    }
    
    public void disableFlag(Flag flag){
        for(FlagState flagS:FlagStates){
            if(flagS.getFlag().equals(flag)){
                if(flagS.isActive()) {
                    flagS.setActive(false);
                    return;
                }
            }
        }
    }
    
    public ArrayList<Flag> getEnabledFlags(){
        ArrayList<Flag> Flags = new ArrayList<>();
        for(FlagState flagS:FlagStates) {
            if(flagS.isActive()){
                Flags.add(flagS.getFlag());
            }
        }
        return Flags;
    }
    
}
