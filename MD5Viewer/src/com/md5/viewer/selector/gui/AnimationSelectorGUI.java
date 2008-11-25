package com.md5.viewer.selector.gui;

import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import com.md5.viewer.selector.gui.enumn.EButton;
import com.md5.viewer.selector.gui.handler.MD5MouseHandler;

/**
 * <code>AnimationSelectorGUI</code> defines the actual {@link Swing} GUI
 * components for the selector.
 *
 * @author Yi Wang (Neakor)
 * @author Tim Poliquin (Weenahmen)
 * @version Creation date: 11-24-2008 12:42 EST
 * @version Modified date: 11-24-2008 21:12 EST
 */
public class AnimationSelectorGUI extends JFrame {
	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = -4933057493044539956L;
	/**
	 * The <code>MD5MouseHandler</code> instance.
	 */
	private final MD5MouseHandler handler;
	/**
	 * Various {@link Swing} widgets.
	 */
	private JButton btnAdd;
	private JButton btnBaseAnimLoad;
	private JButton btnHierarchyLoad;
	private JButton btnRemove;
	private JButton btnOK;
	private JButton btnCancel;
	private JScrollPane jScrollPane1;
	private JLabel lblAnimations;
	private JLabel lblBaseAnimation;
	private JLabel lblBodyHierarchy;
	private JList lstAnimations;
	private JTextField txtBaseAnimation;
	private JTextField txtHierarchyFile;
	private JCheckBox chkManual;
	/**
	 * The <code>DefaultListModel</code> instance.
	 */
	private DefaultListModel listModel;

