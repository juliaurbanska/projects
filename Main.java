//Julia Urbanska juur8340

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.util.*;

public class Main extends Application {
    private Pane center = new Pane();
    private Stage stage;
    private ImageView imageView = null; //ändrat
    private boolean changed = false;
    private RadioButton nameButton = new RadioButton("Name");
    private RadioButton describedButton = new RadioButton("Described");
    private TextField textField = new TextField("Search");
    private Map<String, Set<Place>> placeMap = new HashMap<>();
    private Map<Category, Set<Place>> categoryMap = new HashMap<>();
    private Map<Position, Place> positionMap = new HashMap<>();
    private HashSet<Place> hidePlace = new HashSet<>();
    private Button newButton = new Button("New");
    private Button searchButton = new Button("Search");
    private Button hideButton = new Button("Hide");
    private Button removeButton = new Button("Remove");
    private Button coordinatesButton = new Button("Coordinates");
    private ListView<String> list = new ListView<>(FXCollections.observableArrayList("Bus", "Underground", "Train"));
    private Button hideCategoryButton = new Button("Hide Category");



    @Override
    public void start(Stage stage) {
        this.stage = stage;
        BorderPane root = createPane();
        root.setCenter(center);
        //center.getChildren().add(imageView);
        root.setRight(createRightPane());
        newButton.setOnAction(new NewButtonHandler());
        hideButton.setOnAction(new HideHandler());
        removeButton.setOnAction(new RemoveHandler());
        hideCategoryButton.setOnAction(new HideCategory());
        coordinatesButton.setOnAction(new CoordinateHandler());
        searchButton.setOnAction(new SearchHandler());
        list.getSelectionModel ().selectedItemProperty ().addListener (e -> showCategory()); //detta tillhör showcategory metoden

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setOnCloseRequest(new ExitHandler());
        stage.show();

    }

    private void showCategory() {
        String selected = list.getSelectionModel().getSelectedItem();
        Category category = Category.valueOf(selected);
        if(categoryMap.containsKey(category)) {
            for(Place place : categoryMap.get(category)){
                place.setVisible(true); //platser visas inte igen när man trycker på nån av kategorierna
            }
        }
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        menuBar.getMenus().add(fileMenu);
        MenuItem loadMap = new MenuItem("Load Map");
        MenuItem loadPlaces = new MenuItem("Load places");
        MenuItem save = new MenuItem("Save");
        MenuItem exit = new MenuItem("Exit");
        fileMenu.getItems().add(loadMap);
        fileMenu.getItems().add(loadPlaces);
        fileMenu.getItems().add(save);
        fileMenu.getItems().add(exit);
        loadMap.setOnAction(new LoadMapHandler());
        loadPlaces.setOnAction(new LoadPlacesHandler());
        save.setOnAction(new SaveHandler());
        exit.setOnAction(new ExitItemHandler());
        return menuBar;
    }


    class ExitHandler implements EventHandler<WindowEvent>{
        @Override public void handle(WindowEvent event){
            if(changed){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Osparade ändringar, avsluta ändå?");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.CANCEL)
                    event.consume();
            }

        }
    }

    class ExitItemHandler implements EventHandler<ActionEvent>{
        @Override public void handle(ActionEvent event){
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        }
    }


    class LoadPlacesHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {

