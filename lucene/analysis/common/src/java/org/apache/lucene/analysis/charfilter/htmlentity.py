begin_unit
comment|'# Licensed to the Apache Software Foundation (ASF) under one or more'
nl|'\n'
comment|'# contributor license agreements.  See the NOTICE file distributed with'
nl|'\n'
comment|'# this work for additional information regarding copyright ownership.'
nl|'\n'
comment|'# The ASF licenses this file to You under the Apache License, Version 2.0'
nl|'\n'
comment|'# (the "License"); you may not use this file except in compliance with'
nl|'\n'
comment|'# the License.  You may obtain a copy of the License at'
nl|'\n'
comment|'#'
nl|'\n'
comment|'#     http://www.apache.org/licenses/LICENSE-2.0'
nl|'\n'
comment|'#'
nl|'\n'
comment|'# Unless required by applicable law or agreed to in writing, software'
nl|'\n'
comment|'# distributed under the License is distributed on an "AS IS" BASIS,'
nl|'\n'
comment|'# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.'
nl|'\n'
comment|'# See the License for the specific language governing permissions and'
nl|'\n'
comment|'# limitations under the License.'
nl|'\n'
nl|'\n'
name|'import'
name|'re'
newline|'\n'
nl|'\n'
comment|'# A simple python script to generate an HTML entity map and a regex alternation'
nl|'\n'
comment|'# for inclusion in HTMLStripCharFilter.jflex.'
nl|'\n'
nl|'\n'
DECL|function|main
name|'def'
name|'main'
op|'('
op|')'
op|':'
newline|'\n'
indent|'  '
name|'print'
name|'get_apache_license'
op|'('
op|')'
newline|'\n'
name|'codes'
op|'='
op|'{'
op|'}'
newline|'\n'
name|'regex'
op|'='
name|'re'
op|'.'
name|'compile'
op|'('
string|'r\'\\s*<!ENTITY\\s+(\\S+)\\s+"&(?:#38;)?#(\\d+);"\''
op|')'
newline|'\n'
name|'for'
name|'line'
name|'in'
name|'get_entity_text'
op|'('
op|')'
op|'.'
name|'split'
op|'('
string|"'\\n'"
op|')'
op|':'
newline|'\n'
indent|'    '
name|'match'
op|'='
name|'regex'
op|'.'
name|'match'
op|'('
name|'line'
op|')'
newline|'\n'
name|'if'
name|'match'
op|':'
newline|'\n'
indent|'      '
name|'key'
op|'='
name|'match'
op|'.'
name|'group'
op|'('
number|'1'
op|')'
newline|'\n'
name|'if'
name|'key'
op|'=='
string|"'quot'"
op|':'
name|'codes'
op|'['
name|'key'
op|']'
op|'='
string|'r\'\\"\''
newline|'\n'
name|'elif'
name|'key'
op|'=='
string|"'nbsp'"
op|':'
name|'codes'
op|'['
name|'key'
op|']'
op|'='
string|"' '"
op|';'
newline|'\n'
name|'else'
op|':'
name|'codes'
op|'['
name|'key'
op|']'
op|'='
string|"r'\\u%04X'"
op|'%'
name|'int'
op|'('
name|'match'
op|'.'
name|'group'
op|'('
number|'2'
op|')'
op|')'
newline|'\n'
nl|'\n'
dedent|''
dedent|''
name|'keys'
op|'='
name|'sorted'
op|'('
name|'codes'
op|')'
newline|'\n'
nl|'\n'
name|'first_entry'
op|'='
name|'True'
newline|'\n'
name|'output_line'
op|'='
string|"'CharacterEntities = ( '"
newline|'\n'
name|'for'
name|'key'
name|'in'
name|'keys'
op|':'
newline|'\n'
indent|'    '
name|'new_entry'
op|'='
op|'('
string|'\'"%s"\''
name|'if'
name|'first_entry'
name|'else'
string|'\' | "%s"\''
op|')'
op|'%'
name|'key'
newline|'\n'
name|'first_entry'
op|'='
name|'False'
newline|'\n'
name|'if'
name|'len'
op|'('
name|'output_line'
op|')'
op|'+'
name|'len'
op|'('
name|'new_entry'
op|')'
op|'>='
number|'80'
op|':'
newline|'\n'
indent|'      '
name|'print'
name|'output_line'
newline|'\n'
name|'output_line'
op|'='
string|"'                   '"
newline|'\n'
dedent|''
name|'output_line'
op|'+='
name|'new_entry'
newline|'\n'
name|'if'
name|'key'
name|'in'
op|'('
string|"'quot'"
op|','
string|"'copy'"
op|','
string|"'gt'"
op|','
string|"'lt'"
op|','
string|"'reg'"
op|','
string|"'amp'"
op|')'
op|':'
newline|'\n'
indent|'      '
name|'new_entry'
op|'='
string|'\' | "%s"\''
op|'%'
name|'key'
op|'.'
name|'upper'
op|'('
op|')'
newline|'\n'
name|'if'
name|'len'
op|'('
name|'output_line'
op|')'
op|'+'
name|'len'
op|'('
name|'new_entry'
op|')'
op|'>='
number|'80'
op|':'
newline|'\n'
indent|'        '
name|'print'
name|'output_line'
newline|'\n'
name|'output_line'
op|'='
string|"'                   '"
newline|'\n'
dedent|''
name|'output_line'
op|'+='
name|'new_entry'
newline|'\n'
dedent|''
dedent|''
name|'print'
name|'output_line'
op|','
string|"')'"
newline|'\n'
nl|'\n'
name|'print'
string|"'%{'"
newline|'\n'
name|'print'
string|"'  private static final Map<String,String> upperCaseVariantsAccepted'"
newline|'\n'
name|'print'
string|"'      = new HashMap<>();'"
newline|'\n'
name|'print'
string|"'  static {'"
newline|'\n'
name|'print'
string|'\'    upperCaseVariantsAccepted.put("quot", "QUOT");\''
newline|'\n'
name|'print'
string|'\'    upperCaseVariantsAccepted.put("copy", "COPY");\''
newline|'\n'
name|'print'
string|'\'    upperCaseVariantsAccepted.put("gt", "GT");\''
newline|'\n'
name|'print'
string|'\'    upperCaseVariantsAccepted.put("lt", "LT");\''
newline|'\n'
name|'print'
string|'\'    upperCaseVariantsAccepted.put("reg", "REG");\''
newline|'\n'
name|'print'
string|'\'    upperCaseVariantsAccepted.put("amp", "AMP");\''
newline|'\n'
name|'print'
string|"'  }'"
newline|'\n'
name|'print'
string|"'  private static final CharArrayMap<Character> entityValues'"
newline|'\n'
name|'print'
string|"'      = new CharArrayMap<>(%i, false);'"
op|'%'
name|'len'
op|'('
name|'keys'
op|')'
newline|'\n'
name|'print'
string|"'  static {'"
newline|'\n'
name|'print'
string|"'    String[] entities = {'"
newline|'\n'
name|'output_line'
op|'='
string|"'     '"
newline|'\n'
name|'for'
name|'key'
name|'in'
name|'keys'
op|':'
newline|'\n'
indent|'    '
name|'new_entry'
op|'='
string|'\' "%s", "%s",\''
op|'%'
op|'('
name|'key'
op|','
name|'codes'
op|'['
name|'key'
op|']'
op|')'
newline|'\n'
name|'if'
name|'len'
op|'('
name|'output_line'
op|')'
op|'+'
name|'len'
op|'('
name|'new_entry'
op|')'
op|'>='
number|'80'
op|':'
newline|'\n'
indent|'      '
name|'print'
name|'output_line'
newline|'\n'
name|'output_line'
op|'='
string|"'     '"
newline|'\n'
dedent|''
name|'output_line'
op|'+='
name|'new_entry'
newline|'\n'
dedent|''
name|'print'
name|'output_line'
op|'['
op|':'
op|'-'
number|'1'
op|']'
newline|'\n'
name|'print'
string|"'    };'"
newline|'\n'
name|'print'
string|"'    for (int i = 0 ; i < entities.length ; i += 2) {'"
newline|'\n'
name|'print'
string|"'      Character value = entities[i + 1].charAt(0);'"
newline|'\n'
name|'print'
string|"'      entityValues.put(entities[i], value);'"
newline|'\n'
name|'print'
string|"'      String upperCaseVariant = upperCaseVariantsAccepted.get(entities[i]);'"
newline|'\n'
name|'print'
string|"'      if (upperCaseVariant != null) {'"
newline|'\n'
name|'print'
string|"'        entityValues.put(upperCaseVariant, value);'"
newline|'\n'
name|'print'
string|"'      }'"
newline|'\n'
name|'print'
string|"'    }'"
newline|'\n'
name|'print'
string|'"  }"'
newline|'\n'
name|'print'
string|'"%}"'
newline|'\n'
nl|'\n'
DECL|function|get_entity_text
dedent|''
name|'def'
name|'get_entity_text'
op|'('
op|')'
op|':'
newline|'\n'
comment|'# The text below is taken verbatim from'
nl|'\n'
comment|'# <http://www.w3.org/TR/REC-html40/sgml/entities.html>:'
nl|'\n'
indent|'  '
name|'text'
op|'='
string|'r"""\nF.1. XHTML Character Entities\n\nXHTML DTDs make available a standard collection of named character entities. Those entities are defined in this section.\nF.1.1. XHTML Latin 1 Character Entities\n\nYou can download this version of this file from http://www.w3.org/TR/2010/REC-xhtml-modularization/DTD/xhtml-lat1.ent. The latest version is available at http://www.w3.org/MarkUp/DTD/xhtml-lat1.ent.\n\n<!-- ...................................................................... -->\n<!-- XML-compatible ISO Latin 1 Character Entity Set for XHTML ............ -->\n<!-- file: xhtml-lat1.ent\n\n     Typical invocation:\n\n       <!ENTITY % xhtml-lat1\n           PUBLIC "-//W3C//ENTITIES Latin 1 for XHTML//EN"\n                  "xhtml-lat1.ent" >\n       %xhtml-lat1;\n\n     This DTD module is identified by the PUBLIC and SYSTEM identifiers:\n\n       PUBLIC "-//W3C//ENTITIES Latin 1 for XHTML//EN"\n       SYSTEM "http://www.w3.org/MarkUp/DTD/xhtml-lat1.ent"\n\n     Revision:  Id: xhtml-lat1.ent,v 4.1 2001/04/10 09:34:14 altheim Exp $ SMI\n\n     Portions (C) International Organization for Standardization 1986:\n     Permission to copy in any form is granted for use with conforming\n     SGML systems and applications as defined in ISO 8879, provided\n     this notice is included in all copies.\n-->\n\n<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->\n<!ENTITY iexcl  "&#161;" ><!-- inverted exclamation mark, U+00A1 ISOnum -->\n<!ENTITY cent   "&#162;" ><!-- cent sign, U+00A2 ISOnum -->\n<!ENTITY pound  "&#163;" ><!-- pound sign, U+00A3 ISOnum -->\n<!ENTITY curren "&#164;" ><!-- currency sign, U+00A4 ISOnum -->\n<!ENTITY yen    "&#165;" ><!-- yen sign = yuan sign, U+00A5 ISOnum -->\n<!ENTITY brvbar "&#166;" ><!-- broken bar = broken vertical bar, U+00A6 ISOnum -->\n<!ENTITY sect   "&#167;" ><!-- section sign, U+00A7 ISOnum -->\n<!ENTITY uml    "&#168;" ><!-- diaeresis = spacing diaeresis, U+00A8 ISOdia -->\n<!ENTITY copy   "&#169;" ><!-- copyright sign, U+00A9 ISOnum -->\n<!ENTITY ordf   "&#170;" ><!-- feminine ordinal indicator, U+00AA ISOnum -->\n<!ENTITY laquo  "&#171;" ><!-- left-pointing double angle quotation mark = left pointing guillemet, U+00AB ISOnum -->\n<!ENTITY not    "&#172;" ><!-- not sign, U+00AC ISOnum -->\n<!ENTITY shy    "&#173;" ><!-- soft hyphen = discretionary hyphen, U+00AD ISOnum -->\n<!ENTITY reg    "&#174;" ><!-- registered sign = registered trade mark sign, U+00AE ISOnum -->\n<!ENTITY macr   "&#175;" ><!-- macron = spacing macron = overline = APL overbar, U+00AF ISOdia -->\n<!ENTITY deg    "&#176;" ><!-- degree sign, U+00B0 ISOnum -->\n<!ENTITY plusmn "&#177;" ><!-- plus-minus sign = plus-or-minus sign, U+00B1 ISOnum -->\n<!ENTITY sup2   "&#178;" ><!-- superscript two = superscript digit two = squared, U+00B2 ISOnum -->\n<!ENTITY sup3   "&#179;" ><!-- superscript three = superscript digit three = cubed, U+00B3 ISOnum -->\n<!ENTITY acute  "&#180;" ><!-- acute accent = spacing acute, U+00B4 ISOdia -->\n<!ENTITY micro  "&#181;" ><!-- micro sign, U+00B5 ISOnum -->\n<!ENTITY para   "&#182;" ><!-- pilcrow sign = paragraph sign, U+00B6 ISOnum -->\n<!ENTITY middot "&#183;" ><!-- middle dot = Georgian comma = Greek middle dot, U+00B7 ISOnum -->\n<!ENTITY cedil  "&#184;" ><!-- cedilla = spacing cedilla, U+00B8 ISOdia -->\n<!ENTITY sup1   "&#185;" ><!-- superscript one = superscript digit one, U+00B9 ISOnum -->\n<!ENTITY ordm   "&#186;" ><!-- masculine ordinal indicator, U+00BA ISOnum -->\n<!ENTITY raquo  "&#187;" ><!-- right-pointing double angle quotation mark = right pointing guillemet, U+00BB ISOnum -->\n<!ENTITY frac14 "&#188;" ><!-- vulgar fraction one quarter = fraction one quarter, U+00BC ISOnum -->\n<!ENTITY frac12 "&#189;" ><!-- vulgar fraction one half = fraction one half, U+00BD ISOnum -->\n<!ENTITY frac34 "&#190;" ><!-- vulgar fraction three quarters = fraction three quarters, U+00BE ISOnum -->\n<!ENTITY iquest "&#191;" ><!-- inverted question mark = turned question mark, U+00BF ISOnum -->\n<!ENTITY Agrave "&#192;" ><!-- latin capital A with grave = latin capital A grave, U+00C0 ISOlat1 -->\n<!ENTITY Aacute "&#193;" ><!-- latin capital A with acute, U+00C1 ISOlat1 -->\n<!ENTITY Acirc  "&#194;" ><!-- latin capital A with circumflex, U+00C2 ISOlat1 -->\n<!ENTITY Atilde "&#195;" ><!-- latin capital A with tilde, U+00C3 ISOlat1 -->\n<!ENTITY Auml   "&#196;" ><!-- latin capital A with diaeresis, U+00C4 ISOlat1 -->\n<!ENTITY Aring  "&#197;" ><!-- latin capital A with ring above = latin capital A ring, U+00C5 ISOlat1 -->\n<!ENTITY AElig  "&#198;" ><!-- latin capital AE = latin capital ligature AE, U+00C6 ISOlat1 -->\n<!ENTITY Ccedil "&#199;" ><!-- latin capital C with cedilla, U+00C7 ISOlat1 -->\n<!ENTITY Egrave "&#200;" ><!-- latin capital E with grave, U+00C8 ISOlat1 -->\n<!ENTITY Eacute "&#201;" ><!-- latin capital E with acute, U+00C9 ISOlat1 -->\n<!ENTITY Ecirc  "&#202;" ><!-- latin capital E with circumflex, U+00CA ISOlat1 -->\n<!ENTITY Euml   "&#203;" ><!-- latin capital E with diaeresis, U+00CB ISOlat1 -->\n<!ENTITY Igrave "&#204;" ><!-- latin capital I with grave, U+00CC ISOlat1 -->\n<!ENTITY Iacute "&#205;" ><!-- latin capital I with acute, U+00CD ISOlat1 -->\n<!ENTITY Icirc  "&#206;" ><!-- latin capital I with circumflex, U+00CE ISOlat1 -->\n<!ENTITY Iuml   "&#207;" ><!-- latin capital I with diaeresis, U+00CF ISOlat1 -->\n<!ENTITY ETH    "&#208;" ><!-- latin capital ETH, U+00D0 ISOlat1 -->\n<!ENTITY Ntilde "&#209;" ><!-- latin capital N with tilde, U+00D1 ISOlat1 -->\n<!ENTITY Ograve "&#210;" ><!-- latin capital O with grave, U+00D2 ISOlat1 -->\n<!ENTITY Oacute "&#211;" ><!-- latin capital O with acute, U+00D3 ISOlat1 -->\n<!ENTITY Ocirc  "&#212;" ><!-- latin capital O with circumflex, U+00D4 ISOlat1 -->\n<!ENTITY Otilde "&#213;" ><!-- latin capital O with tilde, U+00D5 ISOlat1 -->\n<!ENTITY Ouml   "&#214;" ><!-- latin capital O with diaeresis, U+00D6 ISOlat1 -->\n<!ENTITY times  "&#215;" ><!-- multiplication sign, U+00D7 ISOnum -->\n<!ENTITY Oslash "&#216;" ><!-- latin capital O with stroke = latin capital O slash, U+00D8 ISOlat1 -->\n<!ENTITY Ugrave "&#217;" ><!-- latin capital U with grave, U+00D9 ISOlat1 -->\n<!ENTITY Uacute "&#218;" ><!-- latin capital U with acute, U+00DA ISOlat1 -->\n<!ENTITY Ucirc  "&#219;" ><!-- latin capital U with circumflex, U+00DB ISOlat1 -->\n<!ENTITY Uuml   "&#220;" ><!-- latin capital U with diaeresis, U+00DC ISOlat1 -->\n<!ENTITY Yacute "&#221;" ><!-- latin capital Y with acute, U+00DD ISOlat1 -->\n<!ENTITY THORN  "&#222;" ><!-- latin capital THORN, U+00DE ISOlat1 -->\n<!ENTITY szlig  "&#223;" ><!-- latin small sharp s = ess-zed, U+00DF ISOlat1 -->\n<!ENTITY agrave "&#224;" ><!-- latin small a with grave = latin small a grave, U+00E0 ISOlat1 -->\n<!ENTITY aacute "&#225;" ><!-- latin small a with acute, U+00E1 ISOlat1 -->\n<!ENTITY acirc  "&#226;" ><!-- latin small a with circumflex, U+00E2 ISOlat1 -->\n<!ENTITY atilde "&#227;" ><!-- latin small a with tilde, U+00E3 ISOlat1 -->\n<!ENTITY auml   "&#228;" ><!-- latin small a with diaeresis, U+00E4 ISOlat1 -->\n<!ENTITY aring  "&#229;" ><!-- latin small a with ring above = latin small a ring, U+00E5 ISOlat1 -->\n<!ENTITY aelig  "&#230;" ><!-- latin small ae = latin small ligature ae, U+00E6 ISOlat1 -->\n<!ENTITY ccedil "&#231;" ><!-- latin small c with cedilla, U+00E7 ISOlat1 -->\n<!ENTITY egrave "&#232;" ><!-- latin small e with grave, U+00E8 ISOlat1 -->\n<!ENTITY eacute "&#233;" ><!-- latin small e with acute, U+00E9 ISOlat1 -->\n<!ENTITY ecirc  "&#234;" ><!-- latin small e with circumflex, U+00EA ISOlat1 -->\n<!ENTITY euml   "&#235;" ><!-- latin small e with diaeresis, U+00EB ISOlat1 -->\n<!ENTITY igrave "&#236;" ><!-- latin small i with grave, U+00EC ISOlat1 -->\n<!ENTITY iacute "&#237;" ><!-- latin small i with acute, U+00ED ISOlat1 -->\n<!ENTITY icirc  "&#238;" ><!-- latin small i with circumflex, U+00EE ISOlat1 -->\n<!ENTITY iuml   "&#239;" ><!-- latin small i with diaeresis, U+00EF ISOlat1 -->\n<!ENTITY eth    "&#240;" ><!-- latin small eth, U+00F0 ISOlat1 -->\n<!ENTITY ntilde "&#241;" ><!-- latin small n with tilde, U+00F1 ISOlat1 -->\n<!ENTITY ograve "&#242;" ><!-- latin small o with grave, U+00F2 ISOlat1 -->\n<!ENTITY oacute "&#243;" ><!-- latin small o with acute, U+00F3 ISOlat1 -->\n<!ENTITY ocirc  "&#244;" ><!-- latin small o with circumflex, U+00F4 ISOlat1 -->\n<!ENTITY otilde "&#245;" ><!-- latin small o with tilde, U+00F5 ISOlat1 -->\n<!ENTITY ouml   "&#246;" ><!-- latin small o with diaeresis, U+00F6 ISOlat1 -->\n<!ENTITY divide "&#247;" ><!-- division sign, U+00F7 ISOnum -->\n<!ENTITY oslash "&#248;" ><!-- latin small o with stroke, = latin small o slash, U+00F8 ISOlat1 -->\n<!ENTITY ugrave "&#249;" ><!-- latin small u with grave, U+00F9 ISOlat1 -->\n<!ENTITY uacute "&#250;" ><!-- latin small u with acute, U+00FA ISOlat1 -->\n<!ENTITY ucirc  "&#251;" ><!-- latin small u with circumflex, U+00FB ISOlat1 -->\n<!ENTITY uuml   "&#252;" ><!-- latin small u with diaeresis, U+00FC ISOlat1 -->\n<!ENTITY yacute "&#253;" ><!-- latin small y with acute, U+00FD ISOlat1 -->\n<!ENTITY thorn  "&#254;" ><!-- latin small thorn with, U+00FE ISOlat1 -->\n<!ENTITY yuml   "&#255;" ><!-- latin small y with diaeresis, U+00FF ISOlat1 -->\n<!-- end of xhtml-lat1.ent -->\n\nF.1.2. XHTML Special Characters\n\nYou can download this version of this file from http://www.w3.org/TR/2010/REC-xhtml-modularization/DTD/xhtml-special.ent. The latest version is available at http://www.w3.org/MarkUp/DTD/xhtml-special.ent.\n\n<!-- ...................................................................... -->\n<!-- XML-compatible ISO Special Character Entity Set for XHTML ............ -->\n<!-- file: xhtml-special.ent\n\n     Typical invocation:\n\n       <!ENTITY % xhtml-special\n           PUBLIC "-//W3C//ENTITIES Special for XHTML//EN"\n                  "xhtml-special.ent" >\n       %xhtml-special;\n\n     This DTD module is identified by the PUBLIC and SYSTEM identifiers:\n\n       PUBLIC "-//W3C//ENTITIES Special for XHTML//EN"\n       SYSTEM "http://www.w3.org/MarkUp/DTD/xhtml-special.ent"\n\n     Revision:  Id: xhtml-special.ent,v 4.1 2001/04/10 09:34:14 altheim Exp $ SMI\n\n     Portions (C) International Organization for Standardization 1986:\n     Permission to copy in any form is granted for use with conforming\n     SGML systems and applications as defined in ISO 8879, provided\n     this notice is included in all copies.\n\n     Revisions:\n2000-10-28: added &apos; and altered XML Predefined Entities for compatibility\n-->\n\n<!-- Relevant ISO entity set is given unless names are newly introduced.\n     New names (i.e., not in ISO 8879 [SGML] list) do not clash with\n     any existing ISO 8879 entity names. ISO 10646 [ISO10646] character\n     numbers are given for each character, in hex. Entity values are\n     decimal conversions of the ISO 10646 values and refer to the\n     document character set. Names are Unicode [UNICODE] names.\n-->\n\n<!-- C0 Controls and Basic Latin -->\n<!ENTITY lt      "&#38;#60;" ><!-- less-than sign, U+003C ISOnum -->\n<!ENTITY gt      "&#62;" ><!-- greater-than sign, U+003E ISOnum -->\n<!ENTITY amp     "&#38;#38;" ><!-- ampersand, U+0026 ISOnum -->\n<!ENTITY apos    "&#39;" ><!-- The Apostrophe (Apostrophe Quote, APL Quote), U+0027 ISOnum -->\n<!ENTITY quot    "&#34;" ><!-- quotation mark (Quote Double), U+0022 ISOnum -->\n\n<!-- Latin Extended-A -->\n<!ENTITY OElig   "&#338;" ><!-- latin capital ligature OE, U+0152 ISOlat2 -->\n<!ENTITY oelig   "&#339;" ><!-- latin small ligature oe, U+0153 ISOlat2 -->\n\n<!-- ligature is a misnomer, this is a separate character in some languages -->\n<!ENTITY Scaron  "&#352;" ><!-- latin capital letter S with caron, U+0160 ISOlat2 -->\n<!ENTITY scaron  "&#353;" ><!-- latin small letter s with caron, U+0161 ISOlat2 -->\n<!ENTITY Yuml    "&#376;" ><!-- latin capital letter Y with diaeresis, U+0178 ISOlat2 -->\n\n<!-- Spacing Modifier Letters -->\n<!ENTITY circ    "&#710;" ><!-- modifier letter circumflex accent, U+02C6 ISOpub -->\n<!ENTITY tilde   "&#732;" ><!-- small tilde, U+02DC ISOdia -->\n\n<!-- General Punctuation -->\n<!ENTITY ensp    "&#8194;" ><!-- en space, U+2002 ISOpub -->\n<!ENTITY emsp    "&#8195;" ><!-- em space, U+2003 ISOpub -->\n<!ENTITY thinsp  "&#8201;" ><!-- thin space, U+2009 ISOpub -->\n<!ENTITY zwnj    "&#8204;" ><!-- zero width non-joiner, U+200C NEW RFC 2070 -->\n<!ENTITY zwj     "&#8205;" ><!-- zero width joiner, U+200D NEW RFC 2070 -->\n<!ENTITY lrm     "&#8206;" ><!-- left-to-right mark, U+200E NEW RFC 2070 -->\n<!ENTITY rlm     "&#8207;" ><!-- right-to-left mark, U+200F NEW RFC 2070 -->\n<!ENTITY ndash   "&#8211;" ><!-- en dash, U+2013 ISOpub -->\n<!ENTITY mdash   "&#8212;" ><!-- em dash, U+2014 ISOpub -->\n<!ENTITY lsquo   "&#8216;" ><!-- left single quotation mark, U+2018 ISOnum -->\n<!ENTITY rsquo   "&#8217;" ><!-- right single quotation mark, U+2019 ISOnum -->\n<!ENTITY sbquo   "&#8218;" ><!-- single low-9 quotation mark, U+201A NEW -->\n<!ENTITY ldquo   "&#8220;" ><!-- left double quotation mark, U+201C ISOnum -->\n<!ENTITY rdquo   "&#8221;" ><!-- right double quotation mark, U+201D ISOnum -->\n<!ENTITY bdquo   "&#8222;" ><!-- double low-9 quotation mark, U+201E NEW -->\n<!ENTITY dagger  "&#8224;" ><!-- dagger, U+2020 ISOpub -->\n<!ENTITY Dagger  "&#8225;" ><!-- double dagger, U+2021 ISOpub -->\n<!ENTITY permil  "&#8240;" ><!-- per mille sign, U+2030 ISOtech -->\n\n<!-- lsaquo is proposed but not yet ISO standardized -->\n<!ENTITY lsaquo  "&#8249;" ><!-- single left-pointing angle quotation mark, U+2039 ISO proposed -->\n<!-- rsaquo is proposed but not yet ISO standardized -->\n<!ENTITY rsaquo  "&#8250;" ><!-- single right-pointing angle quotation mark, U+203A ISO proposed -->\n<!ENTITY euro    "&#8364;" ><!-- euro sign, U+20AC NEW -->\n\n<!-- end of xhtml-special.ent -->\n\nF.1.3. XHTML Mathematical, Greek, and Symbolic Characters\n\nYou can download this version of this file from http://www.w3.org/TR/2010/REC-xhtml-modularization/DTD/xhtml-symbol.ent. The latest version is available at http://www.w3.org/MarkUp/DTD/xhtml-symbol.ent.\n\n<!-- ...................................................................... -->\n<!-- ISO Math, Greek and Symbolic Character Entity Set for XHTML .......... -->\n<!-- file: xhtml-symbol.ent\n\n     Typical invocation:\n\n       <!ENTITY % xhtml-symbol\n           PUBLIC "-//W3C//ENTITIES Symbols for XHTML//EN"\n                  "xhtml-symbol.ent" >\n       %xhtml-symbol;\n\n     This DTD module is identified by the PUBLIC and SYSTEM identifiers:\n\n       PUBLIC "-//W3C//ENTITIES Symbols for XHTML//EN"\n       SYSTEM "http://www.w3.org/MarkUp/DTD/xhtml-symbol.ent"\n\n     Revision:  Id: xhtml-symbol.ent,v 4.1 2001/04/10 09:34:14 altheim Exp $ SMI\n\n     Portions (C) International Organization for Standardization 1986:\n     Permission to copy in any form is granted for use with conforming\n     SGML systems and applications as defined in ISO 8879, provided\n     this notice is included in all copies.\n-->\n\n<!-- Relevant ISO entity set is given unless names are newly introduced.\n     New names (i.e., not in ISO 8879 [SGML] list) do not clash with\n     any existing ISO 8879 entity names. ISO 10646 [ISO10646] character\n     numbers are given for each character, in hex. Entity values are\n     decimal conversions of the ISO 10646 values and refer to the\n     document character set. Names are Unicode [UNICODE] names.\n-->\n\n<!-- Latin Extended-B -->\n<!ENTITY fnof     "&#402;" ><!-- latin small f with hook = function\n                              = florin, U+0192 ISOtech -->\n\n<!-- Greek -->\n<!ENTITY Alpha    "&#913;" ><!-- greek capital letter alpha, U+0391 -->\n<!ENTITY Beta     "&#914;" ><!-- greek capital letter beta, U+0392 -->\n<!ENTITY Gamma    "&#915;" ><!-- greek capital letter gamma, U+0393 ISOgrk3 -->\n<!ENTITY Delta    "&#916;" ><!-- greek capital letter delta, U+0394 ISOgrk3 -->\n<!ENTITY Epsilon  "&#917;" ><!-- greek capital letter epsilon, U+0395 -->\n<!ENTITY Zeta     "&#918;" ><!-- greek capital letter zeta, U+0396 -->\n<!ENTITY Eta      "&#919;" ><!-- greek capital letter eta, U+0397 -->\n<!ENTITY Theta    "&#920;" ><!-- greek capital letter theta, U+0398 ISOgrk3 -->\n<!ENTITY Iota     "&#921;" ><!-- greek capital letter iota, U+0399 -->\n<!ENTITY Kappa    "&#922;" ><!-- greek capital letter kappa, U+039A -->\n<!ENTITY Lambda   "&#923;" ><!-- greek capital letter lambda, U+039B ISOgrk3 -->\n<!ENTITY Mu       "&#924;" ><!-- greek capital letter mu, U+039C -->\n<!ENTITY Nu       "&#925;" ><!-- greek capital letter nu, U+039D -->\n<!ENTITY Xi       "&#926;" ><!-- greek capital letter xi, U+039E ISOgrk3 -->\n<!ENTITY Omicron  "&#927;" ><!-- greek capital letter omicron, U+039F -->\n<!ENTITY Pi       "&#928;" ><!-- greek capital letter pi, U+03A0 ISOgrk3 -->\n<!ENTITY Rho      "&#929;" ><!-- greek capital letter rho, U+03A1 -->\n<!-- there is no Sigmaf, and no U+03A2 character either -->\n<!ENTITY Sigma    "&#931;" ><!-- greek capital letter sigma, U+03A3 ISOgrk3 -->\n<!ENTITY Tau      "&#932;" ><!-- greek capital letter tau, U+03A4 -->\n<!ENTITY Upsilon  "&#933;" ><!-- greek capital letter upsilon,\n                              U+03A5 ISOgrk3 -->\n<!ENTITY Phi      "&#934;" ><!-- greek capital letter phi, U+03A6 ISOgrk3 -->\n<!ENTITY Chi      "&#935;" ><!-- greek capital letter chi, U+03A7 -->\n<!ENTITY Psi      "&#936;" ><!-- greek capital letter psi, U+03A8 ISOgrk3 -->\n<!ENTITY Omega    "&#937;" ><!-- greek capital letter omega, U+03A9 ISOgrk3 -->\n<!ENTITY alpha    "&#945;" ><!-- greek small letter alpha, U+03B1 ISOgrk3 -->\n<!ENTITY beta     "&#946;" ><!-- greek small letter beta, U+03B2 ISOgrk3 -->\n<!ENTITY gamma    "&#947;" ><!-- greek small letter gamma, U+03B3 ISOgrk3 -->\n<!ENTITY delta    "&#948;" ><!-- greek small letter delta, U+03B4 ISOgrk3 -->\n<!ENTITY epsilon  "&#949;" ><!-- greek small letter epsilon, U+03B5 ISOgrk3 -->\n<!ENTITY zeta     "&#950;" ><!-- greek small letter zeta, U+03B6 ISOgrk3 -->\n<!ENTITY eta      "&#951;" ><!-- greek small letter eta, U+03B7 ISOgrk3 -->\n<!ENTITY theta    "&#952;" ><!-- greek small letter theta, U+03B8 ISOgrk3 -->\n<!ENTITY iota     "&#953;" ><!-- greek small letter iota, U+03B9 ISOgrk3 -->\n<!ENTITY kappa    "&#954;" ><!-- greek small letter kappa, U+03BA ISOgrk3 -->\n<!ENTITY lambda   "&#955;" ><!-- greek small letter lambda, U+03BB ISOgrk3 -->\n<!ENTITY mu       "&#956;" ><!-- greek small letter mu, U+03BC ISOgrk3 -->\n<!ENTITY nu       "&#957;" ><!-- greek small letter nu, U+03BD ISOgrk3 -->\n<!ENTITY xi       "&#958;" ><!-- greek small letter xi, U+03BE ISOgrk3 -->\n<!ENTITY omicron  "&#959;" ><!-- greek small letter omicron, U+03BF NEW -->\n<!ENTITY pi       "&#960;" ><!-- greek small letter pi, U+03C0 ISOgrk3 -->\n<!ENTITY rho      "&#961;" ><!-- greek small letter rho, U+03C1 ISOgrk3 -->\n<!ENTITY sigmaf   "&#962;" ><!-- greek small letter final sigma, U+03C2 ISOgrk3 -->\n<!ENTITY sigma    "&#963;" ><!-- greek small letter sigma, U+03C3 ISOgrk3 -->\n<!ENTITY tau      "&#964;" ><!-- greek small letter tau, U+03C4 ISOgrk3 -->\n<!ENTITY upsilon  "&#965;" ><!-- greek small letter upsilon, U+03C5 ISOgrk3 -->\n<!ENTITY phi      "&#966;" ><!-- greek small letter phi, U+03C6 ISOgrk3 -->\n<!ENTITY chi      "&#967;" ><!-- greek small letter chi, U+03C7 ISOgrk3 -->\n<!ENTITY psi      "&#968;" ><!-- greek small letter psi, U+03C8 ISOgrk3 -->\n<!ENTITY omega    "&#969;" ><!-- greek small letter omega, U+03C9 ISOgrk3 -->\n<!ENTITY thetasym "&#977;" ><!-- greek small letter theta symbol, U+03D1 NEW -->\n<!ENTITY upsih    "&#978;" ><!-- greek upsilon with hook symbol, U+03D2 NEW -->\n<!ENTITY piv      "&#982;" ><!-- greek pi symbol, U+03D6 ISOgrk3 -->\n\n<!-- General Punctuation -->\n<!ENTITY bull     "&#8226;" ><!-- bullet = black small circle, U+2022 ISOpub  -->\n<!-- bullet is NOT the same as bullet operator, U+2219 -->\n<!ENTITY hellip   "&#8230;" ><!-- horizontal ellipsis = three dot leader, U+2026 ISOpub  -->\n<!ENTITY prime    "&#8242;" ><!-- prime = minutes = feet, U+2032 ISOtech -->\n<!ENTITY Prime    "&#8243;" ><!-- double prime = seconds = inches, U+2033 ISOtech -->\n<!ENTITY oline    "&#8254;" ><!-- overline = spacing overscore, U+203E NEW -->\n<!ENTITY frasl    "&#8260;" ><!-- fraction slash, U+2044 NEW -->\n\n<!-- Letterlike Symbols -->\n<!ENTITY weierp   "&#8472;" ><!-- script capital P = power set = Weierstrass p, U+2118 ISOamso -->\n<!ENTITY image    "&#8465;" ><!-- blackletter capital I = imaginary part, U+2111 ISOamso -->\n<!ENTITY real     "&#8476;" ><!-- blackletter capital R = real part symbol, U+211C ISOamso -->\n<!ENTITY trade    "&#8482;" ><!-- trade mark sign, U+2122 ISOnum -->\n<!ENTITY alefsym  "&#8501;" ><!-- alef symbol = first transfinite cardinal, U+2135 NEW -->\n<!-- alef symbol is NOT the same as hebrew letter alef, U+05D0 although\n     the same glyph could be used to depict both characters -->\n\n<!-- Arrows -->\n<!ENTITY larr     "&#8592;" ><!-- leftwards arrow, U+2190 ISOnum -->\n<!ENTITY uarr     "&#8593;" ><!-- upwards arrow, U+2191 ISOnum-->\n<!ENTITY rarr     "&#8594;" ><!-- rightwards arrow, U+2192 ISOnum -->\n<!ENTITY darr     "&#8595;" ><!-- downwards arrow, U+2193 ISOnum -->\n<!ENTITY harr     "&#8596;" ><!-- left right arrow, U+2194 ISOamsa -->\n<!ENTITY crarr    "&#8629;" ><!-- downwards arrow with corner leftwards\n                               = carriage return, U+21B5 NEW -->\n<!ENTITY lArr     "&#8656;" ><!-- leftwards double arrow, U+21D0 ISOtech -->\n<!-- Unicode does not say that lArr is the same as the \'is implied by\' arrow\n    but also does not have any other character for that function. So ? lArr can\n    be used for \'is implied by\' as ISOtech suggests -->\n<!ENTITY uArr     "&#8657;" ><!-- upwards double arrow, U+21D1 ISOamsa -->\n<!ENTITY rArr     "&#8658;" ><!-- rightwards double arrow, U+21D2 ISOtech -->\n<!-- Unicode does not say this is the \'implies\' character but does not have\n     another character with this function so ?\n     rArr can be used for \'implies\' as ISOtech suggests -->\n<!ENTITY dArr     "&#8659;" ><!-- downwards double arrow, U+21D3 ISOamsa -->\n<!ENTITY hArr     "&#8660;" ><!-- left right double arrow, U+21D4 ISOamsa -->\n\n<!-- Mathematical Operators -->\n<!ENTITY forall   "&#8704;" ><!-- for all, U+2200 ISOtech -->\n<!ENTITY part     "&#8706;" ><!-- partial differential, U+2202 ISOtech  -->\n<!ENTITY exist    "&#8707;" ><!-- there exists, U+2203 ISOtech -->\n<!ENTITY empty    "&#8709;" ><!-- empty set = null set, U+2205 ISOamso -->\n<!ENTITY nabla    "&#8711;" ><!-- nabla = backward difference, U+2207 ISOtech -->\n<!ENTITY isin     "&#8712;" ><!-- element of, U+2208 ISOtech -->\n<!ENTITY notin    "&#8713;" ><!-- not an element of, U+2209 ISOtech -->\n<!ENTITY ni       "&#8715;" ><!-- contains as member, U+220B ISOtech -->\n<!-- should there be a more memorable name than \'ni\'? -->\n<!ENTITY prod     "&#8719;" ><!-- n-ary product = product sign, U+220F ISOamsb -->\n<!-- prod is NOT the same character as U+03A0 \'greek capital letter pi\' though\n     the same glyph might be used for both -->\n<!ENTITY sum      "&#8721;" ><!-- n-ary sumation, U+2211 ISOamsb -->\n<!-- sum is NOT the same character as U+03A3 \'greek capital letter sigma\'\n     though the same glyph might be used for both -->\n<!ENTITY minus    "&#8722;" ><!-- minus sign, U+2212 ISOtech -->\n<!ENTITY lowast   "&#8727;" ><!-- asterisk operator, U+2217 ISOtech -->\n<!ENTITY radic    "&#8730;" ><!-- square root = radical sign, U+221A ISOtech -->\n<!ENTITY prop     "&#8733;" ><!-- proportional to, U+221D ISOtech -->\n<!ENTITY infin    "&#8734;" ><!-- infinity, U+221E ISOtech -->\n<!ENTITY ang      "&#8736;" ><!-- angle, U+2220 ISOamso -->\n<!ENTITY and      "&#8743;" ><!-- logical and = wedge, U+2227 ISOtech -->\n<!ENTITY or       "&#8744;" ><!-- logical or = vee, U+2228 ISOtech -->\n<!ENTITY cap      "&#8745;" ><!-- intersection = cap, U+2229 ISOtech -->\n<!ENTITY cup      "&#8746;" ><!-- union = cup, U+222A ISOtech -->\n<!ENTITY int      "&#8747;" ><!-- integral, U+222B ISOtech -->\n<!ENTITY there4   "&#8756;" ><!-- therefore, U+2234 ISOtech -->\n<!ENTITY sim      "&#8764;" ><!-- tilde operator = varies with = similar to, U+223C ISOtech -->\n<!-- tilde operator is NOT the same character as the tilde, U+007E,\n     although the same glyph might be used to represent both  -->\n<!ENTITY cong     "&#8773;" ><!-- approximately equal to, U+2245 ISOtech -->\n<!ENTITY asymp    "&#8776;" ><!-- almost equal to = asymptotic to, U+2248 ISOamsr -->\n<!ENTITY ne       "&#8800;" ><!-- not equal to, U+2260 ISOtech -->\n<!ENTITY equiv    "&#8801;" ><!-- identical to, U+2261 ISOtech -->\n<!ENTITY le       "&#8804;" ><!-- less-than or equal to, U+2264 ISOtech -->\n<!ENTITY ge       "&#8805;" ><!-- greater-than or equal to, U+2265 ISOtech -->\n<!ENTITY sub      "&#8834;" ><!-- subset of, U+2282 ISOtech -->\n<!ENTITY sup      "&#8835;" ><!-- superset of, U+2283 ISOtech -->\n<!-- note that nsup, \'not a superset of, U+2283\' is not covered by the Symbol\n     font encoding and is not included. Should it be, for symmetry?\n     It is in ISOamsn  -->\n<!ENTITY nsub     "&#8836;" ><!-- not a subset of, U+2284 ISOamsn -->\n<!ENTITY sube     "&#8838;" ><!-- subset of or equal to, U+2286 ISOtech -->\n<!ENTITY supe     "&#8839;" ><!-- superset of or equal to, U+2287 ISOtech -->\n<!ENTITY oplus    "&#8853;" ><!-- circled plus = direct sum, U+2295 ISOamsb -->\n<!ENTITY otimes   "&#8855;" ><!-- circled times = vector product, U+2297 ISOamsb -->\n<!ENTITY perp     "&#8869;" ><!-- up tack = orthogonal to = perpendicular, U+22A5 ISOtech -->\n<!ENTITY sdot     "&#8901;" ><!-- dot operator, U+22C5 ISOamsb -->\n<!-- dot operator is NOT the same character as U+00B7 middle dot -->\n\n<!-- Miscellaneous Technical -->\n<!ENTITY lceil    "&#8968;" ><!-- left ceiling = apl upstile, U+2308 ISOamsc  -->\n<!ENTITY rceil    "&#8969;" ><!-- right ceiling, U+2309 ISOamsc  -->\n<!ENTITY lfloor   "&#8970;" ><!-- left floor = apl downstile, U+230A ISOamsc  -->\n<!ENTITY rfloor   "&#8971;" ><!-- right floor, U+230B ISOamsc  -->\n<!ENTITY lang     "&#9001;" ><!-- left-pointing angle bracket = bra, U+2329 ISOtech -->\n<!-- lang is NOT the same character as U+003C \'less than\'\n     or U+2039 \'single left-pointing angle quotation mark\' -->\n<!ENTITY rang     "&#9002;" ><!-- right-pointing angle bracket = ket, U+232A ISOtech -->\n<!-- rang is NOT the same character as U+003E \'greater than\'\n     or U+203A \'single right-pointing angle quotation mark\' -->\n\n<!-- Geometric Shapes -->\n<!ENTITY loz      "&#9674;" ><!-- lozenge, U+25CA ISOpub -->\n\n<!-- Miscellaneous Symbols -->\n<!ENTITY spades   "&#9824;" ><!-- black spade suit, U+2660 ISOpub -->\n<!-- black here seems to mean filled as opposed to hollow -->\n<!ENTITY clubs    "&#9827;" ><!-- black club suit = shamrock, U+2663 ISOpub -->\n<!ENTITY hearts   "&#9829;" ><!-- black heart suit = valentine, U+2665 ISOpub -->\n<!ENTITY diams    "&#9830;" ><!-- black diamond suit, U+2666 ISOpub -->\n\n<!-- end of xhtml-symbol.ent -->\n"""'
newline|'\n'
name|'return'
name|'text'
newline|'\n'
nl|'\n'
DECL|function|get_apache_license
dedent|''
name|'def'
name|'get_apache_license'
op|'('
op|')'
op|':'
newline|'\n'
indent|'  '
name|'license'
op|'='
string|'r"""/**\n * Licensed to the Apache Software Foundation (ASF) under one or more\n * contributor license agreements.  See the NOTICE file distributed with\n * this work for additional information regarding copyright ownership.\n * The ASF licenses this file to You under the Apache License, Version 2.0\n * (the "License"); you may not use this file except in compliance with\n * the License.  You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an "AS IS" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n\n"""'
newline|'\n'
name|'return'
name|'license'
newline|'\n'
nl|'\n'
dedent|''
name|'main'
op|'('
op|')'
newline|'\n'
endmarker|''
end_unit