	/**
	 * Constructor of <code>AnimationSelectorGUI</code>.
	 * @param handler The <code>MD5MouseHandler</code> instance.
	 */
	public AnimationSelectorGUI(MD5MouseHandler handler) {
		this.handler = handler;
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AnimationSelectorGUI.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			Logger.getLogger(AnimationSelectorGUI.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(AnimationSelectorGUI.class.getName()).log(Level.SEVERE, null, ex);
		} catch (UnsupportedLookAndFeelException ex) {
			Logger.getLogger(AnimationSelectorGUI.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Initialize the GUI components.
	 */
	public void initComponents() {
		this.setResizable(false);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.jScrollPane1 = new JScrollPane();
		this.lstAnimations = new JList();
		this.lblBodyHierarchy = new JLabel();
		this.txtHierarchyFile = new JTextField();
		this.btnHierarchyLoad = new JButton();
		this.lblBaseAnimation = new JLabel();
		this.txtBaseAnimation = new JTextField();
		this.btnBaseAnimLoad = new JButton();
		this.btnAdd = new JButton();
		this.lblAnimations = new JLabel();
		this.btnRemove = new JButton();
		this.btnOK = new JButton();
		this.btnCancel = new JButton();
		this.chkManual = new JCheckBox("Manual control");

		this.listModel = new DefaultListModel();
		this.lstAnimations.setBorder(BorderFactory.createEtchedBorder());
		this.lstAnimations.setModel(this.listModel);
		this.jScrollPane1.setViewportView(this.lstAnimations);

		this.lblBodyHierarchy.setLabelFor(this.txtHierarchyFile);
		this.lblBodyHierarchy.setText("Model Hierarchy");

		this.txtHierarchyFile.setEditable(false);
		this.txtHierarchyFile.setText("<Select A File>");

		this.btnHierarchyLoad.setText(EButton.SelectHierarchy.getText());
		this.btnHierarchyLoad.setToolTipText("Select a Model Hierarchy File");
		this.btnHierarchyLoad.setName(EButton.SelectHierarchy.name());
		this.btnHierarchyLoad.addMouseListener(this.handler);

		this.lblBaseAnimation.setText("Base Animation");

		this.txtBaseAnimation.setEditable(false);
		this.txtBaseAnimation.setText("<Select A File>");

		this.btnBaseAnimLoad.setText(EButton.SelectBaseAnimation.getText());
		this.btnBaseAnimLoad.setToolTipText("Select a base animation file");
		this.btnBaseAnimLoad.setName(EButton.SelectBaseAnimation.name());
		this.btnBaseAnimLoad.addMouseListener(this.handler);

		this.btnAdd.setText(EButton.AddAnimation.getText());
		this.btnAdd.setToolTipText("Add an animation for playback");
		this.btnAdd.setName(EButton.AddAnimation.name());
		this.btnAdd.addMouseListener(this.handler);

		this.lblAnimations.setText("Animations");

		this.btnRemove.setText(EButton.RemoveAnimation.getText());
		this.btnRemove.setToolTipText("Remove the selected animation");
		this.btnRemove.setName(EButton.RemoveAnimation.name());
		this.btnRemove.addMouseListener(this.handler);

		this.btnOK.setText(EButton.OK.getText());
		this.btnOK.setToolTipText("Enter the MD5 Viewer");
		this.btnOK.setName(EButton.OK.name());
		this.btnOK.addMouseListener(this.handler);

		this.btnCancel.setText(EButton.Cancel.getText());
		this.btnCancel.setToolTipText("Exit the application");
		this.btnCancel.setName(EButton.Cancel.name());
		this.btnCancel.addMouseListener(this.handler);

		GroupLayout layout = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
								.addComponent(this.lblBodyHierarchy)
								.addComponent(this.lblBaseAnimation)
								.addComponent(this.lblAnimations)
								.addComponent(this.chkManual)
								.addGroup(layout.createSequentialGroup()
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addComponent(this.txtHierarchyFile, GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
												.addComponent(this.txtBaseAnimation, GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
												.addComponent(this.jScrollPane1, GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
												.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
														.addComponent(this.btnRemove)
														.addComponent(this.btnAdd, GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
														.addComponent(this.btnBaseAnimLoad, GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
														.addComponent(this.btnHierarchyLoad, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)))
														.addGroup(layout.createSequentialGroup()
																.addComponent(this.btnOK, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(this.btnCancel, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 92, Short.MAX_VALUE)))
																.addContainerGap())
		);

		layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {this.btnOK, this.btnCancel});

		layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {this.btnAdd, this.btnBaseAnimLoad, this.btnHierarchyLoad, this.btnRemove});

		layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {this.jScrollPane1, this.txtBaseAnimation, this.txtHierarchyFile});

		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(this.lblBodyHierarchy)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(this.txtHierarchyFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.btnHierarchyLoad))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(this.lblBaseAnimation)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(this.txtBaseAnimation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(this.btnBaseAnimLoad))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(this.lblAnimations)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addGroup(layout.createSequentialGroup()
														.addComponent(this.btnAdd)
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(this.btnRemove))
														.addComponent(this.jScrollPane1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
														.addComponent(this.chkManual)
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
														.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
																.addComponent(this.btnOK)
																.addComponent(this.btnCancel))
																.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		this.pack();
	}

	/**
	 * Add the given animation text to the chain list.
	 * @param text The <code>String</code> animation file text.
	 */
	public void addAnimation(String text) {
		this.listModel.addElement(text);
	}

	/**
	 * Remove the given animation text from the chain list.
	 * @param text The <code>String</code> animation file text.
	 */
	public void removeAnimation(String text) {
		this.listModel.removeElement(text);
	}

	/**
	 * Set the text for the hierarchy.
	 * @param text The <code>String</code> text.
	 */
	public void setHierarchyText(String text) {
		this.txtHierarchyFile.setText(text);
	}

	/**
	 * Set the text for the base animation.
	 * @param text The <code>String</code> text.
	 */
	public void setBaseAnimText(String text) {
		this.txtBaseAnimation.setText(text);
	}

	/**
	 * Get the selected animation file name.
	 * @return The selected <code>String</code> file name.
	 */
	public String getSelectedAnim() {
		return (String)this.listModel.get(this.lstAnimations.getSelectedIndex());
	}

	/**
	 * Check if the playback mode is selected to be manual.
	 * @return True if the mode is manual. False automatic.
	 */
	public boolean isManual() {
		return this.chkManual.isSelected();
	}
}
