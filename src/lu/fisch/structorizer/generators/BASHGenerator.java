/*
    This file is part of Structorizer.

    Structorizer is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Structorizer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

 ***********************************************************************

    BASH Source Code Generator

    Copyright (C) 2008 Markus Grundner

    This file has been released under the terms of the GNU Lesser General
    Public License as published by the Free Software Foundation.

    http://www.gnu.org/licenses/lgpl.html

 */

package lu.fisch.structorizer.generators;

/******************************************************************************************************
 *
 *      Author:         Markus Grundner
 *
 *      Description:    BASH Source Code Generator
 *
 ******************************************************************************************************
 *
 *      Revision List
 *
 *      Author				Date			Description
 *      ------				----			-----------
 *      Markus Grundner     2008-06-01		First Issue based on KSHGenerator from Jan Peter Kippel
 *      Bob Fisch           2011-11-07      Fixed an issue while doing replacements
 *      Kay Gürtzig         2014-11-16      Bugfixes in operator conversion and enhancements (see comments)
 *      Kay Gürtzig         2015-10-18      Indentation logic and comment insertion revised
 *                                          generateCode(For, String) and generateCode(Root, String) modified
 *      Kay Gürtzig         2015-11-02      transform methods re-organised (KGU#18/KGU#23) using subclassing,
 *                                          Pattern list syntax in Case Elements corrected (KGU#15).
 *                                          Bugfix KGU#60 (Repeat loop was incorrectly translated).
 *      Kay Gürtzig         2015-12-19      Enh. #23 (KGU#78): Jump translation implemented
 *      Kay Gürtzig         2015-12-21      Bugfix #41/#68/#69 (= KGU#93): String literals were spoiled
 *      Kay Gürtzig         2015-12-22      Bugfix #71 (= KGU#114): Text transformation didn't work
 *      Kay Gürtzig         2016-01-08      Bugfix #96 (= KGU#129): Variable names handled properly,
 *                                          KGU#132: Logical expressions (conditions) put into ((  )).
 *      Kay Gürtzig         2016-03-22      Enh. #84/#135 (= KGU#61): Support for FOR-IN loops
 *      Kay Gürtzig         2016-03-24      Bugfix #92/#135 (= KGU#161) Input variables were prefixed
 *      Kay Gürtzig         2016-03-29      KGU#164: Bugfix #138 Function call expression revised (in transformTokens())
 *                                          #135 Array and expression support improved (with thanks to R. Schmidt)
 *      Kay Gürtzig         2016-03-31      Enh. #144 - content conversion may be switched off
 *      Kay Gürtzig         2016-04-05      Enh. #153 - Export of Parallel elements had been missing
 *      Kay Gürtzig         2016-04-05      KGU#150 - provisional support for chr and ord function
 *      Kay Gürtzig         2016-07-20      Enh. #160: Option to involve subroutines implemented (=KGU#178) 
 *      Kay Gürtzig         2016-08-12      Enh. #231: Additions for Analyser checks 18 and 19 (identifier collisions)
 *      Kay Gürtzig         2016-09-01      Issue #234: ord and chr function code generated only if needed and allowed
 *      Kay Gürtzig         2016-09-21      Bugfix #247: Forever loops were exported with a defective condition.
 *      Kay Gürtzig         2016-10-14      Enh. #270: Handling of disabled elements (code.add(...) --> addCode(..))
 *      Kay Gürtzig         2016-10-15      Enh. #271: Support for input with prompt
 *      Kay Gürtzig         2016-10-16      Enh. #274: Colour info for Turtleizer procedures added
 *      Kay Gürtzig         2016-11-06      Issue #279: Method HashMap.getOrDefault() replaced
 *      Kay Gürtzig         2017-01-05      Enh. #314: File API TODO comments added  
 *      Kay Gürtzig         2017-02-27      Enh. #346: Insertion mechanism for user-specific include directives
 *      Kay Gürtzig         2017-04-18      Bugfix #386: Algorithmically empty Subqueues must produce a ':' line
 *      Kay Gürtzig         2017-05-05      Issue #396: function calls should better be enclosed in $(...) than in back ticks
 *      Kay Gürtzig         2017-05-16      Enh. #372: Export of copyright information
 *      Kay Gürtzig         2017-05-19      Issue #237: Expression transformation heuristics improved
 *      Kay Gürtzig         2017-10-05      Enh. #423: First incomplete approach to handle record variables
 *      Kay Gürtzig         2017-10-24      Enh. #423: Record variable handling accomplished for release 3.27
 *      Kay Gürtzig         2017-11-02      Issue #447: Line continuation in Alternative and Case elements supported
 *      Kay Gürtzig         2019-02-15      Enh. #680: Support for input instructions with several variables
 *      Kay Gürtzig         2019-03-08      Enh. #385: Optional function arguments with defaults
 *      Kay Gürtzig         2019-03-30      Issue #696: Type retrieval had to consider an alternative pool
 *      Kay Gürtzig         2019-09-27      Enh. #738: Support for code preview map on Root level
 *      Kay Gürttig         2019-10-15      Bugfix #765: Private field typeMap had to be made protected
 *      Kay Gürtzig         2019-11-08      Bugfix #769: Undercomplex selector list splitting in CASE generation mended
 *      Kay Gürtzig         2019-11-24      Bugfix #783 - Workaround for record initializers without known type
 *      Kay Gürtzig         2019-11-24      Bugfix #784 - Suppression of mere declarations and fix in transformExpression()
 *      Kay Gürtzig         2019-12-01      Enh. #739: Support for enum types, $() around calls removed, array decl subclassable
 *
 ******************************************************************************************************
 *
 *      Comment:		LGPL license (http://www.gnu.org/licenses/lgpl.html).
 *      
 *      2016.04.05 - Enhancement #153 (Kay Gürtzig / Rolf Schmidt)
 *      - Parallel elements hat just been ignored by previous versions Now an easy way could be
 *        implemented. It's working rather well, provided that the commands within the
 *        branches are convertible. Delivered with version 3.24-06. 
 *      
 *      2016.03.21/22 - Enhancement #84/#135 (Kay Gürtzig / Rolf Schmidt)
 *      - Besides the working (but rather rarely used) C-like "three-expression" FOR loop, FOR-IN loops
 *        were to be enabled in a consistent way, i.e. the syntax must also be accepted by Editors and
 *        Executor as well as other code generators.
 *      - This generator copes with value lists of the following types:
 *        {item1, item2, item3} --> item1 item2 item3
 *        item1, item2, item3 -->   item1 item2 item3
 *        {val1..val2} and {val1..val2..step} would be left as is but not explicitly created
 *      
 *      2015.12.21 - Bugfix #41/#68/#69 (Kay Gürtzig)
 *      - Operator replacement had induced unwanted padding and string literal modifications
 *      - new subclassable method transformTokens() for all token-based replacements 
 *      
 *      2015-11-02 - Code revision / enhancements
 *      - Most of the transform stuff delegated to Element and Generator (KGU#18/KGU23)
 *      - Enhancement #10 (KGU#3): FOR loops now provide themselves more reliable loop parameters  
 *      - Case enabled to combine several constants/patterns in one branch (KGU#15)
 *      - The Repeat loop had been implememed in an incorrect way  
 *      
 *      2015.10.18 - Bugfixes (KGU#53, KGU#30)
 *      - Conversion of functions improved by producing headers according to BASH syntax
 *      - Conversion of For loops slightly improved (not robust, may still fail with complex expressions as loop parameters
 *      
 *      2014.11.16 - Bugfixes / Enhancement
 *      - conversion of Pascal-like logical operators "and", "or", and "not" supported 
 *      - conversion of comparison and operators accomplished
 *      - comment export introduced 
 *
 ******************************************************************************************************///

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lu.fisch.structorizer.elements.Alternative;
import lu.fisch.structorizer.elements.Call;
import lu.fisch.structorizer.elements.Case;
import lu.fisch.structorizer.elements.Element;
import lu.fisch.structorizer.elements.For;
import lu.fisch.structorizer.elements.Forever;
import lu.fisch.structorizer.elements.Instruction;
import lu.fisch.structorizer.elements.Jump;
import lu.fisch.structorizer.elements.Parallel;
import lu.fisch.structorizer.elements.Repeat;
import lu.fisch.structorizer.elements.Root;
import lu.fisch.structorizer.elements.Subqueue;
import lu.fisch.structorizer.elements.TypeMapEntry;
import lu.fisch.structorizer.elements.While;
import lu.fisch.structorizer.executor.Executor;
import lu.fisch.structorizer.executor.Function;
import lu.fisch.structorizer.generators.Generator.TryCatchSupportLevel;
import lu.fisch.structorizer.parsers.CodeParser;
import lu.fisch.utils.StringList;


