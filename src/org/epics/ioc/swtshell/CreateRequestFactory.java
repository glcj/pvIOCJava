/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.ioc.swtshell;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.epics.ca.channelAccess.client.Channel;
import org.epics.ca.channelAccess.client.ChannelAccess;
import org.epics.ca.channelAccess.client.GetFieldRequester;
import org.epics.ca.channelAccess.server.impl.ChannelAccessFactory;
import org.epics.pvData.factory.PVDataFactory;
import org.epics.pvData.misc.BitSet;
import org.epics.pvData.misc.Executor;
import org.epics.pvData.misc.ExecutorNode;
import org.epics.pvData.pv.Field;
import org.epics.pvData.pv.MessageType;
import org.epics.pvData.pv.PVDataCreate;
import org.epics.pvData.pv.PVField;
import org.epics.pvData.pv.PVString;
import org.epics.pvData.pv.PVStructure;
import org.epics.pvData.pv.ScalarType;
import org.epics.pvData.pv.Status;
import org.epics.pvData.pv.Structure;
import org.epics.pvData.pv.Type;

/**
 * Shell for creating a PVStructure for a pvRequest.
 * @author mrk
 *
 */
public class CreateRequestFactory {
    
    
    /**
     * Create a create request.
     * @param parent The parent shell.
     * @param channel The channel.
     * @param createRequestRequester The requester.
     * @return The CreateRequest interface.
     */
    public static CreateRequest create(Shell parent,Channel channel,CreateRequestRequester createRequestRequester) {
        return new CreateRequestImpl(parent,channel,createRequestRequester);
    }
    
    private static final ChannelAccess channelAccess = ChannelAccessFactory.getChannelAccess();
    private static final PVDataCreate pvDataCreate = PVDataFactory.getPVDataCreate();
    private static Executor executor = SwtshellFactory.getExecutor();
    
