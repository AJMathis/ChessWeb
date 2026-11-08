public class PieceUtils {
    public static char toChar(int piece, int color) {
        char charPiece;
        charPiece = switch(piece) {
            case 1 -> 'n';
            case 2 -> 'b';
            case 3 -> 'r';
            case 4 -> 'q';
            case 5 -> 'k';
            default -> 'p';
        };
        if(color == 0) charPiece-=32;
        return charPiece;
    }
}
