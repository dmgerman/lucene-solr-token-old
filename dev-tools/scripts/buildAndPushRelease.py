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
name|'datetime'
newline|'\n'
name|'import'
name|'re'
newline|'\n'
name|'import'
name|'time'
newline|'\n'
name|'import'
name|'shutil'
newline|'\n'
name|'import'
name|'os'
newline|'\n'
name|'import'
name|'sys'
newline|'\n'
name|'import'
name|'subprocess'
newline|'\n'
nl|'\n'
comment|'# Usage: python3.2 -u buildAndPushRelease.py [-sign gpgKey(eg: 6E68DA61)] [-prepare] [-push userName] [-pushLocal dirName] [-smoke tmpDir] /path/to/checkout version(eg: 3.4.0) rcNum(eg: 0)'
nl|'\n'
comment|'#'
nl|'\n'
comment|'# EG: python3.2 -u buildAndPushRelease.py -prepare -push -sign 6E68DA61 mikemccand /lucene/34x 3.4.0 0'
nl|'\n'
nl|'\n'
comment|'# NOTE: if you specify -sign, you have to type in your gpg password at'
nl|'\n'
comment|"# some point while this runs; it's VERY confusing because the output"
nl|'\n'
comment|'# is directed to /tmp/release.log, so, you have to tail that and when'
nl|'\n'
comment|'# GPG wants your password, type it!  Also sometimes you have to type'
nl|'\n'
comment|'# it twice in a row!'
nl|'\n'
nl|'\n'
DECL|variable|LOG
name|'LOG'
op|'='
string|"'/tmp/release.log'"
newline|'\n'
nl|'\n'
DECL|function|log
name|'def'
name|'log'
op|'('
name|'msg'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'f'
op|'='
name|'open'
op|'('
name|'LOG'
op|','
name|'mode'
op|'='
string|"'ab'"
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
name|'msg'
op|'.'
name|'encode'
op|'('
string|"'utf-8'"
op|')'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'close'
op|'('
op|')'
newline|'\n'
nl|'\n'
DECL|function|run
dedent|''
name|'def'
name|'run'
op|'('
name|'command'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'log'
op|'('
string|"'\\n\\n%s: RUN: %s\\n'"
op|'%'
op|'('
name|'datetime'
op|'.'
name|'datetime'
op|'.'
name|'now'
op|'('
op|')'
op|','
name|'command'
op|')'
op|')'
newline|'\n'
name|'if'
name|'os'
op|'.'
name|'system'
op|'('
string|"'%s >> %s 2>&1'"
op|'%'
op|'('
name|'command'
op|','
name|'LOG'
op|')'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'msg'
op|'='
string|"'    FAILED: %s [see log %s]'"
op|'%'
op|'('
name|'command'
op|','
name|'LOG'
op|')'
newline|'\n'
name|'print'
op|'('
name|'msg'
op|')'
newline|'\n'
name|'raise'
name|'RuntimeError'
op|'('
name|'msg'
op|')'
newline|'\n'
nl|'\n'
DECL|function|runAndSendGPGPassword
dedent|''
dedent|''
name|'def'
name|'runAndSendGPGPassword'
op|'('
name|'command'
op|','
name|'password'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'p'
op|'='
name|'subprocess'
op|'.'
name|'Popen'
op|'('
name|'command'
op|','
name|'shell'
op|'='
name|'True'
op|','
name|'bufsize'
op|'='
number|'0'
op|','
name|'stdout'
op|'='
name|'subprocess'
op|'.'
name|'PIPE'
op|','
name|'stderr'
op|'='
name|'subprocess'
op|'.'
name|'STDOUT'
op|','
name|'stdin'
op|'='
name|'subprocess'
op|'.'
name|'PIPE'
op|')'
newline|'\n'
name|'f'
op|'='
name|'open'
op|'('
name|'LOG'
op|','
string|"'ab'"
op|')'
newline|'\n'
name|'while'
name|'True'
op|':'
newline|'\n'
indent|'    '
name|'p'
op|'.'
name|'stdout'
op|'.'
name|'flush'
op|'('
op|')'
newline|'\n'
name|'line'
op|'='
name|'p'
op|'.'
name|'stdout'
op|'.'
name|'readline'
op|'('
op|')'
newline|'\n'
name|'if'
name|'len'
op|'('
name|'line'
op|')'
op|'=='
number|'0'
op|':'
newline|'\n'
indent|'      '
name|'break'
newline|'\n'
dedent|''
name|'f'
op|'.'
name|'write'
op|'('
name|'line'
op|')'
newline|'\n'
name|'if'
name|'line'
op|'.'
name|'find'
op|'('
string|"b'Enter GPG keystore password:'"
op|')'
op|'!='
op|'-'
number|'1'
op|':'
newline|'\n'
indent|'      '
name|'time'
op|'.'
name|'sleep'
op|'('
number|'1.0'
op|')'
newline|'\n'
name|'p'
op|'.'
name|'stdin'
op|'.'
name|'write'
op|'('
op|'('
name|'password'
op|'+'
string|"'\\n'"
op|')'
op|'.'
name|'encode'
op|'('
string|"'UTF-8'"
op|')'
op|')'
newline|'\n'
name|'p'
op|'.'
name|'stdin'
op|'.'
name|'write'
op|'('
string|"'\\n'"
op|'.'
name|'encode'
op|'('
string|"'UTF-8'"
op|')'
op|')'
newline|'\n'
nl|'\n'
dedent|''
dedent|''
name|'result'
op|'='
name|'p'
op|'.'
name|'poll'
op|'('
op|')'
newline|'\n'
name|'if'
name|'result'
op|'!='
number|'0'
op|':'
newline|'\n'
indent|'    '
name|'msg'
op|'='
string|"'    FAILED: %s [see log %s]'"
op|'%'
op|'('
name|'command'
op|','
name|'LOG'
op|')'
newline|'\n'
name|'print'
op|'('
name|'msg'
op|')'
newline|'\n'
name|'raise'
name|'RuntimeError'
op|'('
name|'msg'
op|')'
newline|'\n'
nl|'\n'
DECL|function|scrubCheckout
dedent|''
dedent|''
name|'def'
name|'scrubCheckout'
op|'('
op|')'
op|':'
newline|'\n'
comment|'# removes any files not checked into svn'
nl|'\n'
nl|'\n'
indent|'  '
name|'unversionedRex'
op|'='
name|'re'
op|'.'
name|'compile'
op|'('
string|"'^ ?[\\?ID] *[1-9 ]*[a-zA-Z]* +(.*)'"
op|')'
newline|'\n'
nl|'\n'
name|'for'
name|'l'
name|'in'
name|'os'
op|'.'
name|'popen'
op|'('
string|"'svn status --no-ignore -v'"
op|')'
op|'.'
name|'readlines'
op|'('
op|')'
op|':'
newline|'\n'
indent|'    '
name|'match'
op|'='
name|'unversionedRex'
op|'.'
name|'match'
op|'('
name|'l'
op|')'
newline|'\n'
name|'if'
name|'match'
op|':'
newline|'\n'
indent|'      '
name|'s'
op|'='
name|'match'
op|'.'
name|'group'
op|'('
number|'1'
op|')'
newline|'\n'
name|'if'
name|'os'
op|'.'
name|'path'
op|'.'
name|'exists'
op|'('
name|'s'
op|')'
op|':'
newline|'\n'
indent|'        '
name|'print'
op|'('
string|"'    delete %s'"
op|'%'
name|'s'
op|')'
newline|'\n'
name|'if'
name|'os'
op|'.'
name|'path'
op|'.'
name|'isdir'
op|'('
name|'s'
op|')'
name|'and'
name|'not'
name|'os'
op|'.'
name|'path'
op|'.'
name|'islink'
op|'('
name|'s'
op|')'
op|':'
newline|'\n'
indent|'          '
name|'shutil'
op|'.'
name|'rmtree'
op|'('
name|'s'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'          '
name|'os'
op|'.'
name|'remove'
op|'('
name|'s'
op|')'
newline|'\n'
nl|'\n'
DECL|function|getSVNRev
dedent|''
dedent|''
dedent|''
dedent|''
dedent|''
name|'def'
name|'getSVNRev'
op|'('
op|')'
op|':'
newline|'\n'
indent|'  '
name|'rev'
op|'='
name|'os'
op|'.'
name|'popen'
op|'('
string|"'svnversion'"
op|')'
op|'.'
name|'read'
op|'('
op|')'
op|'.'
name|'strip'
op|'('
op|')'
newline|'\n'
name|'try'
op|':'
newline|'\n'
indent|'    '
name|'int'
op|'('
name|'rev'
op|')'
newline|'\n'
dedent|''
name|'except'
op|'('
name|'TypeError'
op|','
name|'ValueError'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'raise'
name|'RuntimeError'
op|'('
string|"'svn version is not clean: %s'"
op|'%'
name|'rev'
op|')'
newline|'\n'
dedent|''
name|'return'
name|'rev'
newline|'\n'
nl|'\n'
nl|'\n'
DECL|function|prepare
dedent|''
name|'def'
name|'prepare'
op|'('
name|'root'
op|','
name|'version'
op|','
name|'gpgKeyID'
op|','
name|'gpgPassword'
op|','
name|'doTest'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'print'
op|'('
op|')'
newline|'\n'
name|'print'
op|'('
string|"'Prepare release...'"
op|')'
newline|'\n'
name|'if'
name|'os'
op|'.'
name|'path'
op|'.'
name|'exists'
op|'('
name|'LOG'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'os'
op|'.'
name|'remove'
op|'('
name|'LOG'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'os'
op|'.'
name|'chdir'
op|'('
name|'root'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'  svn up...'"
op|')'
newline|'\n'
name|'run'
op|'('
string|"'svn up'"
op|')'
newline|'\n'
nl|'\n'
name|'rev'
op|'='
name|'getSVNRev'
op|'('
op|')'
newline|'\n'
name|'print'
op|'('
string|"'  svn rev: %s'"
op|'%'
name|'rev'
op|')'
newline|'\n'
name|'log'
op|'('
string|"'\\nSVN rev: %s\\n'"
op|'%'
name|'rev'
op|')'
newline|'\n'
nl|'\n'
name|'if'
name|'doTest'
op|':'
newline|'\n'
comment|"# Don't run tests if we are gonna smoke test after the release..."
nl|'\n'
indent|'    '
name|'print'
op|'('
string|"'  ant clean test'"
op|')'
newline|'\n'
name|'run'
op|'('
string|"'ant clean test'"
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'print'
op|'('
string|"'  clean checkout'"
op|')'
newline|'\n'
name|'scrubCheckout'
op|'('
op|')'
newline|'\n'
name|'open'
op|'('
string|"'rev.txt'"
op|','
name|'mode'
op|'='
string|"'wb'"
op|')'
op|'.'
name|'write'
op|'('
name|'rev'
op|'.'
name|'encode'
op|'('
string|"'UTF-8'"
op|')'
op|')'
newline|'\n'
nl|'\n'
name|'print'
op|'('
string|"'  lucene prepare-release'"
op|')'
newline|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
string|"'lucene'"
op|')'
newline|'\n'
name|'cmd'
op|'='
string|"'ant -Dversion=%s -Dspecversion=%s'"
op|'%'
op|'('
name|'version'
op|','
name|'version'
op|')'
newline|'\n'
name|'if'
name|'gpgKeyID'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
indent|'    '
name|'cmd'
op|'+='
string|"' -Dgpg.key=%s prepare-release'"
op|'%'
name|'gpgKeyID'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'cmd'
op|'+='
string|"' prepare-release-no-sign'"
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'gpgPassword'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
indent|'    '
name|'runAndSendGPGPassword'
op|'('
name|'cmd'
op|','
name|'gpgPassword'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'run'
op|'('
name|'cmd'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'print'
op|'('
string|"'  solr prepare-release'"
op|')'
newline|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
string|"'../solr'"
op|')'
newline|'\n'
name|'cmd'
op|'='
string|"'ant -Dversion=%s -Dspecversion=%s'"
op|'%'
op|'('
name|'version'
op|','
name|'version'
op|')'
newline|'\n'
name|'if'
name|'gpgKeyID'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
indent|'    '
name|'cmd'
op|'+='
string|"' -Dgpg.key=%s prepare-release'"
op|'%'
name|'gpgKeyID'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'cmd'
op|'+='
string|"' prepare-release-no-sign'"
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'gpgPassword'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
indent|'    '
name|'runAndSendGPGPassword'
op|'('
name|'cmd'
op|','
name|'gpgPassword'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'run'
op|'('
name|'cmd'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'print'
op|'('
string|"'  done!'"
op|')'
newline|'\n'
name|'print'
op|'('
op|')'
newline|'\n'
name|'return'
name|'rev'
newline|'\n'
nl|'\n'
DECL|function|push
dedent|''
name|'def'
name|'push'
op|'('
name|'version'
op|','
name|'root'
op|','
name|'rev'
op|','
name|'rcNum'
op|','
name|'username'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'print'
op|'('
string|"'Push...'"
op|')'
newline|'\n'
name|'dir'
op|'='
string|"'lucene-solr-%s-RC%d-rev%s'"
op|'%'
op|'('
name|'version'
op|','
name|'rcNum'
op|','
name|'rev'
op|')'
newline|'\n'
name|'s'
op|'='
name|'os'
op|'.'
name|'popen'
op|'('
string|'\'ssh %s@people.apache.org "ls -ld public_html/staging_area/%s" 2>&1\''
op|'%'
op|'('
name|'username'
op|','
name|'dir'
op|')'
op|')'
op|'.'
name|'read'
op|'('
op|')'
newline|'\n'
name|'if'
string|"'no such file or directory'"
name|'not'
name|'in'
name|'s'
op|'.'
name|'lower'
op|'('
op|')'
op|':'
newline|'\n'
indent|'    '
name|'print'
op|'('
string|"'  Remove old dir...'"
op|')'
newline|'\n'
name|'run'
op|'('
string|'\'ssh %s@people.apache.org "chmod -R u+rwX public_html/staging_area/%s; rm -rf public_html/staging_area/%s"\''
op|'%'
nl|'\n'
op|'('
name|'username'
op|','
name|'dir'
op|','
name|'dir'
op|')'
op|')'
newline|'\n'
dedent|''
name|'run'
op|'('
string|'\'ssh %s@people.apache.org "mkdir -p public_html/staging_area/%s/lucene public_html/staging_area/%s/solr"\''
op|'%'
op|'('
name|'username'
op|','
name|'dir'
op|','
name|'dir'
op|')'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'  Lucene'"
op|')'
newline|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
string|"'%s/lucene/dist'"
op|'%'
name|'root'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'    zip...'"
op|')'
newline|'\n'
name|'if'
name|'os'
op|'.'
name|'path'
op|'.'
name|'exists'
op|'('
string|"'lucene.tar.bz2'"
op|')'
op|':'
newline|'\n'
indent|'    '
name|'os'
op|'.'
name|'remove'
op|'('
string|"'lucene.tar.bz2'"
op|')'
newline|'\n'
dedent|''
name|'run'
op|'('
string|"'tar cjf lucene.tar.bz2 *'"
op|')'
newline|'\n'
name|'print'
op|'('
string|"'    copy...'"
op|')'
newline|'\n'
name|'run'
op|'('
string|"'scp lucene.tar.bz2 %s@people.apache.org:public_html/staging_area/%s/lucene'"
op|'%'
op|'('
name|'username'
op|','
name|'dir'
op|')'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'    unzip...'"
op|')'
newline|'\n'
name|'run'
op|'('
string|'\'ssh %s@people.apache.org "cd public_html/staging_area/%s/lucene; tar xjf lucene.tar.bz2; rm -f lucene.tar.bz2"\''
op|'%'
op|'('
name|'username'
op|','
name|'dir'
op|')'
op|')'
newline|'\n'
name|'os'
op|'.'
name|'remove'
op|'('
string|"'lucene.tar.bz2'"
op|')'
newline|'\n'
nl|'\n'
name|'print'
op|'('
string|"'  Solr'"
op|')'
newline|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
string|"'%s/solr/package'"
op|'%'
name|'root'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'    zip...'"
op|')'
newline|'\n'
name|'if'
name|'os'
op|'.'
name|'path'
op|'.'
name|'exists'
op|'('
string|"'solr.tar.bz2'"
op|')'
op|':'
newline|'\n'
indent|'    '
name|'os'
op|'.'
name|'remove'
op|'('
string|"'solr.tar.bz2'"
op|')'
newline|'\n'
dedent|''
name|'run'
op|'('
string|"'tar cjf solr.tar.bz2 *'"
op|')'
newline|'\n'
name|'print'
op|'('
string|"'    copy...'"
op|')'
newline|'\n'
name|'run'
op|'('
string|"'scp solr.tar.bz2 %s@people.apache.org:public_html/staging_area/%s/solr'"
op|'%'
op|'('
name|'username'
op|','
name|'dir'
op|')'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'    unzip...'"
op|')'
newline|'\n'
name|'run'
op|'('
string|'\'ssh %s@people.apache.org "cd public_html/staging_area/%s/solr; tar xjf solr.tar.bz2; rm -f solr.tar.bz2"\''
op|'%'
op|'('
name|'username'
op|','
name|'dir'
op|')'
op|')'
newline|'\n'
name|'os'
op|'.'
name|'remove'
op|'('
string|"'solr.tar.bz2'"
op|')'
newline|'\n'
nl|'\n'
name|'print'
op|'('
string|"'  chmod...'"
op|')'
newline|'\n'
name|'run'
op|'('
string|'\'ssh %s@people.apache.org "chmod -R a+rX-w public_html/staging_area/%s"\''
op|'%'
op|'('
name|'username'
op|','
name|'dir'
op|')'
op|')'
newline|'\n'
nl|'\n'
name|'print'
op|'('
string|"'  done!'"
op|')'
newline|'\n'
name|'url'
op|'='
string|"'https://people.apache.org/~%s/staging_area/%s'"
op|'%'
op|'('
name|'username'
op|','
name|'dir'
op|')'
newline|'\n'
name|'return'
name|'url'
newline|'\n'
nl|'\n'
DECL|function|pushLocal
dedent|''
name|'def'
name|'pushLocal'
op|'('
name|'version'
op|','
name|'root'
op|','
name|'rev'
op|','
name|'rcNum'
op|','
name|'localDir'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'print'
op|'('
string|"'Push local [%s]...'"
op|'%'
name|'localDir'
op|')'
newline|'\n'
name|'os'
op|'.'
name|'makedirs'
op|'('
name|'localDir'
op|')'
newline|'\n'
nl|'\n'
name|'dir'
op|'='
string|"'lucene-solr-%s-RC%d-rev%s'"
op|'%'
op|'('
name|'version'
op|','
name|'rcNum'
op|','
name|'rev'
op|')'
newline|'\n'
name|'os'
op|'.'
name|'makedirs'
op|'('
string|"'%s/%s/lucene'"
op|'%'
op|'('
name|'localDir'
op|','
name|'dir'
op|')'
op|')'
newline|'\n'
name|'os'
op|'.'
name|'makedirs'
op|'('
string|"'%s/%s/solr'"
op|'%'
op|'('
name|'localDir'
op|','
name|'dir'
op|')'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'  Lucene'"
op|')'
newline|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
string|"'%s/lucene/dist'"
op|'%'
name|'root'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'    zip...'"
op|')'
newline|'\n'
name|'if'
name|'os'
op|'.'
name|'path'
op|'.'
name|'exists'
op|'('
string|"'lucene.tar.bz2'"
op|')'
op|':'
newline|'\n'
indent|'    '
name|'os'
op|'.'
name|'remove'
op|'('
string|"'lucene.tar.bz2'"
op|')'
newline|'\n'
dedent|''
name|'run'
op|'('
string|"'tar cjf lucene.tar.bz2 *'"
op|')'
newline|'\n'
nl|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
string|"'%s/%s/lucene'"
op|'%'
op|'('
name|'localDir'
op|','
name|'dir'
op|')'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'    unzip...'"
op|')'
newline|'\n'
name|'run'
op|'('
string|'\'tar xjf "%s/lucene/dist/lucene.tar.bz2"\''
op|'%'
name|'root'
op|')'
newline|'\n'
name|'os'
op|'.'
name|'remove'
op|'('
string|"'%s/lucene/dist/lucene.tar.bz2'"
op|'%'
name|'root'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'    copy changes...'"
op|')'
newline|'\n'
name|'run'
op|'('
string|'\'cp -r "%s/lucene/build/docs/changes" changes-%s\''
op|'%'
op|'('
name|'root'
op|','
name|'version'
op|')'
op|')'
newline|'\n'
nl|'\n'
name|'print'
op|'('
string|"'  Solr'"
op|')'
newline|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
string|"'%s/solr/package'"
op|'%'
name|'root'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'    zip...'"
op|')'
newline|'\n'
name|'if'
name|'os'
op|'.'
name|'path'
op|'.'
name|'exists'
op|'('
string|"'solr.tar.bz2'"
op|')'
op|':'
newline|'\n'
indent|'    '
name|'os'
op|'.'
name|'remove'
op|'('
string|"'solr.tar.bz2'"
op|')'
newline|'\n'
dedent|''
name|'run'
op|'('
string|"'tar cjf solr.tar.bz2 *'"
op|')'
newline|'\n'
name|'print'
op|'('
string|"'    unzip...'"
op|')'
newline|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
string|"'%s/%s/solr'"
op|'%'
op|'('
name|'localDir'
op|','
name|'dir'
op|')'
op|')'
newline|'\n'
name|'run'
op|'('
string|'\'tar xjf "%s/solr/package/solr.tar.bz2"\''
op|'%'
name|'root'
op|')'
newline|'\n'
name|'os'
op|'.'
name|'remove'
op|'('
string|"'%s/solr/package/solr.tar.bz2'"
op|'%'
name|'root'
op|')'
newline|'\n'
nl|'\n'
name|'print'
op|'('
string|"'  KEYS'"
op|')'
newline|'\n'
name|'run'
op|'('
string|"'wget http://people.apache.org/keys/group/lucene.asc'"
op|')'
newline|'\n'
name|'os'
op|'.'
name|'rename'
op|'('
string|"'lucene.asc'"
op|','
string|"'KEYS'"
op|')'
newline|'\n'
name|'run'
op|'('
string|"'chmod a+r-w KEYS'"
op|')'
newline|'\n'
name|'run'
op|'('
string|"'cp KEYS ../lucene'"
op|')'
newline|'\n'
nl|'\n'
name|'print'
op|'('
string|"'  chmod...'"
op|')'
newline|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
string|"'..'"
op|')'
newline|'\n'
name|'run'
op|'('
string|"'chmod -R a+rX-w .'"
op|')'
newline|'\n'
nl|'\n'
name|'print'
op|'('
string|"'  done!'"
op|')'
newline|'\n'
name|'return'
string|"'file://%s/%s'"
op|'%'
op|'('
name|'os'
op|'.'
name|'path'
op|'.'
name|'abspath'
op|'('
name|'localDir'
op|')'
op|','
name|'dir'
op|')'
newline|'\n'
nl|'\n'
DECL|function|main
dedent|''
name|'def'
name|'main'
op|'('
op|')'
op|':'
newline|'\n'
indent|'  '
name|'doPrepare'
op|'='
string|"'-prepare'"
name|'in'
name|'sys'
op|'.'
name|'argv'
newline|'\n'
name|'if'
name|'doPrepare'
op|':'
newline|'\n'
indent|'    '
name|'sys'
op|'.'
name|'argv'
op|'.'
name|'remove'
op|'('
string|"'-prepare'"
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'try'
op|':'
newline|'\n'
indent|'    '
name|'idx'
op|'='
name|'sys'
op|'.'
name|'argv'
op|'.'
name|'index'
op|'('
string|"'-push'"
op|')'
newline|'\n'
dedent|''
name|'except'
name|'ValueError'
op|':'
newline|'\n'
indent|'    '
name|'doPushRemote'
op|'='
name|'False'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'doPushRemote'
op|'='
name|'True'
newline|'\n'
name|'username'
op|'='
name|'sys'
op|'.'
name|'argv'
op|'['
name|'idx'
op|'+'
number|'1'
op|']'
newline|'\n'
name|'del'
name|'sys'
op|'.'
name|'argv'
op|'['
name|'idx'
op|':'
name|'idx'
op|'+'
number|'2'
op|']'
newline|'\n'
nl|'\n'
dedent|''
name|'try'
op|':'
newline|'\n'
indent|'    '
name|'idx'
op|'='
name|'sys'
op|'.'
name|'argv'
op|'.'
name|'index'
op|'('
string|"'-smoke'"
op|')'
newline|'\n'
dedent|''
name|'except'
name|'ValueError'
op|':'
newline|'\n'
indent|'    '
name|'smokeTmpDir'
op|'='
name|'None'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'smokeTmpDir'
op|'='
name|'sys'
op|'.'
name|'argv'
op|'['
name|'idx'
op|'+'
number|'1'
op|']'
newline|'\n'
name|'del'
name|'sys'
op|'.'
name|'argv'
op|'['
name|'idx'
op|':'
name|'idx'
op|'+'
number|'2'
op|']'
newline|'\n'
name|'if'
name|'os'
op|'.'
name|'path'
op|'.'
name|'exists'
op|'('
name|'smokeTmpDir'
op|')'
op|':'
newline|'\n'
indent|'      '
name|'print'
op|'('
op|')'
newline|'\n'
name|'print'
op|'('
string|'\'ERROR: smoke tmpDir "%s" exists; please remove first\''
op|'%'
name|'smokeTmpDir'
op|')'
newline|'\n'
name|'print'
op|'('
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
name|'try'
op|':'
newline|'\n'
indent|'    '
name|'idx'
op|'='
name|'sys'
op|'.'
name|'argv'
op|'.'
name|'index'
op|'('
string|"'-pushLocal'"
op|')'
newline|'\n'
dedent|''
name|'except'
name|'ValueError'
op|':'
newline|'\n'
indent|'    '
name|'doPushLocal'
op|'='
name|'False'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'doPushLocal'
op|'='
name|'True'
newline|'\n'
name|'localStagingDir'
op|'='
name|'sys'
op|'.'
name|'argv'
op|'['
name|'idx'
op|'+'
number|'1'
op|']'
newline|'\n'
name|'del'
name|'sys'
op|'.'
name|'argv'
op|'['
name|'idx'
op|':'
name|'idx'
op|'+'
number|'2'
op|']'
newline|'\n'
name|'if'
name|'os'
op|'.'
name|'path'
op|'.'
name|'exists'
op|'('
name|'localStagingDir'
op|')'
op|':'
newline|'\n'
indent|'      '
name|'print'
op|'('
op|')'
newline|'\n'
name|'print'
op|'('
string|'\'ERROR: pushLocal dir "%s" exists; please remove first\''
op|'%'
name|'localStagingDir'
op|')'
newline|'\n'
name|'print'
op|'('
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
name|'if'
name|'doPushRemote'
name|'and'
name|'doPushLocal'
op|':'
newline|'\n'
indent|'    '
name|'print'
op|'('
op|')'
newline|'\n'
name|'print'
op|'('
string|"'ERROR: specify at most one of -push or -pushLocal (got both)'"
op|')'
newline|'\n'
name|'print'
op|'('
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
name|'try'
op|':'
newline|'\n'
indent|'    '
name|'idx'
op|'='
name|'sys'
op|'.'
name|'argv'
op|'.'
name|'index'
op|'('
string|"'-sign'"
op|')'
newline|'\n'
dedent|''
name|'except'
name|'ValueError'
op|':'
newline|'\n'
indent|'    '
name|'gpgKeyID'
op|'='
name|'None'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'gpgKeyID'
op|'='
name|'sys'
op|'.'
name|'argv'
op|'['
name|'idx'
op|'+'
number|'1'
op|']'
newline|'\n'
name|'del'
name|'sys'
op|'.'
name|'argv'
op|'['
name|'idx'
op|':'
name|'idx'
op|'+'
number|'2'
op|']'
newline|'\n'
nl|'\n'
name|'sys'
op|'.'
name|'stdout'
op|'.'
name|'flush'
op|'('
op|')'
newline|'\n'
name|'import'
name|'getpass'
newline|'\n'
name|'gpgPassword'
op|'='
name|'getpass'
op|'.'
name|'getpass'
op|'('
string|"'Enter GPG keystore password: '"
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'root'
op|'='
name|'os'
op|'.'
name|'path'
op|'.'
name|'abspath'
op|'('
name|'sys'
op|'.'
name|'argv'
op|'['
number|'1'
op|']'
op|')'
newline|'\n'
name|'version'
op|'='
name|'sys'
op|'.'
name|'argv'
op|'['
number|'2'
op|']'
newline|'\n'
name|'rcNum'
op|'='
name|'int'
op|'('
name|'sys'
op|'.'
name|'argv'
op|'['
number|'3'
op|']'
op|')'
newline|'\n'
nl|'\n'
name|'if'
name|'doPrepare'
op|':'
newline|'\n'
indent|'    '
name|'rev'
op|'='
name|'prepare'
op|'('
name|'root'
op|','
name|'version'
op|','
name|'gpgKeyID'
op|','
name|'gpgPassword'
op|','
name|'smokeTmpDir'
name|'is'
name|'None'
op|')'
newline|'\n'
dedent|''
name|'else'
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
name|'rev'
op|'='
name|'open'
op|'('
string|"'rev.txt'"
op|','
name|'encoding'
op|'='
string|"'UTF-8'"
op|')'
op|'.'
name|'read'
op|'('
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'doPushRemote'
op|':'
newline|'\n'
indent|'    '
name|'url'
op|'='
name|'push'
op|'('
name|'version'
op|','
name|'root'
op|','
name|'rev'
op|','
name|'rcNum'
op|','
name|'username'
op|')'
newline|'\n'
dedent|''
name|'elif'
name|'doPushLocal'
op|':'
newline|'\n'
indent|'    '
name|'url'
op|'='
name|'pushLocal'
op|'('
name|'version'
op|','
name|'root'
op|','
name|'rev'
op|','
name|'rcNum'
op|','
name|'localStagingDir'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'url'
op|'='
name|'None'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'url'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
indent|'    '
name|'print'
op|'('
string|"'  URL: %s'"
op|'%'
name|'url'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'smokeTmpDir'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
indent|'    '
name|'import'
name|'smokeTestRelease'
newline|'\n'
name|'smokeTestRelease'
op|'.'
name|'DEBUG'
op|'='
name|'False'
newline|'\n'
name|'smokeTestRelease'
op|'.'
name|'smokeTest'
op|'('
name|'url'
op|','
name|'rev'
op|','
name|'version'
op|','
name|'smokeTmpDir'
op|','
name|'gpgKeyID'
name|'is'
name|'not'
name|'None'
op|','
string|"''"
op|')'
newline|'\n'
nl|'\n'
dedent|''
dedent|''
name|'if'
name|'__name__'
op|'=='
string|"'__main__'"
op|':'
newline|'\n'
indent|'  '
name|'try'
op|':'
newline|'\n'
indent|'    '
name|'main'
op|'('
op|')'
newline|'\n'
dedent|''
name|'except'
op|':'
newline|'\n'
indent|'    '
name|'import'
name|'traceback'
newline|'\n'
name|'traceback'
op|'.'
name|'print_exc'
op|'('
op|')'
newline|'\n'
dedent|''
dedent|''
endmarker|''
end_unit
