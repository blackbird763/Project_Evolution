package hardcode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;

public class GUI extends Shell {
	private Text Hunter_text;
	private Text Food_text;
	private Text Time_text;
	private Runnable runnabletext;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			GUI shell = new GUI(display);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public GUI(Display display) {
		super(display, SWT.SHELL_TRIM);
		createContents();
		Composite composite = new Composite(this, SWT.NONE);
		composite.setBounds(10, 10, 224, 223);
		
		
		Canvas canvas1 = new Board(composite);			//Board classe, welche Canvas Extended. Hier steckt der komplette Code Dahinter!
		canvas1.setBounds(10, 10, 200, 200);
		
		Composite composite_1 = new Composite(this, SWT.NONE);
		composite_1.setBounds(240, 10, 196, 223);
		
		Label Hunter_label = new Label(composite_1, SWT.NONE);
		Hunter_label.setBounds(10, 10, 55, 15);
		Hunter_label.setText("Hunters");
		
		Hunter_text = new Text(composite_1, SWT.BORDER);
		Hunter_text.setEditable(false);
		Hunter_text.setBounds(10, 31, 76, 21);
		
		Label Food_label = new Label(composite_1, SWT.NONE);
		Food_label.setBounds(10, 81, 55, 15);
		Food_label.setText("Food");
		
		Food_text = new Text(composite_1, SWT.BORDER);
		Food_text.setEditable(false);
		Food_text.setBounds(10, 102, 76, 21);
		
		Label Time_label = new Label(composite_1, SWT.NONE);
		Time_label.setBounds(10, 148, 55, 15);
		Time_label.setText("Time");
		
		Time_text = new Text(composite_1, SWT.BORDER);
		Time_text.setEditable(false);
		Time_text.setBounds(10, 169, 76, 21);
		
		
		
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SWT Application");
		setSize(535, 418);
		this.setMinimumSize(535, 418);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
