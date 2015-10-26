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

package lu.fisch.structorizer.generators;

/******************************************************************************************************
 *
 *      Author:         Bob Fisch
 *
 *      Description:    This class generates PAscal code.
 *
 ******************************************************************************************************
 *
 *      Revision List
 *
 *      Author              Date            Description
 *      ------              ----            -----------
 *      Bob Fisch           2007.12.27      First Issue
 *      Bob Fisch           2008.04.12      Added "Fields" section for generator to be used as plugin
 *      Bob Fisch           2008.11.17      Added Freepascal extensions
 *      Bob Fisch           2009.08.17      Bugfixes (see comment)
 *      Bob Fisch           2011.11.07      Fixed an issue while doing replacements
 *      Kay Gürtzig         2014.11.10      Conversion of C-like logical operators
 *      Kay Gürtzig         2014.11.16      Conversion of C-like comparison operator, comment export
 *      Kay Gürtzig         2014.12.02      Additional replacement of long assignment operator "<--" by "<-"
 *
 ******************************************************************************************************
 *
 *      Comments:
 *      2014.11.16 - Bugfix / Enhancement
 *      - Conversion of C-style unequality operator had to be added
 *      - Comments are now exported, too
 *       
 *      2014.11.10 - Enhancement
 *      - Conversion of C-style logical operators to the Pascal-like ones added
 *      - assignment operator conversion now preserves or ensures surrounding spaces
 *
 *      2009.08.17 - Bugfixes
 *      - added automatic brackets for "while", "switch", "repeat" & "if"
 *
 ******************************************************************************************************///

import lu.fisch.utils.*;
import lu.fisch.structorizer.parsers.*;
import lu.fisch.structorizer.elements.*;


public class PasGenerator extends Generator 
{
	
    /************ Fields ***********************/
    @Override
    protected String getDialogTitle()
    {
            return "Export Pascal Code ...";
    }

    @Override
    protected String getFileDescription()
    {
            return "Pascal / Delphi Source Code";
    }

    @Override
    protected String getIndent()
    {
            return "  ";
    }

    @Override
    protected String[] getFileExtensions()
    {
            String[] exts = {"pas", "dpr", "pp", "lpr"};
            return exts;
    }

