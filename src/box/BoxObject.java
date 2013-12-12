/*
 *	@author - Rahul Varanasi 
 */

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;

import com.box.boxjavalibv2.BoxClient;
import com.box.boxjavalibv2.dao.BoxOAuthToken;
import com.box.boxjavalibv2.dao.BoxUser;
import com.box.boxjavalibv2.requests.requestobjects.BoxOAuthRequestObject;
import com.box.boxjavalibv2.resourcemanagers.BoxUsersManager;

public class BoxObject {
	private static final int PORT = 4000;
	private static final String KEY = "7fu1z45gvc8bg553r388hgiyol61z3gp";
	private static final String SECRET = "fhyveUXH4VUP9ULVn04vXVTjbU48ud0b";
	private static final String BOX_URL = "https://www.box.com/api/oauth2/authorize?response_type=code&redirect_uri=http://localhost:4000&client_id="
			+ KEY;

	private static BoxClient globalClient = null;

	private BoxObject() {
		super();
	}

	// Get the instance of the authenticated BoxClient
	// Singleton design pattern
	public static synchronized BoxClient getAuthenticatedBoxClientInstance() {
		if (globalClient == null) {
			globalClient = new BoxObject().authenticate();
		}
		return globalClient;
	}

	private BoxClient authenticate() {
		// Get auth code after user authenticates
		String authCode = getAuthCode();
		// Send auth code to authenticate Box account
		BoxClient client = authenticateBox(authCode);

		if (client == null) {
			Utils.printMsg("Access denied", true);
		}
		return client;
	}

	private String getAuthCode() {
		Utils.printMsg("Launching browser", false);
		String authCode = "";

		try {
			// Open browser for user authentication
			Desktop.getDesktop().browse(URI.create(BOX_URL));
			// Get auth code from browser
			authCode = getCodeFromBrowser();
		} catch (Exception ex) {
			Utils.printMsg("Error! " + ex.getMessage(), true);
		}

		return authCode;
	}

	private String getCodeFromBrowser() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		BufferedReader in = null;
		String code = "";

		try {
			try {
				serverSocket = new ServerSocket(PORT);
				socket = serverSocket.accept();
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				code = in.readLine();
				String match = "code";
				int loc = code.indexOf(match);

				if (loc > 0) {
					int httpstr = code.indexOf("HTTP") - 1;
					code = code.substring(code.indexOf(match), httpstr);
					String parts[] = code.split("=");
					code = parts[1];
				} else {
					Utils.printMsg("No code received", true);
					code = "";
				}
			} finally {
				if (serverSocket != null) {
					serverSocket.close();
				}
				if (in != null) {
					in.close();
				}
			}
		} catch (Exception ex) {
			Utils.printMsg("Error! " + ex.getMessage(), true);
		}
		return code;
	}

	private BoxClient authenticateBox(String authCode) {
		Utils.printMsg("Authenticating Box", false);

		if (authCode == null || authCode.equals("")) {
			return null;
		}

		BoxClient client = null;

		try {
			// Pass auth code to get an authenticated client
			client = getAuthenticatedClient(authCode);
			BoxUsersManager boxUsersMgr = client.getUsersManager();
			BoxUser authenticatedUser = boxUsersMgr.getCurrentUser(null);

			Utils.printMsg("User Authenticated", false);
			Utils.printMsgln("User Name: " + authenticatedUser.getName(), false);
		} catch (Exception ex) {
			Utils.printMsg(
					"Error! Authenticating Box failed " + ex.getMessage(), true);
		}
		return client;
	}

	private BoxClient getAuthenticatedClient(String code) {
		BoxClient client = new BoxClient(KEY, SECRET);
		BoxOAuthRequestObject obj = BoxOAuthRequestObject
				.createOAuthRequestObject(code, KEY, SECRET,
						"http://localhost:" + PORT);
		BoxOAuthToken bt = null;
		try {
			bt = client.getOAuthManager().createOAuth(obj);
		} catch (Exception ex) {
			Utils.printMsg("Error! Failed to get an authenticated Box client "
					+ ex.getMessage(), true);
		}
		client.authenticate(bt);
		return client;
	}

}
