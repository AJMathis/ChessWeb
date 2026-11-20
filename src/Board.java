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
        occupancy[2] = occupancy[0] | occupancy[1];
    }

    //Public Methods
    public void move(int from, int to) {
        move(from, to, false, false);
    }
    public void move(int from, int to, boolean passant, boolean castling) {
        int color = getColor(from);
        int piece = getPiece(from);

        clearSqr(from, piece, color);
        if(passant) {
            int displace = color==WHITE?-8:8;
            clearSqr(to+displace, PAWN, color^1);
        } else {
            clearSqr(to, getPiece(to), color ^ 1);
        }

        if(castling) {
            int file = to>from?7:0;
            int sqr = (getRank(from) * 8) + file;
            clearSqr(sqr, ROOK, color);
            setSqr(to+(file==7?-1:1), ROOK, color);
        }

        setSqr(to, piece, color);
    }

    public boolean isOccupied(int sqr, long occupancy) {
        return ((1L<<sqr) & occupancy) != 0;
    }
    public boolean isOccupied(int sqr) {
        return ((1L<<sqr) & this.occupancy[2]) != 0;
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
    public void clearSqr(int sqr, int piece, int color) {
        if(piece == NULL) return;
        pieces[color][piece] &= ~(1L<<sqr);
        updateOcc();
    }
    public void setSqr(int sqr, int piece, int color) {
        if(piece == NULL) return;
        pieces[color][piece] |= 1L<<sqr;
        updateOcc();
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
    public int getFile(int sqr) {
        return sqr % 8;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        for(int i = 7; i >= 0; i--) {
            for(int j = 0; j < 8; j++) {
                str.append(String.valueOf(getPiece((i*8)+j)+1));
            }
            str.append("\n");
        }
        return str.toString();
    }
}
