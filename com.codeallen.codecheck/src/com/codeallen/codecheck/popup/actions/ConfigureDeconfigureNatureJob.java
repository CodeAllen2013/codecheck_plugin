package com.codeallen.codecheck.popup.actions;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

public class ConfigureDeconfigureNatureJob extends WorkspaceJob
{
  private IProject mProject;
  private String mNatureId;
  private IProgressMonitor mMonitor;

  public ConfigureDeconfigureNatureJob(IProject project, String natureId)
  {
    super(NLS.bind("Configure project nature {0}", natureId));
    this.mProject = project;
    this.mNatureId = natureId;
  }

  public IStatus runInWorkspace(IProgressMonitor monitor)
    throws CoreException
  {
    IStatus status = null;

    this.mMonitor = monitor;
    try
    {
      if (this.mProject.hasNature(this.mNatureId)) {
        disableNature();
      }
      else {
        enableNature();
      }

      status = Status.OK_STATUS;
    }
    finally {
      monitor.done();
    }
    return status;
  }

  private void enableNature()
    throws CoreException
  {
    IProjectDescription desc = this.mProject.getDescription();

    String[] natures = desc.getNatureIds();

    String[] newNatures = new String[natures.length + 1];
    System.arraycopy(natures, 0, newNatures, 0, natures.length);
    newNatures[natures.length] = this.mNatureId;

    desc.setNatureIds(newNatures);
    this.mProject.setDescription(desc, this.mMonitor);
  }

  private void disableNature()
    throws CoreException
  {
    IProjectDescription desc = this.mProject.getDescription();
    String[] natures = desc.getNatureIds();

    List newNaturesList = new ArrayList();
    for (int i = 0; i < natures.length; ++i) {
      if (!(this.mNatureId.equals(natures[i]))) {
        newNaturesList.add(natures[i]);
      }
    }

    String[] newNatures = (String[])newNaturesList.toArray(new String[newNaturesList.size()]);

    desc.setNatureIds(newNatures);
    this.mProject.setDescription(desc, this.mMonitor);
  }
}