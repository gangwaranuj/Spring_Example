
package com.workmarket.service.infra;


/*@RunWith(SpringJUnit4ClassRunner.class)
@Category(BrokenTest.class)
@Ignore
public class AwsRemoteFileIT extends BaseServiceTest {
	
	private static final Log logger = LogFactory.getLog(AwsRemoteFileTest.class);
	private static String fileName = "sherman_at_workmarket_friday_august_26_2011_at_10_53_am.jpg";
	private static int fileLength = 924936;

	@Autowired private RemoteFileAdapter remoteFileAdapter;

	@Test
	public void test_put() throws Exception {
		try{
	        InputStream io = AwsRemoteFileTest.class.getResourceAsStream("/assets/" + fileName);
	        RemoteFile remoteFile = getRemoteFileAdapter().put(io, fileLength, RemoteFileType.PUBLIC, fileName);
	        logger.debug(remoteFile.toString());
	        //TODO asserts
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@Test
	public void test_AuthorizedUrl() throws Exception {
		try{	        
			URL url = getRemoteFileAdapter().getAuthorizedURL(RemoteFileType.PUBLIC, fileName);
			logger.debug("url" + url);
			//TODO asserts
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@Test
	public void test_getStream() throws Exception {
		try{
			InputStream is = getRemoteFileAdapter().getFileStream(RemoteFileType.PUBLIC, fileName);
			//TODO asserts
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@Test
	public void test_getFile() throws Exception {
		try{
			File file = getRemoteFileAdapter().getFile(RemoteFileType.PUBLIC, fileName);
			logger.debug("file.length:" + file.length());
			
			//TODO asserts
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@Test
	public void test_move()throws Exception{
		try{
			RemoteFile remoteFile = getRemoteFileAdapter().move(RemoteFileType.PUBLIC, RemoteFileType.PRIVATE, fileName);
			logger.debug(remoteFile);
			URL url = getRemoteFileAdapter().getAuthorizedURL(RemoteFileType.PRIVATE, fileName);
			logger.debug(url);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@Ignore
	@Test
	public void test_putMultipart() throws Exception {
		try {
			RemoteFile remoteFile = getRemoteFileAdapter().put(new File("/Users/amit/Downloads/Firefox-3.6.18.dmg"), RemoteFileType.PUBLIC);

			logger.debug(remoteFile.toString());
			// TODO asserts
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}



	public RemoteFileAdapter getRemoteFileAdapter() {
		return remoteFileAdapter;
	}


	public void setRemoteFileAdapter(RemoteFileAdapter remoteFileAdapter) {
		this.remoteFileAdapter = remoteFileAdapter;
	}	
} */