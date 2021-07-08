package domain.commands;

import java.io.Serializable;
import java.util.*;

public class Command implements Serializable {
  public CommandName commandName;
  public String[] commandArguments;
  public List<String[]> commandFileInfo;

  public Command(CommandName commandName,
                 String[] commandArguments,
                 List<String[]> commandFileInfo)
  {
    this.commandName = commandName;
    this.commandArguments = commandArguments;
    this.commandFileInfo = commandFileInfo;
  }

  public Command(CommandName commandName) {
    this.commandName = commandName;
    this.commandArguments = null;
    this.commandFileInfo = null;
  }

  public Command(CommandName commandName, String[] commandArguments) {
    this.commandName = commandName;
    this.commandArguments = commandArguments;
    this.commandFileInfo = null;
  }
}
