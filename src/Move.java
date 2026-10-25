public class Move {
    String destination;
    String origin;
    char piece;
    char color;
    boolean castling;
    String castlingDest;
    String castlingRook;

    public Move(char piece, String origin, String destination) {
        this.piece = piece;
        this.origin = origin;
        this.destination = destination;
        this.color = Character.isLowerCase(piece) ? 'b' : 'w';
        if((piece == 'k'|| piece == 'K')) {
            int dif =destination.toCharArray()[0] - origin.toCharArray()[0];
            if(dif == 2) {
                castling = true;
                castlingRook = ("h" + origin.toCharArray()[1]);
                castlingDest = ("f" + origin.toCharArray()[1]);
            }
            else if(dif == -2) {
                castling = true;
                castlingRook = ("a" + origin.toCharArray()[1]);
                castlingDest = ("d" + origin.toCharArray()[1]);
            }
        }
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

    public boolean getCastling() {return castling;}

    public String getCastlingRookDest() {return castlingDest;}

    public String getCastlingRook() {return castlingRook;}
}
