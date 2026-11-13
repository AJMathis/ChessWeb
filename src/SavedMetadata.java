import java.util.ArrayList;
import java.util.Arrays;

public class SavedMetadata {
    ArrayList<Move> history;
    char lastPiece;
    String board;
    char turn;
    String castling;
    String enPassant;
    int halfMoves;
    int fullMoves;

    public SavedMetadata(ArrayList<Move> history, char lastPiece, String board, char turn, String castling,
                         String enPassant, int halfMoves, int fullMoves) {
        this.history = history;
        this.lastPiece = lastPiece;
        this.board = board;
        this.turn = turn;
        this.castling = castling;
        this.enPassant = enPassant;
        this.halfMoves = halfMoves;
        this.fullMoves = fullMoves;
    }
}
