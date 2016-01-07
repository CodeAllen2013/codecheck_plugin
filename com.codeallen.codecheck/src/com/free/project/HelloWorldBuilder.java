package com.free.project;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class HelloWorldBuilder extends IncrementalProjectBuilder {
    //实现资源监听器
    private class HelloWorldVisitor implements IResourceVisitor {
        public boolean visit(IResource resource) throws CoreException {
            System.out.println("**** HelloWorldVisitor.visit() ****");
            switch (resource.getType()) {
            case IResource.PROJECT:
                System.out.println("Project added: " + resource.getName());
                break;
            case IResource.FOLDER:
                System.out.println("Folder added: " + resource.getName());
                break;
            case IResource.FILE:
                System.out.println("File added: " + resource.getName());
                break;
            }
            return true; 
        }
    }
    //实现资源监听器
    private class HelloWorldDeltaVisitor implements IResourceDeltaVisitor {
        public boolean visit(IResourceDelta delta) throws CoreException {
            System.out.println("**** HelloWorldDeltaVisitor.visit() ****");
            String type = null;
            switch (delta.getResource().getType()) {
            case IResource.ROOT:
                type = "ROOT";
                break;
            case IResource.PROJECT:
                type = "Project";
                break;
            case IResource.FOLDER:
                type = "Folder";
                break;
            case IResource.FILE:
                type = "File";
                break;
            }
            switch (delta.getKind()) {
            case IResourceDelta.ADDED:
                System.out.println(type + " added: "+ delta.getResource().getName());
                break;
            case IResourceDelta.CHANGED:
                System.out.println(type + " changed: "
                        + delta.getResource().getName());
                break;
            case IResourceDelta.REMOVED:
                System.out.println(type + " removed: "
                        + delta.getResource().getName());
                break;
            }
            return true; 
        }
    }
    public HelloWorldBuilder() {
        System.out.println("HelloWorldBuilder.constructor()");
    }
    //开始构建
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
            throws CoreException {
        System.out.println("HelloWorldBuilder.build()");
        //当构建类型不同时，添加不同的资源监听器
        if (kind == IncrementalProjectBuilder.FULL_BUILD) {
            System.out.println("FULL_BUILD");
            getProject().accept(new HelloWorldVisitor());
        } else {
            IResourceDelta delta = getDelta(getProject());
            if (delta == null) {
                System.out.println("AUTO_BUILD");
                getProject().accept(new HelloWorldVisitor());
            } else {
                System.out.println("INCREMENTAL_BUILD");
                delta.accept(new HelloWorldDeltaVisitor());
            }
        }
        return null;
    }
}