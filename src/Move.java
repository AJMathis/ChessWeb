public class Move {
    String destination;
    String origin;
    char piece;
    char color;

    public Move(char piece, String origin, String destination, char color) {
        this.piece = piece;
        this.origin = origin;
        this.destination = destination;
        this.color = color;
    }

    public String getDestination() {return destination;}

    public String getOrigin() {return origin;}

    public char getPiece() {return piece;}
    public char getColor() {return color;}
}
