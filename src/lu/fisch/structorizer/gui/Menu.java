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

/******************************************************************************************************
 *
 *      Author:         Bob Fisch
 *
 *      Description:    This class is responsible for setting up the entire menubar.
 *
 ******************************************************************************************************
 *
 *      Revision List
 *
 *      Author          Date			Description
 *      ------			----			-----------
 *      Bob Fisch       2007.12.30      First Issue
 *		Bob Fisch		2008.04.12		Adapted for Generator plugin
 *
 ******************************************************************************************************
 *
 *      Comment:		/
 *
 ******************************************************************************************************///


import java.util.*;
import java.io.*;

import java.awt.*;
import java.awt.event.*;


import javax.swing.*;

//import sun.awt.image.codec.JPEGImageEncoderImpl;

import lu.fisch.structorizer.elements.*;
import lu.fisch.structorizer.helpers.*;
import lu.fisch.structorizer.io.INIFilter;
import lu.fisch.structorizer.io.Ini;
import lu.fisch.structorizer.parsers.*;

public class Menu extends JMenuBar implements NSDController
{
	private Diagram diagram = null;
	private NSDController NSDControl = null;

	// Menu "File"
	protected JMenu menuFile = new JMenu("File");
	// Submenus of "File"
	protected JMenuItem menuFileNew = new JMenuItem("New", IconLoader.ico001);
	protected JMenuItem menuFileSave = new JMenuItem("Save", IconLoader.ico003);
	protected JMenuItem menuFileSaveAs = new JMenuItem("Save As ...", IconLoader.ico003);
	protected JMenuItem menuFileOpen = new JMenuItem("Open ...", IconLoader.ico002);
	protected JMenuItem menuFileOpenRecent = new JMenu("Open Recent File");
	protected JMenu menuFileExport = new JMenu("Export");
	// Submenu of "File -> Export"
	protected JMenu menuFileExportPicture = new JMenu("Picture");
	protected JMenuItem menuFileExportPicturePNG = new JMenuItem("PNG ...",IconLoader.ico032);
	protected JMenuItem menuFileExportPicturePNGmulti = new JMenuItem("PNG (multiple) ...",IconLoader.ico032);
	protected JMenuItem menuFileExportPictureEMF = new JMenuItem("EMF ...",IconLoader.ico032);
	protected JMenuItem menuFileExportPictureSWF = new JMenuItem("SWF ...",IconLoader.ico032);
	protected JMenuItem menuFileExportPicturePDF = new JMenuItem("PDF ...",IconLoader.ico032);
	protected JMenuItem menuFileExportPictureSVG = new JMenuItem("SVG ...",IconLoader.ico032);
	protected JMenu menuFileExportCode = new JMenu("Code");
/*	protected JMenuItem menuFileExportPascal = new JMenuItem("Pascal Code ...",IconLoader.ico004);
	protected JMenuItem menuFileExportOberon = new JMenuItem("Oberon Code ...",IconLoader.ico004);
	protected JMenuItem menuFileExportStruktex = new JMenuItem("StrukTeX Code ...",IconLoader.ico076);
	protected JMenuItem menuFileExportPerl = new JMenuItem("Perl Code ...",IconLoader.ico004);
	protected JMenuItem menuFileExportKSH = new JMenuItem("KSH Code ...",IconLoader.ico004);*/
	protected JMenu menuFileImport = new JMenu("Import");
	// Submenu of "File -> Import"
	protected JMenuItem menuFileImportPascal = new JMenuItem("Pascal Code ...",IconLoader.ico004);

	protected JMenuItem menuFilePrint = new JMenuItem("Print ...",IconLoader.ico041);
	protected JMenuItem menuFileQuit = new JMenuItem("Quit");

	// Menu "Edit"
	protected JMenu menuEdit = new JMenu("Edit");
	// Submenu of "Edit"
	protected JMenuItem menuEditUndo = new JMenuItem("Undo",IconLoader.ico039);
	protected JMenuItem menuEditRedo = new JMenuItem("Redo",IconLoader.ico038);
	protected JMenuItem menuEditCut = new JMenuItem("Cut",IconLoader.ico044);
	protected JMenuItem menuEditCopy = new JMenuItem("Copy",IconLoader.ico042);
	protected JMenuItem menuEditPaste = new JMenuItem("Paste",IconLoader.ico043);
	protected JMenuItem menuEditCopyDiagramPNG = new JMenuItem("Copy bitmap diagram to clipboard",IconLoader.ico032);
	protected JMenuItem menuEditCopyDiagramEMF = new JMenuItem("Copy vector diagram to clipboard",IconLoader.ico032);

	protected JMenu menuView = new JMenu("View");

