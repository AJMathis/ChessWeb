public class Move {
    String destination;
    String origin;
    char piece;
    char color;

    public Move(char piece, String origin, String destination) {
        this.piece = piece;
        this.origin = origin;
        this.destination = destination;
        this.color = Character.isLowerCase(piece) ? 'b' : 'w';
    }

    public String getDestination() {return destination;}

    public String getOrigin() {return origin;}

    public char getPiece() {return piece;}
    public char getColor() {return color;}

    public int getIntPiece() {
        return switch (piece) {
            case 'p'|'P' -> 0;
            case 'n'|'N' -> 1;
            case 'b'|'B' -> 2;
            case 'r'|'R' -> 3;
            case 'q'|'Q' -> 4;
            case 'k'|'K' -> 5;
            default -> -1;
        };
    }
}
