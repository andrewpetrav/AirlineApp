import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class DialogWait {

	private JDialog dialog;
	JFrame frame;

	public DialogWait() {
		frame = new JFrame();
		dialog = new JDialog(frame, "Just One Moment", Dialog.ModalityType.APPLICATION_MODAL);

		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		JPanel panel = new JPanel(new BorderLayout(25,20));
		panel.add(progressBar, BorderLayout.CENTER);
		panel.add(new JLabel("Please wait......."), BorderLayout.PAGE_START);
		dialog.add(panel);
		dialog.pack();
		dialog.setLocationRelativeTo(frame);
	}

	public void makeWait() {
		dialog.setVisible(true);
   }

   public void close() {
	   dialog.setVisible(false);
   }
}

