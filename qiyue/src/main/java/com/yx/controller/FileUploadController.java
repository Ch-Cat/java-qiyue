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
	
	//������ҳ
		@RequestMapping(value="/begin")
		public String begin(HttpServletRequest request) {
			//�Ѳ�ѯ����ֵ�浽request������
			FileService fs=new FileServiceImpl();
			List<FileInfo> list = fs.queryAll();
			request.setAttribute("list", list);
			
			// ��ȡ��Կ��
		    KeyPair keyPair=null;
			try {
				keyPair = RSAUtil2.getKeyPair();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// ��ȡRSA��Կ
		    String publicKeyStr = RSAUtil2.getPublicKey(keyPair);
		    // ��ȡRSA˽Կ,�����浽���ݿ⣬���������ļ�ʱ����
		    String privateKeyStr = RSAUtil2.getPrivateKey(keyPair);
		    //��Կ����session������У��
		    HttpSession session = request.getSession();
		    session.setAttribute("publicKey", publicKeyStr);
			//��ȡ�ַ���
			String X_SID = MyStringUtil.getRandomString();
			
			byte[] base642Byte=null;
			String X_Signature=null;
			try {
				//string-->byte
				base642Byte = RSAUtil2.base642Byte(X_SID);
				//��˽Կ���ַ�ǩ��
				X_Signature = RSAUtil2.sign(base642Byte, privateKeyStr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			 
			//�浽request
			request.setAttribute("X_SID", base642Byte);
			request.setAttribute("X_Signature", X_Signature);
			
			return "begin";
		}
	
	//�����ϴ�ҳ��
	@RequestMapping(value="/upLoadpage")
	public String upLoadpage() {
		return "fileUpload";
	}
	
	//ִ���ļ��ϴ�
	@RequestMapping(value="/fileUpload")
	public String fileUpload(@RequestParam("name") String name,
			@RequestParam("uploadfile") List<MultipartFile> uploadfile,
			HttpServletRequest request) {
		//ǩ��У��
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
					throw new RuntimeException("���Ϸ�����");
				}
		
		//�ж����ϴ��ļ��Ƿ����
		if(!uploadfile.isEmpty() && uploadfile.size()>0) {
			//ѭ������ϴ����ļ�
			for (MultipartFile file : uploadfile) {
				//��ȡ�ļ���ԭʼ����
				String yFilename=file.getOriginalFilename();
				//��ȡ�ļ��Ĵ�С
				long f_size = file.getSize();
				System.out.println(f_size);
				//��ȡ�ļ���׺
				String f_type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
			
				//��ȡ��ǰ������
		        Date date = new Date();
		        //����Ҫ��ȡ��ʲô����ʱ��
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		        //��ȡString���͵�ʱ��
		        String createdate = sdf.format(date);
		        //�����ϴ��ļ��ı����ַ
		        String cPath=request.getServletContext().getRealPath(createdate);
				
				File filePath=new File(cPath);
				
				//��������ļ��ĵ�ַ�����ڣ����ȴ���Ŀ¼
				if(!filePath.exists()) {
					filePath.mkdirs();
				}
				//File����ת��ΪString
				String f_url = filePath.getPath();
			
				//��Ҫ��ȡ��ǰ������
		        Date date1 = new Date();
		        //����Ҫ��ȡ��ʲô����ʱ��
		        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        //��ȡString���͵�ʱ��
		        String f_time = sdf1.format(date1);
				
				// ��ȡ��Կ��
			    KeyPair keyPair=null;
				try {
					keyPair = RSAUtil2.getKeyPair();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    // ��ȡRSA��Կ
			    String publicKeyStr = RSAUtil2.getPublicKey(keyPair);
			    // ��ȡRSA˽Կ,�����浽���ݿ⣬���������ļ�ʱ����
			    String privateKeyStr = RSAUtil2.getPrivateKey(keyPair);
			    


    
			    String genKeyAES = null;
			    MultipartFile mfile = null;
			    byte[] publicEncrypt = null;
			    try {
			    	//��ȡAES��Կ��Ȼ��Base64����
					genKeyAES = AESUtil.genKeyAES();
					//��Base64������AES��Կת����SecretKey����
					SecretKey loadKeyAES = AESUtil.loadKeyAES(genKeyAES);
					//���ļ�ת��Ϊbyte
					byte[] fileBt = file.getBytes();
					//AES���ļ�����
					byte[] encryptAES = AESUtil.encryptAES(fileBt, loadKeyAES);
					//���ܺ�byteת��Ϊmultipartfile
					InputStream inputStream = new ByteArrayInputStream(encryptAES);
					mfile = new MockMultipartFile(ContentType.APPLICATION_OCTET_STREAM.toString(), inputStream);
					
					//Base64����ת�ֽ�����
					byte[] byte1 = AESUtil.base642Byte(genKeyAES);
					//��RSA��Կ��AES��Կ����
					publicEncrypt = RSAUtil2.encryptByPublicKey(byte1, publicKeyStr);	
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			    
			    //��RSA��AES��Կ���ܺ�������ŷ�byteת��ΪString����
			    String f_enve = RSAUtil2.byte2Base64(publicEncrypt);

		        
		        
		        
			    //ʹ��UUID���������ϴ����ļ�
				String newFilename=name+"_"+UUID.randomUUID()+"_"+yFilename;
				
				//��ȡ�ļ���Ϣ�Ķ���,����service��
			    FileInfo fileinfo=new FileInfo(newFilename,f_size,f_type,f_time,f_url,f_enve,privateKeyStr);
			    //����service����
		        FileService fs=new FileServiceImpl();
		        //��fileinfo����������ݿ�
		        fs.insertFile(fileinfo);
				try {
					//ʹ��MultipartFile�ӿڵķ�������ļ��ϴ���ָ��λ��
					mfile.transferTo(new File(cPath +"\\"+ newFilename));
				} catch (Exception e) {
					e.printStackTrace();
					return "error";
				}
			}
			//��ת���ɹ�ҳ��
			return "success";
		}else {
			return "error";
		}
	}
	
	
}
