/*
 Structorizer
 A little tool which you can use to create Nassi-Schneiderman Diagrams (NSD)

 Copyright (C) 2009  Bob Fisch

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or any
 later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package lu.fisch.structorizer.gui;

/*
 ******************************************************************************************************
 *
 *     Author: Bob Fisch
 *
 *     Description: This the dialog that allows editing the properties of any element.
 *
 ******************************************************************************************************
 *
 *     Revision List
 *
 *      Author          Date        Description
 *      ------          ----        -----------
 *      Bob Fisch       2007.12.23  First Issue
 *      Kay Gürtzig     2015.10.12  A checkbox added for breakpoint control (KGU#43)
 *      Kay Gürtzig     2015.10.14  Element-class-specific language support (KGU#42)
 *      Kay Gürtzig     2015.10.25  Hook for subclassing added to method create() (KGU#3)
 *      Kay Gürtzig     2016.04.26  Issue #165: Focus transfer reset to Tab and Shift-Tab
 *      Kay Gürtzig     2016.07.14  Enh. #180: Initial focus dependent on switchTextComment mode (KGU#169)
 *      Kay Gürtzig     2016.08.02  Enh. #215: Breakpoint trigger counts partially implemented
 *      Kay Gürtzig     2016.09.13  Bugfix #241: Obsolete mechanisms removed (remnants of KGU#42)
 *      Kay Gürtzig     2016.09.22  Bugfix #241 revised by help of a LangDialog API modification
 *      Kay Gürtzig     2016.10.13  Enh. #270: New checkbox chkDisabled
 *      Kay Gürtzig     2016.11.02  Issue #81: Workaround for lacking DPI awareness
 *      Kay Gürtzig     2016.11.09  Issue #81: Scale factor no longer rounded but ensured to be >= 1
 *      Kay Gürtzig     2016.11.11  Issue #81: DPI-awareness workaround for checkboxes
 *      Kay Gürtzig     2016.11.21  Issue #284: Opportunity to scale up/down the TextField fonts by Ctrl-Numpad+/-
 *      Kay Gürtzig     2017.01.07  Bugfix #330 (issue #81): checkbox scaling suppressed for "Nimbus" l&f
 *      Kay Gürtzig     2017.01.09  Bugfix #330 (issue #81): Basic scaling outsourced to class GUIScaler
 *      Kay Gürtzig     2017.03.14  Enh. #372: Additional hook for subclass InputBoxRoot.
 *      Kay Gürtzig     2017.10.06  Enh. #430: The scaled TextField font size (#284) is now kept during the session
 *
 ******************************************************************************************************
 *
 *     Comment:	/
 *
 ******************************************************************************************************
 */

import lu.fisch.structorizer.io.Ini;
import lu.fisch.structorizer.locales.LangDialog;
import lu.fisch.structorizer.locales.LangTextHolder;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.*;

import lu.fisch.structorizer.elements.Element;

@SuppressWarnings("serial")
public class InputBox extends LangDialog implements ActionListener, KeyListener {
	
	// START KGU#428 2017-10-06: Issue #430 Modified editor font size should be maintained
	/** font size for the text fields, 0 = default, may be overridden by keys or from ini */
	public static float FONT_SIZE = 0;	// Default value
	// END KGU#428 2017-10-06

    public boolean OK = false;

    // KGU#3 2015-11-03: Some of the controls had to be made public in order to allow language support for subclasses 
    // Buttons
    public JButton btnOK = new JButton("OK");    
    public JButton btnCancel = new JButton("Cancel");

    // Labels
    public JLabel lblText = new JLabel("Please enter a text");
    public JLabel lblComment = new JLabel("Comment");

    // Textarea
    public JTextArea txtText = new JTextArea();
    public JTextArea txtComment = new JTextArea();

    // Scrollpanes
    protected JScrollPane scrText = new JScrollPane(txtText);
    protected JScrollPane scrComment = new JScrollPane(txtComment);

    // Checkboxes
    // START KGU#277 2016-10-13: #270
    public JCheckBox chkDisabled = new JCheckBox("Execution and export disabled");
    // END KGU#277 2016-10-13
    // START KGU#43 2015-10-12: Additional possibility to control the breakpoint setting
    public JCheckBox chkBreakpoint = new JCheckBox("Breakpoint");
    // END KGU#43 2015-10-12
    // START KGU#213 2016-08-01: Enh. #215
    //private int prevBreakTrigger = 0;
    public JLabel lblBreakTriggerText = new JLabel("Break at execution count: %");
    public LangTextHolder lblBreakTrigger = new LangTextHolder("0");
    //public JTextField txtBreakTrigger = new JTextField();
    // END KGU#213 2016-08-01
    
