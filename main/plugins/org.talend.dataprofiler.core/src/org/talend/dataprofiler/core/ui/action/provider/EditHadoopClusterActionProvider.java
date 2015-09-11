// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.action.provider;

import org.eclipse.jface.action.IMenuManager;
import org.talend.dataprofiler.core.ui.action.actions.EditHDFSConnectionAction;
import org.talend.dataprofiler.core.ui.action.actions.EditHadoopClusterAction;
import org.talend.dq.nodes.hadoopcluster.HDFSOfHCConnectionNode;
import org.talend.dq.nodes.hadoopcluster.HadoopClusterConnectionRepNode;
import org.talend.repository.model.IRepositoryNode;

/**
 * created by yyin on 2015年5月14日 Detailled comment
 *
 */
public class EditHadoopClusterActionProvider extends AbstractCommonActionProvider {

    /*
     * (non-Jsdoc)
     * 
     * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
     */
    @Override
    public void fillContextMenu(IMenuManager menu) {
        if (!isShowMenu()) {
            return;
        }

        super.fillContextMenu(menu);

        IRepositoryNode node = getFistContextNode();

        if (node != null) {
            if (node instanceof HadoopClusterConnectionRepNode) {
                menu.add(new EditHadoopClusterAction(node));
            } else if (node instanceof HDFSOfHCConnectionNode) {
                menu.add(new EditHDFSConnectionAction(node));
            }
        }
    }

}