public class BASHGenerator extends Generator {
	
	// START KGU#61 2016-03-22: Now provided by Generator class
	// Bugfix #96 (KGU#129, 2015-01-08): We must know all variable names to prefix them with '$'.
	//StringList varNames = new StringList();
	// END KGU#6 2016-03-22

	/************ Fields ***********************/
	@Override
	protected String getDialogTitle()
	{
		return "Export BASH Code ...";
	}
	
	@Override
	protected String getFileDescription()
	{
		return "BASH Source Code";
	}
	
	@Override
	protected String getIndent()
	{
		return " ";
	}
	
	@Override
	protected String[] getFileExtensions()
	{
		String[] exts = {"sh"};
		return exts;
	}
	
	// START KGU 2015-10-18: New pseudo field
	@Override
	protected String commentSymbolLeft()
	{
		return "#";
	}
	// END KGU 2015-10-18

	// START KGU#78 2015-12-18: Enh. #23 We must know whether to create labels for simple breaks
	/* (non-Javadoc)
	 * @see lu.fisch.structorizer.generators.Generator#supportsSimpleBreak()
	 */
	@Override
	protected boolean breakMatchesCase()
	{
		return false;
	}
	// END KGU#78 2015-12-18

	// START KGU#351 2017-02-26: Enh. #346 - include / import / uses config
	/* (non-Javadoc)
	 * @see lu.fisch.structorizer.generators.Generator#getIncludePattern()
	 */
	@Override
	protected String getIncludePattern()
	{
		return ". %";
	}
	// END KGU#351 2017-02-26

	// START KGU#371 2019-03-07: Enh. #385
	/**
	 * @return The level of subroutine overloading support in the target language
	 */
	@Override
	protected OverloadingLevel getOverloadingLevel() {
		return OverloadingLevel.OL_DEFAULT_ARGUMENTS;
	}
	// END KGU#371 2019-03-07

	// START KGU#686 2019-03-18: Enh. #56
	/**
	 * Subclassable method to specify the degree of availability of a try-catch-finally
	 * construction in the target language.
	 * @return either {@link TryCatchSupportLevel#TC_NO_TRY} or {@link TryCatchSupportLevel#TC_TRY_CATCH},
	 * or {@link TryCatchSupportLevel#TC_TRY_CATCH_FINALLY}
	 */
	protected TryCatchSupportLevel getTryCatchLevel()
	{
		/* The only theoretical approach coming near an exception handling
		 * would require to entangle the tried commands with && but this
		 * doesn't work recursively. */
		return TryCatchSupportLevel.TC_NO_TRY;
	}
	// END KGU#686 2019-03-18

	// START KGU#241 2016-09-01: Issue #234: names of certain occurring functions detected by checkElementInformation()
	protected StringList occurringFunctions = new StringList();
	// END KGU#241 2015-09-01

//	// START KGU 2016-08-12: Enh. #231 - information for analyser - obsolete since 3.27 
//    private static final String[] reservedWords = new String[]{
//		"if", "then", "else", "elif", "fi",
//		"select", "case", "in", "esac",
//		"for", "do", "done",
//		"while", "until",
//		"function", "return"};
//	public String[] getReservedWords()
//	{
//		return reservedWords;
//	}
//	public boolean isCaseSignificant()
//	{
//		return true;
//	}
//	// END KGU 2016-08-12
	
	/************ Code Generation **************/
	
	protected static final Matcher VAR_ACCESS_MATCHER = Pattern.compile("[$]\\{[A-Za-z][A-Za-z0-9_]*\\}").matcher("");
	
	// START KGU#542 2019-12-01: Enh. #739 enumeration type support - configuration for subclasses
	/** @return the shell-specific declarator for enumeration constants (e.g. {@code "declare -ri "} for bash) */
	protected String getEnumDeclarator()
	{
		return "declare -ri ";
	}
	
	/** @return the shell-specific declarator for array variables (e.g. {@code "declare -a "} for bash) */
	protected String getArrayDeclarator()
	{
		return "declare -a ";
	}

	/** @return the shell-specific declarator for associative arrays (maps, e.g. {@code "declare -A "} for bash) */
	protected String getAssocDeclarator()
	{
		return "declare -A ";
	}
	// END KGU#542 2019-12-01
	
	// START KGU#753 2019-10-15: Bugfix #765 had to be made protected, since KSHGenerator must initialize it as well. 
	//private HashMap<String, TypeMapEntry> typeMap = null;
	protected HashMap<String, TypeMapEntry> typeMap = null;
	// END KGU#753 2019-10-15 
	
	// START KGU#18/KGU#23 2015-11-01 Transformation decomposed
	/* (non-Javadoc)
	 * @see lu.fisch.structorizer.generators.Generator#getInputReplacer(boolean)
	 */
	// START KGU#281 2016-10-15: Enh. #271 (support for input with prompt)
	//protected String getInputReplacer()
	//{
	//	return "read $1";
	//}
	@Override
	protected String getInputReplacer(boolean withPrompt)
	{
		if (withPrompt) {
			return "echo -n $1 ; read $2";
		}
		return "read $1";
	}
	// END KGU#281 2016-10-15

	/* (non-Javadoc)
	 * @see lu.fisch.structorizer.generators.Generator#getOutputReplacer()
	 */
	@Override
	protected String getOutputReplacer()
	{
		return "echo $1";
	}

	// START KGU#93 2015-12-21: Bugfix #41/#68/#69
//	/**
//	 * Transforms assignments in the given intermediate-language code line.
//	 * Replaces "<-" by "="
//	 * @param _interm - a code line in intermediate syntax
//	 * @return transformed string
//	 */
//	protected String transformAssignment(String _interm)
//	{
//		return _interm.replace(" <- ", "=");
//	}

	// START KGU#150/KGU#241 2016-09-01: Issue #234 - smarter handling of ord and chr functions
	/* (non-Javadoc)
	 * @see lu.fisch.structorizer.generators.Generator#checkElementInformation(lu.fisch.structorizer.elements.Element)
	 */
	@Override
	protected boolean checkElementInformation(Element _ele)
	{
		StringList tokens = Element.splitLexically(_ele.getText().getText(), true);
		String[] functionNames = {"ord", "chr"};
		for (int i = 0; i < functionNames.length; i++)
		{
			if (!occurringFunctions.contains(functionNames[i])) {
				int pos = -1;
				while ((pos = tokens.indexOf(functionNames[i], pos+1)) >= 0 &&
						pos+1 < tokens.count() &&
						tokens.get(pos+1).equals("("))
				{
					occurringFunctions.add(functionNames[i]);
					break;	
				}
			}
		}
		
		return super.checkElementInformation(_ele);
	}
	// END KGU#150/KGU#241 2016-09-01

