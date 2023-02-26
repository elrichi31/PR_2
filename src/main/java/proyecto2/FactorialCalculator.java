package proyecto2;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FactorialCalculator extends Application {

    int nt = 10;
    ExecutorService executorService;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Factorial Calculator");

        Label numberLabel = new Label("Number:");
        Label numberThreadLabel = new Label("Threads");

        TextField numberField = new TextField();
        TextField numberThreadField = new TextField();

        Button calculateButton = new Button("Calculate");
        Label resultLabel = new Label();
        Label res2 = new Label();

        HBox inputBox = new HBox(numberLabel, numberField);
        inputBox.setSpacing(10);
        inputBox.setPadding(new Insets(10));

        HBox inputBox2 = new HBox(numberThreadLabel, numberThreadField);
        inputBox2.setSpacing(10);
        inputBox2.setPadding(new Insets(10));

        VBox layout = new VBox(inputBox, inputBox2, calculateButton, resultLabel, res2);
        layout.setSpacing(10);
        layout.setPadding(new Insets(10));

        calculateButton.setOnAction(event -> {
            String numberString = numberField.getText();
            int numberThreadsInt = Integer.parseInt(numberThreadField.getText());
            BigInteger number = new BigInteger(numberString);

            executorService = Executors.newFixedThreadPool(numberThreadsInt);
            List<FactorialTask> tasks = new ArrayList<>();
            BigInteger chunkSize = number.divide(BigInteger.valueOf(numberThreadsInt));
            System.out.println(chunkSize);
            BigInteger start = BigInteger.ONE;
            BigInteger end = chunkSize;
            for (int i = 0; i < numberThreadsInt; i++) {
                if (i == numberThreadsInt - 1) {
                    end = number;
                }
                System.out.println("Start: " + start + ", End: " + end);
                FactorialTask task = new FactorialTask(start, end);
                tasks.add(task);
                start = end.add(BigInteger.ONE);
                end = end.add(chunkSize);
            }

            List<Future<BigInteger>> futures = new ArrayList<>();
            for (FactorialTask task : tasks) {
                //System.out.println(task);
                Future<?> future = executorService.submit(task);
                futures.add((Future<BigInteger>) future);
            }
            BigInteger result = BigInteger.ONE;
            for (Future<BigInteger> future : futures) {
                try {
                    
                    result = result.multiply(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            executorService.shutdown();
            System.out.println(result);

            resultLabel.setText("Factorial: " + result.toString());
        });

        Scene scene = new Scene(layout, 600, 250);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        executorService.shutdownNow();
    }

    public class FactorialTask implements Callable<BigInteger> {
        private final BigInteger start;
        private final BigInteger end;
    
        public FactorialTask(BigInteger start, BigInteger end) {
            this.start = start;
            this.end = end;
        }
    
        @Override
        public BigInteger call() throws Exception {
            BigInteger result = BigInteger.ONE;
            for (BigInteger i = start; i.compareTo(end) <= 0; i = i.add(BigInteger.ONE)) {
                try {
                    result = result.multiply(i);
                } catch (Exception e) {
                    e.printStackTrace();
                    return BigInteger.ZERO;
                }
            }
            return result;
        }
    }
    

    public static void main(String[] args) {
        launch(args);
    }
}