        // Menu "Diagram"
	protected JMenu menuDiagram = new JMenu("Diagram");
	// Submenus of "Diagram"
	protected JMenu menuDiagramAdd = new JMenu("Add");
	// Submenu "Diagram -> Add -> Before"
	protected JMenu menuDiagramAddBefore = new JMenu("Before");
	// Submenus for adding Elements "Before"
	protected JMenuItem menuDiagramAddBeforeInst = new JMenuItem("Instruction",IconLoader.ico007);
	protected JMenuItem menuDiagramAddBeforeAlt = new JMenuItem("IF statement",IconLoader.ico008);
	protected JMenuItem menuDiagramAddBeforeCase = new JMenuItem("CASE statement",IconLoader.ico047);
	protected JMenuItem menuDiagramAddBeforeFor = new JMenuItem("FOR loop",IconLoader.ico009);
	protected JMenuItem menuDiagramAddBeforeWhile = new JMenuItem("WHILE loop",IconLoader.ico010);
	protected JMenuItem menuDiagramAddBeforeRepeat = new JMenuItem("REPEAT loop",IconLoader.ico011);
	protected JMenuItem menuDiagramAddBeforeForever = new JMenuItem("ENDLESS loop",IconLoader.ico009);
	protected JMenuItem menuDiagramAddBeforeCall = new JMenuItem("Call",IconLoader.ico049);
	protected JMenuItem menuDiagramAddBeforeJump = new JMenuItem("Jump",IconLoader.ico056);
	protected JMenuItem menuDiagramAddBeforePara = new JMenuItem("Parallel",IconLoader.ico090);

	// Submenu "Diagram -> Add -> After"
	protected JMenu menuDiagramAddAfter = new JMenu("After");
	// Submenus for adding Elements "After"
	protected JMenuItem menuDiagramAddAfterInst = new JMenuItem("Instruction",IconLoader.ico012);
	protected JMenuItem menuDiagramAddAfterAlt = new JMenuItem("IF statement",IconLoader.ico013);
	protected JMenuItem menuDiagramAddAfterCase = new JMenuItem("CASE statement",IconLoader.ico048);
	protected JMenuItem menuDiagramAddAfterFor = new JMenuItem("FOR loop",IconLoader.ico014);
	protected JMenuItem menuDiagramAddAfterWhile = new JMenuItem("WHILE loop",IconLoader.ico015);
	protected JMenuItem menuDiagramAddAfterRepeat = new JMenuItem("REPEAT loop",IconLoader.ico016);
	protected JMenuItem menuDiagramAddAfterForever = new JMenuItem("ENDLESS loop",IconLoader.ico014);
	protected JMenuItem menuDiagramAddAfterCall = new JMenuItem("Call",IconLoader.ico050);
	protected JMenuItem menuDiagramAddAfterJump = new JMenuItem("Jump",IconLoader.ico055);
	protected JMenuItem menuDiagramAddAfterPara = new JMenuItem("Parallel",IconLoader.ico089);

	protected JMenuItem menuDiagramEdit = new JMenuItem("Edit",IconLoader.ico006);
	protected JMenuItem menuDiagramDelete = new JMenuItem("Delete",IconLoader.ico005);
	protected JMenuItem menuDiagramMoveUp = new JMenuItem("Move up",IconLoader.ico019);
	protected JMenuItem menuDiagramMoveDown = new JMenuItem("Move down",IconLoader.ico020);

	protected JMenu menuDiagramType = new JMenu("Type");
	protected JCheckBoxMenuItem menuDiagramTypeProgram = new JCheckBoxMenuItem("Main",IconLoader.ico022);
	protected JCheckBoxMenuItem menuDiagramTypeFunction = new JCheckBoxMenuItem("Sub",IconLoader.ico021);
	protected JCheckBoxMenuItem menuDiagramNice = new JCheckBoxMenuItem("Boxed diagram?",IconLoader.ico040);
	protected JCheckBoxMenuItem menuDiagramComment = new JCheckBoxMenuItem("Show comments?",IconLoader.ico077);
	protected JCheckBoxMenuItem menuDiagramMarker = new JCheckBoxMenuItem("Highlight variables?",IconLoader.ico079);
	protected JCheckBoxMenuItem menuDiagramDIN = new JCheckBoxMenuItem("DIN?",IconLoader.ico082);
	protected JCheckBoxMenuItem menuDiagramAnalyser = new JCheckBoxMenuItem("Analyse structogram?",IconLoader.ico083);
	protected JCheckBoxMenuItem menuDiagramSwitchComments = new JCheckBoxMenuItem("Switch text/comments?",IconLoader.ico102);

