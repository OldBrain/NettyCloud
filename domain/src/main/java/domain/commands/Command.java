package domain.commands;

import java.io.Serializable;
import java.util.*;

public class Command implements Serializable {
  public ComName commandName;
  public String[] commandArguments;
  public List<String[]> commandFileInfo;

  public Command(ComName commandName,
                 String[] commandArguments,
                 List<String[]> commandFileInfo)
  {
    this.commandName = commandName;
    this.commandArguments = commandArguments;
    this.commandFileInfo = commandFileInfo;
  }

  public Command(ComName commandName) {
    this.commandName = commandName;
    this.commandArguments = null;
    this.commandFileInfo = null;
  }

  public Command(ComName commandName, String[] commandArguments) {
    this.commandName = commandName;
    this.commandArguments = commandArguments;
    this.commandFileInfo = null;
  }
}