    // START KGU#294 2016-11-22: Enh. #284
    protected final JButton btnFontUp = new JButton(IconLoader.getIcon(33)); 
    protected final JButton btnFontDown = new JButton(IconLoader.getIcon(34));
    // END KGI#294 2016-11-22

    // START KGU 2015-10-14: Additional information for data-specific title translation
    public String elementType = new String();	// The (lower-case) class name of the element type to be edited here
    public boolean forInsertion = false;		// If this dialog is used to setup a new element (in contrast to updating an existing element)
    // END KGU 2015-10-14

    // START KGU#287 2016-11-02: Enh. #180, Issue #81 (DPI awareness workaround)
    protected void setPreferredSize(double scaleFactor) {
        setSize((int)(500 * scaleFactor), (int)(400 * scaleFactor));        
    }
    // END KGU#287 2016-11-02
    
    // START KGU#294 2016-11-21: Issue #284
    // Components with fonts to be scaled independently 
    protected Vector<JComponent> scalableComponents = new Vector<JComponent>();
    // END KGU#294 2016-11-21

    private void create() {
        // set window title
        setTitle("Content");
        // set layout (OS default)
        setLayout(null);
            // START KGU#169 2016-07-14: Enh. #180: Now done after pack() and subclassable
        // set windows size
        //setSize(500, 400);
        // END KGU#169 2016-07-14
        
        // START KGU#287 2016-11-02: Issue #81 (DPI awareness workaround)
		double scaleFactor = Double.valueOf(Ini.getInstance().getProperty("scaleFactor","1"));
		// END KGU#287 2016-11-11

		// show form
        setVisible(false);
        // set action to perform if closed
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // set listeners
        // START KGU#294 2016-11-22: Issue #284
        btnFontUp.addActionListener(this);
        btnFontDown.addActionListener(this);
        // END KGU#294 2016-11-22
        btnOK.addActionListener(this);
        btnCancel.addActionListener(this);
        
        btnOK.addKeyListener(this);		// ???
        btnCancel.addKeyListener(this);	// ???
        txtText.addKeyListener(this);
        // START KGU#186 2016-04-26: Issue #163 - tab isn't really needed within the text
        txtText.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        txtText.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
        // END KGU#186 2016-04-26
        txtComment.addKeyListener(this);
        // START KGU#186 2016-04-26: Issue #163 - tab isn't really needed within the text
        txtComment.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        txtComment.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
            // END KGU#186 2016-04-26
        // START KGU#213 2016-08-01: Enh. #215
        //txtBreakTrigger.addKeyListener(this);
        // END KGU#213 2016-08-01
        addKeyListener(this);
        
        int border = (int)(4 * scaleFactor);
        Border emptyBorder = BorderFactory.createEmptyBorder(border, border, border, border);
        txtText.setBorder(emptyBorder);
        txtComment.setBorder(emptyBorder);
        
        // START KGU#294 2016-11-21: Issue #284
        scalableComponents.addElement(txtText);
        scalableComponents.addElement(txtComment);
        // END KGU#294 2016-11-21
        
        JPanel pnPanel0 = new JPanel();
        GridBagLayout gbPanel0 = new GridBagLayout();
        GridBagConstraints gbcPanel0 = new GridBagConstraints();
        border = (int)(10 * scaleFactor);
        gbcPanel0.insets = new Insets(border, border, 0, border);
        pnPanel0.setLayout(gbPanel0);

        // START KGU#3 2015-10-24: Open opportunities for subclasses
        createPanelTop(pnPanel0, gbPanel0, gbcPanel0);
        
        // START KGU#428 2017-10-06: Enh. #430
        if (FONT_SIZE > 0) {
            for (JComponent comp: scalableComponents) {
                Font font = comp.getFont();
                comp.setFont(font.deriveFont(FONT_SIZE));
            }
        }
        // END KGU#428 2017-10-06
 
        JPanel pnPanel1 = new JPanel();
        GridBagLayout gbPanel1 = new GridBagLayout();
        GridBagConstraints gbcPanel1 = new GridBagConstraints();
        gbcPanel1.insets = new Insets(border, border, 0, border);
        pnPanel1.setLayout(gbPanel1);
        // END KGU#3 2015-10-24
        
        gbcPanel1.gridx = 1;
        gbcPanel1.gridy = 2;
        gbcPanel1.gridwidth = 18;
        gbcPanel1.gridheight = 7;
        gbcPanel1.fill = GridBagConstraints.BOTH;
        gbcPanel1.weightx = 1;
        gbcPanel1.weighty = 1;
        gbcPanel1.anchor = GridBagConstraints.NORTH;
        gbPanel1.setConstraints(scrText, gbcPanel1);
        pnPanel1.add(scrText);
        
        gbcPanel1.gridx = 1;
        gbcPanel1.gridy = 12;
        gbcPanel1.gridwidth = 18;
        gbcPanel1.gridheight = 4;
        gbcPanel1.fill = GridBagConstraints.BOTH;
        gbcPanel1.weightx = 1;
        gbcPanel1.weighty = 1;
        gbcPanel1.anchor = GridBagConstraints.NORTH;
        gbPanel1.setConstraints(scrComment, gbcPanel1);
        pnPanel1.add(scrComment);

        gbcPanel1.gridx = 1;
        gbcPanel1.gridy = 10;
        gbcPanel1.gridwidth = 18;
        gbcPanel1.gridheight = 1;
        gbcPanel1.fill = GridBagConstraints.BOTH;
        gbcPanel1.weightx = 1;
        gbcPanel1.weighty = 0;
        gbcPanel1.anchor = GridBagConstraints.NORTH;
        gbPanel1.setConstraints(lblComment, gbcPanel1);
        pnPanel1.add(lblComment);

        // START KGU#363 2017-03-13: Enh. #372 new hook for InputBoxRoot
        gbcPanel1.gridy = 17;
        gbcPanel1.gridx = this.createExtrasBottom(pnPanel1, gbcPanel1, 12);
        // END KGU#363 2017-03-13
        
        // START KGU#294 2016-11-22: Issue #284 - visible font-resizing buttons
        JPanel fontPanel = new JPanel();
        fontPanel.setLayout(new GridLayout(0,2));
        fontPanel.add(btnFontUp);
        fontPanel.add(btnFontDown);
        
        //gbcPanel1.gridx = 12;
        gbcPanel1.gridy = 17;
        gbcPanel1.gridwidth = 7;
        gbcPanel1.gridheight = 1;
        gbcPanel1.fill = GridBagConstraints.WEST;
        gbcPanel1.weightx = 1;
        gbcPanel1.weighty = 0;
        gbcPanel1.anchor = GridBagConstraints.EAST;
        gbPanel1.setConstraints(fontPanel, gbcPanel1);
        pnPanel1.add(fontPanel);
        // END KGU#294 2016-11-22

        gbcPanel1.gridx = 1;
        // START KGU#277 2016-10-13: Enh. #270
        //gbcPanel1.gridy = 17;
        gbcPanel1.gridy = 18;
        // END KGU#277 2016-10-13
        gbcPanel1.gridwidth = 7;
        gbcPanel1.gridheight = 1;
        gbcPanel1.fill = GridBagConstraints.BOTH;
        gbcPanel1.weightx = 1;
        gbcPanel1.weighty = 0;
        gbcPanel1.anchor = GridBagConstraints.NORTH;
        gbPanel1.setConstraints(chkBreakpoint, gbcPanel1);
        pnPanel1.add(chkBreakpoint);

        // START KGU#213 2016-08-01: Enh. #215 - conditional breakpoints
        gbcPanel1.gridx = 12;
        // START KGU#277 2016-10-13: Enh. #270
        //gbcPanel1.gridy = 17;
        gbcPanel1.gridy = 18;
        // END KGU#277 2016-10-13
        gbcPanel1.gridwidth = 7;
        gbcPanel1.gridheight = 1;
        gbcPanel1.fill = GridBagConstraints.BOTH;
        gbcPanel1.weightx = 1;
        gbcPanel1.weighty = 0;
        gbcPanel1.anchor = GridBagConstraints.NORTH;
        gbPanel1.setConstraints(lblBreakTriggerText, gbcPanel1);
        pnPanel1.add(lblBreakTriggerText);
        // END KGU#213 2106-08-01

//        // START KGU#213/KGU#245 2016-09-13: Enh. #215 + bugfix #241
//        gbcPanel1.gridx = 13;
//        gbcPanel1.gridy = 17;
//        gbcPanel1.gridwidth = 1;
//        gbcPanel1.gridheight = 1;
//        gbcPanel1.fill = GridBagConstraints.RELATIVE;
//        gbcPanel1.weightx = 1;
//        gbcPanel1.weighty = 0;
//        gbcPanel1.anchor = GridBagConstraints.CENTER;
//        gbPanel1.setConstraints( lblBreakTrigger, gbcPanel1 );
//        pnPanel1.add(lblBreakTrigger);
//        // END KGU#246 2106-09-13
        gbcPanel1.insets = new Insets(border, border, border, border);

        //createExitButtons(gridbase)
        gbcPanel1.gridx = 1;
        // START KGU#277 2016-10-13: Enh. #270
        //gbcPanel1.gridy = 18;
        gbcPanel1.gridy = 19;
        // END KGU#277 2016-10-13
        gbcPanel1.gridwidth = 7;
        gbcPanel1.gridheight = 1;
        gbcPanel1.fill = GridBagConstraints.BOTH;
        gbcPanel1.weightx = 1;
        gbcPanel1.weighty = 0;
        gbcPanel1.anchor = GridBagConstraints.NORTH;
        gbPanel1.setConstraints(btnCancel, gbcPanel1);
        pnPanel1.add(btnCancel);

        // START KGU#3 2015-10-31: The new gridx causes no difference here but fits better for InputBoxFor
        gbcPanel1.gridx = 11;
            //gbcPanel1.gridx = 8;
        // END KGU#3 2015-10-31
        // START KGU#277 2016-10-13: Enh. #270
        //gbcPanel1.gridy = 18;
        gbcPanel1.gridy = 19;
        // END KGU#277 2016-10-13
        // START KGU#3 2015-10-31: The new gridwidth causes no difference here but fits better for InputBoxFor
        gbcPanel1.gridwidth = 8;
    		//gbcPanel1.gridwidth = GridBagConstraints.REMAINDER;
        // END KGU#3 2015-10-31
        gbcPanel1.gridheight = 1;
        gbcPanel1.fill = GridBagConstraints.BOTH;
        gbcPanel1.weightx = 1;
        gbcPanel1.weighty = 0;
        gbcPanel1.anchor = GridBagConstraints.NORTH;
        gbPanel1.setConstraints(btnOK, gbcPanel1);
        pnPanel1.add(btnOK);
        
        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(pnPanel0, BorderLayout.NORTH);
        container.add(pnPanel1, BorderLayout.CENTER);

        // START KGU#287 2017-01-09: Bugfix #330  - scaling stuff outsourced to class GUIScaler
		GUIScaler.rescaleComponents(this);
		// END KGU#287 2017-01-09

        // START KGU#91+KGU#169 2016-07-14: Enh. #180 (also see #39 and #142)
        this.pack();	// This makes focus control possible but must precede the size setting
        setPreferredSize(scaleFactor);
        if (Element.E_TOGGLETC) {
            txtComment.requestFocusInWindow();
        } else {
            txtText.requestFocusInWindow();
        }
        // END KGU#91+KGU#169 2016-07-14
    }