	// Menu "Help"
	protected JMenu menuPreferences = new JMenu("Preferences");
	// Submenu of "Help"
	protected JMenuItem menuPreferencesFont = new JMenuItem("Font ...",IconLoader.ico023);
	protected JMenuItem menuPreferencesColors = new JMenuItem("Colors ...",IconLoader.ico031);
	protected JMenuItem menuPreferencesOptions = new JMenuItem("Structures ...",IconLoader.ico040);
	protected JMenuItem menuPreferencesParser = new JMenuItem("Parser ...",IconLoader.ico004);
	protected JMenuItem menuPreferencesAnalyser = new JMenuItem("Analyser ...",IconLoader.ico083);
	protected JMenuItem menuPreferencesExport = new JMenuItem("Export ...",IconLoader.ico032);
	protected JMenu menuPreferencesLanguage = new JMenu("Language");
	protected JMenuItem menuPreferencesLanguageEnglish = new JCheckBoxMenuItem("English",IconLoader.ico046);
	protected JMenuItem menuPreferencesLanguageGerman = new JCheckBoxMenuItem("German",IconLoader.ico080);
	protected JMenuItem menuPreferencesLanguageFrench = new JCheckBoxMenuItem("French",IconLoader.ico045);
	protected JMenuItem menuPreferencesLanguageDutch = new JCheckBoxMenuItem("Dutch",IconLoader.ico051);
	protected JMenuItem menuPreferencesLanguageLuxemburgish = new JCheckBoxMenuItem("Luxemburgish",IconLoader.ico075);
	protected JMenuItem menuPreferencesLanguageSpanish = new JCheckBoxMenuItem("Spanish",IconLoader.ico084);
	protected JMenuItem menuPreferencesLanguagePortugalBrazil = new JCheckBoxMenuItem("Brazilian portuguese",IconLoader.ico085);
	protected JMenuItem menuPreferencesLanguageItalian = new JCheckBoxMenuItem("Italian",IconLoader.ico086);
	protected JMenuItem menuPreferencesLanguageSimplifiedChinese = new JCheckBoxMenuItem("Chinese (simplified)",IconLoader.ico087);
	protected JMenuItem menuPreferencesLanguageTraditionalChinese = new JCheckBoxMenuItem("Chinese (traditional)",IconLoader.ico094);
	protected JMenuItem menuPreferencesLanguageCzech = new JCheckBoxMenuItem("Czech",IconLoader.ico088);
	protected JMenuItem menuPreferencesLanguageRussian = new JCheckBoxMenuItem("Russian",IconLoader.ico092);
	protected JMenuItem menuPreferencesLanguagePolish = new JCheckBoxMenuItem("Polish",IconLoader.ico093);
	protected JMenu menuPreferencesLookAndFeel = new JMenu("Look & Feel");
	protected JMenu menuPreferencesSave = new JMenu("All preferences ...");
	protected JMenuItem menuPreferencesSaveAll = new JMenuItem("Save");
	protected JMenuItem menuPreferencesSaveLoad = new JMenuItem("Load from file ...");
	protected JMenuItem menuPreferencesSaveDump = new JMenuItem("Save to file ...");

	// Menu "Help"
	protected JMenu menuHelp = new JMenu("Help");
	// Submenu of "Help"
	protected JMenuItem menuHelpAbout = new JMenuItem("About ...",IconLoader.ico017);
	protected JMenuItem menuHelpUpdate = new JMenuItem("Update ...",IconLoader.ico052);

	// Error messages for analyser
	public static JLabel error01_1 = new JLabel("WARNING: No loop variable detected ...");
	public static JLabel error01_2 = new JLabel("WARNING: More than one loop variable detected ...");
	public static JLabel error01_3 = new JLabel("You are not allowed to modify the loop variable «%» inside the loop!");
	public static JLabel error02 = new JLabel("No change of the variables in the condition detected. Possible endless loop ...");
	public static JLabel error03_1= new JLabel("The variable «%» has not yet been initialized!");
	public static JLabel error03_2 = new JLabel("The variable «%» may not have been initialized!");
	public static JLabel error04 = new JLabel("You are not allowed to use an IF-statement with an empty TRUE-block!");
	public static JLabel error05 = new JLabel("The variable «%» must be written in uppercase!");
	public static JLabel error06 = new JLabel("The programname «%» must be written in uppercase!");
	public static JLabel error07_1 = new JLabel("«%» is not a valid name for a program or function!");
	public static JLabel error07_2 = new JLabel("«%» is not a valid name for a parameter!");
	public static JLabel error07_3 = new JLabel("«%» is not a valid name for a variable!");
	public static JLabel error08 = new JLabel("It is not allowed to make an assignment inside a condition.");
	public static JLabel error09 = new JLabel("Your program («%») cannot have the same name as a variable or parameter!");
	public static JLabel error10_1 = new JLabel("A single instruction element should not contain input/output instructions and assignments!");
	public static JLabel error10_2 = new JLabel("A single instruction element should not contain input and output instructions!");
	public static JLabel error10_3 = new JLabel("A single instruction element should not contain input instructions and assignments!");
	public static JLabel error10_4 = new JLabel("A single instruction element should not contain ouput instructions and assignments!");
	public static JLabel error11 = new JLabel("You probably made an assignment error. Please check this instruction!");
	public static JLabel error12 = new JLabel("The parameter «%» must start with the letter \"p\" followed by only uppercase letters!");
	public static JLabel error13_1 = new JLabel("Your function does not return any result!");
	public static JLabel error13_2 = new JLabel("Your function may not return a result!");




