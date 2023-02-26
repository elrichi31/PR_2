package proyecto2;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;

import javafx.concurrent.Task;

public class ParallelFactorialTask extends Task<BigInteger> {

    private final BigInteger start;
    private final BigInteger end;
    private BigInteger result;

    public ParallelFactorialTask(BigInteger start, BigInteger end) {
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
}

