// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor.dqrules;

import org.apache.log4j.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.action.actions.DefaultSaveAction;
import org.talend.dataprofiler.core.ui.editor.CommonFormEditor;
import org.talend.dataprofiler.core.ui.editor.TdEditorToolBar;
import org.talend.dataprofiler.core.ui.editor.parserrules.ParserRuleItemEditorInput;
import org.talend.dataquality.rules.DQRule;
import org.talend.dataquality.rules.ParserRule;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class DQRuleEditor extends CommonFormEditor {

    private DQRuleMasterDetailsPage masterPage;

    private ParserRuleMasterDetailsPage parserPage;

    private static final String ID = "DQRuleEditor.masterPage";//$NON-NLS-1$

    // MOD xqliu 2009-07-02 bug 7687
    private DefaultSaveAction saveAction;

    // ~

    protected void addPages() {
        IEditorInput editorInput = this.getEditorInput();
        try {

            if (editorInput instanceof ParserRuleItemEditorInput) {
                parserPage = new ParserRuleMasterDetailsPage(this, ID, "Parser Rule Settings"); //$NON-NLS-1$ 
                addPage(parserPage);
                setPartName(((ParserRuleMasterDetailsPage) parserPage).getIntactElemenetName()); //$NON-NLS-1$
            } else if (editorInput instanceof FileEditorInput) {
                DQRule findDQRule = null;
                FileEditorInput fileEditorInput = (FileEditorInput) editorInput;
                IFile file = fileEditorInput.getFile();
                String label = file.getFullPath().toString();
                if (FactoriesUtil.isDQRuleFile(file.getFileExtension())) {
                    findDQRule = DQRuleResourceFileHelper.getInstance().findDQRule(file);

                }
                if (findDQRule instanceof ParserRule) {
                    parserPage = new ParserRuleMasterDetailsPage(this, ID, "Parser Rule Settings"); //$NON-NLS-1$ 
                    addPage(parserPage);
                    setPartName(((ParserRuleMasterDetailsPage) parserPage).getIntactElemenetName()); //$NON-NLS-1$
                } else {
                    masterPage = new DQRuleMasterDetailsPage(this, ID,
                            DefaultMessagesImpl.getString("DQRuleEditor.dqRuleSettings")); //$NON-NLS-1$ 
                    addPage(masterPage);
                    setPartName(((DQRuleMasterDetailsPage) masterPage).getIntactElemenetName()); //$NON-NLS-1$
                }
            } else {
                masterPage = new DQRuleMasterDetailsPage(this, ID, DefaultMessagesImpl.getString("DQRuleEditor.dqRuleSettings")); //$NON-NLS-1$ 
                addPage(masterPage);
                setPartName(((DQRuleMasterDetailsPage) masterPage).getIntactElemenetName()); //$NON-NLS-1$
            }
            // MOD qiongli 2011-3-21,bug 19472.set method 'setPartName(...)' behind the method 'addPage(...)'

        } catch (PartInitException e) {
            ExceptionHandler.process(e, Level.ERROR);
        }

        // ADD xqliu 2009-07-02 bug 7687
        TdEditorToolBar toolbar = getToolBar();
        if (toolbar != null && masterPage != null) {
            saveAction = new DefaultSaveAction(this);
            toolbar.addActions(saveAction);
        }
        // ~
    }

    public void doSave(IProgressMonitor monitor) {
        if (masterPage != null) {
            if (masterPage.isDirty()) {
                masterPage.doSave(monitor);
                setPartName(masterPage.getIntactElemenetName()); //$NON-NLS-1$
            }
            setEditorObject(masterPage.getRuleRepNode());
        } else if (parserPage != null) {
            if (parserPage.isDirty()) {
                parserPage.doSave(monitor);
                setPartName(parserPage.getIntactElemenetName()); //$NON-NLS-1$
            }
            setEditorObject(parserPage.getRuleRepNode());
        }
        super.doSave(monitor);

    }

    protected void firePropertyChange(final int propertyId) {
        // ADD xqliu 2009-07-02 bug 7687
        setSaveActionButtonState(isDirty());
        // ~
        super.firePropertyChange(propertyId);
    }

    /**
     * Getter for masterPage.
     * 
     * @return the masterPage
     */
    public IFormPage getMasterPage() {
        return this.masterPage;
    }

    @Override
    protected void pageChange(int newPageIndex) {
        super.pageChange(newPageIndex);
        // ADD xqliu 2009-07-02 bug 7686
        if (masterPage != null) {
            setSaveActionButtonState(false);
        } else if (parserPage != null) {
            setSaveActionButtonState(false);
        }
    }

    /**
     * DOC xqliu 2009-07-02 bug 7687.
     * 
     * @param state
     */
    public void setSaveActionButtonState(boolean state) {
        if (saveAction != null) {
            saveAction.setEnabled(state);
        }
    }

    @Override
    public void setFocus() {
        super.setFocus();
        // don't invoke this method here, invoke it in IPartListener.partBroughtToTop()
        // WorkbenchUtils.autoChange2DataProfilerPerspective();
    }

    public ParserRuleMasterDetailsPage getParserPage() {
        return this.parserPage;
    }
}
