public class SavedState {
    long[][] pieces;
    long[] occupancy;
    int whiteKingTile;
    int blackKingTile;
    int enPassant;
    boolean[] kingRookMoved;
    boolean[] queenRookMoved;
    boolean[] kingMoved;
    public SavedState(long[][] pieces, long[] occupancy, int whiteKingTile, int blackKingTile,
                      int enPassant, boolean[] kingRookMoved, boolean[] queenRookMoved, boolean[] kingMoved) {
        this.pieces = pieces;
        this.occupancy = occupancy;
        this.whiteKingTile = whiteKingTile;
        this.blackKingTile = blackKingTile;
        this.enPassant = enPassant;
        this.kingRookMoved = kingRookMoved;
        this.queenRookMoved = queenRookMoved;
        this.kingMoved = kingMoved;
    }
}
