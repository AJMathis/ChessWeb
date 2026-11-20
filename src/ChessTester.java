import java.util.Scanner;

public class ChessTester {
    static Scanner scnr = new Scanner(System.in);
    public static void main(String[] args) {
///*
        for(int i = 0; i <= 6; i++) {
            MoveGen game = new MoveGen();
            System.out.println("Depth " + i + ": " + perft(i, 0, game));
        }
/*
        MoveGen game = new MoveGen();
        System.out.println(game);
        game.move(8,16);
        System.out.println(game);
        game.move(55,47);
        System.out.println(game);
        game.move(9,25);
        System.out.println(game);
//*/

        //MoveGen game = new MoveGen();
        //System.out.println(perftDivide(1,0,game));
    }

    public static int perft(int depth, int color, MoveGen game) {
        long pieces = game.board.occupancy[color];
        int nodes = 0;

        if(depth == 0) return 1;

        while(pieces != 0L) {
            int from = Long.numberOfTrailingZeros(pieces);
            long moves = game.getMoves(from, false);

            while(moves != 0L) {
                int to = Long.numberOfTrailingZeros(moves);

               try {
                    game.move(from, to);
                    nodes += perft(depth - 1, color ^ 1, game);
                    game.undoMove(from, to);
               } catch (IllegalArgumentException | IllegalStateException _) {}
               //System.out.println(from + "->" + to + " ");
               moves &= moves - 1L;
            }
            pieces &= pieces-1L;
        }
        return nodes;
    }

    public static int perftDivide(int depth, int color, MoveGen game) {
        long pieces = game.board.occupancy[color];
        int nodes = 0;

        if(depth == 0) return 1;

        while(pieces != 0L) {
            int from = Long.numberOfTrailingZeros(pieces);
            long moves = game.getMoves(from, false);

            while(moves != 0L) {
                int to = Long.numberOfTrailingZeros(moves);
                int count = 0;
                try {
                    game.move(from, to);
                    count = perft(depth - 1, color ^ 1, game);
                    nodes+= count;
                    game.undoMove(from, to);
                } catch (IllegalArgumentException | IllegalStateException _) {}
                System.out.println(toChar(from) + "->" + toChar(to) + ". " + count);
                moves &= moves - 1L;
            }
            pieces &= pieces-1L;
        }
        return nodes;
    }

    public static String toChar(int sqr) {
        int file = sqr %8;
        int rank = sqr/8;
        return ("" + ((char)(file+97)) + (rank+1));
    }
}
