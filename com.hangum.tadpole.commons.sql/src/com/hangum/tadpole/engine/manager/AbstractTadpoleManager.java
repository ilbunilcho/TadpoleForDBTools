/*******************************************************************************
 * Copyright (c) 2016 hangum.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     hangum - initial API and implementation
 ******************************************************************************/
package com.hangum.tadpole.engine.manager;

import java.sql.Connection;

import com.hangum.tadpole.engine.define.DBGroupDefine;
import com.hangum.tadpole.engine.query.dao.system.UserDBDAO;

public class AbstractTadpoleManager {
//	private static final Logger logger = Logger.getLogger(AbstractTadpoleManager.class);
	
	/**
	 * change schema
	 * 
	 * @param conn
	 * @param strSchema
	 */
	protected void changeSchema(UserDBDAO userDB, Connection conn) {
		if(userDB.getDBGroup() == DBGroupDefine.MYSQL_GROUP) {
//			PreparedStatement ps = conn.prepareStatement("use " + userDB.getSchema());
//			ps.get
		}
	}
	
	/**
	 * 
	 * @param userDB
	 * @param conn
	 */
	protected static void setConnectionInitialize(final UserDBDAO userDB, final Connection conn) {
//		String applicationName = SystemDefine.NAME;

	}
}
