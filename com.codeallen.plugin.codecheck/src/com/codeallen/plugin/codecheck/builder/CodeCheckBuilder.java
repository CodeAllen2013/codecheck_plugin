package com.codeallen.plugin.codecheck.builder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.ElementWithLineNumber;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.codeallen.plugin.codecheck.Activator;
import com.codeallen.plugin.codecheck.CodeCheckConstants;

public class CodeCheckBuilder extends IncrementalProjectBuilder {

	@Override
	protected IProject[] build(int arg0, Map<String, String> arg1, IProgressMonitor arg2) throws CoreException {
		IProject project = getProject();
		project.deleteMarkers(CodeCheckConstants.PLUGIN_CODECHECK_MARKER, false, IResource.DEPTH_INFINITE);
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String regexpTableInfo = store.getString("regexpTableInfo");
		if(regexpTableInfo == null || regexpTableInfo.equals("")){
			return null;
		}
		getProject().accept(new CodeCheckVisitor(regexpTableInfo));
		return null;
	}
	private class CodeCheckVisitor implements IResourceVisitor {
		private String regexpTableInfo;
		public CodeCheckVisitor(String regexpTableInfo) {
			this.regexpTableInfo = regexpTableInfo;
		}
		public boolean visit(IResource resource) throws CoreException {
			switch (resource.getType()) {
			case IResource.FILE:
				parseDbXml(resource,regexpTableInfo);
			}
			return true;
		}
	}

	private void parseDbXml(IResource resource,String regexpTableInfo) {
		String xmlPath = resource.getLocation().toString();
		if (xmlPath.endsWith(".xml") && xmlPath.contains("/src")) {
			// 实例化一个解析器对象
			SAXBuilder builder = new SAXBuilder();
			try {
				Document read_doc = builder.build(xmlPath);
				Element stu = read_doc.getRootElement();
				if (stu.getName().equals("mapper")) {
					Pattern tablePattern = Pattern.compile("([\\s\\S]*)("+regexpTableInfo+")([\\s\\S]*)",
							Pattern.CASE_INSENSITIVE);
					checkSelectElement(stu, tablePattern, resource);
					checkUpdateElement(stu, tablePattern, resource);
					checkInsertElement(stu, tablePattern, resource);
					checkDeleteElement(stu, tablePattern, resource);
				}

			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	@SuppressWarnings("unchecked")
	private void checkSelectElement(Element stu,Pattern tablePattern,IResource resource) throws CoreException{
		List<ElementWithLineNumber> selectElementList = stu.getChildren("select");
		for (ElementWithLineNumber d : selectElementList) {
			Matcher tableMather = tablePattern.matcher(d.getText());
			if(tableMather.matches()){
				Pattern selectPattern = Pattern.compile(
						"([\\s\\S]*)select([\\s\\S]+)from([\\s\\S]+)where([\\s\\S]+)\\#\\{tenantId\\}([\\s\\S]*)",
						Pattern.CASE_INSENSITIVE);
				Matcher selectMatcher = selectPattern.matcher(d.getText());
				if (!selectMatcher.matches()) {
					addMarker(resource,d.getLineNumber(), 2, 2);
				}
			}
		}
	}
	@SuppressWarnings("unchecked")
	private void checkDeleteElement(Element stu,Pattern tablePattern,IResource resource) throws CoreException{
		List<ElementWithLineNumber> deleteElementList = stu.getChildren("delete");
		for (ElementWithLineNumber d : deleteElementList) {
			Matcher tableMather = tablePattern.matcher(d.getText());
			if(tableMather.matches()){
				Pattern deletePattern = Pattern.compile(
						"([\\s\\S]*)delete([\\s\\S]+)from([\\s\\S]+)where([\\s\\S]+)\\#\\{tenantId\\}([\\s\\S]*)",
						Pattern.CASE_INSENSITIVE);
				Matcher deleteMatcher = deletePattern.matcher(d.getText());
				if (!deleteMatcher.matches()) {
					addMarker(resource,d.getLineNumber(), 2, 2);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void checkUpdateElement(Element stu,Pattern tablePattern,IResource resource) throws CoreException{
		List<ElementWithLineNumber> updateElementList = stu.getChildren("update");
		for (ElementWithLineNumber d : updateElementList) {
			Matcher tableMather = tablePattern.matcher(d.getText());
			if(tableMather.matches()){
				Pattern updatePattern = Pattern.compile(
						"([\\s\\S]*)update([\\s\\S]+)set([\\s\\S]+)where([\\s\\S]+)\\#\\{tenantId\\}([\\s\\S]*)",
						Pattern.CASE_INSENSITIVE);
				Matcher updateMatcher = updatePattern.matcher(d.getText());
				if (!updateMatcher.matches()) {
					addMarker(resource,d.getLineNumber(), 2, 2);
				}
			}
		}
	}
	@SuppressWarnings("unchecked")
	private void checkInsertElement(Element stu,Pattern tablePattern,IResource resource) throws CoreException{
		List<ElementWithLineNumber> insertElementList = stu.getChildren("insert");
		for (ElementWithLineNumber d : insertElementList) {
			Matcher tableMather = tablePattern.matcher(d.getText());
			if(tableMather.matches()){
				Pattern insertPattern = Pattern.compile(
						"([\\s\\S]*)insert into([\\s\\S]+)\\#\\{tenantId\\}([\\s\\S]*)",
						Pattern.CASE_INSENSITIVE);
				Matcher insertMatcher = insertPattern.matcher(d.getText());
				if (!insertMatcher.matches()) {
					addMarker(resource,d.getLineNumber(), 2, 2);
				}
			}
		}
	}

	private void addMarker(IResource resource,int lineNumber, int severity, int priority)
			throws CoreException {
		if (resource != null) {
			IMarker marker = resource.createMarker(CodeCheckConstants.PLUGIN_CODECHECK_MARKER);
			marker.setAttribute(IMarker.MESSAGE, "this sql need tenantId");
			if (lineNumber >= 0)
				marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
			marker.setAttribute(IMarker.SEVERITY, severity);
			marker.setAttribute(IMarker.PRIORITY, priority);
		}
	}

}
