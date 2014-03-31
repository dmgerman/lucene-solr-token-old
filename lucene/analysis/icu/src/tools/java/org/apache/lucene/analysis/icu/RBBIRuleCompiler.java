begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.icu
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|icu
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FilenameFilter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import
begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|RuleBasedBreakIterator
import|;
end_import
begin_comment
comment|/**  * Command-line utility to converts RuleBasedBreakIterator (.rbbi) files into  * binary compiled form (.brk).  */
end_comment
begin_class
DECL|class|RBBIRuleCompiler
specifier|public
class|class
name|RBBIRuleCompiler
block|{
DECL|method|getRules
specifier|static
name|String
name|getRules
parameter_list|(
name|File
name|ruleFile
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|rules
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|InputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|ruleFile
argument_list|)
decl_stmt|;
name|BufferedReader
name|cin
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|cin
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|line
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
condition|)
name|rules
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|rules
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|cin
operator|.
name|close
argument_list|()
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|rules
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|compile
specifier|static
name|void
name|compile
parameter_list|(
name|File
name|srcDir
parameter_list|,
name|File
name|destDir
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|files
index|[]
init|=
name|srcDir
operator|.
name|listFiles
argument_list|(
operator|new
name|FilenameFilter
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|.
name|endsWith
argument_list|(
literal|"rbbi"
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|files
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Path does not exist: "
operator|+
name|srcDir
argument_list|)
throw|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|File
name|file
init|=
name|files
index|[
name|i
index|]
decl_stmt|;
name|File
name|outputFile
init|=
operator|new
name|File
argument_list|(
name|destDir
argument_list|,
name|file
operator|.
name|getName
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"rbbi$"
argument_list|,
literal|"brk"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|rules
init|=
name|getRules
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
literal|"Compiling "
operator|+
name|file
operator|.
name|getName
argument_list|()
operator|+
literal|" to "
operator|+
name|outputFile
operator|.
name|getName
argument_list|()
operator|+
literal|": "
argument_list|)
expr_stmt|;
comment|/*        * if there is a syntax error, compileRules() may succeed. the way to        * check is to try to instantiate from the string. additionally if the        * rules are invalid, you can get a useful syntax error.        */
try|try
block|{
operator|new
name|RuleBasedBreakIterator
argument_list|(
name|rules
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|/*          * do this intentionally, so you don't get a massive stack trace          * instead, get a useful syntax error!          */
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|FileOutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|outputFile
argument_list|)
decl_stmt|;
name|RuleBasedBreakIterator
operator|.
name|compileRules
argument_list|(
name|rules
argument_list|,
name|os
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|outputFile
operator|.
name|length
argument_list|()
operator|+
literal|" bytes."
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|2
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: RBBIRuleComputer<sourcedir><destdir>"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|compile
argument_list|(
operator|new
name|File
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
