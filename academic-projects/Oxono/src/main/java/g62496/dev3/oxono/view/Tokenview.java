package g62496.dev3.oxono.view;

import g62496.dev3.oxono.model.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
/**
 * The Tokenview class is responsible for creating and displaying tokens on the board,
 * whether they are pieces or totems.
 */
public class Tokenview extends StackPane {// extends un objet javafx et placé quelque part.
    public Tokenview(  ) {
    }
    /**
     * Adds a grid element based on the type of token.
     *
     * @param token The token to be added (either a piece or a totem).
     * @return A StackPane representing the grid element for the token.
     */
    public StackPane addGrid(Token token) {
        if (token instanceof Totem) {
            return createTotem((Totem) token);
        } else {
            return createPiece((Piece) token);
        }
    }
    /**
     * Creates a StackPane for a piece (circle or cross) based on its type and color.
     *
     * @param piece The piece to create a StackPane for.
     * @return A StackPane representing the piece.
     */
    public StackPane createPiece(Piece piece){
        boolean isBlack = piece.getColor() == g62496.dev3.oxono.model.Color.BLACK;
        boolean isCircle = piece.getType() == Symbol.O;

        Color outerColor = isBlack ? Color.BLACK : Color.PINK;
        Color innerColor = Color.BEIGE;

        StackPane cell;
        if (isCircle) {
            cell = createCircleStack(outerColor, innerColor);
        } else {
            cell = createCrossStack(outerColor, innerColor);
        }
        cell.setMouseTransparent(true);
        return cell;

    }

    private StackPane createTotem(Totem totem ) {
        String imagePath = totem.getType() == Symbol.X ? "image/Totemx.png" : "image/lettre-o.png";
        ImageView totemImage = new ImageView(imagePath);
        totemImage.setFitHeight(40);
        totemImage.setFitWidth(40);
        StackPane totemCell = new StackPane(totemImage);
        totemCell.setMouseTransparent(true);
        return totemCell;

    }

    private StackPane createCircleStack(Color outerColor, Color innerColor) {
        Circle outerCircle = new Circle();
        outerCircle.setFill(outerColor);

        Circle midCircle = new Circle();
        midCircle.setFill(innerColor);

        Circle innerCircle = new Circle();
        innerCircle.setFill(outerColor);

        StackPane circleStack = new StackPane(outerCircle, midCircle, innerCircle);
        bindCircleSizes(outerCircle, midCircle, innerCircle, circleStack);

        return circleStack;
    }

    private void bindCircleSizes(Circle outer, Circle mid, Circle inner, StackPane stack) {
        outer.radiusProperty().bind(stack.widthProperty().add(stack.heightProperty()).divide(8));
        mid.radiusProperty().bind(stack.widthProperty().add(stack.heightProperty()).divide(16));
        inner.radiusProperty().bind(stack.widthProperty().add(stack.heightProperty()).divide(32));
    }

    private StackPane createCrossStack(Color outerColor, Color lineColor) {
        Circle outerCircle = new Circle();
        outerCircle.setFill(outerColor);

        Line diagonal1 = new Line();
        diagonal1.setStroke(lineColor);
        diagonal1.setStrokeWidth(5);

        Line diagonal2 = new Line();
        diagonal2.setStroke(lineColor);
        diagonal2.setStrokeWidth(5);

        StackPane crossStack = new StackPane(outerCircle, diagonal1, diagonal2);
        bindDiagonalSizes(outerCircle, diagonal1, diagonal2, crossStack);

        return crossStack;
    }

    private void bindDiagonalSizes(Circle outer, Line diagonal1, Line diagonal2, StackPane stack) {
        outer.radiusProperty().bind(stack.widthProperty().add(stack.heightProperty()).divide(8));

        diagonal1.startXProperty().bind(stack.widthProperty().multiply(0.35)); // Proche du bord gauche
        diagonal1.startYProperty().bind(stack.heightProperty().multiply(0.35)); // Proche du haut
        diagonal1.endXProperty().bind(stack.widthProperty().multiply(0.65)); // Proche du bord droit
        diagonal1.endYProperty().bind(stack.heightProperty().multiply(0.65)); // Proche du bas

        diagonal2.startXProperty().bind(stack.widthProperty().multiply(0.65)); // Proche du bord droit
        diagonal2.startYProperty().bind(stack.heightProperty().multiply(0.35)); // Proche du haut
        diagonal2.endXProperty().bind(stack.widthProperty().multiply(0.35)); // Proche du bord gauche
        diagonal2.endYProperty().bind(stack.heightProperty().multiply(0.65)); // Proche du bas
    }

}
