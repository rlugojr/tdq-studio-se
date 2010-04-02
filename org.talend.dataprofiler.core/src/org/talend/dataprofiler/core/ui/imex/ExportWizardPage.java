// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.imex;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.talend.cwm.helper.ModelElementHelper;
import org.talend.dq.factory.ModelElementFileFactory;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * This class defines the UI for the export feature in TOP and TQ for the Data profile perspective.
 */
public class ExportWizardPage extends WizardPage {

    private static final int EXPAND_LEVEL = 3;

    private CheckboxTreeViewer repositoryTree;

    private Button dirBTN, archBTN;

    private Button browseDirBTN, browseArchBTN;

    private Text dirTxt, archTxt;

    private String specifiedPath;

    private static final String[] FILE_EXPORT_MASK = { "*.zip;*.tar;*.tar.gz", "*.*" }; //$NON-NLS-1$//$NON-NLS-2$

    public ExportWizardPage(String specifiedPath) {
        super(Messages.getString("ExportWizardPage.2")); //$NON-NLS-1$
        setMessage(Messages.getString("ExportWizardPage.3")); //$NON-NLS-1$
        this.specifiedPath = specifiedPath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite top = new Composite(parent, SWT.NONE);
        top.setLayout(new GridLayout());
        top.setLayoutData(new GridData(GridData.FILL_BOTH));

        createSelectComposite(top);

        // createOptionComposite(top);

        createRepositoryTree(top);

        addListeners();

        initControlState();

        temporaryDisableTreeSelection();

        setControl(top);
    }

    /**
     * This method is created right before 4.0 release to avoid exporting a non consistent repository. this should be
     * removed when implementing consistency check on the export. This will disable selection on the reposiroty tree and
     * check all the items to force complete selection and avoid partial unchecked selection.
     */
    private void temporaryDisableTreeSelection() {
        TreeItem topItem = repositoryTree.getTree().getTopItem();
        if (topItem != null) { // check all items
            repositoryTree.setSubtreeChecked(topItem.getData(), true);
        } // else no item to export so no need to check anything
        repositoryTree.getTree().setEnabled(false);
    }

    /**
     * DOC bZhou Comment method "initControlState".
     */
    protected void initControlState() {
        setArchState(false);
        setPageComplete(false);
    }

    /**
     * DOC bZhou Comment method "setDirState".
     * 
     * @param state
     */
    protected void setDirState(boolean state) {
        dirTxt.setEnabled(state);
        browseDirBTN.setEnabled(state);
    }

    /**
     * DOC bZhou Comment method "isDirState".
     * 
     * @return
     */
    public boolean isDirState() {
        return dirBTN.getSelection();
    }

    /**
     * DOC bZhou Comment method "setArchState".
     * 
     * @param state
     */
    protected void setArchState(boolean state) {
        archTxt.setEnabled(state);
        browseArchBTN.setEnabled(state);
    }

    /**
     * DOC bZhou Comment method "isArchState".
     * 
     * @return
     */
    public boolean isArchState() {
        return archBTN.getSelection();
    }

