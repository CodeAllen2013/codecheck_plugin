package com.codeallen.plugin.codecheck.popup.actions;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.codeallen.plugin.codecheck.CodeCheckConstants;

public class TenantIdCheckDeAction implements IObjectActionDelegate {
	private Collection<IProject> mSelectedProjects;

	private Shell shell;

	/**
	 * Constructor for Action1.
	 */
	public TenantIdCheckDeAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
	    BulkCheckstyleActivateJob job = new BulkCheckstyleActivateJob(this.mSelectedProjects);
	    job.schedule();
	}
	
	
	
	private class BulkCheckstyleActivateJob extends WorkspaceJob
	  {
	    private Collection<IProject> mProjectsToActivate;

	    public BulkCheckstyleActivateJob(Collection<IProject> projectsToActivate)
	    {
	      super("Activate TenantId CodeCheck for selected projects...");
	      this.mProjectsToActivate = projectsToActivate;
	    }

	    public IStatus runInWorkspace(IProgressMonitor monitor)
	      throws CoreException
	    {
	      for (IProject configurationTarget : this.mProjectsToActivate)
	      {
	        if ((!(configurationTarget.isOpen())) || 
	          (!configurationTarget.hasNature(CodeCheckConstants.PLUGIN_CODECHECK_NATURE)))
	          continue;
	        ConfigureDeconfigureNatureJob job = new ConfigureDeconfigureNatureJob(
	          configurationTarget, CodeCheckConstants.PLUGIN_CODECHECK_NATURE);
	        job.schedule();
	      }

	      return Status.OK_STATUS;
	    }
	  }
	
	

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if ((selection instanceof IStructuredSelection)) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			this.mSelectedProjects = sel.toList();
		}
	}

}
