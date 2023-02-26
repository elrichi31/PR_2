// outside the JavaFX application thread.
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.math.BigInteger;

public class FactorialController {
   @FXML private TextField numberTextField;
   @FXML private Button goButton;
   @FXML private Label messageLabel;
   @FXML private Label factorialLabel;
   @FXML private Label nthLabel;
   @FXML private Label nthFibonacciLabel;

   private long n1 = 0; // initialize with Fibonacci of 0
   private long n2 = 1; // initialize with Fibonacci of 1
   private int number = 1; // current Fibonacci number to display

   // starts FactorialTask to calculate in background
   @FXML
   void goButtonPressed(ActionEvent event) {
      // get factorial 
      try {
         int input = Integer.parseInt(numberTextField.getText());
         BigInteger in = BigInteger.valueOf(input);

         // create, configure and launch FactorialTask
         FactorialTask task = new FactorialTask(in);

         // display task's messages in messageLabel
         messageLabel.textProperty().bind(task.messageProperty());

         // clear factorialLabel when task starts
         task.setOnRunning((succeededEvent) -> {
            goButton.setDisable(true);
            factorialLabel.setText(""); 
         });
         
         // set factorialLabel when task completes successfully
         task.setOnSucceeded((succeededEvent) -> {
            factorialLabel.setText(task.getValue().toString());
            goButton.setDisable(false);
         });

         // create ExecutorService to manage threads
         ExecutorService executorService = 
            Executors.newFixedThreadPool(1); // pool of one thread
         executorService.execute(task); // start the task
         executorService.shutdown();
      }
      catch (NumberFormatException e) {
         numberTextField.setText("Enter an integer");
         numberTextField.selectAll();
         numberTextField.requestFocus();
      }
   }

   // calculates next Fibonacci value   
   @FXML
   void nextNumberButtonPressed(ActionEvent event) {
      // display the next Fibonacci number
      nthLabel.setText("Fibonacci of " + number + ": ");
      nthFibonacciLabel.setText(String.valueOf(n2));
      long temp = n1 + n2;
      n1 = n2;
      n2 = temp;
      ++number;
   }
}