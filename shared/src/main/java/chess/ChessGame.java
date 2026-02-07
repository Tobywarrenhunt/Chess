package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard board;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {

        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            return null;
        }

        Collection<ChessMove> pieceMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> legalMoves = new java.util.ArrayList<>();

        for (ChessMove move : pieceMoves) {
            ChessPiece originalStart = board.getPiece(move.getStartPosition());
            ChessPiece originalEnd = board.getPiece(move.getEndPosition());

            board.addPiece(move.getEndPosition(), originalStart);
            board.addPiece(move.getStartPosition(), null);

            if (!isInCheck(piece.getTeamColor())) {
                legalMoves.add(move);
            }

            board.addPiece(move.getStartPosition(), originalStart);
            board.addPiece(move.getEndPosition(), originalEnd);
        }

        return legalMoves;

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());

        if (piece == null || piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException();
        }

        Collection<ChessMove> legalMoves = validMoves(move.getStartPosition());
        if (legalMoves == null || !legalMoves.contains(move)) {
            throw new InvalidMoveException();
        }

        ChessPiece pieceToPlace = (move.getPromotionPiece() != null)
                ? new ChessPiece(teamTurn, move.getPromotionPiece())
                : piece;

        board.addPiece(move.getStartPosition(), null);
        board.addPiece(move.getEndPosition(), pieceToPlace);

        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;


    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
            ChessPosition kingPosition = null;

            for (int r = 1; r <= 8; r++) {
                for (int c = 1; c <= 8; c++) {
                    ChessPosition pos = new ChessPosition(r, c);
                    ChessPiece piece = board.getPiece(pos);
                    if (piece != null &&
                            piece.getPieceType() == ChessPiece.PieceType.KING &&
                            piece.getTeamColor() == teamColor) {
                        kingPosition = pos;
                        break;
                    }
                }
            }

            if (kingPosition == null) return false;

            TeamColor opponentColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

            for (int r = 1; r <= 8; r++) {
                for (int c = 1; c <= 8; c++) {
                    ChessPosition pos = new ChessPosition(r, c);
                    ChessPiece piece = board.getPiece(pos);

                    if (piece != null && piece.getTeamColor() == opponentColor) {
                        Collection<ChessMove> enemyMoves = piece.pieceMoves(board, pos);

                        for (ChessMove move : enemyMoves) {
                            if (move.getEndPosition().equals(kingPosition)) {
                                return true;
                            }
                        }
                    }
                }
            }

            return false;

    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(pos);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> possibleMoves = validMoves(pos);
                    if (possibleMoves != null && !possibleMoves.isEmpty()) {
                        return false;
                    }
                }

            }
        }

        return true;

    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(pos);

                if (piece != null && piece.getTeamColor() == teamColor) {

                    Collection<ChessMove> possibleMoves = validMoves(pos);
                    if (possibleMoves != null && !possibleMoves.isEmpty()) {
                        return false;
                    }

                }

            }
        }

        return true;

    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        // Two games are equal if it's the same team's turn and the boards are equal
        return teamTurn == chessGame.teamTurn &&
                java.util.Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        // Generate a hash based on the turn and the board
        return java.util.Objects.hash(teamTurn, board);
    }


}



