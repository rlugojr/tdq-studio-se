/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.util.Date;

import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.indicators.IndicatorValueType;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.MaxValueIndicator;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Max Value Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class MaxValueIndicatorImpl extends ValueIndicatorImpl implements MaxValueIndicator {

    boolean isDateType = false;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected MaxValueIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.MAX_VALUE_INDICATOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.ValueIndicatorImpl#getValueType()
     */
    @Override
    public IndicatorValueType getValueType() {
        // MOD scorreia handle date: bug 5938
        if (isDateValue()) {
            return IndicatorValueType.DATE_VALUE;
        }

        return IndicatorValueType.REAL_VALUE;
    }

    @Override
    public boolean handle(Object data) {
        boolean ok = super.handle(data);
        if (data == null) {
            return ok;
        }
        if (isGreater(data) || null == this.value) {
            this.value = String.valueOf(data);
            if (isDateType) {
                objValue = data;
            }
        }
        return ok;
    }

    /**
     * DOC scorreia Comment method "isGreater".
     * 
     * @param data
     * @return
     */
    private boolean isGreater(Object data) {
        // MOD xqliu 2009-06-29 bug 7068
        try {
            // MOD qiongli 2011-11-22 TDQ-4033,compare the date type
            if (isDateType) {
                if (data == null) {
                    return false;
                }
                if (objValue == null) {
                    objValue = data;
                    this.value = String.valueOf(data);
                    return false;
                }
                Date thisDate = (Date) objValue;
                Date dataDate = (Date) data;
                if (dataDate.compareTo(thisDate) > 0) {
                    return true;
                }
                return false;
            }// ~
            double thisValue = Double.valueOf(this.value);
            double dataValue = Double.valueOf(data.toString());
            return thisValue < dataValue;
        } catch (Exception e) {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#prepare()
     */
    @Override
    public boolean prepare() {
        this.isDateType = isDateValue();
        return super.prepare();
    }

} // MaxValueIndicatorImpl
