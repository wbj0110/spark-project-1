package com.ibeifeng.sparkproject.dao.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ibeifeng.sparkproject.dao.IAdUserClickCountDAO;
import com.ibeifeng.sparkproject.domain.AdUserClickCount;
import com.ibeifeng.sparkproject.jdbc.JDBCHelper;
import com.ibeifeng.sparkproject.model.AdUserClickCountQueryResult;

/**
 * 用户广告点击量DAO实现类
 * @author Administrator
 *
 */
public class AdUserClickCountDAOImpl implements IAdUserClickCountDAO {

	public void updateBatch(List<AdUserClickCount> adUserClickCounts) {
		JDBCHelper jdbcHelper = JDBCHelper.getInstance();
		
		// 首先对用户广告点击量进行分类，分成待插入的和待更新的
		List<AdUserClickCount> insertAdUserClickCounts = new ArrayList<AdUserClickCount>();
		List<AdUserClickCount> updateAdUserClickCounts = new ArrayList<AdUserClickCount>();
		
		String selectSQL = "SELECT count(*) FROM ad_user_click_count "
				+ "WHERE date=? AND user_id=? AND ad_id=? ";
		Object[] selectParams = null;
		
		for(AdUserClickCount adUserClickCount : adUserClickCounts) {
			final AdUserClickCountQueryResult queryResult = new AdUserClickCountQueryResult();
			
			selectParams = new Object[]{adUserClickCount.getDate(), 
					adUserClickCount.getUserid(), adUserClickCount.getAdid()};
			
			jdbcHelper.executeQuery(selectSQL, selectParams, new JDBCHelper.QueryCallback() {
				
				public void process(ResultSet rs) throws Exception {
					if(rs.next()) {
						int count = rs.getInt(1);
						queryResult.setCount(count);  
					}
				}
			});
			
			int count = queryResult.getCount();
			
			if(count > 0) {
				updateAdUserClickCounts.add(adUserClickCount);
			} else {
				insertAdUserClickCounts.add(adUserClickCount);
			}
		}
		
		// 执行批量插入
		String insertSQL = "INSERT INTO ad_user_click_count VALUES(?,?,?,?)";  
		List<Object[]> insertParamsList = new ArrayList<Object[]>();
		
		for(AdUserClickCount adUserClickCount : insertAdUserClickCounts) {
			Object[] insertParams = new Object[]{adUserClickCount.getDate(),
					adUserClickCount.getUserid(),
					adUserClickCount.getAdid(),
					adUserClickCount.getClickCount()};  
			insertParamsList.add(insertParams);
		}
		
		jdbcHelper.executeBatch(insertSQL, insertParamsList);
		
		// 执行批量更新
		String updateSQL = "UPDATE ad_user_click_count SET click_count=click_count+? "
				+ "WHERE date=? AND user_id=? AND ad_id=? ";  
		List<Object[]> updateParamsList = new ArrayList<Object[]>();
		
		for(AdUserClickCount adUserClickCount : updateAdUserClickCounts) {
			Object[] updateParams = new Object[]{adUserClickCount.getClickCount(),
					adUserClickCount.getDate(),
					adUserClickCount.getUserid(),
					adUserClickCount.getAdid()};  
			updateParamsList.add(updateParams);
		}
		
		jdbcHelper.executeBatch(updateSQL, updateParamsList);
	}

	public int findClickCountByMultiKey(String date, long userid, long adid) {
		String sql = "SELECT click_count "
				+ "FROM ad_user_click_count "
				+ "WHERE date = ? "
				+ "AND user_id = ? "
				+ "AND ad_id = ? ";
		
		Object[] params = new Object[]{date,userid,adid};
		
		final AdUserClickCountQueryResult queryResult = new AdUserClickCountQueryResult();
		
		JDBCHelper jdbcHelper = JDBCHelper.getInstance();
		jdbcHelper.executeQuery(sql, params, new JDBCHelper.QueryCallback() {
			
			public void process(ResultSet rs) throws Exception {
				if(rs.next()) {
					int clickCount = rs.getInt(1);
					queryResult.setClickCount(clickCount);
				}
			}
		});
		
		int clickCount = queryResult.getClickCount();
		
		return clickCount;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