    /************ Code Generation **************/
    private String transform(String _input)
    {
            // et => and
            // ou => or
            // lire => readln()
            // écrire => writeln()
            // tant que => ""
            // pour => ""
            // jusqu'à => ""
            // à => "to"

            // START KGU 2014-12-02: To achieve consistency with operator highlighting
            _input=BString.replace(_input, "<--", "<-");
            // END KGU 2014-12-02
            _input=BString.replace(_input," <- "," := ");
            _input=BString.replace(_input,"<- "," := ");
            _input=BString.replace(_input," <-"," := ");
            _input=BString.replace(_input,"<-"," := ");

            // START KGU 2014-11-16: C comparison operator required transformation, too
            _input=BString.replace(_input,"!=","<>");
            // END KGU 2014-11-16
            // START KGU 2014-11-10: logical operators required transformation, too
            _input=BString.replace(_input," && "," and ");
            _input=BString.replace(_input," || "," or ");
            _input=BString.replace(_input," ! "," not ");
            _input=BString.replace(_input,"&&"," and ");
            _input=BString.replace(_input,"||"," or ");
            _input=BString.replace(_input,"!"," not ");
            // END KGU 2014-11-10
            // START KGU 2014-11-16: C bit operators required transformation, too
            _input=BString.replace(_input," ~ "," not ");
            _input=BString.replace(_input," & "," and ");
            _input=BString.replace(_input," | "," or ");
            _input=BString.replace(_input,"~"," not ");
            _input=BString.replace(_input,"&"," and ");
            _input=BString.replace(_input,"|"," or ");
            _input=BString.replace(_input," << "," shl ");
            _input=BString.replace(_input," >> "," shr ");
            _input=BString.replace(_input,"<<"," shl ");
            _input=BString.replace(_input,">>"," shr ");
            // END KGU 2014-11-16
            
            StringList empty = new StringList();
            empty.addByLength(D7Parser.preAlt);
            empty.addByLength(D7Parser.postAlt);
            empty.addByLength(D7Parser.preCase);
            empty.addByLength(D7Parser.postCase);
            empty.addByLength(D7Parser.preFor);
            empty.addByLength(D7Parser.preWhile);
            empty.addByLength(D7Parser.postWhile);
            empty.addByLength(D7Parser.postRepeat);
            empty.addByLength(D7Parser.preRepeat);
            //System.out.println(empty);
            for(int i=0;i<empty.count();i++)
            {
                _input=BString.replace(_input,empty.get(i),"");
                //System.out.println(_input);
                //System.out.println(i);
            }
            if(!D7Parser.postFor.equals("")){_input=BString.replace(_input,D7Parser.postFor,"to");}

            
/*
            if(!D7Parser.preAlt.equals("")){_input=BString.replace(_input,D7Parser.preAlt,"");}
            if(!D7Parser.postAlt.equals("")){_input=BString.replace(_input,D7Parser.postAlt,"");}
            if(!D7Parser.preCase.equals("")){_input=BString.replace(_input,D7Parser.preCase,"");}
            if(!D7Parser.postCase.equals("")){_input=BString.replace(_input,D7Parser.postCase,"");}
            if(!D7Parser.preFor.equals("")){_input=BString.replace(_input,D7Parser.preFor,"");}
            if(!D7Parser.postFor.equals("")){_input=BString.replace(_input,D7Parser.postFor,"to");}
            if(!D7Parser.preWhile.equals("")){_input=BString.replace(_input,D7Parser.preWhile,"");}
            if(!D7Parser.postWhile.equals("")){_input=BString.replace(_input,D7Parser.postWhile,"");}
            if(!D7Parser.preRepeat.equals("")){_input=BString.replace(_input,D7Parser.preRepeat,"");}
            if(!D7Parser.postRepeat.equals("")){_input=BString.replace(_input,D7Parser.postRepeat,"");}
*/

            /*Regex r;
            r = new Regex(BString.breakup(D7Parser.input)+"[ ](.*?)","readln($1)"); _input=r.replaceAll(_input);
            r = new Regex(BString.breakup(D7Parser.output)+"[ ](.*?)","writeln($1)"); _input=r.replaceAll(_input);
            r = new Regex(BString.breakup(D7Parser.input)+"(.*?)","readln($1)"); _input=r.replaceAll(_input);
            r = new Regex(BString.breakup(D7Parser.output)+"(.*?)","writeln($1)"); _input=r.replaceAll(_input);*/


            if(!D7Parser.input.equals("")&&_input.indexOf(D7Parser.input+" ")>=0){_input=BString.replace(_input,D7Parser.input+" ","readln(")+")";}
            if(!D7Parser.output.equals("")&&_input.indexOf(D7Parser.output+" ")>=0){_input=BString.replace(_input,D7Parser.output+" ","writeln(")+")";}
            if(!D7Parser.input.equals("")&&_input.indexOf(D7Parser.input)>=0){_input=BString.replace(_input,D7Parser.input,"readln(")+")";}
            if(!D7Parser.output.equals("")&&_input.indexOf(D7Parser.output)>=0){_input=BString.replace(_input,D7Parser.output,"writeln(")+")";}

            return _input.trim();
    }

    @Override
    protected void generateCode(Instruction _inst, String _indent)
    {
            // START KGU 2014-11-16
            insertComment(_inst, _indent, "{ ", " }");
            // END KGU 2014-11-16
            for(int i=0;i<_inst.getText().count();i++)
            {
                    code.add(_indent+transform(_inst.getText().get(i))+";");
            }
    }

    @Override
    protected void generateCode(Alternative _alt, String _indent)
    {
            // START KGU 2014-11-16
            insertComment(_alt, _indent, "{ ", " }");
            // END KGU 2014-11-16
            
            String condition = BString.replace(transform(_alt.getText().getText()),"\n","").trim();
            if(!condition.startsWith("(") && !condition.endsWith(")")) condition="("+condition+")";

            code.add(_indent+"if "+condition+" then");
            code.add(_indent+"begin");
            generateCode(_alt.qTrue,_indent+_indent.substring(0,1));
            if(_alt.qFalse.getSize()!=0)
            {
                    code.add(_indent+"end");
                    code.add(_indent+"else");
                    code.add(_indent+"begin");
                    generateCode(_alt.qFalse,_indent+_indent.substring(0,1));
            }
            code.add(_indent+"end;");
    }

