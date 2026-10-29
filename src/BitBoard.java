public class BitBoard {
    long[][] pieces;
    long[] occupancy;
    int whiteKingTile;
    int blackKingTile;
    int enPassant;

    boolean[] kingRookMoved = {false, false};
    boolean[] queenRookMoved = {false, false};
    boolean[] kingMoved = {false, false};

    public BitBoard() {
        pieces = new long[2][6];
        occupancy = new long[3];
        initBitmaps();
        whiteKingTile = 4;
        blackKingTile = 60;
        enPassant = -1;
    }

    private void initBitmaps() {
        //Init White pieces
        pieces[0][0] = 0x000000000000FF00L;
        pieces[0][1] = 0x0000000000000024L;
        pieces[0][2] = 0x0000000000000042L;
        pieces[0][3] = 0x0000000000000018L;
        pieces[0][4] = 0x0000000000000080L;
        pieces[0][5] = 0x0000000000000001L;
        //Init Black Pieces
        pieces[1][0] = 0x00FF000000000000L;
        pieces[1][1] = 0x2400000000000000L;
        pieces[1][2] = 0x4200000000000000L;
        pieces[1][3] = 0x1800000000000000L;
        pieces[1][4] = 0x8000000000000000L;
        pieces[1][5] = 0x0100000000000000L;
        //Init occupancy
        updateOccupancy();
    }

    private void updateOccupancy() {
        occupancy[0] = 0x0L;
        occupancy[1] = 0x0L;
        for (int i = 0; i < 6; i++) {
            occupancy[0] |= pieces[0][i];
            occupancy[1] |= pieces[1][i];
        }
        occupancy[2] = occupancy[0] | occupancy[1];
    }

    public int getColor(int square) {
        if ((occupancy[0] & (1L << square)) != 0) return 0;
        else if ((occupancy[1] & (1L << square)) != 0) return 1;
        else return -1;
    }

    public int getPiece(int square) {
        if ((occupancy[2] & (1L << square)) != 0) {
            if ((occupancy[0] & (1L << square)) != 0) {
                for (int i = 0; i < 6; i++) {
                    if ((pieces[0][i] & (1L << square)) != 0) return i;
                }
            } else {
                for (int i = 0; i < 6; i++) {
                    if ((pieces[1][i] & (1L << square)) != 0) return i;
                }
            }
        }
        return -1;
    }

    public void movePiece(int orig, int dest) {
        int piece = getPiece(orig);

        enPassant = -1;
        if(piece == 0) {
            if(dest-orig >= 16) enPassant = orig + 8;
            else if (dest-orig <= 16) enPassant = orig - 8;
        }

        setPiece(dest, piece, getColor(orig));
        setPiece(orig, -1, -1);
    }

    public void undoMove(int orig, int dest, int enemyPiece) {
        int color = getColor(dest);
        setPiece(orig, getPiece(dest), color);
        setPiece(dest, enemyPiece, color ^ 1);
    }

    private void setPiece(int sqr, int piece, int color) {
        if (piece == -1) {
            int oldPiece = getPiece(sqr);
            if (oldPiece != -1) pieces[getColor(sqr)][getPiece(sqr)] &= ~(0x1L << sqr);
        } else {
            int oldPiece = getPiece(sqr);
            if (oldPiece != -1) pieces[getColor(sqr)][oldPiece] &= ~(0x1L << sqr);
            pieces[color][piece] |= (0x1L << sqr);
        }
        updateOccupancy();
    }

    public int getKingTile(int color) {
        return color == 0 ? whiteKingTile : blackKingTile;
    }

    public boolean freeSqr(int sqr) {
        return (this.occupancy[2] & (1L << (sqr))) == 0;
    }
    public boolean filledSqr(int sqr, int color) {
        return (this.occupancy[color] & (1L << (sqr))) == 1;
    }
    public boolean enPassantOnSqr(int sqr) {
        return this.enPassant == sqr;
    }
}