	public void create()
	{
		JMenuBar menubar = this;

		// Setting up Menu "File" with all submenus and shortcuts and actions
		menubar.add(menuFile);
		menuFile.setMnemonic(KeyEvent.VK_F);

		menuFile.add(menuFileNew);
		menuFileNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menuFileNew.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.newNSD(); doButtons(); } } );

		menuFile.add(menuFileSave);
		menuFileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menuFileSave.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.saveNSD(false); doButtons(); } } );

		menuFile.add(menuFileSaveAs);
		menuFileSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, (java.awt.event.InputEvent.SHIFT_MASK | (Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()))));
		menuFileSaveAs.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.saveAsNSD(); doButtons(); } } );

		menuFile.add(menuFileOpen);
		menuFileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menuFileOpen.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.openNSD(); doButtons(); } } );

		menuFile.add(menuFileOpenRecent);
		menuFileOpenRecent.setIcon(IconLoader.ico002);

		menuFile.addSeparator();

		menuFile.add(menuFileImport);

		menuFileImport.add(menuFileImportPascal);
		menuFileImportPascal.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.importPAS(); doButtons(); } } );

		menuFile.add(menuFileExport);

		menuFileExport.add(menuFileExportPicture);
		menuFileExportPicture.setIcon(IconLoader.ico032);

		menuFileExportPicture.add(menuFileExportPicturePNG);
		menuFileExportPicturePNG.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menuFileExportPicturePNG.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.exportPNG(); doButtons(); } } );

		menuFileExportPicture.add(menuFileExportPicturePNGmulti);
		menuFileExportPicturePNGmulti.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.exportPNGmulti(); doButtons(); } } );

		menuFileExportPicture.add(menuFileExportPictureEMF);
		menuFileExportPictureEMF.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.exportEMF(); doButtons(); } } );

		menuFileExportPicture.add(menuFileExportPictureSWF);
		menuFileExportPictureSWF.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.exportSWF(); doButtons(); } } );

		menuFileExportPicture.add(menuFileExportPicturePDF);
		menuFileExportPicturePDF.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.exportPDF(); doButtons(); } } );

		menuFileExportPicture.add(menuFileExportPictureSVG);
		menuFileExportPictureSVG.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.exportSVG(); doButtons(); } } );

		menuFileExport.add(menuFileExportCode);
		menuFileExportCode.setIcon(IconLoader.ico004);

		// read generators from file
		// and add them to the menu
		BufferedInputStream buff = new BufferedInputStream(getClass().getResourceAsStream("generators.xml"));
		GENParser genp = new GENParser();
		Vector plugins = genp.parse(buff);
		for(int i=0;i<plugins.size();i++)
		{
			GENPlugin plugin = (GENPlugin) plugins.get(i);
			JMenuItem pluginItem = new JMenuItem(plugin.title,IconLoader.ico004);
			menuFileExportCode.add(pluginItem);
			final String className = plugin.className;
			pluginItem.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.export(className); doButtons(); } } );
		}