	/* (non-Javadoc)
	 * @see lu.fisch.structorizer.generators.Generator#transformTokens(lu.fisch.utils.StringList)
	 */
	@Override
	protected String transformTokens(StringList tokens)
	{
		// Trim the tokens at both ends (just for sure)
		tokens = tokens.trim();
		// START KGU#129 2016-01-08: Bugfix #96 - variable name processing
		// We must of course identify variable names and prefix them with $ unless being an lvalue
		int posAsgnOpr = tokens.indexOf("<-");
		// START KGU#388 2017-10-24: Enh. #335, #389, #423
		if (posAsgnOpr > 0) {
			// FIXME: Consider using lValueToTypeNameIndexComp(String) rather than reinventing all
			String token0 = tokens.get(0);
			int posColon = -1;
			if (token0.equalsIgnoreCase("var") || token0.equalsIgnoreCase("const")) {
				tokens.remove(0);
				posAsgnOpr--;
				posColon = tokens.indexOf(":");
			}
			else if (token0.equalsIgnoreCase("dim")) {
				tokens.remove(0);
				posAsgnOpr--;
				posColon = tokens.indexOf("as", false);
			}
			if (posColon > 0 && posColon < posAsgnOpr) {
				tokens.remove(posColon, posAsgnOpr);
				posAsgnOpr = posColon;
			}
			// START KGU#388 2017-10-05: Enh. #423
			int posDot = -1;
			while ((posDot = tokens.indexOf(".", posDot+1)) > 0 && posDot + 1 < posAsgnOpr) {
				if (Function.testIdentifier(tokens.get(posDot+1), null))
				{
					// FIXME: Handle multi-level record access! We might also check type
					tokens.set(posDot - 1, tokens.get(posDot-1) + "[" + tokens.get(posDot+1) + "]");
					tokens.remove(posDot, posDot+2);
					posAsgnOpr -= 2;
				}
			}
			// END KGU#388 2017-10-05
		}
		// END KGU#388 2017-10-24
		// START KGU#161 2016-03-24: Bugfix #135/#92 - variables in read instructions must not be prefixed!
		if (tokens.contains(CodeParser.getKeyword("input")))
		{
			// Hide the text from the replacement, except for occurrences as index
			posAsgnOpr = tokens.count();
		}
		// END KGU#161 2016-03-24
		// START KGU#61 2016-03-21: Enh. #84/#135
		if (posAsgnOpr < 0 && !CodeParser.getKeyword("postForIn").trim().isEmpty()) posAsgnOpr = tokens.indexOf(CodeParser.getKeyword("postForIn"));
		// END KGU#61 2016-03-21
		// If there is an array variable left of the assignment symbol, check the index 
		int posBracket1 = tokens.indexOf("[");
		int posBracket2 = -1;
		if (posBracket1 >= 0 && posBracket1 < posAsgnOpr) posBracket2 = tokens.lastIndexOf("]", posAsgnOpr-1);
		for (int i = 0; i < varNames.count(); i++)
		{
			String varName = varNames.get(i);
			//System.out.println("Looking for " + varName + "...");	// FIXME (KGU): Remove after Test!
			//_input = _input.replaceAll("(.*?[^\\$])" + varName + "([\\W$].*?)", "$1" + "\\$" + varName + "$2");
			// Transform the expression right of the assignment symbol
			transformVariableAccess(varName, tokens, posAsgnOpr+1, tokens.count());
			// Transform the index expression on the left side of the assignment symbol
			transformVariableAccess(varName, tokens, posBracket1+1, posBracket2+1);
		}

		// Position of the assignment operator may have changed now
		posAsgnOpr = tokens.indexOf("<-");
		// END KGU#96 2016-01-08
		// FIXME (KGU): Function calls, math expressions etc. will have to be put into brackets etc. pp.
		tokens.replaceAll("div", "/");
		tokens.replaceAllCi("false", "0");	// FIXME: Is this correct?
		tokens.replaceAllCi("true", "1");	// FIXME: Is this correct?
		// START KGU#164 2016-03-29: Bugfix #138 - function calls weren't handled
		//return tokens.concatenate();
		String lval = "";
		if (posAsgnOpr > 0)
		{
			// Separate lval and assignment operator from the expression tokens
			lval += tokens.concatenate("", 0, posAsgnOpr).trim() + "=";
			tokens = tokens.subSequence(posAsgnOpr+1, tokens.count());
		}
		else if (tokens.count() > 0)
		{
			// Since keywords have already been replaced by super.transform(String), this is quite fine
			// 
			String[] keywords = CodeParser.getAllProperties();
			boolean startsWithKeyword = false;
			String firstToken = tokens.get(0);
			for (int kwi = 0; !startsWithKeyword && kwi < keywords.length; kwi++)
			{
				if (firstToken.equals(keywords[kwi]))
				{
					lval = firstToken + " ";
					tokens.delete(0);
					startsWithKeyword = true;
				}
			}
		}
		// Trim the tokens (at front)
		tokens = tokens.trim();
		// Re-combine the rval expression to a string 
		String expr = tokens.concatenate();
		// If the expression is a function call, then convert it to shell syntax
		// (i.e. drop the parentheses and dissolve the argument list)
		Function fct = new Function(expr);
		// START KGU#388 2017-10-24: Enh. #423
		HashMap<String, String> recordIni = null;
		// END KGU#388 2017-10-24
		if (fct.isFunction())
		{
			// START KGU#405 2017-05-19: Bugfix #237 - was too simple an analysis
			//expr = fct.getName();
			//for (int p = 0; p < fct.paramCount(); p++)
			//{
			//	String param = fct.getParam(p);
			//	if (param.matches("(.*?)(-|[+*/%])(.*?)"))
			//	{
			//		param = "$(( " + param + " ))";
			//	}
			//	else if (param.contains(" "))
			//	{
			//		param = "\"" + param + "\"";
			//	}
			//	expr += (" " + param);
			//}
			expr = transformExpression(fct);
			// END KGU#405 2017-05-19
			// START KGU 2019-12-01: An evaluation should not apply for subroutines!
			//if (posAsgnOpr > 0)
			if (posAsgnOpr > 0 &&
					(this.routinePool == null || this.routinePool.findRoutinesBySignature(fct.getName(), fct.paramCount(), null).isEmpty()))
			// END KGU 2019-12-01
			{
				// START KGU#390 2017-05-05: Issue #396
				//expr = "`" + expr + "`";
				expr = "$(" + expr + ")";
				// END KGU#390 2017-05-05
			}
		}
		// FIXME (KGU 2019-12-01) this looks too simplistic
		else if (expr.startsWith("{") && expr.endsWith("}") && posAsgnOpr > 0)
		{
			lval = this.getArrayDeclarator() + lval;
			StringList items = Element.splitExpressionList(expr.substring(1, expr.length()-1), ",");
			// START KGU#405 2017-05-19: Bugfix #237 - was too simple an analysis
			for (int i = 0; i < items.count(); i++) {
				items.set(i, transformExpression(items.get(i), true));
			}
			// END KGU#405 2017-05-19
			expr = "(" + items.getLongString() + ")";
		}
		// START KGU#388 2017-10-24: Enh. #423
		else if (tokens.count() > 2 && Function.testIdentifier(tokens.get(0), null)
				&& tokens.get(1).equals("{") && expr.endsWith("}")
				// START KGU#559 2018-07-20: Enh. #  Try to fetch sufficient type info
				//&& (recordIni = Element.splitRecordInitializer(expr, null)) != null) {
				&& (recordIni = Element.splitRecordInitializer(expr, this.typeMap.get(":"+tokens.get(0)), false)) != null) {
				// END KGU#559 2018-07-20
			// START KGU#388 2019-11-28: Bugfix #423 - record initializations must not be separated from the declaration
			lval = this.getArrayDeclarator() + lval;
			// END KGU#388 2019-11-28
			StringBuilder sb = new StringBuilder(15 * recordIni.size());
			String sepa = "(";
			for (Entry<String, String> entry: recordIni.entrySet()) {
				String key = entry.getKey();
				if (!key.startsWith("§")) {
					sb.append(sepa + '[' + key + "]=" + entry.getValue());
					sepa = " ";
				}
			}
			// START KGU#771 2019-11-24: Bugfix #783 - fallback for the case of missing struct info
			//sb.append(")");
			//expr = sb.toString();
			// If the type info was available or didn't provide any content then leave expr as is
			if (sb.length() > 0) {
				sb.append(")");
				expr = sb.toString();
			}
			// END KGU#771 2019-11-24
		}
		// END KGU#388 2017-10-24
		// The following is a very rough and vague heuristics to support arithmetic expressions 
		else if ( !(expr.startsWith("(") && expr.endsWith(")")
				|| expr.startsWith("`") && expr.endsWith("`")
				|| expr.startsWith("'") && expr.endsWith("'")
				|| expr.startsWith("\"") && expr.endsWith("\"")
				|| expr.startsWith("[[") && expr.endsWith("]]")))
		{
			if (expr.matches(".*?[+*/%-].*?"))
			{
				// START KGU#405 2017-05-19: Issue #237
				//expr = "(( " + expr + " ))";
				//if (posAsgnOpr > 0)
				//{
				//	expr = "$" + expr;
				//}
				expr = transformExpression(expr, posAsgnOpr > 0);
				// END KGU#405 2017-05-19
			}
			// START KGU 2016-03-31: Issue #135+#144 - quoting wasn't actually helpful
//			else if (expr.contains(" "))
//			{
//				expr = "\"" + expr + "\"";
//			}
			// END KGU 2016-03-31
		}
		return lval + expr;
		// END KGU#164 2016-03-29
	}
	// END KGU#93 2015-12-21

