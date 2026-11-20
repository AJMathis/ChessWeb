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
    Stack<Integer> takenPieces;
    int[] kingSqr;
    Stack<Integer> kingMoved;
    Stack<Integer> rookMoved;

    public MoveGen(){
        board = new Board();
        playerTurn = WHITE;
        enPassant = new Stack<>();
        enPassant.add(NULL);
        takenPieces = new Stack<>();
        takenPieces.add(NULL);
        kingSqr = new int[]{4,60};
        kingMoved = new Stack<>();
        kingMoved.add(0);
        rookMoved = new Stack<>();
        rookMoved.add(0);
    }
    public void move(int from, int to) {move(from, to, true);}
    public void move(int from, int to, boolean check) {
        int color = board.getColor(from);
        int piece = board.getPiece(from);
        if (check && color != playerTurn) throw new IllegalArgumentException();
        if(check && !validMove(from, to)) throw new IllegalStateException();

        boolean isPassant = false;
        int takenPiece = board.getPiece(to);
        if(takenPiece==NULL && enPassant.peek()==to && piece==PAWN) {
            takenPiece = PAWN;
            isPassant = true;
        }
        int rookMoving = rookMoved.peek();
        if(takenPiece==ROOK) {
            int rank = (color^1)==WHITE?0:7;
            if((rookMoved.peek() & 3<<((color^1)*2)) != 3<<((color^1) * 2)) {
                if(to==(rank*8) && (rookMoved.peek() & 1<<((color^1)*2)) == 0) {
                    rookMoving |=  (1<<((color^1)*2));
                }
                if(to==(rank*8+7) && (rookMoved.peek() & 1<<(((color^1)*2)+1)) == 0) {
                    rookMoving |= (1<<(((color^1)*2)+1));
                }
            }
        }
        boolean isCastling = false;
        if(piece==KING && (to==from+2 || to==from-2)) {
            isCastling = true;
        }
        int passant = getEnPassant(from, to);
        enPassant.add(passant);
        playerTurn ^= 1;
        if(piece==KING) {
            kingSqr[color] = to;
            kingMoved.add(kingMoved.peek() | (1<<color));
        }
        if(piece==ROOK) {
            int file = board.getFile(from);
            if(file == 0 || file == 7) {
                int side = file == 0 ? 0 : 1;
                int displace = ((color * 2) + side);
                rookMoving |= ((1 << displace));
            }
        }

        rookMoved.add(rookMoving);
        takenPieces.add(takenPiece);
        board.move(from, to, isPassant, isCastling);
    }
    public void undoMove(int from, int to) {
        int color = board.getColor(to);
        int piece = board.getPiece(to);

        playerTurn ^= 1;
        enPassant.pop();
        rookMoved.pop();
        if(piece==KING) {
            kingSqr[color] = from;
            kingMoved.pop();
        }

        board.move(to ,from);
        int taken = takenPieces.pop();
        if(taken != NULL ) {
            if(to==enPassant.peek()){
                int displace = color==WHITE?-8:8;
                board.setSqr(to+displace, taken, color^1);
            } else {
                board.setSqr(to, taken, color^1);
            }
        }
        if(piece == KING) {
            int rank = color==WHITE?0:7;
            if(to == from+2) {
                board.clearSqr(to-1, ROOK, color);
                board.setSqr((rank*8)+7, ROOK, color);
            } else if (to == from-2) {
                board.clearSqr(to+1, ROOK, color);
                board.setSqr((rank*8), ROOK, color);
            }
        }
    }

    public long getMoves(int sqr, boolean attacking) {
        int piece = board.getPiece(sqr);
        return switch(piece) {
            case PAWN -> getPawnMoves(sqr);
            case KNIGHT -> getKnightMoves(sqr);
            case BISHOP -> getBishopMoves(sqr);
            case ROOK -> getRookMoves(sqr);
            case QUEEN -> getQueenMoves(sqr);
            case KING -> getKingMoves(sqr, attacking);
            default -> 0L;
        };
    }
    public long getPawnMoves(int sqr) {
        int color = board.getColor(sqr);
        int multi = color==WHITE?1:-1;
        int startRank = color == WHITE ? 1 : 6;
        long moves = 0L;

        if(!board.isOccupied(sqr+(8* multi)) && board.onBoard(sqr+(8*multi))) {
            moves = board.setSqr(moves, sqr+(8* multi), color);
            if(board.getRank(sqr) == startRank && !board.isOccupied(sqr+(16* multi)) && board.onBoard(sqr+(16*multi))) {
                moves = board.setSqr(moves, sqr + (16 * multi), color);
            }
        }
        if(sqr%8>0){
            if(color == WHITE && (board.isOccupied(sqr+7, color^1) || sqr+7 == enPassant.peek()) && board.onBoard(sqr+7)) {
                moves = board.setSqr(moves, sqr+7, color);
            }
            else if(color == BLACK && (board.isOccupied(sqr-9, color^1) || sqr-9 == enPassant.peek()) && board.onBoard(sqr-9)) {
                moves = board.setSqr(moves, sqr-9, color);
            }
        }
        if(sqr%8<7) {
            if(color == WHITE && (board.isOccupied(sqr+9, color^1) || sqr+9 == enPassant.peek()) && board.onBoard(sqr+9)) {
                moves = board.setSqr(moves, sqr+9, color);
            }
            else if(color==BLACK && (board.isOccupied(sqr-7, color^1) || sqr-7 == enPassant.peek()) && board.onBoard(sqr-7)) {
                moves = board.setSqr(moves, sqr-7, color);
            }
        }
        return moves;
    }
    public long getKnightMoves(int sqr) {
        int color = board.getColor(sqr);
        long moves = 0L;

        if(sqr % 8 < 7) {
            if(board.onBoard(sqr+17) && !board.isOccupied(sqr+17, color)) moves = board.setSqr(moves, sqr+17, color);
            if(board.onBoard(sqr-15) && !board.isOccupied(sqr-15, color)) moves = board.setSqr(moves, sqr-15, color);
            if(sqr%8<6) {
                if(board.onBoard(sqr+10) && !board.isOccupied(sqr+10, color)) moves = board.setSqr(moves, sqr+10, color);
                if(board.onBoard(sqr-6) && !board.isOccupied(sqr-6, color)) moves = board.setSqr(moves, sqr-6, color);
            }
        }
        if(sqr % 8 > 0) {
            if(board.onBoard(sqr-17) && !board.isOccupied(sqr-17, color)) moves = board.setSqr(moves, sqr-17, color);
            if(board.onBoard(sqr+15) && !board.isOccupied(sqr+15, color)) moves = board.setSqr(moves, sqr+15, color);
            if(sqr%8 > 1) {
                if(board.onBoard(sqr-10) && !board.isOccupied(sqr-10, color)) moves = board.setSqr(moves, sqr-10, color);
                if(board.onBoard(sqr+6) && !board.isOccupied(sqr+6, color)) moves = board.setSqr(moves, sqr+6, color);
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
    public long getKingMoves(int sqr, boolean attacking) {
        int color = board.getColor(sqr);
        long moves = 0L;

        if(!board.isOccupied(sqr+8, color) && board.onBoard(sqr+8)) moves = board.setSqr(moves, sqr+8, color);
        if(!board.isOccupied(sqr-8, color) && board.onBoard(sqr-8)) moves = board.setSqr(moves, sqr-8, color);
        if(sqr %  8 < 7) {
            if(!board.isOccupied(sqr+9, color) && board.onBoard(sqr+9)) moves = board.setSqr(moves, sqr+9, color);
            if(!board.isOccupied(sqr+1, color) && board.onBoard(sqr+1)) moves = board.setSqr(moves, sqr+1, color);
            if(!board.isOccupied(sqr-7, color) && board.onBoard(sqr-7)) moves = board.setSqr(moves, sqr-7, color);
        }
        if(sqr %  8 > 0) {
            if(!board.isOccupied(sqr-9, color) && board.onBoard(sqr-9)) moves = board.setSqr(moves, sqr-9, color);
            if(!board.isOccupied(sqr-1, color) && board.onBoard(sqr-1)) moves = board.setSqr(moves, sqr-1, color);
            if(!board.isOccupied(sqr+7, color) && board.onBoard(sqr+7)) moves = board.setSqr(moves, sqr+7, color);
        }
        if(!attacking && (kingMoved.peek() & (1<<color)) == 0) {
            int rookState = rookMoved.peek();
            if ((rookState & 3<<(color * 2)) != 3<<(color * 2)) {
                if ((rookState & 2 << (color * 2)) == 0) {
                    boolean valid = true;
                    for(int i = 0; i <= 2; i++) {
                        if (i == 0) {
                            if (inCheck(color)) valid = false;
                        } else {
                            if (board.isOccupied(sqr + i)) valid = false;
                            else if (inCheck(sqr, sqr + i, color)) valid = false;
                        }
                    }
                    if(valid) moves = board.setSqr(moves, sqr+2, color);
                }
                if ((rookState & 1 << (color * 2)) == 0) {
                    boolean valid = true;
                    for(int i = 0; i >= -2; i--) {

                        if (i == 0) {
                            if (inCheck(color)) valid = false;
                        } else {
                            if (board.isOccupied(sqr + i)) valid = false;
                            else if (inCheck(sqr, sqr + i, color)) valid = false;
                        }
                    }
                    if(board.isOccupied(sqr-3)) valid = false;
                    if(valid) moves = board.setSqr(moves, sqr-2, color);
                }
            }
        }
        return moves;
    }
    private long getAllMoves(int color, boolean attacking) {
        long moves = 0L;

        for(int i = 0; i < 64; i++) {
            if(board.isOccupied(i, color)) {
                int piece = board.getPiece(i);
                moves |= getMoves(i, attacking);
            }
        }
        return moves;
    }

    private long slidingMove(int sqr,  int color, int x, int y) {
        int displace = (y*8)+x;
        int currSqr = sqr+displace;
        long moves = 0L;

        while(true) {
            if((x>0 && currSqr % 8 == 0) || (x < 0 && currSqr % 8 == 7)) break;
            if(currSqr < 0 || currSqr > 63) break;
            if(board.onBoard(currSqr) && !board.isOccupied(currSqr, color)) moves = board.setSqr(moves, currSqr, color);
            if(board.isOccupied(currSqr)) break;
            currSqr+=displace;
        }
        return moves;
    }
    private boolean validMove(int from, int to) {
        int piece = board.getPiece(from);
        int color = board.getColor(from);
        if(piece == NULL) return false;

        long moves = getMoves(from, false);
        return board.isOccupied(to, moves) && !inCheck(from, to, color);
    }
    public boolean inCheck (int from, int to, int color) {

        move(from, to, false);

        long moves = getAllMoves(color^1, true);
        boolean valid = board.isOccupied(kingSqr[color], moves);
        undoMove(from, to);
        return valid;
    }
    public boolean inCheck(int color) {
        long moves = getAllMoves(color^1, true);
        return board.isOccupied(kingSqr[color], moves);
    }


    private int getEnPassant(int from, int to) {
        int piece = board.getPiece(from);
        if(piece == PAWN) {
            int color = board.getColor(from);
            int multi = color==WHITE?1:-1;

            if(to == (from + (16 * multi))) return from + (8 * multi);
        }
        return NULL;
    }

    public String toString() {
        return board.toString();
    }

}
