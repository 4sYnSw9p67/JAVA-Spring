package main.model;

public enum PlayerClass {
    MYSTIC_MUSE("Mystic Muse"),
    GHOST_HUNTER("Ghost Hunter"),
    DOOMBRINGER("Doombringer");

    private final String displayName;

    PlayerClass(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}