    @Override
    protected void generateCode(Case _case, String _indent)
    {
            // START KGU 2014-11-16
            insertComment(_case, _indent, "{ ", " }");
            // END KGU 2014-11-16

            String condition = transform(_case.getText().get(0));
            if(!condition.startsWith("(") && !condition.endsWith(")")) condition="("+condition+")";

            code.add(_indent+"case "+condition+" of");

            for(int i=0;i<_case.qs.size()-1;i++)
            {
                    code.add(_indent+_indent.substring(0,1)+_case.getText().get(i+1).trim()+":");
                    code.add(_indent+_indent.substring(0,1)+_indent.substring(0,1)+"begin");
                    generateCode((Subqueue) _case.qs.get(i),_indent+_indent.substring(0,1)+_indent.substring(0,1)+_indent.substring(0,1));
                    code.add(_indent+_indent.substring(0,1)+_indent.substring(0,1)+"end;");
            }

            if(!_case.getText().get(_case.qs.size()).trim().equals("%"))
            {
                    code.add(_indent+_indent.substring(0,1)+"else");
                    generateCode((Subqueue) _case.qs.get(_case.qs.size()-1),_indent+_indent.substring(0,1)+_indent.substring(0,1));
            }
            code.add(_indent+_indent.substring(0,1)+"end;");
    }

    @Override
    protected void generateCode(For _for, String _indent)
    {
            // START KGU 2014-11-16
            insertComment(_for, _indent, "{ ", " }");
            // END KGU 2014-11-16

            code.add(_indent+"for "+BString.replace(transform(_for.getText().getText()),"\n","").trim()+" do");
            code.add(_indent+"begin");
            generateCode(_for.q,_indent+_indent.substring(0,1));
            code.add(_indent+"end;");
    }

    @Override
    protected void generateCode(While _while, String _indent)
    {
            // START KGU 2014-11-16
            insertComment(_while, _indent, "{ ", " }");
            // END KGU 2014-11-16

            String condition = BString.replace(transform(_while.getText().getText()),"\n","").trim();
            if(!condition.startsWith("(") && !condition.endsWith(")")) condition="("+condition+")";

            code.add(_indent+"while "+condition+" do");
            code.add(_indent+"begin");
            generateCode(_while.q,_indent+_indent.substring(0,1));
            code.add(_indent+"end;");
    }

    @Override
    protected void generateCode(Repeat _repeat, String _indent)
    {
            // START KGU 2014-11-16
            insertComment(_repeat, _indent, "{ ", " }");
            // END KGU 2014-11-16

            String condition = BString.replace(transform(_repeat.getText().getText()),"\n","").trim();
            if(!condition.startsWith("(") && !condition.endsWith(")")) condition="("+condition+")";

            code.add(_indent+"repeat");
            generateCode(_repeat.q,_indent+_indent.substring(0,1));
            code.add(_indent+"until "+condition+";");
    }

    @Override
    protected void generateCode(Forever _forever, String _indent)
    {
            // START KGU 2014-11-16
            insertComment(_forever, _indent, "{ ", " }");
            // END KGU 2014-11-16

            code.add(_indent+"while (true) do");
            code.add(_indent+"begin");
            generateCode(_forever.q,_indent+_indent.substring(0,1));
            code.add(_indent+"end;");
    }
	
    @Override
    protected void generateCode(Call _call, String _indent)
    {
            // START KGU 2014-11-16
            insertComment(_call, _indent, "{ ", " }");
            // END KGU 2014-11-16

            for(int i=0;i<_call.getText().count();i++)
            {
                    code.add(_indent+transform(_call.getText().get(i))+";");
            }
    }

    @Override
    protected void generateCode(Jump _jump, String _indent)
    {
            // START KGU 2014-11-16
            insertComment(_jump, _indent, "{ ", " }");
            // END KGU 2014-11-16

            for(int i=0;i<_jump.getText().count();i++)
            {
                    code.add(_indent+transform(_jump.getText().get(i))+";");
            }
    }

    @Override
    protected void generateCode(Subqueue _subqueue, String _indent)
    {
            // code.add(_indent+"");
            for(int i=0;i<_subqueue.children.size();i++)
            {
                    generateCode((Element) _subqueue.children.get(i),_indent);
            }
            // code.add(_indent+"");
    }
	
    @Override
    public String generateCode(Root _root, String _indent)
    {
            String pr = "program";
            if(_root.isProgram==false) {pr="function";}

            // START KGU 2014-11-16
            insertComment(_root, "", "{ ", " }");
            // END KGU 2014-11-16

            code.add(pr+" "+_root.getText().get(0)+";");
            code.add("");
            // START KGU 2014-11-16: comment syntax corrected
            //code.add("// declare your variables here");
            code.add("{ declare your variables here }");
            // END KGU 2014-11-16
            code.add("");
            code.add("begin");
            generateCode(_root.children,_indent);
            code.add("end.");

            return code.getText();
    }
	
	
}