package com.yx.controller;




import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import org.apache.http.entity.ContentType;
import com.yx.entity.FileInfo;
import com.yx.service.FileService;
import com.yx.service.FileServiceImpl;
import com.yx.util.AESUtil;
import com.yx.util.MyStringUtil;
import com.yx.util.RSAUtil2;


@Controller
public class FileUploadController {
	
	//进入首页
		@RequestMapping(value="/begin")
		public String begin(HttpServletRequest request) {
			//把查询到的值存到request作用域
			FileService fs=new FileServiceImpl();
			List<FileInfo> list = fs.queryAll();
			request.setAttribute("list", list);
			
			// 获取密钥对
		    KeyPair keyPair=null;
			try {
				keyPair = RSAUtil2.getKeyPair();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// 获取RSA公钥
		    String publicKeyStr = RSAUtil2.getPublicKey(keyPair);
		    // 获取RSA私钥,并保存到数据库，方便下载文件时解密
		    String privateKeyStr = RSAUtil2.getPrivateKey(keyPair);
		    //公钥存入session，用于校验
		    HttpSession session = request.getSession();
		    session.setAttribute("publicKey", publicKeyStr);
			//获取字符串
			String X_SID = MyStringUtil.getRandomString();
			
			byte[] base642Byte=null;
			String X_Signature=null;
			try {
				//string-->byte
				base642Byte = RSAUtil2.base642Byte(X_SID);
				//用私钥对字符签名
				X_Signature = RSAUtil2.sign(base642Byte, privateKeyStr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			 
			//存到request
			request.setAttribute("X_SID", base642Byte);
			request.setAttribute("X_Signature", X_Signature);
			
			return "begin";
		}
	
	//进入上传页面
	@RequestMapping(value="/upLoadpage")
	public String upLoadpage() {
		return "fileUpload";
	}
	
	//执行文件上传
	@RequestMapping(value="/fileUpload")
	public String fileUpload(@RequestParam("name") String name,
			@RequestParam("uploadfile") List<MultipartFile> uploadfile,
			HttpServletRequest request) {
		//签名校验
				HttpSession session = request.getSession();
				byte[] SID = (byte[]) session.getAttribute("SID");
				String Signature = (String) session.getAttribute("Signature");
				System.out.println(SID);
				String pk = (String)session.getAttribute("publicKey");
				boolean verify=false;
				try {
					verify = RSAUtil2.verify(SID, pk, Signature);
				} catch (Exception e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}
				if(!verify) {
					throw new RuntimeException("不合法访问");
				}
		
		//判断所上传文件是否存在
		if(!uploadfile.isEmpty() && uploadfile.size()>0) {
			//循环输出上传的文件
			for (MultipartFile file : uploadfile) {
				//获取文件的原始名称
				String yFilename=file.getOriginalFilename();
				//获取文件的大小
				long f_size = file.getSize();
				System.out.println(f_size);
				//获取文件后缀
				String f_type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
			
				//获取当前的日期
		        Date date = new Date();
		        //设置要获取到什么样的时间
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		        //获取String类型的时间
		        String createdate = sdf.format(date);
		        //设置上传文件的保存地址
		        String cPath=request.getServletContext().getRealPath(createdate);
				
				File filePath=new File(cPath);
				
				//如果保存文件的地址不存在，就先创建目录
				if(!filePath.exists()) {
					filePath.mkdirs();
				}
				//File类型转换为String
				String f_url = filePath.getPath();
			
				//我要获取当前的日期
		        Date date1 = new Date();
		        //设置要获取到什么样的时间
		        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        //获取String类型的时间
		        String f_time = sdf1.format(date1);
				
				// 获取密钥对
			    KeyPair keyPair=null;
				try {
					keyPair = RSAUtil2.getKeyPair();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    // 获取RSA公钥
			    String publicKeyStr = RSAUtil2.getPublicKey(keyPair);
			    // 获取RSA私钥,并保存到数据库，方便下载文件时解密
			    String privateKeyStr = RSAUtil2.getPrivateKey(keyPair);
			    


    
			    String genKeyAES = null;
			    MultipartFile mfile = null;
			    byte[] publicEncrypt = null;
			    try {
			    	//获取AES秘钥，然后Base64编码
					genKeyAES = AESUtil.genKeyAES();
					//将Base64编码后的AES秘钥转换成SecretKey对象
					SecretKey loadKeyAES = AESUtil.loadKeyAES(genKeyAES);
					//把文件转换为byte
					byte[] fileBt = file.getBytes();
					//AES对文件加密
					byte[] encryptAES = AESUtil.encryptAES(fileBt, loadKeyAES);
					//加密后，byte转换为multipartfile
					InputStream inputStream = new ByteArrayInputStream(encryptAES);
					mfile = new MockMultipartFile(ContentType.APPLICATION_OCTET_STREAM.toString(), inputStream);
					
					//Base64编码转字节数组
					byte[] byte1 = AESUtil.base642Byte(genKeyAES);
					//用RSA公钥对AES密钥加密
					publicEncrypt = RSAUtil2.encryptByPublicKey(byte1, publicKeyStr);	
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			    
			    //将RSA对AES密钥加密后的数字信封byte转换为String类型
			    String f_enve = RSAUtil2.byte2Base64(publicEncrypt);

		        
		        
		        
			    //使用UUID重新命名上传的文件
				String newFilename=name+"_"+UUID.randomUUID()+"_"+yFilename;
				
				//获取文件信息的对象,传到service层
			    FileInfo fileinfo=new FileInfo(newFilename,f_size,f_type,f_time,f_url,f_enve,privateKeyStr);
			    //创建service对象
		        FileService fs=new FileServiceImpl();
		        //将fileinfo对象存入数据库
		        fs.insertFile(fileinfo);
				try {
					//使用MultipartFile接口的方法完成文件上传到指定位置
					mfile.transferTo(new File(cPath +"\\"+ newFilename));
				} catch (Exception e) {
					e.printStackTrace();
					return "error";
				}
			}
			//跳转到成功页面
			return "success";
		}else {
			return "error";
		}
	}
	
	
}
