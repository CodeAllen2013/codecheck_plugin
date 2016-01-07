package com.free.project;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class HelloWorldNature implements IProjectNature {
	private IProject _project;

	// 配置Nature
	public void configure() throws CoreException {
		System.out.println("configure");
		IProjectDescription projectDesc = _project.getDescription();
		ICommand[] buildSpec = projectDesc.getBuildSpec();
		boolean hasBuilder = false;
		// 遍历项目的构建器
		for (int i = 0; i < buildSpec.length; ++i) {
			if (buildSpec[i].getBuilderName().equals("com.free.proj.helloproject.helloworldbuilder")) {
				hasBuilder = true;
				System.out.println("true");
				break;
			}
		}
		if (hasBuilder == false) {
			System.out.println("false");
			ICommand newCommand = projectDesc.newCommand();
			newCommand.setBuilderName("com.free.proj.helloproject.helloworldbuilder");
			ICommand[] buildSpecs = new ICommand[buildSpec.length + 1];
			System.arraycopy(buildSpec, 0, buildSpecs, 1, buildSpec.length);
			buildSpecs[0] = newCommand;
			projectDesc.setBuildSpec(buildSpecs);
			_project.setDescription(projectDesc, null);
		}
	}

	public void deconfigure() throws CoreException {

	}

	// 获得Nature所关联的项目
	public IProject getProject() {
		System.out.println("getProject");
		return _project;
	}

	// 设置Nature所关联的项目
	public void setProject(IProject project) {
		System.out.println("setProject");
		_project = project;
	}
}