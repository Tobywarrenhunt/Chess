package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {

        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {

        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (getPieceType() == PieceType.PAWN) {
            return getPawnMoves(board, myPosition, this);
        }

        Rule rule = switch (getPieceType()) {
            case BISHOP -> new Rule(true, new int[][]{{1, 1}, {-1, 1}, {-1, -1}, {1, -1}});
            case ROOK -> new Rule(true, new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}});
            case KNIGHT ->
                    new Rule(false, new int[][]{{2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}, {2, -1}});
            case QUEEN ->
                    new Rule(true, new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, 1}, {-1, -1}, {1, -1}});
            case KING ->
                    new Rule(false, new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, 1}, {-1, -1}, {1, -1}});
            default -> null;
        };

        if (rule != null) {
            return rule.getMoves(board, myPosition, this);
        }
        return List.of();
    }


    private Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition pos, ChessPiece pawn) {
        Set<ChessMove> moves = new HashSet<>();

        int direction = (pawn.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (pawn.getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promoteRow = (pawn.getTeamColor() == ChessGame.TeamColor.WHITE) ? 8 : 1;

        int row = pos.getRow();
        int col = pos.getColumn();

        // Forward 1
        ChessPosition oneForward = new ChessPosition(row + direction, col);
        if (board.getPiece(oneForward) == null) {
            addPawnMove(moves, pos, oneForward, promoteRow);

            // Forward 2 (only from start row)
            if (row == startRow) {
                ChessPosition twoForward = new ChessPosition(row + 2 * direction, col);
                if (board.getPiece(twoForward) == null) {
                    moves.add(new ChessMove(pos, twoForward, null));
                }
            }
        }

        // Capture diagonally: left and right
        for (int dx : new int[]{-1, 1}) {
            int newCol = col + dx;
            if (newCol < 1 || newCol > 8) continue;

            ChessPosition diagPos = new ChessPosition(row + direction, newCol);
            ChessPiece target = board.getPiece(diagPos);
            if (target != null && target.getTeamColor() != pawn.getTeamColor()) {
                addPawnMove(moves, pos, diagPos, promoteRow);
            }
        }

        return moves;
    }

    private void addPawnMove(Set<ChessMove> moves, ChessPosition from, ChessPosition to, int promoteRow) {
        if (to.getRow() == promoteRow) {
            for (ChessPiece.PieceType promo : List.of(
                    ChessPiece.PieceType.QUEEN,
                    ChessPiece.PieceType.ROOK,
                    ChessPiece.PieceType.BISHOP,
                    ChessPiece.PieceType.KNIGHT
            )) {
                moves.add(new ChessMove(from, to, promo));
            }
        } else {
            moves.add(new ChessMove(from, to, null));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece piece = (ChessPiece) o;
        return pieceColor == piece.pieceColor && type == piece.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}