    /**
     * DOC bZhou Comment method "addListeners".
     */
    private void addListeners() {

        dirBTN.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setDirState(dirBTN.getSelection());
            }
        });

        archBTN.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setArchState(archBTN.getSelection());
            }
        });

        browseDirBTN.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String result = openDirectoryDialog();
                if (result != null) {
                    dirTxt.setText(result);
                }
            }

            private String openDirectoryDialog() {
                DirectoryDialog dialog = new DirectoryDialog(Display.getDefault().getActiveShell());
                if (dirTxt.getText() != null) {
                    dialog.setFilterPath(dirTxt.getText());
                }

                return dialog.open();
            }
        });

        browseArchBTN.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String result = openFileDialog();
                if (result != null) {
                    archTxt.setText(result);
                }
            }

            private String openFileDialog() {
                FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell());
                dialog.setFilterExtensions(FILE_EXPORT_MASK);
                if (archTxt.getText() != null) {
                    dialog.setFileName(archTxt.getText());
                }

                return dialog.open();
            }
        });

        repositoryTree.getTree().addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                TreeItem item = (TreeItem) e.item;
                File sfile = (File) item.getData();
                IFile sIFile = file2IFile(sfile);
                if (sIFile != null) {
                    File[] dependencies = computeDependencies(sIFile);

                    for (File file : dependencies) {
                        repositoryTree.setChecked(file, item.getChecked());
                    }

                    // repositoryTree.setExpandedElements(repositoryTree.getCheckedElements());
                    repositoryTree.refresh();
                }
            }
        });
        dirTxt.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                checkForErrors();
                updatePageStatus();
            }
        });
    }

    /**
     * this check that the folder entered in the target export location exist otherwhise set an erro message and disable
     * export.
     */
    protected void checkForErrors() {
        if (!new File(dirTxt.getText()).exists()) {
            setErrorMessage(Messages.getString("ExportWizardPage.4")); //$NON-NLS-1$
        } else {
            setErrorMessage(null);
        }
    }

    /**
     * update the page state that is the finish button enable state according to the error message being present or not.
     */
    protected void updatePageStatus() {
        setPageComplete(getErrorMessage() == null);
    }

    /**
     * DOC bZhou Comment method "computeDependencies".
     * 
     * @param elements
     * @return
     */
    protected File[] computeDependencies(IFile... elements) {
        List<ModelElement> dependencyElements = new ArrayList<ModelElement>();
        List<File> dependencyFiles = new ArrayList<File>();

        ModelElement[] modelElements = ModelElementFileFactory.getModelElements(elements);
        for (ModelElement melement : modelElements) {
            ModelElementHelper.iterateClientDependencies(melement, dependencyElements);
        }

        for (ModelElement element : dependencyElements) {
            ResourceFileMap fileMap = ModelElementFileFactory.getResourceFileMap(element);
            IFile file = fileMap.findCorrespondingFile(element);
            if (file != null && file.exists()) {
                dependencyFiles.add(file.getLocation().toFile());
            }
        }
        return dependencyFiles.toArray(new File[dependencyFiles.size()]);
    }

    /**
     * DOC bZhou Comment method "createOptionComposite".
     * 
     * @param top
     */
    // private void createOptionComposite(Composite top) {
    // Group optionGroup = new Group(top, SWT.NONE);
    // optionGroup.setLayout(new RowLayout());
    // optionGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    //        optionGroup.setText(Messages.getString("ExportWizardPage.5")); //$NON-NLS-1$
    // }

    /**
     * DOC bZhou Comment method "createRepositoryTree".
     * 
     * @param top
     */
    private void createRepositoryTree(Composite top) {
        repositoryTree = new ContainerCheckedTreeViewer(top);
        repositoryTree.setContentProvider(new FileTreeContentProvider());
        repositoryTree.setLabelProvider(new FileTreeLabelProvider());
        repositoryTree.setInput(computInput());
        // repositoryTree.expandToLevel(EXPAND_LEVEL);
        repositoryTree.expandAll();

        GridDataFactory.fillDefaults().grab(true, true).applyTo(repositoryTree.getTree());
    }

    /**
     * DOC bZhou Comment method "computInput".
     * 
     * @return
     */
    private Object computInput() {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        return specifiedPath == null ? workspace.getRoot().getLocation().toFile() : workspace.getRoot().getFolder(
                new Path(specifiedPath)).getLocation().toFile();
    }

    /**
     * DOC bZhou Comment method "createSelectComposite".
     * 
     * @param top
     */
    private void createSelectComposite(Composite top) {
        Composite selectComp = new Composite(top, SWT.NONE);
        selectComp.setLayout(new GridLayout(3, false));
        selectComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        dirBTN = new Button(selectComp, SWT.RADIO);
        dirBTN.setText(Messages.getString("ExportWizardPage.6")); //$NON-NLS-1$
        setButtonLayoutData(dirBTN);

        dirTxt = new Text(selectComp, SWT.BORDER);
        dirTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        browseDirBTN = new Button(selectComp, SWT.PUSH);
        browseDirBTN.setText(Messages.getString("ExportWizardPage.7")); //$NON-NLS-1$
        setButtonLayoutData(browseDirBTN);

        archBTN = new Button(selectComp, SWT.RADIO);
        archBTN.setText(Messages.getString("ExportWizardPage.8")); //$NON-NLS-1$
        archBTN.setEnabled(false); // TODO make it enable after implemence.
        setButtonLayoutData(archBTN);

        archTxt = new Text(selectComp, SWT.BORDER);
        archTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        browseArchBTN = new Button(selectComp, SWT.PUSH);
        browseArchBTN.setText(Messages.getString("ExportWizardPage.9")); //$NON-NLS-1$
        setButtonLayoutData(browseArchBTN);
    }

    /**
     * DOC bZhou Comment method "getElements".
     * 
     * @return
     */
    public File[] getElements() {
        Object[] checkedElements = repositoryTree.getCheckedElements();

        List<File> files = new ArrayList<File>();
        if (checkedElements != null) {
            for (Object obj : checkedElements) {
                if (obj instanceof File) {
                    File file = (File) obj;
                    if (file.isFile()) {
                        files.add(file);
                    }
                }
            }
        }
        return files.toArray(new File[files.size()]);
    }

    /**
     * DOC bZhou Comment method "file2IFile".
     * 
     * @param file
     * @return
     */
    private IFile file2IFile(File file) {
        if (file.isFile()) {
            IPath path = new Path(file.getAbsolutePath());
            path = path.makeRelativeTo(ResourceManager.getRootProject().getLocation());
            return ResourceManager.getRootProject().getFile(path);
        }

        return null;
    }

    /**
     * DOC bZhou Comment method "getFilePath".
     * 
     * @return
     */
    public String getFilePath() {
        if (dirBTN.getSelection()) {
            return dirTxt.getText();
        } else {
            return archTxt.getText();
        }
    }
}
