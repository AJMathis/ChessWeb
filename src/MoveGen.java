import java.util.Stack;

public class MoveGen {
    final int NULL = -1;
    final int WHITE = 0;
    final int BLACK = 1;
    final int PAWN = 0;
    final int KNIGHT = 1;
    final int BISHOP = 2;
    final int ROOK = 3;
    final int QUEEN = 4;
    final int KING = 5;

    Board board;
    int playerTurn;
    Stack<Integer> enPassant;
    int[] kingSqr;

    public MoveGen(){
        board = new Board();
        playerTurn = 0;
        enPassant = new Stack<>();
        kingSqr = new int[]{4,60};
    }

    public void move(int from, int to) {
        int color = board.getColor(from);
        if (color != playerTurn) throw new IllegalArgumentException();
        if(validMove(from, to)) {
            board.move(from, to);
            playerTurn ^= 1;

            int passant = getEnPassant(from, to);
            if(passant != NULL) enPassant.add(passant);
        }
    }
    private void undoMove(int from, int to) {
        int color = board.getColor(to);
        if(color == playerTurn) throw new IllegalArgumentException();

        board.move(to ,from);
        playerTurn ^= 1;
        enPassant.pop();
    }

    public long getMoves(int sqr, int piece) {
        return switch(piece) {
            case PAWN -> getPawnMoves(sqr);
            case KNIGHT -> getKnightMoves(sqr);
            case BISHOP -> getBishopMoves(sqr);
            case ROOK -> getRookMoves(sqr);
            case QUEEN -> getQueenMoves(sqr);
            case KING -> getKingMoves(sqr);
            default -> NULL;
        };
    }
    public long getPawnMoves(int sqr) {
        int color = board.getColor(sqr);
        int multi = color==WHITE?1:-1;
        int startRank = color == WHITE ? 1 : 6;
        long moves = 0L;

        if(!board.isOccupied(sqr+(8* multi)) && board.onBoard(sqr+(8*multi))) {
            board.setSqr(moves, sqr+(8* multi), color);
            if(board.getRank(sqr) == startRank && !board.isOccupied(sqr+(16* multi)) && board.onBoard(sqr+(16*multi))) {
                board.setSqr(moves, sqr + (16 * multi), color);
            }
        }
        if(board.isOccupied(sqr+(7* multi), color^1) && board.onBoard(sqr+(7*multi))) {
            board.setSqr(moves, sqr+(7* multi), color);
        }
        if(board.isOccupied(sqr+(9* multi), color^1) && board.onBoard(sqr+(9*multi))){
            board.setSqr(moves, sqr+(9* multi), color);
        }
        return moves;
    }
    public long getKnightMoves(int sqr) {
        int color = board.getColor(sqr);
        long moves = 0L;

        if(sqr % 8 < 7) {
            if(board.onBoard(sqr+17) && !board.isOccupied(sqr+17, color)) board.setSqr(moves, sqr+17, color);
            if(board.onBoard(sqr-15) && !board.isOccupied(sqr-15, color)) board.setSqr(moves, sqr-15, color);
            if(sqr%8<6) {
                if(board.onBoard(sqr+10) && !board.isOccupied(sqr+10, color)) board.setSqr(moves, sqr+10, color);
                if(board.onBoard(sqr-6) && !board.isOccupied(sqr-6, color)) board.setSqr(moves, sqr-6, color);
            }
        }
        if(sqr % 8 > 0) {
            if(board.onBoard(sqr-17) && !board.isOccupied(sqr-17, color)) board.setSqr(moves, sqr-17, color);
            if(board.onBoard(sqr+15) && !board.isOccupied(sqr+15, color)) board.setSqr(moves, sqr+15, color);
            if(sqr%8 > 1) {
                if(board.onBoard(sqr-10) && !board.isOccupied(sqr-10, color)) board.setSqr(moves, sqr-10, color);
                if(board.onBoard(sqr+6) && !board.isOccupied(sqr+6, color)) board.setSqr(moves, sqr+6, color);
            }
        }

        return moves;
    }
    public long getBishopMoves(int sqr) {
        int color = board.getColor(sqr);
        long moves = 0L;
        moves |= slidingMove(sqr, color, 1,1);
        moves |= slidingMove(sqr, color, -1,1);
        moves |= slidingMove(sqr, color, 1,-1);
        moves |= slidingMove(sqr, color, -1,-1);

        return moves;
    }
    public long getRookMoves(int sqr) {
        int color = board.getColor(sqr);
        long moves = 0L;
        moves |= slidingMove(sqr, color, 1,0);
        moves |= slidingMove(sqr, color, -1,0);
        moves |= slidingMove(sqr, color, 0,1);
        moves |= slidingMove(sqr, color, 0,-1);

        return moves;
    }
    public long getQueenMoves(int sqr) {
        int color = board.getColor(sqr);
        long moves = 0L;
        moves |= slidingMove(sqr, color, 1,0);
        moves |= slidingMove(sqr, color, -1,0);
        moves |= slidingMove(sqr, color, 0,1);
        moves |= slidingMove(sqr, color, 0,-1);
        moves |= slidingMove(sqr, color, 1,1);
        moves |= slidingMove(sqr, color, -1,1);
        moves |= slidingMove(sqr, color, 1,-1);
        moves |= slidingMove(sqr, color, -1,-1);

        return moves;
    }
    public long getKingMoves(int sqr) {
        int color = board.getColor(sqr);
        long moves = 0L;

        if(!board.isOccupied(sqr+8, color) && board.onBoard(sqr+8)) board.setSqr(moves, sqr+8, color);
        if(!board.isOccupied(sqr-8, color) && board.onBoard(sqr-8)) board.setSqr(moves, sqr-8, color);
        if(sqr %  8 < 7) {
            if(!board.isOccupied(sqr+9, color) && board.onBoard(sqr+9)) board.setSqr(moves, sqr+9, color);
            if(!board.isOccupied(sqr+1, color) && board.onBoard(sqr+1)) board.setSqr(moves, sqr+1, color);
            if(!board.isOccupied(sqr-7, color) && board.onBoard(sqr-7)) board.setSqr(moves, sqr-7, color);
        }
        if(sqr %  8 > 0) {
            if(!board.isOccupied(sqr-9, color) && board.onBoard(sqr-9)) board.setSqr(moves, sqr-9, color);
            if(!board.isOccupied(sqr-1, color) && board.onBoard(sqr-1)) board.setSqr(moves, sqr-1, color);
            if(!board.isOccupied(sqr+7, color) && board.onBoard(sqr+7)) board.setSqr(moves, sqr+7, color);
        }
        return moves;
    }
    private long getAllMoves(int color) {
        long moves = 0L;

        for(int i = 0; i < 64; i++) {
            if(board.isOccupied(i, color)) {
                int piece = board.getPiece(i);
                moves |= getMoves(i, piece);
            }
        }
        return moves;
    }