	// END KGU#18/KGU#23 2015-11-01
	
	// START KGU#405 2017-05-19: Issue #237
	protected String transformExpression(StringList exprTokens, boolean isAssigned)
	{
		// FIXME: Check the operands - they must be literals (type detectable),
		// variables (consult typeMap), built-in functions (type known), or
		// recursively constructed expressions themselves (analyse recursively)
		// This static type check should be implemented as static method on Element
		// but needs access to the typeMap of the current Root.
		boolean isArithm =
				exprTokens.contains("+") ||
				exprTokens.contains("-") ||
				exprTokens.contains("*") ||
				exprTokens.contains("/") ||
				exprTokens.contains("%");
		// Avoid recursive enclosing in $(...)
		if (isArithm) {
			exprTokens.insert((isAssigned ? "$(( " : "(( "), 0);
			exprTokens.add(" ))");
		}
		// START KGU#772 2019-11-24: Bugfix #784 - avoid redundant enclosing with $(...)
		//else if (isAssigned) {
		//	exprTokens.insert("$(", 0);
		//	exprTokens.add(")");
		//}
		else if (isAssigned) {
			boolean isVarAccess =
					exprTokens.count() == 4 &&
					exprTokens.get(0).equals("$") &&
					exprTokens.get(1).equals("{") &&
					this.varNames.contains(exprTokens.get(2)) &&
					exprTokens.get(3).equals("}") ||
					exprTokens.count() == 1 &&
					VAR_ACCESS_MATCHER.reset(exprTokens.get(0)).matches() &&
					this.varNames.contains(exprTokens.get(0).substring(2, exprTokens.get(0).length()-1));
			if (isVarAccess) {
				exprTokens.insert("\"", 0);
				exprTokens.add("\"");
			}
			else {
				exprTokens.insert("$(", 0);
				exprTokens.add(")");
			}
		}
		// END KGU#772 2019-11-24
		return exprTokens.concatenate();
	}
	protected String transformExpression(String expr, boolean isAssigned)
	{
		if (Function.isFunction(expr)) {
			expr = this.transformExpression(new Function(expr));
			if (isAssigned)
			{
				expr = "$(" + expr + ")";
			}
		}
		else {
			expr = transformExpression(Element.splitLexically(expr, true), isAssigned);
		}
		return expr;
	}
	protected String transformExpression(Function fct)
	{
		String expr = fct.getName();
		for (int p = 0; p < fct.paramCount(); p++)
		{
			String param = fct.getParam(p);
			param = this.transformExpression(param, true);
			expr += (" " + param);
		}
		return expr;
	}
	private String finishCondition(String condition) {
		if (!this.suppressTransformation && !(condition.trim().matches("^\\(\\(.*?\\)\\)$")))
		{
			final String[] compOprs = new String[]{"==", "<", ">", "<=", ">=", "!=", "<>"};
			StringList condTokens = Element.splitLexically(condition, true);
			condTokens.removeAll(" ");
			boolean isNumber = false;
			for (int i = 0; i < condTokens.count(); i++) {
				for (int j = 1; j < compOprs.length-1; j++) {
					if (condTokens.get(i).equals(compOprs[j])) {
						// FIXME this is too vague again
						int k = i-1;
						String leftOpnd = condTokens.get(k);
						while (k >= 0 && leftOpnd.equals(")") || leftOpnd.equals("}")) {
							leftOpnd = condTokens.get(k--);
						}
						k = i+1;
						String rightOpnd = condTokens.get(k);
						while (j < condTokens.count() && rightOpnd.equals("$") || rightOpnd.equals("(") || rightOpnd.equals("{")) {
							rightOpnd = condTokens.get(j++);
						}
						String typeLeft = Element.identifyExprType(typeMap, leftOpnd, true);
						String typeRight = Element.identifyExprType(typeMap, rightOpnd, true);
						if ((typeLeft.equals("int") || typeLeft.equals("double")) && (typeRight.equals("int") || typeRight.equals("double"))) {
							isNumber = true;
						}
					}
				}
			}
			if (isNumber) {
				condition = "(( " + condition + " ))";
			}
			else {
				condition = "[[ " + condition + " ]]";
			}
		}
		return condition;
	}
	// END KGU#405 2017-05-10
	
	// START KGU#167 2016-03-30: Enh. #135 Array support
	protected void transformVariableAccess(String _varName, StringList _tokens, int _start, int _end)
	{
		int pos = _start-1;
		while ((pos = _tokens.indexOf(_varName, pos+1)) >= 0 && pos < _end)
		{
			int posNext = pos+1;
			while (posNext < _end && _tokens.get(posNext).trim().isEmpty()) posNext++;
			String nextToken = _tokens.get(posNext); 
			if (nextToken.equals("["))
			{
				_tokens.set(pos, "${" + _varName);
				// index brackets follow, so remove the blanks
				for (int i = 0; i < posNext - pos-1; i++)
				{
					_tokens.delete(pos+1);
					_end--;
				}
				// find the corresponding closing bracket
				int depth = 1;
				for (posNext = pos+2; depth > 0 && posNext < _end; posNext++)
				{
					String token = _tokens.get(posNext);
					if (token.equals("["))
					{
						depth++;
					}
					else if (token.equals("]"))
					{
						if (--depth <= 0)
						{
							_tokens.set(posNext, "]}");
						}
					}
				}
			}
			// START KGU#388 2017-10-05: Enh. #423 (record export)
			else if (nextToken.equals(".") && posNext+1 < _end && Function.testIdentifier(_tokens.get(posNext+1), null))
			{
				// FIXME: Handle multi-level record access! We might also check type
				_tokens.set(pos, "${" + _varName + "[" + _tokens.get(posNext+1) + "]}");
				_tokens.remove(posNext, posNext+2);
				_end -= 2;
			}
			// END KGU#388 2017-10-05
			else
			{
				_tokens.set(pos, "${" + _varName + "}");
			}
		}
	}

	// END KGU#167 2016-03-30

	// START KGU#101 2015-12-22: Enh. #54 - handling of multiple expressions
	/* (non-Javadoc)
	 * @see lu.fisch.structorizer.generators.Generator#transformInput(java.lang.String)
	 */
	@Override
	protected String transformOutput(String _interm)
	{
		String output = CodeParser.getKeyword("output").trim();
		if (_interm.matches("^" + output + "[ ](.*?)"))
		{
			StringList expressions = 
					Element.splitExpressionList(_interm.substring(output.length()), ",");
			expressions.removeAll(" ");
			_interm = output + " " + expressions.getLongString();
		}
		
		String transformed = super.transformOutput(_interm);
		if (transformed.startsWith("print , "))
		{
			transformed = transformed.replace("print , ", "print ");
		}
		return transformed;
	}
	// END KGU#101 2015-12-22
	
