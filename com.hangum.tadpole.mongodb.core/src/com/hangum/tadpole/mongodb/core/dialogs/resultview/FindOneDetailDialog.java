/*******************************************************************************
 * Copyright (c) 2013 Cho Hyun Jong.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Cho Hyun Jong - initial API and implementation
 ******************************************************************************/
package com.hangum.tadpole.mongodb.core.dialogs.resultview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import com.hangum.tadpole.dao.system.UserDBDAO;
import com.hangum.tadpole.mongodb.core.composite.result.TreeMongoContentProvider;
import com.hangum.tadpole.mongodb.core.composite.result.TreeMongoLabelProvider;
import com.hangum.tadpole.mongodb.core.dto.MongodbTreeViewDTO;
import com.hangum.tadpole.mongodb.core.utils.MongoDBTableColumn;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * 결과 한개를 디테일하게 보는 다이얼로그
 * 
 * @author hangum
 *
 */
public class FindOneDetailDialog extends Dialog {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(FindOneDetailDialog.class);

	private UserDBDAO userDB;
	private String collectionName;
	private DBObject dbResultObject;
	
	private Text textColName;
	private TreeViewer treeViewerMongo;
	private List<MongodbTreeViewDTO> listTrees;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public FindOneDetailDialog(Shell parentShell, UserDBDAO userDB, String collectionName, DBObject dbResultObject) {
		super(parentShell);
		setShellStyle(SWT.MAX | SWT.RESIZE | SWT.TITLE);
		
		this.userDB = userDB;
		this.collectionName = collectionName;
		this.dbResultObject = dbResultObject;				
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		newShell.setText("Collection Detail"); //$NON-NLS-1$
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.verticalSpacing = 2;
		gridLayout.horizontalSpacing = 2;
		gridLayout.marginHeight = 2;
		gridLayout.marginWidth = 2;
		
		Composite compositeHead = new Composite(container, SWT.NONE);
		GridLayout gl_compositeHead = new GridLayout(2, false);
		gl_compositeHead.verticalSpacing = 2;
		gl_compositeHead.horizontalSpacing = 2;
		gl_compositeHead.marginHeight = 2;
		gl_compositeHead.marginWidth = 2;
		compositeHead.setLayout(gl_compositeHead);
		compositeHead.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblName = new Label(compositeHead, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText("Name");
		
		textColName = new Text(compositeHead, SWT.BORDER);
		textColName.setEditable(false);
		textColName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Composite compositeBody = new Composite(container, SWT.NONE);
		compositeBody.setLayout(new GridLayout(1, false));
		compositeBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		treeViewerMongo = new TreeViewer(compositeBody, SWT.BORDER | SWT.VIRTUAL | SWT.FULL_SELECTION);		
		Tree tree = treeViewerMongo.getTree();
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		createTreeColumn();
		
		treeViewerMongo.setContentProvider(new TreeMongoContentProvider() );
		treeViewerMongo.setLabelProvider(new TreeMongoLabelProvider());
		
		initData();

		return container;
	}
	
	private void initData() {
		textColName.setText(collectionName);
		
		listTrees = new ArrayList<MongodbTreeViewDTO>();
		try {
			MongodbTreeViewDTO treeDto = new MongodbTreeViewDTO(dbResultObject, "", "", "Document");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			parserTreeObject(dbResultObject, treeDto, dbResultObject);
			listTrees.add(treeDto);
			
			treeViewerMongo.setInput(listTrees);			
			treeViewerMongo.expandToLevel(2);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * parser tree obejct
	 * 
	 * @param dbObject
	 */
	private void parserTreeObject(final DBObject rootDbObject, final MongodbTreeViewDTO treeDto, final DBObject dbObject) throws Exception {
		List<MongodbTreeViewDTO> listTrees = new ArrayList<MongodbTreeViewDTO>();
		
		Map<Integer, String> tmpMapColumns = MongoDBTableColumn.getTabelColumnView(dbObject);
		for(int i=0; i<tmpMapColumns.size(); i++)	{
			MongodbTreeViewDTO tmpTreeDto = new MongodbTreeViewDTO();
			tmpTreeDto.setDbObject(rootDbObject);
			
			String keyName = tmpMapColumns.get(i);			
			Object keyVal = dbObject.get(keyName);
			
			tmpTreeDto.setRealKey(keyName);
			// is sub document
			if( keyVal instanceof BasicDBObject ) {
				tmpTreeDto.setKey(tmpMapColumns.get(i) + " {..}"); //$NON-NLS-1$
				tmpTreeDto.setType("Document"); //$NON-NLS-1$
				
				parserTreeObject(rootDbObject, tmpTreeDto, (DBObject)keyVal);
			} else if(keyVal instanceof BasicDBList) {
				BasicDBList dbObjectList = (BasicDBList)keyVal;
				
				tmpTreeDto.setKey(tmpMapColumns.get(i) + " [" + dbObjectList.size() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
				tmpTreeDto.setType("Array"); //$NON-NLS-1$
				parseObjectArray(rootDbObject, tmpTreeDto, dbObjectList);
			} else {
				tmpTreeDto.setKey(tmpMapColumns.get(i));
				tmpTreeDto.setType(keyVal != null?keyVal.getClass().getName():"Unknow"); //$NON-NLS-1$
				
				if(keyVal == null) tmpTreeDto.setValue(""); //$NON-NLS-1$
				else tmpTreeDto.setValue(keyVal.toString());
			}
			
			// 컬럼의 데이터를 넣는다.
			listTrees.add(tmpTreeDto);
		}
		
		treeDto.setChildren(listTrees);
	}
	
	/**
	 * object array
	 * 
	 * @param treeDto
	 * @param dbObject
	 * @throws Exception
	 */
	private void parseObjectArray(final DBObject rootDbObject, final MongodbTreeViewDTO treeDto, final BasicDBList dbObjectList) throws Exception {
		List<MongodbTreeViewDTO> listTrees = new ArrayList<MongodbTreeViewDTO>();
		
		for(int i=0; i<dbObjectList.size(); i++) {
			MongodbTreeViewDTO mongodbDto = new MongodbTreeViewDTO();
			
			mongodbDto.setRealKey("" + i ); //$NON-NLS-1$
			mongodbDto.setKey("(" + i + ")"); //$NON-NLS-1$ //$NON-NLS-2$
			mongodbDto.setDbObject(rootDbObject);

			Object keyVal = dbObjectList.get(i);
			if( keyVal instanceof BasicDBObject ) {
				mongodbDto.setType("Document"); //$NON-NLS-1$
				
				parserTreeObject(rootDbObject, mongodbDto, (DBObject)keyVal);
			} else if(keyVal instanceof BasicDBList) {
				BasicDBList tmpDbObjectList = (BasicDBList)keyVal;
				
				mongodbDto.setType("Array"); //$NON-NLS-1$
				parseObjectArray(rootDbObject, mongodbDto, tmpDbObjectList);
			} else {
				mongodbDto.setType(keyVal != null?keyVal.getClass().getName():"Unknow"); //$NON-NLS-1$
				
				if(keyVal == null) mongodbDto.setValue(""); //$NON-NLS-1$
				else mongodbDto.setValue(keyVal.toString());
			}
			
			listTrees.add(mongodbDto);
		}
		
		treeDto.setChildren(listTrees);
	}
	
	/**
	 * treeview create
	 */
	private void createTreeColumn() {
		String[] columnName = {"Key", "Value", "Type"};  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		int[] columnSize = {140, 200, 140};
		
		try {
			// reset column 
			for(int i=0; i<columnName.length; i++) {
				final TreeViewerColumn tableColumn = new TreeViewerColumn(treeViewerMongo, SWT.LEFT);
				tableColumn.getColumn().setText( columnName[i] );
				tableColumn.getColumn().setWidth( columnSize[i] );
				tableColumn.getColumn().setResizable(true);
				tableColumn.getColumn().setMoveable(false);
//				if(isUserAction) {
//					if(i == 1) tableColumn.setEditingSupport(new TreeViewerEditingSupport(userDB, collectionName, treeViewerMongo));
//				}
			}	// end for
			
		} catch(Exception e) { 
			logger.error("MongoDB Tree view Editor", e); //$NON-NLS-1$
		}		
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "OK", true);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(521, 477);
	}

}