/*
		menuFileExport.add(menuFileExportPascal);
		menuFileExportPascal.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.export("lu.fisch.structorizer.generators.PasGenerator"); doButtons(); } } );

		menuFileExport.add(menuFileExportOberon);
		menuFileExportOberon.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.exportMOD(); doButtons(); } } );

		menuFileExport.add(menuFileExportStruktex);
		menuFileExportStruktex.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.exportTEX(); doButtons(); } } );

		menuFileExport.add(menuFileExportPerl);
		menuFileExportPerl.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.exportPerl(); doButtons(); } } );

		menuFileExport.add(menuFileExportKSH);
		menuFileExportKSH.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.exportKSH(); doButtons(); } } );
*/
		menuFile.addSeparator();

		menuFile.add(menuFilePrint);
		menuFilePrint.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menuFilePrint.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.printNSD(); doButtons(); } } );

		menuFile.addSeparator();

		menuFile.add(menuFileQuit);
		menuFileQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menuFileQuit.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { System.exit(0); } } );


		// Setting up Menu "Edit" with all submenus and shortcuts and actions
		menubar.add(menuEdit);
		menuEdit.setMnemonic(KeyEvent.VK_E);

		menuEdit.add(menuEditUndo);
		menuEditUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menuEditUndo.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.undoNSD(); doButtons(); } } );

		menuEdit.add(menuEditRedo);
		menuEditRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, (java.awt.event.InputEvent.SHIFT_MASK | (Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()))));
		menuEditRedo.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.redoNSD(); doButtons(); } } );

		menuEdit.addSeparator();

		menuEdit.add(menuEditCut);
		menuEditCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menuEditCut.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.cutNSD(); doButtons(); } } );

		menuEdit.add(menuEditCopy);
                //Toolkit.getDefaultToolkit().get
                //MenuShortcut ms = new MenuShortcut
                menuEditCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menuEditCopy.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.copyNSD(); doButtons(); } } );

		menuEdit.add(menuEditPaste);
		menuEditPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menuEditPaste.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.pasteNSD(); doButtons(); } } );

		menuEdit.addSeparator();

		menuEdit.add(menuEditCopyDiagramPNG);
		menuEditCopyDiagramPNG.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menuEditCopyDiagramPNG.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.copyToClipboardPNG(); doButtons(); } } );

		if(!System.getProperty("os.name").toLowerCase().startsWith("mac os x"))
		{
			menuEdit.add(menuEditCopyDiagramEMF);
			menuEditCopyDiagramEMF.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			menuEditCopyDiagramEMF.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.copyToClipboardEMF(); doButtons(); } } );
		}

		// Setting up Menu "View" with all submenus and shortcuts and actions
                //menubar.add(menuView);

                // Setting up Menu "Diagram" with all submenus and shortcuts and actions
		menubar.add(menuDiagram);
		menuDiagram.setMnemonic(KeyEvent.VK_D);

		menuDiagram.add(menuDiagramAdd);
		menuDiagramAdd.setIcon(IconLoader.ico018);

		menuDiagramAdd.add(menuDiagramAddBefore);
		menuDiagramAddBefore.setIcon(IconLoader.ico019);

		menuDiagramAddBefore.add(menuDiagramAddBeforeInst);
		menuDiagramAddBeforeInst.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.addNewElement(new Instruction(),"Add new instruction ...","",false); doButtons(); } } );

		menuDiagramAddBefore.add(menuDiagramAddBeforeAlt);
		menuDiagramAddBeforeAlt.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.addNewElement(new Alternative(),"Add new IF statement ...",Element.preAlt,false); doButtons(); } } );

		menuDiagramAddBefore.add(menuDiagramAddBeforeCase);
		menuDiagramAddBeforeCase.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.addNewElement(new Case(),"Add new CASE statement ...",Element.preCase,false); doButtons(); } } );

		menuDiagramAddBefore.add(menuDiagramAddBeforeFor);
		menuDiagramAddBeforeFor.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.addNewElement(new For(),"Add new FOR loop ...",Element.preFor,false); doButtons(); } } );

		menuDiagramAddBefore.add(menuDiagramAddBeforeWhile);
		menuDiagramAddBeforeWhile.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.addNewElement(new While(),"Add new WHILE loop ...",Element.preWhile,false); doButtons(); } } );

		menuDiagramAddBefore.add(menuDiagramAddBeforeRepeat);
		menuDiagramAddBeforeRepeat.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.addNewElement(new Repeat(),"Add new REPEAT loop ...",Element.preRepeat,false); doButtons(); } } );

		menuDiagramAddBefore.add(menuDiagramAddBeforeForever);
		menuDiagramAddBeforeForever.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.addNewElement(new Forever(),"Add new ENDLESS loop ...","",false); doButtons(); } } );

		menuDiagramAddBefore.add(menuDiagramAddBeforeCall);
		menuDiagramAddBeforeCall.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.addNewElement(new Call(),"Add new call ...","",false); doButtons(); } } );

		menuDiagramAddBefore.add(menuDiagramAddBeforeJump);
		menuDiagramAddBeforeJump.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.addNewElement(new Jump(),"Add new jump ...","",false); doButtons(); } } );

		menuDiagramAddBefore.add(menuDiagramAddBeforePara);
		menuDiagramAddBeforePara.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.addNewElement(new Parallel(),"Add new parallel ...","",false); doButtons(); } } );

		menuDiagramAdd.add(menuDiagramAddAfter);
		menuDiagramAddAfter.setIcon(IconLoader.ico020);

		menuDiagramAddAfter.add(menuDiagramAddAfterInst);
		menuDiagramAddAfterInst.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.addNewElement(new Instruction(),"Add new instruction ...","",true); doButtons(); } } );
		menuDiagramAddAfterInst.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5,0));

		menuDiagramAddAfter.add(menuDiagramAddAfterAlt);
		menuDiagramAddAfterAlt.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.addNewElement(new Alternative(),"Add new IF statement ...",Element.preAlt,true); doButtons(); } } );
		menuDiagramAddAfterAlt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6,0));

		menuDiagramAddAfter.add(menuDiagramAddAfterCase);
		menuDiagramAddAfterCase.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.addNewElement(new Case(),"Add new CASE statement ...",Element.preCase,true); doButtons(); } } );
		menuDiagramAddAfterCase.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F10,0));

		menuDiagramAddAfter.add(menuDiagramAddAfterFor);
		menuDiagramAddAfterFor.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.addNewElement(new For(),"Add new FOR loop ...",Element.preFor,true); doButtons(); } } );
		menuDiagramAddAfterFor.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7,0));

		menuDiagramAddAfter.add(menuDiagramAddAfterWhile);
		menuDiagramAddAfterWhile.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.addNewElement(new While(),"Add new WHILE loop ...",Element.preWhile,true); doButtons(); } } );
		menuDiagramAddAfterWhile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8,0));

		menuDiagramAddAfter.add(menuDiagramAddAfterRepeat);
		menuDiagramAddAfterRepeat.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.addNewElement(new Repeat(),"Add new REPEAT loop ...",Element.preRepeat,true); doButtons(); } } );
		menuDiagramAddAfterRepeat.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9,0));

		menuDiagramAddAfter.add(menuDiagramAddAfterForever);
		menuDiagramAddAfterForever.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.addNewElement(new Forever(),"Add new ENDLESS loop ...","",true); doButtons(); } } );

		menuDiagramAddAfter.add(menuDiagramAddAfterCall);
		menuDiagramAddAfterCall.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.addNewElement(new Call(),"Add new call ...","",true); doButtons(); } } );
		menuDiagramAddAfterCall.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11,0));

		menuDiagramAddAfter.add(menuDiagramAddAfterJump);
		menuDiagramAddAfterJump.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.addNewElement(new Jump(),"Add new jump ...","",true); doButtons(); } } );
		menuDiagramAddAfterJump.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12,0));

		menuDiagramAddAfter.add(menuDiagramAddAfterPara);
		menuDiagramAddAfterPara.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.addNewElement(new Parallel(),"Add new parallel ...","",true); doButtons(); } } );
		menuDiagramAddAfterPara.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F13,0));

		menuDiagram.add(menuDiagramEdit);
		menuDiagramEdit.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.editNSD(); doButtons(); } } );

		menuDiagram.add(menuDiagramDelete);
		menuDiagramDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
		menuDiagramDelete.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.deleteNSD(); doButtons(); } } );

		menuDiagram.addSeparator();

		menuDiagram.add(menuDiagramMoveUp);
		menuDiagramMoveUp.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.moveUpNSD(); doButtons(); } } );

		menuDiagram.add(menuDiagramMoveDown);
		menuDiagramMoveDown.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.moveDownNSD(); doButtons(); } } );

		menuDiagram.addSeparator();

		menuDiagram.add(menuDiagramType);

		menuDiagramType.add(menuDiagramTypeProgram);
		menuDiagramTypeProgram.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.setProgram(); doButtons(); } } );

		menuDiagramType.add(menuDiagramTypeFunction);
		menuDiagramTypeFunction.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.setFunction(); doButtons(); } } );

		menuDiagram.add(menuDiagramNice);
		menuDiagramNice.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.setNice(menuDiagramNice.isSelected()); doButtons(); } } );

		menuDiagram.add(menuDiagramComment);
		menuDiagramComment.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.setComments(menuDiagramComment.isSelected()); doButtons(); } } );

		menuDiagram.add(menuDiagramSwitchComments);
		menuDiagramSwitchComments.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.toggleTextComments(); doButtons(); } } );

		menuDiagram.add(menuDiagramMarker);
		menuDiagramMarker.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.setHightlightVars(menuDiagramMarker.isSelected()); doButtons(); } } );
		menuDiagramMarker.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));

		menuDiagram.add(menuDiagramDIN);
		menuDiagramDIN.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.toggleDIN(); doButtons(); } } );

		menuDiagram.add(menuDiagramAnalyser);
		menuDiagramAnalyser.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.toggleAnalyser(); doButtons(); } } );
		menuDiagramAnalyser.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));

		// Setting up Menu "Preferences" with all submenus and shortcuts and actions
		menubar.add(menuPreferences);
		menuPreferences.setMnemonic(KeyEvent.VK_P);

		menuPreferences.add(menuPreferencesFont);
		menuPreferencesFont.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.fontNSD(); doButtons(); } } );

		menuPreferences.add(menuPreferencesColors);
		menuPreferencesColors.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.colorsNSD(); doButtons(); } } );

		menuPreferences.add(menuPreferencesOptions);
		menuPreferencesOptions.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.preferencesNSD(); doButtons(); } } );

		menuPreferences.add(menuPreferencesParser);
		menuPreferencesParser.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.parserNSD(); doButtons(); } } );

		menuPreferences.add(menuPreferencesAnalyser);
		menuPreferencesAnalyser.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.analyserNSD(); doButtons(); } } );

		menuPreferences.add(menuPreferencesExport);
		menuPreferencesExport.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.exportOptions(); doButtons(); } } );

		menuPreferences.add(menuPreferencesLanguage);
		menuPreferencesLanguage.setIcon(IconLoader.ico081);

		menuPreferencesLanguage.add(menuPreferencesLanguageEnglish);
		menuPreferencesLanguageEnglish.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { setLang("en.txt"); doButtons(); } } );

		menuPreferencesLanguage.add(menuPreferencesLanguageGerman);
		menuPreferencesLanguageGerman.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { setLang("de.txt"); doButtons(); } } );

		menuPreferencesLanguage.add(menuPreferencesLanguageFrench);
		(menuPreferencesLanguageFrench).addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { setLang("fr.txt"); doButtons(); } } );

		menuPreferencesLanguage.add(menuPreferencesLanguageDutch);
		menuPreferencesLanguageDutch.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { setLang("nl.txt"); doButtons(); } } );

		menuPreferencesLanguage.add(menuPreferencesLanguageLuxemburgish);
		menuPreferencesLanguageLuxemburgish.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { setLang("lu.txt"); doButtons(); } } );

		menuPreferencesLanguage.add(menuPreferencesLanguageSpanish);
		menuPreferencesLanguageSpanish.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { setLang("es.txt"); doButtons(); } } );

		menuPreferencesLanguage.add(menuPreferencesLanguagePortugalBrazil);
		menuPreferencesLanguagePortugalBrazil.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { setLang("pt_br.txt"); doButtons(); } } );

		menuPreferencesLanguage.add(menuPreferencesLanguageItalian);
		menuPreferencesLanguageItalian.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { setLang("it.txt"); doButtons(); } } );

		menuPreferencesLanguage.add(menuPreferencesLanguageSimplifiedChinese);
		menuPreferencesLanguageSimplifiedChinese.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { setLang("chs.txt"); doButtons(); } } );

		menuPreferencesLanguage.add(menuPreferencesLanguageTraditionalChinese);
		menuPreferencesLanguageTraditionalChinese.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { setLang("cht.txt"); doButtons(); } } );

		menuPreferencesLanguage.add(menuPreferencesLanguageCzech);
		menuPreferencesLanguageCzech.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { setLang("cz.txt"); doButtons(); } } );

		menuPreferencesLanguage.add(menuPreferencesLanguageRussian);
		menuPreferencesLanguageRussian.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { setLang("ru.txt"); doButtons(); } } );

		menuPreferencesLanguage.add(menuPreferencesLanguagePolish);
		menuPreferencesLanguagePolish.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { setLang("pl.txt"); doButtons(); } } );
                
		// create Look & Feel Menu
		menuPreferences.add(menuPreferencesLookAndFeel);
		menuPreferencesLookAndFeel.setIcon(IconLoader.ico078);
		UIManager.LookAndFeelInfo plafs[] = UIManager.getInstalledLookAndFeels();
		for(int j = 0; j < plafs.length; ++j)
		{
			JCheckBoxMenuItem mi = new JCheckBoxMenuItem(plafs[j].getName());
			mi.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { NSDControl.setLookAndFeel((((JCheckBoxMenuItem) event.getSource()).getText())); doButtons(); } } );
			menuPreferencesLookAndFeel.add(mi);

			if(mi.getText().equals(UIManager.getLookAndFeel().getName()))
			{
				mi.setSelected(true);
			}
		}

		menuPreferences.addSeparator();

		menuPreferences.add(menuPreferencesSave);
                menuPreferencesSave.add(menuPreferencesSaveAll);
		menuPreferencesSaveAll.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { NSDControl.savePreferences(); } } );
                menuPreferencesSave.add(menuPreferencesSaveDump);
		menuPreferencesSaveDump.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) 
                { 
                    NSDControl.savePreferences(); 
                    JFileChooser fc = new JFileChooser();
                    fc.setFileFilter(new INIFilter());
                    if(fc.showSaveDialog(NSDControl.getFrame())==JFileChooser.APPROVE_OPTION)
                    {
                        // save some data from the INI file
                        Ini ini = Ini.getInstance();
                        try
                        {
                            ini.load();
                            String fn = fc.getSelectedFile().toString();
                            if(fn.toLowerCase().indexOf(".ini")==-1) fn+=".ini";
                            ini.save(fn);
                        }
                        catch (Exception ex)
                        {
                            System.err.println("Error saving the configuration file ...");
                            ex.printStackTrace();
                        }
                    }
                } } );
                menuPreferencesSave.add(menuPreferencesSaveLoad);
		menuPreferencesSaveLoad.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) 
                { 
                    JFileChooser fc = new JFileChooser();
                    fc.setFileFilter(new INIFilter());
                    if(fc.showOpenDialog(NSDControl.getFrame())==JFileChooser.APPROVE_OPTION)
                    {
                        try
                        {
                            // load some data from the INI file
                            Ini ini = Ini.getInstance();
                            ini.load(fc.getSelectedFile().toString());
                            ini.save();
                            NSDControl.loadFromINI();
                        }
                        catch (Exception ex)
                        {
                            System.err.println("Error loading the configuration file ...");
                            ex.printStackTrace();
                        }
                    }
                    NSDControl.savePreferences(); 
                } } );


		// Setting up Menu "Help" with all submenus and shortcuts and actions
		menubar.add(menuHelp);
		menuDiagram.setMnemonic(KeyEvent.VK_A);

		menuHelp.add(menuHelpAbout);
		menuHelpAbout.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) {diagram.aboutNSD(); } } );
		menuHelpAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,0));

		menuHelp.add(menuHelpUpdate);
		menuHelpUpdate.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) {diagram.updateNSD(); } } );
		menuHelpUpdate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

	}

	public void setLookAndFeel(String _laf) {}
	public String getLookAndFeel() { return null;}

	public void doButtons()
	{
		if(NSDControl!=null)
		{
			NSDControl.doButtons();
		}
	}

	public void setLangLocal(String _langfile)
	{
		LangDialog.setLang(this,NSDControl.getLang());
		diagram.analyse();
	}

	public void setLang(String _langfile)
	{
		NSDControl.setLang(_langfile);
	}

	public String getLang()
	{
		return NSDControl.getLang();
	}

	public void doButtonsLocal()
	{
		if(diagram!=null)
		{
                        /*
                        // remove all submenus from "view"
                        menuView.removeAll();
                        // add submenus to "view"
                        for(int i=0;i<diagram.toolbars.size();i++)
                        {
                          final MyToolbar tb = diagram.toolbars.get(i);

                          JCheckBoxMenuItem menuToolbar = new JCheckBoxMenuItem(tb.getName(),IconLoader.ico023);
                	  menuToolbar.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { tb.setVisible(!tb.isVisible()); doButtons(); } } );

                          if (tb.isVisible())
                          {
                                menuToolbar.setSelected(true);
                          }
                          menuView.add(menuToolbar);
                          //System.out.println(entry.getKey() + "-->" + entry.getValue());
                        }
                        */

			// conditions
			boolean condition =  diagram.getSelected()!=null && diagram.getSelected()!=diagram.root;
			boolean conditionAny =  diagram.getSelected()!=null;
			int i = -1;
			boolean conditionCanMoveUp = false;
			boolean conditionCanMoveDown = false;
			if(diagram.getSelected()!=null)
			{
				if(diagram.getSelected().parent!=null)
				{
					// make shure parent is a subqueue, which is not the case if somebody clicks on a subqueue!
					if (diagram.getSelected().parent.getClass().getSimpleName().equals("Subqueue"))
					{
						i = ((Subqueue) diagram.getSelected().parent).getIndexOf(diagram.getSelected());
						conditionCanMoveUp = (i-1>=0);
						conditionCanMoveDown = (i+1<((Subqueue) diagram.getSelected().parent).getSize());
					}
				}
			}

			// undo & redo
			menuEditUndo.setEnabled(diagram.root.canUndo());
			menuEditRedo.setEnabled(diagram.root.canRedo());

			// style
			menuDiagramTypeFunction.setSelected(!diagram.isProgram());
			menuDiagramTypeProgram.setSelected(diagram.isProgram());
			menuDiagramNice.setSelected(diagram.root.isNice);
			menuDiagramComment.setSelected(Element.E_SHOWCOMMENTS);
			menuDiagramAnalyser.setSelected(Element.E_ANALYSER);

			// elements
			menuDiagramAddBeforeInst.setEnabled(condition);
			menuDiagramAddBeforeAlt.setEnabled(condition);
			menuDiagramAddBeforeCase.setEnabled(condition);
			menuDiagramAddBeforeFor.setEnabled(condition);
			menuDiagramAddBeforeWhile.setEnabled(condition);
			menuDiagramAddBeforeRepeat.setEnabled(condition);
			menuDiagramAddBeforeForever.setEnabled(condition);
			menuDiagramAddBeforeCall.setEnabled(condition);
			menuDiagramAddBeforeJump.setEnabled(condition);
			menuDiagramAddBeforePara.setEnabled(condition);

			menuDiagramAddAfterInst.setEnabled(condition);
			menuDiagramAddAfterAlt.setEnabled(condition);
			menuDiagramAddAfterCase.setEnabled(condition);
			menuDiagramAddAfterFor.setEnabled(condition);
			menuDiagramAddAfterWhile.setEnabled(condition);
			menuDiagramAddAfterRepeat.setEnabled(condition);
			menuDiagramAddAfterForever.setEnabled(condition);
			menuDiagramAddAfterCall.setEnabled(condition);
			menuDiagramAddAfterJump.setEnabled(condition);
			menuDiagramAddAfterPara.setEnabled(condition);


			// editing
			menuDiagramEdit.setEnabled(conditionAny);
			menuDiagramDelete.setEnabled(diagram.canCutCopy());
			menuDiagramMoveUp.setEnabled(conditionCanMoveUp);
			menuDiagramMoveDown.setEnabled(conditionCanMoveDown);

			// copy & paste
			menuEditCopy.setEnabled(diagram.canCutCopy());
			menuEditCut.setEnabled(diagram.canCutCopy());
			menuEditPaste.setEnabled(diagram.canPaste());

			// nice
			menuDiagramNice.setSelected(diagram.isNice());

			// comments?
			menuDiagramComment.setSelected(diagram.drawComments());

			// variable hightlighting
			menuDiagramMarker.setSelected(diagram.root.hightlightVars);

			menuDiagramSwitchComments.setSelected(Element.E_TOGGLETC);
                        
                        
			// din
			menuDiagramDIN.setSelected(Element.E_DIN);
			if(Element.E_DIN==true)
			{
				menuDiagramAddBeforeFor.setIcon(IconLoader.ico010);
				menuDiagramAddAfterFor.setIcon(IconLoader.ico015);
			}
			else
			{
				menuDiagramAddBeforeFor.setIcon(IconLoader.ico009);
				menuDiagramAddAfterFor.setIcon(IconLoader.ico014);
			}

			// Look and Feel submenu
			//System.out.println("Having: "+UIManager.getLookAndFeel().getName());
			for(i=0;i<menuPreferencesLookAndFeel.getMenuComponentCount();i++)
			{
				JCheckBoxMenuItem mi =(JCheckBoxMenuItem) menuPreferencesLookAndFeel.getMenuComponent(i);

				//System.out.println("Listing: "+mi.getText());
				if (mi.getText().equals(NSDControl.getLookAndFeel()))
				{
					mi.setSelected(true);
					//System.out.println("Found: "+mi.getText());
				}
				else
				{
					mi.setSelected(false);
				}
			}

			// Langauges
			menuPreferencesLanguageEnglish.setSelected(getLang().equals("en.txt"));
			menuPreferencesLanguageGerman.setSelected(getLang().equals("de.txt"));
			menuPreferencesLanguageFrench.setSelected(getLang().equals("fr.txt"));
			menuPreferencesLanguageDutch.setSelected(getLang().equals("nl.txt"));
			menuPreferencesLanguageLuxemburgish.setSelected(getLang().equals("lu.txt"));
			menuPreferencesLanguageSpanish.setSelected(getLang().equals("es.txt"));
                        menuPreferencesLanguagePortugalBrazil.setSelected(getLang().equals("pt_br.txt"));
                        menuPreferencesLanguageItalian.setSelected(getLang().equals("it.txt"));
                        menuPreferencesLanguageSimplifiedChinese.setSelected(getLang().equals("chs.txt"));
                        menuPreferencesLanguageTraditionalChinese.setSelected(getLang().equals("cht.txt"));
                        menuPreferencesLanguageCzech.setSelected(getLang().equals("cz.txt"));
                        menuPreferencesLanguageRussian.setSelected(getLang().equals("ru.txt"));
                        menuPreferencesLanguagePolish.setSelected(getLang().equals("pl.txt"));

			// Recentl file
			menuFileOpenRecent.removeAll();
			for(int j = 0; j < diagram.recentFiles.size(); ++j)
			{
				JMenuItem mi = new JMenuItem((String) diagram.recentFiles.get(j),IconLoader.ico074);
				final String nextFile = (String) diagram.recentFiles.get(j);
				mi.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent event) { diagram.openNSD(nextFile); doButtons(); } } );
				menuFileOpenRecent.add(mi);
			}

		}
	}

	public void updateColors() {}


	public Menu(Diagram _diagram, NSDController _NSDController)
	{
		super();
		diagram=_diagram;
		NSDControl=_NSDController;
		create();
	}

	public void savePreferences() {};

    public JFrame getFrame()
    {
        return NSDControl.getFrame();
    }

    public void loadFromINI()
    {
    }

}