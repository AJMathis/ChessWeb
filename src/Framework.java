import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Framework {
    long[][] pieces;
    long[] occupancy;
    int whiteKingTile;
    int blackKingTile;

    int[] kingRookMoved = {0,0};
    int[] queenRookMoved = {0,0};
    int[] kingMoved = {0,0};

    public Framework() {
        pieces = new long[2][6];
        occupancy = new long[3];
        initBitmaps();
        whiteKingTile = 4;
        blackKingTile = 60;
    }

    private void initBitmaps() {
        //Init White pieces
        pieces[0][0] = 0x000000000000FF00L;
        pieces[0][1] = 0x0000000000000024L;
        pieces[0][2] = 0x0000000000000042L;
        pieces[0][3] = 0x0000000000000018L;
        pieces[0][4] = 0x0000000000000080L;
        pieces[0][5] = 0x0000000000000001L;
        //Init Black Pieces
        pieces[1][0] = 0x00FF000000000000L;
        pieces[1][1] = 0x2400000000000000L;
        pieces[1][2] = 0x4200000000000000L;
        pieces[1][3] = 0x1800000000000000L;
        pieces[1][4] = 0x8000000000000000L;
        pieces[1][5] = 0x0100000000000000L;
        //Init occupancy
        updateOccupancy();
    }


    public boolean isValidMove(Move move, int turn) {
        int piece = move.getIntPiece();
        int orig = SQRUtils.toIndex(move.getOrigin());
        int dest = SQRUtils.toIndex(move.getDestination());
        int color = move.getColor()=='w'?0:1;

        if(turn != color) return false;
        if((orig < 0 || orig > 63) || (dest < 0 || dest > 63)) return false;
        if(getColor(dest) == color) return false;
        if(!getMoves(piece, orig, color, false).contains(dest)) return false;
        if(inCheck(color) || simulateMove(orig, dest, () -> this.inCheck(color))) return false;
        return true;
    }

    public void makeMove(Move move) {
        int color = move.getColor() == 'w'? 0:1;
        if(move.getCastling()) {
            movePiece(SQRUtils.toIndex(move.getCastlingRook()), SQRUtils.toIndex(move.getCastlingRookDest()));
        }
        movePiece(SQRUtils.toIndex(move.getOrigin()), SQRUtils.toIndex(move.getDestination()));
        if(move.getIntPiece() == 5) {
            kingMoved[color] = 1;
            if(color == 0) whiteKingTile = SQRUtils.toIndex(move.getDestination());
            else blackKingTile = SQRUtils.toIndex(move.getDestination());
        }
        else if(move.getIntPiece() == 3) {
            if(SQRUtils.toIndex(move.getOrigin())%8 == 7) queenRookMoved[color] = 1;
            else if(SQRUtils.toIndex(move.getOrigin())%8 == 0) kingRookMoved[color] = 1;
        }
    }


    private int getColor(int square) {
        if((occupancy[0] & (1L<<square)) != 0) return 0;
        else if ((occupancy[1] & (1L<<square)) != 0) return 1;
        else return -1;
    }
    private int getPiece(int square) {
        if((occupancy[2] & (1L<<square)) != 0) {
           if((occupancy[0] & (1L<<square)) != 0) {
               for(int i = 0; i < 6; i++) {
                   if((pieces[0][i] & (1L<< square)) != 0) return i;
               }
           } else {
               for(int i = 0; i < 6; i++) {
                   if((pieces[1][i] & (1L<< square)) != 0) return i;
               }
           }
        }
        return -1;
    }
    private HashMap<Integer, Integer> getEnemyTiles (int color) {
        HashMap<Integer, Integer> map = new HashMap<>();
        for(int i = 0; i < 64; i++) {
            if((occupancy[color^1] & (1L<<i)) != 0) {
                map.put(i, getPiece(i));
            }
        }
        return map;
    }

    private boolean inCheck(int color) {
        int kingTile = getKingTile(color);
        HashMap<Integer, Integer> enemyTiles = getEnemyTiles(color);

        for(Map.Entry<Integer, Integer> entry : enemyTiles.entrySet()) {
            if(getMoves(entry.getValue(), entry.getKey(), color, true).contains(kingTile)) return true;
        }
        return false;
    }
    private <T> T simulateMove(int orig, int dest, Supplier<T> method) {
        int enemyPiece = getPiece(dest);

        movePiece(orig, dest);

        try{
            return method.get();
        } finally {
            undoMove(orig, dest, enemyPiece);
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
        if(sqr/8 == 1) {
            if((sqr+16) <= 63 && (occupancy[2] & (1L<<(sqr + 8))) == 0 && (occupancy[2] & (1L<<(sqr + 16))) == 0) moves.add(sqr+16);
        }
        if((sqr+8) <= 63 && (occupancy[2] & (1L<<(sqr + 8))) == 0) moves.add(sqr+8);
        if((sqr+7) <= 63 && (occupancy[color^1] & (1L<<(sqr + 8))) != 0) moves.add(sqr+7);
        if((sqr+9) <= 63 && (occupancy[color^1] & (1L<<(sqr + 8))) != 0) moves.add(sqr+9);

        //ADD En PASSANT
        return moves;
    }
    private ArrayList<Integer> getKnightMoves(int sqr, int color) {
        ArrayList<Integer> moves = new ArrayList<>();
        if(sqr % 8 > 0) {
            if((sqr+15) <= 63 && (occupancy[color] & (1L<<(sqr + 15))) == 0) moves.add(sqr+15);
            if((sqr-17) >= 0 && (occupancy[color] & (1L<<(sqr - 17))) == 0) moves.add(sqr-17);
            if(sqr % 8 > 1) {
                if((sqr+6) <= 63 && (occupancy[color] & (1L<<(sqr + 15))) == 0) moves.add(sqr+6);
                if((sqr-10) >= 0 && (occupancy[color] & (1L<<(sqr - 10))) == 0) moves.add(sqr-10);
            }
        }
        if(sqr % 8 < 7) {
            if((sqr+17) <= 63 && (occupancy[color] & (1L<<(sqr + 15))) == 0) moves.add(sqr+15);
            if((sqr-15) >= 0 && (occupancy[color] & (1L<<(sqr - 17))) == 0) moves.add(sqr-17);
            if(sqr % 8 < 6) {
                if((sqr+10) <= 63 && (occupancy[color] & (1L<<(sqr + 15))) == 0) moves.add(sqr+6);
                if((sqr-6) >= 0 && (occupancy[color] & (1L<<(sqr - 10))) == 0) moves.add(sqr-10);
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
            if((occupancy[color] & (1L<<k)) == 0) {
                moves.add(k);
                if((occupancy[color^1] & (1L<<k)) != 0) break;
            } else {break;}
        }
        k = sqr;
        while(k < 64) {
            k+=7;
            if(k % 8 == 7) break;
            if((occupancy[color] & (1L<<k)) == 0) {
                moves.add(k);
                if((occupancy[color^1] & (1L<<k)) != 0) break;
            } else {break;}
        }
        k = sqr;
        while(k > 0) {
            k-=7;
            if(k % 8 == 0) break;
            if((occupancy[color] & (1L<<k)) == 0) {
                moves.add(k);
                if((occupancy[color^1] & (1L<<k)) != 0) break;
            } else {break;}
        }
        k = sqr;
        while(k > 0) {
            k-=9;
            if(k % 8 == 7) break;
            if((occupancy[color] & (1L<<k)) == 0) {
                moves.add(k);
                if((occupancy[color^1] & (1L<<k)) != 0) break;
            } else {break;}
        }
        return moves;
    }
    private ArrayList<Integer> getRookMoves(int sqr, int color) {
        ArrayList<Integer> moves = new ArrayList<>();
        int k = sqr;
        while(k < 64) {
            k+=8;
            if((occupancy[color] & (1L<<k)) == 0) {
                moves.add(k);
                if((occupancy[color^1] & (1L<<k)) != 0) break;
            } else {break;}
        }
        k = sqr;
        while(k < 64) {
            k+=1;
            if(k % 8 == 0) break;
            if((occupancy[color] & (1L<<k)) == 0) {
                moves.add(k);
                if((occupancy[color^1] & (1L<<k)) != 0) break;
            } else {break;}
        }
        k = sqr;
        while(k > 0) {
            k-=1;
            if(k % 8 == 7) break;
            if((occupancy[color] & (1L<<k)) == 0) {
                moves.add(k);
                if((occupancy[color^1] & (1L<<k)) != 0) break;
            } else {break;}
        }
        k = sqr;
        while(k > 0) {
            k-=8;
            if((occupancy[color] & (1L<<k)) == 0) {
                moves.add(k);
                if((occupancy[color^1] & (1L<<k)) != 0) break;
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
            if((occupancy[color] & (1L<<(sqr+7))) == 0 && !simulateMove(sqr, (sqr+7), () -> inCheck(color))) moves.add(sqr+7);
            if((occupancy[color] & (1L<<(sqr-1))) == 0 && !simulateMove(sqr, (sqr-1), () -> inCheck(color))) moves.add(sqr-1);
            if((occupancy[color] & (1L<<(sqr-9))) == 0 && !simulateMove(sqr, (sqr-9), () -> inCheck(color))) moves.add(sqr-9);
        }
        if(sqr % 8 < 7) {
            if((occupancy[color] & (1L<<(sqr+9))) == 0 && !simulateMove(sqr, (sqr+9), () -> inCheck(color))) moves.add(sqr+9);
            if((occupancy[color] & (1L<<(sqr+1))) == 0 && !simulateMove(sqr, (sqr+1), () -> inCheck(color))) moves.add(sqr+1);
            if((occupancy[color] & (1L<<(sqr-7))) == 0 && !simulateMove(sqr, (sqr-7), () -> inCheck(color))) moves.add(sqr-7);
        }
        if((occupancy[color] & (1L<<(sqr+8))) == 0 && !simulateMove(sqr, (sqr+8), () -> inCheck(color))) moves.add(sqr+8);
        if((occupancy[color] & (1L<<(sqr-8))) == 0 && !simulateMove(sqr, (sqr-8), () -> inCheck(color))) moves.add(sqr-8);
        if(kingMoved[color] == 0 && (occupancy[2] & (1L<<(sqr+1))) == 0 && (occupancy[2] & (1L<<(sqr+2))) == 0
                && !simulateMove(sqr, (sqr+1), () -> inCheck(color)) && !simulateMove(sqr, (sqr+2), () -> inCheck(color)) && kingRookMoved[color] == 0) {
            moves.add(sqr+2);
        }
        if(kingMoved[color] == 0 && (occupancy[2] & (1L<<(sqr-1))) == 0 && (occupancy[2] & (1L<<(sqr-2))) == 0 && (occupancy[2] & (1L<<(sqr-3))) == 0
                && !simulateMove(sqr, (sqr-1), () -> inCheck(color)) && !simulateMove(sqr, (sqr-2), () -> inCheck(color)) && queenRookMoved[color] == 0) {
            moves.add(sqr-2);
        }
        return moves;
    }
    private ArrayList<Integer> getSimKingMoves(int sqr, int color) {
        ArrayList<Integer> moves = new ArrayList<>();
        if(sqr % 8 > 0) {
            if((occupancy[color] & (1L<<(sqr+7))) == 0) moves.add(sqr+7);
            if((occupancy[color] & (1L<<(sqr-1))) == 0) moves.add(sqr-1);
            if((occupancy[color] & (1L<<(sqr-9))) == 0) moves.add(sqr-9);
        }
        if(sqr % 8 < 7) {
            if((occupancy[color] & (1L<<(sqr+9))) == 0) moves.add(sqr+9);
            if((occupancy[color] & (1L<<(sqr+1))) == 0) moves.add(sqr+1);
            if((occupancy[color] & (1L<<(sqr-7))) == 0) moves.add(sqr-7);
        }
        if((occupancy[color] & (1L<<(sqr+8))) == 0) moves.add(sqr+8);
        if((occupancy[color] & (1L<<(sqr-8))) == 0) moves.add(sqr-8);
        return moves;
    }


    private void movePiece(int orig, int dest) {
        int piece = getPiece(orig);
        setPiece(dest, piece, getColor(orig));
        setPiece(orig, -1, -1);
    }
    private void undoMove(int orig, int dest, int enemyPiece) {
        int color = getColor(dest);
        setPiece(orig, getPiece(dest), color);
        setPiece(dest, enemyPiece, color^1);
    }
    private void setPiece(int sqr, int piece, int color) {
        if(piece == -1) {
            int oldPiece = getPiece(sqr);
            if(oldPiece != -1) pieces[getColor(sqr)][getPiece(sqr)] &= ~(0x1L<<sqr);
        } else {
            int oldPiece = getPiece(sqr);
            if(oldPiece != -1) pieces[getColor(sqr)][oldPiece] &= ~(0x1L<<sqr);
            pieces[color][piece] |= (0x1L<<sqr);
        }
        updateOccupancy();
    }

    private int getKingTile(int color) {
        return color == 0 ? whiteKingTile : blackKingTile;
    }

    private void updateOccupancy() {
        occupancy[0] = 0x0L;
        occupancy[1] = 0x0L;
        for(int i = 0; i < 6; i++) {
            occupancy[0] |= pieces[0][i];
            occupancy[1] |= pieces[1][i];
        }
        occupancy[2] = occupancy[0] | occupancy[1];
    }

}
