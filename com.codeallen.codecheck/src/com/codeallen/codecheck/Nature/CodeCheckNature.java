package com.codeallen.codecheck.Nature;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

public class CodeCheckNature implements IProjectNature
{
  private IProject mProject;

  public void configure()
    throws CoreException
  {
    IProjectDescription description = this.mProject.getDescription();
    ICommand[] commands = description.getBuildSpec();
    boolean found = false;
    for (int i = 0; i < commands.length; ++i) {
      if (commands[i].getBuilderName().equals("com.codeallen.codecheck.CodeCheckBuilder")) {
        found = true;
        break;
      }
    }

    if (found)
      return;
    ICommand command = description.newCommand();
    command.setBuilderName("com.codeallen.codecheck.CodeCheckBuilder");
    ICommand[] newCommands = new ICommand[commands.length + 1];

    System.arraycopy(commands, 0, newCommands, 0, commands.length);
    newCommands[commands.length] = command;
    description.setBuildSpec(newCommands);
    this.mProject.setDescription(description, null);
  }

  public void deconfigure()
    throws CoreException
  {
    IProjectDescription description = this.mProject.getDescription();
    ICommand[] commands = description.getBuildSpec();
    List newCommandsVec = new ArrayList();
    for (int i = 0; i < commands.length; ++i) {
      if (commands[i].getBuilderName().equals("com.codeallen.codecheck.CodeCheckBuilder")) {
        continue;
      }

      newCommandsVec.add(commands[i]);
    }

    ICommand[] newCommands = (ICommand[])newCommandsVec.toArray(new ICommand[newCommandsVec.size()]);
    description.setBuildSpec(newCommands);

    this.mProject.setDescription(description, new NullProgressMonitor());

    getProject().deleteMarkers("com.codeallen.codecheck.CodeCheckMark", true, 2);
  }

  public IProject getProject()
  {
    return this.mProject;
  }

  public void setProject(IProject project)
  {
    this.mProject = project;
  }

}