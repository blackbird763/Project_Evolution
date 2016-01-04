package hardcode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Canvas;

public class GUI extends Shell {

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
		
		Canvas canvas = new Board(composite);			//Board classe, welche Canvas Extended. Hier steckt der komplette Code Dahinter!
		canvas.setBounds(10, 10, 200, 200);
	
		
		
		
		
		
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SWT Application");
		setSize(535, 418);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