	// START KGU#3 2015-10-24: Hook for subclasses
    /**
     * Subclassable method to add specific stuff to the Panel top
     *
     * @param _panel the panel to be enhanced
     * @param _gb a usable GridBagLayout object
     * @param _gbc the layout constraints
     * @return number of lines (y units) inserted
     */
    protected int createPanelTop(JPanel _panel, GridBagLayout _gb, GridBagConstraints _gbc) {
        _gbc.gridx = 1;
        _gbc.gridy = 1;
        _gbc.gridwidth = 18;
        _gbc.gridheight = 1;
        _gbc.fill = GridBagConstraints.BOTH;
        _gbc.weightx = 1;
        _gbc.weighty = 0;
        _gbc.anchor = GridBagConstraints.NORTH;
        _gb.setConstraints(lblText, _gbc);
        _panel.add(lblText);
        // Return the number of used grid lines such that the calling method may go on there
        return 1;
    }
    // END KGU#3 2015-10-24

    /**
     * Allows a subclass to add additional controls to the left of the font button
     * panel. Must return the number of columns created.
     * @param _panel - the panel where the extra controls may be added
     * @param _gbc - the layout constraints
     * @param _maxGridX - the gridX value InputBox will claim (we must stay left of it)
     * @return the next unused gridx value
     */
    protected int createExtrasBottom(JPanel _panel, GridBagConstraints _gbc, int _maxGridX) {
        // START KGU#277 2016-10-13: Enh. #270
        _gbc.gridx = 1;
        _gbc.gridwidth = 7;
        _gbc.gridheight = 1;
        _gbc.fill = GridBagConstraints.BOTH;
        _gbc.weightx = 1;
        _gbc.weighty = 0;
        _gbc.anchor = GridBagConstraints.NORTH;
        ((GridBagLayout)_panel.getLayout()).setConstraints(chkDisabled, _gbc);
        _panel.add(chkDisabled);
        // END KGU#277 2016-10-13
        
        return _gbc.gridx + _gbc.gridwidth;
	}

