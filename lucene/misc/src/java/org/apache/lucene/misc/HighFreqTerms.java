begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.misc
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|misc
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|DirectoryReader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|MultiFields
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Fields
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|TermsEnum
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Terms
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|FSDirectory
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|PriorityQueue
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|SuppressForbidden
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
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
begin_comment
comment|/**  *<code>HighFreqTerms</code> class extracts the top n most frequent terms  * (by document frequency) from an existing Lucene index and reports their  * document frequency.  *<p>  * If the -t flag is given, both document frequency and total tf (total  * number of occurrences) are reported, ordered by descending total tf.  *  */
end_comment
begin_class
DECL|class|HighFreqTerms
specifier|public
class|class
name|HighFreqTerms
block|{
comment|// The top numTerms will be displayed
DECL|field|DEFAULT_NUMTERMS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_NUMTERMS
init|=
literal|100
decl_stmt|;
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"System.out required: command line tool"
argument_list|)
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|field
init|=
literal|null
decl_stmt|;
name|int
name|numTerms
init|=
name|DEFAULT_NUMTERMS
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
operator|||
name|args
operator|.
name|length
operator|>
literal|4
condition|)
block|{
name|usage
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|Directory
name|dir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|Comparator
argument_list|<
name|TermStats
argument_list|>
name|comparator
init|=
operator|new
name|DocFreqComparator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-t"
argument_list|)
condition|)
block|{
name|comparator
operator|=
operator|new
name|TotalTermFreqComparator
argument_list|()
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|numTerms
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|field
operator|=
name|args
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
block|}
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|TermStats
index|[]
name|terms
init|=
name|getHighFreqTerms
argument_list|(
name|reader
argument_list|,
name|numTerms
argument_list|,
name|field
argument_list|,
name|comparator
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%s:%s \t totalTF = %,d \t docFreq = %,d \n"
argument_list|,
name|terms
index|[
name|i
index|]
operator|.
name|field
argument_list|,
name|terms
index|[
name|i
index|]
operator|.
name|termtext
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|terms
index|[
name|i
index|]
operator|.
name|totalTermFreq
argument_list|,
name|terms
index|[
name|i
index|]
operator|.
name|docFreq
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"System.out required: command line tool"
argument_list|)
DECL|method|usage
specifier|private
specifier|static
name|void
name|usage
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\n\n"
operator|+
literal|"java org.apache.lucene.misc.HighFreqTerms<index dir> [-t] [number_terms] [field]\n\t -t: order by totalTermFreq\n\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns TermStats[] ordered by the specified comparator    */
DECL|method|getHighFreqTerms
specifier|public
specifier|static
name|TermStats
index|[]
name|getHighFreqTerms
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|numTerms
parameter_list|,
name|String
name|field
parameter_list|,
name|Comparator
argument_list|<
name|TermStats
argument_list|>
name|comparator
parameter_list|)
throws|throws
name|Exception
block|{
name|TermStatsQueue
name|tiq
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"field "
operator|+
name|field
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|tiq
operator|=
operator|new
name|TermStatsQueue
argument_list|(
name|numTerms
argument_list|,
name|comparator
argument_list|)
expr_stmt|;
name|tiq
operator|.
name|fill
argument_list|(
name|field
argument_list|,
name|termsEnum
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Fields
name|fields
init|=
name|MultiFields
operator|.
name|getFields
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"no fields found for this index"
argument_list|)
throw|;
block|}
name|tiq
operator|=
operator|new
name|TermStatsQueue
argument_list|(
name|numTerms
argument_list|,
name|comparator
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|fieldName
range|:
name|fields
control|)
block|{
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|tiq
operator|.
name|fill
argument_list|(
name|fieldName
argument_list|,
name|terms
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|TermStats
index|[]
name|result
init|=
operator|new
name|TermStats
index|[
name|tiq
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
comment|// we want highest first so we read the queue and populate the array
comment|// starting at the end and work backwards
name|int
name|count
init|=
name|tiq
operator|.
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|tiq
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|result
index|[
name|count
index|]
operator|=
name|tiq
operator|.
name|pop
argument_list|()
expr_stmt|;
name|count
operator|--
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Compares terms by docTermFreq    */
DECL|class|DocFreqComparator
specifier|public
specifier|static
specifier|final
class|class
name|DocFreqComparator
implements|implements
name|Comparator
argument_list|<
name|TermStats
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|TermStats
name|a
parameter_list|,
name|TermStats
name|b
parameter_list|)
block|{
name|int
name|res
init|=
name|Long
operator|.
name|compare
argument_list|(
name|a
operator|.
name|docFreq
argument_list|,
name|b
operator|.
name|docFreq
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|==
literal|0
condition|)
block|{
name|res
operator|=
name|a
operator|.
name|field
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|field
argument_list|)
expr_stmt|;
if|if
condition|(
name|res
operator|==
literal|0
condition|)
block|{
name|res
operator|=
name|a
operator|.
name|termtext
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|termtext
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|res
return|;
block|}
block|}
comment|/**    * Compares terms by totalTermFreq    */
DECL|class|TotalTermFreqComparator
specifier|public
specifier|static
specifier|final
class|class
name|TotalTermFreqComparator
implements|implements
name|Comparator
argument_list|<
name|TermStats
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|TermStats
name|a
parameter_list|,
name|TermStats
name|b
parameter_list|)
block|{
name|int
name|res
init|=
name|Long
operator|.
name|compare
argument_list|(
name|a
operator|.
name|totalTermFreq
argument_list|,
name|b
operator|.
name|totalTermFreq
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|==
literal|0
condition|)
block|{
name|res
operator|=
name|a
operator|.
name|field
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|field
argument_list|)
expr_stmt|;
if|if
condition|(
name|res
operator|==
literal|0
condition|)
block|{
name|res
operator|=
name|a
operator|.
name|termtext
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|termtext
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|res
return|;
block|}
block|}
comment|/**    * Priority queue for TermStats objects    **/
DECL|class|TermStatsQueue
specifier|static
specifier|final
class|class
name|TermStatsQueue
extends|extends
name|PriorityQueue
argument_list|<
name|TermStats
argument_list|>
block|{
DECL|field|comparator
specifier|final
name|Comparator
argument_list|<
name|TermStats
argument_list|>
name|comparator
decl_stmt|;
DECL|method|TermStatsQueue
name|TermStatsQueue
parameter_list|(
name|int
name|size
parameter_list|,
name|Comparator
argument_list|<
name|TermStats
argument_list|>
name|comparator
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
name|comparator
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|TermStats
name|termInfoA
parameter_list|,
name|TermStats
name|termInfoB
parameter_list|)
block|{
return|return
name|comparator
operator|.
name|compare
argument_list|(
name|termInfoA
argument_list|,
name|termInfoB
argument_list|)
operator|<
literal|0
return|;
block|}
DECL|method|fill
specifier|protected
name|void
name|fill
parameter_list|(
name|String
name|field
parameter_list|,
name|TermsEnum
name|termsEnum
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRef
name|term
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|term
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|insertWithOverflow
argument_list|(
operator|new
name|TermStats
argument_list|(
name|field
argument_list|,
name|term
argument_list|,
name|termsEnum
operator|.
name|docFreq
argument_list|()
argument_list|,
name|termsEnum
operator|.
name|totalTermFreq
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
