package g62496.dev3.oxono.console;

import g62496.dev3.oxono.model.*;
import g62496.dev3.oxono.util.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ControllerConsole implements Observer {
    private Board board ;
    Scanner scanner = new Scanner(System.in);
    Facade facade ;
    String endGame ="";
    Color player1 = Color.PINK;
    Color player2 = Color.BLACK;
    Color CurrentPlayer = player1;
    List<Position> positionList = new ArrayList<>();


    public static void main(String[] args) {
        Scanner scanner1 = new Scanner(System.in);
        System.out.println("voulez vous commencer la partie ?OUI/NON");
        String start = scanner1.next();
        if(start.toLowerCase().equals("oui")) {
            System.out.println("entrer le nombre de colonnes");
            int width = scanner1.nextInt();
            System.out.println("entrer le nombre de lignes");
            int height = scanner1.nextInt();
            ControllerConsole controllerConsole = new ControllerConsole();
            controllerConsole.Start(width,height);
        }
    }

    public ControllerConsole() {

    }

    public void Start(int width, int height){
        board= new Board(width,height);
        facade = new Facade(board);
        facade.registerObserver(this);
        setToken(board,width,height);
        DisplayBoard();
        Game(board,width,height);
    }

    public void setToken(Board board,int width,int height){
        Totem TotemX = new Totem(Symbol.X,new Position((width/2),(height/2)));
        board.initialiseTotem(TotemX);

        Totem TotemO = new Totem(Symbol.O,new Position((width/2)-1,(height/2)-1));
        board.initialiseTotem(TotemO);
    }
    public void endGame(){
        endGame="exit";
        System.out.println("fin de partie le joueur "+facade.getCurrenPlayer().getColor()+" a gagner ");
   }
    public void Game(Board board ,int width,int height ){
        while(!endGame.equals("exit")){
            System.out.println("Il reste "+ (16-facade.getPinkOPieces())+" pions rose O:");
            System.out.println("Il reste "+ (16-facade.getPinkXPieces())+" pions rose X:");
            System.out.println("Il reste "+(16 -facade.getBlackOPieces())+" pions noir O:");
            System.out.println("Il reste "+(16 -facade.getBlackXPieces())+" pions noir X :");
            System.out.println("Il reste "+board.getFreeBox()+" cases de libre sur le plateau de jeux ");
            System.out.println("Le joueur courant est le Joueur "+CurrentPlayer);

            System.out.println("Quel Totem souhaitez vous utilisé X/O");
            String totemSymbol = scanner.next().toUpperCase();

            Position totemPos = Symbol.valueOf(totemSymbol) == Symbol.X? facade.getTotems().getFirst().getPosition()
                    :facade.getTotems().getLast().getPosition();
            positionList.add(totemPos);
           // List<Position> possibleMoves = facade.checkTotemFirstClick(positionList);

//            System.out.println("Positions possibles pour déplacer le totem : ");
//            for (Position position : possibleMoves) {
//                System.out.println("x: " + position.getDx() + ", y: " + position.getDy());
//            }
            while (!handleTotemMove()){

            }
            while (!handlePieceInsert()){
                DisplayBoard();
            }

        }
    }
    private boolean handleTotemMove() {
       // Récupère les cases possibles pour le totem
        // Affiche les positions possibles
        System.out.println("joueur courant "+facade.getCurrenPlayer().getColor());
        System.out.print("Entrez les coordonnées du totem (x) : ");
        int x = scanner.nextInt();
        System.out.print("Entrez les coordonnées du totem (y) : ");
        int y = scanner.nextInt();
        Position movePosition = new Position(x, y);
        positionList.add(movePosition);
        // Vérifie si le mouvement est valide et effectue le déplacement
        facade.checkTotemFirstClick(positionList);
        facade.checkTotemSecondClick(positionList);
        if (!facade.isTotemTurn()) {
            return true;
            ///ca on le fait dans le oxono
            //facade.moveTotem(facade.getCurrentTotem(), movePosition); // Déplace le totem
        } else {
            System.out.println("Mouvement invalide pour le totem.");
            return false;
        }
    }
    public boolean handlePieceInsert(){
        System.out.println("joueur courant "+facade.getCurrenPlayer().getColor());
        System.out.print("sur quelle colonne voulez vous placez votre jeton : ");
        int x = scanner.nextInt();
        System.out.print("sur quelle ligne voulez vous placez votre jeton  ");
        int y = scanner.nextInt();
        System.out.println(positionList.getFirst().getDx());
        System.out.println(positionList.getFirst().getDy());
        Position movePosition = new Position(x, y);
        positionList.add(movePosition);
        facade.checkPieceClick(positionList);
        facade.checkPieceClick(positionList);
        if (facade.isTotemTurn()) {
            return true;
        } else {
            System.out.println("Mouvement invalide pour le jeton.");
            return false;
        }
    }

    public void DisplayBoard() {
        // todo: bouger ça dans la vue
        System.out.println("-------------------------------------------");
        for (int i = 0; i <board.getHeight() ; i++) {
            for (int j = 0; j <board.getWidth(); j++) {
                if (board.getTokenAt(i,j) != null) {
                    System.out.print(board.getTokenAt(i,j) + "|");
                } else {
                    System.out.print(".|");
                }
            }
            System.out.println();
        }
    }

    @Override
    public void update(List<Position> positionList, String message) {
        DisplayBoard();
        if (facade.isGameOver()){
            endGame();
        }
    }
    //    public void totemGame(Board board){
//        System.out.println("Quel Totem souhaitez vous utilisé X/O");
//        String totemSymbol = scanner.next().toUpperCase();
//        List<Position> positionList = new ArrayList<>();
//        System.out.println(board.getTotems().getFirst().getPosition().getDx());
//        System.out.println(board.getTotems().getFirst().getPosition().getDy());
//        Position totemPos = Symbol.valueOf(totemSymbol) == Symbol.X? board.getTotems().getFirst().getPosition()
//                :board.getTotems().getLast().getPosition();
//        positionList.add(totemPos);
//        facade.getPossibleTotemMoves(positionList);
//
//        System.out.println("Sur quelle ligne souhaitez vous placer votre Totem");
//        int totemDx = scanner.nextInt();
//        System.out.println("Sur quelle colonne souhaitez vous placer votre Totem");
//        int totemDy = scanner.nextInt();
//
//        positionList.add(new Position(totemDx,totemDy));
//        facade.getPossibleTotemMoves(positionList);
//
//        PieceGame(board,positionList,totemSymbol);
//    }
}
