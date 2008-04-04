/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.swtshell;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.epics.ioc.dbd.DBD;
import org.epics.ioc.dbd.DBDFactory;
import org.epics.ioc.dbd.XMLToDBDFactory;
import org.epics.ioc.util.IOCFactory;
import org.epics.ioc.util.MessageType;
import org.epics.ioc.util.Requester;

/**
 * A shell for loading a new Database Definition or Record Instance into a running JavaIOC.
 * The new definition is added only if no errors occur.
 * New record instances are only added if they are now records and initialize properly.
 * The controls are:
 * <ul>
 *    <li>find<br />
 *       Clicking this button brings up a file dialog window.
 *       The selected file appears in the text window at the end of the controls row.
 *    </li>
 *    <li>show<br />
 *      Clicking this lists the currently selected file.
 *    </li>
 *    <li>loadDBD<br />
 *    Clicking this loads the currently selected Database Definition file.
 *    If any errors are reported in the test window after the clear button.
 *    </li>
 *    <li>loadDB<br />
 *    Clicking this loads the currently selected record instance file.
 *    If any errors are reported in the test window after the clear button.
 *    </li>
 *    <li>text input window<br />
 *    A file name followed by the enter key can be used to specify a file name.
 *    </li>
 * </ul>
 *       
 * @author mrk
 *
 */
public class LoadDatabaseFactory {
    
    /**
     * Create the shell for loading a javaIOC database.
     * @param display The display.
     */
    public static void init(Display display) {
        Load load = new Load(display);
        load.start();
    }
    
    private static class Load implements SelectionListener,  Requester{
        private Display display;
        private Shell shell;
        private Button findButton;
        private Button showButton;
        private Button loadDBDButton;
        private Button loadDBButton;
        private Text fileNameText;
        private Text consoleText;
        private Button clearButton;
        
        private Load(Display display) {
            this.display = display;
        }
        
        private void start() {
            shell = new Shell(display);
            shell.setText("loadDatabase");
            GridLayout gridLayout = new GridLayout();
            gridLayout.numColumns = 1;
            shell.setLayout(gridLayout);
            Composite composite = new Composite(shell,SWT.BORDER);
            gridLayout = new GridLayout();
            gridLayout.numColumns = 1;
            composite.setLayout(gridLayout);
            GridData gridData = new GridData(GridData.FILL_BOTH);
            composite.setLayoutData(gridData);
            
            Composite fileComposite = new Composite(composite,SWT.BORDER);
            gridLayout = new GridLayout();
            gridLayout.numColumns = 5;
            fileComposite.setLayout(gridLayout);
            gridData = new GridData(GridData.FILL_HORIZONTAL);
            fileComposite.setLayoutData(gridData);
            findButton = new Button(fileComposite,SWT.PUSH);
            findButton.setText("find");
            findButton.addSelectionListener(this);
            showButton = new Button(fileComposite,SWT.PUSH);
            showButton.setText("show");
            showButton.addSelectionListener(this);
            loadDBDButton = new Button(fileComposite,SWT.PUSH);
            loadDBDButton.setText("loadDBD");
            loadDBDButton.addSelectionListener(this);
            loadDBButton = new Button(fileComposite,SWT.PUSH);
            loadDBButton.setText("loadDB");
            loadDBButton.addSelectionListener(this);
            fileNameText = new Text(fileComposite,SWT.BORDER);
            gridData = new GridData(GridData.FILL_HORIZONTAL);
            fileNameText.setLayoutData(gridData);
            Composite consoleComposite = new Composite(composite,SWT.BORDER);
            gridLayout = new GridLayout();
            gridLayout.numColumns = 1;
            consoleComposite.setLayout(gridLayout);
            gridData = new GridData(GridData.FILL_BOTH);
            consoleComposite.setLayoutData(gridData);
            clearButton = new Button(consoleComposite,SWT.PUSH);
            clearButton.setText("&Clear");
            clearButton.addSelectionListener(this);
            consoleText = new Text(consoleComposite,SWT.BORDER|SWT.H_SCROLL|SWT.V_SCROLL|SWT.READ_ONLY);
            GridData textGridData = new GridData(GridData.FILL_BOTH);
            consoleText.setLayoutData(textGridData);
            shell.open();
        }
        /* (non-Javadoc)
         * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
         */
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
        /* (non-Javadoc)
         * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
         */
        public void widgetSelected(SelectionEvent e) {
            if(e.getSource()==findButton) {
                FileDialog fd = new FileDialog(shell, SWT.OPEN);
                fd.setText("Open");
                String[] filterExt = { "*.xml"};
                fd.setFilterExtensions(filterExt);
                String fileName = fd.open();
                if(fileName!=null) {
                    fileNameText.setText(fileName);
                }
                return;
            }
            if(e.getSource()==showButton) {
                String fileName = fileNameText.getText();
                if(fileName==null || fileName.length()==0) {
                    consoleText.append("fileName not specified");
                    return;
                }
                try {
                    BufferedReader file = new BufferedReader(new FileReader(fileName));
                    String line;
                    while((line = file.readLine())!=null) {
                        consoleText.append(String.format("%s%n",line));
                    }
                    file.close();
                } catch (IOException ex) {
                    consoleText.append(String.format("%s%s",ex.getMessage()));
                }
                return;
            }
            if(e.getSource()==loadDBDButton) {
                String fileName = fileNameText.getText();
                if(fileName==null || fileName.length()==0) {
                    consoleText.append("fileName not specified");
                    return;
                }
                try {
                    DBD add = DBDFactory.create("add");
                    XMLToDBDFactory.convert(add,fileName,this);
                    add.mergeIntoMaster();
                } catch (RuntimeException ex) {
                    consoleText.append(String.format("%s%s",ex.getMessage()));
                }
                return;
            }
            if(e.getSource()==loadDBButton) {
                String fileName = fileNameText.getText();
                if(fileName==null || fileName.length()==0) {
                    consoleText.append("fileName not specified");
                    return;
                }
                try {
                    boolean initOK = IOCFactory.initDatabase(fileName,this);
                    if(!initOK) {
                        consoleText.append(String.format("IOCFactory.initDatabase failed%n"));
                    }
                } catch (RuntimeException ex) {
                    consoleText.append(String.format("%s%n",ex.getMessage()));
                }
                return;
            }
            if(e.getSource()==clearButton) {
                consoleText.selectAll();
                consoleText.clearSelection();
                consoleText.setText("");
                return;
            }
        }       
        /* (non-Javadoc)
         * @see org.epics.ioc.util.Requester#getRequesterName()
         */
        public String getRequesterName() {
            return "swtshell.loadDBD";
        }
        /* (non-Javadoc)
         * @see org.epics.ioc.util.Requester#message(java.lang.String, org.epics.ioc.util.MessageType)
         */
        public void message(String message, MessageType messageType) {
            consoleText.append(String.format("%s %s %n",messageType.toString(),message));
        }
    }
}