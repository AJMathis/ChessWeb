import java.util.ArrayList;

public class GameState {
    ArrayList<String> history;
    char lastPiece;
    String board;
    char turn;
    String castling;
    String enPassant;
    int halfMoves;
    int fullMoves;

    public GameState() {
        this.board = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        this.turn = 'w';
        this.castling = "KQkq";
        this.enPassant = "-";
        this.halfMoves = 0;
        this.fullMoves = 1;
        this.history = new ArrayList<>();
    }

    public int getTurn() {return turn;}

    public String getFEN() {
        StringBuilder str = new StringBuilder();

        str.append(this.board);
        str.append(" ");
        str.append(this.turn);
        str.append(" ");
        str.append(this.castling);
        str.append(" ");
        str.append(this.enPassant);
        str.append(" ");
        str.append(this.halfMoves);
        str.append(" ");
        str.append(this.fullMoves);

        return str.toString();
    }

    public ArrayList<String> getHistory() {return history;}

    public void addMove(Move move) {
        if(!confirmValidMove(move)) {
            throw new IllegalArgumentException("NOT A VALID MOVE");
        };
        this.history.add(move.getOrigin() + move.getDestination());
        this.turn = this.turn == 'w' ? 'b' : 'w';
        this.lastPiece = move.getPiece();
        updateFEN();
    }

    public void updateFEN() {
        char[] moves = history.getLast().toCharArray();
        int destFileIndx = (moves[2] - 'a');
        int destRankIndx = 8 - (moves[3] - '0');
        int origFileIndx = (moves[0] - 'a');
        int origRankIndx = 8 - (moves[1] - '0');

        char[][] boardRep = parseBoard(this.board);
        char piece = this.lastPiece;
        boolean capture = boardRep[destFileIndx][destRankIndx] != '.';

        boardRep[origFileIndx][origRankIndx] = '.';
        boardRep[destFileIndx][destRankIndx] = piece;
        this.board = fenBoard(boardRep);

        String castling = this.castling;
        if(!castling.equals("-")) {
            if (piece == 'K') castling = castling.replace("K", "").replace("Q", "");
            else if (piece == 'k') castling = castling.replace("k", "").replace("q", "");
            else if (piece == 'R' && ((moves[0] == 'a') && (moves[1] == '1'))) castling = castling.replace("Q", "");
            else if (piece == 'R' && ((moves[0] == 'h') && (moves[1] == '1'))) castling = castling.replace("K", "");
            else if (piece == 'r' && ((moves[0] == 'a') && (moves[1] == '8'))) castling = castling.replace("q", "");
            else if (piece == 'r' && ((moves[0] == 'h') && (moves[1] == '8'))) castling = castling.replace("k", "");
            if (((moves[2] == 'a') && (moves[3] == '1'))) castling = castling.replace("Q", "");
            if (((moves[2] == 'h') && (moves[3] == '1'))) castling = castling.replace("K", "");
            if (((moves[2] == 'a') && (moves[3] == '8'))) castling = castling.replace("q", "");
            if (((moves[2] == 'h') && (moves[3] == '8'))) castling = castling.replace("k", "");

            if (castling.isEmpty()) castling = "-";
            this.castling = castling;
        }

        String enPassant = "-";
        if(piece == 'P' && moves[1] == '2' && moves[3] == '4') enPassant = (String.valueOf(moves[0]) + "3");
        else if(piece == 'p' && moves[1] == '7' && moves[3] == '5') enPassant = (String.valueOf(moves[0]) + "6");
        this.enPassant = enPassant;

        int halfMove = this.halfMoves;
        if(capture || piece == 'p' || piece == 'P') halfMove = 0;
        else halfMove++;
        this.halfMoves = halfMove;

        int fullMove = this.fullMoves;
        if (this.turn == 'w') fullMove++;
        this.fullMoves = fullMove;
    }

    public char[][] parseBoard(String fenBoard) {
        char[][] board = new char[8][8];
        String[] ranks = fenBoard.split("/");
        for(int i = 0; i < ranks.length; i++) {
            int j = 0;
            for(char pce : ranks[i].toCharArray()) {
                if(Character.isDigit(pce)) {
                    for(int k = 0; k < (pce - '0'); k++) {
                        board[j++][i] = '.';
                    }
                } else {
                    board[j++][i] = pce;
                }
            }
        }
        return board;
    }

    public String fenBoard(char[][] board) {
        ArrayList<String> fenBoard = new ArrayList<>();
        int empty = 0;
        for(int rank = 0; rank < 8; rank++) {
            for(int file = 0; file < 8; file++) {
                if(board[file][rank] == '.') empty++;
                else {
                    if(empty > 0) {
                        fenBoard.add(String.valueOf(empty));
                        empty = 0;
                    }
                    fenBoard.add(String.valueOf(board[file][rank]));
                }
            }
            if(empty > 0) {
                fenBoard.add(String.valueOf(empty));
                empty = 0;
            }
            fenBoard.add("/");
        }
        fenBoard.removeLast();
        return String.join("", fenBoard);

    }

    public boolean confirmValidMove(Move move) {
        boolean validMove = true;
        char piece = move.getPiece();
        char[] origin = move.getOrigin().toCharArray();
        char[] dest = move.getDestination().toCharArray();

        char[][] board = parseBoard(this.board);

        if (origin[0] < 'a' || origin[0] > 'h' ||
                origin[1] < '1' || origin[1] > '8') {
            validMove = false;
        }
        if (dest[0] < 'a' || dest[0] > 'h' ||
                dest[1] < '1' || dest[1] > '8') {
            validMove = false;
        }
        if(board[origin[0] - 'a'][8-(origin[1]-'0')] != piece) validMove = false;
        return validMove;
    }
}
