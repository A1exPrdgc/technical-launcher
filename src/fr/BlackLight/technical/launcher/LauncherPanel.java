package fr.BlackLight.technical.launcher;

import static fr.theshark34.swinger.Swinger.getResource;
import static fr.theshark34.swinger.Swinger.getTransparentWhite;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.util.Saver;
import fr.theshark34.openlauncherlib.util.ramselector.RamSelector;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.colored.SColoredBar;
import fr.theshark34.swinger.event.SwingerEvent;
import fr.theshark34.swinger.event.SwingerEventListener;
import fr.theshark34.swinger.textured.STexturedButton;

@SuppressWarnings("serial")
public class LauncherPanel extends JPanel implements SwingerEventListener {

	private Image background = getResource("background.png");

	private Saver saver = new Saver(new File(Launcher.TC_DIR, "launcher.properties"));

	private JTextField usernameField = new JTextField(saver.get("username"));
	private JPasswordField passwordField = new JPasswordField();

	private STexturedButton playButton = new STexturedButton(getResource("play.png"));
	private STexturedButton quitButton = new STexturedButton(getResource("quit.png"));
	private STexturedButton hideButton = new STexturedButton(getResource("hide.png"));
	
	private STexturedButton ramButton = new STexturedButton(getResource("gear.png"));

	public static SColoredBar progressBar = new SColoredBar(getTransparentWhite(50), Color.RED);
	private JLabel infoLabel = new JLabel("clique sur jouer!", SwingConstants.CENTER);  
	
	private RamSelector ramselector = new RamSelector(new File(Launcher.TC_DIR, "ram.txt"));

	public LauncherPanel() {
		this.setLayout(null);

		usernameField.setForeground(Color.BLACK);
		usernameField.setFont(usernameField.getFont().deriveFont(30F));
		usernameField.setCaretColor(Color.RED);
		usernameField.setOpaque(false);
		usernameField.setBorder(null);
		usernameField.setBounds(570, 130, 323, 63);
		this.add(usernameField);

		passwordField.setForeground(Color.BLACK);
		passwordField.setFont(usernameField.getFont());
		passwordField.setCaretColor(Color.RED);
		passwordField.setOpaque(false);
		passwordField.setBorder(null);
		passwordField.setBounds(570, 263, 323, 63);
		this.add(passwordField);

		playButton.setBounds(600, 400);
		playButton.addEventListener(this);
		this.add(playButton);

		quitButton.setBounds(910, 10);
		quitButton.addEventListener(this);
		this.add(quitButton);

		hideButton.setBounds(853, 10);
		hideButton.addEventListener(this);
		this.add(hideButton);

		progressBar.setBounds(0, 573, 960, 27);
		this.add(progressBar);

		infoLabel.setForeground(Color.RED);
		infoLabel.setFont(usernameField.getFont());
		infoLabel.setBounds(0, 530, 960, 30);
		this.add(infoLabel);
		
		this.ramButton.addEventListener(this);
		this.ramButton.setBounds(805, 10, 38, 38);
		this.add(ramButton);
	}

	@Override
	public void onEvent(SwingerEvent e) {
		if (e.getSource() == playButton) {
			setFieldsEnabled(false);

			if (usernameField.getText().replaceAll(" ", "").length() == 0 || passwordField.getText().length() == 0) {
				JOptionPane.showMessageDialog(this, "Erreur, le mot de passe ou le pseudo est invalide.", "Erreur",
						JOptionPane.ERROR_MESSAGE);
				setFieldsEnabled(true);
				return;
			}

			Thread t = new Thread() {
				@Override
				public void run() {
					try {
						Launcher.auth(usernameField.getText(), passwordField.getText());
					} catch (AuthenticationException e) {
						JOptionPane.showMessageDialog(LauncherPanel.this,
								"Erreur,impossible de se connecter : " + e.getErrorModel().getErrorMessage(), "Erreur",
								JOptionPane.ERROR_MESSAGE);
						setFieldsEnabled(true);
						return;
					}

					try {
						Launcher.update();
					} catch (Exception e) {
						Launcher.interruptThread();
						JOptionPane.showMessageDialog(LauncherPanel.this,
								"Erreur,impossible de mettre le jeu a jour : " + e, "Erreur",
								JOptionPane.ERROR_MESSAGE);
						setFieldsEnabled(true);
						return;
					}

					try {
						Launcher.launch();
					} catch (LaunchException e) {
						Launcher.interruptThread();
						JOptionPane.showMessageDialog(LauncherPanel.this, "Erreur,impossible de lancer le jeu : " + e,
								"Erreur", JOptionPane.ERROR_MESSAGE);
						setFieldsEnabled(true);
					}
				}
			};
			t.start();
		} else if (e.getSource() == quitButton)
			System.exit(0);
		else if (e.getSource() == hideButton)
			LauncherFrame.getInstance().setState(JFrame.ICONIFIED);
		else if (e.getSource() == this.ramButton)
			ramselector.display();
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		Swinger.drawFullsizedImage(graphics, this, background);

	}

	private void setFieldsEnabled(boolean enabled) {
		usernameField.setEnabled(enabled);
		passwordField.setEnabled(enabled);
		playButton.setEnabled(enabled);

	}

	public void setInfoText(String text)
	{
		this.infoLabel.setText(text);
	}
	
    public RamSelector getRamSelector()
    {
    	return ramselector;
    }

}
