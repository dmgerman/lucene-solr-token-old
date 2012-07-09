begin_unit
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *   * Licensed under the Apache License, Version 2.0 (the "License"); you may not  * use this file except in compliance with the License. You may obtain a copy of  * the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.stempel
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|stempel
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
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
name|util
operator|.
name|Locale
import|;
end_import
begin_import
import|import
name|org
operator|.
name|egothor
operator|.
name|stemmer
operator|.
name|Diff
import|;
end_import
begin_import
import|import
name|org
operator|.
name|egothor
operator|.
name|stemmer
operator|.
name|Trie
import|;
end_import
begin_comment
comment|/**  *<p>  * Stemmer class is a convenient facade for other stemmer-related classes. The  * core stemming algorithm and its implementation is taken verbatim from the  * Egothor project (<a href="http://www.egothor.org">www.egothor.org</a>).  *</p>  *<p>  * Even though the stemmer tables supplied in the distribution package are built  * for Polish language, there is nothing language-specific here.  *</p>  */
end_comment
begin_class
DECL|class|StempelStemmer
specifier|public
class|class
name|StempelStemmer
block|{
DECL|field|stemmer
specifier|private
name|Trie
name|stemmer
init|=
literal|null
decl_stmt|;
DECL|field|buffer
specifier|private
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|/**    * Create a Stemmer using selected stemmer table    *     * @param stemmerTable stemmer table.    */
DECL|method|StempelStemmer
specifier|public
name|StempelStemmer
parameter_list|(
name|InputStream
name|stemmerTable
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|load
argument_list|(
name|stemmerTable
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a Stemmer using pre-loaded stemmer table    *     * @param stemmer pre-loaded stemmer table    */
DECL|method|StempelStemmer
specifier|public
name|StempelStemmer
parameter_list|(
name|Trie
name|stemmer
parameter_list|)
block|{
name|this
operator|.
name|stemmer
operator|=
name|stemmer
expr_stmt|;
block|}
comment|/**    * Load a stemmer table from an inputstream.    */
DECL|method|load
specifier|public
specifier|static
name|Trie
name|load
parameter_list|(
name|InputStream
name|stemmerTable
parameter_list|)
throws|throws
name|IOException
block|{
name|DataInputStream
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|in
operator|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
name|stemmerTable
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|method
init|=
name|in
operator|.
name|readUTF
argument_list|()
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
if|if
condition|(
name|method
operator|.
name|indexOf
argument_list|(
literal|'M'
argument_list|)
operator|<
literal|0
condition|)
block|{
return|return
operator|new
name|org
operator|.
name|egothor
operator|.
name|stemmer
operator|.
name|Trie
argument_list|(
name|in
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|org
operator|.
name|egothor
operator|.
name|stemmer
operator|.
name|MultiTrie2
argument_list|(
name|in
argument_list|)
return|;
block|}
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Stem a word.     *     * @param word input word to be stemmed.    * @return stemmed word, or null if the stem could not be generated.    */
DECL|method|stem
specifier|public
name|StringBuilder
name|stem
parameter_list|(
name|CharSequence
name|word
parameter_list|)
block|{
name|CharSequence
name|cmd
init|=
name|stemmer
operator|.
name|getLastOnPath
argument_list|(
name|word
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmd
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|buffer
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|word
argument_list|)
expr_stmt|;
name|Diff
operator|.
name|apply
argument_list|(
name|buffer
argument_list|,
name|cmd
argument_list|)
expr_stmt|;
if|if
condition|(
name|buffer
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
return|return
name|buffer
return|;
else|else
return|return
literal|null
return|;
block|}
block|}
end_class
end_unit
