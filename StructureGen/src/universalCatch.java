/*
Class to catch and log any uncaught exceptions
*/
import javax.swing.*;


//catch anything that gets back to main
public class universalCatch implements Thread.UncaughtExceptionHandler
{
  //constructor
  public universalCatch()
  {
    
  }
  
  public void uncaughtException(Thread brokenThread, Throwable brokenThreadException)
  {
    j.err(new Exception("An uncaught exception occurred", brokenThreadException));
    
    if(brokenThreadException instanceof OutOfMemoryError)
    {
      JOptionPane.showMessageDialog(null, "The program has run out of memory!  I highly recommend you exit the program immediately and increase the memory limit in the batch file \"runjar.bat\"  If this frequently occurs, please report this in the forum thread.");
      j.err(new Exception("Out Of Memory!", brokenThreadException));
    }
    else
    {
      JOptionPane.showMessageDialog(null, "An uncaught Exception has occurred.  I recommend immediately saving and restarting the program.  Please also report this on the forum thread.");
      j.err(new Exception("Uncaught Exception or Error"));
    }
  }
}