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
name|'glob'
newline|'\n'
name|'import'
name|'datetime'
newline|'\n'
name|'import'
name|'tarfile'
newline|'\n'
name|'import'
name|'re'
newline|'\n'
nl|'\n'
name|'try'
op|':'
newline|'\n'
indent|'  '
name|'sys'
op|'.'
name|'argv'
op|'.'
name|'remove'
op|'('
string|"'-verbose'"
op|')'
newline|'\n'
DECL|variable|VERBOSE
name|'VERBOSE'
op|'='
name|'True'
newline|'\n'
dedent|''
name|'except'
name|'ValueError'
op|':'
newline|'\n'
DECL|variable|VERBOSE
indent|'  '
name|'VERBOSE'
op|'='
name|'False'
newline|'\n'
nl|'\n'
dedent|''
name|'try'
op|':'
newline|'\n'
indent|'  '
name|'sys'
op|'.'
name|'argv'
op|'.'
name|'remove'
op|'('
string|"'-docPerParagraph'"
op|')'
newline|'\n'
DECL|variable|docPerParagraph
name|'docPerParagraph'
op|'='
name|'True'
newline|'\n'
dedent|''
name|'except'
name|'ValueError'
op|':'
newline|'\n'
DECL|variable|docPerParagraph
indent|'  '
name|'docPerParagraph'
op|'='
name|'False'
newline|'\n'
nl|'\n'
DECL|variable|reChapterOnly
dedent|''
name|'reChapterOnly'
op|'='
name|'re'
op|'.'
name|'compile'
op|'('
string|"'^<CHAPTER ID=.*?>$'"
op|')'
newline|'\n'
DECL|variable|reTagOnly
name|'reTagOnly'
op|'='
name|'re'
op|'.'
name|'compile'
op|'('
string|"'^<.*?>$'"
op|')'
newline|'\n'
DECL|variable|reNumberOnly
name|'reNumberOnly'
op|'='
name|'re'
op|'.'
name|'compile'
op|'('
string|"r'^\\d+\\.?$'"
op|')'
newline|'\n'
nl|'\n'
DECL|variable|maxDoc
name|'maxDoc'
op|'='
number|'0'
newline|'\n'
DECL|variable|didEnglish
name|'didEnglish'
op|'='
name|'False'
newline|'\n'
nl|'\n'
DECL|function|write
name|'def'
name|'write'
op|'('
name|'date'
op|','
name|'title'
op|','
name|'pending'
op|','
name|'fOut'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'global'
name|'maxDoc'
newline|'\n'
name|'body'
op|'='
string|"' '"
op|'.'
name|'join'
op|'('
name|'pending'
op|')'
op|'.'
name|'replace'
op|'('
string|"'\\t'"
op|','
string|"' '"
op|')'
op|'.'
name|'strip'
op|'('
op|')'
newline|'\n'
name|'if'
name|'len'
op|'('
name|'body'
op|')'
op|'>'
number|'0'
op|':'
newline|'\n'
indent|'    '
name|'line'
op|'='
string|"'%s\\t%s\\t%s\\n'"
op|'%'
op|'('
name|'title'
op|','
name|'date'
op|','
name|'body'
op|')'
newline|'\n'
name|'fOut'
op|'.'
name|'write'
op|'('
name|'line'
op|')'
newline|'\n'
name|'maxDoc'
op|'+='
number|'1'
newline|'\n'
name|'del'
name|'pending'
op|'['
op|':'
op|']'
newline|'\n'
name|'if'
name|'VERBOSE'
op|':'
newline|'\n'
indent|'      '
name|'print'
name|'len'
op|'('
name|'body'
op|')'
newline|'\n'
nl|'\n'
DECL|function|processTar
dedent|''
dedent|''
dedent|''
name|'def'
name|'processTar'
op|'('
name|'fileName'
op|','
name|'fOut'
op|')'
op|':'
newline|'\n'
nl|'\n'
indent|'  '
name|'global'
name|'didEnglish'
newline|'\n'
nl|'\n'
name|'t'
op|'='
name|'tarfile'
op|'.'
name|'open'
op|'('
name|'fileName'
op|','
string|"'r:gz'"
op|')'
newline|'\n'
name|'for'
name|'ti'
name|'in'
name|'t'
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'ti'
op|'.'
name|'isfile'
op|'('
op|')'
name|'and'
op|'('
name|'not'
name|'didEnglish'
name|'or'
name|'ti'
op|'.'
name|'name'
op|'.'
name|'find'
op|'('
string|"'/en/'"
op|')'
op|'=='
op|'-'
number|'1'
op|')'
op|':'
newline|'\n'
nl|'\n'
indent|'      '
name|'tup'
op|'='
name|'ti'
op|'.'
name|'name'
op|'.'
name|'split'
op|'('
string|"'/'"
op|')'
newline|'\n'
name|'lang'
op|'='
name|'tup'
op|'['
number|'1'
op|']'
newline|'\n'
name|'year'
op|'='
name|'int'
op|'('
name|'tup'
op|'['
number|'2'
op|']'
op|'['
number|'3'
op|':'
number|'5'
op|']'
op|')'
newline|'\n'
name|'if'
name|'year'
op|'<'
number|'20'
op|':'
newline|'\n'
indent|'        '
name|'year'
op|'+='
number|'2000'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'        '
name|'year'
op|'+='
number|'1900'
newline|'\n'
nl|'\n'
dedent|''
name|'month'
op|'='
name|'int'
op|'('
name|'tup'
op|'['
number|'2'
op|']'
op|'['
number|'6'
op|':'
number|'8'
op|']'
op|')'
newline|'\n'
name|'day'
op|'='
name|'int'
op|'('
name|'tup'
op|'['
number|'2'
op|']'
op|'['
number|'9'
op|':'
number|'11'
op|']'
op|')'
newline|'\n'
name|'date'
op|'='
name|'datetime'
op|'.'
name|'date'
op|'('
name|'year'
op|'='
name|'year'
op|','
name|'month'
op|'='
name|'month'
op|','
name|'day'
op|'='
name|'day'
op|')'
newline|'\n'
nl|'\n'
name|'if'
name|'VERBOSE'
op|':'
newline|'\n'
indent|'        '
name|'print'
newline|'\n'
name|'print'
string|"'%s: %s'"
op|'%'
op|'('
name|'ti'
op|'.'
name|'name'
op|','
name|'date'
op|')'
newline|'\n'
dedent|''
name|'nextIsTitle'
op|'='
name|'False'
newline|'\n'
name|'title'
op|'='
name|'None'
newline|'\n'
name|'pending'
op|'='
op|'['
op|']'
newline|'\n'
name|'for'
name|'line'
name|'in'
name|'t'
op|'.'
name|'extractfile'
op|'('
name|'ti'
op|')'
op|'.'
name|'readlines'
op|'('
op|')'
op|':'
newline|'\n'
indent|'        '
name|'line'
op|'='
name|'line'
op|'.'
name|'strip'
op|'('
op|')'
newline|'\n'
name|'if'
name|'reChapterOnly'
op|'.'
name|'match'
op|'('
name|'line'
op|')'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
indent|'          '
name|'if'
name|'title'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
indent|'            '
name|'write'
op|'('
name|'date'
op|','
name|'title'
op|','
name|'pending'
op|','
name|'fOut'
op|')'
newline|'\n'
dedent|''
name|'nextIsTitle'
op|'='
name|'True'
newline|'\n'
name|'continue'
newline|'\n'
dedent|''
name|'if'
name|'nextIsTitle'
op|':'
newline|'\n'
indent|'          '
name|'if'
name|'not'
name|'reNumberOnly'
op|'.'
name|'match'
op|'('
name|'line'
op|')'
name|'and'
name|'not'
name|'reTagOnly'
op|'.'
name|'match'
op|'('
name|'line'
op|')'
op|':'
newline|'\n'
indent|'            '
name|'title'
op|'='
name|'line'
newline|'\n'
name|'nextIsTitle'
op|'='
name|'False'
newline|'\n'
name|'if'
name|'VERBOSE'
op|':'
newline|'\n'
indent|'              '
name|'print'
string|"'  title %s'"
op|'%'
name|'line'
newline|'\n'
dedent|''
dedent|''
name|'continue'
newline|'\n'
dedent|''
name|'if'
name|'line'
op|'.'
name|'lower'
op|'('
op|')'
op|'=='
string|"'<p>'"
op|':'
newline|'\n'
indent|'          '
name|'if'
name|'docPerParagraph'
op|':'
newline|'\n'
indent|'            '
name|'write'
op|'('
name|'date'
op|','
name|'title'
op|','
name|'pending'
op|','
name|'fOut'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'            '
name|'pending'
op|'.'
name|'append'
op|'('
string|"'PARSEP'"
op|')'
newline|'\n'
dedent|''
dedent|''
name|'elif'
name|'not'
name|'reTagOnly'
op|'.'
name|'match'
op|'('
name|'line'
op|')'
op|':'
newline|'\n'
indent|'          '
name|'pending'
op|'.'
name|'append'
op|'('
name|'line'
op|')'
newline|'\n'
dedent|''
dedent|''
name|'if'
name|'title'
name|'is'
name|'not'
name|'None'
name|'and'
name|'len'
op|'('
name|'pending'
op|')'
op|'>'
number|'0'
op|':'
newline|'\n'
indent|'        '
name|'write'
op|'('
name|'date'
op|','
name|'title'
op|','
name|'pending'
op|','
name|'fOut'
op|')'
newline|'\n'
nl|'\n'
dedent|''
dedent|''
dedent|''
name|'didEnglish'
op|'='
name|'True'
newline|'\n'
nl|'\n'
comment|"# '/x/lucene/data/europarl/all.lines.txt'"
nl|'\n'
DECL|variable|dirIn
dedent|''
name|'dirIn'
op|'='
name|'sys'
op|'.'
name|'argv'
op|'['
number|'1'
op|']'
newline|'\n'
DECL|variable|fileOut
name|'fileOut'
op|'='
name|'sys'
op|'.'
name|'argv'
op|'['
number|'2'
op|']'
newline|'\n'
nl|'\n'
DECL|variable|fOut
name|'fOut'
op|'='
name|'open'
op|'('
name|'fileOut'
op|','
string|"'wb'"
op|')'
newline|'\n'
nl|'\n'
name|'for'
name|'fileName'
name|'in'
name|'glob'
op|'.'
name|'glob'
op|'('
string|"'%s/??-??.tgz'"
op|'%'
name|'dirIn'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'if'
name|'fileName'
op|'.'
name|'endswith'
op|'('
string|"'.tgz'"
op|')'
op|':'
newline|'\n'
indent|'    '
name|'print'
string|"'process %s; %d docs so far...'"
op|'%'
op|'('
name|'fileName'
op|','
name|'maxDoc'
op|')'
newline|'\n'
name|'processTar'
op|'('
name|'fileName'
op|','
name|'fOut'
op|')'
newline|'\n'
nl|'\n'
dedent|''
dedent|''
name|'print'
string|"'TOTAL: %s'"
op|'%'
name|'maxDoc'
newline|'\n'
nl|'\n'
comment|'#run something like this:'
nl|'\n'
string|'"""\n\n# Europarl V5 makes 76,917 docs, avg 38.6 KB per\npython -u europarl.py /x/lucene/data/europarl /x/lucene/data/europarl/tmp.lines.txt\nshuf /x/lucene/data/europarl/tmp.lines.txt > /x/lucene/data/europarl/full.lines.txt\nrm /x/lucene/data/europarl/tmp.lines.txt\n\n# Run again, this time each paragraph is a doc:\n# Europarl V5 makes 5,607,746 paragraphs (one paragraph per line), avg 620 bytes per:\npython -u europarl.py /x/lucene/data/europarl /x/lucene/data/europarl/tmp.lines.txt -docPerParagraph\nshuf /x/lucene/data/europarl/tmp.lines.txt > /x/lucene/data/europarl/para.lines.txt\nrm /x/lucene/data/europarl/tmp.lines.txt\n\n# ~5.5 MB gzip\'d:\nhead -200 /x/lucene/data/europarl/full.lines.txt > tmp.txt\nhead -10000 /x/lucene/data/europarl/para.lines.txt >> tmp.txt\nshuf tmp.txt > europarl.subset.txt\nrm -f tmp.txt\ngzip --best europarl.subset.txt\n"""'
newline|'\n'
endmarker|''
end_unit
