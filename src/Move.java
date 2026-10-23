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
        return switch (Character.toLowerCase(piece)) {
            case 'p' -> 0;
            case 'n' -> 1;
            case 'b' -> 2;
            case 'r' -> 3;
            case 'q' -> 4;
            case 'k' -> 5;
            default -> -1;
        };
    }
}
