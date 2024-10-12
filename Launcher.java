package fr.BlackLight.technical.launcher;

import java.io.File;

import fr.theshark34.openauth.AuthPoints;
import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openauth.Authenticator;
import fr.theshark34.openauth.model.AuthAgent;
import fr.theshark34.openauth.model.response.AuthResponse;
import fr.theshark34.openlauncherlib.launcher.AuthInfos;
import fr.theshark34.openlauncherlib.launcher.GameInfos;
import fr.theshark34.openlauncherlib.launcher.GameTweak;
import fr.theshark34.openlauncherlib.launcher.GameType;
import fr.theshark34.openlauncherlib.launcher.GameVersion;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;

public class Launcher {
	
	public static final GameVersion TC_VERSION = new GameVersion("1.12.2", GameType.V1_8_HIGHER);
	public static final GameInfos TC_INFOS = new GameInfos("Technical 3", TC_VERSION, true, new GameTweak[] {GameTweak.FORGE});
	public static final File TC_DIR = TC_INFOS.getGameDir();
	
	@SuppressWarnings("unused")
	private static AuthInfos authInfos;
	
	public static void auth(String username, String password)throws AuthenticationException {
		Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);
		AuthResponse response = authenticator.authenticate(AuthAgent.MINECRAFT, username, password, "");
		authInfos = new AuthInfos(response.getSelectedProfile().getName(), response.getAccessToken(),response.getSelectedProfile().getId());
	}

	public static void update() throws Exception {
		SUpdate su = new SUpdate("https://technicaltest.w2.websr.fr/, TC_DIR");
		
		Thread t = new thread() {
			private int val;
			private int max;
			
			@Override
			public void run() {
				while(this.isInterrupted()) {
					val = (int) (BarAPI.getNumberOfTotalDownloadedBytes() / 1000);
                    max = (int) (BarAPI.getNumberOfTotalBytesToDownload() / 1000);
                    
                    LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setValue(val);
				}
				
			}
		};
		su.start();
	}
}
