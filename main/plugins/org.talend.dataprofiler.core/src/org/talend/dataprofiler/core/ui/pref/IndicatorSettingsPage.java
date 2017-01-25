package org.talend.dataprofiler.core.ui.pref;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.talend.commons.emf.EMFUtil;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ui.filters.SelectAnalysisDialogNodeFilter;
import org.talend.dataprofiler.core.ui.filters.SpecialLimitFrequencyAnalysisFilter;
import org.talend.dataprofiler.core.ui.utils.AnalysisUtils;
import org.talend.dataprofiler.core.ui.utils.CheckValueUtils;
import org.talend.dataprofiler.core.ui.utils.UIMessages;
import org.talend.dataprofiler.core.ui.views.provider.DQRepositoryViewLabelProviderWithFilter;
import org.talend.dataprofiler.core.ui.views.provider.ResourceViewContentProvider;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.FrequencyIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.LowFrequencyIndicator;
import org.talend.dq.nodes.AnalysisRepNode;
import org.talend.dq.nodes.DQRepositoryNode;
import org.talend.resource.EResourceConstant;

public class IndicatorSettingsPage extends PreferencePage implements IWorkbenchPreferencePage {

    public static final String FREQUENCY_TABLE_RESULT_LIMIT_KEY = "org.talend.dataprofiler.core.ui.pref.FrequencyResultLimit";

    public static final String LOW_FREQUENCY_TABLE_RESULT_LIMIT_KEY = "org.talend.dataprofiler.core.ui.pref.LowFrequencyResultLimit";

    private int freResultLimit = 10;

    private int lowFreResultLimit = 10;

    public IndicatorSettingsPage() {
    }

    public IndicatorSettingsPage(String title) {
        super(title);
    }

    public IndicatorSettingsPage(String title, ImageDescriptor image) {
        super(title, image);
    }

    public void init(IWorkbench workbench) {

        int tempFreResultLimit = getPreferenceStore().getInt(FREQUENCY_TABLE_RESULT_LIMIT_KEY);
        if (tempFreResultLimit > 0) {
            this.freResultLimit = tempFreResultLimit;
        }
        int tempLowFreResultLimit = getPreferenceStore().getInt(LOW_FREQUENCY_TABLE_RESULT_LIMIT_KEY);
        if (tempLowFreResultLimit > 0) {
            this.lowFreResultLimit = tempLowFreResultLimit;
        }

    }

    @Override
    protected Control createContents(Composite parent) {

        Composite mainComposite = new Composite(parent, SWT.BORDER);
        GridLayout gridLayout = new GridLayout(1, false);
        mainComposite.setLayout(gridLayout);

        Group frequenceGroup = new Group(mainComposite, SWT.NONE);
        frequenceGroup.setText("Frequency Table"); //$NON-NLS-1$
        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gridLayout = new GridLayout(3, false);
        frequenceGroup.setLayoutData(data);
        frequenceGroup.setLayout(gridLayout);

        Label frequenceLabel = new Label(frequenceGroup, SWT.NONE);
        frequenceLabel.setText("Number of result shown"); //$NON-NLS-1$
        data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1);
        frequenceLabel.setLayoutData(data);

        final Text frequenceText = new Text(frequenceGroup, SWT.BORDER);
        frequenceText.setText("" + freResultLimit); //$NON-NLS-1$
        data = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        frequenceText.setLayoutData(data);
        frequenceText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (frequenceText.getText().equals(StringUtils.EMPTY)) {
                    IndicatorSettingsPage.this.setErrorMessage(UIMessages.MSG_EMPTY_FIELD);
                    IndicatorSettingsPage.this.setValid(false);
                    IndicatorSettingsPage.this.updateApplyButton();
                    return;
                }