	// START KGU#18/KGU#23 2015-11-02: Most of the stuff became obsolete by subclassing
	protected String transform(String _input)
	{
		String intermed = super.transform(_input);
		
		// START KGU#162 2016-03-31: Enh. #144
		if (!this.suppressTransformation)
		{
		// END KGU#162 2016-03-31
		
			// START KGU 2014-11-16 Support for Pascal-style operators		
			intermed = intermed.replace(" div ", " / ");
			// END KGU 2014-11-06

			// START KGU#78 2015-12-19: Enh. #23: We only have to ensure the correct keywords
			// START KGU#288 2016-11-06: Issue #279 - some JREs don't know method getOrDefault()
			//String preLeave = CodeParser.keywordMap.getOrDefault("preLeave","").trim();
			//String preReturn = CodeParser.keywordMap.getOrDefault("preReturn","").trim();
			//String preExit = CodeParser.keywordMap.getOrDefault("preExit","").trim();
			String preLeave = CodeParser.getKeywordOrDefault("preLeave","leave").trim();
			String preReturn = CodeParser.getKeywordOrDefault("preReturn","return").trim();
			String preExit = CodeParser.getKeywordOrDefault("preExit","exit").trim();
			// END KGU#288 2016-11-06
			if (intermed.matches("^" + Matcher.quoteReplacement(preLeave) + "(\\W.*|$)"))
			{
				intermed = "break " + intermed.substring(preLeave.length());
			}
			else if (intermed.matches("^" + Matcher.quoteReplacement(preReturn) + "(\\W.*|$)"))
			{
				intermed = "return " + intermed.substring(preReturn.length());
			}
			else if (intermed.matches("^" + Matcher.quoteReplacement(preExit) + "(\\W.*|$)"))
			{
				intermed = "exit " + intermed.substring(preExit.length());
			} 
			// END KGU#78 2015-12-19
			
		// START KGU#162 2016-03-31: Enh. #144
		}
		// END KGU#162 2016-03-31
		

		// START KGU#114 2015-12-22: Bugfix #71
		//return _input.trim();
		return intermed.trim();
		// END KGU#114 2015-12-22
	}
	
	/* (non-Javadoc)
	 * Generates a ":" line if the Subqueue contains only empty instructions
	 * @see lu.fisch.structorizer.generators.Generator#generateCode(lu.fisch.structorizer.elements.Subqueue, java.lang.String)
	 */
	protected void generateCode(Subqueue _subqueue, String _indent)
	{
		super.generateCode(_subqueue, _indent);
		if (_subqueue.isNoOp()) {
			addCode(":", _indent, _subqueue.isDisabled());
		}
	}

	
	protected void generateCode(Instruction _inst, String _indent) {
		
		if(!appendAsComment(_inst, _indent)) {
			// START KGU 2014-11-16
			appendComment(_inst, _indent);
			boolean disabled = _inst.isDisabled();
			// END KGU 2014-11-16
			StringList text = _inst.getUnbrokenText(); 
			int nLines = text.count();
			for (int i = 0; i < nLines; i++)
			{
				// START KGU#277/KGU#284 2016-10-13/16: Enh. #270 + Enh. #274
				//code.add(_indent + transform(_inst.getText().get(i)));
				String line = text.get(i);
				// START KGU#653 2019-02-15: Enh. #680 - special treatment for mult-variable input instructions
				StringList inputItems = Instruction.getInputItems(line);
				if (inputItems != null && inputItems.count() > 2) {
					String prompt = inputItems.get(0);
					if (!prompt.isEmpty()) {
						addCode(transform(CodeParser.getKeyword("output") + " " + prompt), _indent, disabled);
					}
					for (int j = 1; j < inputItems.count(); j++) {
						String item = transform(inputItems.get(j) + " <-");
						int posEq = item.lastIndexOf("=");
						if (posEq > 0) {
							item = item.substring(0, posEq);
						}
						inputItems.set(j, item);
					}
					addCode(this.getInputReplacer(false).replace("$1", inputItems.concatenate(" ", 1)), _indent, disabled);
					continue;
				}
				// END KGU#653 2019-02-15
				// START KGU#388/KGU#772 2017-10-24/2019-11-24: Enh. #423/bugfix #784 ignore type definitions and mere variable declarations
				//if (Instruction.isTypeDefinition(line)) {
				if (Instruction.isMereDeclaration(line)) {
					continue;
				}
				// END KGU#388/KGU#772 2017-10-24/2019-11-24
				String codeLine = transform(line);
				// START KGU#311 2017-01-05: Enh. #314: We should at least put some File API remarks
				if (this.usesFileAPI) {
					for (int j = 0; j < Executor.fileAPI_names.length; j++) {
						if (line.contains(Executor.fileAPI_names[j] + "(")) {
							appendComment("TODO File API: Replace the \"" + Executor.fileAPI_names[j] + "\" call by an appropriate shell construct", _indent);
							break;
						}
					}
				}
				// END KGU#311 2017-01-05
				if (Instruction.isTurtleizerMove(line)) {
					codeLine += " " + this.commentSymbolLeft() + " color = " + _inst.getHexColor();
				}
				// START KGU#383 2017-04-18: Bugfix #386 - suppress sole empty line
				//addCode(codeLine, _indent, disabled);
				if (!codeLine.trim().isEmpty() || nLines > 1) {
					addCode(codeLine, _indent, disabled);
				}
				// END KGU#383 2017-04-18
				// END KGU#277/KGU#284 2016-10-13
			}
		}

	}

	protected void generateCode(Alternative _alt, String _indent) {
		
		boolean disabled = _alt.isDisabled();
		if (code.count() > 0 && !code.get(code.count()-1).trim().isEmpty()) {
			addCode("", "", disabled);
		}
		// START KGU 2014-11-16
		appendComment(_alt, _indent);
		// END KGU 2014-11-16
		// START KGU#132 2016-01-08: Bugfix #96 - approach with C-like syntax
		//code.add(_indent+"if "+BString.replace(transform(_alt.getText().getText()),"\n","").trim());
		// START KGU#132 2016-03-24: Bugfix #96/#135 second approach with [[ ]] instead of (( ))
		//code.add(_indent+"if (( "+BString.replace(transform(_alt.getText().getText()),"\n","").trim() + " ))");
		// START KGU#453 2017-11-02: Issue #447
		//String condition = transform(_alt.getText().getLongString()).trim();
		String condition = transform(_alt.getUnbrokenText().getLongString()).trim();
		// END KGU#453 2017-11-02
		// START KGU#311 2017-01-05: Enh. #314: We should at least put some File API remarks
		if (this.usesFileAPI) {
			for (int j = 0; j < Executor.fileAPI_names.length; j++) {
				if (condition.contains(Executor.fileAPI_names[j] + "(")) {
					appendComment("TODO File API: Replace the \"" + Executor.fileAPI_names[j] + "\" call by an appropriate shell construct", _indent);
					break;
				}
			}
		}
		// END KGU#311 2017-01-05
		// START KGU#277 2016-10-13: Enh. #270
		//code.add(_indent + "if " + condition);
		addCode("if " + finishCondition(condition), _indent, disabled);
		// END KGU#277 2016-10-13
		// END KGU#132 2016-03-24
		// END KGU#131 2016-01-08
		// START KGU#277 2016-10-13: Enh. #270
		//code.add(_indent+"then");
		addCode("then", _indent, disabled);
		// END KGU#277 2016-10-13
		generateCode(_alt.qTrue,_indent+this.getIndent());
		
		if(_alt.qFalse.getSize()!=0) {
			
			// START KGU#277 2016-10-13: Enh. #270
			//code.add(_indent+"");
			//code.add(_indent+"else");			
			if (!code.get(code.count()-1).trim().isEmpty()) {
				addCode("", "", disabled);
			}
			addCode("else", _indent, disabled);			
			// END KGU#277 2016-10-13
			generateCode(_alt.qFalse,_indent+this.getIndent());
			
		}
		
		// START KGU#277 2016-10-13: Enh. #270
		//code.add(_indent+"fi");
		//code.add("");
		addCode("fi", _indent, disabled);
		addCode("", "", disabled);
		// END KGU#277 2016-10-13
		
	}
	
