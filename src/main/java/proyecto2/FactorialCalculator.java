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

    ExecutorService executorService;//objeto de la clase ExecutorService
    TimeData timeData = new TimeData();//objeto para medir tiempos
    
    @Override
    //se sobrecarga el metodo start para la GUI de JavaFX
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Factorial Calculator");//titulo del GUI
        DecimalFormat formato1 = new DecimalFormat("#0.0000");
         
        //nombre de los campos a preguntar al usuario
        Label numberLabel = new Label("Number:");
        Label numberThreadLabel = new Label("Threads");

        
        //el usuario decide el numero a calcular y el numero de threads que desea
        TextField numberField = new TextField();
        TextField numberThreadField = new TextField();

        
        Button calculateButton = new Button("Calculate");
        Button resetButton = new Button("Reset");
        
        //se ingresan los valores preguntados
        //se especifica las caracteristicas de las hbox en donde se ingresan los datos
        HBox inputBox = new HBox(numberLabel, numberField);
        inputBox.setSpacing(7);
        inputBox.setPadding(new Insets(7));

        HBox inputBox2 = new HBox(numberThreadLabel, numberThreadField);
        inputBox2.setSpacing(7);
        inputBox2.setPadding(new Insets(7));

        //se especifica las caracteristicas de la hbox en donde se muestran los resultados
        VBox parallelBox = new VBox();
        VBox concurrentBox = new VBox();
        HBox resultsBox = new HBox(parallelBox, concurrentBox);
        resultsBox.setSpacing(30);

        //la vbox en donde se despliega todo
        VBox layout = new VBox(inputBox, inputBox2, calculateButton, resetButton, resultsBox);
        layout.setSpacing(10);
        layout.setPadding(new Insets(10));

        resetButton.setOnAction(event -> {
        parallelBox.getChildren().clear();
        concurrentBox.getChildren().clear();
        numberField.setText("");
        numberThreadField.setText("");
        });
        
        //el calculo del factorial empieza tras accionar el boton
        calculateButton.setOnAction(event -> {

            calculateButton.setDisable(true);
        
            // Se toman los valores de los textfields y se crean los threads paralelos
            //Se castea al tipo de datos para su manejo 
            String numberString = numberField.getText();
            int numberThreadsInt = Integer.parseInt(numberThreadField.getText());
            BigInteger number = new BigInteger(numberString);
            
            //Dados los threads, se calcula el factorial en paralelo
            List<FactorialTask> tasksParallel = new ArrayList<>();
            BigInteger result1 = createAndExecuteThreads(numberThreadsInt, number, tasksParallel);
            
            //se despliegan los resultados y tiempos por intervalos
            //el numero de intervalos es el numero de threads
            parallelBox.getChildren().addAll(new Label("HILOS PARALELOS"));
            double time1 = Duration.between(timeData.start1, timeData.end1).toNanos()*0.000001;
            for(FactorialTask task: tasksParallel){
                parallelBox.getChildren().add(new Label("Start: " + task.start + " End: " + task.end + " || " + " Time: " + formato1.format(task.time1) + " ms"));
            }
            //se muestra el tiempo total de los hilos paralelos junto con el factorial resultante
            parallelBox.getChildren().addAll(new Label("Total time: " + formato1.format(time1) + " ms"));
            parallelBox.getChildren().addAll(new Label("Result: " + result1));


            // Creacion de thread concurrente
            TimeData timeConcurrent = new TimeData();
            timeConcurrent.start1 = Instant.now();
            List<FactorialTask> tasksConcurrent = new ArrayList<>();
            
            //se usa el mismo algoritmo de calculo pero con 1 thread 
            BigInteger result2 = createAndExecuteThreads(1, number, tasksConcurrent);
            timeConcurrent.end1 = Instant.now(); 
            double timeResult = Duration.between(timeConcurrent.start1, timeConcurrent.end1).toNanos()*0.000001;
            
            concurrentBox.getChildren().addAll(new Label("HILO CONCURRENTE"));
            for(FactorialTask task: tasksConcurrent){
                concurrentBox.getChildren().add(new Label("Start: " + task.start + " End: " + task.end + " || " + " Time: " + formato1.format(task.time1) + " ms"));
            }
            //se muestra el tiempo total del hilo concurrente junto con el factorial resultante
            concurrentBox.getChildren().addAll(new Label("Total time: " + formato1.format(timeResult) + " ms"));
            concurrentBox.getChildren().addAll(new Label("Result: " + result2));
            
            calculateButton.setDisable(false);
            
        });
         
        
        
        
        
        //se agrega todas las boxes a la scene y luego a la stage del GUI
        Scene scene = new Scene(layout, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
    
    //metodo para el calculo del factorial en hilos paralelos
    public BigInteger createAndExecuteThreads(int numTasks, BigInteger number, List<FactorialTask> tasks){
        
        //el executorService nos sirve para hacer los hilos en paralelo y mostrarlos en el GUI
        executorService = Executors.newFixedThreadPool(numTasks);
        
        //dado el numero de threads, se divide al numero del que se desea calcular el factorial
        //para obtener el tamaño de los intervalos
        BigInteger chunkSize = number.divide(BigInteger.valueOf(numTasks));
        
        //por default, el factorial de 0 es 1 y el factorial de 1 es 1
        BigInteger start = BigInteger.ONE;
        BigInteger end = chunkSize;
        
        //cuantos hilos se van a tener
        for (int i = 0; i < numTasks; i++) {
            if (i == numTasks - 1) {
                    end = number;//tamaño de los intervalos
            }
            
            //el intervalo calculado, que representa la operacion de un hilo, se agrega a 
            //una lista para ser mostrado en el GUI, mediante el ArrayList taskParallel
            FactorialTask task = new FactorialTask(start, end);
            tasks.add(task);
            //se prosigue con el siguiente hilo/intervalo
            start = end.add(BigInteger.ONE);
            end = end.add(chunkSize);
        }
        
        //se mide el tiempo una vez se termina todo el calculo
        timeData.start1 = Instant.now(); 
        
        //se hace uso de la clase future, esta trabaja con el ExecutorService
        //mediante submit, similar a runnable y run
        List<Future<BigInteger>> futures = new ArrayList<>();
        //se procede con todos los threads
        for (FactorialTask task : tasks) {
            //uno a uno son agregados(submit)
            Future<?> future = executorService.submit(task);
            futures.add((Future<BigInteger>) future);
        }
        
        BigInteger result = BigInteger.ONE;//factorial de 0 es 1, factorial de 1 es 1
        for (Future<BigInteger> future : futures) {
            try {
                
                result = result.multiply(future.get());//calculo del factorial, se ve mas adelante
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();//se concluye con el proceso de los hilos
        timeData.end1 = Instant.now();//finalmente se mide el tiempo total
        return result;//resultado del factorial
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        executorService.shutdownNow();
    }

    public class FactorialTask implements Callable<BigInteger> {//clase donde se maneja el calculo del factorial
        private final BigInteger start;
        private final BigInteger end;
        DecimalFormat formato1 = new DecimalFormat("#0.0000");
        public double time1;
    
        public FactorialTask(BigInteger start, BigInteger end) {//constuctor de la clase
            this.start = start;
            this.end = end;
        }
        
        //metodo de submit, al sobrecargarlo se agrega el algoritmo de calculo de factorial
        @Override
        public BigInteger call() throws Exception {
            BigInteger result = BigInteger.ONE;
            TimeData timeData = new TimeData();
            timeData.start1 = Instant.now(); //se mide el tiempo antes del calculo
            for (BigInteger i = start; i.compareTo(end) <= 0; i = i.add(BigInteger.ONE)) {//algoritmo manejando BigIntegers
                try {
                    result = result.multiply(i);
                } catch (Exception e) {
                    e.printStackTrace();
                    return BigInteger.ZERO;
                }
            }
            timeData.end1 = Instant.now();//se mide el tiempo luego del calculo
            double time1 = Duration.between(timeData.start1, timeData.end1).toNanos()*0.000001;
            this.time1 = time1;//se actualiza el tiempo y el resultado del factorial
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
