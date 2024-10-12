package fr.BlackLight.technical.launcher;

import java.io.File;
import java.util.Arrays;

import fr.theshark34.openauth.AuthPoints;
import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openauth.Authenticator;
import fr.theshark34.openauth.model.AuthAgent;
import fr.theshark34.openauth.model.response.AuthResponse;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.external.ExternalLaunchProfile;
import fr.theshark34.openlauncherlib.external.ExternalLauncher;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import fr.theshark34.openlauncherlib.minecraft.GameInfos;
import fr.theshark34.openlauncherlib.minecraft.GameTweak;
import fr.theshark34.openlauncherlib.minecraft.GameType;
import fr.theshark34.openlauncherlib.minecraft.GameVersion;
import fr.theshark34.openlauncherlib.minecraft.MinecraftLauncher;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.swinger.Swinger;


public class Launcher {

    public static final GameVersion TC_VERSION = new GameVersion("1.12.2", GameType.V1_8_HIGHER);
	public static final GameInfos TC_INFOS = new GameInfos("Technical 3", TC_VERSION, new GameTweak[] {GameTweak.FORGE});
	public static final File TC_DIR = TC_INFOS.getGameDir();
	
	@SuppressWarnings("unused")
	private static AuthInfos authInfos;
	private static Thread updateThread;
	
	public static void auth(String username, String password)throws AuthenticationException {
		Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);
		AuthResponse response = authenticator.authenticate(AuthAgent.MINECRAFT, username, password, "");
		authInfos = new AuthInfos(response.getSelectedProfile().getName(), response.getAccessToken(),response.getSelectedProfile().getId());
	}

	public static void update() throws Exception {
	    SUpdate su = new SUpdate("http://localhost", TC_DIR);
		
	    updateThread = new Thread() {
			private int val = 0;
			private int max = 0;
			
			@Override
			public void run() {
				while(!this.isInterrupted()) {
					if(BarAPI.getNumberOfFileToDownload() == 0) {
						LauncherFrame.getInstance().getLauncherPanel().setInfoText("Vérification des fichiers");
						continue;
					}
					
					val = (int) (BarAPI.getNumberOfTotalDownloadedBytes() / 1000);  
				    max = (int) (BarAPI.getNumberOfTotalBytesToDownload() / 1000);
				    
				    LauncherFrame.getInstance().getLauncherPanel().progressBar.setValue(val);
				    LauncherFrame.getInstance().getLauncherPanel().progressBar.setMaximum(max);
				    
				    LauncherFrame.getInstance().getLauncherPanel().setInfoText("Téléchargement des fichiers " +
					        BarAPI.getNumberOfDownloadedFiles() + "/" + BarAPI.getNumberOfFileToDownload() + " " +   
					            Swinger.percentage(val, max) + "%");
					}
			}	
		};
		updateThread.start();
		
		su.start();	
		updateThread.interrupt();
	}
	
	public static void launch() throws LaunchException 
	{
		ExternalLaunchProfile profile=MinecraftLauncher.createExternalProfile(TC_INFOS, GameFolder.BASIC, authInfos);
		profile.getVmArgs().addAll(Arrays.asList(LauncherFrame.getInstance().getLauncherPanel().getRamSelector().getRamArguments()));
		ExternalLauncher launcher = new ExternalLauncher(profile);
		
		
		Process p = launcher.launch();
		System.exit(0);		
	}
	
	public static void interruptThread() {
		updateThread.interrupt();	
	}
	
}