	protected void generateCode(Case _case, String _indent) {
		
		boolean disabled = _case.isDisabled();
		if (code.count() > 0 && !code.get(code.count()-1).trim().isEmpty()) {
			addCode("", "", disabled);
		}
		// START KGU 2014-11-16
		appendComment(_case, _indent);
		// END KGU 2014-11-16
		// START KGU#277 2016-10-14: Enh. #270
		//code.add(_indent+"case "+transform(_case.getText().get(0))+" in");
		// START KGU#453 2017-11-02: Issue #447
		//addCode("case "+transform(_case.getText().get(0))+" in", _indent, disabled);
		StringList unbrokenText = _case.getUnbrokenText();
		addCode("case "+transform(unbrokenText.get(0))+" in", _indent, disabled);
		// END KGU#453 2017-11-02
		// END KGU#277 2016-10-14
		
		for (int i=0; i<_case.qs.size()-1; i++)
		{
			// START KGU#277 2016-10-14: Enh. #270
			//code.add("");
			//code.add(_indent + this.getIndent() + _case.getText().get(i+1).trim().replace(",", "|") + ")");
			addCode("", "", disabled);
			// START KGU#453 2017-11-02: Issue #447
			//addCode(this.getIndent() + _case.getText().get(i+1).trim().replace(",", "|") + ")", _indent, disabled);
			// START KGU#755 2019-11-08: Bugfix #769 - more precise splitting necessary
			//addCode(this.getIndent() + unbrokenText.get(i+1).trim().replace(",", "|") + ")", _indent, disabled);
			StringList items = Element.splitExpressionList(unbrokenText.get(i+1).trim(), ",");
			addCode(this.getIndent() + items.concatenate("|") + ")", _indent, disabled);
			// END KGU#755 2019-11-08
			// END KGU#453 2017-11-02
			// END KGU#277 2016-10-14
			// START KGU#15 2015-11-02
			generateCode((Subqueue) _case.qs.get(i),_indent+this.getIndent()+this.getIndent()+this.getIndent());
			addCode(";;", _indent + this.getIndent(), disabled);
		}
		
		if(!_case.getText().get(_case.qs.size()).trim().equals("%"))
		{
			addCode("", "", disabled);
			addCode("*)", _indent+this.getIndent(), disabled);
			generateCode((Subqueue) _case.qs.get(_case.qs.size()-1),_indent+this.getIndent()+this.getIndent());
			addCode(";;", _indent+this.getIndent(), disabled);
		}
		addCode("esac", _indent, disabled);
		addCode("", "", disabled);
	}
	
	
	protected void generateCode(For _for, String _indent) {

		// START KGU#277 2016-10-13: Enh. #270
		boolean disabled = _for.isDisabled(); 
		// END KGU#277 2016-10-13
		if (code.count() > 0 && !code.get(code.count()-1).trim().isEmpty()) {
			addCode("", "", disabled);
		}
		// START KGU 2014-11-16
		appendComment(_for, _indent);
		// END KGU 2014-11-16
		// START KGU#30 2015-10-18: This resulted in nonsense if the algorithm was a real counting loop
		// We now use C-like syntax  for ((var = sval; var < eval; var=var+incr)) ...
		// START KGU#3 2015-11-02: And now we have a competent splitting mechanism...
		String counterStr = _for.getCounterVar();
		//START KGU#61 2016-03-21: Enh. #84/#135 - FOR-IN support
		if (_for.isForInLoop())
		{
			String valueList = _for.getValueList();
			if (!this.suppressTransformation)
			{
				StringList items = null;
				// Convert an array initializer to a space-separated sequence
				if (valueList.startsWith("{") && valueList.endsWith("}") &&
						!valueList.contains(".."))	// Preserve ranges like {3..18} or {1..200..2}
				{
					items = Element.splitExpressionList(
							valueList.substring(1, valueList.length()-1), ",");
				}
				// Convert a comma-separated list to a space-separated sequence
				else if (valueList.contains(","))
				{
					items = Element.splitExpressionList(valueList, ",");				
				}
				if (items != null)
				{
					valueList = transform(items.getLongString());
				}
				else if (varNames.contains(valueList))
				{
					// Must be an array variable
					valueList = "${" + valueList + "[@]}";
				}
				else
				{
					valueList = transform(valueList);
				}
			}
			// START KGU#277 2016-10-13: Enh. #270
			//code.add(_indent + "for " + counterStr + " in " + valueList);
			addCode("for " + counterStr + " in " + valueList, _indent, disabled);
			// END KGU#277 2016-10-13
		}
		else // traditional COUNTER loop
		{
		// END KGU#61 2016-03-21
			// START KGU#129 2016-01-08: Bugfix #96: Expressions must be transformed
			//String startValueStr = _for.getStartValue();
			//String endValueStr = _for.getEndValue();
			String startValueStr = transform(_for.getStartValue());
			String endValueStr = transform(_for.getEndValue());
			// END KGU#129 2016-01-08
			int stepValue = _for.getStepConst();
			String incrStr = counterStr + "++";
			if (stepValue == -1) {
				incrStr = counterStr + "--";
			}
			else if (stepValue != 1) {
				// START KGU#129 2016-01-08: Bugfix #96 - prefix variables
				incrStr = "(( " + counterStr + "=$" + counterStr + "+(" + stepValue + ") ))";
			}
			// END KGU#3 2015-11-02
			// START KGU#277 2016-10-13: Enh. #270
			//code.add(_indent+"for (( "+counterStr+"="+startValueStr+"; "+
			//		counterStr + ((stepValue > 0) ? "<=" : ">=") + endValueStr + "; " +
			//		incrStr + " ))");
			addCode("for (( "+counterStr+"="+startValueStr+"; "+
					counterStr + ((stepValue > 0) ? "<=" : ">=") + endValueStr + "; " +
					incrStr + " ))", _indent, disabled);
			// END KGU#277 2016-10-13
			// END KGU#30 2015-10-18
		// START KGU#61 2016-03-21: Enh. #84/#135 (continued)
		}
		// END KGU#61 2016-03-21
		// START KGU#277 2016-10-14: Enh. #270
		//code.add(_indent+"do");
		//generateCode(_for.q,_indent+this.getIndent());
		//code.add(_indent+"done");	
		//code.add("");
		addCode("do", _indent, disabled);
		generateCode(_for.q,_indent+this.getIndent());
		addCode("done", _indent, disabled);	
		addCode("", "", disabled);
		// END KGU#277 2016-10-14

	}
	protected void generateCode(While _while, String _indent) {
		
		// START KGU#277 2016-10-14: Enh. #270
		boolean disabled = _while.isDisabled();
		// END KGU#277 2016-10-14
		if (code.count() > 0 && !code.get(code.count()-1).trim().isEmpty()) {
			addCode("", "", disabled);
		}
		// START KGU 2014-11-16
		appendComment(_while, _indent);
		// END KGU 2014-11-16
		// START KGU#132 2016-01-08: Bugfix #96 first approach with C-like syntax (( ))
		//code.add(_indent+"while " + transform(_while.getText().getLongString()));
		// START KGU#132 2016-03-24: Bugfix #96/#135 second approach with [[ ]] instead of (( ))
		//code.add(_indent+"while (( " + transform(_while.getText().getLongString()) + " ))");
		// START KGU#132/KGU#162 2016-03-31: Bugfix #96 + Enh. #144
		//code.add(_indent+"while [[ " + transform(_while.getText().getLongString()).trim() + " ]]");
		String condition = transform(_while.getText().getLongString()).trim();
		// START KGU#311 2017-01-05: Enh. #314: We should at least put some File API remarks
		if (this.usesFileAPI) {
			for (int j = 0; j < Executor.fileAPI_names.length; j++) {
				if (condition.contains(Executor.fileAPI_names[j] + "(")) {
					appendComment("TODO File API: Replace the \"" + Executor.fileAPI_names[j] + "\" call by an appropriate shell construct", _indent);
					break;
				}
			}
		}
		// END KGU#311 2017-01-05
		// START KGU#277 2016-10-14: Enh. #270
		//code.add(_indent + "while " + condition);
		addCode("while " + this.finishCondition(condition), _indent, disabled);
		// END KGU#277 2016-10-14
		// END KGU#132/KGU#144 2016-03-31
		// END KGU#132 2016-03-24
		// END KGU#132 2016-01-08
		// START KGU#277 2016-10-14: Enh. #270
		//code.add(_indent+"do");
		//generateCode(_while.q,_indent+this.getIndent());
		//code.add(_indent+"done");
		//code.add("");
		addCode("do", _indent, disabled);
		generateCode(_while.q,_indent+this.getIndent());
		addCode("done", _indent, disabled);
		addCode("", "", disabled);
		// END KGU#277 2016-10-14
		
	}
	
