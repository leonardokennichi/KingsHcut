package main;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.ReservationHeader;
import model.Service;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import util.ConnectSQL;

public class Main extends Application {
	
	private ConnectSQL connect = ConnectSQL.getInstance();
	
	private Scene loginScene, registrationScene, reserveServiceScene, serviceManagementScene, reservationManagementScene;
	private TextField timeField;
    private DatePicker datePicker;
    private ListView<String> reserveList;
    private List<String> reservedServices;
    private TableView<Service> serviceTable;
    private ArrayList<ReservationHeader> e = new ArrayList<ReservationHeader>();
    
    private String username = "";
    private String userID= "";
    LocalTime reservationTime;
    private String time, date, reservationString;
    private Service service;
    LocalDate dateReal;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("KingsHcut");
        
        loginScene = createLoginScene(primaryStage);
        
        registrationScene = createRegistrationScene(primaryStage);
        
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }
    
    private Scene createLoginScene(Stage primaryStage) {
   	Label loginTitle = new Label("Login");
   	loginTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
   	Label emailLabel = new Label("Email");
    Label passwordLabel = new Label("Password");
      TextField emailField = new TextField();
      PasswordField passwordField = new PasswordField();
      Button loginButton = new Button("Login");
      Hyperlink registerLink = new Hyperlink("Don't have an account yet? Register Here!");

      // Layout
      GridPane gridPane = new GridPane();
      gridPane.setAlignment(Pos.CENTER);
      gridPane.setHgap(10);
      gridPane.setVgap(5);

      gridPane.add(loginTitle, 0, 0, 2, 1);
      GridPane.setHalignment(loginTitle, HPos.CENTER);
      gridPane.add(emailLabel, 0, 1);
      gridPane.add(emailField, 0, 2);
      gridPane.add(passwordLabel, 0, 3);
      gridPane.add(passwordField, 0, 4);
      gridPane.add(loginButton, 0, 5);
      gridPane.add(registerLink, 0, 6);

      // Event Handling
      loginButton.setOnAction(event -> {
          String email = emailField.getText().trim();
          String password = passwordField.getText().trim();

          if (email.isEmpty() || password.isEmpty()) {
              showAlert(Alert.AlertType.ERROR, "Login Error","Error", "Email cannot be empty.");
          } else {
              validateLogin(email, password, primaryStage, emailField, passwordField);
          }
      });

      registerLink.setOnAction(e -> primaryStage.setScene(registrationScene));
    	
    	
    	return new Scene(gridPane, 400,300);
    }
    
    private Scene createRegistrationScene(Stage primaryStage) {
    	 Label registerTitle = new Label("Register");
    	 registerTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
         Label usernameLabel = new Label("Username");
         usernameLabel.setMinWidth(350);
         Label emailLabel = new Label("Email");
         Label passwordLabel = new Label("Password");
         Label confirmPasswordLabel = new Label("Confirm Password");
         Label phoneNumberLabel = new Label("Phone Number");
         Label genderLabel = new Label("Gender");

         TextField usernameField = new TextField();
         usernameField.setPromptText("Input your username here");

         TextField emailField = new TextField();
         emailField.setPromptText("Input your email here");

         PasswordField passwordField = new PasswordField();
         passwordField.setPromptText("Input your password here");

         PasswordField confirmPasswordField = new PasswordField();
         confirmPasswordField.setPromptText("Confirm your password here");

         TextField phoneNumberField = new TextField();
         phoneNumberField.setPromptText("Input your phone number here");

         ToggleGroup genderGroup = new ToggleGroup();
         RadioButton maleRadio = new RadioButton("Male");
         maleRadio.setToggleGroup(genderGroup);
         RadioButton femaleRadio = new RadioButton("Female");
         femaleRadio.setToggleGroup(genderGroup);

         Button registerButton = new Button("Register");
         Hyperlink loginLink = new Hyperlink("Already have an account? Click here to login!");

         // Layout
         HBox radioPane = new HBox();
         radioPane.setSpacing(5);
         radioPane.getChildren().addAll(maleRadio, femaleRadio);
         GridPane gridPane = new GridPane();
         gridPane.setAlignment(Pos.CENTER);
         gridPane.setHgap(10);
         gridPane.setVgap(10);

         gridPane.add(registerTitle, 0, 0, 2, 1);
         GridPane.setHalignment(registerTitle, HPos.CENTER);
         gridPane.add(usernameLabel, 0, 1);
         gridPane.add(usernameField, 0, 2);
         gridPane.add(emailLabel, 0, 3);
         gridPane.add(emailField, 0, 4);
         gridPane.add(passwordLabel, 0, 5);
         gridPane.add(passwordField, 0, 6);
         gridPane.add(confirmPasswordLabel, 0, 7);
         gridPane.add(confirmPasswordField, 0, 8);
         gridPane.add(phoneNumberLabel, 0, 9);
         gridPane.add(phoneNumberField, 0, 10);
         gridPane.add(radioPane, 0, 11);
         gridPane.add(registerButton, 0, 12, 2, 1);
         GridPane.setHalignment(registerButton, HPos.CENTER);
         gridPane.add(loginLink, 0, 13, 2, 1);
         GridPane.setHalignment(loginLink, HPos.CENTER);

         // Event Handling
         registerButton.setOnAction(event -> {
             String username = usernameField.getText().trim();
             String email = emailField.getText().trim();
             String password = passwordField.getText().trim();
             String confirmPassword = confirmPasswordField.getText().trim();
             String phoneNumber = phoneNumberField.getText().trim();
             String phoneNumberReal = phoneNumber.replace("62", "0");
             String gender = (maleRadio.isSelected() ? "Male" : (femaleRadio.isSelected() ? "Female" : ""));

             String validationError = validateInputs(username, email, password, confirmPassword, phoneNumber, gender);
             if (validationError != null) {
                 showAlert(Alert.AlertType.ERROR, "Register Error", "Error", validationError);
                 return;
             }
             
             String userID = generateUserID();

             // Insert into database
             String role = username.toLowerCase().contains("admin") ? "Admin" : "User";
             String insertQuery = String.format(
                 "INSERT INTO MsUser (UserID, UserName, UserEmail, UserPassword, UserPhoneNumber, UserGender, UserRole) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                 userID, username, email, password, phoneNumberReal, gender, role
             );

             try {
                 ConnectSQL.connect.execUpdate(insertQuery);
                 showAlert(Alert.AlertType.INFORMATION,"Registration Success", "Success", "Account successfully created!");
                 // Redirect to Login Page
                 primaryStage.setScene(loginScene);
             } catch (Exception e) {
                 showAlert(Alert.AlertType.ERROR,null, "Database Error", "An error occurred while registering. Please try again.");
                 e.printStackTrace();
             }
         });

         loginLink.setOnAction(event -> {
             try {
            	 primaryStage.setScene(loginScene);
             } catch (Exception e) {
                 e.printStackTrace();
             }
         });
    	return new Scene(gridPane, 700, 500);
    }
    
    public Scene createReserveServiceScene(Stage stage, String email) {
        // Components (Your existing code)
        String query = String.format("SELECT Username FROM msuser WHERE UserEmail = '%s'", email);
        connect.rs = connect.execQuery(query);
        try {
            while (connect.rs.next()) {
                username = connect.rs.getString("Username");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Label titleLabel = new Label("Reserve Service");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label userLabel = new Label("User: " + username);
        Label reserveLbl = new Label("Reserve List");
        reserveLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        CheckBox cbHairCut = new CheckBox("Hair Cut");
        CheckBox cbHairTreatment = new CheckBox("Hair Treatment");
        CheckBox cbHairPerming = new CheckBox("Hair Perming");
        CheckBox cbHairColoring = new CheckBox("Hair Coloring");
        CheckBox cbHairTattoo = new CheckBox("Hair Tattoo");

        TableView<Service> serviceTable = new TableView<>();
        ListView<String> reserveListView = new ListView<>();

        DatePicker datePicker = new DatePicker();
        TextField timeField = new TextField();
        timeField.setPromptText("hh:mm");

        Button addButton = new Button("Add");
        Button cancelButton = new Button("Cancel");
        Button reserveButton = new Button("Reserve");

        ArrayList<Service> serviceList = fetchServicesFromDatabase();

        // Table Columns
        TableColumn<Service, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().getServiceId());
        TableColumn<Service, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().getServiceName());
        TableColumn<Service, Integer> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().getServicePrice().asObject());
        TableColumn<Service, Integer> durationColumn = new TableColumn<>("Duration");
        durationColumn.setCellValueFactory(cellData -> cellData.getValue().getServiceDuration().asObject());

        ObservableList<Service> serviceData = FXCollections.observableArrayList(serviceList);

        serviceTable.getColumns().addAll(idColumn, nameColumn, priceColumn, durationColumn);
        serviceTable.setItems(serviceData);

        // Checkbox listeners
        cbHairCut.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filterTable(serviceData, serviceList, cbHairCut, cbHairTreatment, cbHairPerming, cbHairColoring, cbHairTattoo);
        });
        cbHairTreatment.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filterTable(serviceData, serviceList, cbHairCut, cbHairTreatment, cbHairPerming, cbHairColoring, cbHairTattoo);
        });
        cbHairPerming.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filterTable(serviceData, serviceList, cbHairCut, cbHairTreatment, cbHairPerming, cbHairColoring, cbHairTattoo);
        });
        cbHairColoring.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filterTable(serviceData, serviceList, cbHairCut, cbHairTreatment, cbHairPerming, cbHairColoring, cbHairTattoo);
        });
        cbHairTattoo.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filterTable(serviceData, serviceList, cbHairCut, cbHairTreatment, cbHairPerming, cbHairColoring, cbHairTattoo);
        });

        // Menu Bar
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Menu");
        MenuItem reserveServiceItem = new MenuItem("Reserve Service");
        MenuItem customerReservationItem = new MenuItem("Customer Reservation");
        MenuItem logOutItem = new MenuItem("Log Out");

        menu.getItems().addAll(reserveServiceItem, customerReservationItem, logOutItem);
        menuBar.getMenus().add(menu);

        // Menu Item Actions
        reserveServiceItem.setOnAction(e -> stage.setScene(createReserveServiceScene(stage, email)));
        customerReservationItem.setOnAction(e -> stage.setScene(createCustomerReservationScene(stage, email)));
         logOutItem.setOnAction(e -> stage.setScene(createLoginScene(stage)));

        // Layout
        HBox filterBox = new HBox(5, cbHairCut, cbHairTreatment, cbHairPerming, cbHairColoring, cbHairTattoo);
        filterBox.setPadding(new Insets(10));

        VBox reservationBox = new VBox(10, new Label("Reservation Date"), datePicker, new Label("Reservation Time"), timeField);
        VBox buttonBox = new VBox(10, addButton, cancelButton, reserveButton);

        VBox root2 = new VBox(reserveLbl, reserveListView, reservationBox, buttonBox);
        HBox mainBox = new HBox(10, serviceTable, root2);

        // Content below the menu bar
        VBox content = new VBox(10, titleLabel, userLabel, filterBox, mainBox);
        content.setPadding(new Insets(10));

        // Use BorderPane as the root layout
        BorderPane root = new BorderPane();
        root.setTop(menuBar);  // Place the MenuBar at the top
        root.setCenter(content); // Place the rest of your content in the center

        // Event Handlers
        addButton.setOnAction(e -> handleAddButton(datePicker, timeField, reserveListView, serviceTable));
        cancelButton.setOnAction(e -> handleCancelButton(reserveListView, datePicker, timeField));
        reserveButton.setOnAction(e -> handleReserveButton(reserveListView, email));

        return new Scene(root, 1000, 800);
    }
    
    private Scene createCustomerReservationScene(Stage stage, String email) {
    	String userQuery = String.format("SELECT UserId, Username FROM msuser WHERE UserEmail = '%s'", email);
        String finalUsername;
        try {
            ResultSet userResult = connect.execQuery(userQuery);
            if (userResult.next()) {
                finalUsername = userResult.getString("Username");
            } else {
                finalUsername = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            finalUsername = "";
        }

        // Components
        Label titleLabel = new Label(finalUsername + "'s Reservation");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label historyLabel = new Label("Reservation History");
        historyLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Table View
        TableView<ReservationHeader> reservationTable = new TableView<>();

        TableColumn<ReservationHeader, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReservationId()));

        TableColumn<ReservationHeader, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

        TableColumn<ReservationHeader, String> startTimeColumn = new TableColumn<>("Start Time");
        startTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReservationTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));

        TableColumn<ReservationHeader, String> endTimeColumn = new TableColumn<>("End Time");
        endTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));

        TableColumn<ReservationHeader, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReservationStatus()));

        reservationTable.getColumns().addAll(idColumn, dateColumn, startTimeColumn, endTimeColumn, statusColumn);

        // Labels for selected reservation details
        Label idValueLabel = new Label();
        Label dateValueLabel = new Label();
        Label startTimeValueLabel = new Label();
        Label endTimeValueLabel = new Label();
        Label statusValueLabel = new Label();

        // Cancel Button
        Button cancelButton = new Button("Cancel");

        // Menu Bar
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Menu");
        MenuItem reserveServiceItem = new MenuItem("Reserve Service");
        MenuItem customerReservationItem = new MenuItem("Customer Reservation");
        MenuItem logOutItem = new MenuItem("Log Out");

        menu.getItems().addAll(reserveServiceItem, customerReservationItem, logOutItem);
        menuBar.getMenus().add(menu);

        // Menu Item Actions
        reserveServiceItem.setOnAction(e -> stage.setScene(createReserveServiceScene(stage, email)));
        customerReservationItem.setOnAction(e -> stage.setScene(createCustomerReservationScene(stage, email)));
        logOutItem.setOnAction(e -> {
            stage.setScene(loginScene);
        });

        // Layout
        VBox rightPane = new VBox(10,
                new HBox(5, new Label("ID:"), idValueLabel),
                new HBox(5, new Label("Date:"), dateValueLabel),
                new HBox(5, new Label("Start Time:"), startTimeValueLabel),
                new HBox(5, new Label("End Time:"), endTimeValueLabel),
                new HBox(5, new Label("Status:"), statusValueLabel),
                cancelButton
        );
        rightPane.setPadding(new Insets(10));

        VBox contentBox = new VBox();
        HBox reservationDetail = new HBox();

        contentBox.setPadding(new Insets(20));
        contentBox.setSpacing(10);

        reservationDetail.getChildren().addAll(reservationTable, rightPane);

        contentBox.getChildren().addAll(titleLabel, historyLabel, reservationDetail);

        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(contentBox);

        // Event Handlers
        reservationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                idValueLabel.setText(newSelection.getReservationId());
                dateValueLabel.setText(newSelection.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                startTimeValueLabel.setText(newSelection.getReservationTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                endTimeValueLabel.setText(newSelection.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                statusValueLabel.setText(newSelection.getReservationStatus());
            } else {
                // Clear the labels if nothing is selected
                idValueLabel.setText("");
                dateValueLabel.setText("");
                startTimeValueLabel.setText("");
                endTimeValueLabel.setText("");
                statusValueLabel.setText("");
            }
        });

        cancelButton.setOnAction(e -> {
            ReservationHeader selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
            if (selectedReservation != null && selectedReservation.getReservationStatus().equals("In progress")) {
                // Now you can directly use finalUserId inside the lambda:

                // Update the status to "Cancelled" in the database
                String updateQuery = String.format("UPDATE ReservationHeader SET ReservationStatus = 'Cancelled' WHERE ReservationID = '%s'", selectedReservation.getReservationId());
                connect.execUpdate(updateQuery);

                // Refresh the table view using finalUserId
                ObservableList<ReservationHeader> updatedReservations = fetchUserReservations(getUserID(email));
                reservationTable.setItems(updatedReservations);

                showAlert(Alert.AlertType.INFORMATION, "Cancellation Success", "Message", "Your reservation is successfully cancelled");
            } else if(selectedReservation == null) {
                showAlert(Alert.AlertType.ERROR, "Error Cancellation", "Error", "No reservation selected");
            } else if (selectedReservation.getReservationStatus().equals("Cancelled")) {
            	showAlert(Alert.AlertType.ERROR, "Error Cancellation", "Error", "Reservation is already cancelled");
			} else if (selectedReservation.getReservationStatus().equals("Finished")) {
            	showAlert(Alert.AlertType.ERROR, "Error Cancellation", "Error", "Cannot cancel finished reservation");
			}
        });

        // Load initial data into the table
        ObservableList<ReservationHeader> reservations = fetchUserReservations(getUserID(email));
        reservationTable.setItems(reservations);

        return new Scene(root, 1000, 600);
    }
    
    private Scene createServiceManagementScene (Stage primaryStage) {
    	
    	Label titleLabel = new Label("Service Management");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label serviceListLabel = new Label("Service List");
        serviceListLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        ObservableList<Service> serviceData = fetchAllServices();
        
        TableView<Service> serviceTable = new TableView<>();

        TableColumn<Service, String> serviceIDColumn = new TableColumn<>("Service ID");
        serviceIDColumn.setCellValueFactory(cellData -> cellData.getValue().getServiceId());
        serviceIDColumn.setMinWidth(75);

        TableColumn<Service, String> serviceTypeIDColumn = new TableColumn<>("Service Type ID");
        serviceTypeIDColumn.setCellValueFactory(cellData -> cellData.getValue().getServiceTypeId());
        serviceTypeIDColumn.setMinWidth(75);
        
        TableColumn<Service, String> serviceNameColumn = new TableColumn<>("Service Name");
        serviceNameColumn.setCellValueFactory(cellData -> cellData.getValue().getServiceName());
        serviceNameColumn.setMinWidth(150);
        
        TableColumn<Service, Integer> servicePriceColumn = new TableColumn<>("Service Price");
        servicePriceColumn.setCellValueFactory(cellData -> cellData.getValue().getServicePrice().asObject());
        servicePriceColumn.setMinWidth(100);
        
        TableColumn<Service, Integer> serviceDurationColumn = new TableColumn<>("Service Duration");
        serviceDurationColumn.setCellValueFactory(cellData -> cellData.getValue().getServiceDuration().asObject());
        serviceDurationColumn.setMinWidth(75);
        
        serviceTable.getColumns().addAll(serviceIDColumn, serviceTypeIDColumn, serviceNameColumn, servicePriceColumn, serviceDurationColumn);
        serviceTable.setItems(serviceData);
        
        Label serviceNameLbl = new Label("Service Name");
        Label serviceTypeLbl = new Label("Service Type");
        Label servicePriceLbl = new Label("Service Price");
        Label serviceDurationLbl = new Label("Service Duration");
        
        TextField serviceNameTf, servicePriceTf, serviceDurationTf;
        
        ComboBox<String> serviceTypeCombo = new ComboBox<>();
        serviceTypeCombo.getItems().addAll(
                "Haircut",
                "Hair Perming",
                "Hair Coloring",
                "Hair Treatment",
                "Hair Tattoo"
        );
        serviceTypeCombo.setValue("Haircut");
        
        serviceNameTf = new TextField();
        serviceNameTf.setPromptText("Input service name here");
        
        servicePriceTf = new TextField();
        servicePriceTf.setPromptText("Input service price here");
        
        serviceDurationTf = new TextField();
        serviceDurationTf.setPromptText("Input service duration here");
        
        serviceTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                serviceNameTf.setText(newSelection.getServiceName().get());
                serviceTypeCombo.setValue(getServiceTypeName(newSelection.getServiceTypeId().get()));
                servicePriceTf.setText(String.valueOf(newSelection.getServicePrice().get()));
                serviceDurationTf.setText(String.valueOf(newSelection.getServiceDuration().get()));
            }
        });
        
        Button addBtn = new Button("Add");
        Button updateBtn = new Button("Update");
        Button deleteBtn = new Button("Delete");
        
        VBox contentPane = new VBox();
        contentPane.setSpacing(15);
        
        VBox inputPane = new VBox();
        inputPane.setSpacing(5);
        
        inputPane.getChildren().addAll(serviceNameLbl, serviceNameTf, serviceTypeLbl, serviceTypeCombo, servicePriceLbl, servicePriceTf,
        		serviceDurationLbl, serviceDurationTf, addBtn, updateBtn, deleteBtn);
        
        HBox serviceDetail = new HBox();
        serviceDetail.setSpacing(7);
        
        serviceDetail.getChildren().addAll(serviceTable, inputPane);
        
        contentPane.getChildren().addAll(titleLabel, serviceListLabel, serviceDetail);
        contentPane.setPadding(new Insets(12));
        
        addBtn.setOnAction(e -> {
            String serviceName = serviceNameTf.getText();
            String serviceType = serviceTypeCombo.getValue();
            String servicePriceStr = servicePriceTf.getText();
            String serviceDurationStr = serviceDurationTf.getText();

            // Validation
            if (serviceName.isEmpty() || serviceType == null || servicePriceStr.isEmpty() || serviceDurationStr.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error", "All fields must be filled.");
                return;
            }

            if (!isServiceNameUnique(serviceName, null)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error", "Service already exist");
                return;
            }

            int servicePrice, serviceDuration;
            try {
                servicePrice = Integer.parseInt(servicePriceStr);
                serviceDuration = Integer.parseInt(serviceDurationStr);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error", "Service price and duration must be numbers.");
                return;
            }

            // Add service to database
            String serviceTypeId = getServiceTypeId(serviceType);
            if (serviceTypeId == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error","Invalid service type.");
                return;
            }
            
            String serviceId = generateServiceID();
            String query = String.format("INSERT INTO msservice (ServiceID, ServiceTypeID, ServiceName, ServicePrice, ServiceDuration) VALUES ('%s', '%s', '%s', %d, %d)",
                    serviceId, serviceTypeId, serviceName, servicePrice, serviceDuration);
            
            showAlert(Alert.AlertType.INFORMATION, "Success", "Message", "Service added successfully!");
            serviceTable.getItems().add(new Service(new SimpleStringProperty(serviceId), new SimpleStringProperty(serviceTypeId), new SimpleStringProperty(serviceName), new SimpleIntegerProperty(servicePrice), new SimpleIntegerProperty(serviceDuration)));
            serviceTable.refresh();

            serviceNameTf.clear();
            servicePriceTf.clear();
            serviceDurationTf.clear();
            serviceTypeCombo.setValue("Haircut");

        });
        
        updateBtn.setOnAction(e -> {
            Service selectedService = serviceTable.getSelectionModel().getSelectedItem();
            if (selectedService == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error", "There is no service selected");
                return;
            }

            String serviceId = selectedService.getServiceId().get();
            String serviceName = serviceNameTf.getText();
            String serviceType = serviceTypeCombo.getValue();
            String servicePriceStr = servicePriceTf.getText();
            String serviceDurationStr = serviceDurationTf.getText();

            // Validation
            if (serviceName.isEmpty() || serviceType == null || servicePriceStr.isEmpty() || serviceDurationStr.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error", "All fields must be filled.");
                return;
            }

            if (!isServiceNameUnique(serviceName, serviceId)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error", "Service name must be unique.");
                return;
            }

            int servicePrice, serviceDuration;
            try {
                servicePrice = Integer.parseInt(servicePriceStr);
                serviceDuration = Integer.parseInt(serviceDurationStr);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error", "Service price and duration must be numbers.");
                return;
            }

            String serviceTypeId = getServiceTypeId(serviceType);
            if (serviceTypeId == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error", "Invalid service type.");
                return;
            }

            // Update service in database
            String query = String.format("UPDATE msservice SET ServiceTypeID = '%s', ServiceName = '%s', ServicePrice = %d, ServiceDuration = %d WHERE ServiceID = '%s'",
                    serviceTypeId, serviceName, servicePrice, serviceDuration, serviceId);

            try {
                connect.execUpdate(query);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Message", "Service is successfully updated");

                // Update the item in the TableView
                selectedService.setServiceTypeId(new SimpleStringProperty(serviceTypeId));
                selectedService.setServiceName(new SimpleStringProperty(serviceName));
                selectedService.setServicePrice(new SimpleIntegerProperty(servicePrice));
                selectedService.setServiceDuration(new SimpleIntegerProperty(serviceDuration));
                serviceTable.refresh();

                serviceNameTf.clear();
                servicePriceTf.clear();
                serviceDurationTf.clear();
                serviceTypeCombo.setValue("Haircut");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error", "Failed to update service: " + ex.getMessage());
            }
        });

        deleteBtn.setOnAction(e -> {
            Service selectedService = serviceTable.getSelectionModel().getSelectedItem();
            if (selectedService == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error", "Please select a service to delete.");
                return;
            }

            String serviceId = selectedService.getServiceId().get();
            String query = String.format("DELETE FROM msservice WHERE ServiceID = '%s'", serviceId);

            try {
                connect.execUpdate(query);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Message", "Service is successfully deleted");

                // Remove the item from the TableView
                serviceTable.getItems().remove(selectedService);
                serviceTable.refresh();

                serviceNameTf.clear();
                servicePriceTf.clear();
                serviceDurationTf.clear();
                serviceTypeCombo.setValue("Haircut");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error","Failed to delete service: " + ex.getMessage());
            }
        });
    
    	MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Menu");
        MenuItem serviceManagementItem = new MenuItem("Service Management");
        MenuItem reservationManagementItem = new MenuItem("Reservation Management");
        MenuItem logOutItem = new MenuItem("Log Out");
        
        menu.getItems().addAll(serviceManagementItem, reservationManagementItem, logOutItem);
        menuBar.getMenus().add(menu);
        
        reservationManagementItem.setOnAction(e -> {
        	reservationManagementScene = createReservationManagementScene(primaryStage);
        	primaryStage.setScene(reservationManagementScene);
        });
        
        logOutItem.setOnAction(e -> {
            primaryStage.setScene(loginScene);
        });
    	
    	BorderPane root = new BorderPane();
    	root.setTop(menuBar);
    	root.setCenter(contentPane);
    	
    	return new Scene(root, 1000, 600);
    	}
    
    private Scene createReservationManagementScene(Stage primaryStage) {
    	Label titleLabel = new Label("Reservation Management");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label reservationListLabel = new Label("Reservation List");
        reservationListLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Table View
        TableView<ReservationHeader> reservationTable = new TableView<>();

        TableColumn<ReservationHeader, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReservationId()));

        TableColumn<ReservationHeader, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

        TableColumn<ReservationHeader, String> startTimeColumn = new TableColumn<>("Start Time");
        startTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReservationTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));

        TableColumn<ReservationHeader, String> endTimeColumn = new TableColumn<>("End Time");
        endTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));

        TableColumn<ReservationHeader, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReservationStatus()));

        reservationTable.getColumns().addAll(idColumn, dateColumn, startTimeColumn, endTimeColumn, statusColumn);

        // Labels for selected reservation details
        Label idValueLabel = new Label();
        Label dateValueLabel = new Label();
        Label startTimeValueLabel = new Label();
        Label endTimeValueLabel = new Label();
        Label statusValueLabel = new Label();

        // Cancel and Complete Buttons
        Button cancelButton = new Button("Cancel");
        Button completeButton = new Button("Complete");

        // Menu Bar
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Menu");
        MenuItem serviceManagementItem = new MenuItem("Service Management");
        MenuItem reservationManagementItem = new MenuItem("Reservation Management");
        MenuItem logOutItem = new MenuItem("Log Out");

        menu.getItems().addAll(serviceManagementItem, reservationManagementItem, logOutItem);
        menuBar.getMenus().add(menu);

        // Menu Item Actions
        serviceManagementItem.setOnAction(e -> primaryStage.setScene(createServiceManagementScene(primaryStage)));
        reservationManagementItem.setOnAction(e -> primaryStage.setScene(createReservationManagementScene(primaryStage)));
        logOutItem.setOnAction(e -> primaryStage.setScene(loginScene));

        // Layout
        // Use HBox for each label pair to keep them side-by-side
        HBox idPane = new HBox(5, new Label("ID:"), idValueLabel);
        HBox datePane = new HBox(5, new Label("Date:"), dateValueLabel);
        HBox startTimePane = new HBox(5, new Label("Start Time:"), startTimeValueLabel);
        HBox endTimePane = new HBox(5, new Label("End Time:"), endTimeValueLabel);
        HBox statusPane = new HBox(5, new Label("Status:"), statusValueLabel);

        VBox rightPane = new VBox(10, idPane, datePane, startTimePane, endTimePane, statusPane, cancelButton, completeButton);
        rightPane.setPadding(new Insets(10));

        // Make all text in rightPane bold and font size 20
        for (javafx.scene.Node node : rightPane.getChildren()) {
            if (node instanceof Label) {
                ((Label) node).setStyle("-fx-font-weight: bold; -fx-font-size: 20;");
            }
            if (node instanceof HBox) {
            	for (javafx.scene.Node innerNode : ((HBox) node).getChildren()) {
                    if (innerNode instanceof Label) {
                        ((Label) innerNode).setStyle("-fx-font-weight: bold; -fx-font-size: 20;");
                    }
            	}
            }
        }

        VBox contentBox = new VBox();
        HBox reservationDetail = new HBox();

        contentBox.setPadding(new Insets(20));
        contentBox.setSpacing(10);

        reservationDetail.getChildren().addAll(reservationTable, rightPane);

        contentBox.getChildren().addAll(titleLabel, reservationListLabel, reservationDetail);

        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(contentBox);

        // Event Handlers
        reservationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                idValueLabel.setText(newSelection.getReservationId());
                dateValueLabel.setText(newSelection.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                startTimeValueLabel.setText(newSelection.getReservationTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                endTimeValueLabel.setText(newSelection.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                statusValueLabel.setText(newSelection.getReservationStatus());
            } else {
                idValueLabel.setText("");
                dateValueLabel.setText("");
                startTimeValueLabel.setText("");
                endTimeValueLabel.setText("");
                statusValueLabel.setText("");
            }
        });

        cancelButton.setOnAction(e -> {
            ReservationHeader selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
            if (selectedReservation != null && selectedReservation.getReservationStatus().equals("In progress")) {
                String updateQuery = String.format("UPDATE ReservationHeader SET ReservationStatus = 'Cancelled' WHERE ReservationID = '%s'", selectedReservation.getReservationId());
                connect.execUpdate(updateQuery);

                ObservableList<ReservationHeader> updatedReservations = fetchAllReservations();
                reservationTable.setItems(updatedReservations);

                showAlert(Alert.AlertType.INFORMATION, "Cancellation Success", "Message", "You cancelled reservation " + selectedReservation.getReservationId());
            } else if (selectedReservation == null) {
                showAlert(Alert.AlertType.ERROR, "Error Cancellation", "Error", "No reservation selected.");
            } else if (selectedReservation.getReservationStatus().equals("Finished")) {
            	showAlert(Alert.AlertType.ERROR, "Error Cancellation", "Error", "Cannot cancel finished reservation");
			} else if (selectedReservation.getReservationStatus().equals("Cancelled")) {
				showAlert(Alert.AlertType.ERROR, "Error Cancellation", "Error", "Reservation is already cancelled");
			}
        });

        completeButton.setOnAction(e -> {
            ReservationHeader selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
            if (selectedReservation != null && selectedReservation.getReservationStatus().equals("In progress")) {
                String updateQuery = String.format("UPDATE ReservationHeader SET ReservationStatus = 'Finished' WHERE ReservationID = '%s'", selectedReservation.getReservationId());
                connect.execUpdate(updateQuery);

                ObservableList<ReservationHeader> updatedReservations = fetchAllReservations();
                reservationTable.setItems(updatedReservations);

                showAlert(Alert.AlertType.INFORMATION, "Cancellation Success", "Message", "You completed reservation " + selectedReservation.getReservationId());
            } else if (selectedReservation == null) {
                showAlert(Alert.AlertType.ERROR, "Error Cancellation", "Error", "No reservation selected.");
            } else if (selectedReservation.getReservationStatus().equals("Finished")) {
            	showAlert(Alert.AlertType.ERROR, "Error Cancellation", "Error", "Cannot complete finished reservation");
			} else if (selectedReservation.getReservationStatus().equals("Cancelled")) {
				showAlert(Alert.AlertType.ERROR, "Error Cancellation", "Error", "Reservation is already cancelled");
			}
        });

        // Load initial data into the table
        ObservableList<ReservationHeader> reservations = fetchAllReservations();
        reservationTable.setItems(reservations);

        return new Scene(root, 1000, 600);
    }
    
    private ObservableList<ReservationHeader> fetchAllReservations() {
        ObservableList<ReservationHeader> reservations = FXCollections.observableArrayList();
        String query = "SELECT rh.ReservationID, rh.ReservationDate, rh.StartReservationTime, rh.EndReservationTime, rh.ReservationStatus FROM ReservationHeader rh";
        try {
            ResultSet rs = connect.execQuery(query);
            while (rs.next()) {
                ReservationHeader reservation = new ReservationHeader(
                        rs.getString("ReservationID"),
                        rs.getDate("ReservationDate").toLocalDate(),
                        rs.getTime("StartReservationTime").toLocalTime(),
                        rs.getTime("EndReservationTime").toLocalTime(),
                        rs.getString("ReservationStatus")
                );
                reservations.add(reservation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reservations;
    }
    
    private boolean isServiceNameUnique(String serviceName, String currentServiceId) {
        String query;
        if (currentServiceId == null) {
            query = "SELECT COUNT(*) FROM msservice WHERE ServiceName = '" + serviceName + "'";
        } else {
            query = "SELECT COUNT(*) FROM msservice WHERE ServiceName = '" + serviceName + "' AND ServiceID <> '" + currentServiceId + "'";
        }
        try {
            ResultSet rs = connect.execQuery(query);
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private String getServiceTypeName(String serviceTypeId) {
        switch (serviceTypeId) {
            case "ST001":
                return "Haircut";
            case "ST002":
                return "Hair Perming";
            case "ST003":
                return "Hair Coloring";
            case "ST004":
                return "Hair Treatment";
            case "ST005":
                return "Hair Tattoo";
            default:
                return null; // Or handle the unknown type appropriately
        }
    }
    
    private String getServiceTypeId(String serviceType) {
        // Map service type names to IDs
        switch (serviceType) {
            case "Haircut":
                return "ST001";
            case "Hair Perming":
                return "ST002";
            case "Hair Coloring":
                return "ST003";
            case "Hair Treatment":
                return "ST004";
            case "Hair Tattoo":
                return "ST005";
            default:
                return null; // Or handle the unknown type appropriately
        }
    }
    
    private ObservableList<Service> fetchAllServices() {
        ObservableList<Service> services = FXCollections.observableArrayList();
        String query = "SELECT ServiceID, ServiceTypeID, ServiceName, ServicePrice, ServiceDuration FROM msservice"; 

        try {
            ResultSet rs = connect.execQuery(query); // Assuming you have a method to execute queries
            while (rs.next()) {
                Service service = new Service(
                        new SimpleStringProperty(rs.getString("ServiceID")),
                        new SimpleStringProperty(rs.getString("ServiceTypeID")),
                        new SimpleStringProperty(rs.getString("ServiceName")),
                        new SimpleIntegerProperty(rs.getInt("ServicePrice")),
                        new SimpleIntegerProperty(rs.getInt("ServiceDuration"))
                );
                services.add(service);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return services;
    }

    // Helper method to fetch user's reservations from the database
    private ObservableList<ReservationHeader> fetchUserReservations(String userId) {
        ObservableList<ReservationHeader> reservations = FXCollections.observableArrayList();
        String query = String.format("SELECT rh.ReservationID, rh.ReservationDate, rh.StartReservationTime, rh.EndReservationTime, rh.ReservationStatus FROM ReservationHeader rh WHERE rh.UserID = '%s'", userId); // Filter by UserID directly
        try {
            ResultSet rs = connect.execQuery(query);
            while (rs.next()) {
                ReservationHeader reservation = new ReservationHeader(
                        rs.getString("ReservationID"),
                        rs.getDate("ReservationDate").toLocalDate(),
                        rs.getTime("StartReservationTime").toLocalTime(),
                        rs.getTime("EndReservationTime").toLocalTime(),
                        rs.getString("ReservationStatus")
                );
                reservations.add(reservation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reservations;
    }

	private String getUserID(String email) {
		userID = "";
    	String query = String.format("SELECT UserID FROM msuser WHERE UserEmail = '%s'", email);
    	
    	try {
			ResultSet rs = ConnectSQL.connect.execQuery(query);
			if (rs.next()) {
				userID = rs.getString("UserID");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return userID;
    }
    
    private String validateInputs(String username, String email, String password, String confirmPassword, String phoneNumber, String gender) {
        if (username.isEmpty() ) {
            return "Username cannot be empty";
        }
        if (username.length() < 5 || username.length() > 30) {
            return "Username must be 5 - 30 characters.";
        }
        if (!email.endsWith("@gmail.com")) {
            return "Email must ends with @gmail.com";
        }
        try {
            ResultSet rs = ConnectSQL.connect.execQuery("SELECT UserEmail FROM MsUser WHERE UserEmail = '" + email + "'");
            if (rs.next()) {
                return "Email has already been used";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (password.length() < 8 || password.length() > 15 ) {
            return "Password must be 8 - 15 characters.";
        }
        if (!password.matches("^[a-zA-Z0-9]*$")) {
			return "Password must be alphanumeric";
		}
        if (!password.equals(confirmPassword)) {
            return "Password must be the same as confirm password";
        }
        String changedPhoneNumber = phoneNumber.replace("62", "0");
        try {
            ResultSet rs = ConnectSQL.connect.execQuery("SELECT UserPhoneNumber FROM MsUser WHERE UserPhoneNumber = '" + changedPhoneNumber + "'");
            if (rs.next()) {
                return "PhoneNumber has already been used";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (phoneNumber.length() < 9 || phoneNumber.length() > 13) {
            return "Phone number must be 9 - 13 characters.";
        }
        if (!phoneNumber.startsWith("62")) {
			return "Phone number must start with 62";
		}
        return null;
    }
    
    private String generateUserID() {
        userID = "";
        try {
            ResultSet rs = ConnectSQL.connect.execQuery("SELECT COUNT(*) AS userCount FROM MsUser");
            if (rs.next()) {
                int userCount = rs.getInt("userCount");
                userID = String.format("US%03d", userCount + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userID;
    }
    
    private String generateServiceID() {
        String serviceID = "";
        try {
            ResultSet rs = ConnectSQL.connect.execQuery("SELECT COUNT(*) AS serviceCount FROM Msservice");
            if (rs.next()) {
                int serviceCount = rs.getInt("serviceCount");
                serviceID = String.format("SV%03d", serviceCount + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serviceID;
    }
    
    private String generateReservationID() {
    	String reservationID = "";
        try {
            ResultSet rs = ConnectSQL.connect.execQuery("SELECT COUNT(*) AS reservationCount FROM ReservationHeader");
            if (rs.next()) {
                int reservationCount = rs.getInt("reservationCount");
                reservationID = String.format("RS%03d", reservationCount + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reservationID;
    }

    private void validateLogin(String email, String password, Stage primaryStage, TextField emailField, TextField passwordField) {
        String query = String.format("SELECT UserRole FROM MsUser WHERE UserEmail = '%s' AND UserPassword = '%s'", email, password);

        try {
            ConnectSQL connectSQL = ConnectSQL.getInstance();
            ResultSet resultSet = connectSQL.execQuery(query);

            if (resultSet.next()) {
                String role = resultSet.getString("UserRole");
                if (role.equals("User")) {
                	emailField.clear();
                	passwordField.clear();
                	reserveServiceScene = createReserveServiceScene(primaryStage, email);
                    primaryStage.setScene(reserveServiceScene); // Redirect to Reserve Service scene
                    primaryStage.show();
                    // Implement redirection logic here
                } else if (role.equals("Admin")) {
                	emailField.clear();
                	passwordField.clear();
                	serviceManagementScene = createServiceManagementScene(primaryStage);
                    primaryStage.setScene(serviceManagementScene); // Redirect to Reserve Service scene
                    primaryStage.show();
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Error","Error", "Invalid email or password.");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, null, "Database Error", "An error occurred while connecting to the database.");
            e.printStackTrace();
        }
    }

    
    private void filterTable(ObservableList<Service> serviceData, ArrayList<Service> serviceList,
            CheckBox cbHairCut, CheckBox cbTreatment, CheckBox cbHairPerming, 
            CheckBox cbHairColoring, CheckBox cbHairTattoo) {
        
        // Clear the current items in the table
        serviceData.clear();

        // If no checkboxes are selected, show all services
        if (!cbHairCut.isSelected() && !cbTreatment.isSelected() && 
            !cbHairPerming.isSelected() && !cbHairColoring.isSelected() && 
            !cbHairTattoo.isSelected()) {
            serviceData.addAll(serviceList);
            return;
        }

        // Add services that match the selected filters
        for (Service service : serviceList) {
            String serviceType = service.getServiceTypeId().get();
            
            if ((cbHairCut.isSelected() && serviceType.equals("ST001")) ||
                (cbTreatment.isSelected() && serviceType.equals("ST004")) ||
                (cbHairPerming.isSelected() && serviceType.equals("ST002")) ||
                (cbHairColoring.isSelected() && serviceType.equals("ST003")) ||
                (cbHairTattoo.isSelected() && serviceType.equals("ST005"))) {
                serviceData.add(service);
            }
        }
    }
    
    
    public ArrayList<Service> fetchServicesFromDatabase() {
        ArrayList<Service> serviceList = new ArrayList<>();
        String query = "SELECT * FROM msservice";
        connect.rs = connect.execQuery(query);
        
        try {
            while (connect.rs.next()) {
                String serviceId = connect.rs.getString("ServiceID");
                String serviceTypeId = connect.rs.getString("ServiceTypeID");
                String serviceName = connect.rs.getString("ServiceName");
                int servicePrice = connect.rs.getInt("ServicePrice");
                int serviceDuration = connect.rs.getInt("ServiceDuration");
                
                service = new Service(
                    new SimpleStringProperty(serviceId),
                    new SimpleStringProperty(serviceTypeId),
                    new SimpleStringProperty(serviceName),
                    new SimpleIntegerProperty(servicePrice),
                    new SimpleIntegerProperty(serviceDuration)
                );
                
                serviceList.add(service);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serviceList;
    }

    
    private void handleAddButton(DatePicker datePicker, TextField timeField, ListView<String> reserveListView, TableView<Service> serviceTable) {
        date = datePicker.getValue() != null ? datePicker.getValue().toString() : "";
        time = timeField.getText();
        
        dateReal = datePicker.getValue();

        // Validation
        if (serviceTable.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.ERROR, "Error Reservation", "Error", "There is no service selected");
            return;
        }
        
        if (date.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error Reservation", "Error", "Reservation date cannot be empty");
            return;
        }
        
        if (time.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error Reservation", "Error", "Reservation time cannot be empty");
            return;
        }

        try {
            reservationTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            showAlert(Alert.AlertType.ERROR, "Error Reservation", "Error", "Invalid time format");
            return;
        }

        if (datePicker.getValue().isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR, "Error Reservation", "Error", "Reservation date invalid");
            return;
        }

        if (reservationTime.isBefore(LocalTime.of(9, 0)) || reservationTime.isAfter(LocalTime.of(21, 0))) {
            showAlert(Alert.AlertType.ERROR, "Error Reservation", "Error", "Reservation Time must be between 09:00 - 21:00");
            return;
        }

        // Check for overlapping reservations
        for (String reservation : reserveListView.getItems()) {
            try {
                // Split the reservation string more carefully
                String[] parts = reservation.split(" - ");
                if (parts.length >= 3) {
                    String reservedDate = parts[1];
                    String[] timeParts = parts[2].split(" to ");
                    
                    if (timeParts.length == 2) {
                        String reservedStartTime = timeParts[0];
                        String reservedEndTime = timeParts[1];

                        if (reservedDate.equals(date)) {
                            LocalTime startTime = LocalTime.parse(reservedStartTime);
                            LocalTime endTime = LocalTime.parse(reservedEndTime);

                            if (!reservationTime.isBefore(startTime) && !reservationTime.isAfter(endTime)) {
                                showAlert(Alert.AlertType.ERROR, "Error Reservation", "Error", "Reservation time is already reserved by someone else");
                                return;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Log or handle parsing errors
                System.out.println("Error parsing reservation: " + reservation);
            }
        }
        
        Service selectedService = serviceTable.getSelectionModel().getSelectedItem();
        String serviceName = selectedService.getServiceName().get();
        int serviceDuration = selectedService.getServiceDuration().get(); // Duration in minutes
        LocalTime endReservationTime = reservationTime.plusMinutes(serviceDuration);

        if (endReservationTime.isAfter(LocalTime.of(21, 0))) {
            showAlert(Alert.AlertType.ERROR, "Error Reservation", "Error", "Reservation time is over the Barber's open hours");
            return;
        }

        // Add reservation
        reservationString = selectedService.getServiceName().get();
        reserveListView.getItems().add(reservationString);
        ReservationHeader dateAndTime = new ReservationHeader(dateReal, reservationTime, serviceName);
        e.add(dateAndTime);

        // Reset fields
        datePicker.setValue(null);
        timeField.clear();
        serviceTable.getSelectionModel().clearSelection();
        
        showAlert(Alert.AlertType.INFORMATION, "Service added", "Message", "Service successfully added");
    }

    private void handleCancelButton(ListView<String> reserveListView, DatePicker datePicker, TextField timeField) {
        // Get the selected reservation
        String selectedReservation = reserveListView.getSelectionModel().getSelectedItem();
        
        if (selectedReservation == null) {
            showAlert(Alert.AlertType.ERROR, "Error Cancellation", "Error", "No service selected");
            return;
        }

        // Remove only the selected reservation
        showAlert(Alert.AlertType.INFORMATION, "Cancellation Success", "Message", "Service successfully cancelled");
        reserveListView.getItems().remove(selectedReservation);
        
        // Only enable date/time fields if all reservations are cleared
        
        ReservationHeader reservationToRemove = null;
        for (ReservationHeader reservationHeader : e) {
            if (reservationHeader.matches(selectedReservation)) { // Create a 'matches' method in ReservationHeader
                reservationToRemove = reservationHeader;
                break;
            }
        }
        if (reservationToRemove != null) {
            e.remove(reservationToRemove);
        }
        if (reserveListView.getItems().isEmpty()) {
        	datePicker.setDisable(false);
        	timeField.setDisable(false);
        }
    }

    private void handleReserveButton(ListView<String> reserveListView, String email) {
        String reservationStatus = "In progress";

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Reserve Confirmation Pop-Up");
        confirmationAlert.setHeaderText("Reserve confirmation");
        confirmationAlert.setContentText("Are you sure you want to reserve?");

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        confirmationAlert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            boolean allReservationsSuccessful = true;

            for (ReservationHeader reservation : e) {
                try {
                    LocalDate reservationDate = reservation.getDate();
                    LocalTime startReservationTime = reservation.getReservationTime(); // Unique time per reservation
                    Service selectedService = findServiceByName(reservation.getServiceName());

                    if (selectedService == null) {
                        showAlert(Alert.AlertType.ERROR, "Reservation Error", "Error",
                                "Could not find service: " + reservation.getServiceName());
                        allReservationsSuccessful = false;
                        continue;
                    }

                    int serviceDuration = selectedService.getServiceDuration().get();
                    LocalTime endReservationTime = startReservationTime.plusMinutes(serviceDuration);
                    String serviceID = selectedService.getServiceId().get();

                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                    userID = getUserID(email);
                    String reservationID = generateReservationID();

                    String insertQuery = String.format(
                            "INSERT INTO ReservationHeader (ReservationID, UserID, ReservationDate, StartReservationTime, EndReservationTime, ReservationStatus) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')",
                            reservationID, userID, reservationDate, startReservationTime.format(timeFormatter),
                            endReservationTime.format(timeFormatter), reservationStatus);

                    try {
                        ConnectSQL.connect.execUpdate(insertQuery);
                    } catch (Exception sqlException) {  // Catch specific SQL exceptions
                        System.err.println("Error executing insertQuery:");
                        sqlException.printStackTrace(); // Print the detailed SQL error
                        showAlert(Alert.AlertType.ERROR, "Database Error", "Error", "Failed to insert into ReservationHeader: " + sqlException.getMessage());
                        allReservationsSuccessful = false;
                        continue; // Move to the next reservation if this one fails
                    }

                    String insertReservationDetail = String.format(
                            "INSERT INTO ReservationDetail (ReservationID, ServiceID) VALUES ('%s', '%s')",
                            reservationID, serviceID);

                    try {
                        ConnectSQL.connect.execUpdate(insertReservationDetail);
                    } catch (Exception sqlException) {
                        System.err.println("Error executing insertReservationDetail:");
                        sqlException.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Database Error", "Error", "Failed to insert into ReservationDetail: " + sqlException.getMessage());
                        allReservationsSuccessful = false;
                        continue;
                    }

                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Reservation Error", "Error",
                            "Failed to reserve service for: " + reservation.getServiceName());
                    allReservationsSuccessful = false;
                    e.printStackTrace();
                }
            }

            if (allReservationsSuccessful) {
                showAlert(Alert.AlertType.INFORMATION, "Reservation Success", "Message", "All reservations completed!");
                e.clear();
                reserveListView.getItems().clear();
            }
        }
    }




    // Helper method to find service by name
    private Service findServiceByName(String serviceName) {
        ArrayList<Service> serviceList = fetchServicesFromDatabase();
        for (Service service : serviceList) {
            if (service.getServiceName().get().equals(serviceName)) {
                return service;
            }
        }
        return null;
    }

    private void showAlert(Alert.AlertType alertType, String header, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}