package proyecto2;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FactorialCalculator extends Application {

    ExecutorService executorService;
    TimeData timeData = new TimeData();
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Factorial Calculator");
        DecimalFormat formato1 = new DecimalFormat("#0.0000");

        Label numberLabel = new Label("Number:");
        Label numberThreadLabel = new Label("Threads");

        TextField numberField = new TextField();
        TextField numberThreadField = new TextField();

        Button calculateButton = new Button("Calculate");

        HBox inputBox = new HBox(numberLabel, numberField);
        inputBox.setSpacing(7);
        inputBox.setPadding(new Insets(7));

        HBox inputBox2 = new HBox(numberThreadLabel, numberThreadField);
        inputBox2.setSpacing(7);
        inputBox2.setPadding(new Insets(7));

        VBox parallelBox = new VBox();
        VBox concurrentBox = new VBox();
        HBox resultsBox = new HBox(parallelBox, concurrentBox);
        resultsBox.setSpacing(30);

        VBox layout = new VBox(inputBox, inputBox2, calculateButton, resultsBox);
        layout.setSpacing(10);
        layout.setPadding(new Insets(10));

        
        calculateButton.setOnAction(event -> {
            // Se toman los valores de los textfields y se crean los threads paralelos
            String numberString = numberField.getText();
            int numberThreadsInt = Integer.parseInt(numberThreadField.getText());
            BigInteger number = new BigInteger(numberString);
            List<FactorialTask> tasksParallel = new ArrayList<>();

            BigInteger result1 = createAndExecuteThreads(numberThreadsInt, number, tasksParallel);

            parallelBox.getChildren().addAll(new Label("HILOS PARALELOS"));
            double time1 = Duration.between(timeData.start1, timeData.end1).toNanos()*0.000001;
            for(FactorialTask task: tasksParallel){
                parallelBox.getChildren().add(new Label("Start: " + task.start + " End: " + task.end + " || " + " Time: " + formato1.format(task.time1) + " ms"));
            }
            parallelBox.getChildren().addAll(new Label("Total time: " + formato1.format(time1) + " ms"));
            parallelBox.getChildren().addAll(new Label("Result: " + result1));


            // Creacion de thread concurrente
            TimeData timeConcurrent = new TimeData();
            timeConcurrent.start1 = Instant.now();
            List<FactorialTask> tasksConcurrent = new ArrayList<>();

            BigInteger result2 = createAndExecuteThreads(1, number, tasksConcurrent);
            timeConcurrent.end1 = Instant.now(); 
            double timeResult = Duration.between(timeConcurrent.start1, timeConcurrent.end1).toNanos()*0.000001;
            
            concurrentBox.getChildren().addAll(new Label("HILO CONCURRENTE"));
            for(FactorialTask task: tasksConcurrent){
                concurrentBox.getChildren().add(new Label("Start: " + task.start + " End: " + task.end + " || " + " Time: " + formato1.format(task.time1) + " ms"));
            }
            concurrentBox.getChildren().addAll(new Label("Total time: " + formato1.format(timeResult) + " ms"));
            concurrentBox.getChildren().addAll(new Label("Result: " + result2));
            
        });

        Scene scene = new Scene(layout, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
    public BigInteger createAndExecuteThreads(int numTasks, BigInteger number, List<FactorialTask> tasks){
        executorService = Executors.newFixedThreadPool(numTasks);
        BigInteger chunkSize = number.divide(BigInteger.valueOf(numTasks));
        BigInteger start = BigInteger.ONE;
        BigInteger end = chunkSize;
        for (int i = 0; i < numTasks; i++) {
            if (i == numTasks - 1) {
                    end = number;
            }
            FactorialTask task = new FactorialTask(start, end);
            tasks.add(task);
            start = end.add(BigInteger.ONE);
            end = end.add(chunkSize);
        }
        
        timeData.start1 = Instant.now(); 

        List<Future<BigInteger>> futures = new ArrayList<>();
        for (FactorialTask task : tasks) {
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
        timeData.end1 = Instant.now(); 
        return result; 
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        executorService.shutdownNow();
    }

    public class FactorialTask implements Callable<BigInteger> {
        private final BigInteger start;
        private final BigInteger end;
        DecimalFormat formato1 = new DecimalFormat("#0.0000");
        public double time1;
    
        public FactorialTask(BigInteger start, BigInteger end) {
            this.start = start;
            this.end = end;
        }
    
        @Override
        public BigInteger call() throws Exception {
            BigInteger result = BigInteger.ONE;
            TimeData timeData = new TimeData();
            timeData.start1 = Instant.now(); 
            for (BigInteger i = start; i.compareTo(end) <= 0; i = i.add(BigInteger.ONE)) {
                try {
                    result = result.multiply(i);
                } catch (Exception e) {
                    e.printStackTrace();
                    return BigInteger.ZERO;
                }
            }
            timeData.end1 = Instant.now(); 
            double time1 = Duration.between(timeData.start1, timeData.end1).toNanos()*0.000001;
            this.time1 = time1;
            return result;
        }
    }
    class TimeData {
        public Instant start1;
        public Instant end1;
    } 
    public static void main(String[] args) {
        launch(args);
    }
}