                if (!CheckValueUtils.isNumberOfShownValue(frequenceText.getText())) {
                    IndicatorSettingsPage.this.setErrorMessage(UIMessages.MSG_ONLY_NUMBER);
                    IndicatorSettingsPage.this.setValid(false);
                    IndicatorSettingsPage.this.updateApplyButton();
                    return;
                }
                if (IndicatorSettingsPage.this.getErrorMessage() != null) {
                    IndicatorSettingsPage.this.setErrorMessage(null);
                    IndicatorSettingsPage.this.setValid(true);
                    IndicatorSettingsPage.this.updateApplyButton();
                }
                freResultLimit = Integer.valueOf(frequenceText.getText());

            }

        });

        Button frequenceButton = new Button(frequenceGroup, SWT.PUSH);
        frequenceButton.setText("Apple to others"); //$NON-NLS-1$
        data = new GridData(SWT.END, SWT.CENTER, true, false, 3, 1);
        frequenceButton.setLayoutData(data);
        frequenceButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {
                openAnalysesSelectionDialog(false);

            }
        });

        Group lowFrequenceGroup = new Group(mainComposite, SWT.NONE);
        lowFrequenceGroup.setText("Low frequency table"); //$NON-NLS-1$
        data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gridLayout = new GridLayout(2, false);
        lowFrequenceGroup.setLayoutData(data);
        lowFrequenceGroup.setLayout(gridLayout);

        Label lowFrequenceLabel = new Label(lowFrequenceGroup, SWT.NONE);
        lowFrequenceLabel.setText("Number of result shown"); //$NON-NLS-1$
        data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1);
        lowFrequenceLabel.setLayoutData(data);

        final Text lowFrequenceText = new Text(lowFrequenceGroup, SWT.BORDER);
        lowFrequenceText.setText("" + lowFreResultLimit); //$NON-NLS-1$
        data = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        lowFrequenceText.setLayoutData(data);

        lowFrequenceText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (lowFrequenceText.getText().equals(StringUtils.EMPTY)) {
                    IndicatorSettingsPage.this.setErrorMessage(UIMessages.MSG_EMPTY_FIELD);
                    IndicatorSettingsPage.this.setValid(false);
                    IndicatorSettingsPage.this.updateApplyButton();
                    return;
                }

                if (!CheckValueUtils.isNumberOfShownValue(lowFrequenceText.getText())) {
                    IndicatorSettingsPage.this.setErrorMessage(UIMessages.MSG_ONLY_NUMBER);
                    IndicatorSettingsPage.this.setValid(false);
                    IndicatorSettingsPage.this.updateApplyButton();
                    return;
                }
                if (IndicatorSettingsPage.this.getErrorMessage() != null) {
                    IndicatorSettingsPage.this.setErrorMessage(null);
                    IndicatorSettingsPage.this.setValid(true);
                    IndicatorSettingsPage.this.updateApplyButton();
                }
                lowFreResultLimit = Integer.valueOf(lowFrequenceText.getText());

            }

        });

        Button lowFrequenceButton = new Button(lowFrequenceGroup, SWT.PUSH);
        lowFrequenceButton.setText("Apple to others"); //$NON-NLS-1$
        data = new GridData(SWT.END, SWT.CENTER, true, false, 2, 1);
        lowFrequenceButton.setLayoutData(data);
        lowFrequenceButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {
                openAnalysesSelectionDialog(true);

            }
        });
        return mainComposite;

    }

    /**
     * DOC qzhang Comment method "openAnalysesSelectionDialog".
     */
    protected void openAnalysesSelectionDialog(boolean isLowCase) {
        SpecialLimitFrequencyAnalysisFilter limitFilter = new SpecialLimitFrequencyAnalysisFilter(isLowCase ? 0 : freResultLimit,
                isLowCase ? lowFreResultLimit : 0);
        CheckedTreeSelectionDialog checkedTreeSelectionDialog = new CheckedTreeSelectionDialog(this.getShell(),
                new DQRepositoryViewLabelProviderWithFilter(limitFilter), new ResourceViewContentProvider());
        checkedTreeSelectionDialog.setContainerMode(true);
        checkedTreeSelectionDialog.addFilter(new SelectAnalysisDialogNodeFilter());
        checkedTreeSelectionDialog.addFilter(limitFilter);
        DQRepositoryNode analysisSelectDialogInputData = AnalysisUtils
                .getAnalysisSelectDialogInputData(EResourceConstant.ANALYSIS);
        checkedTreeSelectionDialog.setInput(analysisSelectDialogInputData);
        if (checkedTreeSelectionDialog.open() == Dialog.OK) {
            Object[] result = checkedTreeSelectionDialog.getResult();
            for (Object obj : result) {
                if (obj instanceof AnalysisRepNode) {
                    AnalysisRepNode anaNode = (AnalysisRepNode) obj;
                    modifyAndSaveLimit(anaNode, isLowCase);

                }
            }

        }
    }

    /**
     * DOC zshen Comment method "modifyAndSaveLimit".
     * 
     * @param anaNode
     * @param isLowCase
     */
    private void modifyAndSaveLimit(AnalysisRepNode anaNode, boolean isLowCase) {
        Analysis analysis = anaNode.getAnalysis();
        for (Indicator indicator : analysis.getResults().getIndicators()) {
            if (isLowCase) {
                modifyLowFre(indicator);
            } else {
                // too many frequency type
                modifyFre(indicator);
            }
        }
        Resource resource = analysis.eResource();
        EMFUtil.saveSingleResource(resource);

    }

    /**
     * DOC zshen Comment method "modifyFre".
     * 
     * @param indicator
     */
    private void modifyFre(Indicator indicator) {
        if (indicator instanceof FrequencyIndicator && !(indicator instanceof LowFrequencyIndicator)) {
            indicator.getParameters().setTopN(freResultLimit);
        }
    }

    /**
     * DOC zshen Comment method "modifyLowFre".
     * 
     * @param indicator
     */
    private void modifyLowFre(Indicator indicator) {
        if (indicator instanceof LowFrequencyIndicator) {
            indicator.getParameters().setTopN(lowFreResultLimit);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.preference.PreferencePage#performOk()
     */
    @Override
    public boolean performOk() {
        this.getPreferenceStore().setValue(FREQUENCY_TABLE_RESULT_LIMIT_KEY, freResultLimit);
        this.getPreferenceStore().setValue(LOW_FREQUENCY_TABLE_RESULT_LIMIT_KEY, lowFreResultLimit);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
     */
    @Override
    protected IPreferenceStore doGetPreferenceStore() {
        return CorePlugin.getDefault().getPreferenceStore();
    }

}
