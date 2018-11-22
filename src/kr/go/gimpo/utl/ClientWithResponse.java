package kr.go.gimpo.utl;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.jsoup.Jsoup; 
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ClientWithResponse {
//	public static String fileFullPath = "C:\\workspace_gimpo\\SNSData\\bin\\kr\\go\\gimpo\\utl\\";
	public static String fileFullPath = "C:\\Program Files\\Java\\jdk1.7.0_51\\";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClientWithResponse cb = new ClientWithResponse();
		SFTPService sftp = new SFTPService();
		
//		cb.getFacebookHtCont();
		
		while (true) {
			try {
				
				try {
					cb.getPholarHtCont2(cb.getPholarHtCont());
					cb.getTwitterHtCont();
					cb.getInstagramHtCont();
					cb.getFacebookHtCont();
					
					String path = ClientWithResponse.class.getResource("").getPath(); // 현재 클래스의 절대 경로를 가져온다.

			        String sftpHost = "192.168.1.77";
			        int sftpPort = 7721;
			        String sftpUser = "gp-was";
			        String sftpPass = "rlavh301*";
			        String sftpWorkingDir = "/Data1/home_src/home_user/common/"; //접근할 폴더가 위치할 경로

			        ClientWithResponse.upload(fileFullPath + "snsOut.txt", sftpWorkingDir, "snsOut.txt");
			        

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
				Thread.sleep(180000);	//3분
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		

	}

	public static void apdFile(String txt, boolean del){
	    try{
	         
	        // 파일 객체 생성
		    File fileInSamePackage = new File(fileFullPath + "snsOut.txt"); // path 폴더 내의 test.txt 를 가리킨다.
		    if( !fileInSamePackage.exists()){
		    	fileInSamePackage.createNewFile();
		    }else{
		    	if(del){
		    		fileInSamePackage.delete();
		    		fileInSamePackage.createNewFile();
		    	}
		    }

		    try {
		    	FileOutputStream output=new FileOutputStream(fileFullPath + "snsOut.txt",true);
		        OutputStreamWriter writer=new OutputStreamWriter(output,"UTF-8");
		        BufferedWriter out=new BufferedWriter(writer);
		        out.write(txt);
		        out.newLine();
		        out.close();

		    }catch(Exception e){
		        e.printStackTrace();
		    }
	                
	    }catch(Exception e){
	        e.printStackTrace();
	    }

	}
	
 	public static int upload(String localFilePath, String remoteFilePath, String fileName) throws Exception {

		FTPClient ftp = null; // FTP Client 객체
		FileInputStream fis = null; // File Input Stream
		File uploadfile = new File(localFilePath); // File 객체

		String url = "192.168.1.77";
		String id = "gp-was";
		String pwd = "rlavh301*";
		String port = "7721";

		int result = -1;
		try {
			ftp = new FTPClient(); // FTP Client 객체 생성
			ftp.setControlEncoding("UTF-8"); // 문자 코드를 UTF-8로 인코딩
			ftp.connect(url, Integer.parseInt(port)); // 서버접속 " "안에 서버 주소 입력 또는
														// "서버주소", 포트번호
			ftp.login(id, pwd); // FTP 로그인 ID, PASSWORLD 입력
			ftp.enterLocalPassiveMode(); // Passive Mode 접속일때
			ftp.changeWorkingDirectory(remoteFilePath); // 작업 디렉토리 변경
			ftp.setFileType(FTP.BINARY_FILE_TYPE); // 업로드 파일 타입 셋팅

			try {
				String delFile = remoteFilePath + fileName;
				boolean delflag = ftp.deleteFile(delFile);
				
				fis = new FileInputStream(uploadfile); // 업로드할 File 생성
				boolean isSuccess = ftp.storeFile(fileName, fis); // File 업로드

				if (isSuccess) {
					result = 1; // 성공
				} else {
//					throw new CommonException("파일 업로드를 할 수 없습니다.");
				}
			} catch (IOException ex) {
				System.out.println("IO Exception : " + ex.getMessage());
			} finally {
				if (fis != null) {
					try {
						fis.close(); // Stream 닫기
						return result;

					} catch (IOException ex) {
						System.out.println("IO Exception : " + ex.getMessage());
					}
				}
			}
			ftp.logout(); // FTP Log Out
		} catch (IOException e) {
			System.out.println("IO:" + e.getMessage());
		} finally {
			if (ftp != null && ftp.isConnected()) {
				try {
					ftp.disconnect(); // 접속 끊기
					return result;
				} catch (IOException e) {
					System.out.println("IO Exception : " + e.getMessage());
				}
			}
		}
		return result;
	}
	
    public String getHtmlCont(){
        String rntVal = "";
        try {
        	CloseableHttpClient httpclient = HttpClients.createDefault();
        	
            try {
            	HttpGet httpget = new HttpGet("https://twitter.com/gimpomaru");
            	//sHttpGet httpget = new HttpGet("https://www.instagram.com/gimpocity/");

                System.out.println("Executing request " + httpget.getRequestLine());

                // Create a custom response handler
                ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                    @Override
                    public String handleResponse(
                            final HttpResponse response) throws ClientProtocolException, IOException {
                        int status = response.getStatusLine().getStatusCode();
                        if (status >= 200 && status < 300) {
                            HttpEntity entity = response.getEntity();
                            return entity != null ? EntityUtils.toString(entity) : null;
                        } else {
                            throw new ClientProtocolException("Unexpected response status: " + status);
                        }
                    }

                };
                String responseBody = httpclient.execute(httpget, responseHandler);
//                System.out.println("----------------------------------------");
                //System.out.println(responseBody);
                String html = responseBody; 
                Document doc = Jsoup.parse(html);
                Element body = doc.body();
//                Elements cont = doc.getElementsByClass("content");
//                System.out.println(cont.toString());
                Elements cont = doc.getElementsByClass("js-tweet-text-container");
//                System.out.println("============================");
//                System.out.println(cont.get(0).getElementsByTag("p").html());
                
                Elements imgs = doc.getElementsByClass("AdaptiveMedia-photoContainer");
//                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
//                System.out.println(imgs.get(0).getElementsByTag("img").attr("src"));
                
                rntVal = cont.toString();
            } finally {
                httpclient.close();
            }
		} catch (Exception e) {
			// TODO: handle exception
		}

        
        return rntVal;
    }

    public String getFacebookHtCont(){
        String rntVal = "";
        try {
        	CloseableHttpClient httpclient = HttpClients.createDefault();
        	String yUrl = "https://www.facebook.com/pg/gimpocity.kr/posts/?ref=page_internal";
            try {
            	HttpGet httpget = new HttpGet(yUrl);
            	//sHttpGet httpget = new HttpGet("https://www.instagram.com/gimpocity/");

                System.out.println("Executing request " + httpget.getRequestLine());

                // Create a custom response handler
                ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                    @Override
                    public String handleResponse(
                            final HttpResponse response) throws ClientProtocolException, IOException {
                        int status = response.getStatusLine().getStatusCode();
                        if (status >= 200 && status < 300) {
                            HttpEntity entity = response.getEntity();
                            return entity != null ? EntityUtils.toString(entity) : null;
                        } else {
                            throw new ClientProtocolException("Unexpected response status: " + status);
                        }
                    }

                };
                String responseBody = httpclient.execute(httpget, responseHandler);
//                System.out.println("----------------------------------------");
                //System.out.println(responseBody);
                String html = responseBody; 
                Document doc = Jsoup.parse(html);
//                Element body = doc.body();
//                Elements cont = doc.getElementsByClass("content");
//                System.out.println(cont.toString());
                Element cont = doc.getElementById("pagelet_timeline_main_column");
                Elements container = cont.getElementsByClass("userContentWrapper");
//                System.out.println(cont.get(0).getElementsByTag("p").html());
                String TTxt = container.get(0).getElementsByTag("p").html();
                String pTxt = container.get(0).getElementsByTag("p").text().replaceAll(container.get(0).getElementsByTag("p").get(0).getElementsByTag("a").text(), "");
                rntVal += pTxt;
                rntVal += "±";
                
                String aHref="";
                if(cont.getElementsByClass("see_more_link") != null){
                	aHref = "https://www.facebook.com"+cont.getElementsByClass("see_more_link").attr("href");
                }else{
                	aHref = "https://www.facebook.com/gimpocity.kr/";
                }
                rntVal += aHref;
                rntVal += "±";
                
                String aImg="";
                if(cont.getElementsByClass("uiScaledImageContainer") != null){
                	aImg = cont.getElementsByClass("uiScaledImageContainer").get(0).getElementsByTag("img").attr("src");
                }else{
                	aImg = "http://www.gimpo.go.kr/images/site/portal/main/noImage.jpg";
                }
                rntVal += aImg;
                rntVal += "±";


            } finally {
                httpclient.close();
            }
		} catch (Exception e) {
			// TODO: handle exception
		}

        apdFile(rntVal, false);
        return rntVal;
    }    
     
    public String getTwitterHtCont(){
        String rntVal = "";
        try {
        	CloseableHttpClient httpclient = HttpClients.createDefault();
        	String yUrl = "https://www.twitter.com/gimpomaru";
            try {
            	HttpGet httpget = new HttpGet(yUrl);
            	//sHttpGet httpget = new HttpGet("https://www.instagram.com/gimpocity/");

                System.out.println("Executing request " + httpget.getRequestLine());

                // Create a custom response handler
                ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                    @Override
                    public String handleResponse(
                            final HttpResponse response) throws ClientProtocolException, IOException {
                        int status = response.getStatusLine().getStatusCode();
                        if (status >= 200 && status < 300) {
                            HttpEntity entity = response.getEntity();
                            return entity != null ? EntityUtils.toString(entity) : null;
                        } else {
                            throw new ClientProtocolException("Unexpected response status: " + status);
                        }
                    }

                };
                String responseBody = httpclient.execute(httpget, responseHandler);
//                System.out.println("----------------------------------------");
                //System.out.println(responseBody);
                String html = responseBody; 
                Document doc = Jsoup.parse(html);
//                Element body = doc.body();
//                Elements cont = doc.getElementsByClass("content");
//                System.out.println(cont.toString());
                Elements cont = doc.getElementsByClass("content");
                Elements container = doc.getElementsByClass("js-tweet-text-container");
//                System.out.println(cont.get(0).getElementsByTag("p").html());
                String TTxt = container.get(0).getElementsByTag("p").html();
                String pTxt = container.get(0).getElementsByTag("p").text().replaceAll(container.get(0).getElementsByTag("p").get(0).getElementsByTag("a").text(), "");
                rntVal += pTxt;
                rntVal += "±";
                
                String aHref="";
                if(TTxt.indexOf("<a") >=0){
                	aHref = container.get(0).getElementsByTag("p").get(0).getElementsByTag("a").attr("href");
                }else{
                	aHref = "https://twitter.com/gimpomaru";
                }
                rntVal += aHref;
                rntVal += "±";
                
                String aImg="";
                if(cont.toString().indexOf("AdaptiveMedia-photoContainer") >=0){
                	aImg = cont.get(0).getElementsByClass("AdaptiveMedia-photoContainer").get(0).getElementsByTag("img").attr("src");
                }else{
                	aImg = "http://www.gimpo.go.kr/images/site/portal/main/noImage.jpg";
                }
                rntVal += aImg;
                rntVal += "±";


            } finally {
                httpclient.close();
            }
		} catch (Exception e) {
			// TODO: handle exception
		}

        apdFile(rntVal, false);
        return rntVal;
    }    
 
    public String getInstagramHtCont(){
        String rntVal = "";
        try {
        	String url = "https://www.instagram.com/gimpocity/";
            
		} catch (Exception e) {
			// TODO: handle exception
		}
        
        try {
        	CloseableHttpClient httpclient = HttpClients.createDefault();
        	String yUrl = "https://www.instagram.com/gimpocity/";
            try {
            	HttpGet httpget = new HttpGet(yUrl);
            	//sHttpGet httpget = new HttpGet("https://www.instagram.com/gimpocity/");

                System.out.println("Executing request " + httpget.getRequestLine());

                // Create a custom response handler
                ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                    @Override
                    public String handleResponse(
                            final HttpResponse response) throws ClientProtocolException, IOException {
                        int status = response.getStatusLine().getStatusCode();
                        if (status >= 200 && status < 300) {
                            HttpEntity entity = response.getEntity();
                            return entity != null ? EntityUtils.toString(entity) : null;
                        } else {
                            throw new ClientProtocolException("Unexpected response status: " + status);
                        }
                    }

                };
                String responseBody = httpclient.execute(httpget, responseHandler);
//                System.out.println("----------------------------------------");
//                System.out.println(responseBody);
                String html = responseBody; 
                Document doc = Jsoup.parse(html);
                String window_sharedData = ""; 
                for (int i = 0; i < doc.getElementsByTag("script").size()-1; i++) {
					if(doc.getElementsByTag("script").get(i).toString().indexOf("window._sharedData") >= 0){
						window_sharedData = doc.getElementsByTag("script").get(i).toString().toString();
						window_sharedData = window_sharedData.replace("<script type=\"text/javascript\">", "").replace("</script>", "").replace("window.", "").replace("\"", "\'");
						break;
					}
                	
				}
        		// JavaScript를 호출
        		// JavaScript의 ScriptEngine을 구함
        		ScriptEngineManager sem = new ScriptEngineManager();
        		ScriptEngine se = sem.getEngineByName("JavaScript");
        		// eval 함수로 평가. JavaScript를 실행
        		se.eval("print('JavaScript is running')");

        		// JavaScript를 커파일하여 실행함
        		Compilable compilable = (Compilable) se;
        		CompiledScript cs = compilable.compile("print('JavaScript is compiled')");
        		cs.eval();

        		
        		se.eval(window_sharedData);
//        		se.eval("print(_sharedData.entry_data.ProfilePage[0].graphql.user.edge_owner_to_timeline_media.edges[0].node.edge_media_to_caption.edges[0].node.text)");
        		
        		
//        		// Java에서 JavaScript를 호출
//        		// addtion.js로부터 함수를 읽어 인수를 전달해 실행
        		se.eval(new FileReader(fileFullPath+"addition.js"));
//        		// Java에서 호출 결과는 3
        		//((Invocable) se).invokeFunction("add", 1, 2);
        		String lnk = "https://www.instagram.com/gimpocity/";
        		String media_to_caption = ((Invocable) se).invokeFunction("getData_media_to_caption").toString().replaceAll("(\r\n|\r|\n|\n\r)", " ");
        		String thumbnail_src = ((Invocable) se).invokeFunction("getData_thumbnail_src").toString();

        		rntVal += lnk;
                rntVal += "±";
                rntVal += media_to_caption;
                rntVal += "±";
                rntVal += media_to_caption;
                rntVal += "±";
                rntVal += thumbnail_src;
                //System.out.println(rntVal);
        		// JavaScript로부터 Java의 메소드를 호출
//        		se.put("jsr", new ClientWithResponse());
//        		se.eval("jsr.sayHello('World')");
                
            } finally {
                httpclient.close();
            }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

        apdFile(rntVal,false);
        return rntVal;
    }    
    
    public String getPholarHtCont(){
    	System.setProperty("jsse.enableSNIExtension", "false");
    	
    	String rntVal = "";
        try {
        	CloseableHttpClient httpclient = HttpClients.createDefault();
        	
            try {
            	HttpGet httpget = new HttpGet("https://www.pholar.co/my/913211/profile");

                System.out.println("Executing request " + httpget.getRequestLine());

                // Create a custom response handler
                ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                    @Override
                    public String handleResponse(
                            final HttpResponse response) throws ClientProtocolException, IOException {
                        int status = response.getStatusLine().getStatusCode();
                        if (status >= 200 && status < 300) {
                            HttpEntity entity = response.getEntity();
                            return entity != null ? EntityUtils.toString(entity) : null;
                        } else {
                            throw new ClientProtocolException("Unexpected response status: " + status);
                        }
                    }

                };
                String responseBody = httpclient.execute(httpget, responseHandler);
//                System.out.println("----------------------------------------");
//                System.out.println(responseBody);
                String html = responseBody; 
                Document doc = Jsoup.parse(html);
                Element body = doc.body();
                Elements list_wrap = doc.getElementsByClass("list_wrap");
                String tgtUrl = "https://pholar.co:443"+list_wrap.get(0).getElementsByTag("a").attr("href");
                
                System.setProperty("jsse.enableSNIExtension", "false");
                rntVal = getPholarHtCont2(tgtUrl);


            } catch (Exception e) {
    			// TODO: handle exception
    			e.printStackTrace();
    		} finally {
                httpclient.close();
            }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

        apdFile(rntVal, true);
        return rntVal;
    }    
    
    public String getPholarHtCont2(String tgtUrl){
//    	System.setProperty("jsse.enableSNIExtension", "false");

    	String rntVal = "";
        try {
        	CloseableHttpClient httpclient = HttpClients.createDefault();
        	
            try {
            	HttpGet httpget = new HttpGet(tgtUrl);
            	//sHttpGet httpget = new HttpGet("https://www.instagram.com/gimpocity/");

                System.out.println("Executing request " + httpget.getRequestLine());

                // Create a custom response handler
                ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                	@Override
                    public String handleResponse(
                            final HttpResponse response) throws ClientProtocolException, IOException {
                        int status = response.getStatusLine().getStatusCode();
                        if (status >= 200 && status < 300) {
                            HttpEntity entity = response.getEntity();
                            return entity != null ? EntityUtils.toString(entity) : null;
                        } else {
                            throw new ClientProtocolException("Unexpected response status: " + status);
                        }
                    }

                };
                System.setProperty("jsse.enableSNIExtension", "false");
                
                String responseBody = httpclient.execute(httpget, responseHandler);
//                System.out.println("----------------------------------------");
//                System.out.println(responseBody);
                String html = responseBody; 
                Document doc = Jsoup.parse(html);
                
                Elements data_location = doc.getElementsByClass("item_info_wrap");
//              System.out.println(item_data.text());
                String TTxt = data_location.html();
                String pTxt = data_location.get(0).getElementsByClass("item_data _post_content").text();
                rntVal += pTxt;
	            rntVal += "±";
	            
	            String aHref = tgtUrl;
	            rntVal += aHref;
                rntVal += "±";

                String aImg="";
                if(TTxt.indexOf("item_thumb") >=0){
                	aImg = data_location.get(0).getElementsByClass("item_thumb").get(0).getElementsByTag("img").attr("data-source");
                }else{
                	aImg = "http://www.gimpo.go.kr/images/site/portal/main/noImage.jpg";
                }
                rntVal += aImg;
                rntVal += "±";              
                
                rntVal += pTxt;
                rntVal += "±";

            } finally {
                httpclient.close();
            }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

        
        return rntVal;
    }    
}
