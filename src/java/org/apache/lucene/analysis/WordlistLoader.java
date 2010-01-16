begin_unit
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|FileReader
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
name|InputStreamReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_comment
comment|/**  * Loader for text files that represent a list of stopwords.  */
end_comment
begin_class
DECL|class|WordlistLoader
specifier|public
class|class
name|WordlistLoader
block|{
comment|/**    * Loads a text file associated with a given class (See    * {@link Class#getResourceAsStream(String)}) and adds every line as an entry    * to a {@link Set} (omitting leading and trailing whitespace). Every line of    * the file should contain only one word. The words need to be in lower-case if    * you make use of an Analyzer which uses LowerCaseFilter (like    * StandardAnalyzer).    *     * @param aClass    *          a class that is associated with the given stopwordResource    * @param stopwordResource    *          name of the resource file associated with the given class    * @return a {@link Set} with the file's words    */
DECL|method|getWordSet
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|getWordSet
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|aClass
parameter_list|,
name|String
name|stopwordResource
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Reader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|aClass
operator|.
name|getResourceAsStream
argument_list|(
name|stopwordResource
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|getWordSet
argument_list|(
name|reader
argument_list|)
return|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Loads a text file associated with a given class (See    * {@link Class#getResourceAsStream(String)}) and adds every line as an entry    * to a {@link Set} (omitting leading and trailing whitespace). Every line of    * the file should contain only one word. The words need to be in lower-case if    * you make use of an Analyzer which uses LowerCaseFilter (like    * StandardAnalyzer).    *     * @param aClass    *          a class that is associated with the given stopwordResource    * @param stopwordResource    *          name of the resource file associated with the given class    * @param comment    *          the comment string to ignore    * @return a {@link Set} with the file's words    */
DECL|method|getWordSet
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|getWordSet
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|aClass
parameter_list|,
name|String
name|stopwordResource
parameter_list|,
name|String
name|comment
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Reader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|aClass
operator|.
name|getResourceAsStream
argument_list|(
name|stopwordResource
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|getWordSet
argument_list|(
name|reader
argument_list|,
name|comment
argument_list|)
return|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Loads a text file and adds every line as an entry to a HashSet (omitting    * leading and trailing whitespace). Every line of the file should contain only    * one word. The words need to be in lowercase if you make use of an    * Analyzer which uses LowerCaseFilter (like StandardAnalyzer).    *    * @param wordfile File containing the wordlist    * @return A HashSet with the file's words    */
DECL|method|getWordSet
specifier|public
specifier|static
name|HashSet
argument_list|<
name|String
argument_list|>
name|getWordSet
parameter_list|(
name|File
name|wordfile
parameter_list|)
throws|throws
name|IOException
block|{
name|FileReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
operator|new
name|FileReader
argument_list|(
name|wordfile
argument_list|)
expr_stmt|;
return|return
name|getWordSet
argument_list|(
name|reader
argument_list|)
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Loads a text file and adds every non-comment line as an entry to a HashSet (omitting    * leading and trailing whitespace). Every line of the file should contain only    * one word. The words need to be in lowercase if you make use of an    * Analyzer which uses LowerCaseFilter (like StandardAnalyzer).    *    * @param wordfile File containing the wordlist    * @param comment The comment string to ignore    * @return A HashSet with the file's words    */
DECL|method|getWordSet
specifier|public
specifier|static
name|HashSet
argument_list|<
name|String
argument_list|>
name|getWordSet
parameter_list|(
name|File
name|wordfile
parameter_list|,
name|String
name|comment
parameter_list|)
throws|throws
name|IOException
block|{
name|FileReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
operator|new
name|FileReader
argument_list|(
name|wordfile
argument_list|)
expr_stmt|;
return|return
name|getWordSet
argument_list|(
name|reader
argument_list|,
name|comment
argument_list|)
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Reads lines from a Reader and adds every line as an entry to a HashSet (omitting    * leading and trailing whitespace). Every line of the Reader should contain only    * one word. The words need to be in lowercase if you make use of an    * Analyzer which uses LowerCaseFilter (like StandardAnalyzer).    *    * @param reader Reader containing the wordlist    * @return A HashSet with the reader's words    */
DECL|method|getWordSet
specifier|public
specifier|static
name|HashSet
argument_list|<
name|String
argument_list|>
name|getWordSet
parameter_list|(
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|HashSet
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|BufferedReader
name|br
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|reader
operator|instanceof
name|BufferedReader
condition|)
block|{
name|br
operator|=
operator|(
name|BufferedReader
operator|)
name|reader
expr_stmt|;
block|}
else|else
block|{
name|br
operator|=
operator|new
name|BufferedReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
name|String
name|word
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|word
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|word
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|br
operator|!=
literal|null
condition|)
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Reads lines from a Reader and adds every non-comment line as an entry to a HashSet (omitting    * leading and trailing whitespace). Every line of the Reader should contain only    * one word. The words need to be in lowercase if you make use of an    * Analyzer which uses LowerCaseFilter (like StandardAnalyzer).    *    * @param reader Reader containing the wordlist    * @param comment The string representing a comment.    * @return A HashSet with the reader's words    */
DECL|method|getWordSet
specifier|public
specifier|static
name|HashSet
argument_list|<
name|String
argument_list|>
name|getWordSet
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|String
name|comment
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|HashSet
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|BufferedReader
name|br
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|reader
operator|instanceof
name|BufferedReader
condition|)
block|{
name|br
operator|=
operator|(
name|BufferedReader
operator|)
name|reader
expr_stmt|;
block|}
else|else
block|{
name|br
operator|=
operator|new
name|BufferedReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
name|String
name|word
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|word
operator|=
name|br
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
name|word
operator|.
name|startsWith
argument_list|(
name|comment
argument_list|)
operator|==
literal|false
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|word
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|br
operator|!=
literal|null
condition|)
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Loads a text file in Snowball format associated with a given class (See    * {@link Class#getResourceAsStream(String)}) and adds all words as entries to    * a {@link Set}. The words need to be in lower-case if you make use of an    * Analyzer which uses LowerCaseFilter (like StandardAnalyzer).    *     * @param aClass a class that is associated with the given stopwordResource    * @param stopwordResource name of the resource file associated with the given    *          class    * @return a {@link Set} with the file's words    * @see #getSnowballWordSet(Reader)    */
DECL|method|getSnowballWordSet
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|getSnowballWordSet
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|aClass
parameter_list|,
name|String
name|stopwordResource
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Reader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|aClass
operator|.
name|getResourceAsStream
argument_list|(
name|stopwordResource
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|getSnowballWordSet
argument_list|(
name|reader
argument_list|)
return|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Reads stopwords from a stopword list in Snowball format.    *<p>    * The snowball format is the following:    *<ul>    *<li>Lines may contain multiple words separated by whitespace.    *<li>The comment character is the vertical line (&#124;).    *<li>Lines may contain trailing comments.    *</ul>    *</p>    *     * @param reader Reader containing a Snowball stopword list    * @return A Set with the reader's words    */
DECL|method|getSnowballWordSet
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|getSnowballWordSet
parameter_list|(
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|BufferedReader
name|br
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|reader
operator|instanceof
name|BufferedReader
condition|)
block|{
name|br
operator|=
operator|(
name|BufferedReader
operator|)
name|reader
expr_stmt|;
block|}
else|else
block|{
name|br
operator|=
operator|new
name|BufferedReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
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
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|int
name|comment
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|'|'
argument_list|)
decl_stmt|;
if|if
condition|(
name|comment
operator|>=
literal|0
condition|)
name|line
operator|=
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|comment
argument_list|)
expr_stmt|;
name|String
name|words
index|[]
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|words
operator|.
name|length
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|words
index|[
name|i
index|]
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|result
operator|.
name|add
argument_list|(
name|words
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|br
operator|!=
literal|null
condition|)
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Reads a stem dictionary. Each line contains:    *<pre>word<b>\t</b>stem</pre>    * (i.e. two tab separated words)    *    * @return stem dictionary that overrules the stemming algorithm    * @throws IOException     */
DECL|method|getStemDict
specifier|public
specifier|static
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getStemDict
parameter_list|(
name|File
name|wordstemfile
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|wordstemfile
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"wordstemfile may not be null"
argument_list|)
throw|;
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|BufferedReader
name|br
init|=
literal|null
decl_stmt|;
try|try
block|{
name|br
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|wordstemfile
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|wordstem
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\t"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|result
operator|.
name|put
argument_list|(
name|wordstem
index|[
literal|0
index|]
argument_list|,
name|wordstem
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|br
operator|!=
literal|null
condition|)
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