    private long slidingMove(int sqr, int color, int x, int y) {
        int displace = (y*8)+x;
        int currSqr = sqr+displace;
        long moves = 0L;

        while(true) {
            if((x>0 && currSqr % 8 == 0) || (x < 0 && currSqr % 8 == 7)) break;
            if(board.onBoard(sqr+displace) && !board.isOccupied(sqr, color)) board.setSqr(moves, currSqr+displace, color);
            if(board.isOccupied(sqr)) break;
            currSqr+=displace;
        }
        return moves;
    }
    private boolean validMove(int from, int to) {
        int piece = board.getPiece(from);
        int color = board.getColor(from);
        if(piece == NULL) return false;

        long moves = getMoves(from, piece);
        return board.isOccupied(to, moves) && !inCheck(from, to, color);
    }
    private boolean inCheck (int from, int to, int color) {

        move(from, to);

        long moves = getAllMoves(color^1);
        boolean valid = board.isOccupied(kingSqr[color], moves);
        undoMove(from, to);
        return valid;
    }

    private int getEnPassant(int from, int to) {
        int piece = board.getPiece(from);
        if(piece == PAWN) {
            int color = board.getColor(from);
            int multi = color==WHITE?1:-1;

            if(from == (to + (16 * multi))) return from + (8 * multi);
        }
        return NULL;
    }

}
