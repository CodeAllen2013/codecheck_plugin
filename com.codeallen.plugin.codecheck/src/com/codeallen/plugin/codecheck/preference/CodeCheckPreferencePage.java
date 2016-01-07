package com.codeallen.plugin.codecheck.preference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.codeallen.plugin.codecheck.Activator;
import com.codeallen.plugin.codecheck.CodeCheckConstants;

public class CodeCheckPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Label fullPathLabel;
	private Button browseButton;
	private Text fileManagerPath;
	private String fileManagerPathString;

	protected Composite createComposite(Composite parent) {
		Composite composite = new Composite(parent, 0);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(272));

		return composite;
	}
	
	private Group createGroup(Composite composite, String title) {
	    Group groupComposite = new Group(composite, 16384);
	    GridLayout layout = new GridLayout();
	    groupComposite.setLayout(layout);
	    GridData data = new GridData(768);

	    groupComposite.setLayoutData(data);
	    groupComposite.setText(title);
	    return groupComposite;
	  }

	protected Control createContents(Composite parent) {
		Composite composite = createComposite(parent);
		Group groupComposite = createGroup(composite, "Config");
		Composite c = new Composite(groupComposite, 0);
	    c.setLayoutData(new GridData(768));
	    GridLayout layout = new GridLayout(3, false);
	    layout.marginWidth = 0;
	    c.setLayout(layout);
		this.fullPathLabel = new Label(c, 0);
		this.fullPathLabel.setText("Full Path");
		this.fileManagerPath = new Text(c, 2048);
		if (this.fileManagerPathString != null) {
			this.fileManagerPath.setText(this.fileManagerPathString);
		}
		this.fileManagerPath.setLayoutData(new GridData(768));
		this.fileManagerPath.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (CodeCheckPreferencePage.this.fileManagerPath.isEnabled()) {
					String path = CodeCheckPreferencePage.this.fileManagerPath.getText();
					if ((path == null) || (path.equals(""))) {
						CodeCheckPreferencePage.this.setValid(false);
						CodeCheckPreferencePage.this.setErrorMessage("ErrorPath Cant be blank");
						return;
					}
					File f = new File(path);
					if ((!(f.exists())) || (!(f.isFile()))) {
						CodeCheckPreferencePage.this.setValid(false);
						CodeCheckPreferencePage.this.setErrorMessage("Erorr Path Not Exist or Not a File");
						return;
					}
					CodeCheckPreferencePage.this.setErrorMessage(null);
					CodeCheckPreferencePage.this.setValid(true);
				}

			}

		});
		this.browseButton = new Button(c, 8);
		this.browseButton.setText("Browse...");
		this.browseButton.setFont(composite.getFont());
		this.browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				String newValue = CodeCheckPreferencePage.this.browsePressed();
				if (newValue != null)
					CodeCheckPreferencePage.this.setFileManagerPath(newValue);
			}
		});
		this.browseButton.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				CodeCheckPreferencePage.this.browseButton = null;
			}
		});

		return composite;
	}

	private String browsePressed() {
		File f = new File(this.fileManagerPath.getText());
		if (!(f.exists())) {
			f = null;
		}
		File filePath = getFilePath(f);
		if (filePath == null) {
			return null;
		}
		return filePath.getAbsolutePath();
	}

	private void setFileManagerPath(String value) {
		if (this.fileManagerPath != null) {
			if (value == null) {
				value = "";
			}
			this.fileManagerPath.setText(value);
		}
	}

	private File getFilePath(File startingDirectory) {
		FileDialog fileDialog = new FileDialog(getShell(), 268439552);
		if (startingDirectory != null) {
			fileDialog.setFilterPath(startingDirectory.getPath());
		}
		String filePath = fileDialog.open();
		if (filePath != null) {
			filePath = filePath.trim();
			if (filePath.length() > 0) {
				return new File(filePath);
			}
		}

		return null;
	}

	 protected IPreferenceStore doGetPreferenceStore()
	  {
	    return Activator.getDefault().getPreferenceStore();
	  }
	 
	public void init(IWorkbench workbench)
	  {
	    IPreferenceStore store = getPreferenceStore();
	    this.fileManagerPathString = store.getString(CodeCheckConstants.PLUGIN_REGEXP_TABLE_PATH);
	  }

	 protected void performDefaults()
	  {
	    IPreferenceStore store = getPreferenceStore();
	    this.fileManagerPath
	      .setText(store.getDefaultString(CodeCheckConstants.PLUGIN_REGEXP_TABLE_PATH));
	    super.performDefaults();
	  }
	
	private String getTableRegexpFromFile(String filePath){
		StringBuilder sb = new StringBuilder();
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(new File(filePath));
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = null;
			while((line =bufferedReader.readLine())!=null){
				sb.append(line);
				sb.append("|");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if(null !=fileReader){
				try {
					fileReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		String returnString = sb.toString();
		if(returnString.endsWith("|")){
			returnString = returnString.substring(0,returnString.length() - 1);
		}
		if(returnString.startsWith("|")){
			returnString = returnString.substring(1);
		}
		return returnString;
	}
	

	public boolean performOk() {
		IPreferenceStore store = doGetPreferenceStore();
		String regexpTable = getTableRegexpFromFile(this.fileManagerPath.getText());
		store.setValue("regexpTableInfo",regexpTable);
		store.setValue(CodeCheckConstants.PLUGIN_REGEXP_TABLE_PATH,this.fileManagerPath.getText());
		return super.performOk();

	}

}