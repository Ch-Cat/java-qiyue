package com.yx.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.yx.entity.FileInfo;
import com.yx.util.JdbcUtil;

public class FileDaoImpl implements FileDao {

	public List<FileInfo> queryAll() {
		Connection conn=null;
		ResultSet rs=null;
		Statement stm=null;
		try {
			conn=JdbcUtil.getConnection();
			stm = conn.createStatement();
			String sql="select * from fileinfo";
			rs=stm.executeQuery(sql);
			FileInfo f=null;
			List<FileInfo> list=new ArrayList<FileInfo>();
			while(rs.next()) {
				String f_name=rs.getString(1);
				long f_size=rs.getLong(2);
				String f_type=rs.getString(3);
				String f_time=rs.getString(4);
				String f_url=rs.getString(5);
				String f_enve=rs.getString(6);
				f=new FileInfo(f_name,f_size,f_type,f_time,f_url,f_enve,null);
				list.add(f);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}finally {
			JdbcUtil.close(rs, stm);
		}
		
		
	}

	public void insertOne(FileInfo fileinfo) {
		Connection conn=null;
		PreparedStatement pstm=null;
		FileInfo fi1 = queryOne(fileinfo.getF_name());
		if(fi1==null) {
			try {
				conn=JdbcUtil.getConnection();
				String sql="insert into fileinfo values(?,?,?,?,?,?,?)";
				pstm=conn.prepareStatement(sql);
				pstm.setString(1, fileinfo.getF_name());
				pstm.setLong(2, fileinfo.getF_size());
				pstm.setString(3, fileinfo.getF_type());
				pstm.setString(4, fileinfo.getF_time());
				pstm.setString(5, fileinfo.getF_url());
				pstm.setString(6, fileinfo.getF_enve());
				pstm.setString(7, fileinfo.getF_rsaprivate());
				pstm.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}finally {
				JdbcUtil.close(null, pstm);
				
			}
		}else {
			try {
				conn=JdbcUtil.getConnection();
				String sql="update fileinfo set f_size=?,f_time=?,f_url=?,f_enve=?,f_rsaprivate=? where f_name=?";
				pstm=conn.prepareStatement(sql);				
				pstm.setLong(1, fileinfo.getF_size());				
				pstm.setString(2, fileinfo.getF_time());
				pstm.setString(3, fileinfo.getF_url());
				pstm.setString(4, fileinfo.getF_enve());
				pstm.setString(5, fileinfo.getF_rsaprivate());
				pstm.setString(6, fi1.getF_name());
				pstm.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}finally {
				JdbcUtil.close(null, pstm);
				
			}

		}
		
		

	}

	public FileInfo queryOne(String filename) {
		Connection conn=null;
		PreparedStatement pstm=null;
		ResultSet rs=null;
		try {
			conn=JdbcUtil.getConnection();
			String sql="select * from fileinfo where f_name=?";
			pstm=conn.prepareStatement(sql);
			pstm.setString(1, filename);
			rs = pstm.executeQuery();
			FileInfo fi=null;
			while(rs.next()) {
				String name = rs.getString(1);
				long size = rs.getLong(2);
				String type = rs.getString(3);
				String time = rs.getString(4);
				String url = rs.getString(5);
				String enve = rs.getString(6);
				String rsaprivate = rs.getString(7);
				fi=new FileInfo(name,size,type,time,url,enve,rsaprivate);
			}
			return fi;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}finally {
			JdbcUtil.close(rs, pstm);
		}
		
	}

}
