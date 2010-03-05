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
package org.talend.dataprofiler.core.ui.wizard.analysis.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.TableHelper;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataprofiler.core.ui.wizard.analysis.AnalysisDPSelectionPage;
import org.talend.dq.analysis.parameters.NamedColumnSetAnalysisParameter;
import orgomg.cwm.resource.relational.NamedColumnSet;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class TableAnalysisDPSelectionPage extends AnalysisDPSelectionPage {

    private static String newAnaStr = DefaultMessagesImpl.getString("TableAnalysisPageStep0.newAnalysis"); //$NON-NLS-1$

    private static String chooseConnStr = DefaultMessagesImpl.getString("TableAnalysisPageStep0.chooseTable"); //$NON-NLS-1$

    private static String connsStr = DefaultMessagesImpl.getString("TableAnalysisPageStep0.tables"); //$NON-NLS-1$

    public TableAnalysisDPSelectionPage() {
        super(newAnaStr, chooseConnStr, connsStr, new TableContentProvider(), true);
    }

    @Override
    protected void addListeners() {
        addListener(new IDoubleClickListener() {

            public void doubleClick(DoubleClickEvent event) {
                Object object = ((IStructuredSelection) event.getSelection()).getFirstElement();
                if (object instanceof NamedColumnSet) {
                    advanceToNextPageOrFinish();
                }
            }

        });
        addListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                TdDataProvider oldTdDataProvider = null;
                NamedColumnSetAnalysisParameter paraneter = (NamedColumnSetAnalysisParameter) getConnectionParams();
                List tempList = ((IStructuredSelection) event.getSelection()).toList();
                List<NamedColumnSet> setList = new ArrayList<NamedColumnSet>();
                for (Object object : tempList) {
                    if (object instanceof NamedColumnSet) {
                        NamedColumnSet set = (NamedColumnSet) object;
                        TdDataProvider tdProvider = DataProviderHelper.getTdDataProvider(TableHelper
                                .getParentCatalogOrSchema(set));
                        oldTdDataProvider = oldTdDataProvider == null ? tdProvider : oldTdDataProvider;
                        if (oldTdDataProvider != null && !oldTdDataProvider.equals(tdProvider)) {
                            MessageUI.openWarning(DefaultMessagesImpl
                                    .getString("TableAnalysisDPSelectionPage.TableSelectWarning")); //$NON-NLS-1$
                        } else if (tdProvider != null && paraneter != null) {
                            setList.add(set);
                            paraneter.setTdDataProvider(oldTdDataProvider);
                        }
                    }
                }
                if (setList.size() > 0 && paraneter != null) {
                    paraneter.setNamedColumnSets(setList.toArray(new NamedColumnSet[setList.size()]));
                    setPageComplete(true);
                } else {
                    setPageComplete(false);
                }
            }

        });

    }
}
