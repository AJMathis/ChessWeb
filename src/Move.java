public class Move {
    String destination;
    String origin;
    char piece;

    public Move(char piece, String origin, String destination) {
        this.piece = piece;
        this.origin = origin;
        this.destination = destination;
    }

    public String getDestination() {return destination;}

    public String getOrigin() {return origin;}

    public char getPiece() {return piece;}
}