	protected void generateCode(Repeat _repeat, String _indent) {
		
		// START KGU#277 2016-10-14: Enh. #270
		boolean disabled = _repeat.isDisabled();
		// END KGU#277 2016-10-14
		if (code.count() > 0 && !code.get(code.count()-1).trim().isEmpty()) {
			addCode("", "", disabled);
		}
		// START KGU 2014-11-16
		appendComment(_repeat, _indent);
		// END KGU 2014-11-16
		// START KGU#60 2015-11-02: The do-until loop is not equivalent to a Repeat element: We must
		// generate the loop body twice to preserve semantics!
		appendComment("NOTE: This is an automatically inserted copy of the loop body below.", _indent);
		generateCode(_repeat.q, _indent);		
		// END KGU#60 2015-11-02
		// START KGU#131 2016-01-08: Bugfix #96 first approach with C-like syntax
		//code.add(_indent + "until " + transform(_repeat.getText().getLongString()).trim());
		// START KGU#132 2016-03-24: Bugfix #96/#135 second approach with [[ ]] instead of (( ))
		//code.add(_indent + "until (( " + transform(_repeat.getText().getLongString()).trim() + " ))");
		// START KGU#132/KGU#162 2016-03-31: Bugfix #96 + Enh. #144
		//code.add(_indent + "until [[ " + transform(_repeat.getText().getLongString()).trim() + " ]]");
		String condition = transform(_repeat.getText().getLongString()).trim();
		// START KGU#311 2017-01-05: Enh. #314: We should at least put some File API remarks
		if (this.usesFileAPI) {
			for (int j = 0; j < Executor.fileAPI_names.length; j++) {
				if (condition.contains(Executor.fileAPI_names[j] + "(")) {
					appendComment("TODO File API: Replace the \"" + Executor.fileAPI_names[j] + "\" call by an appropriate shell construct", _indent);
					break;
				}
			}
		}
		// END KGU#311 2017-01-05
		// START KGU#277 2016-10-14: Enh. #270
		//code.add(_indent + "while " + condition);
		addCode("while " + this.finishCondition(condition), _indent, disabled);
		// END KGU#277 2016-10-14
		// END KGU#132/KGU#144 2016-03-31
		// END KGU#132 2016-03-24
		// END KGU#131 2016-01-08
		// START KGU#277 2016-10-14: Enh. #270
		//code.add(_indent + "do");
		//generateCode(_repeat.q, _indent + this.getIndent());
		//code.add(_indent + "done");
		//code.add("");
		addCode("do", _indent, disabled);
		generateCode(_repeat.q, _indent + this.getIndent());
		addCode("done", _indent, disabled);
		addCode("", "", disabled);
		// END KGU#277 2016-10-14
		
	}

	protected void generateCode(Forever _forever, String _indent) {
		
		// START KGU#277 2016-10-14: Enh. #270
		//code.add("");
		boolean disabled = _forever.isDisabled();
		if (code.count() > 0 && !code.get(code.count()-1).trim().isEmpty()) {
			addCode("", "", disabled);
		}
		// END KGU#277 2016-10-14
		// START KGU 2014-11-16
		appendComment(_forever, _indent);
		// END KGU 2014-11-16
		// START KGU#277 2016-10-14: Enh. #270
		//code.add(_indent + "while [ 1 ]");
		//code.add(_indent + "do");
		//generateCode(_forever.q, _indent + this.getIndent());
		//code.add(_indent + "done");
		//code.add("");
		addCode("while [ 1 ]", _indent, disabled);
		addCode("do", _indent, disabled);
		generateCode(_forever.q, _indent + this.getIndent());
		addCode("done", _indent, disabled);
		addCode("", "", disabled);
		// END KGU#277 2016-10-14
		
	}
	
	protected void generateCode(Call _call, String _indent) {
		if(!appendAsComment(_call, _indent)) {
			// START KGU 2014-11-16
			appendComment(_call, _indent);
			// END KGU 2014-11-16
			// START KGU#277 2016-10-14: Enh. #270
			boolean disabled = _call.isDisabled();
			// END KGU#277 2016-10-14
			for (int i = 0; i < _call.getText().count(); i++)
			{
				// START KGU#277 2016-10-14: Enh. #270
				//code.add(_indent+transform(_call.getText().get(i)));
				addCode(transform(_call.getText().get(i)), _indent, disabled);
				// END KGU#277 2016-10-14
			}
		}
	}
	
	protected void generateCode(Jump _jump, String _indent) {
		if(!appendAsComment(_jump, _indent)) {
			// START KGU 2014-11-16
			appendComment(_jump, _indent);
			// END KGU 2014-11-16
			// START KGU#277 2016-10-14: Enh. #270
			boolean disabled = _jump.isDisabled();
			// END KGU#277 2016-10-14
			for(int i=0;i<_jump.getText().count();i++)
			{
				// FIXME (KGU 2016-03-25): Handle the kinds of exiting jumps!
				// START KGU#277 2016-10-14: Enh. #270
				//code.add(_indent+transform(_jump.getText().get(i)));
				addCode(transform(_jump.getText().get(i)), _indent, disabled);
				// END KGU#277 2016-10-14
			}
		}
	}
	
	// START KGU#174 2016-04-05: Issue #153 - export had been missing
	protected void generateCode(Parallel _para, String _indent)
	{
		// START KGU#277 2016-10-14: Enh. #270
		boolean disabled = _para.isDisabled();
		// END KGU#277 2016-10-14
		appendComment(_para, _indent);
		appendComment("==========================================================", _indent);
		appendComment("================= START PARALLEL SECTION =================", _indent);
		appendComment("==========================================================", _indent);
		String indent1 = _indent + this.getIndent();
		String varName = "pids" + Integer.toHexString(_para.hashCode());
		// START KGU#277 2016-10-14: Enh. #270
		//code.add(_indent + varName + "=\"\"");
		addCode(varName + "=\"\"", _indent , disabled);
		// END KGU#277 2016-10-14
		for (Subqueue q : _para.qs)
		{
			// START KGU#277 2016-10-14: Enh. #270
			//code.add(_indent + "(");
			//generateCode(q, indent1);
			//code.add(_indent + ") &");
			//code.add(_indent + varName + "=\"${" + varName + "} $!\"");
			addCode("(", _indent, disabled);
			generateCode(q, indent1);
			addCode(") &", _indent, disabled);
			addCode(varName + "=\"${" + varName + "} $!\"", _indent, disabled);
			// END KGU#277 2016-10-14
		}
		// START KGU#277 2016-10-14: Enh. #270
		addCode("wait ${" + varName + "}", _indent, disabled);
		// END KGU#277 2016-10-14
		appendComment("==========================================================", _indent);
		appendComment("================== END PARALLEL SECTION ==================", _indent);
		appendComment("==========================================================", _indent);
	}
	// END KGU#174 2016-04-05
	
//	public void generateCode(Try _try, String _indent)
//	{
//		// TODO
//		// That is what we might achieve
//		{ # try
//
//		    command1 &&
//		    #save your output
//
//		} || { # catch
//		    # save log for exception 
//		}
//	}

