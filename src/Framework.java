import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Supplier;

public class Framework {
    long[][] pieces;
    long[] occupancy;
    String whiteKingTile;
    String blackKingTile;

    public Framework() {
        pieces = new long[2][6];
        occupancy = new long[3];
        initBitmaps();
        whiteKingTile = "e1";
        blackKingTile = "e8";
    }

    public void initBitmaps() {
        //Init Occupancy
        occupancy[0] = 0x000000000000FFFFL;
        occupancy[1] = 0xFFFF000000000000L;
        occupancy[2] = occupancy[0] | occupancy[1];
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
    }

    public boolean isValidMove(Move move) {
        int piece = move.getIntPiece();
        String orig = move.getOrigin();
        String dest = move.getDestination();
        char color = move.getColor();


        if(isInvalidSquare(orig) || isInvalidSquare(dest)) return false;
        if(getColor(dest) == color) return false;
        if(!getMoves(piece, orig).contains(dest)) return false;
        if(inCheck(color) || simulateMove(move, () -> this.inCheck(color))) return false;
        return true;
    }

    private boolean isInvalidSquare(String square) {
        char[] parts = square.toCharArray();
        if(parts[0] - 'a' < 0 || parts[0] - 'a' > 7) return true;
        if(parts[1] - '1' < 0 || parts[1] - '1' > 7) return true;
        return false;
    }

    private char getColor(String square) {
        char[] parts = square.toCharArray();
        int index = ('h' - parts[0]) + 8 * ('8' - parts[1]);
        if((occupancy[0] & 1L<<index) == 1) return 'w';
        else if ((occupancy[1] & 1L<<index) == 1) return 'b';
        else return '-';
    }

    public boolean inCheck(char color) {
        String kingTile = getKingTile();
        HashMap<String, Integer> enemyTiles = getEnemyTiles(color);
        char enemyColor = color == 'w' ? 'b':'w';
        enemyTiles.forEach((square, piece) -> {
            if(getMoves(piece, square).contains(kingTile)) return true;
        });
        return false;
    }

    public <T> T simulateMove(Move move, Supplier<T> method) {
        int enemyPiece = getPiece(move.getDestination());
        String orig = move.getOrigin();
        String dest = move.getDestination();

        movePiece(orig, dest);

        try{
            return method.get();
        } finally {
            undoMove(orig, dest, enemyPiece);
        }
    }

    public String getKingTile(char color) {
        return color == 'w' ? whiteKingTile : blackKingTile;
    }
}
