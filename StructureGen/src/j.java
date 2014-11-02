/*
Used for outputting stuff to the command prompt or log file
*/
import java.io.StringWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Calendar;
import java.text.SimpleDateFormat;

//shorthand class to output errors and other messages
public class j
{
  private static String drawString = "";
  
  //Constructor, never used
  public j()
  {
    
  }
  
  //output a blank line
  public static void out()
  {
    System.out.println("");
  }
  
  //output a line to the cli
  public static void out(Object writeOut)
  {
    if(writeOut != null)
    {
      System.out.println(writeOut.toString());
    }
    else
    {
      System.out.println("null");
    }
  }
  
  //writes a line to the debug log
  public static void debug(Object debugOut)
  {
    try
    (
      FileWriter debugWriter = new FileWriter(staticData.buildPath(new String[]{"logs", "ConstructionDebug.txt"}), true)
    )
    {
      //write the line to file
      debugWriter.write(debugOut.toString() + "\n");
    }
    catch(IOException IOe)
    {
      out(new Exception("Could not write to debug file!", IOe));
      return;
    }
    catch(NullPointerException NPe)
    {
      debug("null");
    }
  }
  
  //write an error message and exception stack trace to the log file
  public static void err(Exception writeStack)
  {
    //holds the stack trace as a string
    StringWriter exceptionWriter = new StringWriter();
    
    //get the stack trace
    writeStack.printStackTrace(new PrintWriter(exceptionWriter));
    
    //try to write to the file
    //second argument concatenates the new data
    try
    (
      FileWriter errorLogWriter = new FileWriter(staticData.buildPath(new String[]{"logs", "ConstructionLog.txt"}), true)
    )
    {
      //the time
      String errorTime = new SimpleDateFormat("'Error occurred on: 'EEEE MMMM yyyy - HH:mm:ss a:").format(Calendar.getInstance().getTime());
      
      //write out the message and time
      errorLogWriter.write(errorTime + "\n");
      errorLogWriter.write(writeStack.getMessage() + "\n");
      
      //write out the stack trace
      errorLogWriter.write(exceptionWriter.toString() + "\n");
    }
    catch(IOException IOe)
    {
      System.out.println("Could not write to log file!");
      IOe.printStackTrace();
      return;
    }
    
    //also output to cli
    j.out(writeStack.getMessage());
    j.out(exceptionWriter.toString());
  }
  
  //set the text to draw
  //use j instead of this, since it's a static method
  public static void setDrawString(String drawString)
  {
    j.drawString = drawString;
  }
  
  //get the string to draw
  public static String getDrawString()
  {
    return drawString;
  }
}