	// TODO: Decompose this - Result mechanism is missing!
	public String generateCode(Root _root, String _indent) {
		
		// START KGU#405 2017-05-19: Issue #237
		// START KGU#676 2019-03-30: Enh. #696 special pool in case of batch export
		//typeMap = _root.getTypeInfo();
		typeMap = _root.getTypeInfo(routinePool);
		// END KGU#676 2019-03-30
		// START KGU#705 2019-09-23: Enh. #738
		int line0 = code.count();
		if (codeMap!= null) {
			// register the triple of start line no, end line no, and indentation depth
			// (tab chars count as 1 char for the text positioning!)
			codeMap.put(_root, new int[]{line0, line0, _indent.length()});
		}
		// END KGU#705 2019-09-23
		// END KGU#405 2017-05-19
		if (topLevel)
		{
			code.add("#!/bin/bash");
			// STARTB KGU#351 2017-02-26: Enh. #346
			this.appendUserIncludes("");
			// END KGU#351 2017-02-26
		}
		code.add("");

		// START KGU 2014-11-16
		appendComment(_root, _indent);
		// END KGU 2014-11-16
		String indent = _indent;
		if (topLevel)
		{
			appendComment("(generated by Structorizer " + Element.E_VERSION + ")", indent);
			// START KGU#363 2017-05-16: Enh. #372
			appendCopyright(_root, _indent, true);
			// END KGU#363 2017-05-16

			// START KGU#311 2017-01-05: Enh. #314: We should at least put some File API remarks
			if (this.usesFileAPI) {
				code.add(_indent);
				appendComment("TODO The exported algorithms made use of the Structorizer File API.", _indent);
				appendComment("     Unfortunately there are no comparable constructs in shell", _indent);
				appendComment("     syntax for automatic conversion.", _indent);
				appendComment("     The respective lines are marked with a TODO File API comment.", _indent);
				appendComment("     You might try something like \"echo value >> filename\" for output", _indent);
				appendComment("     or \"while ... do ... read var ... done < filename\" for input.", _indent);
			}
			// END KGU#311 2017-01-05
			// START KGU#150 2016-04-05: Provisional support for chr and ord functions
			// START KGU#241 2016-09-01: Issue #234 - Mechanism to introduce these definitions on demand only
//			code.add(indent);
//			insertComment("chr() - converts decimal value to its ASCII character representation", indent);
//			code.add(indent + "chr() {");
//			code.add(indent + this.getIndent() + "printf \\\\$(printf '%03o' $1)");
//			code.add(indent + "}");
//			insertComment("ord() - converts ASCII character to its decimal value", indent);
//			code.add(indent + "ord() {");
//			code.add(indent + this.getIndent() + "printf '%d' \"'$1\"");
//			code.add(indent + "}");
//			code.add(indent);
			if (!this.suppressTransformation)
			{
				boolean builtInAdded = false;
				if (occurringFunctions.contains("chr"))
				{
			code.add(indent);
			appendComment("chr() - converts decimal value to its ASCII character representation", indent);
			code.add(indent + "chr() {");
			code.add(indent + this.getIndent() + "printf \\\\$(printf '%03o' $1)");
			code.add(indent + "}");
					builtInAdded = true;
				}
				if (occurringFunctions.contains("ord"))
				{
					code.add(indent);
			appendComment("ord() - converts ASCII character to its decimal value", indent);
			code.add(indent + "ord() {");
			code.add(indent + this.getIndent() + "printf '%d' \"'$1\"");
			code.add(indent + "}");
					builtInAdded = true;
				}
				if (builtInAdded) code.add(indent);
			}
			// END KGU#241 2016-09-01
			// END KGU#150 2016-04-05
			
			subroutineInsertionLine = code.count();
		}
		
		if( _root.isSubroutine() ) {
			// START KGU#53 2015-10-18: Shell functions get their arguments via $1, $2 etc.
			//code.add(_root.getText().get(0)+" () {");
			String header = _root.getMethodName() + "()";
			code.add(header + " {");
			indent = indent + this.getIndent();
			StringList paraNames = _root.getParameterNames();
			// START KGU#371 2019-03-08: Enh. #385 support optional arguments
			//for (int i = 0; i < paraNames.count(); i++)
			//{
			//	code.add(indent + paraNames.get(i) + "=$" + (i+1));
			//}
			int minArgs = _root.getMinParameterCount();
			StringList argDefaults = _root.getParameterDefaults();
			for (int i = 0; i < minArgs; i++)
			{
				code.add(indent + paraNames.get(i) + "=$" + (i+1));
			}
			for (int i = minArgs; i < paraNames.count(); i++)
			{
				code.add(indent + "if [ $# -lt " + (i+1) + " ]");
				code.add(indent + "then");
				code.add(indent + this.getIndent() + paraNames.get(i) + "=" + transform(argDefaults.get(i)));
				code.add(indent + "else");
				code.add(indent + this.getIndent() + paraNames.get(i) + "=$" + (i+1));
				code.add(indent + "fi");
			}
			// END KGU#371 2019-03-08
			// END KGU#53 2015-10-18
		} else {				
			code.add("");
		}
		
		code.add("");
		// START KGU#129 2016-01-08: Bugfix #96 - Now fetch all variable names from the entire diagram
		varNames = _root.retrieveVarNames();
		appendComment("TODO: Check and revise the syntax of all expressions!", _indent);
		code.add("");
		// END KGU#129 2016-01-08
		// START KGU#542 2019-12-01: Enh. #739 - support for enumeration types
		for (Entry<String, TypeMapEntry> typeEntry: typeMap.entrySet()) {
			TypeMapEntry type = typeEntry.getValue();
			if (typeEntry.getKey().startsWith(":") && type != null && type.isEnum()) {
				appendEnumeratorDef(type, _indent);
			}
		}
		// END KGU#542 2019-12-01
		// START KGU#389 2017-10-23: Enh. #423 declare records as associative arrays
		// FIXME: We should only do so if they won't get initialized
		for (int i = 0; i < varNames.count(); i++) {
			String varName = varNames.get(i);
			TypeMapEntry typeEntry = typeMap.get(varName);
			if (typeEntry != null && typeEntry.isRecord()) {
				addCode(this.getAssocDeclarator() + varName, _indent, false);
			}
		}
		// END KGU#389 2017-10-23
		generateCode(_root.children, indent);
		
		if (_root.isSubroutine()) {
			code.add("}");
		}
		
		// START KGU#705 2019-09-23: Enh. #738
		if (codeMap != null) {
			// Update the end line no relative to the start line no
			codeMap.get(_root)[1] += (code.count() - line0);
		}
		// END KGU#705 2019-09-23

		return code.getText();
		
	}

	// START KGU#542 2019-12-01: Enh. #739 support for enumeration types
	/**
	 * Generates a shell equivalent for an enumeration type by declaring the
	 * respective set of read-only integer variables.
	 * @param _type - the {@link TpyeMapEntry} of the enumeration type
	 * @param _indent - the current indentation string
	 */
	protected void appendEnumeratorDef(TypeMapEntry _type, String _indent) {
		StringList enumItems = _type.getEnumerationInfo();
		appendComment("START enumeration type " + _type.typeName, _indent);
		// In vintage BASIC, we will just generate separate variable definitions
		int offset = 0;
		String lastVal = "";
		for (int i = 0; i < enumItems.count(); i++) {
			String[] itemSpec = enumItems.get(i).split("=", 2);
			if (itemSpec.length > 1) {
				lastVal = itemSpec[1].trim();
				offset = 0;
				try {
					int code = Integer.parseUnsignedInt(lastVal);
					lastVal = "";
					offset = code;
				}
				catch (NumberFormatException ex) {}
			}
			addCode(this.getEnumDeclarator() + itemSpec[0] + "=" + transform(lastVal) + (lastVal.isEmpty() ? "" : "+") + offset, _indent, false);
			offset++;
		}
		appendComment("END enumeration type "+ _type.typeName, _indent);
	}
	// END KGU#542 2019-12-01
	
}


