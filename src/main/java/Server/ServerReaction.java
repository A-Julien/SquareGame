package Server;

import FX.Case;
import Class.Player;
import java.util.ArrayList;

public interface ServerReaction {
    public void Initialisation(ArrayList<Case> mesCases);

    public boolean jeSouhaiteVenirEnCase(Player p, int X, int Y);

    public boolean jeQuitteLaCase(Player p, Case c);

    public boolean isThereSomoeoneThere(Case c);
}

