package com.yx.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.yx.dao.FileDao;
import com.yx.dao.FileDaoImpl;
import com.yx.entity.FileInfo;
import com.yx.util.JdbcUtil;

public class FileServiceImpl implements FileService {

	public List<FileInfo> queryAll() {
		Connection conn=null;
		try {
			conn=JdbcUtil.getConnection();
			conn.setAutoCommit(false);
			FileDao fd=new FileDaoImpl();
			List<FileInfo> list = fd.queryAll();
			conn.commit();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new RuntimeException(e.getMessage());
		}finally {
			JdbcUtil.close(conn);
		}
		
	}

	public void insertFile(FileInfo fileInfo) {
		Connection conn=null;
		try {
			conn=JdbcUtil.getConnection();
			conn.setAutoCommit(false);
			FileDao fd=new FileDaoImpl();
			fd.insertOne(fileInfo);
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new RuntimeException(e.getMessage());
		}finally {
			JdbcUtil.close(conn);
		}

	}

	public FileInfo queryOne(String filename) {
		Connection conn=null;
		try {
			conn=JdbcUtil.getConnection();
			conn.setAutoCommit(false);
			FileDao fd=new FileDaoImpl();
			FileInfo fileInfo = fd.queryOne(filename);
			conn.commit();
			return fileInfo;
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new RuntimeException(e.getMessage());
		}finally {
			JdbcUtil.close(conn);
		}
	}

}
