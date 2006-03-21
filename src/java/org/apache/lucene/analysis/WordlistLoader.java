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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Reader
import|;
end_import
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
name|Hashtable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
begin_comment
comment|/**  * Loader for text files that represent a list of stopwords.  *  * @author Gerhard Schwarz  * @version $Id$  */
end_comment
begin_class
DECL|class|WordlistLoader
specifier|public
class|class
name|WordlistLoader
block|{
comment|/**    * Loads a text file and adds every line as an entry to a HashSet (omitting    * leading and trailing whitespace). Every line of the file should contain only    * one word. The words need to be in lowercase if you make use of an    * Analyzer which uses LowerCaseFilter (like StandardAnalyzer).    *    * @param wordfile File containing the wordlist    * @return A HashSet with the file's words    */
DECL|method|getWordSet
specifier|public
specifier|static
name|HashSet
name|getWordSet
parameter_list|(
name|File
name|wordfile
parameter_list|)
throws|throws
name|IOException
block|{
name|HashSet
name|result
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
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
name|result
operator|=
name|getWordSet
argument_list|(
name|reader
argument_list|)
expr_stmt|;
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
return|return
name|result
return|;
block|}
comment|/**    * Reads lines from a Reader and adds every line as an entry to a HashSet (omitting    * leading and trailing whitespace). Every line of the Reader should contain only    * one word. The words need to be in lowercase if you make use of an    * Analyzer which uses LowerCaseFilter (like StandardAnalyzer).    *    * @param reader Reader containing the wordlist    * @return A HashSet with the reader's words    */
DECL|method|getWordSet
specifier|public
specifier|static
name|HashSet
name|getWordSet
parameter_list|(
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|HashSet
name|result
init|=
operator|new
name|HashSet
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
comment|/**    * Builds a wordlist table, using words as both keys and values    * for backward compatibility.    *    * @param wordSet   stopword set    */
DECL|method|makeWordTable
specifier|private
specifier|static
name|Hashtable
name|makeWordTable
parameter_list|(
name|HashSet
name|wordSet
parameter_list|)
block|{
name|Hashtable
name|table
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|wordSet
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|word
init|=
operator|(
name|String
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|table
operator|.
name|put
argument_list|(
name|word
argument_list|,
name|word
argument_list|)
expr_stmt|;
block|}
return|return
name|table
return|;
block|}
block|}
end_class
end_unit
