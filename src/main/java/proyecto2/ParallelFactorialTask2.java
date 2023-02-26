package proyecto2;
import java.math.BigInteger;
import java.time.Duration;
import java.text.DecimalFormat;
import java.time.Instant;

import javafx.concurrent.Task;

class TimeData {
    public Instant start1;
    public Instant end1;
 } 
public class ParallelFactorialTask2 extends Task<BigInteger> {

    private final BigInteger start;
    private final BigInteger end;
    private BigInteger result;

    public ParallelFactorialTask2(BigInteger start, BigInteger end) {
        this.start = start;
        this.end = end;
        this.result = BigInteger.ONE;
    }

    DecimalFormat formato1 = new DecimalFormat("#0.0000");

    @Override
    public BigInteger call() throws Exception {
        TimeData timeData = new TimeData();
        timeData.start1 = Instant.now(); 
        this.result = calculateFactorial(this.start, this.end);
        timeData.end1 = Instant.now(); 
        double time1 = Duration.between(timeData.start1, timeData.end1).toNanos()*0.000001;
        System.out.println("Factorial " + start + " to " + end + ", Time: " + formato1.format(time1) + " miliseconds");
        return result;
    }

    private BigInteger calculateFactorial(BigInteger start, BigInteger end) {
        BigInteger result = BigInteger.ONE;
        for (BigInteger i = start; i.compareTo(end) <= 0; i = i.add(BigInteger.ONE)) {
            result = result.multiply(i);
        }
        return result;
    }

    public BigInteger getResult() {
        return this.result;
    }

    public static BigInteger createAndExecuteThreads(int numTasks, BigInteger number){
        DecimalFormat formato1 = new DecimalFormat("#0.0000");
        TimeData timeData = new TimeData();
        timeData.start1 = Instant.now(); 
        
        BigInteger taskSize = number.divide(BigInteger.valueOf(numTasks));
        ParallelFactorialTask2[] tasks = new ParallelFactorialTask2[numTasks];

        BigInteger start = BigInteger.ONE;
        BigInteger end = taskSize;

        // Crear las tareas
        for (int i = 0; i < numTasks; i++) {
            if (i == numTasks - 1) {
                end = number;
            }
            tasks[i] = new ParallelFactorialTask2(start, end);
            start = end.add(BigInteger.ONE);
            end = end.add(taskSize);
        }

        // Ejecutar las tareas en hilos separados
        Thread[] threads = new Thread[numTasks];
        for (int i = 0; i < numTasks; i++) {
            threads[i] = new Thread(tasks[i]);
            threads[i].start();
        }

        // Esperar a que los hilos terminen y combinar los resultados
        BigInteger result = BigInteger.ONE;
        for (int i = 0; i < numTasks; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            result = result.multiply(tasks[i].getValue());
        }
        timeData.end1 = Instant.now(); 
        double time1 = Duration.between(timeData.start1, timeData.end1).toNanos()*0.000001;
        System.out.println("Tiempo total: " + formato1.format(time1) + " milisegundos");
        return result;
    }
    public static void main(String[] args) {
        BigInteger number = new BigInteger("100000"); // Número para el cual se quiere calcular el factorial
        int numTasks = 5; // Número de tareas que se desean crear (ingresado por el usuario)
        int numTasks2 = 1;
        System.out.println("----------------Hilos en paralelo---------------");
        BigInteger res = createAndExecuteThreads(numTasks, number);
        System.out.println("El factorial de " + number + " es: " + res);
        System.out.println("----------------Hilo concurrente---------------");
        BigInteger res2 = createAndExecuteThreads(numTasks2, number);
        System.out.println("El factorial de " + number + " es: " + res2);
    }
}
