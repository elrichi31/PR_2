package proyecto2.FactorialJFX;

// Task subclass for calculating factorial result in the background
import java.math.BigInteger;
import java.time.Duration;
import java.text.DecimalFormat;
import java.time.Instant;
import javafx.concurrent.Task;

class TimeData {
    public Instant start1;
    public Instant end1;
 } 

public class FactorialTask extends Task<BigInteger> {
   private BigInteger number;
   private BigInteger result; 

   // constructor
   public FactorialTask(BigInteger number) {
        this.number = number;
        this.result = BigInteger.ONE;
    }

   // long-running code to be run in a worker thread
   @Override
   protected BigInteger call() {
      TimeData timeData = new TimeData();
      timeData.start1 = Instant.now(); 
      updateMessage("Calculating...");
      this.result = calculateFactorial(this.number);
      timeData.end1 = Instant.now(); 
      double time1 = Duration.between(timeData.start1, timeData.end1).toNanos()*0.000001;
      updateMessage("Done calculating."); 
      return result;
   } 

   // calculates factorial number
   private BigInteger calculateFactorial(BigInteger number) {
        BigInteger result = BigInteger.ONE;
        for (BigInteger i = BigInteger.TWO; i.compareTo(number) <= 0; i = i.add(BigInteger.ONE)) {
            result = result.multiply(i);
        }
        return result;
    }
} 