    private static class CreateRequestImpl extends Dialog
    implements CreateRequest,GetFieldRequester,SelectionListener,Runnable  {
        
        private CreateRequestImpl(Shell parent,Channel channel,CreateRequestRequester createRequestRequester) {
            super(parent,SWT.DIALOG_TRIM|SWT.NONE);
            this.createRequestRequester = createRequestRequester;
            this.parent = parent;
            this.channel = channel;
            display = parent.getDisplay();
        }
        
        private Shell parent = null;
        private Channel channel = null;
        private CreateRequestRequester createRequestRequester;
        private Display display = null;
        
        private ExecutorNode executorNode = null;
        private Shell shell = null;
        private Button shareDataButton;
        private boolean isShared = false;
        private Button showRequestButton;
        private boolean isShow = false;
        private Button createRequestButton;
        private Button requestButton;
        private Text requestText = null;
        private Structure channelStructure = null;
        private PVStructure pvRequest = null;
        
        private enum RunState {
            createDone,createRequest
        }
        private RunState runState = null;
        /* (non-Javadoc)
         * @see org.epics.ioc.swtshell.CreateRequest#create()
         */
        @Override
        public void create() {
            executorNode = executor.createNode(this);
            shell = new Shell(parent);
            shell.setText("createRequest");
            GridLayout gridLayout = new GridLayout();
            gridLayout.numColumns = 4;
            shell.setLayout(gridLayout);
            shareDataButton = new Button(shell,SWT.CHECK);
            shareDataButton.setText("shareData");
            shareDataButton.setSelection(false);           
            shareDataButton.setEnabled(true);
            shareDataButton.addSelectionListener(this);
            showRequestButton = new Button(shell,SWT.CHECK);
            showRequestButton.setText("showRequest");
            shareDataButton.setSelection(false);
            showRequestButton.addSelectionListener(this);               
            showRequestButton.setEnabled(true);
            createRequestButton = new Button(shell,SWT.PUSH);
            createRequestButton.setText("createRequest");
            createRequestButton.addSelectionListener(this);               
            createRequestButton.setEnabled(true);
            Composite requestComposite = new Composite(shell,SWT.BORDER);
            gridLayout = new GridLayout();
            gridLayout.numColumns = 2;
            requestComposite.setLayout(gridLayout);
            requestButton = new Button(requestComposite,SWT.PUSH);
            requestButton.setText("request");
            requestButton.addSelectionListener(this);               
            requestButton.setEnabled(true);
            requestText = new Text(requestComposite,SWT.BORDER);
            GridData gridData = new GridData(); 
            gridData.widthHint = 400;
            requestText.setLayoutData(gridData);
            requestText.setText("value,timeStamp,alarm");
            requestText.addSelectionListener(this);
            shell.pack();
            shell.open();
            while(!shell.isDisposed()) {
                if(!display.readAndDispatch()) {
                    display.sleep();
                }
            }
            shell.dispose();
        } 
        /* (non-Javadoc)
         * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
         */
        @Override
        public void widgetDefaultSelected(SelectionEvent arg0) {
            widgetSelected(arg0);
        }
        /* (non-Javadoc)
         * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
         */
        @Override
        public void widgetSelected(SelectionEvent arg0) {
            Object object = arg0.getSource(); 
            if(object==createRequestButton) {
                runState = RunState.createRequest;
                executor.execute(executorNode);
            } else if(object==showRequestButton) {
                isShow = showRequestButton.getSelection();
            } else if(object==shareDataButton) {
                isShared = shareDataButton.getSelection();
            } else if(object==requestButton) {
                try{
                    pvRequest = channelAccess.createRequest(channel.getChannelName(), requestText.getText());
                    createRequestRequester.request(pvRequest,isShared);
                    if(isShow) {
                        message(pvRequest.toString(),MessageType.info);
                    }
                    shell.close();
                } catch (IllegalArgumentException e) {
                    message("illegal request value",MessageType.error);
                }
            }
        }
        /* (non-Javadoc)
         * @see org.epics.ca.channelAccess.client.GetFieldRequester#getDone(Status,org.epics.pvData.pv.Field)
         */
        @Override
        public void getDone(Status status,Field field) {
            if (!status.isOK()) {
            	message(status.toString(), status.isSuccess() ? MessageType.warning : MessageType.error);
            	if (!status.isSuccess()) return;
            }
            if(field.getType()!=Type.structure) {
                message("CreateRequest: channel introspection did not return a Structure",MessageType.error);
            } else {
                this.channelStructure = (Structure)field;
                runState = RunState.createDone;
                display.asyncExec(this);
            }
        }
        /* (non-Javadoc)
         * @see org.epics.pvData.pv.Requester#getRequesterName()
         */
        @Override
        public String getRequesterName() {
            return createRequestRequester.getRequesterName();
        }
        /* (non-Javadoc)
         * @see org.epics.pvData.pv.Requester#message(java.lang.String, org.epics.pvData.pv.MessageType)
         */
        @Override
        public void message(String message, MessageType messageType) {
            createRequestRequester.message(message, messageType);
        }
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            switch(runState) {
            case createDone: { // must run as swt thread
                SelectSubSet selectSubSet = new SelectSubSet(shell);
                PVStructure pvStructure = pvDataCreate.createPVStructure(null, channelStructure);
                BitSet bitSet = selectSubSet.getSubSet(pvStructure,channel.getChannelName());
                pvRequest = pvDataCreate.createPVStructure(null,channel.getChannelName(), new Field[0]);
                createPVRequest(bitSet,pvStructure,pvRequest);
                if(isShow) {
                    message(pvRequest.toString(),MessageType.info);
                }
                createRequestRequester.request(pvRequest,isShared);
                shell.close();
                return;
            }
            case createRequest: { // must run as executor thread
                channel.getField(this, null);
                return;
            }
            }
        }
        
        private void createPVRequest(BitSet bitSet,PVStructure pvStructure,PVStructure pvRequest) {
            PVField[] pvFields = pvStructure.getPVFields();
            if(bitSet.get(pvStructure.getFieldOffset())) {
                for(int i=0; i<pvFields.length; i++) {
                    PVField pvField = pvFields[i];
                    PVString pvString = (PVString)pvDataCreate.createPVScalar(pvRequest, pvField.getField().getFieldName(),ScalarType.pvString);
                    pvString.put(pvField.getFullFieldName());
                    pvRequest.appendPVField(pvString);
                }
                return;
            }
            for(int i=0; i<pvFields.length; i++) {
                PVField pvField = pvFields[i];
                
                int offset = pvField.getFieldOffset();
                if(bitSet.get(offset)) {
                    PVString pvString = (PVString)pvDataCreate.createPVScalar(pvRequest, pvField.getField().getFieldName(),ScalarType.pvString);
                    pvString.put(pvField.getFullFieldName());
                    pvRequest.appendPVField(pvString);
                } else {
                    int numberFields = pvField.getNumberFields();
                    if(numberFields>1) {
                        int nextSet = bitSet.nextSetBit(offset);
                        if(nextSet>offset && nextSet<offset+numberFields) {
                            PVStructure temp = pvDataCreate.createPVStructure(pvRequest,pvField.getField().getFieldName(), new Field[0]);
                            createPVRequest(bitSet,(PVStructure)pvField,temp);
                            pvRequest.appendPVField(temp);
                        }
                    }
                }
            }
        }
        
