package qiyue;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.yx.dao.FileDao;
import com.yx.dao.FileDaoImpl;
import com.yx.entity.FileInfo;
import com.yx.service.FileService;
import com.yx.service.FileServiceImpl;

public class FileTest {

	@Test
	public void test() {
		 FileInfo fileinfo=new FileInfo("as",1,"jpg",null,null,null,null);
		    //����service����
	        FileService fs=new FileServiceImpl();
	        //��fileinfo����������ݿ�
	        fs.insertFile(fileinfo);
		
	}
	@Test
	public void test1() {
		FileDao fd=new FileDaoImpl();
		List<FileInfo> list = fd.queryAll();
		for (FileInfo file : list) {
			System.out.println(file);
		}
	}
	

}