            if(imageView == null){
                new Alert(Alert.AlertType.ERROR,"Karta finns inte").showAndWait();
            }
            try{
                clear();
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Ange filnamn");
                fileChooser.setInitialDirectory(new File("."));
                File file = fileChooser.showOpenDialog(stage);
                if(file == null) {
                    return;
                }
                    String fileName = file.getAbsolutePath();

                    FileReader infile = new FileReader(fileName);
                    BufferedReader in = new BufferedReader(infile);
                    String line;
                    while((line = in.readLine()) != null){
                        String[] tokens = line.split(",");
                        String type = tokens[0];
                        String cat = tokens[1];
                        int x = Integer.parseInt(tokens[2]);
                        int y = Integer.parseInt(tokens[3]);
                        String name = tokens[4];
                        Position position = new Position(x, y);
                        Place p;
                        if(type.equals("Named")){
                            p = new NamedPlace(name, position, cat);
                        } else {
                            String description = tokens[5];
                           p = new DescribedPlace(name, position, cat, description);
                        }
                        addPlace(p);

                    }
            } catch (FileNotFoundException e) {
                new Alert(Alert.AlertType.ERROR, "Fel!").showAndWait();
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Fel!").showAndWait();
            }

        }
    }

    class SaveHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Ange filnamn");
                fileChooser.setInitialDirectory(new File("."));
                File file = fileChooser.showSaveDialog(stage);
                if(file == null){
                    return;
                }
                String fileName = file.getAbsolutePath();
                FileOutputStream outStream = new FileOutputStream(fileName);
                PrintWriter out = new PrintWriter(outStream);
                for (Place place : positionMap.values()) {
                    out.println(place.showList());
                }
                changed = false;
                out.close();
                outStream.close();
            } catch (FileNotFoundException e) { //om den inte hittar filen
                Alert alert = new Alert(Alert.AlertType.ERROR, "Kan inte öppna filen" + e.getMessage());
                alert.show();

            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "IO fel har inträffat!" + e.getMessage());
                alert.showAndWait();
            }

        }
    }


    class SearchHandler implements EventHandler<ActionEvent>{
        @Override public void handle(ActionEvent event){
            for (Place place : hidePlace){
                place.setUnmarked();
                //avmarkera alla platser som eventuellt är markerade? du kanske måste skapa en ny map
            }   hidePlace.clear();
            String selected = textField.getText(); //hämtar söksträngen
            if(!placeMap.containsKey(selected))
                return;
            placeMap.get(selected);
            for(Place place : placeMap.get(selected)){ //för varje plats som har det namnet
                place.setMarked();
                place.setVisible(true);
                hidePlace.add(place);
            }
        }
    }

    class CoordinateHandler implements EventHandler<ActionEvent>{
        @Override public void handle(ActionEvent event){
            try {
                CoordinateDialog dialog = new CoordinateDialog();
                Optional<ButtonType> answer = dialog.showAndWait();
                if (answer.isPresent() && answer.get() == ButtonType.OK){
                    double x =  dialog.getXCoordinate();
                    double y = dialog.getYCoordinate();
                    Position pos = new Position((int)x,(int)y);
                    Place p = positionMap.get(pos);
                    if(p == null){
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Ingen plats funnen!");
                        alert.showAndWait();
                        return;
                    } else{
                        for(Place place : hidePlace)
                            place.setUnmarked();
                        hidePlace.clear();
                        p.setMarked(); //markera
                        hidePlace.add(p);
                    }
                }
            } catch(NumberFormatException e){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Felaktigt inmatning!");
                alert.showAndWait();
                return;
            }
            

        }

    }

    class HideCategory implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            //en sträng, hämta strängen som är vald
            String selected = list.getSelectionModel().getSelectedItem(); //innehåller det item som är selected
            Category category = Category.valueOf(selected);
            Set<Place> mySet = categoryMap.get(category);
            if (mySet == null) { //skicka in kategorin till mappen, hämta setet
                return;
            }
            for (Place place : mySet) {
                place.setUnmarked();
                place.setVisible(false);
                hidePlace.remove(place);
            }
        }
    }

    class HideHandler implements EventHandler<ActionEvent>{
        @Override public void handle(ActionEvent event) {
            for (Place place : hidePlace){
                place.setUnmarked();
                place.setVisible(false);
            } hidePlace.clear();
        }
    }

    class RemoveHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            for (Place place : hidePlace) {
                positionMap.remove(place.getPosition());
                Set<Place> category = categoryMap.get(place.getCategory());
                category.remove(place);
                Set<Place> places = placeMap.get(place.getName());
                places.remove(place);
                center.getChildren().remove(place);

            }
            hidePlace.clear();
        }
    }

    class PlaceClickHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            Place place = (Place) event.getSource();
            if (event.getButton() == MouseButton.PRIMARY) {
                if (place.isMarked()) {
                    place.setUnmarked();
                    hidePlace.remove(place);

                } else {
                    place.setMarked();
                    hidePlace.add(place);
                }
            } else if (event.getButton() == MouseButton.SECONDARY) {
                {
                    place.showInfo();
                }

            }

        }

    }

    class ClickHandler implements EventHandler<MouseEvent> {
        private Place createNamePlaceDialog(Position position, String category) {
            NamedPlaceDialog dialog = new NamedPlaceDialog();

            Optional<ButtonType> answer = dialog.showAndWait();
            if (answer.isPresent() && answer.get() == ButtonType.OK) {
                String name = dialog.getName();
                if (name.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Felaktig inmatning");
                    alert.showAndWait();
                    return null;
                }
                return new NamedPlace(name, position, category);
            }
            return null;
        }

        private Place createDescribedPlace(Position position, String category) {
            DescribedPlaceDialog dialog = new DescribedPlaceDialog();
            Optional<ButtonType> answer = dialog.showAndWait();
            if (answer.isPresent() && answer.get() == ButtonType.OK) {
                String name = dialog.getName();
                String describeField = dialog.getDescribe();
                if (name.isEmpty() || describeField.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Felaktig inmatning");
                    alert.showAndWait();
                    return null;
                }
                return new DescribedPlace(name, position, category, describeField);
            }
            return null;

        }

        @Override
        public void handle(MouseEvent event) {
            double x = event.getX();
            double y = event.getY();
            Position position = new Position((int) x, (int) y);
            Place newPlace = null;
            String category = list.getSelectionModel().getSelectedItem();

            if(positionMap.containsKey(position)){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Platsen finns redan!");
                alert.showAndWait();
                return;
            }
            if (nameButton.isSelected()) {
                newPlace = createNamePlaceDialog(position, category);
            } else { newPlace = createDescribedPlace(position, category);

            } if(newPlace == null)
                return;
            addPlace(newPlace);
        }
    }

    private void addPlace(Place newPlace) {
        newPlace.setOnMouseClicked(new PlaceClickHandler());
        if (!placeMap.containsKey(newPlace.getName())) {
            placeMap.put(newPlace.getName(), new HashSet<>());
        }
        placeMap.get(newPlace.getName()).add(newPlace);
        if (!categoryMap.containsKey(newPlace.getCategory())) {
            categoryMap.put(newPlace.getCategory(), new HashSet<>());
        }
        categoryMap.get(newPlace.getCategory()).add(newPlace);
        positionMap.put(newPlace.getPosition(), newPlace);
        center.getChildren().add(newPlace);
        center.setOnMouseClicked(null);
        changed = true;
    }


    class LoadMapHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (clear()) return;

            FileChooser filechooser = new FileChooser();
                File file = filechooser.showOpenDialog(stage);
                if (file == null)
                    return;
                String fileName = file.getAbsolutePath();
                Image image = new Image("file:" + fileName);
                imageView = new ImageView();
                center.getChildren().add(imageView);
                imageView.setImage(image);
                stage.sizeToScene();
            }
        }

    private boolean clear() {
        if (changed) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Osparade ändringar, forsätta ändå?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                return true;
            }
        }
        if(!positionMap.values().isEmpty())
        center.getChildren().removeAll(positionMap.values());
        placeMap.clear();
        categoryMap.clear();
        positionMap.clear();
        hidePlace.clear();
        //center.getChildren().clear();
        return false;
    }


    private BorderPane createPane() {
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(imageView);
        HBox hBox = new HBox(newButton, new VBox(nameButton, describedButton), textField, searchButton, hideButton, removeButton, coordinatesButton);
        hBox.setPadding(new Insets(5));
        hBox.setSpacing(10);
        ToggleGroup toggleGroup = new ToggleGroup();
        nameButton.setToggleGroup(toggleGroup);
        describedButton.setToggleGroup(toggleGroup);
        hBox.setAlignment(Pos.CENTER);
        borderPane.setTop(new VBox(createMenuBar(), hBox));
        return borderPane;
    }

    private Node createRightPane() {
        Label label = new Label("Categories");
        VBox vBox = new VBox(label, list, hideCategoryButton);
        vBox.setAlignment(Pos.CENTER);
        list.setPrefSize(150, 100);
        return vBox;

    }

    class NewButtonHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            center.setCursor(Cursor.CROSSHAIR);
            center.setOnMouseClicked(new ClickHandler());

        }

    }
    

}
