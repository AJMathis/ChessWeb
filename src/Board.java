public class Board {
    final int NULL = -1;
    final int WHITE = 0;
    final int BLACK = 1;
    final int PAWN = 0;
    final int KNIGHT = 1;
    final int BISHOP = 2;
    final int ROOK = 3;
    final int QUEEN = 4;
    final int KING = 5;
    long[][] pieces;
    long[] occupancy;

    public Board() {
        this.pieces = new long[2][6];
        this.occupancy = new long[3];
        initPieces();
    }

    private void initPieces() {
        this.pieces[WHITE][PAWN] = 0x000000000000FF00L;
        this.pieces[BLACK][PAWN] = 0x00FF000000000000L;
        this.pieces[WHITE][KNIGHT] = 0x0000000000000042L;
        this.pieces[BLACK][KNIGHT] = 0x4200000000000000L;
        this.pieces[WHITE][BISHOP] = 0x0000000000000024L;
        this.pieces[BLACK][BISHOP] = 0x2400000000000000L;
        this.pieces[WHITE][ROOK] = 0x0000000000000081L;
        this.pieces[BLACK][ROOK] = 0x8100000000000000L;
        this.pieces[WHITE][QUEEN] = 0x0000000000000008L;
        this.pieces[BLACK][QUEEN] = 0x0800000000000000L;
        this.pieces[WHITE][KING] = 0x0000000000000010L;
        this.pieces[BLACK][KING] = 0x1000000000000000L;
        updateOcc();
    }
    private void updateOcc() {
        occupancy[0] = 0x0;
        occupancy[1] = 0x0;
        occupancy[2] = 0x0;
        for(int i = PAWN; i <= KING; i++) {
            occupancy[0] |= pieces[0][i];
            occupancy[1] |= pieces[1][i];
        }
        occupancy[3] = occupancy[0] | occupancy[1];
    }

    //Public Methods
    public void move(int from, int to) {
        int color = getColor(from);
        int piece = getPiece(from);

        clearSqr(from, piece, color);
        clearSqr(to, getPiece(to), color^1);
        setSqr(to, piece, color);

        updateOcc();
    }

    public boolean isOccupied(int sqr, long occupancy) {
        return ((1L<<sqr) & occupancy) != 0;
    }
    public boolean isOccupied(int sqr) {
        return ((1L<<sqr) & this.occupancy[3]) != 0;
    }
    public boolean isOccupied(int sqr, int color) {
        return ((1L<<sqr) & this.occupancy[color]) != 0;
    }
    public boolean onBoard(int sqr) {
        return (sqr >=0 && sqr <=63);
    }

    //BitBoard Methods
    public long setSqr(long board, int sqr, int color) {
        return board | 1L<<sqr;
    }

    //SQR Editing
    private void clearSqr(int sqr, int piece, int color) {
        pieces[color][piece] &= ~(1L<<sqr);
    }
    private void setSqr(int sqr, int piece, int color) {
        pieces[color][piece] |= 1L<<sqr;
    }


    //Getters
    public int getPiece(int sqr) {
        int color = getColor(sqr);
        if(color == NULL) return NULL;

        for(int i = PAWN; i <= KING; i++) {
            if(isOccupied(sqr,pieces[color][i])) return i;
        }
        return NULL;
    }
    public int getColor(int sqr) {
        if(isOccupied(sqr,occupancy[WHITE])) return WHITE;
        else if(isOccupied(sqr, occupancy[BLACK])) return BLACK;
        else return NULL;
    }
    public int getRank(int sqr) {
        return sqr / 8;
    }
}
