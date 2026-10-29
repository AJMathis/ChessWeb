import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Framework {
    BitBoard board;

    public Framework() {
        this.board = new BitBoard();
    }

    public boolean isValidMove(Move move, int turn) {
        int piece = move.getIntPiece();
        int orig = SQRUtils.toIndex(move.getOrigin());
        int dest = SQRUtils.toIndex(move.getDestination());
        int color = move.getColor()=='w'?0:1;

        if(turn != color) return false;
        if((orig < 0 || orig > 63) || (dest < 0 || dest > 63)) return false;
        if(board.getColor(dest) == color) return false;
        if(!getMoves(piece, orig, color, false).contains(dest)) return false;
        if(simulateMove(orig, dest, () -> this.inCheck(color))) return false;
        return true;
    }

    public void makeMove(Move move) {
        int color = move.getColor() == 'w'? 0:1;
        if(move.getCastling()) {
            board.movePiece(SQRUtils.toIndex(move.getCastlingRook()), SQRUtils.toIndex(move.getCastlingRookDest()));
        }
        board.movePiece(SQRUtils.toIndex(move.getOrigin()), SQRUtils.toIndex(move.getDestination()));
        if(move.getIntPiece() == 5) {
            board.kingMoved[color] = true;
            if(color == 0) board.whiteKingTile = SQRUtils.toIndex(move.getDestination());
            else board.blackKingTile = SQRUtils.toIndex(move.getDestination());
        }
        else if(move.getIntPiece() == 3) {
            if(SQRUtils.toIndex(move.getOrigin())%8 == 7) board.queenRookMoved[color] = true;
            else if(SQRUtils.toIndex(move.getOrigin())%8 == 0) board.kingRookMoved[color] = true;
        }
    }

    private HashMap<Integer, Integer> getEnemyTiles (int color) {
        HashMap<Integer, Integer> map = new HashMap<>();
        for(int i = 0; i < 64; i++) {
            if((board.occupancy[color^1] & (1L<<i)) != 0) {
                map.put(i, board.getPiece(i));
            }
        }
        return map;
    }

    private boolean inCheck(int color) {
        int kingTile = board.getKingTile(color);
        HashMap<Integer, Integer> enemyTiles = getEnemyTiles(color);

        for(Map.Entry<Integer, Integer> entry : enemyTiles.entrySet()) {
            if(getMoves(entry.getValue(), entry.getKey(), color, true).contains(kingTile)) return true;
        }
        return false;
    }

    private <T> T simulateMove(int orig, int dest, Supplier<T> method) {
        int enemyPiece = board.getPiece(dest);

        board.movePiece(orig, dest);

        try{
            return method.get();
        } finally {
            board.undoMove(orig, dest, enemyPiece);
        }
    }

    private ArrayList<Integer> getMoves(int piece, int square, int color, boolean simKing) {
        return switch(piece) {
            case 0 -> getPawnMoves(square, color);
            case 1 -> getKnightMoves(square, color);
            case 2 -> getBishopMoves(square, color);
            case 3 -> getRookMoves(square, color);
            case 4 -> getQueenMoves(square, color);
            case 5 -> simKing ? getSimKingMoves(square, color) : getKingMoves(square, color);
            default -> null;
        };
    }
    private ArrayList<Integer> getPawnMoves(int sqr, int color) {
        ArrayList<Integer> moves = new ArrayList<>();
        if(color == 0) {
            if(sqr/8 == 1) {
                if((sqr+16) <= 63 && (board.occupancy[2] & (1L<<(sqr + 8))) == 0 && (board.occupancy[2] & (1L<<(sqr + 16))) == 0) moves.add(sqr+16);
            }
            if((sqr+8) <= 63 && (board.occupancy[2] & (1L<<(sqr + 8))) == 0) moves.add(sqr+8);
            if((sqr+7) <= 63 && ((board.occupancy[color^1] & (1L<<(sqr + 7))) != 0 || board.enPassant == (1<<(sqr+7)))) moves.add(sqr+7);
            if((sqr+9) <= 63 && ((board.occupancy[color^1] & (1L<<(sqr + 9))) != 0 || board.enPassant == (1<<(sqr+9)))) moves.add(sqr+9);
        } else {
            if(sqr/8 == 6 && color==1) {
                if ((sqr - 16) <= 63 && (board.occupancy[2] & (1L << (sqr - 8))) == 0 && (board.occupancy[2] & (1L << (sqr - 16))) == 0)
                    moves.add(sqr - 16);
            }
            if((sqr-8) >= 0 && ((board.occupancy[2] & (1L<<(sqr - 8))) == 0)) moves.add(sqr+8);
            if((sqr-7) >= 0 && ((board.occupancy[color^1] & (1L<<(sqr - 7))) != 0 || board.enPassant == (1<<(sqr-7)))) moves.add(sqr-7);
            if((sqr-9) >= 0 && ((board.occupancy[color^1] & (1L<<(sqr - 9))) != 0 || board.enPassant == (1<<(sqr-9)))) moves.add(sqr-9);
        }

        //ADD En PASSANT
        return moves;
    }
    private ArrayList<Integer> getKnightMoves(int sqr, int color) {
        ArrayList<Integer> moves = new ArrayList<>();
        if(sqr % 8 > 0) {
            if((sqr+15) <= 63 && (board.occupancy[color] & (1L<<(sqr + 15))) == 0) moves.add(sqr+15);
            if((sqr-17) >= 0 && (board.occupancy[color] & (1L<<(sqr - 17))) == 0) moves.add(sqr-17);
            if(sqr % 8 > 1) {
                if((sqr+6) <= 63 && (board.occupancy[color] & (1L<<(sqr + 15))) == 0) moves.add(sqr+6);
                if((sqr-10) >= 0 && (board.occupancy[color] & (1L<<(sqr - 10))) == 0) moves.add(sqr-10);
            }
        }
        if(sqr % 8 < 7) {
            if((sqr+17) <= 63 && (board.occupancy[color] & (1L<<(sqr + 15))) == 0) moves.add(sqr+15);
            if((sqr-15) >= 0 && (board.occupancy[color] & (1L<<(sqr - 17))) == 0) moves.add(sqr-17);
            if(sqr % 8 < 6) {
                if((sqr+10) <= 63 && (board.occupancy[color] & (1L<<(sqr + 15))) == 0) moves.add(sqr+6);
                if((sqr-6) >= 0 && (board.occupancy[color] & (1L<<(sqr - 10))) == 0) moves.add(sqr-10);
            }
        }
        return moves;
    }
    private ArrayList<Integer> getBishopMoves(int sqr, int color) {
        ArrayList<Integer> moves = new ArrayList<>();
        int k = sqr;
        while(k < 64) {
            k+=9;
            if(k%8 == 0) break;
            if((board.occupancy[color] & (1L<<k)) == 0) {
                moves.add(k);
                if((board.occupancy[color^1] & (1L<<k)) != 0) break;
            } else {break;}
        }
        k = sqr;
        while(k < 64) {
            k+=7;
            if(k % 8 == 7) break;
            if((board.occupancy[color] & (1L<<k)) == 0) {
                moves.add(k);
                if((board.occupancy[color^1] & (1L<<k)) != 0) break;
            } else {break;}
        }
        k = sqr;
        while(k > 0) {
            k-=7;
            if(k % 8 == 0) break;
            if((board.occupancy[color] & (1L<<k)) == 0) {
                moves.add(k);
                if((board.occupancy[color^1] & (1L<<k)) != 0) break;
            } else {break;}
        }
        k = sqr;
        while(k > 0) {
            k-=9;
            if(k % 8 == 7) break;
            if((board.occupancy[color] & (1L<<k)) == 0) {
                moves.add(k);
                if((board.occupancy[color^1] & (1L<<k)) != 0) break;
            } else {break;}
        }
        return moves;
    }
    private ArrayList<Integer> getRookMoves(int sqr, int color) {
        ArrayList<Integer> moves = new ArrayList<>();
        int k = sqr;
        while(k < 64) {
            k+=8;
            if((board.occupancy[color] & (1L<<k)) == 0) {
                moves.add(k);
                if((board.occupancy[color^1] & (1L<<k)) != 0) break;
            } else {break;}
        }
        k = sqr;
        while(k < 64) {
            k+=1;
            if(k % 8 == 0) break;
            if((board.occupancy[color] & (1L<<k)) == 0) {
                moves.add(k);
                if((board.occupancy[color^1] & (1L<<k)) != 0) break;
            } else {break;}
        }
        k = sqr;
        while(k > 0) {
            k-=1;
            if(k % 8 == 7) break;
            if((board.occupancy[color] & (1L<<k)) == 0) {
                moves.add(k);
                if((board.occupancy[color^1] & (1L<<k)) != 0) break;
            } else {break;}
        }
        k = sqr;
        while(k > 0) {
            k-=8;
            if((board.occupancy[color] & (1L<<k)) == 0) {
                moves.add(k);
                if((board.occupancy[color^1] & (1L<<k)) != 0) break;
            } else {break;}
        }
        return moves;
    }
    private ArrayList<Integer> getQueenMoves(int sqr, int color) {
        ArrayList<Integer> moves = new ArrayList<>();
        moves.addAll(getBishopMoves(sqr, color));
        moves.addAll(getRookMoves(sqr, color));
        return moves;
    }
    private ArrayList<Integer> getKingMoves(int sqr, int color) {
        ArrayList<Integer> moves = new ArrayList<>();
        if(sqr % 8 > 0) {
            if((board.occupancy[color] & (1L<<(sqr+7))) == 0 && !simulateMove(sqr, (sqr+7), () -> inCheck(color))) moves.add(sqr+7);
            if((board.occupancy[color] & (1L<<(sqr-1))) == 0 && !simulateMove(sqr, (sqr-1), () -> inCheck(color))) moves.add(sqr-1);
            if((board.occupancy[color] & (1L<<(sqr-9))) == 0 && !simulateMove(sqr, (sqr-9), () -> inCheck(color))) moves.add(sqr-9);
        }
        if(sqr % 8 < 7) {
            if((board.occupancy[color] & (1L<<(sqr+9))) == 0 && !simulateMove(sqr, (sqr+9), () -> inCheck(color))) moves.add(sqr+9);
            if((board.occupancy[color] & (1L<<(sqr+1))) == 0 && !simulateMove(sqr, (sqr+1), () -> inCheck(color))) moves.add(sqr+1);
            if((board.occupancy[color] & (1L<<(sqr-7))) == 0 && !simulateMove(sqr, (sqr-7), () -> inCheck(color))) moves.add(sqr-7);
        }
        if((board.occupancy[color] & (1L<<(sqr+8))) == 0 && !simulateMove(sqr, (sqr+8), () -> inCheck(color))) moves.add(sqr+8);
        if((board.occupancy[color] & (1L<<(sqr-8))) == 0 && !simulateMove(sqr, (sqr-8), () -> inCheck(color))) moves.add(sqr-8);
        if(board.kingMoved[color] == false && (board.occupancy[2] & (1L<<(sqr+1))) == 0 && (board.occupancy[2] & (1L<<(sqr+2))) == 0
                && !simulateMove(sqr, (sqr+1), () -> inCheck(color)) && !simulateMove(sqr, (sqr+2), () -> inCheck(color)) && board.kingRookMoved[color] == false) {
            moves.add(sqr+2);
        }
        if(board.kingMoved[color] == false && (board.occupancy[2] & (1L<<(sqr-1))) == 0 && (board.occupancy[2] & (1L<<(sqr-2))) == 0 && (board.occupancy[2] & (1L<<(sqr-3))) == 0
                && !simulateMove(sqr, (sqr-1), () -> inCheck(color)) && !simulateMove(sqr, (sqr-2), () -> inCheck(color)) && board.queenRookMoved[color] == false) {
            moves.add(sqr-2);
        }
        return moves;
    }
    private ArrayList<Integer> getSimKingMoves(int sqr, int color) {
        ArrayList<Integer> moves = new ArrayList<>();
        if(sqr % 8 > 0) {
            if((board.occupancy[color] & (1L<<(sqr+7))) == 0) moves.add(sqr+7);
            if((board.occupancy[color] & (1L<<(sqr-1))) == 0) moves.add(sqr-1);
            if((board.occupancy[color] & (1L<<(sqr-9))) == 0) moves.add(sqr-9);
        }
        if(sqr % 8 < 7) {
            if((board.occupancy[color] & (1L<<(sqr+9))) == 0) moves.add(sqr+9);
            if((board.occupancy[color] & (1L<<(sqr+1))) == 0) moves.add(sqr+1);
            if((board.occupancy[color] & (1L<<(sqr-7))) == 0) moves.add(sqr-7);
        }
        if((board.occupancy[color] & (1L<<(sqr+8))) == 0) moves.add(sqr+8);
        if((board.occupancy[color] & (1L<<(sqr-8))) == 0) moves.add(sqr-8);
        return moves;
    }


}
