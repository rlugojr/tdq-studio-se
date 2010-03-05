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
package org.talend.dataprofiler.core.ui.editor.analysis;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.IRuningStatusListener;
import org.talend.dataprofiler.core.ui.editor.AbstractFormPage;
import org.talend.dataprofiler.core.ui.editor.CommonFormEditor;
import org.talend.dq.analysis.AnalysisHandler;

/**
 * DOC rli class global comment. Detailled comment
 */
public abstract class AbstractAnalysisResultPage extends AbstractFormPage implements IRuningStatusListener {

    protected ScrolledForm form;

    protected Composite topComposite;

    protected Composite summaryComp;

    protected Section summarySection = null;

    protected CommonFormEditor currentEditor;

    public AbstractAnalysisResultPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
        currentEditor = (CommonFormEditor) editor;
    }

    protected void createFormContent(IManagedForm managedForm) {
        this.form = managedForm.getForm();
        this.form.setText(DefaultMessagesImpl.getString("AbstractAnalysisResultPage.analysisResult")); //$NON-NLS-1$
        topComposite = form.getBody();
        topComposite.setLayout(new GridLayout());
        summaryComp = toolkit.createComposite(topComposite);
        summaryComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        summaryComp.setLayout(new GridLayout());
        createSummarySection(form, summaryComp, getAnalysisHandler());
    }

    protected abstract AnalysisHandler getAnalysisHandler();

    protected void createSummarySection(ScrolledForm form, Composite parent, AnalysisHandler analysisHandler) {
        summarySection = createSection(form, parent,
                DefaultMessagesImpl.getString("AbstractAnalysisResultPage.analysisSummary"), null); //$NON-NLS-1$
        Composite sectionClient = toolkit.createComposite(summarySection);
        sectionClient.setLayout(new GridLayout(2, false));
        sectionClient.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Composite databaseComp = toolkit.createComposite(sectionClient);
        databaseComp.setLayout(new GridLayout(2, false));
        GridData databaseCompData = new GridData(GridData.FILL_HORIZONTAL);
        databaseCompData.verticalAlignment = GridData.BEGINNING;
        databaseComp.setLayoutData(databaseCompData);

        toolkit.createLabel(databaseComp, DefaultMessagesImpl.getString("AbstractAnalysisResultPage.connection")); //$NON-NLS-1$
        toolkit.createLabel(databaseComp, analysisHandler.getConnectionName());
        if (analysisHandler.isCatalogExisting()) {
            toolkit.createLabel(databaseComp, DefaultMessagesImpl.getString("AbstractAnalysisResultPage.catalog")); //$NON-NLS-1$
            toolkit.createLabel(databaseComp, analysisHandler.getCatalogNames());
        }

        if (analysisHandler.isSchemaExisting()) {
            toolkit.createLabel(databaseComp, DefaultMessagesImpl.getString("AbstractAnalysisResultPage.schema")); //$NON-NLS-1$
            toolkit.createLabel(databaseComp, analysisHandler.getSchemaNames());
        }

        toolkit.createLabel(databaseComp, DefaultMessagesImpl.getString("AbstractAnalysisResultPage.table")); //$NON-NLS-1$
        toolkit.createLabel(databaseComp, analysisHandler.getTableNames());

        // bug 10541 fix by zshen,Change some character set to be proper to add view in the table anasys
        toolkit.createLabel(databaseComp, DefaultMessagesImpl.getString("AbstractAnalysisResultPage.view"));
        toolkit.createLabel(databaseComp, analysisHandler.getViewNames());

        Composite executionComp = toolkit.createComposite(sectionClient);
        executionComp.setLayout(new GridLayout(2, false));
        GridData executionCompData = new GridData(GridData.FILL_HORIZONTAL);
        executionCompData.verticalAlignment = GridData.BEGINNING;
        executionComp.setLayoutData(executionCompData);
        toolkit.createLabel(executionComp, DefaultMessagesImpl.getString("AbstractAnalysisResultPage.executionDate")); //$NON-NLS-1$
        toolkit.createLabel(executionComp, analysisHandler.getExecuteData());
        toolkit.createLabel(executionComp, DefaultMessagesImpl.getString("AbstractAnalysisResultPage.executionDuration")); //$NON-NLS-1$
        toolkit.createLabel(executionComp, analysisHandler.getExecuteDuration());
        toolkit.createLabel(executionComp, DefaultMessagesImpl.getString("AbstractAnalysisResultPage.executionStatus")); //$NON-NLS-1$
        if (analysisHandler.getResultMetadata().isLastRunOk()) {
            toolkit.createLabel(executionComp, DefaultMessagesImpl.getString("AbstractAnalysisResultPage.success")); //$NON-NLS-1$
        } else {
            toolkit
                    .createLabel(
                            executionComp,
                            DefaultMessagesImpl.getString("AbstractAnalysisResultPage.failure") + analysisHandler.getResultMetadata().getMessage()).setForeground( //$NON-NLS-1$
                            Display.getDefault().getSystemColor(SWT.COLOR_RED));
        }

        toolkit.createLabel(executionComp, DefaultMessagesImpl.getString("AbstractAnalysisResultPage.numberOfExecution")); //$NON-NLS-1$
        toolkit.createLabel(executionComp, analysisHandler.getExecuteNumber());
        toolkit.createLabel(executionComp, DefaultMessagesImpl.getString("AbstractAnalysisResultPage.lastSucessfulExecution")); //$NON-NLS-1$
        toolkit.createLabel(executionComp, analysisHandler.getLastExecutionNumberOk());

        summarySection.setClient(sectionClient);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.IRuningStatusListener#fireRuningItemChanged(boolean)
     */
    public void fireRuningItemChanged(boolean status) {
        IEditorPart editor = CorePlugin.getDefault().getCurrentActiveEditor();
        if (editor instanceof AnalysisEditor && status) {
            refresh(((AnalysisEditor) editor).getMasterPage());
        }
    }

    public abstract void refresh(AbstractAnalysisMetadataPage masterPage);

    protected abstract void createResultSection(Composite parent);

}
