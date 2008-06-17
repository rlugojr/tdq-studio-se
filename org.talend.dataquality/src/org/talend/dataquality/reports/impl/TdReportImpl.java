/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.reports.impl;

import java.util.Collection;

import java.util.Date;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.reports.AnalysisMap;
import org.talend.dataquality.reports.PresentationParameter;
import org.talend.dataquality.reports.ReportsPackage;
import org.talend.dataquality.reports.TdReport;
import orgomg.cwmx.analysis.informationreporting.impl.ReportImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Td Report</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.reports.impl.TdReportImpl#getPresentationParams <em>Presentation Params</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.impl.TdReportImpl#getCreationDate <em>Creation Date</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.impl.TdReportImpl#getLastExecutionDate <em>Last Execution Date</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.impl.TdReportImpl#getAnalysisMap <em>Analysis Map</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TdReportImpl extends ReportImpl implements TdReport {

    /**
     * The cached value of the '{@link #getPresentationParams() <em>Presentation Params</em>}' containment reference list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getPresentationParams()
     * @generated
     * @ordered
     */
    protected EList<PresentationParameter> presentationParams;

    /**
     * The default value of the '{@link #getCreationDate() <em>Creation Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCreationDate()
     * @generated
     * @ordered
     */
    protected static final Date CREATION_DATE_EDEFAULT = null;
    /**
     * The cached value of the '{@link #getCreationDate() <em>Creation Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCreationDate()
     * @generated
     * @ordered
     */
    protected Date creationDate = CREATION_DATE_EDEFAULT;

    /**
     * The default value of the '{@link #getLastExecutionDate() <em>Last Execution Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLastExecutionDate()
     * @generated
     * @ordered
     */
    protected static final Date LAST_EXECUTION_DATE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getLastExecutionDate() <em>Last Execution Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLastExecutionDate()
     * @generated
     * @ordered
     */
    protected Date lastExecutionDate = LAST_EXECUTION_DATE_EDEFAULT;

    /**
     * The cached value of the '{@link #getAnalysisMap() <em>Analysis Map</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAnalysisMap()
     * @generated
     * @ordered
     */
    protected EList<AnalysisMap> analysisMap;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected TdReportImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ReportsPackage.Literals.TD_REPORT;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EList<PresentationParameter> getPresentationParams() {
        if (presentationParams == null) {
            presentationParams = new EObjectContainmentEList<PresentationParameter>(PresentationParameter.class, this, ReportsPackage.TD_REPORT__PRESENTATION_PARAMS);
        }
        return presentationParams;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCreationDate(Date newCreationDate) {
        Date oldCreationDate = creationDate;
        creationDate = newCreationDate;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ReportsPackage.TD_REPORT__CREATION_DATE, oldCreationDate, creationDate));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Date getLastExecutionDate() {
        return lastExecutionDate;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setLastExecutionDate(Date newLastExecutionDate) {
        Date oldLastExecutionDate = lastExecutionDate;
        lastExecutionDate = newLastExecutionDate;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ReportsPackage.TD_REPORT__LAST_EXECUTION_DATE, oldLastExecutionDate, lastExecutionDate));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<AnalysisMap> getAnalysisMap() {
        if (analysisMap == null) {
            analysisMap = new EObjectContainmentEList<AnalysisMap>(AnalysisMap.class, this, ReportsPackage.TD_REPORT__ANALYSIS_MAP);
        }
        return analysisMap;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public boolean addAnalysis(Analysis analysis) {
        return this.getComponent().add(analysis);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case ReportsPackage.TD_REPORT__PRESENTATION_PARAMS:
                return ((InternalEList<?>)getPresentationParams()).basicRemove(otherEnd, msgs);
            case ReportsPackage.TD_REPORT__ANALYSIS_MAP:
                return ((InternalEList<?>)getAnalysisMap()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case ReportsPackage.TD_REPORT__PRESENTATION_PARAMS:
                return getPresentationParams();
            case ReportsPackage.TD_REPORT__CREATION_DATE:
                return getCreationDate();
            case ReportsPackage.TD_REPORT__LAST_EXECUTION_DATE:
                return getLastExecutionDate();
            case ReportsPackage.TD_REPORT__ANALYSIS_MAP:
                return getAnalysisMap();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case ReportsPackage.TD_REPORT__PRESENTATION_PARAMS:
                getPresentationParams().clear();
                getPresentationParams().addAll((Collection<? extends PresentationParameter>)newValue);
                return;
            case ReportsPackage.TD_REPORT__CREATION_DATE:
                setCreationDate((Date)newValue);
                return;
            case ReportsPackage.TD_REPORT__LAST_EXECUTION_DATE:
                setLastExecutionDate((Date)newValue);
                return;
            case ReportsPackage.TD_REPORT__ANALYSIS_MAP:
                getAnalysisMap().clear();
                getAnalysisMap().addAll((Collection<? extends AnalysisMap>)newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case ReportsPackage.TD_REPORT__PRESENTATION_PARAMS:
                getPresentationParams().clear();
                return;
            case ReportsPackage.TD_REPORT__CREATION_DATE:
                setCreationDate(CREATION_DATE_EDEFAULT);
                return;
            case ReportsPackage.TD_REPORT__LAST_EXECUTION_DATE:
                setLastExecutionDate(LAST_EXECUTION_DATE_EDEFAULT);
                return;
            case ReportsPackage.TD_REPORT__ANALYSIS_MAP:
                getAnalysisMap().clear();
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case ReportsPackage.TD_REPORT__PRESENTATION_PARAMS:
                return presentationParams != null && !presentationParams.isEmpty();
            case ReportsPackage.TD_REPORT__CREATION_DATE:
                return CREATION_DATE_EDEFAULT == null ? creationDate != null : !CREATION_DATE_EDEFAULT.equals(creationDate);
            case ReportsPackage.TD_REPORT__LAST_EXECUTION_DATE:
                return LAST_EXECUTION_DATE_EDEFAULT == null ? lastExecutionDate != null : !LAST_EXECUTION_DATE_EDEFAULT.equals(lastExecutionDate);
            case ReportsPackage.TD_REPORT__ANALYSIS_MAP:
                return analysisMap != null && !analysisMap.isEmpty();
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (creationDate: ");
        result.append(creationDate);
        result.append(", lastExecutionDate: ");
        result.append(lastExecutionDate);
        result.append(')');
        return result.toString();
    }

} // TdReportImpl