    // listen to actions
    @Override
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        
        if (source == btnOK) {
            OK = true;
        } else if (source == btnCancel) {
            OK = false;
        }
        // START KGU#294 2016-11-22: Issue #284
        else if (source == btnFontUp || source == btnFontDown) {
        	fontControl(source == btnFontUp);
        	return;
        }
        // END KGU#294 2016-11-22
        setVisible(false);
    }
    
    @Override
    public void keyTyped(KeyEvent kevt) {
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            OK = false;
            setVisible(false);
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER && (e.isShiftDown() || e.isControlDown())) {
            OK = true;
            setVisible(false);
        }
        // START KGU#294 2016-11-21: Issue #284 - Opportunity to modify JTextField font size
        else if ((e.getKeyCode() == KeyEvent.VK_ADD || e.getKeyCode() == KeyEvent.VK_SUBTRACT) && (e.isControlDown())) {
            fontControl(e.getKeyCode() == KeyEvent.VK_ADD);
        }
        // END KGU#294 2016-11-21
    }
    
    @Override
    public void keyReleased(KeyEvent ke) {
//    	// START KGU#213 2016-08-01: Enh. #215
//    	Object source = ke.getSource();
//    	if (source == txtBreakTrigger)
//    	{
//    		int cnt = 0;
//    		String triggerStr = txtBreakTrigger.getText();
//    		if (triggerStr != null)
//    		{
//    			try{
//    				cnt = Integer.parseUnsignedInt(triggerStr);
//    				this.prevBreakTrigger = cnt;
//    			}
//    			catch (Exception ex)
//    			{
//    				txtBreakTrigger.setText(Integer.toString(this.prevBreakTrigger));
//    			}
//    		}
//    	}
    }

    // constructors
    public InputBox(Frame owner, boolean modal) {
        super(owner, modal);
        setPacking(false);
        create();
    }

    public String getInsertionType()
    {
        return (forInsertion?"insert":"update");
    }

    // START KGU#61 2016-03-21: Enh. #84 - Addition to facilitate specific handling
    /**
     * May check and ensure consistency between inserted data, control behaviour
     * etc.
     */
    public void checkConsistency() {
    	// Basic implementation doesn't do anything
    }
    // END KGU#61 2016-03-21

    // START KGU#246 2016-09-21: Enhancement to implement issues like bugfix #241
    /**
     * This method is called on opening after setLocale and before re-packing.
     * Replaces markers in translated texts.
     */
    @Override
    protected void adjustLangDependentComponents()
    {
    	this.lblBreakTriggerText.setText(this.lblBreakTriggerText.getText().replace("%", lblBreakTrigger.getText()));
    }
    // END KGU#246 2016-09-21
    
    // START KGU#294 2016-11-22: Issue #284
    public void fontControl(boolean up)
    {
    	Font font = txtText.getFont();
    	float increment = 2.0f;
    	if (!up) {
    		increment = font.getSize() > 8 ? -2.0f : 0.0f;
    	}
    	// START KGU#428 2017-10-06: Enh. #430
    	//Font newFont = font.deriveFont(font.getSize()+increment);
    	FONT_SIZE = font.getSize()+increment;
    	Font newFont = font.deriveFont(FONT_SIZE);
    	// END KGU#428 2017-10-06
    	for (JComponent comp: scalableComponents) {
    		comp.setFont(newFont);
    	}
    }
    // END KGU#294 2016-11-22

}
