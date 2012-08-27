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
name|'sys'
newline|'\n'
name|'import'
name|'os'
newline|'\n'
name|'import'
name|'re'
newline|'\n'
nl|'\n'
DECL|variable|reHREF
name|'reHREF'
op|'='
name|'re'
op|'.'
name|'compile'
op|'('
string|"'<a.*?>(.*?)</a>'"
op|','
name|'re'
op|'.'
name|'IGNORECASE'
op|')'
newline|'\n'
nl|'\n'
DECL|variable|reMarkup
name|'reMarkup'
op|'='
name|'re'
op|'.'
name|'compile'
op|'('
string|"'<.*?>'"
op|')'
newline|'\n'
DECL|variable|reDivBlock
name|'reDivBlock'
op|'='
name|'re'
op|'.'
name|'compile'
op|'('
string|'\'<div class="block">(.*?)</div>\''
op|','
name|'re'
op|'.'
name|'IGNORECASE'
op|')'
newline|'\n'
DECL|variable|reCaption
name|'reCaption'
op|'='
name|'re'
op|'.'
name|'compile'
op|'('
string|"'<caption><span>(.*?)</span>'"
op|','
name|'re'
op|'.'
name|'IGNORECASE'
op|')'
newline|'\n'
DECL|variable|reTDLast
name|'reTDLast'
op|'='
name|'re'
op|'.'
name|'compile'
op|'('
string|'\'<td class="colLast">(.*?)$\''
op|','
name|'re'
op|'.'
name|'IGNORECASE'
op|')'
newline|'\n'
DECL|variable|reColOne
name|'reColOne'
op|'='
name|'re'
op|'.'
name|'compile'
op|'('
string|'\'<td class="colOne">(.*?)</td>\''
op|','
name|'re'
op|'.'
name|'IGNORECASE'
op|')'
newline|'\n'
nl|'\n'
DECL|function|cleanHTML
name|'def'
name|'cleanHTML'
op|'('
name|'s'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'s'
op|'='
name|'reMarkup'
op|'.'
name|'sub'
op|'('
string|"''"
op|','
name|'s'
op|')'
newline|'\n'
name|'s'
op|'='
name|'s'
op|'.'
name|'replace'
op|'('
string|"'&nbsp;'"
op|','
string|"' '"
op|')'
newline|'\n'
name|'s'
op|'='
name|'s'
op|'.'
name|'replace'
op|'('
string|"'&lt;'"
op|','
string|"'<'"
op|')'
newline|'\n'
name|'s'
op|'='
name|'s'
op|'.'
name|'replace'
op|'('
string|"'&gt;'"
op|','
string|"'>'"
op|')'
newline|'\n'
name|'s'
op|'='
name|'s'
op|'.'
name|'replace'
op|'('
string|"'&amp;'"
op|','
string|"'&'"
op|')'
newline|'\n'
name|'return'
name|'s'
op|'.'
name|'strip'
op|'('
op|')'
newline|'\n'
nl|'\n'
DECL|function|checkClass
dedent|''
name|'def'
name|'checkClass'
op|'('
name|'fullPath'
op|')'
op|':'
newline|'\n'
comment|'# TODO: only works with java7 generated javadocs now!'
nl|'\n'
indent|'  '
name|'f'
op|'='
name|'open'
op|'('
name|'fullPath'
op|','
name|'encoding'
op|'='
string|"'UTF-8'"
op|')'
newline|'\n'
name|'anyMissing'
op|'='
name|'False'
newline|'\n'
nl|'\n'
name|'printed'
op|'='
name|'False'
newline|'\n'
name|'inThing'
op|'='
name|'False'
newline|'\n'
name|'lastCaption'
op|'='
name|'None'
newline|'\n'
name|'lastItem'
op|'='
name|'None'
newline|'\n'
nl|'\n'
name|'desc'
op|'='
name|'None'
newline|'\n'
nl|'\n'
name|'for'
name|'line'
name|'in'
name|'f'
op|'.'
name|'readlines'
op|'('
op|')'
op|':'
newline|'\n'
indent|'    '
name|'m'
op|'='
name|'reCaption'
op|'.'
name|'search'
op|'('
name|'line'
op|')'
newline|'\n'
name|'if'
name|'m'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
indent|'      '
name|'lastCaption'
op|'='
name|'m'
op|'.'
name|'group'
op|'('
number|'1'
op|')'
newline|'\n'
comment|"#print('    caption %s' % lastCaption)"
nl|'\n'
dedent|''
name|'m'
op|'='
name|'reTDLast'
op|'.'
name|'search'
op|'('
name|'line'
op|')'
newline|'\n'
name|'if'
name|'m'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
comment|'# TODO: this will only get the first line of multi-line things:'
nl|'\n'
indent|'      '
name|'lastItem'
op|'='
name|'cleanHTML'
op|'('
name|'m'
op|'.'
name|'group'
op|'('
number|'1'
op|')'
op|')'
newline|'\n'
comment|"#print('      item %s' % lastItem)"
nl|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'      '
name|'m'
op|'='
name|'reColOne'
op|'.'
name|'search'
op|'('
name|'line'
op|')'
newline|'\n'
name|'if'
name|'m'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
comment|'# TODO: this will only get the first line of multi-line things:'
nl|'\n'
indent|'        '
name|'lastItem'
op|'='
name|'cleanHTML'
op|'('
name|'m'
op|'.'
name|'group'
op|'('
number|'1'
op|')'
op|')'
newline|'\n'
comment|"#print('      item %s' % lastItem)"
nl|'\n'
nl|'\n'
dedent|''
dedent|''
name|'lineLower'
op|'='
name|'line'
op|'.'
name|'strip'
op|'('
op|')'
op|'.'
name|'lower'
op|'('
op|')'
newline|'\n'
nl|'\n'
name|'if'
name|'lineLower'
op|'.'
name|'find'
op|'('
string|'\'<tr class="\''
op|')'
op|'!='
op|'-'
number|'1'
op|':'
newline|'\n'
indent|'      '
name|'inThing'
op|'='
name|'True'
newline|'\n'
name|'hasDesc'
op|'='
name|'False'
newline|'\n'
name|'continue'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'inThing'
op|':'
newline|'\n'
indent|'      '
name|'if'
name|'lineLower'
op|'.'
name|'find'
op|'('
string|"'</tr>'"
op|')'
op|'!='
op|'-'
number|'1'
op|':'
newline|'\n'
indent|'        '
name|'if'
name|'not'
name|'hasDesc'
op|':'
newline|'\n'
indent|'          '
name|'if'
name|'not'
name|'printed'
op|':'
newline|'\n'
indent|'            '
name|'print'
op|'('
op|')'
newline|'\n'
name|'print'
op|'('
name|'fullPath'
op|')'
newline|'\n'
name|'printed'
op|'='
name|'True'
newline|'\n'
dedent|''
name|'print'
op|'('
string|"'  missing %s: %s'"
op|'%'
op|'('
name|'lastCaption'
op|','
name|'lastItem'
op|')'
op|')'
newline|'\n'
name|'anyMissing'
op|'='
name|'True'
newline|'\n'
dedent|''
name|'inThing'
op|'='
name|'False'
newline|'\n'
name|'continue'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'        '
name|'if'
name|'line'
op|'.'
name|'find'
op|'('
string|'\'<div class="block">\''
op|')'
op|'!='
op|'-'
number|'1'
op|':'
newline|'\n'
indent|'          '
name|'desc'
op|'='
op|'['
op|']'
newline|'\n'
dedent|''
name|'if'
name|'desc'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
indent|'          '
name|'desc'
op|'.'
name|'append'
op|'('
name|'line'
op|')'
newline|'\n'
name|'if'
name|'line'
op|'.'
name|'find'
op|'('
string|"'</div>'"
op|')'
op|'!='
op|'-'
number|'1'
op|':'
newline|'\n'
indent|'            '
name|'desc'
op|'='
string|"''"
op|'.'
name|'join'
op|'('
name|'desc'
op|')'
newline|'\n'
name|'desc'
op|'='
name|'desc'
op|'.'
name|'replace'
op|'('
string|'\'<div class="block">\''
op|','
string|"''"
op|')'
newline|'\n'
name|'desc'
op|'='
name|'desc'
op|'.'
name|'replace'
op|'('
string|"'</div>'"
op|','
string|"''"
op|')'
newline|'\n'
name|'desc'
op|'='
name|'desc'
op|'.'
name|'strip'
op|'('
op|')'
newline|'\n'
comment|"#print('        desc %s' % desc)"
nl|'\n'
name|'hasDesc'
op|'='
name|'len'
op|'('
name|'desc'
op|')'
op|'>'
number|'0'
newline|'\n'
name|'desc'
op|'='
name|'None'
newline|'\n'
dedent|''
dedent|''
dedent|''
dedent|''
dedent|''
name|'f'
op|'.'
name|'close'
op|'('
op|')'
newline|'\n'
name|'return'
name|'anyMissing'
newline|'\n'
nl|'\n'
DECL|function|checkSummary
dedent|''
name|'def'
name|'checkSummary'
op|'('
name|'fullPath'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'printed'
op|'='
name|'False'
newline|'\n'
name|'f'
op|'='
name|'open'
op|'('
name|'fullPath'
op|','
name|'encoding'
op|'='
string|"'UTF-8'"
op|')'
newline|'\n'
name|'anyMissing'
op|'='
name|'False'
newline|'\n'
name|'sawPackage'
op|'='
name|'False'
newline|'\n'
name|'desc'
op|'='
op|'['
op|']'
newline|'\n'
name|'lastHREF'
op|'='
name|'None'
newline|'\n'
name|'for'
name|'line'
name|'in'
name|'f'
op|'.'
name|'readlines'
op|'('
op|')'
op|':'
newline|'\n'
indent|'    '
name|'lineLower'
op|'='
name|'line'
op|'.'
name|'strip'
op|'('
op|')'
op|'.'
name|'lower'
op|'('
op|')'
newline|'\n'
name|'if'
name|'desc'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
comment|'# TODO: also detect missing description in overview-summary'
nl|'\n'
indent|'      '
name|'if'
name|'lineLower'
op|'.'
name|'startswith'
op|'('
string|"'package '"
op|')'
name|'or'
name|'lineLower'
op|'.'
name|'startswith'
op|'('
string|'\'<h1 title="package" \''
op|')'
op|':'
newline|'\n'
indent|'        '
name|'sawPackage'
op|'='
name|'True'
newline|'\n'
dedent|''
name|'elif'
name|'sawPackage'
op|':'
newline|'\n'
indent|'        '
name|'if'
name|'lineLower'
op|'.'
name|'startswith'
op|'('
string|"'<table '"
op|')'
name|'or'
name|'lineLower'
op|'.'
name|'startswith'
op|'('
string|"'<b>see: '"
op|')'
op|':'
newline|'\n'
indent|'          '
name|'desc'
op|'='
string|"' '"
op|'.'
name|'join'
op|'('
name|'desc'
op|')'
newline|'\n'
name|'desc'
op|'='
name|'reMarkup'
op|'.'
name|'sub'
op|'('
string|"' '"
op|','
name|'desc'
op|')'
newline|'\n'
name|'desc'
op|'='
name|'desc'
op|'.'
name|'strip'
op|'('
op|')'
newline|'\n'
name|'if'
name|'desc'
op|'=='
string|"''"
op|':'
newline|'\n'
indent|'            '
name|'if'
name|'not'
name|'printed'
op|':'
newline|'\n'
indent|'              '
name|'print'
op|'('
op|')'
newline|'\n'
name|'print'
op|'('
name|'fullPath'
op|')'
newline|'\n'
name|'printed'
op|'='
name|'True'
newline|'\n'
dedent|''
name|'print'
op|'('
string|"'  no package description (missing package.html in src?)'"
op|')'
newline|'\n'
name|'anyMissing'
op|'='
name|'True'
newline|'\n'
dedent|''
name|'desc'
op|'='
name|'None'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'          '
name|'desc'
op|'.'
name|'append'
op|'('
name|'lineLower'
op|')'
newline|'\n'
nl|'\n'
dedent|''
dedent|''
dedent|''
name|'if'
name|'lineLower'
name|'in'
op|'('
string|"'<td>&nbsp;</td>'"
op|','
string|"'<td></td>'"
op|','
string|'\'<td class="collast">&nbsp;</td>\''
op|')'
op|':'
newline|'\n'
indent|'      '
name|'if'
name|'not'
name|'printed'
op|':'
newline|'\n'
indent|'        '
name|'print'
op|'('
op|')'
newline|'\n'
name|'print'
op|'('
name|'fullPath'
op|')'
newline|'\n'
name|'printed'
op|'='
name|'True'
newline|'\n'
dedent|''
name|'print'
op|'('
string|"'  missing: %s'"
op|'%'
name|'unescapeHTML'
op|'('
name|'lastHREF'
op|')'
op|')'
newline|'\n'
name|'anyMissing'
op|'='
name|'True'
newline|'\n'
dedent|''
name|'elif'
name|'lineLower'
op|'.'
name|'find'
op|'('
string|"'licensed to the apache software foundation'"
op|')'
op|'!='
op|'-'
number|'1'
name|'or'
name|'lineLower'
op|'.'
name|'find'
op|'('
string|"'copyright 2004 the apache software foundation'"
op|')'
op|'!='
op|'-'
number|'1'
op|':'
newline|'\n'
indent|'      '
name|'if'
name|'not'
name|'printed'
op|':'
newline|'\n'
indent|'        '
name|'print'
op|'('
op|')'
newline|'\n'
name|'print'
op|'('
name|'fullPath'
op|')'
newline|'\n'
name|'printed'
op|'='
name|'True'
newline|'\n'
dedent|''
name|'print'
op|'('
string|"'  license-is-javadoc: %s'"
op|'%'
name|'unescapeHTML'
op|'('
name|'lastHREF'
op|')'
op|')'
newline|'\n'
name|'anyMissing'
op|'='
name|'True'
newline|'\n'
dedent|''
name|'m'
op|'='
name|'reHREF'
op|'.'
name|'search'
op|'('
name|'line'
op|')'
newline|'\n'
name|'if'
name|'m'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
indent|'      '
name|'lastHREF'
op|'='
name|'m'
op|'.'
name|'group'
op|'('
number|'1'
op|')'
newline|'\n'
dedent|''
dedent|''
name|'if'
name|'desc'
name|'is'
name|'not'
name|'None'
name|'and'
name|'fullPath'
op|'.'
name|'find'
op|'('
string|"'/overview-summary.html'"
op|')'
op|'=='
op|'-'
number|'1'
op|':'
newline|'\n'
indent|'    '
name|'raise'
name|'RuntimeError'
op|'('
string|"'BUG: failed to locate description in %s'"
op|'%'
name|'fullPath'
op|')'
newline|'\n'
dedent|''
name|'f'
op|'.'
name|'close'
op|'('
op|')'
newline|'\n'
name|'return'
name|'anyMissing'
newline|'\n'
nl|'\n'
DECL|function|unescapeHTML
dedent|''
name|'def'
name|'unescapeHTML'
op|'('
name|'s'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'s'
op|'='
name|'s'
op|'.'
name|'replace'
op|'('
string|"'&lt;'"
op|','
string|"'<'"
op|')'
newline|'\n'
name|'s'
op|'='
name|'s'
op|'.'
name|'replace'
op|'('
string|"'&gt;'"
op|','
string|"'>'"
op|')'
newline|'\n'
name|'s'
op|'='
name|'s'
op|'.'
name|'replace'
op|'('
string|"'&amp;'"
op|','
string|"'&'"
op|')'
newline|'\n'
name|'return'
name|'s'
newline|'\n'
nl|'\n'
DECL|function|checkPackageSummaries
dedent|''
name|'def'
name|'checkPackageSummaries'
op|'('
name|'root'
op|','
name|'level'
op|'='
string|"'class'"
op|')'
op|':'
newline|'\n'
indent|'  '
string|'"""\n  Just checks for blank summary lines in package-summary.html; returns\n  True if there are problems.\n  """'
newline|'\n'
nl|'\n'
name|'if'
name|'level'
op|'!='
string|"'class'"
name|'and'
name|'level'
op|'!='
string|"'package'"
name|'and'
name|'level'
op|'!='
string|"'method'"
op|':'
newline|'\n'
indent|'    '
name|'print'
op|'('
string|'\'unsupported level: %s, must be "class" or "package" or "method"\''
op|'%'
name|'level'
op|')'
newline|'\n'
name|'sys'
op|'.'
name|'exit'
op|'('
number|'1'
op|')'
newline|'\n'
nl|'\n'
comment|"#for dirPath, dirNames, fileNames in os.walk('%s/lucene/build/docs/api' % root):"
nl|'\n'
nl|'\n'
dedent|''
name|'if'
name|'False'
op|':'
newline|'\n'
indent|'    '
name|'os'
op|'.'
name|'chdir'
op|'('
name|'root'
op|')'
newline|'\n'
name|'print'
op|'('
op|')'
newline|'\n'
name|'print'
op|'('
string|'\'Run "ant javadocs" > javadocs.log...\''
op|')'
newline|'\n'
name|'if'
name|'os'
op|'.'
name|'system'
op|'('
string|"'ant javadocs > javadocs.log 2>&1'"
op|')'
op|':'
newline|'\n'
indent|'      '
name|'print'
op|'('
string|"'  FAILED'"
op|')'
newline|'\n'
name|'sys'
op|'.'
name|'exit'
op|'('
number|'1'
op|')'
newline|'\n'
nl|'\n'
dedent|''
dedent|''
name|'anyMissing'
op|'='
name|'False'
newline|'\n'
name|'for'
name|'dirPath'
op|','
name|'dirNames'
op|','
name|'fileNames'
name|'in'
name|'os'
op|'.'
name|'walk'
op|'('
name|'root'
op|')'
op|':'
newline|'\n'
nl|'\n'
indent|'    '
name|'if'
name|'dirPath'
op|'.'
name|'find'
op|'('
string|"'/all/'"
op|')'
op|'!='
op|'-'
number|'1'
op|':'
newline|'\n'
comment|'# These are dups (this is a bit risk, eg, root IS this /all/ directory..)'
nl|'\n'
indent|'      '
name|'continue'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
string|"'package-summary.html'"
name|'in'
name|'fileNames'
op|':'
newline|'\n'
indent|'      '
name|'if'
name|'level'
op|'!='
string|"'package'"
name|'and'
name|'checkSummary'
op|'('
string|"'%s/package-summary.html'"
op|'%'
name|'dirPath'
op|')'
op|':'
newline|'\n'
indent|'        '
name|'anyMissing'
op|'='
name|'True'
newline|'\n'
dedent|''
name|'if'
name|'level'
op|'=='
string|"'method'"
op|':'
newline|'\n'
indent|'        '
name|'for'
name|'fileName'
name|'in'
name|'fileNames'
op|':'
newline|'\n'
indent|'          '
name|'fullPath'
op|'='
string|"'%s/%s'"
op|'%'
op|'('
name|'dirPath'
op|','
name|'fileName'
op|')'
newline|'\n'
name|'if'
name|'not'
name|'fileName'
op|'.'
name|'startswith'
op|'('
string|"'package-'"
op|')'
name|'and'
name|'fileName'
op|'.'
name|'endswith'
op|'('
string|"'.html'"
op|')'
name|'and'
name|'os'
op|'.'
name|'path'
op|'.'
name|'isfile'
op|'('
name|'fullPath'
op|')'
op|':'
newline|'\n'
indent|'            '
name|'if'
name|'checkClass'
op|'('
name|'fullPath'
op|')'
op|':'
newline|'\n'
indent|'               '
name|'anyMissing'
op|'='
name|'True'
newline|'\n'
dedent|''
dedent|''
dedent|''
dedent|''
dedent|''
name|'if'
string|"'overview-summary.html'"
name|'in'
name|'fileNames'
op|':'
newline|'\n'
indent|'      '
name|'if'
name|'checkSummary'
op|'('
string|"'%s/overview-summary.html'"
op|'%'
name|'dirPath'
op|')'
op|':'
newline|'\n'
indent|'        '
name|'anyMissing'
op|'='
name|'True'
newline|'\n'
nl|'\n'
dedent|''
dedent|''
dedent|''
name|'return'
name|'anyMissing'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'__name__'
op|'=='
string|"'__main__'"
op|':'
newline|'\n'
indent|'  '
name|'if'
name|'len'
op|'('
name|'sys'
op|'.'
name|'argv'
op|')'
op|'<'
number|'2'
name|'or'
name|'len'
op|'('
name|'sys'
op|'.'
name|'argv'
op|')'
op|'>'
number|'3'
op|':'
newline|'\n'
indent|'    '
name|'print'
op|'('
string|"'usage: %s <dir> [class|package|method]'"
op|'%'
name|'sys'
op|'.'
name|'argv'
op|'['
number|'0'
op|']'
op|')'
newline|'\n'
name|'sys'
op|'.'
name|'exit'
op|'('
number|'1'
op|')'
newline|'\n'
dedent|''
name|'if'
name|'len'
op|'('
name|'sys'
op|'.'
name|'argv'
op|')'
op|'=='
number|'2'
op|':'
newline|'\n'
DECL|variable|level
indent|'    '
name|'level'
op|'='
string|"'class'"
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
DECL|variable|level
indent|'    '
name|'level'
op|'='
name|'sys'
op|'.'
name|'argv'
op|'['
number|'2'
op|']'
newline|'\n'
dedent|''
name|'if'
name|'checkPackageSummaries'
op|'('
name|'sys'
op|'.'
name|'argv'
op|'['
number|'1'
op|']'
op|','
name|'level'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'print'
op|'('
op|')'
newline|'\n'
name|'print'
op|'('
string|"'Missing javadocs were found!'"
op|')'
newline|'\n'
name|'sys'
op|'.'
name|'exit'
op|'('
number|'1'
op|')'
newline|'\n'
dedent|''
name|'sys'
op|'.'
name|'exit'
op|'('
number|'0'
op|')'
newline|'\n'
dedent|''
endmarker|''
end_unit
