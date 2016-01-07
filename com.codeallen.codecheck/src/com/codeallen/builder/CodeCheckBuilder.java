package com.codeallen.builder;

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
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.ElementWithLineNumber;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class CodeCheckBuilder extends IncrementalProjectBuilder {

	@Override
	protected IProject[] build(int arg0, Map<String, String> arg1, IProgressMonitor arg2) throws CoreException {
		getProject().accept(new CodeCheckVisitor());
		return null;
	}

	private class CodeCheckVisitor implements IResourceVisitor {
		public boolean visit(IResource resource) throws CoreException {
			switch (resource.getType()) {
			case IResource.FILE:
				parseDbXml(resource);
			}
			return true;
		}
	}

	private void parseDbXml(IResource resource) {
		String xmlPath = resource.getLocation().toString();
		if (xmlPath.endsWith(".xml")) {
			// 实例化一个解析器对象
			SAXBuilder builder = new SAXBuilder();
			try {
				// 通过XML文件，构造文档对象
				Document read_doc = builder.build(xmlPath);
				// 得到根元素
				Element stu = read_doc.getRootElement();
				if (stu.getName().equals("mapper")) {
					// 得到student元素的列表
					List<ElementWithLineNumber> selectElementList = stu.getChildren("select");

					for (ElementWithLineNumber d : selectElementList) {
						Pattern selectPattern = Pattern.compile(
								"([\\s\\S]*)select([\\s\\S]+)from([\\s\\S]+)where([\\s\\S]+)\\#\\{tenantId\\}([\\s\\S]*)",
								Pattern.CASE_INSENSITIVE);
						Matcher selectMatcher = selectPattern.matcher(d.getText());
						if (!selectMatcher.matches()) {
							addMarker(resource, "this is test mark", d.getLineNumber(), 2, 2);
						}
					}
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

	private void addMarker(IResource resource, String message, int lineNumber, int severity, int priority)
			throws CoreException {
		if (resource != null) {
			IMarker marker = resource.createMarker("com.codeallen.codecheck.CodeCheckMark");
			if (message != null)
				marker.setAttribute(IMarker.MESSAGE, message);
			if (lineNumber >= 0)
				marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
			marker.setAttribute(IMarker.SEVERITY, severity);
			marker.setAttribute(IMarker.PRIORITY, priority);
		}
	}

}
