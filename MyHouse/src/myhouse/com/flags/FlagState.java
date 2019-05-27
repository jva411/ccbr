package myhouse.com.flags;

public class FlagState {
    
    private Flag Flag;
    private boolean Active;

    public FlagState(Flag Flag, boolean Active) {
        this.Flag = Flag;
        this.Active = Active;
    }

    public Flag getFlag() {
        return Flag;
    }

    public boolean isActive() {
        return Active;
    }

    public void setActive(boolean Active) {
        this.Active = Active;
    }

    public void setFlag(Flag Flag) {
        this.Flag = Flag;
    }
    
    
    
}