        private static class SelectSubSet extends Dialog implements SelectionListener {
            
            public SelectSubSet(Shell parent) {
                super(parent,SWT.DIALOG_TRIM|SWT.NONE);
                this.parent = parent;
            }
            
            private Shell parent;
            private Shell shell;
            private Button doneButton;
            private Tree tree;
            private PVStructure pvStructure;
            private BitSet bitSet;
            
            private BitSet getSubSet(PVStructure pvStructure,String topName) {
                this.pvStructure = pvStructure;
                bitSet = new BitSet(pvStructure.getNumberFields());
                shell = new Shell(parent);
                shell.setText("getSubSet");
                GridLayout gridLayout = new GridLayout();
                gridLayout.numColumns = 1;
                shell.setLayout(gridLayout);
                Composite composite = new Composite(shell,SWT.BORDER);
                gridLayout = new GridLayout();
                gridLayout.numColumns = 1;
                composite.setLayout(gridLayout);
                GridData gridData = new GridData(GridData.FILL_BOTH);
                composite.setLayoutData(gridData);
                Composite modifyComposite = new Composite(composite,SWT.BORDER);
                gridLayout = new GridLayout();
                gridLayout.numColumns = 1;
                modifyComposite.setLayout(gridLayout);
                gridData = new GridData(GridData.FILL_HORIZONTAL);
                modifyComposite.setLayoutData(gridData);
                doneButton = new Button(modifyComposite,SWT.PUSH);
                doneButton.setText("Done");
                doneButton.addSelectionListener(this);
                tree = new Tree(composite,SWT.CHECK|SWT.BORDER);
                gridData = new GridData(GridData.FILL_BOTH);
                tree.setLayoutData(gridData);
                TreeItem treeItem = new TreeItem(tree,SWT.NONE);
                treeItem.setText(topName);
                initTreeItem(treeItem,pvStructure);
                shell.open();
                Display display = shell.getDisplay();
                while(!shell.isDisposed()) {
                    if(!display.readAndDispatch()) {
                        display.sleep();
                    }
                }
                shell.dispose();
                return bitSet;
            }
            
            /* (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
                widgetSelected(arg0);
            }
            /* (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                Object object = arg0.getSource();
               
                if(object==doneButton) {
                    bitSet.clear();
                    setBitSet(bitSet,tree.getItems()[0],pvStructure);
                    shell.close();
                    return;
                }
            }
            
            private void initTreeItem(TreeItem tree,PVField pv) {
                tree.setData(pv);
                if(!(pv.getField().getType()==Type.structure)) return;
                PVStructure pvStructure = (PVStructure)pv;
                PVField[] pvFields = pvStructure.getPVFields();
                for(PVField pvField : pvFields) {
                    Field field = pvField.getField();
                    TreeItem treeItem = new TreeItem(tree,SWT.NONE);
                    treeItem.setText(field.getFieldName());
                    initTreeItem(treeItem,pvField);
                }
            }
            
            private void setBitSet(BitSet bitSet,TreeItem tree,PVField pv) {
                if(tree.getChecked()) {
                    bitSet.set(pv.getFieldOffset());
                    return;
                }
                if(!(pv.getField().getType()==Type.structure)) return;
                PVStructure pvStructure = (PVStructure)pv;
                PVField[] pvFields = pvStructure.getPVFields();
                TreeItem[] treeItems = tree.getItems();
                for(int i=0; i<pvFields.length; i++) {
                    PVField pvField = pvFields[i];
                    TreeItem treeItem = treeItems[i];
                    setBitSet(bitSet,treeItem,pvField);
                }
            }
        }
    }
}
